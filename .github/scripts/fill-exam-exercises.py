import csv
import re
import subprocess
from collections import OrderedDict
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

        /exams/CA/CA_MATHE/file.pdf

    Existing leading slashes are preserved.
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
    return tuple(normalize(row[field]) for field in EXAM_FIELDS)

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

    The button type is determined by the CSV's Qualifier column,
    not by the Source filename.
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
        qualifier = normalize(row.get("Qualifier", "")).upper()

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
    """
    Generate the value used for {EXERCISE_TITLE}.

    The template already contains the translated "Exercise" label,
    so this only returns the exercise number.
    """
    index = exercise_index(row)

    alternative = alternative_index(row)

    if alternative:
        return f"{index} #{alternative}"

    return str(index)


def is_mission_statement(row: dict) -> bool:
    qualifier = normalize(row["Qualifier"]).upper()

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
    """
    Fill either exercise-mission_statement.template.html or
    exercise-solution.template.html.
    """
    attachment = normalize(row["Attachment"])

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
            escape_attribute(make_absolute_site_path(attachment)),
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
    """
    Generate one exercise.

    The CONTENT placeholder receives:

        mission statement
        solution
        solution
        solution
        ...

    depending on which rows exist for the exercise.
    """
    rows = sorted(
        rows,
        key=lambda row: (
            alternative_index(row),
            normalize(row["Qualifier"]).upper(),
        ),
    )

    first_row = rows[0]

    content_parts: list[str] = []

    # Mission statement first.
    for row in rows:
        if is_mission_statement(row):
            content_parts.append(
                create_exercise_image(
                    row,
                    image_template,
                    "mission",
                )
            )

    # Then all solutions.
    for row in rows:
        if row["Qualifier"].upper() == "SOLUTION":
            content_parts.append(
                create_exercise_image(
                    row,
                    image_template,
                    "solution",
                )
            )

    # Fallback: if an unknown qualifier was used, still include the image
    # rather than silently dropping it.
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
            escape(exercise_title(first_row)),
        )
        .replace(
            "{{CONTENT}}",
            content,
        )
    )


def create_exercises(
    rows: list[dict],
    exercise_template: str,
    image_template: str
) -> str:
    """
    Group rows by exercise index + alternative index and generate
    one exercises.template.html block for every exercise.
    """
    exercises: OrderedDict[tuple[int, int], list[dict]] = OrderedDict()

    for row in rows:
        key = (
            exercise_index(row)
        )

        exercises.setdefault(key, []).append(row)

    result: list[str] = []

    for _, exercise_rows in sorted(exercises.items()):
        result.append(
            create_exercise(
                exercise_rows,
                exercise_template,
                image_template
            )
        )

    return "".join(result)


# ---------------------------------------------------------------------------
# Exam generation
# ---------------------------------------------------------------------------

def output_directory(exam: tuple[str, ...]) -> Path:
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
        .replace("{{SECTION}}", rows[0]["Section"])
        .replace("{{SUBJECT}}", rows[0]["Subject"])
        .replace("{{YEAR}}", rows[0]["Year"])
        .replace("{{SEASON}}", rows[0]["Season"])
        .replace("{{SUBTYPE}}", rows[0]["Subtype"])
    )

    output_dir = output_directory(exam)
    output_dir.mkdir(parents=True, exist_ok=True)

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

    exam_template = read_template(EXAM_TEMPLATE_FILE)
    exercise_template = read_template(EXERCISES_TEMPLATE_FILE)
    image_template = read_template(EXERCISE_IMAGE_TEMPLATE_FILE)

    # ---------------------------------------------------------------
    # Load CSV and group rows by exam.
    # ---------------------------------------------------------------

    exams: OrderedDict[tuple[str, ...], list[dict]] = OrderedDict()

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
        }

        missing_columns = required_columns - set(reader.fieldnames or [])

        if missing_columns:
            raise ValueError(
                "CSV is missing columns: "
                + ", ".join(sorted(missing_columns))
            )

        for row in reader:
            key = exam_key(row)
            exams.setdefault(key, []).append(row)

    # ---------------------------------------------------------------
    # Generate one index.html per exam.
    # ---------------------------------------------------------------

    generated = 0

    for exam, rows in exams.items():
        output_file = create_exam(
            exam,
            rows,
            exam_template,
            exercise_template,
            image_template,
        )

        generated += 1
        print(f"Generated: {output_file}")

    print()
    print(f"Exams found: {len(exams)}")
    print(f"Files generated: {generated}")


if __name__ == "__main__":
    main()