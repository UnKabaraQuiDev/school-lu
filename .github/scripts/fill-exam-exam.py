import csv
import re
import subprocess
from collections import OrderedDict, defaultdict
from html import escape
from pathlib import Path


# ---------------------------------------------------------------------------
# Paths
# ---------------------------------------------------------------------------

ROOT_DIR = Path(
    subprocess.check_output(
        [
            "git",
            "-C",
            str(Path(__file__).resolve().parent),
            "rev-parse",
            "--show-toplevel",
        ],
        text=True,
    ).strip()
)

DATA_REL = "/data"
DATA_DIR = ROOT_DIR / "data"
EXAMS_DB_DIR = ROOT_DIR / "exam-db"
TEMPLATE_DIR = EXAMS_DB_DIR

CSV_FILE = DATA_DIR / "exams" / "exercises.csv"

EXAM_TEMPLATE_FILE = TEMPLATE_DIR / "exam.template.html"
EXERCISES_TEMPLATE_FILE = TEMPLATE_DIR / "exercises.template.html"
EXERCISE_IMAGE_TEMPLATE_FILE = TEMPLATE_DIR / "exercise-image.template.html"


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def require_file(path: Path) -> None:
    if not path.exists():
        raise FileNotFoundError(f"Required file not found: {path}")


def read_template(path: Path) -> str:
    require_file(path)
    return path.read_text(encoding="utf-8")


def normalize(value: str) -> str:
    return value.strip()


def make_absolute_site_path(path: str) -> str:
    """
    Convert a repository-relative path such as:

        exams/CA/CA_MATHE/file.pdf

    into:

        /data/exams/CA/CA_MATHE/file.pdf
    """
    path = normalize(path)

    if not path:
        return ""

    return DATA_REL + "/" + path.lstrip("/")


def escape_attribute(value: str) -> str:
    return escape(value, quote=True)


# ---------------------------------------------------------------------------
# Exam identification
# ---------------------------------------------------------------------------

EXAM_FIELDS = (
    "Section",
    "Subject",
    "Year",
    "Subtype",
    "Season",
)


def exam_key(row: dict) -> tuple[str, ...]:
    """
    Identify an exam.

    Source and Attachment are deliberately NOT part of the key because
    an exam can contain multiple documents and multiple exercise images.
    """
    return tuple(
        normalize(row[field])
        for field in EXAM_FIELDS
    )


def exam_key_from_source(source: str) -> tuple[str, ...] | None:
    """
    Convert a SourceExam key into an exam key.

    SourceExam has this format:

        Section:Subject:Year:Season:Subtype:Name:Qualifier

    The exam itself is identified by:

        Section
        Subject
        Year
        Subtype
        Season

    Example:

        CC:MATH1:2025:ETE:NORMAL::STATEMENT

    becomes:

        ("CC", "MATH1", "2025", "NORMAL", "ETE")
    """

    source = normalize(source)

    if not source:
        return None

    parts = source.split(":")

    if len(parts) != 7:
        return None

    section, subject, year, season, subtype, _, qualifier = parts

    return (
        section,
        subject,
        year,
        subtype,
        season,
    )


def source_qualifier(source: str) -> str:
    """
    Return the document qualifier from a SourceExam key.

    Example:

        CC:MATH1:2025:ETE:NORMAL::STATEMENT

    returns:

        STATEMENT
    """

    parts = normalize(source).split(":")

    if len(parts) != 7:
        return ""

    return parts[6].upper()


# ---------------------------------------------------------------------------
# Document links
# ---------------------------------------------------------------------------

def create_button(url: str, translation_key: str, color: str) -> str:
    if not url:
        return ""

    return f"""
<a
    href="/data/{escape(url, quote=True)}"
    target="_blank"
    rel="noopener"
    data-i18n="{escape(translation_key.lower(), quote=True)}"
    class="inline-block px-3 py-1.5 rounded-md bg-{color}-100 text-{color}-700 hover:bg-{color}-200 transition"
>{escape(translation_key)}</a>
"""


def create_exam_links(rows: list[dict]) -> str:
    """
    Create document buttons from the CSV rows.

    The button type is determined by the CSV's Qualifier column.
    """
    links: list[str] = []
    seen: set[tuple[str, str]] = set()

    qualifier_styles = {
        "STATEMENT": ("Mission", "blue"),
        "SOLUTION": ("Solution", "green"),
        "DATA": ("Data", "purple"),
        "ORAL": ("Oral", "cyan"),
    }

    for row in rows:
        url = normalize(row.get("Source", ""))
        qualifier = normalize(
            row.get("Qualifier", "")
        ).upper()

        if not url:
            continue

        style = qualifier_styles.get(qualifier)

        if style is None:
            continue

        translation_key, color = style

        key = (url, qualifier)

        if key in seen:
            continue

        seen.add(key)

        links.append(
            create_button(
                url,
                translation_key,
                color,
            )
        )

    return "".join(links)


