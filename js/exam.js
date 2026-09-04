const exerciseElements = [
    ...document.querySelectorAll(".exercise")
];

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

// Store the current position of every active pointer.
const activePointers = new Map();


function getImages(exerciseIndex) {
    return [
        ...exerciseElements[exerciseIndex]
            .querySelectorAll(".exercise-image img")
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
    viewerImage.alt = image.alt;

    viewerCounter.textContent =
        `${currentImage + 1} / ${images.length}`;

    const previousButton =
        document.getElementById("viewer-image-prev");

    const nextButton =
        document.getElementById("viewer-image-next");

    // Hide previous arrow on the first image.
    previousButton.classList.toggle(
        "invisible",
        currentImage === 0
    );

    // Hide next arrow on the last image.
    nextButton.classList.toggle(
        "invisible",
        currentImage === images.length - 1
    );

    resetZoom();
}


function openViewer(exerciseIndex, imageIndex) {
    currentExercise = exerciseIndex;
    currentImage = imageIndex;

    viewer.classList.remove("hidden");
    document.body.classList.add("overflow-hidden");

    updateViewer();
}


function closeViewer() {
    viewer.classList.add("hidden");
    document.body.classList.remove("overflow-hidden");

    viewerImage.src = "";

    activePointers.clear();
    isDragging = false;
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


function previousExercise() {
    if (currentExercise <= 0) {
        return;
    }

    currentExercise--;

    const images = getImages(currentExercise);

    currentImage = Math.min(
        currentImage,
        images.length - 1
    );

    updateViewer();
}


function nextExercise() {
    if (currentExercise >= exerciseElements.length - 1) {
        return;
    }

    currentExercise++;

    const images = getImages(currentExercise);

    currentImage = Math.min(
        currentImage,
        images.length - 1
    );

    updateViewer();
}


exerciseElements.forEach((exercise, exerciseIndex) => {
    const images = exercise.querySelectorAll(".exercise-image");

    images.forEach((image, imageIndex) => {
        image.addEventListener("click", () => {
            openViewer(exerciseIndex, imageIndex);
        });
    });
});


function updateImageNavigation(exercise) {
    const container = exercise.querySelector(
        ".exercise-images > div"
    );

    const previousButton =
        exercise.querySelector(".previous-image");

    const nextButton =
        exercise.querySelector(".next-image");

    const hasOverflow =
        container.scrollWidth > container.clientWidth;

    const atStart =
        container.scrollLeft <= 0;

    const atEnd =
        container.scrollLeft + container.clientWidth >=
        container.scrollWidth - 1;

    // Previous arrow.
    previousButton.classList.toggle(
        "md:flex",
        hasOverflow && !atStart
    );

    previousButton.classList.toggle(
        "md:hidden",
        !hasOverflow || atStart
    );

    // Next arrow.
    nextButton.classList.toggle(
        "md:flex",
        hasOverflow && !atEnd
    );

    nextButton.classList.toggle(
        "md:hidden",
        !hasOverflow || atEnd
    );
}


exerciseElements.forEach((exercise) => {
    const container = exercise.querySelector(
        ".exercise-images > div"
    );

    const previousButton =
        exercise.querySelector(".previous-image");

    const nextButton =
        exercise.querySelector(".next-image");

    previousButton.addEventListener("click", () => {
        container.scrollBy({
            left: -350,
            behavior: "smooth"
        });
    });

    nextButton.addEventListener("click", () => {
        container.scrollBy({
            left: 350,
            behavior: "smooth"
        });
    });

    container.addEventListener("scroll", () => {
        updateImageNavigation(exercise);
    });

    updateImageNavigation(exercise);
});


document.getElementById("viewer-close")
    .addEventListener("click", closeViewer);

document.getElementById("viewer-image-prev")
    .addEventListener("click", previousImage);

document.getElementById("viewer-image-next")
    .addEventListener("click", nextImage);

document.getElementById("viewer-exercise-prev")
    .addEventListener("click", previousExercise);

document.getElementById("viewer-exercise-next")
    .addEventListener("click", nextExercise);


/* =========================================================
   ZOOM WITH MOUSE WHEEL
   ========================================================= */

viewerContainer.addEventListener(
    "wheel",
    (event) => {
        event.preventDefault();

        const oldScale = scale;
        const factor = event.deltaY < 0 ? 1.15 : 0.87;

        scale = clampScale(scale * factor);

        const rect = viewerImage.getBoundingClientRect();

        const mouseX =
            event.clientX -
            rect.left -
            rect.width / 2;

        const mouseY =
            event.clientY -
            rect.top -
            rect.height / 2;

        const ratio = scale / oldScale;

        translateX -= mouseX * (ratio - 1);
        translateY -= mouseY * (ratio - 1);

        updateTransform();
    },
    { passive: false }
);


/* =========================================================
   POINTER / PAN / PINCH
   ========================================================= */

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
        dx * dx +
        dy * dy
    );
}


