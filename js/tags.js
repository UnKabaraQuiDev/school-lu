document.addEventListener("DOMContentLoaded", async function () {
    await initCsrf();

    const tagsTable = document.getElementById("tags-table");
    const addTagRow = document.getElementById("add-tag-row");
    const addTagButton = document.getElementById("add-tag-button");

    const errorMessage = document.getElementById("tags-error");

    const modal = document.getElementById("tag-modal");
    const modalBackdrop = document.getElementById("tag-modal-backdrop");
    const modalTitle = document.getElementById("modal-title");

    const tagForm = document.getElementById("tag-form");
    const tagName = document.getElementById("tag-name");
    const tagColor = document.getElementById("tag-color");
    const tagColorValue = document.getElementById("tag-color-value");

    const modalError = document.getElementById("modal-error");

    const cancelTagButton =
        document.getElementById("cancel-tag-button");

    const saveTagButton =
        document.getElementById("save-tag-button");

    let editingTagId = null;


    async function apiRequest(url, options = {}) {
        const response = await fetch(apiUrl(url), {
            ...options,
            headers: {
                ...(options.headers || {}),
                [csrf.headerName]: csrf.token
            },
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(
                await response.text() ||
                `Request failed (${response.status})`
            );
        }

        if (response.status === 204) {
            return null;
        }

        return response;
    }


    async function loadTags() {
        try {
            clearError();

            const tags = await (await apiRequest("tags/list")).json();

            renderTags(tags);
        } catch (error) {
            console.error(error);
            showError(error.message);
        }
    }


    function renderTags(tags) {
        tagsTable.innerHTML = "";

        for (const tag of tags) {
            tagsTable.appendChild(createTagRow(tag));
        }

        tagsTable.appendChild(addTagRow);

        reloadLanguage();
    }


    function createTagRow(tag) {
        const row = document.createElement("tr");

        const nameCell = document.createElement("td");
        nameCell.className =
            "whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900";
        nameCell.textContent = tag.name;

        const colorCell = document.createElement("td");
        colorCell.className = "whitespace-nowrap px-6 py-4";

        const colorWrapper = document.createElement("div");
        colorWrapper.className = "flex items-center gap-3";

        const colorPreview = document.createElement("span");
        colorPreview.className =
            "inline-block h-7 w-7 rounded-full border border-gray-300 shadow-sm";

        colorPreview.style.backgroundColor = colorToCss(tag.color);

        const colorText = document.createElement("span");
        colorText.className = "text-sm text-gray-500";
        colorText.textContent = colorToHex(tag.color);

        colorWrapper.appendChild(colorPreview);
        colorWrapper.appendChild(colorText);
        colorCell.appendChild(colorWrapper);

        const optionsCell = document.createElement("td");
        optionsCell.className =
            "whitespace-nowrap px-6 py-4 text-right text-sm";

        const optionsWrapper = document.createElement("div");
        optionsWrapper.className =
            "flex justify-end gap-3";

        const editButton = document.createElement("button");
        editButton.type = "button";
        editButton.className =
            "font-medium text-indigo-600 hover:text-indigo-700";
        editButton.dataset.i18n = "tags.edit";
        editButton.textContent = "Edit";

        editButton.addEventListener("click", function () {
            openEditModal(tag);
        });

        const deleteButton = document.createElement("button");
        deleteButton.type = "button";
        deleteButton.className =
            "font-medium text-red-600 hover:text-red-700";
        deleteButton.dataset.i18n = "tags.delete";
        deleteButton.textContent = "Delete";

        deleteButton.addEventListener("click", function () {
            deleteTag(tag);
        });

        optionsWrapper.appendChild(editButton);
        optionsWrapper.appendChild(deleteButton);

        optionsCell.appendChild(optionsWrapper);

        row.appendChild(nameCell);
        row.appendChild(colorCell);
        row.appendChild(optionsCell);

        return row;
    }


    function colorToHex(color) {
        return "#" + Number(color)
            .toString(16)
            .padStart(6, "0")
            .toUpperCase();
    }


    function colorToCss(color) {
        return colorToHex(color);
    }


    function hexToColor(hex) {
        return parseInt(hex.replace("#", ""), 16);
    }


    function openEditModal(tag) {
        editingTagId = tag.id;

        modalTitle.textContent = "Edit tag";

        tagName.value = tag.name;
        tagColor.value = colorToHex(tag.color);
        tagColorValue.textContent = colorToHex(tag.color);

        modalError.classList.add("hidden");
        modalError.textContent = "";

        modal.classList.remove("hidden");
        modal.setAttribute("aria-hidden", "false");

        tagName.focus();
    }


    function openCreateModal() {
        editingTagId = null;

        modalTitle.textContent = "Add tag";

        tagName.value = "";
        tagColor.value = "#6366F1";
        tagColorValue.textContent = tagColor.value;

        modalError.classList.add("hidden");
        modalError.textContent = "";

        modal.classList.remove("hidden");
        modal.setAttribute("aria-hidden", "false");

        tagName.focus();
    }


    function closeModal() {
        modal.classList.add("hidden");
        modal.setAttribute("aria-hidden", "true");

        editingTagId = null;
    }


    async function saveTag(event) {
        event.preventDefault();

        const name = tagName.value.trim();
        const color = hexToColor(tagColor.value);

        if (!name) {
            showModalError("Tag name is required.");
            return;
        }

        setSaving(true);

        try {
            if (editingTagId === null) {
                await apiRequest("tags/new", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        name: name,
                        color: color
                    })
                });
            } else {
                await apiRequest(`tags/${editingTagId}`, {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({
                        name: name,
                        color: color
                    })
                });
            }

            closeModal();
            await loadTags();

        } catch (error) {
            console.error(error);
            showModalError(error.message);
        } finally {
            setSaving(false);
        }
    }


    async function deleteTag(tag) {
        const confirmed = confirm(
            `Delete tag "${tag.name}"?`
        );

        if (!confirmed) {
            return;
        }

        try {
            clearError();

            await apiRequest(`tags/${tag.id}`, {
                method: "DELETE"
            });

            await loadTags();
        } catch (error) {
            console.error(error);
            showError(error.message);
        }
    }


    function setSaving(saving) {
        saveTagButton.disabled = saving;
        cancelTagButton.disabled = saving;

        saveTagButton.textContent =
            saving ? "Saving..." : "Save";
    }


    function showError(message) {
        errorMessage.textContent = message;
        errorMessage.classList.remove("hidden");
    }


    function clearError() {
        errorMessage.textContent = "";
        errorMessage.classList.add("hidden");
    }


    function showModalError(message) {
        modalError.textContent = message;
        modalError.classList.remove("hidden");
    }


    addTagButton.addEventListener("click", openCreateModal);

    cancelTagButton.addEventListener("click", closeModal);

    modalBackdrop.addEventListener("click", closeModal);

    tagForm.addEventListener("submit", saveTag);

    tagColor.addEventListener("input", function () {
        tagColorValue.textContent = tagColor.value.toUpperCase();
    });

    document.addEventListener("keydown", function (event) {
        if (
            event.key === "Escape" &&
            !modal.classList.contains("hidden")
        ) {
            closeModal();
        }
    });


    loadTags();
});