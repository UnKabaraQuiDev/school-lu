(() => {
    "use strict";

    const viewer = document.getElementById("image-viewer");
    const viewerContainer = document.getElementById("viewer-container");
    const viewerImage = document.getElementById("viewer-image");
    const viewerCounter = document.getElementById("viewer-counter");

    let currentExercise = 0;
    let currentImage = 0;

    let scale = 1;
    let translateX = 0;
    let translateY = 0;

    let isDragging = false;

    let dragStartX = 0;
    let dragStartY = 0;

    let startTranslateX = 0;
    let startTranslateY = 0;

    let pinchStartDistance = 0;
    let pinchStartScale = 1;

    const activePointers = new Map();


    function getExerciseElements() {
        return [
            ...document.querySelectorAll(".exercise")
        ];
    }


    function getImages(exerciseIndex) {
        const exercises = getExerciseElements();
        const exercise = exercises[exerciseIndex];

        if (!exercise) {
            return [];
        }

        return [
            ...exercise.querySelectorAll(".exercise-image img")
        ];
    }


    function resetZoom() {
        scale = 1;
        translateX = 0;
        translateY = 0;

        updateTransform();
    }


    function updateTransform() {
        viewerImage.style.transform =
            `translate(${translateX}px, ${translateY}px) scale(${scale})`;
    }


    function clampScale(value) {
        return Math.min(Math.max(value, 0.5), 8);
    }


    function updateViewer() {
        const images = getImages(currentExercise);
        const image = images[currentImage];

        if (!image) {
            return;
        }

        viewerImage.src = image.src;
        viewerImage.alt = image.alt || "";

        viewerCounter.textContent =
            `${currentImage + 1} / ${images.length}`;

        const previousButton =
            document.getElementById("viewer-image-prev");

        const nextButton =
            document.getElementById("viewer-image-next");

        previousButton.classList.toggle(
            "invisible",
            currentImage === 0
        );

        nextButton.classList.toggle(
            "invisible",
            currentImage === images.length - 1
        );

        resetZoom();
        reloadLanguage();
    }


    function openViewer(exerciseIndex, imageIndex) {
        currentExercise = exerciseIndex;
        currentImage = imageIndex;

        viewer.classList.remove("hidden");
        document.body.classList.add("overflow-hidden");

        updateViewer();

        viewerContainer.style.cursor = "default";
    }


    function closeViewer() {
        viewer.classList.add("hidden");
        document.body.classList.remove("overflow-hidden");

        viewerImage.src = "";

        activePointers.clear();
        isDragging = false;
        pinchStartDistance = 0;

        viewerContainer.style.cursor = "default";
    }


    function previousImage() {
        if (currentImage <= 0) {
            return;
        }

        currentImage--;
        updateViewer();
    }


    function nextImage() {
        const images = getImages(currentExercise);

        if (currentImage >= images.length - 1) {
            return;
        }

        currentImage++;
        updateViewer();
    }


    function getPointerDistance() {
        const pointers = [...activePointers.values()];

        if (pointers.length < 2) {
            return 0;
        }

        const a = pointers[0];
        const b = pointers[1];

        const dx = a.clientX - b.clientX;
        const dy = a.clientY - b.clientY;

        return Math.sqrt(
            dx * dx + dy * dy
        );
    }


    function startPan(event) {
        // if (scale <= 1) {
        //     return;
        // }

        isDragging = true;

        dragStartX = event.clientX;
        dragStartY = event.clientY;

        startTranslateX = translateX;
        startTranslateY = translateY;

        try {
            viewerContainer.setPointerCapture(
                event.pointerId
            );
        } catch {
            // Pointer capture is not available in every situation.
        }

        viewerContainer.style.cursor = "grabbing";

        event.preventDefault();
    }


    function movePan(event) {
        if (!isDragging) {
            return;
        }

        translateX =
            startTranslateX +
            event.clientX -
            dragStartX;

        translateY =
            startTranslateY +
            event.clientY -
            dragStartY;

        updateTransform();

        event.preventDefault();
    }


    function endPan(event) {
        if (
            viewerContainer.hasPointerCapture &&
            viewerContainer.hasPointerCapture(event.pointerId)
        ) {
            try {
                viewerContainer.releasePointerCapture(
                    event.pointerId
                );
            } catch {
                // Pointer capture may already be released.
            }
        }

        isDragging = false;

        viewerContainer.style.cursor = "grab";
    }


    function bindExercise(exercise, exerciseIndex, openFirst = false) {
        const images = exercise.querySelectorAll(
            ".exercise-image"
        );

        images.forEach((image, imageIndex) => {
            image.addEventListener("click", () => {
                openViewer(
                    exerciseIndex,
                    imageIndex
                );
            });
        });


        const container = exercise.querySelector(
            ".exercise-images > div"
        );

        const previousButton =
            exercise.querySelector(".previous-image");

        const nextButton =
            exercise.querySelector(".next-image");


        function updateImageNavigation() {
            const hasOverflow =
                container.scrollWidth > container.clientWidth;

            const atStart =
                container.scrollLeft <= 0;

            const atEnd =
                container.scrollLeft +
                container.clientWidth >=
                container.scrollWidth - 1;


            previousButton.classList.toggle(
                "hidden",
                !hasOverflow || atStart
            );

            previousButton.classList.toggle(
                "flex",
                hasOverflow && !atStart
            );


            nextButton.classList.toggle(
                "hidden",
                !hasOverflow || atEnd
            );

            nextButton.classList.toggle(
                "flex",
                hasOverflow && !atEnd
            );
        }


        previousButton.addEventListener(
            "click",
            () => {
                container.scrollBy({
                    left: -350,
                    behavior: "smooth"
                });
            }
        );


        nextButton.addEventListener(
            "click",
            () => {
                container.scrollBy({
                    left: 350,
                    behavior: "smooth"
                });
            }
        );


        container.addEventListener(
            "scroll",
            updateImageNavigation
        );


        updateImageNavigation();

        if (openFirst && images.length > 0) {
            openViewer(exerciseIndex, 0);
        }
    }


    /*
     * Called by settings.js after an exercise has been
     * created from the template.
     */
    window.LearnImageViewer = {
        bindExercise,
        closeViewer
    };


    document.getElementById("viewer-close")
        .addEventListener("click", closeViewer);


    document.getElementById("viewer-image-prev")
        .addEventListener("click", previousImage);


    document.getElementById("viewer-image-next")
        .addEventListener("click", nextImage);


    /*
     * Zoom with mouse wheel.
     */
    viewerContainer.addEventListener(
        "wheel",
        (event) => {
            event.preventDefault();

            const oldScale = scale;
            const factor =
                event.deltaY < 0
                    ? 1.15
                    : 0.87;

            scale = clampScale(
                scale * factor
            );

            const rect =
                viewerImage.getBoundingClientRect();

            const mouseX =
                event.clientX -
                rect.left -
                rect.width / 2;

            const mouseY =
                event.clientY -
                rect.top -
                rect.height / 2;

            const ratio =
                scale / oldScale;

            translateX -=
                mouseX * (ratio - 1);

            translateY -=
                mouseY * (ratio - 1);

            updateTransform();

            viewerContainer.style.cursor =
                scale > 1 ? "grab" : "default";
        },
        { passive: false }
    );


    /*
     * Pointer down.
     *
     * Left mouse button:
     *     Pan when zoomed.
     *
     * Middle mouse button:
     *     Pan when zoomed.
     *
     * Touch:
     *     One finger = pan.
     *     Two fingers = pinch.
     */
    viewerContainer.addEventListener(
        "pointerdown",
        (event) => {
            activePointers.set(
                event.pointerId,
                {
                    clientX: event.clientX,
                    clientY: event.clientY
                }
            );


            if (activePointers.size === 2) {
                const distance =
                    getPointerDistance();

                if (distance > 0) {
                    pinchStartDistance = distance;
                    pinchStartScale = scale;
                }

                isDragging = false;
                viewerContainer.style.cursor = "default";

                event.preventDefault();

                return;
            }


            if (
                event.pointerType === "mouse" &&
                (
                    event.button === 0 ||
                    event.button === 1
                )
            ) {
                startPan(event);
                return;
            }


            if (
                event.pointerType === "touch" &&
                scale > 1
            ) {
                startPan(event);
            }
        },
        { passive: false }
    );


    viewerContainer.addEventListener(
        "pointermove",
        (event) => {
            if (activePointers.has(event.pointerId)) {
                activePointers.set(
                    event.pointerId,
                    {
                        clientX: event.clientX,
                        clientY: event.clientY
                    }
                );
            }


            if (activePointers.size === 2) {
                const distance =
                    getPointerDistance();

                if (
                    pinchStartDistance > 0 &&
                    distance > 0
                ) {
                    scale = clampScale(
                        pinchStartScale *
                        (
                            distance /
                            pinchStartDistance
                        )
                    );

                    updateTransform();
                }

                event.preventDefault();

                return;
            }


            if (isDragging) {
                movePan(event);
            }
        },
        { passive: false }
    );


    viewerContainer.addEventListener(
        "pointerup",
        (event) => {
            activePointers.delete(
                event.pointerId
            );

            endPan(event);


            /*
             * Continue panning if one finger remains
             * after a pinch.
             */
            if (
                activePointers.size === 1 &&
                scale > 1
            ) {
                const [
                    pointerId,
                    pointer
                ] = [
                    ...activePointers.entries()
                ][0];

                isDragging = true;

                dragStartX =
                    pointer.clientX;

                dragStartY =
                    pointer.clientY;

                startTranslateX =
                    translateX;

                startTranslateY =
                    translateY;

                try {
                    viewerContainer.setPointerCapture(
                        pointerId
                    );
                } catch {
                    // Pointer capture may already be released.
                }

                viewerContainer.style.cursor =
                    "grabbing";
            }
        }
    );


    viewerContainer.addEventListener(
        "pointercancel",
        (event) => {
            activePointers.delete(
                event.pointerId
            );

            endPan(event);
        }
    );


    /*
     * Keyboard controls.
     *
     * Only image navigation remains.
     * Up/down exercise navigation was removed.
     */
    document.addEventListener(
        "keydown",
        (event) => {
            if (
                viewer.classList.contains("hidden")
            ) {
                return;
            }


            switch (event.key) {
                case "Escape":
                    closeViewer();
                    break;

                case "ArrowLeft":
                    previousImage();
                    break;

                case "ArrowRight":
                    nextImage();
                    break;

                case "+":
                case "=":
                    scale = clampScale(
                        scale * 1.2
                    );

                    updateTransform();
                    break;

                case "-":
                    scale = clampScale(
                        scale * 0.8
                    );

                    updateTransform();
                    break;

                case "0":
                    resetZoom();
                    break;
            }
        }
    );


    /*
     * Clicking the dark background closes the viewer.
     */
    viewer.addEventListener(
        "click",
        (event) => {
            if (event.target === viewer) {
                closeViewer();
            }
        }
    );


    window.addEventListener(
        "resize",
        () => {
            getExerciseElements().forEach(
                (exercise) => {
                    const container =
                        exercise.querySelector(
                            ".exercise-images > div"
                        );

                    if (!container) {
                        return;
                    }

                    container.dispatchEvent(
                        new Event("scroll")
                    );
                }
            );
        }
    );

})();