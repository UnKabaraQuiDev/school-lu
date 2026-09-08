(() => {
    "use strict";


    /* =========================================================
       API
       ========================================================= */

    const API = {
        restore: "learn/restore",
        save: "learn/save",
        next: "learn/next",
        subjects: "subjects/tree",
        tags: "tags/list"
    };


    /*
     * Change only these functions if your backend uses a
     * different JSON structure.
     */


    async function getJSON(path) {
        const response = await fetch(
            apiUrl(path), {
                method: "GET",
                credentials: "include",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!response.ok) {
            throw new Error(
                `${response.status} ${response.statusText}`
            );
        }

        return response.json();
    }


    async function postJSON(path, body) {
        const response = await fetch(
            apiUrl(path), {
                method: "POST",
                credentials: "include",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify(body)
            }
        );

        if (!response.ok) {
            throw new Error(
                `${response.status} ${response.statusText}`
            );
        }

        return response.json();
    }

    async function putJSON(path, body) {
        const response = await fetch(
            apiUrl(path), {
                method: "PUT",
                credentials: "include",
                headers: {
                    "Accept": "application/json",
                    "Content-Type": "application/json",
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify(body)
            }
        );

        if (!response.ok) {
            throw new Error(
                `${response.status} ${response.statusText}`
            );
        }

        return response.json();
    }


    /*
     * These two functions normalize the possible restore
     * response without forcing the rest of the code to know
     * its exact structure.
     */
    function getRestoredSettings(data) {
        return (
            data.settings ||
            data.options ||
            data || {}
        );
    }


    function getRestoredExercise(data) {
        return (
            data.exercise ||
            data.currentExercise ||
            null
        );
    }


    /*
     * Normalize the /learn/next response.
     */
    function getNextExercise(data) {
        return (
            data.exercise ||
            data.next ||
            data.currentExercise ||
            (
                Array.isArray(data) ?
                data[0] :
                null
            )
        );
    }


    /* =========================================================
       State
       ========================================================= */

    const state = {
        subjects: [],
        tags: [],

        selectedSubjects: new Set(),
        selectedTags: new Set(),

        withSolutionOnly: false,
        excludeSuccess: false,

        currentExercise: null,
        busy: false
    };


    /* =========================================================
       DOM
       ========================================================= */

    const subjectsList =
        document.getElementById("subjects-list");

    const tagsList =
        document.getElementById("tags-list");

    const tagSearch =
        document.getElementById("tag-search");

    const withSolutionOnly =
        document.getElementById("with-solution-only");

    const excludeSuccess =
        document.getElementById("exclude-success");

    const startButton =
        document.getElementById("start-learning");

    const exercisesContainer =
        document.getElementById("exercises");

    const emptyState =
        document.getElementById("empty-state");

    const examInfo =
        document.getElementById("exam-info");

    const examBreadcrumb =
        document.getElementById("exam-breadcrumb");

    const examSection =
        document.getElementById("exam-section");

    const examSubject =
        document.getElementById("exam-subject");

    const examYear =
        document.getElementById("exam-year");

    const examSeason =
        document.getElementById("exam-season");

    const examSubtype =
        document.getElementById("exam-subtype");

    const exerciseTemplate =
        document.getElementById("exercise-template");


    /* =========================================================
       Helpers
       ========================================================= */

    function escapeHTML(value) {
        return String(value ?? "")
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#039;");
    }


    function normalizeArray(value) {
        return Array.isArray(value) ?
            value :
            [];
    }


    function getSubjectId(subject) {
        return (
            subject.id ??
            subject.code ??
            subject.name
        );
    }


    function getSubjectName(subject) {
        return (
            subject.name ??
            subject.code ??
            String(subject.id ?? "")
        );
    }

    function getSelectedSubjectsBySection() {
        const result = {};

        for (
            const [section, subjects]
            of Object.entries(state.subjects)
        ) {
            const selected = [];

            for (const subject of subjects) {
                const key =
                    `${section}/${subject}`;

                if (
                    state.selectedSubjects.has(key)
                ) {
                    selected.push(subject);
                }
            }

            if (selected.length > 0) {
                result[section] = selected;
            }
        }

        return result;
    }


    function getSectionName(subject) {
        if (typeof subject.section === "string") {
            return subject.section;
        }

        if (subject.section?.name) {
            return subject.section.name;
        }

        if (subject.parent?.name) {
            return subject.parent.name;
        }

        return "Other";
    }


    function getTagId(tag) {
        return (
            tag.id ??
            tag.name
        );
    }


    function getTagName(tag) {
        return (
            tag.name ??
            tag.label ??
            String(tag.id ?? "")
        );
    }


    /*
     * The example has color: 0.
     *
     * This supports:
     *   - CSS colors such as "#ff0000"
     *   - CSS colors such as "red"
     *   - numeric color indexes
     */
    function getTagColor(color) {
        if (
            typeof color === "string" &&
            color.trim() !== ""
        ) {
            return color;
        }

        const colors = [
            "#dbeafe",
            "#dcfce7",
            "#fef3c7",
            "#fce7f3",
            "#ede9fe",
            "#cffafe",
            "#ffedd5",
            "#f3e8ff",
            "#ecfccb",
            "#fee2e2"
        ];

        const index =
            Number.isFinite(Number(color)) ?
            Number(color) :
            0;

        return colors[
            Math.abs(index) % colors.length
        ];
    }


    function getTagTextColor(background) {
        /*
         * For numeric palette colors, the matching text
         * color is kept readable.
         */
        if (
            typeof background === "string" &&
            background.startsWith("#") &&
            background.length === 7
        ) {
            const r = parseInt(
                background.slice(1, 3),
                16
            );

            const g = parseInt(
                background.slice(3, 5),
                16
            );

            const b = parseInt(
                background.slice(5, 7),
                16
            );

            const luminance =
                (
                    0.299 * r +
                    0.587 * g +
                    0.114 * b
                );

            return luminance > 165 ?
                "#1e293b" :
                "#ffffff";
        }

        return "#1e293b";
    }


    /* =========================================================
       Subjects
       ========================================================= */


    function renderSubjects() {
        subjectsList.innerHTML = "";

        const groups =
            Object.entries(state.subjects);

        if (groups.length === 0) {
            const message =
                document.createElement("p");

            message.className =
                "text-sm text-slate-500";

            message.textContent =
                "No subjects available.";

            subjectsList.appendChild(
                message
            );

            return;
        }


        for (const [section, subjects] of groups) {
            const details =
                document.createElement("details");

            details.className =
                "mb-2 overflow-hidden rounded-lg border border-slate-200 last:mb-0";


            /*
            * Open sections that contain at least
            * one selected subject.
            */
            const hasSelectedSubject =
                subjects.some((subject) =>
                    state.selectedSubjects.has(
                        `${section}/${subject}`
                    )
                );

            details.open =
                hasSelectedSubject;


            const summary =
                document.createElement("summary");

            summary.className =
                "flex cursor-pointer list-none items-center gap-2 bg-slate-50 px-3 py-2.5 font-semibold text-slate-800 hover:bg-slate-100 [&::-webkit-details-marker]:hidden";


            /*
            * Section checkbox.
            */
            const sectionCheckbox =
                document.createElement("input");

            sectionCheckbox.type =
                "checkbox";

            sectionCheckbox.className =
                "h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500";


            /*
            * Prevent clicking the checkbox from
            * toggling the dropdown itself.
            */
            sectionCheckbox.addEventListener(
                "click",
                (event) => {
                    event.stopPropagation();
                }
            );


            const sectionText =
                document.createElement("span");

            sectionText.textContent =
                section;


            summary.appendChild(
                sectionCheckbox
            );

            summary.appendChild(
                sectionText
            );


            /*
            * Subjects.
            */
            const children =
                document.createElement("div");

            children.className =
                "space-y-1 border-t border-slate-200 px-3 py-2";


            const subjectCheckboxes = [];


            for (const subject of subjects) {
                const id =
                    String(subject);

                const key =
                    `${section}/${subject}`;


                const label =
                    document.createElement("label");

                label.className =
                    "flex cursor-pointer items-center gap-2 rounded-md px-2 py-1.5 hover:bg-slate-50";


                const checkbox =
                    document.createElement("input");

                checkbox.type =
                    "checkbox";

                checkbox.value =
                    id;

                checkbox.className =
                    "h-4 w-4 rounded border-slate-300 text-blue-600 focus:ring-blue-500";

                checkbox.checked =
                    state.selectedSubjects.has(
                        key
                    );


                const name =
                    document.createElement("span");

                name.textContent =
                    subject;


                label.appendChild(
                    checkbox
                );

                label.appendChild(
                    name
                );

                children.appendChild(
                    label
                );

                subjectCheckboxes.push(
                    checkbox
                );


                checkbox.addEventListener(
                    "change",
                    () => {
                        if (checkbox.checked) {
                            state.selectedSubjects.add(
                                key
                            );
                        } else {
                            state.selectedSubjects.delete(
                                key
                            );
                        }

                        updateSectionCheckbox(
                            sectionCheckbox,
                            subjectCheckboxes
                        );
                    }
                );
            }


            /*
            * Select/unselect all subjects in this section.
            */
            sectionCheckbox.addEventListener(
                "change",
                () => {
                    for (
                        const checkbox
                        of subjectCheckboxes
                    ) {
                        checkbox.checked =
                            sectionCheckbox.checked;

                        const subject =
                            String(
                                checkbox.value
                            );

                        const key =
                            `${section}/${subject}`;


                        if (checkbox.checked) {
                            state.selectedSubjects.add(
                                key
                            );
                        } else {
                            state.selectedSubjects.delete(
                                key
                            );
                        }
                    }

                    updateSectionCheckbox(
                        sectionCheckbox,
                        subjectCheckboxes
                    );
                }
            );


            updateSectionCheckbox(
                sectionCheckbox,
                subjectCheckboxes
            );


            details.appendChild(
                summary
            );

            details.appendChild(
                children
            );

            subjectsList.appendChild(
                details
            );
        }
    }


    function updateSectionCheckbox(
        sectionCheckbox,
        subjectCheckboxes
    ) {
        const checked =
            subjectCheckboxes.filter(
                checkbox => checkbox.checked
            ).length;

        sectionCheckbox.checked =
            checked === subjectCheckboxes.length;

        sectionCheckbox.indeterminate =
            checked > 0 &&
            checked < subjectCheckboxes.length;
    }


    /* =========================================================
       Tags
       ========================================================= */

    function renderTags() {
        const search =
            tagSearch.value
            .trim()
            .toLocaleLowerCase();

        tagsList.innerHTML = "";


        const filtered =
            state.tags.filter((tag) => {
                return getTagName(tag)
                    .toLocaleLowerCase()
                    .includes(search);
            });


        if (filtered.length === 0) {
            const message =
                document.createElement("p");

            message.className =
                "text-sm text-slate-500";

            message.textContent =
                "No matching tags.";

            tagsList.appendChild(message);

            return;
        }


        for (const tag of filtered) {
            const id =
                String(getTagId(tag));

            const label =
                document.createElement("label");

            label.className =
                "flex cursor-pointer items-center gap-3 rounded-lg px-2 py-2 hover:bg-slate-50";


            const checkbox =
                document.createElement("input");

            checkbox.type = "checkbox";
            checkbox.value = id;

            checkbox.checked =
                state.selectedTags.has(id);

            checkbox.className =
                "h-4 w-4 shrink-0 rounded border-slate-300 text-blue-600 focus:ring-blue-500";


            const color =
                getTagColor(tag.color);

            const textColor =
                getTagTextColor(color);


            const sticker =
                document.createElement("span");

            sticker.className =
                "rounded-full px-3 py-1 text-sm font-medium";

            sticker.style.backgroundColor =
                color;

            sticker.style.color =
                textColor;

            sticker.textContent =
                getTagName(tag);


            label.appendChild(checkbox);
            label.appendChild(sticker);

            tagsList.appendChild(label);


            checkbox.addEventListener(
                "change",
                () => {
                    if (checkbox.checked) {
                        state.selectedTags.add(id);
                    } else {
                        state.selectedTags.delete(id);
                    }
                }
            );
        }
    }


    tagSearch.addEventListener(
        "input",
        renderTags
    );


    /* =========================================================
       Settings state
       ========================================================= */

    function applyRestoredSettings(settings) {
        const subjects =
            settings.subjects ??
            settings.selectedSubjects ??
            {};

        state.selectedSubjects =
            new Set();

        if (
            subjects &&
            typeof subjects === "object" &&
            !Array.isArray(subjects)
        ) {
            for (
                const [section, subjectList]
                of Object.entries(subjects)
            ) {
                for (
                    const subject
                    of normalizeArray(subjectList)
                ) {
                    state.selectedSubjects.add(
                        `${section}/${subject}`
                    );
                }
            }
        }


        const selectedTags =
            settings.requiredTags ??
            settings.selectedTags ??
            settings.tags ??
            [];

        state.selectedTags =
            new Set(
                normalizeArray(selectedTags)
                    .map((tag) => {
                        if (
                            typeof tag === "object"
                        ) {
                            return String(
                                getTagId(tag)
                            );
                        }

                        return String(tag);
                    })
            );


        state.withSolutionOnly =
            Boolean(
                settings.withSolutionOnly ??
                settings.with_solution_only ??
                false
            );

        state.excludeSuccess =
            Boolean(
                settings.excludeSuccess ??
                settings.exclude_success ??
                false
            );

        withSolutionOnly.checked =
            state.withSolutionOnly;

        excludeSuccess.checked =
            state.excludeSuccess;
    }


    function readSettingsFromDOM() {
        state.withSolutionOnly =
            withSolutionOnly.checked;

        state.excludeSuccess =
            excludeSuccess.checked;


        return {
            subjects: [
                ...state.selectedSubjects
            ],

            tags: [
                ...state.selectedTags
            ],

            withSolutionOnly: state.withSolutionOnly,

            excludeSuccess: state.excludeSuccess
        };
    }


    function getNextRequestBody() {
        const settings =
            readSettingsFromDOM();

        return {
            withSolutionOnly: Boolean(settings.withSolutionOnly),

            subjects: getSelectedSubjectsBySection(),

            requiredTags: Array.from(state.selectedTags),

            excludeSuccess: Boolean(settings.excludeSuccess)
        };
    }


    /* =========================================================
       Exam
       ========================================================= */

    function getExamSubtype(exam) {
        return (
            exam.subtype ??
            ""
        );
    }


    function getExamSeason(exam) {
        return (
            exam.season ??
            ""
        );
    }


    function getExamFolder(exam) {
        const season =
            getExamSeason(exam);

        const subtype =
            getExamSubtype(exam);

        return `${season}_${subtype}`;
    }


    function getExamBaseURL(exam) {
        return [
            "/exam-db",
            encodeURIComponent(exam.section),
            encodeURIComponent(exam.subject),
            encodeURIComponent(exam.year),
            encodeURIComponent(
                getExamFolder(exam)
            )
        ].join("/");
    }


    function renderExam(exam) {
        if (!exam) {
            examInfo.classList.add("hidden");
            return;
        }


        examInfo.classList.remove("hidden");


        const base =
            getExamBaseURL(exam);


        /*
         * Exact structure requested:
         *
         * /exam-db/{section}/{subject}/{year}/{season}_{subtype}/
         */
        const links = [{
                label: "Sections",
                href: "/exam-db/"
            },
            {
                label: exam.section,
                href: `/exam-db/${encodeURIComponent(exam.section)}/`
            },
            {
                label: exam.subject,
                href: `/exam-db/${encodeURIComponent(exam.section)}/${encodeURIComponent(exam.subject)}/`
            },
            {
                label: exam.year,
                href: `/exam-db/${encodeURIComponent(exam.section)}/${encodeURIComponent(exam.subject)}/${encodeURIComponent(exam.year)}/`
            },
            {
                label: getExamFolder(exam),
                href: `${base}/`
            }
        ];


        examBreadcrumb.innerHTML = "";


        links.forEach((link, index) => {
            if (index > 0) {
                const slash =
                    document.createElement("span");

                slash.className =
                    "mx-1";

                slash.textContent = "/";

                examBreadcrumb.appendChild(
                    slash
                );
            }


            const anchor =
                document.createElement("a");

            anchor.className =
                "underline hover:no-underline";

            anchor.href =
                link.href;

            anchor.textContent =
                link.label;


            examBreadcrumb.appendChild(
                anchor
            );
        });


        examSection.textContent =
            exam.section ?? "";

        examSubject.textContent =
            exam.subject ?? "";

        examYear.textContent =
            exam.year ?? "";

        examSeason.textContent =
            getExamSeason(exam);

        examSubtype.textContent =
            getExamSubtype(exam);
    }


    /* =========================================================
       Exercise rendering
       ========================================================= */

    function getAttachmentURL(attachment) {
        if (!attachment?.location) {
            return "";
        }

        return (
            attachment.location.startsWith("/") ?
            attachment.location :
            `/data/${attachment.location}`
        );
    }


    function getAttachmentBackground(
        attachment
    ) {
        const qualifier =
            String(
                attachment?.qualifier ?? ""
            ).toUpperCase();


        if (
            qualifier === "CORRIGE" ||
            qualifier === "SOLUTION"
        ) {
            return {
                border: "border-green-200",
                background: "bg-green-100"
            };
        }


        return {
            border: "border-blue-200",
            background: "bg-blue-100"
        };
    }


    function getAttachmentAlt(
        attachment,
        exercise
    ) {
        const number =
            exercise.exerciseIndex ??
            exercise.exerciseNumber ??
            "";


        const qualifier =
            String(
                attachment?.qualifier ?? ""
            ).toLowerCase();


        if (
            qualifier === "corrige" ||
            qualifier === "solution"
        ) {
            return `Exercise ${number} solution`;
        }


        if (qualifier) {
            return `Exercise ${number} ${qualifier}`;
        }


        return `Exercise ${number}`;
    }


    function renderTagsForExercise(
        container,
        tags
    ) {
        container.innerHTML = "";


        for (const tag of normalizeArray(tags)) {
            const sticker =
                document.createElement("span");

            const background =
                getTagColor(tag.color);

            const textColor =
                getTagTextColor(background);


            sticker.className =
                "rounded-full px-3 py-1 text-sm font-medium shadow-sm";


            sticker.style.backgroundColor =
                background;

            sticker.style.color =
                textColor;


            sticker.textContent =
                getTagName(tag);


            container.appendChild(
                sticker
            );
        }
    }


    function createExerciseElement(
        exercise,
        exerciseIndex
    ) {
        const fragment =
            exerciseTemplate.content.cloneNode(
                true
            );


        const section =
            fragment.querySelector(
                ".exercise"
            );


        section.dataset.exercise =
            String(
                exercise.exerciseIndex ??
                exerciseIndex + 1
            );


        const number =
            fragment.querySelector(
                ".exercise-number"
            );


        number.textContent =
            String(
                exercise.exerciseIndex ??
                exerciseIndex + 1
            );


        const tagsContainer =
            fragment.querySelector(
                ".exercise-tags"
            );


        renderTagsForExercise(
            tagsContainer,
            exercise.tags
        );


        const imageContainer =
            fragment.querySelector(
                ".exercise-images > div"
            );


        const attachments =
            normalizeArray(
                exercise.attachments
            );


        attachments.forEach(
            (attachment) => {
                const imageButton =
                    document.createElement("button");

                imageButton.type =
                    "button";

                imageButton.className =
                    "exercise-image group aspect-square w-[25vh] max-h-[25vh] max-w-[25vh] shrink-0 overflow-hidden rounded-lg border-2 bg-white focus:outline-none focus:ring-2 focus:ring-blue-500";


                const colors =
                    getAttachmentBackground(
                        attachment
                    );


                imageButton.classList.add(
                    colors.border,
                    colors.background
                );


                const image =
                    document.createElement("img");

                image.src =
                    getAttachmentURL(
                        attachment
                    );

                image.alt =
                    getAttachmentAlt(
                        attachment,
                        exercise
                    );

                image.className =
                    "h-full w-full select-none object-contain transition-transform group-hover:scale-[1.02]";

                image.draggable =
                    false;


                imageButton.appendChild(
                    image
                );

                imageContainer.appendChild(
                    imageButton
                );
            }
        );


        /*
         * Exercise action buttons.
         */
        fragment
            .querySelectorAll(
                ".exercise-action"
            )
            .forEach((button) => {
                button.addEventListener(
                    "click",
                    () => {
                        submitResult(
                            button.dataset.action,
                            exercise
                        );
                    }
                );
            });


        const result =
            section;


        return {
            element: result,
            exercise
        };
    }


    function clearExercises() {
        exercisesContainer.innerHTML = "";

        examInfo.classList.add(
            "hidden"
        );
    }


    function showExercise(
        exercise,
        replace = true
    ) {
        if (!exercise) {
            emptyState.classList.remove(
                "hidden"
            );

            return;
        }


        emptyState.classList.add(
            "hidden"
        );


        if (replace) {
            clearExercises();
        }


        const existingExercises =
            exercisesContainer.querySelectorAll(
                ".exercise"
            );


        const index =
            existingExercises.length;


        const {
            element
        } = createExerciseElement(
            exercise,
            index
        );


        exercisesContainer.appendChild(
            element
        );


        const inserted =
            exercisesContainer.lastElementChild;


        state.currentExercise =
            exercise;


        renderExam(
            exercise.exam
        );


        if (
            window.LearnImageViewer
        ) {
            window.LearnImageViewer.bindExercise(
                inserted,
                index
            );
        }


        /*
         * Keep only one exercise in the normal
         * learning flow.
         */
        if (replace) {
            while (
                exercisesContainer.children.length > 1
            ) {
                exercisesContainer.firstElementChild.remove();
            }
        }
    }


    /* =========================================================
       Loading data
       ========================================================= */

    async function loadSubjects() {
        document
            .getElementById("clear-subjects")
            .addEventListener("click", () => {
                state.selectedSubjects.clear();

                renderSubjects();
            });
            
        try {
            const data =
                await getJSON(
                    API.subjects
                );

            state.subjects = data;

            renderSubjects();
        } catch (error) {
            console.error(
                "Could not load subjects:",
                error
            );


            subjectsList.innerHTML = "";


            const message =
                document.createElement("p");

            message.className =
                "text-sm text-red-600";

            message.textContent =
                "Could not load subjects.";

            subjectsList.appendChild(
                message
            );
        }
    }


    async function loadTags() {
        document
            .getElementById("clear-tags")
            .addEventListener("click", () => {
                state.selectedTags.clear();

                renderTags();
            });

        try {
            const data =
                await getJSON(
                    API.tags
                );


            state.tags =
                normalizeArray(
                    data.tags ??
                    data
                );


            renderTags();
        } catch (error) {
            console.error(
                "Could not load tags:",
                error
            );


            tagsList.innerHTML = "";


            const message =
                document.createElement("p");

            message.className =
                "text-sm text-red-600";

            message.textContent =
                "Could not load tags.";

            tagsList.appendChild(
                message
            );
        }
    }


    async function restoreSession() {
        try {
            const data =
                await getJSON(
                    API.restore
                );


            const settings =
                getRestoredSettings(data);


            applyRestoredSettings(
                settings
            );


            /*
             * Subjects and tags may have loaded before
             * or after restore.
             */
            renderSubjects();
            renderTags();


            const exercise =
                getRestoredExercise(data);


            if (exercise) {
                showExercise(
                    exercise,
                    true
                );
            }
        } catch (error) {
            console.error(
                "Could not restore learning session:",
                error
            );
        }
    }


    /* =========================================================
       Start / Next
       ========================================================= */

    async function startLearning() {
        if (state.busy) {
            return;
        }


        setBusy(true);


        try {
            const body =
                getNextRequestBody();

            putJSON(API.save, body);

            /*
             * Starting the session uses /learn/next/.
             *
             * If your backend has a dedicated /learn/start/
             * endpoint, this is the one function to change.
             */
            const data =
                await postJSON(
                    API.next,
                    body
                );


            const exercise =
                getNextExercise(data);


            showExercise(
                exercise,
                true
            );
        } catch (error) {
            console.error(
                "Could not start learning:",
                error
            );

            showError(
                "Could not start the learning session."
            );
        } finally {
            setBusy(false);
        }
    }


    async function submitResult(
        result,
        exercise
    ) {
        if (state.busy) {
            return;
        }


        setBusy(true);


        /*
         * Close fullscreen before sending the result.
         */
        if (
            window.LearnImageViewer
        ) {
            window.LearnImageViewer.closeViewer();
        }


        try {
            const body =
                getNextRequestBody(
                    result
                );


            /*
             * Include the exercise id when available.
             *
             * This can be removed if /learn/next/ identifies
             * the current exercise entirely from the session.
             */
            if (exercise?.id != null) {
                body.exerciseId =
                    exercise.id;
            }

            putJSON(API.save, body);

            const data =
                await postJSON(
                    API.next,
                    body
                );


            const nextExercise =
                getNextExercise(data);


            showExercise(
                nextExercise,
                true
            );
        } catch (error) {
            console.error(
                "Could not load next exercise:",
                error
            );

            showError(
                "Could not load the next exercise."
            );
        } finally {
            setBusy(false);
        }
    }


    /* =========================================================
       UI state
       ========================================================= */

    function setBusy(busy) {
        state.busy = busy;

        startButton.disabled =
            busy;


        document
            .querySelectorAll(
                ".exercise-action, #viewer-skip, #viewer-failed, #viewer-success"
            )
            .forEach((button) => {
                button.disabled =
                    busy;
            });
    }


    function showError(message) {
        emptyState.classList.remove(
            "hidden"
        );


        const text =
            emptyState.querySelector(
                "p"
            );


        if (text) {
            text.textContent =
                message;
        }
    }


    /* =========================================================
       Events
       ========================================================= */

    startButton.addEventListener(
        "click",
        startLearning
    );


    withSolutionOnly.addEventListener(
        "change",
        () => {
            state.withSolutionOnly =
                withSolutionOnly.checked;
        }
    );


    excludeSuccess.addEventListener(
        "change",
        () => {
            state.excludeSuccess =
                excludeSuccess.checked;
        }
    );


    /*
     * Fullscreen action buttons.
     */
    document
        .querySelectorAll(
            "#viewer-skip, #viewer-failed, #viewer-success"
        )
        .forEach((button) => {
            button.addEventListener(
                "click",
                () => {
                    submitResult(
                        button.dataset.action,
                        state.currentExercise
                    );
                }
            );
        });


    /* =========================================================
       Initial load
       ========================================================= */

    async function initialize() {
        await initCsrf();
        /*
         * Load restore and metadata in parallel.
         */
        await Promise.all([
            loadSubjects(),
            loadTags(),
            restoreSession()
        ]);
    }


    initialize();

})();