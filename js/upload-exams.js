document.addEventListener("DOMContentLoaded", async function () {
    await initCsrf();

    const form = document.getElementById("update-index-form");
    const fileInput = document.getElementById("csv-file");

    const allowSectionCreation =
        document.getElementById("allow-section-creation");

    const allowSubjectCreation =
        document.getElementById("allow-subject-creation");

    const submitButton =
        document.getElementById("submit-button");

    const progressContainer =
        document.getElementById("progress-container");

    const progressBar =
        document.getElementById("progress-bar");

    const progressValue =
        document.getElementById("progress-value");

    const progressMessage =
        document.getElementById("progress-message");

    const progressStatus =
        document.getElementById("progress-status");

    const errorMessage =
        document.getElementById("error-message");

    const successMessage =
        document.getElementById("success-message");


    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const file = fileInput.files[0];

        if (!file) {
            showError("Please select a CSV file.");
            return;
        }

        if (!file.name.toLowerCase().endsWith(".csv")) {
            showError("Only CSV files are allowed.");
            return;
        }

        clearMessages();
        setLoading(true);

        progressContainer.classList.remove("hidden");
        updateProgress(0);

        const formData = new FormData();

        formData.append("file", file);
        formData.append(
            "allowSectionCreation",
            allowSectionCreation.checked
        );
        formData.append(
            "allowSubjectCreation",
            allowSubjectCreation.checked
        );

        try {
            const response = await fetch(apiUrl("exam-db/exams/update-index"), {
                method: "POST",
                headers: {
                    [csrf.headerName]: csrf.token
                },
                credentials: "include",
                body: formData
            });

            if (!response.ok) {
                throw new Error(
                    await response.text() || "Failed to update index."
                );
            }

            if (!response.body) {
                throw new Error("The server did not return a stream.");
            }

            await readSseStream(response.body);

        } catch (error) {
            console.error(error);
            showError(error.message);
            setLoading(false);
        }
    });


    async function readSseStream(body) {
        const reader = body.getReader();
        const decoder = new TextDecoder();

        let buffer = "";

        while (true) {
            const { value, done } = await reader.read();

            if (done) {
                break;
            }

            buffer += decoder.decode(value, { stream: true });

            const events = buffer.split("\n\n");

            buffer = events.pop();

            for (const event of events) {
                handleSseEvent(event);
            }
        }

        if (buffer.trim()) {
            handleSseEvent(buffer);
        }
    }


    function handleSseEvent(rawEvent) {
        let eventName = "message";
        let data = "";

        for (const line of rawEvent.split("\n")) {
            if (line.startsWith("event:")) {
                eventName = line.substring(6).trim();
            }

            if (line.startsWith("data:")) {
                data += line.substring(5).trim();
            }
        }

        if (!data) {
            return;
        }

        switch (eventName) {
            case "progress":
                handleProgress(data);
                break;

            case "complete":
                handleComplete(data);
                break;

            case "error":
                handleError(data);
                break;

            case "warning":
                handleWarning(data);

            default:
                console.log("SSE:", eventName, data);
        }
    }


    function handleProgress(data) {
        const [current, total] = data.split("/").map(Number);

        if (
            Number.isFinite(current) &&
            Number.isFinite(total) &&
            total > 0
        ) {
            const progress = Math.round((current / total) * 100);

            updateProgress(progress);
            progressMessage.textContent = `${current} / ${total}`;
        } else {
            progressMessage.textContent = data;
        }
    }


    function handleComplete(data) {
        updateProgress(100);

        progressStatus.textContent = "Complete";
        progressMessage.textContent = data;

        successMessage.textContent = data;
        successMessage.classList.remove("hidden");

        setLoading(false);
    }


    function handleError(data) {
        showError(data);
        setLoading(false);
    }

    function handleWarning(data) {
        appendWarning(data);
    }

    function updateProgress(progress) {
        const value = Math.max(0, Math.min(100, progress));

        progressBar.style.width = `${value}%`;
        progressValue.textContent = `${value}%`;
    }


    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.remove("hidden");
    }

    function appendWarning(message) {
        const warning = document.createElement("div");

        warning.textContent = message;

        errorMessage.appendChild(warning);
        errorMessage.classList.remove("hidden");
    }


    function clearMessages() {
        errorMessage.classList.add("hidden");
        successMessage.classList.add("hidden");
        errorMessage.textContent = "";
        successMessage.textContent = "";
        progressMessage.textContent = "";
    }


    function setLoading(loading) {
        submitButton.disabled = loading;
        fileInput.disabled = loading;
        allowSectionCreation.disabled = loading;
        allowSubjectCreation.disabled = loading;

        if (loading) {
            submitButton.textContent = "Updating...";
        } else {
            submitButton.textContent = "Update index";
        }
    }
});
