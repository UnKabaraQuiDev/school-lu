const DEFAULT_PAGE_SIZE = 25
const DEFAULT_STATUSES = ['FAILED', 'SUCCESS', 'SKIP']

let pageSize = DEFAULT_PAGE_SIZE
let currentPage = 1
let historyCount = 0
let selectedIds = new Set()
let selectedStatuses = new Set(DEFAULT_STATUSES)

const pageSizeSelect = document.getElementById('page-size')
const statusFilter = document.getElementById('status-filter')
const exerciseList = document.getElementById('exercise-list')
const pagination = document.getElementById('pagination')
const loading = document.getElementById('loading')
const error = document.getElementById('error')
const empty = document.getElementById('empty')
const retry = document.getElementById('retry')

const selectionBar = document.getElementById('selection-bar')
const selectedCount = document.getElementById('selected-count')
const deleteSelected = document.getElementById('delete-selected')

function escapeHtml(value) {
    if (value == null) {
        return ''
    }

    return String(value)
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#039;')
}

function examPath(exam) {
    const section = encodeURIComponent(exam.section)
    const subject = encodeURIComponent(exam.subject)
    const year = encodeURIComponent(exam.year)
    const seasonSubtype = encodeURIComponent(
        `${exam.season}_${exam.subtype}`
    )

    return `/exam-db/${section}/${subject}/${year}/${seasonSubtype}/`
}

function renderExam(exam) {
    const path = examPath(exam)

    return `
        <p class="mb-3 flex flex-wrap items-center gap-1 text-sm text-gray-600">
            <a class="underline hover:text-black" href="/exam-db/">
                Sections
            </a>

            <span>/</span>

            <a class="underline hover:text-black"
                href="/exam-db/${encodeURIComponent(exam.section)}/">
                ${escapeHtml(exam.section)}
            </a>

            <span>/</span>

            <a class="underline hover:text-black"
                href="/exam-db/${encodeURIComponent(exam.section)}/${encodeURIComponent(exam.subject)}/">
                ${escapeHtml(exam.subject)}
            </a>

            <span>/</span>

            <a class="underline hover:text-black"
                href="/exam-db/${encodeURIComponent(exam.section)}/${encodeURIComponent(exam.subject)}/${encodeURIComponent(exam.year)}/">
                ${escapeHtml(exam.year)}
            </a>

            <span>/</span>

            <a class="underline hover:text-black"
                href="${path}">
                ${escapeHtml(exam.season)}_${escapeHtml(exam.subtype)}
            </a>
        </p>
    `
}

function renderStatus(status) {
    if (status === 'SUCCESS') {
        return `
            <span class="font-semibold text-green-600"
                    data-i18n="actions.success">
                Success
            </span>
        `
    }

    if (status === 'FAILED') {
        return `
            <span class="font-semibold text-red-600"
                    data-i18n="actions.failed">
                Failed
            </span>
        `
    }

    return `
        <span class="font-semibold text-gray-500"
                data-i18n="actions.skip">
            Skip
        </span>
    `
}

function renderExercise(exercise) {
    const id = exercise.id
    const checked = selectedIds.has(id) ? 'checked' : ''

    const exerciseName = exercise.exerciseName
        ? escapeHtml(exercise.exerciseName)
        : `<span data-i18n="exercise">Exercise</span> ${escapeHtml(exercise.exerciseIndex)}`

    const timestamp = new Date(exercise.timestamp).toLocaleString()

    return `
        <article
            class="grid grid-cols-[40px_minmax(0,1fr)_auto_40px] items-center gap-3 rounded-xl bg-white px-4 py-3 shadow-sm"
            data-exercise-id="${id}">

            <!-- Selection -->
            <label
                class="flex h-9 w-9 cursor-pointer items-center justify-center rounded-lg bg-gray-100 hover:bg-gray-200">

                <input
                    type="checkbox"
                    class="exercise-checkbox h-4 w-4 rounded border-gray-300"
                    data-id="${id}"
                    ${checked}>

                <span class="sr-only" data-i18n="history.select">
                    Select
                </span>
            </label>

            <!-- Exercise -->
            <div class="min-w-0">
                <div class="rounded-lg bg-gray-50 px-4 py-3">
                    <span
                        class="block font-semibold text-gray-900">
                        ${exerciseName}
                    </span>

                    <div class="mt-1">
                        ${renderExam(exercise.exam)}
                    </div>
                </div>
            </div>

            <!-- Status + timestamp -->
            <div class="min-w-[110px] px-4 py-3 text-center">
                <div>
                    ${renderStatus(exercise.status)}
                </div>

                <div class="mt-3 text-xs text-gray-500">
                    ${escapeHtml(timestamp)}
                </div>
            </div>

            <!-- Delete -->
            <button
                type="button"
                class="delete-exercise flex h-9 w-9 items-center justify-center rounded-lg bg-red-50 text-xl font-bold leading-none text-red-600 hover:bg-red-100"
                data-id="${id}"
                aria-label="Delete exercise"
                data-i18n-aria-label="history.delete">
                ×
            </button>

        </article>
    `
}