# ---------------------------------------------------------------------------
# Reuse table
# ---------------------------------------------------------------------------

REUSE_QUALIFIERS = (
    "STATEMENT",
    "SOLUTION",
    "DATA",
    "ORAL",
)

def create_exam_link(
    target_exam: tuple[str, ...],
) -> str:
    """
    Create the requested breadcrumb-style exam link.

    The hrefs are relative to the current exam directory.
    """

    section, subject, year, subtype, season = target_exam

    return f"""
                <a class="underline" href="../../../../{escape(section, quote=True)}/">{escape(section)}</a>
                <span>/</span>
                <a class="underline" href="../../../../{escape(section, quote=True)}/{escape(subject, quote=True)}/">{escape(subject)}</a>
                <span>/</span>
                <a class="underline" href="../../../../{escape(section, quote=True)}/{escape(subject, quote=True)}/{escape(year, quote=True)}/">{escape(year)}</a>
                <span>/</span>
                <a class="underline" href="../../../../{escape(section, quote=True)}/{escape(subject, quote=True)}/{escape(year, quote=True)}/{escape(season, quote=True)}_{escape(subtype, quote=True)}/">{escape(season)}_{escape(subtype)}</a>
"""


def create_reuse_table(
    current_exam: tuple[str, ...],
    exams: OrderedDict[tuple[str, ...], list[dict]],
) -> str:
    """
    Create the {{REUSE}} table.

    Relationships are derived entirely from SourceExam.

    For each SourceExam:

        Section:Subject:Year:Season:Subtype:Name:Qualifier

    the source exam is connected to the exam containing that row.

    The resulting table contains:
        - the current exam
        - source exams referenced by it
        - exams which reference it
        - exams connected through the same source

    A checkmark is displayed when that exam has reused/linked the
    corresponding document qualifier.
    """

    # ---------------------------------------------------------------
    # Build:
    #
    #     source_exam -> [(child_exam, qualifier)]
    #
    # Example:
    #
    #     CC:MATH1:2025:ETE:NORMAL
    #         -> [
    #              (CI,MATH1,2025,NORMAL,ETE, STATEMENT),
    #              (CI,MATH1,2025,NORMAL,ETE, SOLUTION)
    #            ]
    # ---------------------------------------------------------------

    children_by_source: dict[
        tuple[str, ...],
        list[tuple[tuple[str, ...], str]],
    ] = defaultdict(list)

    for child_exam, rows in exams.items():
        for row in rows:
            source = normalize(row.get("SourceExam", ""))

            if not source:
                continue

            source_exam = exam_key_from_source(source)

            if source_exam is None:
                continue

            qualifier = source_qualifier(source)

            if qualifier not in REUSE_QUALIFIERS:
                continue

            children_by_source[source_exam].append(
                (
                    child_exam,
                    qualifier,
                )
            )

    # ---------------------------------------------------------------
    # Build the set of related exams.
    #
    # Start from the current exam and walk the SourceExam graph in
    # both directions.
    # ---------------------------------------------------------------

    related: set[tuple[str, ...]] = {current_exam}

    changed = True

    while changed:
        changed = False

        for source_exam, children in children_by_source.items():

            child_exams = {
                child_exam
                for child_exam, _ in children
            }

            # Current connected component touches this source.
            if source_exam in related or related.intersection(
                child_exams
            ):
                new_related = {
                    source_exam,
                    *child_exams,
                }

                before = len(related)

                related.update(new_related)

                if len(related) != before:
                    changed = True

    # ---------------------------------------------------------------
    # Determine which qualifiers actually occur in the relationship.
    # ---------------------------------------------------------------

    qualifiers: set[str] = set()

    for source_exam, children in children_by_source.items():

        if source_exam not in related:
            continue

        for child_exam, qualifier in children:
            if child_exam in related:
                qualifiers.add(qualifier)

    # There is no reuse relationship.
    if not qualifiers:
        return ""

    ordered_qualifiers = [
        qualifier
        for qualifier in REUSE_QUALIFIERS
        if qualifier in qualifiers
    ]

    # ---------------------------------------------------------------
    # Determine the cell state.
    #
    # A cell is checked when the exam has a SourceExam row for the
    # given qualifier which points to an exam in the same connected
    # reuse group.
    #
    # The source exam itself is checked for the qualifier when it
    # actually provides that document.
    # ---------------------------------------------------------------

    checked: set[tuple[tuple[str, ...], str]] = set()

    for child_exam in related:

        rows = exams.get(child_exam, [])

        for row in rows:

            source = normalize(
                row.get("SourceExam", "")
            )

            qualifier = source_qualifier(source)

            if not source or qualifier not in ordered_qualifiers:
                continue

            source_exam = exam_key_from_source(source)

            if source_exam in related:
                checked.add(
                    (
                        child_exam,
                        qualifier,
                    )
                )

    # ---------------------------------------------------------------
    # The source exam itself represents the original document.
    #
    # If a source exam has a document of that qualifier, mark it too.
    # ---------------------------------------------------------------

    for related_exam in related:

        for row in exams.get(related_exam, []):

            qualifier = normalize(
                row.get("Qualifier", "")
            ).upper()

            if qualifier not in ordered_qualifiers:
                continue

            # This row is an actual document for the exam.
            # Only mark the source exam itself when it is the target
            # of a reuse relationship.
            #
            # We check whether some child points to this exam with
            # this qualifier.
            for child_exam, child_qualifier in (
                children_by_source.get(
                    related_exam,
                    [],
                )
            ):
                if (
                    child_exam in related
                    and child_qualifier == qualifier
                ):
                    checked.add(
                        (
                            related_exam,
                            qualifier,
                        )
                    )
                    break

    # ---------------------------------------------------------------
    # Sort rows by normal exam ordering.
    # ---------------------------------------------------------------

    ordered_exams = sorted(
        related,
        key=lambda exam: (
            exam[0],
            exam[1],
            int(exam[2]),
            exam[4],
            exam[3],
        ),
    )

    # ---------------------------------------------------------------
    # Generate table.
    # ---------------------------------------------------------------

    html: list[str] = []

    html.append(
        """
<div class="overflow-x-auto">
<table class="min-w-full text-sm border-collapse">
    <thead>
        <tr class="border-b">
            <th class="text-left px-3 py-2">Exam</th>
"""
    )

    for qualifier in ordered_qualifiers:
        html.append(
            f"""
            <th class="text-center px-3 py-2">
                {escape(qualifier)}
            </th>
"""
        )

    html.append(
        """
        </tr>
    </thead>
    <tbody>
"""
    )

    for related_exam in ordered_exams:

        html.append(
            """
        <tr class="border-b">
            <td class="px-3 py-2 whitespace-nowrap">
"""
        )

        html.append(
            create_exam_link(related_exam)
        )

        html.append(
            """
            </td>
"""
        )

        for qualifier in ordered_qualifiers:

            if (
                related_exam,
                qualifier,
            ) in checked:
                cell = "✅"
            else:
                cell = "❌"

            html.append(
                f"""
            <td class="text-center px-3 py-2">
                {cell}
            </td>
"""
            )

        html.append(
            """
        </tr>
"""
        )

    html.append(
        """
    </tbody>
</table>
</div>
"""
    )

    return "".join(html)


