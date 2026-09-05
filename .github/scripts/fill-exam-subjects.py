import csv
from pathlib import Path
from html import escape
import subprocess


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

DATA_DIR = ROOT_DIR / "data"
EXAMS_DB_DIR = ROOT_DIR / "exam-db"

TEMPLATE_FILE = EXAMS_DB_DIR / "subject.template.html"
CSV_FILE = DATA_DIR / "exams" / "db.csv"


QUALIFIER_BUTTONS = {
    "STATEMENT": ("Mission", "blue"),
    "SOLUTION": ("Solution", "green"),
    "DATA": ("Data", "purple"),
    "ORAL": ("Oral", "cyan"),
}


def create_button(url, translation_key, color):
    if not url:
        return ""

    return f"""
<a
    href="/data/{escape(url, quote=True)}"
    target="_blank"
    rel="noopener"
    data-i18n="{escape(translation_key.lower())}"
    class="inline-block px-3 py-1.5 rounded-md bg-{color}-100 text-{color}-700 hover:bg-{color}-200 transition"
></a>
"""


def conditional_button(url, text, color):
    if not url:
        return ""

    return create_button(url, text, color)


def group_rows_by_exam(rows):
    """
    Group attachment rows belonging to the same exam.

    db.csv now contains one row per attachment:

        Section,Subject,Year,Season,Subtype,Name,Qualifier,Attachement,Source

    The exam identity is:

        Section + Subject + Year + Season + Subtype

    Returns:
        {
            (section, subject, year, season, subtype): {
                "Section": ...,
                "Subject": ...,
                "Year": ...,
                "Season": ...,
                "Subtype": ...,
                "Name": ...,
                "attachments": {
                    "STATEMENT": "path/to/file.pdf",
                    "SOLUTION": "path/to/file.pdf",
                    "DATA": "path/to/file.pdf",
                    "ORAL": "path/to/file.pdf",
                }
            }
        }
    """

    exams = {}

    for row in rows:
        section = row.get("Section", "").strip()
        subject = row.get("Subject", "").strip()
        year = row.get("Year", "").strip()
        season = row.get("Season", "").strip()
        subtype = row.get("Subtype", "").strip()

        if not section or not subject or not year:
            continue

        key = (
            section,
            subject,
            year,
            season,
            subtype,
        )

        if key not in exams:
            exams[key] = {
                "Section": section,
                "Subject": subject,
                "Year": year,
                "Season": season,
                "Subtype": subtype,
                "Name": row.get("Name", "").strip(),
                "attachments": {},
            }

        qualifier = row.get("Qualifier", "").strip().upper()
        attachment = row.get("Attachement", "").strip()

        if qualifier and attachment:
            exams[key]["attachments"][qualifier] = attachment

    return exams


