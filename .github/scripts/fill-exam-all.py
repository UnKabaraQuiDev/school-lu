import csv
import os
from pathlib import Path
from html import escape
import subprocess

ROOT_DIR = Path(
    subprocess.check_output(
        ["git", "-C", str(Path(__file__).resolve().parent), "rev-parse", "--show-toplevel"],
        text=True
    ).strip()
)
DATA_DIR = ROOT_DIR / "data"
EXAMS_DB_DIR = ROOT_DIR / "exam-db"
EXAMS_DB_ALL_DIR = EXAMS_DB_DIR / "all"

TEMPLATE_FILE = EXAMS_DB_ALL_DIR / "index.template.html"
CSV_FILE = DATA_DIR / "exams/db.csv"
OUTPUT_FILE = EXAMS_DB_ALL_DIR / "index.html"

def create_button(url, translation_key, color):
    if not url:
        return ""

    return f"""
<a href="/data/{escape(url)}"
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

def create_table(csv_file):
    """Load CSV data and turn it into an HTML table."""

    rows = []

    with csv_file.open("r", encoding="utf-8", newline="") as file:
        reader = csv.DictReader(file)

        for row in reader:
            exercises_url = (
                f'{escape(row["Section"], quote=True)}/'
                f'{escape(row["Subject"], quote=True)}/'
                f'{escape(row["Year"], quote=True)}/'
                f'{escape(row["Season"], quote=True)}_'
                f'{escape(row["Subtype"], quote=True)}'
            )

            exercises_path = (
                EXAMS_DB_DIR
                / exercises_url
                / "index.html"
            )

            include_exercises = exercises_path.exists()
            
            rows.append(f"""
                <tr class="border-b hover:bg-gray-50">

                    <td class="px-4 py-3">
                        <a class="underline" href="../{row["Section"]}">{escape(row["Section"])}</a>
                    </td>

                    <td class="px-4 py-3">
                        <a class="underline" href="../{row["Section"]}/{row["Subject"]}">{escape(row["Subject"])}</a>
                    </td>

                    <td class="px-4 py-3">
                        <a class="underline" href="../{row["Section"]}/{row["Subject"]}/{row["Year"]}">{escape(row["Year"])}</a>
                    </td>

                    <td class="px-4 py-3">
                        {escape(row["Season"])}
                    </td>

                    <td class="px-4 py-3">
                        {f'<a class="underline" href="../{exercises_url}">' if include_exercises else ''}{escape(row["Subtype"])}{'</a>' if include_exercises else ''}
                    </td>

                    <td class="px-4 py-3 font-medium">
                        {escape(row["Name"])}
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
            """)

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
    # Check that the required files exist
    if not TEMPLATE_FILE.exists():
        raise FileNotFoundError(
            f"Template not found: {TEMPLATE_FILE}"
        )

    if not CSV_FILE.exists():
        raise FileNotFoundError(
            f"CSV file not found: {CSV_FILE}"
        )

    # Load template
    template = TEMPLATE_FILE.read_text(encoding="utf-8")

    # Generate table
    table = create_table(CSV_FILE)

    # Insert table into template
    if "{{TABLE}}" not in template:
        raise ValueError(
            "Template does not contain {{TABLE}}"
        )

    html = template.replace("{{TABLE}}", table)

    # Write final HTML
    OUTPUT_FILE.write_text(html, encoding="utf-8")

    print(f"Generated: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()