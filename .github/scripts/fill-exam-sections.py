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

TEMPLATE_FILE = EXAMS_DB_DIR / "section.template.html"
CSV_FILE = DATA_DIR / "exams/db.csv"


def create_subject_button(section, subject):
    """Create a button linking to a subject."""

    subject_url = escape(subject, quote=True)

    return f"""
<a
    href="{subject_url}/"
    class="inline-block px-4 py-2 rounded-lg bg-blue-100 text-blue-700
           hover:bg-blue-200 transition"
    data-i18n="subject.view"
>
    View
</a>
"""


def get_subjects_by_section(csv_file):
    """Return all unique subjects grouped by section."""

    subjects_by_section = {}

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

            if section not in subjects_by_section:
                subjects_by_section[section] = set()

            subjects_by_section[section].add(subject)

    return subjects_by_section


def create_table(section, subjects):
    """Create a table containing all subjects for a section."""

    sorted_subjects = sorted(
        subjects,
        key=str.casefold,
    )

    rows = []

    for subject in sorted_subjects:
        rows.append(
            f"""
                <tr class="border-b last:border-b-0 hover:bg-gray-50">

                    <td class="px-4 py-3 font-medium">
                        {escape(subject)}
                    </td>

                    <td class="px-4 py-3">
                        {create_subject_button(section, subject)}
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
                        data-i18n="subject.code"
                    >
                        Subject
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


def generate_section_page(template, section, subjects):
    """Generate the index page for one section."""

    table = create_table(
        section,
        subjects,
    )

    html = template.replace(
        "{{TABLE}}",
        table,
    )
    
    html = html.replace("{{SECTION}}", section)

    section_directory = EXAMS_DB_DIR / section
    output_file = section_directory / "index.html"

    section_directory.mkdir(
        parents=True,
        exist_ok=True,
    )

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

    # Get all subjects grouped by section.
    subjects_by_section = get_subjects_by_section(
        CSV_FILE
    )

    # Generate one page for every section.
    for section in sorted(
        subjects_by_section,
        key=str.casefold,
    ):
        generate_section_page(
            template,
            section,
            subjects_by_section[section],
        )


if __name__ == "__main__":
    main()