function startPan(event) {
    if (scale <= 1) {
        return;
    }

    isDragging = true;

    dragStartX = event.clientX;
    dragStartY = event.clientY;

    startTranslateX = translateX;
    startTranslateY = translateY;

    viewerContainer.setPointerCapture(
        event.pointerId
    );

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
    if (viewerContainer.hasPointerCapture(event.pointerId)) {
        viewerContainer.releasePointerCapture(
            event.pointerId
        );
    }

    isDragging = false;

    viewerContainer.style.cursor =
        scale > 1 ? "grab" : "default";
}


/*
 * Left mouse button:
 *     Pan the image.
 *
 * Middle mouse button:
 *     Pan the image.
 *
 * Touch:
 *     One finger = pan.
 *     Two fingers = pinch zoom.
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

        /*
         * Two pointers = pinch zoom.
         */
        if (activePointers.size === 2) {
            const distance = getPointerDistance();

            if (distance > 0) {
                pinchStartDistance = distance;
                pinchStartScale = scale;
            }

            isDragging = false;

            viewerContainer.style.cursor = "default";

            event.preventDefault();

            return;
        }

        /*
         * Mouse:
         * left button = 0
         * middle button = 1
         */
        if (
            event.pointerType === "mouse" &&
            (event.button === 0 || event.button === 1)
        ) {
            startPan(event);

            return;
        }

        /*
         * Touch:
         * one finger pans when zoomed.
         */
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
        /*
         * Always update the pointer's current position.
         * This makes pinch zoom reliable even when only
         * one finger is moving.
         */
        if (activePointers.has(event.pointerId)) {
            activePointers.set(
                event.pointerId,
                {
                    clientX: event.clientX,
                    clientY: event.clientY
                }
            );
        }

        /*
         * Two pointers = pinch zoom.
         */
        if (activePointers.size === 2) {
            const distance = getPointerDistance();

            if (
                pinchStartDistance > 0 &&
                distance > 0
            ) {
                scale = clampScale(
                    pinchStartScale *
                    (distance / pinchStartDistance)
                );

                updateTransform();
            }

            event.preventDefault();

            return;
        }

        /*
         * One pointer = pan.
         */
        if (isDragging) {
            movePan(event);
        }
    },
    { passive: false }
);


viewerContainer.addEventListener(
    "pointerup",
    (event) => {
        activePointers.delete(event.pointerId);

        endPan(event);

        /*
         * If one pointer remains after a pinch,
         * allow it to continue as a normal pan.
         */
        if (
            activePointers.size === 1 &&
            scale > 1
        ) {
            const remainingPointer =
                [...activePointers.entries()][0];

            const pointerId =
                remainingPointer[0];

            const pointer =
                remainingPointer[1];

            isDragging = true;

            dragStartX = pointer.clientX;
            dragStartY = pointer.clientY;

            startTranslateX = translateX;
            startTranslateY = translateY;

            /*
             * Re-capture the remaining pointer.
             */
            try {
                viewerContainer.setPointerCapture(
                    pointerId
                );
            } catch {
                // Pointer capture may already be released.
            }

            viewerContainer.style.cursor = "grabbing";
        }
    }
);


viewerContainer.addEventListener(
    "pointercancel",
    (event) => {
        activePointers.delete(event.pointerId);

        endPan(event);
    }
);


/* =========================================================
   KEYBOARD CONTROLS
   ========================================================= */

document.addEventListener("keydown", (event) => {
    if (viewer.classList.contains("hidden")) {
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

        case "ArrowUp":
            previousExercise();
            break;

        case "ArrowDown":
            nextExercise();
            break;

        case "+":
        case "=":
            scale = clampScale(scale * 1.2);
            updateTransform();
            break;

        case "-":
            scale = clampScale(scale * 0.8);
            updateTransform();
            break;

        case "0":
            resetZoom();
            break;
    }
});


/* =========================================================
   CLOSE VIEWER BY CLICKING BACKGROUND
   ========================================================= */

viewer.addEventListener("click", (event) => {
    if (event.target === viewer) {
        closeViewer();
    }
});


/* =========================================================
   RESIZE
   ========================================================= */

window.addEventListener("resize", () => {
    exerciseElements.forEach(updateImageNavigation);
});