def create_table(rows):
    """Create an HTML table from the rows belonging to one subject."""

    exams = group_rows_by_exam(rows)

    table_rows = []

    for exam in sorted(
        exams.values(),
        key=lambda item: (
            item["Year"],
            item["Season"],
            item["Subtype"],
            item["Name"],
        ),
    ):
        year = exam["Year"]
        season = exam["Season"]
        subtype = exam["Subtype"]
        name = exam["Name"]
        attachments = exam["attachments"]

        exercises_url = (
            f'{escape(year, quote=True)}/'
            f'{escape(season, quote=True)}_'
            f'{escape(subtype, quote=True)}'
        )

        exercises_path = (
            EXAMS_DB_DIR
            / exam["Section"]
            / exam["Subject"]
            / year
            / f"{season}_{subtype}"
            / "index.html"
        )

        include_exercises = exercises_path.exists()

        if include_exercises:
            subtype_html = (
                f'<a class="underline" href="{exercises_url}">'
                f'{escape(subtype)}'
                f'</a>'
            )
        else:
            subtype_html = escape(subtype)

        buttons = []

        for qualifier, (translation_key, color) in QUALIFIER_BUTTONS.items():
            attachment = attachments.get(qualifier, "")

            if attachment:
                buttons.append(
                    conditional_button(
                        attachment,
                        translation_key,
                        color,
                    )
                )

        table_rows.append(
            f"""
                <tr class="border-b hover:bg-gray-50">

                    <td class="px-4 py-3">
                        <a class="underline" href="{escape(year, quote=True)}/">
                            {escape(year)}
                        </a>
                    </td>

                    <td class="px-4 py-3">
                        {escape(season)}
                    </td>

                    <td class="px-4 py-3">
                        {subtype_html}
                    </td>

                    <td class="px-4 py-3 font-medium">
                        {escape(name)}
                    </td>

                    <td class="px-4 py-3">
                        <div class="flex flex-wrap gap-2">
                            {"".join(buttons)}
                        </div>
                    </td>

                </tr>
            """
        )

    return f"""
<div class="bg-white rounded-xl border shadow-sm overflow-hidden">

    <div class="overflow-x-auto">

        <table class="w-full text-left">

            <thead>
                <tr class="border-b bg-gray-50">

                    <th class="px-4 py-3">Year</th>
                    <th class="px-4 py-3">Season</th>
                    <th class="px-4 py-3">Subtype</th>
                    <th class="px-4 py-3">Name</th>
                    <th class="px-4 py-3">Attachments</th>

                </tr>
            </thead>

            <tbody>
                {"".join(table_rows)}
            </tbody>

        </table>

    </div>

</div>
"""


def get_rows_by_subject(csv_file):
    """
    Load CSV data and group rows by section and subject.

    db.csv contains one row per attachment, so the rows are later
    regrouped by exam inside create_table().

    Returns:
        {
            section: {
                subject: [rows]
            }
        }
    """

    rows_by_subject = {}

    with csv_file.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as file:

        reader = csv.DictReader(file)

        for row in reader:
            section = row.get("Section", "").strip()
            subject = row.get("Subject", "").strip()

            if not section or not subject:
                continue

            if section not in rows_by_subject:
                rows_by_subject[section] = {}

            if subject not in rows_by_subject[section]:
                rows_by_subject[section][subject] = []

            rows_by_subject[section][subject].append(row)

    return rows_by_subject


def generate_subject_page(
    template,
    section,
    subject,
    rows,
):
    """Generate one page for a section and subject."""

    table = create_table(rows)

    html = template.replace(
        "{{TABLE}}",
        table,
    )

    html = html.replace(
        "{{SECTION}}",
        escape(section),
    )

    html = html.replace(
        "{{SUBJECT}}",
        escape(subject),
    )

    subject_directory = (
        EXAMS_DB_DIR
        / section
        / subject
    )

    subject_directory.mkdir(
        parents=True,
        exist_ok=True,
    )

    output_file = subject_directory / "index.html"

    output_file.write_text(
        html,
        encoding="utf-8",
    )

    print(f"Generated: {output_file}")


def main():

    # Check that the required files exist.
    if not TEMPLATE_FILE.exists():
        raise FileNotFoundError(
            f"Template not found: {TEMPLATE_FILE}"
        )

    if not CSV_FILE.exists():
        raise FileNotFoundError(
            f"CSV file not found: {CSV_FILE}"
        )

    # Load template.
    template = TEMPLATE_FILE.read_text(
        encoding="utf-8"
    )

    if "{{TABLE}}" not in template:
        raise ValueError(
            "Template does not contain {{TABLE}}"
        )

    # Group all attachment rows by section and subject.
    rows_by_subject = get_rows_by_subject(
        CSV_FILE
    )

    # Generate one page for every section/subject.
    for section in sorted(
        rows_by_subject,
        key=str.casefold,
    ):

        subjects = rows_by_subject[section]

        for subject in sorted(
            subjects,
            key=str.casefold,
        ):

            generate_subject_page(
                template,
                section,
                subject,
                subjects[subject],
            )


if __name__ == "__main__":
    main()
