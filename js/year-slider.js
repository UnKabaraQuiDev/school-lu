const MIN_YEAR = 1995;
const MAX_YEAR = new Date().getFullYear() + 1;

const yearFrom = document.getElementById("year-from");
const yearTo = document.getElementById("year-to");
const yearRangeMin = document.getElementById("year-range-min");
const yearRangeMax = document.getElementById("year-range-max");
const yearRangeSelected = document.getElementById("year-range-selected");

yearRangeMin.min = MIN_YEAR;
yearRangeMin.max = MAX_YEAR;

yearRangeMax.min = MIN_YEAR;
yearRangeMax.max = MAX_YEAR;

yearFrom.min = MIN_YEAR;
yearFrom.max = MAX_YEAR;

yearTo.min = MIN_YEAR;
yearTo.max = MAX_YEAR;

// Initial values
yearRangeMin.value = MIN_YEAR;
yearRangeMax.value = MAX_YEAR;
yearFrom.value = MIN_YEAR;
yearTo.value = MAX_YEAR;

function updateYearRange(source) {
    let min = Number(yearRangeMin.value);
    let max = Number(yearRangeMax.value);

    if (min > max) {
        if (source === yearRangeMin) {
            min = max;
            yearRangeMin.value = min;
        } else {
            max = min;
            yearRangeMax.value = max;
        }
    }

    yearFrom.value = min;
    yearTo.value = max;

    const range = MAX_YEAR - MIN_YEAR;

    const left = ((min - MIN_YEAR) / range) * 100;
    const right = ((max - MIN_YEAR) / range) * 100;

    yearRangeSelected.style.left = `${left}%`;
    yearRangeSelected.style.width = `${right - left}%`;
}

yearRangeMin.addEventListener("input", () => {
    updateYearRange(yearRangeMin);
});

yearRangeMax.addEventListener("input", () => {
    updateYearRange(yearRangeMax);
});

yearFrom.addEventListener("change", () => {
    let value = Number(yearFrom.value);

    value = Math.max(MIN_YEAR, Math.min(value, Number(yearTo.value)));

    yearFrom.value = value;
    yearRangeMin.value = value;

    updateYearRange(yearRangeMin);
});

yearTo.addEventListener("change", () => {
    let value = Number(yearTo.value);

    value = Math.min(MAX_YEAR, Math.max(value, Number(yearFrom.value)));

    yearTo.value = value;
    yearRangeMax.value = value;

    updateYearRange(yearRangeMax);
});

function getYearRange() {
    return [
        Number(yearRangeMin.value),
        Number(yearRangeMax.value)
    ];
}

function setYearRange(min, max) {
    min = Number(min);
    max = Number(max);

    if (!Number.isInteger(min) || !Number.isInteger(max)) {
        throw new Error("Years must be integers");
    }

    if (min < MIN_YEAR || max > MAX_YEAR) {
        throw new Error(
            `Years must be between ${MIN_YEAR} and ${MAX_YEAR}`
        );
    }

    if (min > max) {
        throw new Error(
            "The start year cannot be greater than the end year"
        );
    }

    yearRangeMin.value = min;
    yearRangeMax.value = max;

    updateYearRange();
}

updateYearRange();