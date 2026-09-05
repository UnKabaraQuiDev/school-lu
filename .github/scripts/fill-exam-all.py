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
EXAMS_DB_ALL_DIR = EXAMS_DB_DIR / "all"

TEMPLATE_FILE = EXAMS_DB_ALL_DIR / "index.template.html"
CSV_FILE = DATA_DIR / "exams" / "db.csv"
OUTPUT_FILE = EXAMS_DB_ALL_DIR / "index.html"


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
<a href="/data/{escape(url, quote=True)}"
   target="_blank"
   rel="noopener"
   data-i18n="{escape(translation_key.lower())}"
   class="inline-block px-3 py-1.5 rounded-md bg-{color}-100 text-{color}-700 hover:bg-{color}-200 transition">
</a>
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

        Section + Subject + Year + Season + Subtype

    Each attachment is stored using its Qualifier.
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


def create_table(csv_file):
    """Load CSV data and turn it into an HTML table."""

    csv_rows = []

    with csv_file.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as file:
        reader = csv.DictReader(file)

        for row in reader:
            csv_rows.append(row)

    exams = group_rows_by_exam(csv_rows)

    rows = []

    for exam in sorted(
        exams.values(),
        key=lambda item: (
            item["Section"],
            item["Subject"],
            item["Year"],
            item["Season"],
            item["Subtype"],
            item["Name"],
        ),
    ):
        section = exam["Section"]
        subject = exam["Subject"]
        year = exam["Year"]
        season = exam["Season"]
        subtype = exam["Subtype"]
        name = exam["Name"]
        attachments = exam["attachments"]

        exercises_url = (
            f'{escape(section, quote=True)}/'
            f'{escape(subject, quote=True)}/'
            f'{escape(year, quote=True)}/'
            f'{escape(season, quote=True)}_'
            f'{escape(subtype, quote=True)}'
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
                f'<a class="underline" '
                f'href="../{exercises_url}/">'
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

        rows.append(
            f"""
                <tr class="border-b hover:bg-gray-50">

                    <td class="px-4 py-3">
                        <a
                            class="underline"
                            href="../{escape(section, quote=True)}/"
                        >
                            {escape(section)}
                        </a>
                    </td>

                    <td class="px-4 py-3">
                        <a
                            class="underline"
                            href="../{escape(section, quote=True)}/{escape(subject, quote=True)}/"
                        >
                            {escape(subject)}
                        </a>
                    </td>

                    <td class="px-4 py-3">
                        <a
                            class="underline"
                            href="../{escape(section, quote=True)}/{escape(subject, quote=True)}/{escape(year, quote=True)}/"
                        >
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

                    <th class="px-4 py-3">Section</th>
                    <th class="px-4 py-3">Subject</th>
                    <th class="px-4 py-3">Date</th>
                    <th class="px-4 py-3">Season</th>
                    <th class="px-4 py-3">Subtype</th>
                    <th class="px-4 py-3">Name</th>
                    <th class="px-4 py-3">Attachments</th>

                </tr>
            </thead>

            <tbody>
                {"".join(rows)}
            </tbody>

        </table>

    </div>

</div>
"""


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

    # Generate table.
    table = create_table(CSV_FILE)

    # Insert table into template.
    if "{{TABLE}}" not in template:
        raise ValueError(
            "Template does not contain {{TABLE}}"
        )

    html = template.replace(
        "{{TABLE}}",
        table,
    )

    # Make sure the output directory exists.
    EXAMS_DB_ALL_DIR.mkdir(
        parents=True,
        exist_ok=True,
    )

    # Write final HTML.
    OUTPUT_FILE.write_text(
        html,
        encoding="utf-8",
    )

    print(f"Generated: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