# ---------------------------------------------------------------------------
# Exercise images
# ---------------------------------------------------------------------------

def exercise_index(row: dict) -> int:
    return int(normalize(row["Exercise Index"]))


def alternative_index(row: dict) -> int:
    value = normalize(row["Alternative Index"])

    if not value:
        return 0

    return int(value)


def exercise_title(row: dict) -> str:
    index = exercise_index(row)
    alternative = alternative_index(row)

    if alternative:
        return f"{index} #{alternative}"

    return str(index)


def is_mission_statement(row: dict) -> bool:
    qualifier = normalize(
        row["Qualifier"]
    ).upper()

    return qualifier in {
        "MISSION",
        "MISSION STATEMENT",
        "STATEMENT",
        "MISSION_STATEMENT",
    }


def create_exercise_image(
    row: dict,
    template,
    image_type: str,
) -> str:

    attachment = normalize(
        row["Attachment"]
    )

    if not attachment:
        return ""

    index = exercise_index(row)
    alternative = alternative_index(row)

    if image_type == "mission":
        alt = f"Exercise {index} mission statement"
        border_color = "blue-200"
        bg_color = "blue-100"
    else:
        if alternative:
            alt = f"Exercise {index} solution #{alternative}"
        else:
            alt = f"Exercise {index} solution"

        border_color = "green-200"
        bg_color = "green-100"

    return (
        template
        .replace(
            "{{IMG_SRC}}",
            escape_attribute(
                make_absolute_site_path(
                    attachment
                )
            ),
        )
        .replace(
            "{{IMG_ALT}}",
            escape_attribute(alt),
        )
        .replace(
            "{{BORDER_COLOR}}",
            border_color,
        )
        .replace(
            "{{BG_COLOR}}",
            bg_color,
        )
    )


# ---------------------------------------------------------------------------
# Exercise generation
# ---------------------------------------------------------------------------

