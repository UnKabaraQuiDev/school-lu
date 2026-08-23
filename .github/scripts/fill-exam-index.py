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

TEMPLATE_FILE = EXAMS_DB_DIR / "index.template.html"
CSV_FILE = DATA_DIR / "exams/db.csv"
OUTPUT_FILE = EXAMS_DB_DIR / "index.html"


def create_section_button(section):
    """Create a button linking to a section."""

    section_url = escape(section, quote=True)

    return f"""
<a
    href="{section_url}/"
    class="inline-block px-4 py-2 rounded-lg bg-blue-100 text-blue-700
           hover:bg-blue-200 transition"
    data-i18n="section.view"
>
    View
</a>
"""


def create_table(csv_file):
    """Load CSV data and create a table containing all unique sections."""

    sections = set()

    with csv_file.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as file:
        reader = csv.DictReader(file)

        for row in reader:
            section = row.get("Section", "").strip()

            if section:
                sections.add(section)

    sorted_sections = sorted(
        sections,
        key=str.casefold,
    )

    rows = []

    for section in sorted_sections:
        rows.append(
            f"""
                <tr class="border-b last:border-b-0 hover:bg-gray-50">

                    <td class="px-4 py-3 font-medium">
                        {escape(section)}
                    </td>

                    <td class="px-4 py-3">
                        {create_section_button(section)}
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

                    <th
                        class="px-4 py-3"
                        data-i18n="section.code"
                    >
                        Section
                    </th>

                    <th
                        class="px-4 py-3"
                        data-i18n="action"
                    >
                        Action
                    </th>

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

    # Write final HTML.
    OUTPUT_FILE.write_text(
        html,
        encoding="utf-8",
    )

    print(f"Generated: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()