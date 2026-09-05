#!/usr/bin/env python3

from pathlib import Path
import subprocess
import csv
import re


# ============================================================
# Configuration
# ============================================================

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

EXAM_DIR = ROOT_DIR / "exams"
OUTPUT_FILE = EXAM_DIR / "db.csv"

CSV_HEADER = [
    "Section",
    "Subject",
    "Year",
    "Season",
    "Subtype",
    "Name",
    "Qualifier",
    "Attachement",
    "Source",
]


# ============================================================
# Regex
# ============================================================

# Examples:
#
# CB_INFOR_2006_ETE_PARTIE_PRATIQUE_CORRIGE.pdf
# CB_4LANG_CHINO_2023_ETE_ENONCE.pdf
# CA_LLCO_MALA_PSYA_ALLEM_2005_ETE_ENONCE.pdf
#
# The subject is everything between the section and the year.

FILENAME_PATTERN = re.compile(
    r"""
    ^
    (?P<prefix>.+?)
    _
    (?P<year>\d{4})
    _
    (?P<season>[A-Z]+)
    (?P<retry>_REP|_AJOU)?
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
# Type mapping
# ============================================================

TYPE_MAPPING = {
    "ENONCE": "STATEMENT",
    "CORRIGE": "SOLUTION",
    "ORAL": "ORAL",
    "DATA": "DATA",
}


# ============================================================
# Helpers
# ============================================================

def print_ambiguity(path, reason):
    """Print an ambiguous or invalid file."""

    try:
        display_path = path.relative_to(ROOT_DIR)
    except ValueError:
        display_path = path

    print(f"[SKIP] {display_path}")
    print(f"       {reason}")


def parse_filename(path):
    """
    Parse an exam filename.

    Returns:
        dict or None
    """

    match = FILENAME_PATTERN.match(path.name)

    if not match:
        print_ambiguity(
            path,
            "Filename does not match the expected pattern.",
        )
        return None

    prefix = match.group("prefix")
    year = match.group("year")
    season = match.group("season").upper()

    retry = match.group("retry")

    if retry == "_REP":
        subtype = "REP"
    elif retry == "_AJOU":
        subtype = "AJOU"
    else:
        subtype = "NORMAL"

    name = match.group("name") or ""
    name = name.upper()

    filename_type = match.group("type").upper()

    if filename_type not in TYPE_MAPPING:
        print_ambiguity(
            path,
            f"Unknown file type: {filename_type}",
        )
        return None

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
            f"Could not determine Section and Subject from '{prefix}'.",
        )
        return None

    section = prefix_parts[0]
    subject = "_".join(prefix_parts[1:])

    if not section or not subject:
        print_ambiguity(
            path,
            "Section or Subject is empty.",
        )
        return None

    return {
        "Section": section.upper(),
        "Subject": subject.upper(),
        "Year": year,
        "Season": season,
        "Subtype": subtype,
        "Name": name,
        "Qualifier": TYPE_MAPPING[filename_type],
    }


def make_key(parsed):
    """
    Convert parsed exam metadata to the source key format.

    Example:

        CB:INFOR:2006:ETE:NORMAL:PARTIE_PRATIQUE:SOLUTION
    """

    return ":".join(
        [
            parsed["Section"],
            parsed["Subject"],
            parsed["Year"],
            parsed["Season"],
            parsed["Subtype"],
            parsed["Name"],
            parsed["Qualifier"],
        ]
    )


def get_source(path):
    """
    If path is a symlink, resolve its target and parse the target
    filename again.

    Returns:
        source key or an empty string.
    """

    if not path.is_symlink():
        return ""

    try:
        target = path.resolve(strict=True)
    except FileNotFoundError:
        print_ambiguity(
            path,
            "Symlink target does not exist.",
        )
        return ""

    parsed = parse_filename(target)

    if parsed is None:
        print_ambiguity(
            path,
            f"Could not decode symlink target '{target.name}'.",
        )
        return ""

    return make_key(parsed)


def parse_exam(path):
    """Parse an exam attachment."""

    return parse_filename(path)


# ============================================================
# Main processing
# ============================================================

def collect_exams():
    """
    Scan all PDFs and ZIP files.

    Each file becomes one CSV row.
    """

    rows = []

    files = sorted(
        list(EXAM_DIR.rglob("*.pdf"))
        + list(EXAM_DIR.rglob("*.zip"))
    )

    print(f"Found {len(files)} files.")

    for path in files:
        parsed = parse_exam(path)

        if parsed is None:
            continue

        try:
            attachment = path.relative_to(ROOT_DIR).as_posix()
        except ValueError:
            print_ambiguity(
                path,
                "File is outside the repository root.",
            )
            continue

        source = get_source(path)

        rows.append(
            [
                parsed["Section"],
                parsed["Subject"],
                parsed["Year"],
                parsed["Season"],
                parsed["Subtype"],
                parsed["Name"],
                parsed["Qualifier"],
                attachment,
                source,
            ]
        )

    return rows


def create_csv(rows):
    """Write all exam attachments to db.csv."""

    with OUTPUT_FILE.open(
        "w",
        encoding="utf-8",
        newline="",
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

    rows = collect_exams()

    print()
    print(f"Collected {len(rows)} attachments.")

    count = create_csv(rows)

    print(f"Generated {count} rows.")
    print(f"CSV written to: {OUTPUT_FILE}")


if __name__ == "__main__":
    main()
