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

TEMPLATE_FILE = EXAMS_DB_DIR / "year.template.html"
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
    Group db.csv rows belonging to the same exam.

    db.csv now contains one row per attachment:

        Section,Subject,Year,Season,Subtype,Name,Qualifier,Attachement,Source

    The exam identity is:

        Season + Subtype + Name

    Each attachment is stored using its Qualifier.
    """

    exams = {}

    for row in rows:
        season = row.get("Season", "").strip()
        subtype = row.get("Subtype", "").strip()
        name = row.get("Name", "").strip()

        key = (
            season,
            subtype,
            name,
        )

        if key not in exams:
            exams[key] = {
                "Section": row.get("Section", "").strip(),
                "Subject": row.get("Subject", "").strip(),
                "Year": row.get("Year", "").strip(),
                "Season": season,
                "Subtype": subtype,
                "Name": name,
                "attachments": {},
            }

        qualifier = row.get("Qualifier", "").strip().upper()
        attachment = row.get("Attachement", "").strip()

        if qualifier and attachment:
            exams[key]["attachments"][qualifier] = attachment

    return exams


def create_table(rows):
    """Create an HTML table from the exams belonging to one year."""

    exams = group_rows_by_exam(rows)

    table_rows = []

    for exam in sorted(
        exams.values(),
        key=lambda item: (
            item["Season"].casefold(),
            item["Subtype"].casefold(),
            item["Name"].casefold(),
        ),
    ):
        season = exam["Season"]
        subtype = exam["Subtype"]
        name = exam["Name"]
        section = exam["Section"]
        subject = exam["Subject"]
        year = exam["Year"]
        attachments = exam["attachments"]

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

        exercises_url = (
            f"{escape(season, quote=True)}_"
            f"{escape(subtype, quote=True)}/"
        )

        exercises_path = (
            EXAMS_DB_DIR
            / section
            / subject
            / year
            / f"{season}_{subtype}"
            / "index.html"
        )

        include_exercises = exercises_path.exists()

        if include_exercises:
            subtype_html = (
                f'<a class="underline" href="{exercises_url}/">'
                f'{escape(subtype)}'
                f'</a>'
            )
        else:
            subtype_html = escape(subtype)

        table_rows.append(
            f"""
                <tr class="border-b hover:bg-gray-50">

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


def get_rows_by_year(csv_file):
    """
    Load CSV data and group rows by section, subject and year.

    Returns:
        {
            section: {
                subject: {
                    year: [rows]
                }
            }
        }
    """

    rows_by_year = {}

    with csv_file.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as file:

        reader = csv.DictReader(file)

        for row in reader:
            section = row.get("Section", "").strip()
            subject = row.get("Subject", "").strip()
            year = row.get("Year", "").strip()

            if not section or not subject or not year:
                continue

            if section not in rows_by_year:
                rows_by_year[section] = {}

            if subject not in rows_by_year[section]:
                rows_by_year[section][subject] = {}

            if year not in rows_by_year[section][subject]:
                rows_by_year[section][subject][year] = []

            rows_by_year[section][subject][year].append(row)

    return rows_by_year


def generate_year_page(
    template,
    section,
    subject,
    year,
    rows,
):
    """Generate one page for a section, subject and year."""

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

    html = html.replace(
        "{{YEAR}}",
        escape(year),
    )

    year_directory = (
        EXAMS_DB_DIR
        / section
        / subject
        / year
    )

    year_directory.mkdir(
        parents=True,
        exist_ok=True,
    )

    output_file = year_directory / "index.html"

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

    # Group all attachment rows by section, subject and year.
    rows_by_year = get_rows_by_year(
        CSV_FILE
    )

    # Generate one page for every section/subject/year.
    for section in sorted(
        rows_by_year,
        key=str.casefold,
    ):

        subjects = rows_by_year[section]

        for subject in sorted(
            subjects,
            key=str.casefold,
        ):

            years = subjects[subject]

            for year in sorted(
                years,
                key=str.casefold,
            ):

                generate_year_page(
                    template,
                    section,
                    subject,
                    year,
                    years[year],
                )


if __name__ == "__main__":
    main()
