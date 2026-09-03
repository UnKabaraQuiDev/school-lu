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
    """Create an HTML table from the exams belonging to one year."""

    table_rows = []

    # An exam can have multiple CSV rows because it can have
    # multiple exercises/attachments. Group those rows first.
    exams = {}

    for row in rows:
        season = row["Season"].strip()
        subtype = row["Subtype"].strip()
        name = row["Name"].strip()

        key = (season, subtype, name)

        if key not in exams:
            exams[key] = []

        exams[key].append(row)

    for (season, subtype, name), exam_rows in sorted(
        exams.items(),
        key=lambda item: (
            item[0][0].casefold(),
            item[0][1].casefold(),
            item[0][2].casefold(),
        ),
    ):
        # The exercises page is inside:
        #
        # exam-db/<section>/<subject>/<year>/<season>_<subtype>/
        #
        # so from year/index.html the link is simply:
        #
        # <season>_<subtype>/
        exercises_url = (
            f"{escape(season, quote=True)}_"
            f"{escape(subtype, quote=True)}/"
        )

        attachments = []

        seen_attachments = set()

        for row in exam_rows:
            attachment_data = (
                row["Mission statement"],
                "Mission",
                "blue",
            )

            for url, translation_key, color in [
                (
                    row["Mission statement"],
                    "Mission",
                    "blue",
                ),
                (
                    row["Solution"],
                    "Solution",
                    "green",
                ),
                (
                    row["Data"],
                    "Data",
                    "purple",
                ),
                (
                    row["Oral"],
                    "Oral",
                    "cyan",
                ),
            ]:
                url = url.strip()

                if not url:
                    continue

                key = (url, translation_key)

                if key in seen_attachments:
                    continue

                seen_attachments.add(key)

                attachments.append(
                    create_button(
                        url,
                        translation_key,
                        color,
                    )
                )

        table_rows.append(
            f"""
                <tr class="border-b hover:bg-gray-50">

                    <td class="px-4 py-3">
                        {escape(season)}
                    </td>

                    <td class="px-4 py-3">
                        {escape(subtype)}
                    </td>

                    <td class="px-4 py-3 font-medium">
                        {escape(name)}
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
                            {"".join(attachments)}
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

    # Group all exam rows by section, subject and year.
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