def create_exercise(
    rows: list[dict],
    exercise_template: str,
    image_template: str,
) -> str:

    rows = sorted(
        rows,
        key=lambda row: (
            alternative_index(row),
            normalize(
                row["Qualifier"]
            ).upper(),
        ),
    )

    first_row = rows[0]

    content_parts: list[str] = []

    for row in rows:
        if is_mission_statement(row):
            content_parts.append(
                create_exercise_image(
                    row,
                    image_template,
                    "mission",
                )
            )

    for row in rows:
        if row["Qualifier"].upper() == "SOLUTION":
            content_parts.append(
                create_exercise_image(
                    row,
                    image_template,
                    "solution",
                )
            )

    if not content_parts:
        for row in rows:
            content_parts.append(
                create_exercise_image(
                    row,
                    image_template,
                    "solution",
                )
            )

    content = "".join(content_parts)

    return (
        exercise_template
        .replace(
            "{{EXERCISE_TITLE}}",
            escape(
                exercise_title(first_row)
            ),
        )
        .replace(
            "{{CONTENT}}",
            content,
        )
    )


def create_exercises(
    rows: list[dict],
    exercise_template: str,
    image_template: str,
) -> str:

    exercises: OrderedDict[
        int,
        list[dict],
    ] = OrderedDict()

    for row in rows:
        key = exercise_index(row)

        exercises.setdefault(
            key,
            [],
        ).append(row)

    result: list[str] = []

    for _, exercise_rows in sorted(
        exercises.items()
    ):
        result.append(
            create_exercise(
                exercise_rows,
                exercise_template,
                image_template,
            )
        )

    return "".join(result)


# ---------------------------------------------------------------------------
# Exam generation
# ---------------------------------------------------------------------------

def output_directory(
    exam: tuple[str, ...],
) -> Path:

    section, subject, year, subtype, season = exam

    return (
        EXAMS_DB_DIR
        / section
        / subject
        / year
        / f"{season}_{subtype}"
    )


def create_exam(
    exam: tuple[str, ...],
    rows: list[dict],
    all_exams: OrderedDict[tuple[str, ...], list[dict]],
    exam_template: str,
    exercise_template: str,
    image_template: str,
) -> Path:

    section, subject, year, subtype, season = exam

    links = create_exam_links(rows)

    exercises = create_exercises(
        rows,
        exercise_template,
        image_template,
    )

    reuse = create_reuse_table(
        exam,
        all_exams,
    )

    html = (
        exam_template
        .replace(
            "{{LINKS}}",
            links,
        )
        .replace(
            "{{EXERCISES}}",
            exercises,
        )
        .replace(
            "{{REUSE}}",
            reuse,
        )
        .replace(
            "{{SECTION}}",
            rows[0]["Section"],
        )
        .replace(
            "{{SUBJECT}}",
            rows[0]["Subject"],
        )
        .replace(
            "{{YEAR}}",
            rows[0]["Year"],
        )
        .replace(
            "{{SEASON}}",
            rows[0]["Season"],
        )
        .replace(
            "{{SUBTYPE}}",
            rows[0]["Subtype"],
        )
    )

    output_dir = output_directory(exam)
    output_dir.mkdir(
        parents=True,
        exist_ok=True,
    )

    output_file = output_dir / "index.html"

    output_file.write_text(
        html,
        encoding="utf-8",
    )

    return output_file


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

def main() -> None:

    require_file(CSV_FILE)

    exam_template = read_template(
        EXAM_TEMPLATE_FILE
    )

    exercise_template = read_template(
        EXERCISES_TEMPLATE_FILE
    )

    image_template = read_template(
        EXERCISE_IMAGE_TEMPLATE_FILE
    )

    # ---------------------------------------------------------------
    # Load CSV and group rows by exam.
    # ---------------------------------------------------------------

    exams: OrderedDict[
        tuple[str, ...],
        list[dict],
    ] = OrderedDict()

    with CSV_FILE.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as file:

        reader = csv.DictReader(file)

        required_columns = {
            "Section",
            "Subject",
            "Year",
            "Subtype",
            "Season",
            "Source",
            "Exercise Index",
            "Qualifier",
            "Alternative Index",
            "Attachment",
            "SourceExam",
        }

        missing_columns = (
            required_columns
            - set(reader.fieldnames or [])
        )

        if missing_columns:
            raise ValueError(
                "CSV is missing columns: "
                + ", ".join(
                    sorted(missing_columns)
                )
            )

        for row in reader:
            key = exam_key(row)

            exams.setdefault(
                key,
                [],
            ).append(row)

    # ---------------------------------------------------------------
    # Generate one index.html per exam.
    # ---------------------------------------------------------------

    generated = 0

    for exam, rows in exams.items():

        output_file = create_exam(
            exam,
            rows,
            exams,
            exam_template,
            exercise_template,
            image_template,
        )

        generated += 1

        print(
            f"Generated: {output_file}"
        )

    print()
    print(
        f"Exams found: {len(exams)}"
    )
    print(
        f"Files generated: {generated}"
    )


if __name__ == "__main__":
    main()