function updateSelectionBar() {
    selectedCount.textContent = selectedIds.size

    if (selectedIds.size === 0) {
        selectionBar.classList.add('hidden')
        selectionBar.classList.remove('flex')
    } else {
        selectionBar.classList.remove('hidden')
        selectionBar.classList.add('flex')
    }
}

function renderPagination() {
    pagination.innerHTML = ''

    const pageCount = Math.ceil(historyCount / pageSize)

    if (pageCount <= 1) {
        pagination.classList.add('hidden')
        return
    }

    pagination.classList.remove('hidden')

    for (let page = 1; page <= pageCount; page++) {
        const button = document.createElement('button')

        button.type = 'button'
        button.textContent = page

        button.className = page === currentPage
            ? 'rounded-lg bg-black px-4 py-2 font-semibold text-white'
            : 'rounded-lg bg-white px-4 py-2 font-semibold text-gray-700 shadow-sm hover:bg-gray-100'

        button.addEventListener('click', () => {
            if (page === currentPage) {
                return
            }

            currentPage = page
            selectedIds.clear()
            updateSelectionBar()
            loadPage()
        })

        pagination.appendChild(button)
    }
}

function buildStatusQuery() {
    const params = new URLSearchParams()

    selectedStatuses.forEach(status => {
        params.append('status', status)
    })

    return params.toString()
}

function historyPageUrl() {
    const query = buildStatusQuery()

    return `history/page/${currentPage}/${pageSize}${query ? `?${query}` : ''}`
}

function historyCountUrl() {
    const query = buildStatusQuery()

    return `history/count${query ? `?${query}` : ''}`
}

async function loadPage() {
    showLoading()

    try {
        const exercises = await getJSON(historyPageUrl())

        renderExercises(exercises)
        renderPagination()
    } catch (e) {
        console.error(e)
        showError()
    }
}

function renderExercises(exercises) {
    loading.classList.add('hidden')
    error.classList.add('hidden')

    if (!exercises || exercises.length === 0) {
        exerciseList.classList.add('hidden')
        empty.classList.remove('hidden')
        return
    }

    empty.classList.add('hidden')
    exerciseList.classList.remove('hidden')

    exerciseList.innerHTML = exercises
        .map(renderExercise)
        .join('')

    exerciseList.querySelectorAll('.exercise-checkbox')
        .forEach(checkbox => {
            checkbox.addEventListener('change', event => {
                const id = Number(event.target.dataset.id)

                if (event.target.checked) {
                    selectedIds.add(id)
                } else {
                    selectedIds.delete(id)
                }

                updateSelectionBar()
            })
        })

    exerciseList.querySelectorAll('.delete-exercise')
        .forEach(button => {
            button.addEventListener('click', async () => {
                await deleteExercise(Number(button.dataset.id))
            })
        })

    updateSelectionBar()

    reloadLanguage()
}

async function deleteExercise(id) {
    try {
        const response = await fetch(
            apiUrl(`history/remove/${id}`),
            {
                method: 'PATCH',
                credentials: 'include',
                headers: {
                    Accept: 'application/json',
                    [csrf.headerName]: csrf.token
                }
            }
        )

        if (!response.ok) {
            throw new Error(`${response.status} ${response.statusText}`)
        }

        selectedIds.delete(id)

        await loadHistoryCount()

        const pageCount = Math.max(1, Math.ceil(historyCount / pageSize))

        if (currentPage > pageCount) {
            currentPage = pageCount
        }

        updateSelectionBar()
        await loadPage()

    } catch (e) {
        console.error(e)
    }
}

