#!/usr/bin/env python3

from pathlib import Path
import subprocess
import csv
import re
from collections import defaultdict


# ============================================================
# Configuration
# ============================================================

ROOT_DIR = Path(
    subprocess.check_output(
        ["git", "-C", str(Path(__file__).resolve().parent), "rev-parse", "--show-toplevel"],
        text=True
    ).strip()
)

EXAM_DIR = ROOT_DIR / "exams"
OUTPUT_FILE = EXAM_DIR / "db.csv"

CSV_HEADER = [
    "Section",
    "Subject",
    "Year",
    "Season",
    "Retry",
    "Name",
    "Mission statement",
    "Solution",
    "Data",
    "Oral"
]


# ============================================================
# Regex
# ============================================================

# Example:
# CB_INFOR_2006_ETE_PARTIE_PRATIQUE_CORRIGE.pdf
#
# CB_4LANG_CHINO_2023_ETE_ENONCE.pdf
#
# CA_LLCO_MALA_PSYA_ALLEM_2005_ETE_ENONCE.pdf
#
# The subject is taken as the token immediately before the year.

FILENAME_PATTERN = re.compile(
    r"""
    ^
    (?P<prefix>.+?)
    _
    (?P<year>\d{4})
    _
    (?P<season>[A-Z]+)
    (?P<retry>_REP)?
    (?:
        _
        (?P<name>.+?)
    )?
    _
    (?P<type>DATA|CORRIGE|ENONCE|ORAL)
    \.(?P<extension>pdf|zip)
    $
    """,
    re.IGNORECASE | re.VERBOSE,
)


# ============================================================
# Helpers
# ============================================================

def print_ambiguity(path, reason):
    """Print an ambiguous or invalid file."""

    print(f"[SKIP] {path.relative_to(ROOT_DIR)}")
    print(f"       {reason}")


def parse_filename(path):
    """
    Parse an exam filename.

    Returns:
        dict or None
    """

    match = FILENAME_PATTERN.match(path.name)

    if not match:
        print_ambiguity(path, "Filename does not match the expected pattern.")
        return None

    prefix = match.group("prefix")
    year = match.group("year")
    season = match.group("season").upper()
    retry = "Yes" if match.group("retry") else "No"
    name = match.group("name") or ""
    file_type = match.group("type").upper()

    prefix_parts = prefix.split("_")

    # We need at least:
    #
    # SECTION_SUBJECT
    #
    # Examples:
    # CB_INFOR
    # CB_4LANG_CHINO
    # CA_LLCO_MALA_PSYA_ALLEM

    if len(prefix_parts) < 2:
        print_ambiguity(
            path,
            f"Could not determine Section and Subject from '{prefix}'."
        )
        return None

    section = prefix_parts[0]

    # The subject is the token immediately before the year.
    #
    # This handles:
    #
    # CB_INFOR
    #              -> INFOR
    #
    # CB_4LANG_CHINO
    #              -> CHINO
    #
    # CA_LLCO_MALA_PSYA_ALLEM
    #              -> ALLEM

    subject = "_".join(prefix_parts[1:])

    if not section or not subject:
        print_ambiguity(
            path,
            "Section or Subject is empty."
        )
        return None

    # Name may contain underscores, which we preserve.
    name = name.upper()

    # Only allow the expected file types.
    if file_type not in {"ENONCE", "CORRIGE", "ORAL", "DATA"}:
        print_ambiguity(
            path,
            f"Unknown file type: {file_type}"
        )
        return None

    return {
        "Section": section.upper(),
        "Subject": subject.upper(),
        "Year": year,
        "Season": season,
        "Retry": retry,
        "Name": name,
        "Type": file_type,
        "Path": path.relative_to(ROOT_DIR).as_posix(),
    }


def get_directory_candidates(path):
    """
    Try to get Section and Subject from the directory structure.

    Example:

        exams/
            CB/
                CB_INFOR/
                    file.pdf

    gives:

        Section = CB
        Subject = INFOR

    This is only used as a fallback.
    """

    try:
        relative = path.relative_to(EXAM_DIR)
    except ValueError:
        return None

    parts = relative.parts

    # Expected:
    #
    # Section/
    #     Subject/
    #         file.pdf

    if len(parts) < 3:
        return None

    section = parts[0]
    subject_dir = parts[1]

    # Remove the section prefix from the directory name.
    #
    # CB_INFOR -> INFOR
    # CA_ALLEM -> ALLEM
    #
    # But for directories such as:
    # CB_CHINO_4LANG
    #
    # we cannot safely assume the subject from the directory.

    prefix = section + "_"

    if subject_dir.startswith(prefix):
        possible_subject = subject_dir[len(prefix):]

        if "_" not in possible_subject:
            return {
                "Section": section.upper(),
                "Subject": possible_subject.upper(),
            }

    return None


def parse_exam(path):
    """Parse an exam PDF and return its metadata."""

    result = parse_filename(path)

    if result is not None:
        return result

    # Filename parsing failed.
    # Try the directory structure.

    return None


# ============================================================
# Main processing
# ============================================================

def collect_exams():
    """
    Scan all PDFs and group ENONCE/CORRIGE files
    belonging to the same exam.
    """

    exams = defaultdict(dict)

    pdf_files = sorted(
        list(EXAM_DIR.rglob("*.pdf"))
        + list(EXAM_DIR.rglob("*.zip"))
    )

    print(f"Found {len(pdf_files)} files.")

    for path in pdf_files:

        parsed = parse_exam(path)

        if parsed is None:
            continue

        key = (
            parsed["Section"],
            parsed["Subject"],
            parsed["Year"],
            parsed["Season"],
            parsed["Retry"],
            parsed["Name"],
        )

        file_type = parsed["Type"]

        # Detect duplicate ENONCE/CORRIGE/ORAL/DATA files.
        if file_type in exams[key]:
            print_ambiguity(
                path,
                f"Duplicate {file_type} for exam {key}."
            )
            continue

        exams[key][file_type] = parsed["Path"]

    return exams


def create_csv(exams):
    """Write the grouped exams to db.csv."""

    rows = []

    for key in sorted(exams):

        section, subject, year, season, retry, name = key

        files = exams[key]

        mission_statement = files.get("ENONCE", "")
        solution = files.get("CORRIGE", "")
        data = files.get("DATA", "")
        oral = files.get("ORAL", "")

        rows.append([
            section,
            subject,
            year,
            season,
            retry,
            name,
            mission_statement,
            solution,
            data,
            oral
        ])

    with OUTPUT_FILE.open(
        "w",
        encoding="utf-8",
        newline=""
    ) as file:

        writer = csv.writer(file)

        writer.writerow(CSV_HEADER)
        writer.writerows(rows)

    return len(rows)


def main():

    if not EXAM_DIR.exists():
        raise FileNotFoundError(
            f"Exam directory does not exist: {EXAM_DIR}"
        )

    print(f"Root:   {ROOT_DIR}")
    print(f"Exams:  {EXAM_DIR}")
    print(f"Output: {OUTPUT_FILE}")
    print()

    exams = collect_exams()

    print()
    print(f"Grouped {len(exams)} exams.")

    count = create_csv(exams)

    print(f"Generated {count} rows.")
    print(f"CSV written to: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
