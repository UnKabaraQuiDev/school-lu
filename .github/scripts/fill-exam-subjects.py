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
CSV_FILE = DATA_DIR / "exams/db.csv"


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


def create_table(rows):
    """Create an HTML table from the rows belonging to one subject."""

    table_rows = []

    for row in rows:
        exercises_url = (
            f'{escape(row["Year"], quote=True)}/'
            f'{escape(row["Season"], quote=True)}_'
            f'{escape(row["Subtype"], quote=True)}'
        )

        table_rows.append(
            f"""
                <tr class="border-b hover:bg-gray-50">

                    <td class="px-4 py-3">
                        {escape(row["Year"])}
                    </td>

                    <td class="px-4 py-3">
                        {escape(row["Season"])}
                    </td>

                    <td class="px-4 py-3">
                        {escape(row["Subtype"])}
                    </td>

                    <td class="px-4 py-3 font-medium">
                        {escape(row["Name"])}
                    </td>

                    <td class="px-4 py-3">
                        <a
                            href="{exercises_url}"
                            data-i18n="view-exercises"
                            class="inline-block px-3 py-1.5 rounded-md bg-blue-100 text-blue-700 hover:bg-blue-200 transition"
                        ></a>
                    </td>

                    <td class="px-4 py-3">
                        <div class="flex flex-wrap gap-2">

                            {conditional_button(
                                row["Mission statement"],
                                "Mission",
                                "blue"
                            )}

                            {conditional_button(
                                row["Solution"],
                                "Solution",
                                "green"
                            )}

                            {conditional_button(
                                row["Data"],
                                "Data",
                                "purple"
                            )}

                            {conditional_button(
                                row["Oral"],
                                "Oral",
                                "cyan"
                            )}

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
                    <th class="px-4 py-3">View exercises</th>
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
    
    html = html.replace("{{SECTION}}", section)
    html = html.replace("{{SUBJECT}}", subject)

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

    # Group all exam rows by section and subject.
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