async function deleteSelectedExercises() {
    const ids = [...selectedIds]

    if (ids.length === 0) {
        return
    }

    try {
        const response = await fetch(
            apiUrl('history/remove'),
            {
                method: 'PATCH',
                credentials: 'include',
                headers: {
                    Accept: 'application/json',
                    'Content-Type': 'application/json',
                    [csrf.headerName]: csrf.token
                },
                body: JSON.stringify(ids)
            }
        )

        if (!response.ok) {
            throw new Error(`${response.status} ${response.statusText}`)
        }

        selectedIds.clear()

        await loadHistoryCount()

        const pageCount = Math.max(1, Math.ceil(historyCount / pageSize))

        if (currentPage > pageCount) {
            currentPage = pageCount
        }

        updateSelectionBar()
        await loadPage()

    } catch (e) {
        console.error(e)
    }
}

async function loadHistoryCount() {
    historyCount = await getJSON(historyCountUrl())
}

async function loadPageSize() {
    try {
        const size = await getJSON('history/page-size')

        if (
            Number.isInteger(size) &&
            size > 0 &&
            [...pageSizeSelect.options]
                .some(option => Number(option.value) === size)
        ) {
            pageSize = size
        } else {
            pageSize = DEFAULT_PAGE_SIZE
        }
    } catch (e) {
        console.warn('Failed to load saved page size, using default.', e)
        pageSize = DEFAULT_PAGE_SIZE
    }

    pageSizeSelect.value = pageSize
}

async function loadStatusFilter() {
    try {
        const statuses = await getJSON('history/filter')

        if (
            Array.isArray(statuses) &&
            statuses.every(status => DEFAULT_STATUSES.includes(status))
        ) {
            selectedStatuses = new Set(statuses)
        } else {
            selectedStatuses = new Set(DEFAULT_STATUSES)
        }
    } catch (e) {
        console.warn('Failed to load saved status filter, using default.', e)
        selectedStatuses = new Set(DEFAULT_STATUSES)
    }

    statusFilter.querySelectorAll('input[type="checkbox"]')
        .forEach(checkbox => {
            checkbox.checked = selectedStatuses.has(checkbox.value)
        })
}

async function changeStatusFilter() {
    selectedStatuses = new Set(
        [...statusFilter.querySelectorAll('input[type="checkbox"]:checked')]
            .map(checkbox => checkbox.value)
    )

    currentPage = 1
    selectedIds.clear()
    updateSelectionBar()

    try {
        await putJSON(
            `history/filter?${buildStatusQuery()}`,
            null
        )
    } catch (e) {
        console.warn('Failed to save status filter.', e)
    }

    try {
        await loadHistoryCount()
        await loadPage()
    } catch (e) {
        console.error(e)
        showError()
    }
}

async function changePageSize() {
    const newPageSize = Number(pageSizeSelect.value)

    if (!newPageSize || newPageSize <= 0) {
        return
    }

    pageSize = newPageSize
    currentPage = 1
    selectedIds.clear()
    updateSelectionBar()

    try {
        await putJSON(`history/page-size?size=${pageSize}`, null)
    } catch (e) {
        console.warn('Failed to save page size.', e)
    }

    await loadPage()
}

function showLoading() {
    loading.classList.remove('hidden')
    error.classList.add('hidden')
    empty.classList.add('hidden')
    exerciseList.classList.add('hidden')
}

function showError() {
    loading.classList.add('hidden')
    empty.classList.add('hidden')
    exerciseList.classList.add('hidden')
    error.classList.remove('hidden')
}

async function init() {
    showLoading()

    try {
        /*
         * Start all three independent initial requests together.
         * CSRF, page size and status filter do not depend on each other.
         */
        await Promise.all([
            initCsrf(),
            loadPageSize(),
            loadStatusFilter()
        ])

        await loadHistoryCount()
        await loadPage()

    } catch (e) {
        console.error(e)
        showError()
    }
}

pageSizeSelect.addEventListener('change', changePageSize)

statusFilter.addEventListener('change', changeStatusFilter)

retry.addEventListener('click', init)

deleteSelected.addEventListener(
    'click',
    deleteSelectedExercises
)

init()
