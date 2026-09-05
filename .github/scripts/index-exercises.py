#!/usr/bin/env python3

import csv
import re
import subprocess
from pathlib import Path
from collections import defaultdict


# ============================================================
# Configuration
# ============================================================

GIT_DIR = Path(
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

EXAMS_DIR = GIT_DIR / "exams"
OUTPUT = EXAMS_DIR / "exercises.csv"


# ============================================================
# Regex
# ============================================================

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
    (?P<type>ENONCE|CORRIGE|ORAL|DATA)
    \.(?P<extension>pdf|zip)
    $
    """,
    re.IGNORECASE | re.VERBOSE,
)


# Directory names:

# CB_INFOR_2006_ETE_ENONCE
# CB_INFOR_2006_ETE_REP_ENONCE
# CB_INFOR_2006_ETE_AJOU_CORRIGE

DIR_PATTERN = re.compile(
    r"""
    ^
    (?P<section>[^_]+)
    _
    (?P<subject>.+?)
    _
    (?P<year>\d{4})
    _
    (?P<season>[A-Z]+)
    (?P<retry>_REP|_AJOU)?
    _
    (?P<type>ENONCE|CORRIGE)
    $
    """,
    re.IGNORECASE | re.VERBOSE,
)


EXERCISE_NUMBER_PATTERN = re.compile(r" \d+")
ALTERNATIVE_PATTERN = re.compile(r"#(\d+)$")


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

def rel(path: Path) -> str:
    """Return a path relative to the repository root."""

    return path.relative_to(GIT_DIR).as_posix()


def format_box(row: dict) -> str:
    """Format a box as ((x1, y1), (x2, y2))."""

    x1 = float(row["PosX"])
    y1 = float(row["PosY"])
    x2 = x1 + float(row["Width"])
    y2 = y1 + float(row["Height"])

    return f"(({x1:g}, {y1:g}), ({x2:g}, {y2:g}))"


def read_boxes(path: Path) -> dict[int, list[dict]]:
    """Read index.csv and group boxes by Index."""

    boxes = defaultdict(list)

    with path.open(
        newline="",
        encoding="utf-8",
    ) as f:
        reader = csv.DictReader(f)

        for row in reader:
            try:
                index = int(row["Index"])
            except (ValueError, KeyError):
                continue

            boxes[index].append(row)

    return boxes


def get_exercise_index(
    name: str,
    path: Path,
) -> int | None:
    """Extract the exercise number from a box name."""

    match = EXERCISE_NUMBER_PATTERN.search(name)

    if not match:
        print(
            f"Warning: No exercise number found in box name "
            f"{name!r} in {rel(path)}, skipping box"
        )
        return None

    return int(match.group())


def get_alternative_index(name: str) -> int:
    """Extract an alternative index from the end of a box name."""

    match = ALTERNATIVE_PATTERN.search(name)

    return int(match.group(1)) if match else 0


def parse_tags(value: str | None) -> list[str]:
    """Parse the space-separated Tags column."""

    if not value:
        return []

    tags = []

    for tag in re.split(r"\s+", value.strip()):
        if tag and tag not in tags:
            tags.append(tag)

    return tags


def format_tags(tags: set[str]) -> str:
    """Format tags as space-separated values."""

    return " ".join(
        sorted(
            tags,
            key=str.lower,
        )
    )


# ============================================================
# Exam filename decoding
# ============================================================

def parse_exam_filename(path: Path) -> dict | None:
    """
    Decode an exam filename.

    This is the same decoding scheme used for db.csv.
    """

    match = FILENAME_PATTERN.match(path.name)

    if not match:
        print(
            f"Warning: Could not decode exam filename: "
            f"{path.name}"
        )
        return None

    prefix = match.group("prefix")
    year = match.group("year")
    season = match.group("season").upper()
    retry = match.group("retry")
    name = (match.group("name") or "").upper()
    filename_type = match.group("type").upper()

    prefix_parts = prefix.split("_")

    if len(prefix_parts) < 2:
        print(
            f"Warning: Could not determine Section and Subject "
            f"from exam filename: {path.name}"
        )
        return None

    section = prefix_parts[0].upper()
    subject = "_".join(prefix_parts[1:]).upper()

    if retry == "_REP":
        subtype = "REP"
    elif retry == "_AJOU":
        subtype = "AJOU"
    else:
        subtype = "NORMAL"

    file_type = TYPE_MAPPING.get(filename_type)

    if file_type is None:
        print(
            f"Warning: Unknown exam type {filename_type!r} "
            f"in {path.name}"
        )
        return None

    return {
        "Section": section,
        "Subject": subject,
        "Year": year,
        "Season": season,
        "Subtype": subtype,
        "Name": name,
        "Type": file_type,
    }


def make_exam_key(parsed: dict) -> str:
    """
    Build the same key used by db.csv.

    Example:

        CC:MATH1:2025:ETE:NORMAL::SOLUTION
    """

    return ":".join(
        [
            parsed["Section"],
            parsed["Subject"],
            parsed["Year"],
            parsed["Season"],
            parsed["Subtype"],
            parsed["Name"],
            parsed["Type"],
        ]
    )


# ============================================================
# Source PDF
# ============================================================

def get_source_pdf(folder: Path) -> Path | None:
    """
    Get the PDF associated with an exercise directory.

    Given:

        a/b/c.pdf
        a/b/c/

    returns:

        a/b/c.pdf

    No symlink is resolved here.
    """

    pdf = folder.with_suffix(".pdf")

    # is_symlink() must be checked separately because a broken
    # symlink returns False for exists().
    if not pdf.exists() and not pdf.is_symlink():
        print(
            f"Warning: Missing source PDF: {rel(pdf)}"
        )
        return None

    return pdf


def get_source_exam(folder: Path) -> str:
    """
    Return the SourceExam key for an exercise.

    Only the associated PDF is followed.

    The individual WEBP files are never followed or inspected.

    If the PDF is not a symlink, SourceExam is empty.
    """

    pdf = get_source_pdf(folder)

    if pdf is None:
        return ""

    # This is the important distinction:
    #
    # c.pdf
    #     -> SourceExam = ""
    #
    # c.pdf -> original.pdf
    #     -> SourceExam = decoded key of original.pdf
    #
    if not pdf.is_symlink():
        return ""

    try:
        target = pdf.resolve(strict=True)
    except FileNotFoundError:
        print(
            f"Warning: Broken source PDF symlink: {rel(pdf)}"
        )
        return ""

    parsed = parse_exam_filename(target)

    if parsed is None:
        print(
            f"Warning: Could not decode source PDF target: "
            f"{target.name}"
        )
        return ""

    return make_exam_key(parsed)


# ============================================================
# Exercise discovery
# ============================================================

def discover_exams():
    """
    Discover exercise directories from their associated PDFs.

    This intentionally scans PDFs instead of index.csv files.

    This is important because rglob("index.csv") does not reliably
    descend into symlinked directories.

    For every:

        a/b/c.pdf

    we inspect:

        a/b/c/index.csv

    The PDF itself may be a symlink. That is allowed.
    """

    exams = defaultdict(dict)

    # --------------------------------------------------------
    # Find PDFs.
    #
    # Do NOT use resolve() here.
    #
    # We need the path as it exists in the repository so that:
    #
    #     c.pdf -> source.pdf
    #
    # is still recognized as the c.pdf attachment.
    # --------------------------------------------------------

    pdf_files = sorted(
        EXAMS_DIR.rglob("*.pdf")
    )

    print(f"Found {len(pdf_files)} PDF files.")

    for pdf in pdf_files:

        # The exercise directory is the directory with the same
        # basename as the PDF.
        #
        #     c.pdf
        #     c/
        #
        # Therefore:
        #
        #     pdf.parent / pdf.stem
        #
        folder = pdf.parent / pdf.stem

        index_csv = folder / "index.csv"

        if not index_csv.exists():
            continue

        # The directory name is still the exercise directory name.
        # It may itself be a symlink. That does not matter because
        # we access it directly rather than asking rglob() to
        # traverse it.

        match = DIR_PATTERN.match(folder.name)

        if not match:
            print(
                f"Warning: Skipping unrecognized directory: "
                f"{rel(folder)}"
            )
            continue

        data = match.groupdict()

        if data["retry"] == "_REP":
            subtype = "REP"
        elif data["retry"] == "_AJOU":
            subtype = "AJOU"
        else:
            subtype = "NORMAL"

        key = (
            data["section"].upper(),
            data["subject"].upper(),
            data["year"],
            subtype,
            data["season"].upper(),
        )

        document_type = data["type"].upper()

        # If the same document was discovered more than once,
        # keep the first one.
        if document_type in exams[key]:
            print(
                f"Warning: Duplicate {document_type}: "
                f"{rel(folder)}"
            )
            continue

        exams[key][document_type] = folder

    return exams


# ============================================================
# Main
# ============================================================

def main():

    exams = discover_exams()

    with OUTPUT.open(
        "w",
        newline="",
        encoding="utf-8",
    ) as f:

        writer = csv.writer(f)

        writer.writerow(
            [
                "Section",
                "Subject",
                "Year",
                "Subtype",
                "Season",
                "Source",
                "Exercise Index",
                "Qualifier",
                "Alternative Index",
                "Additive box",
                "Subtractive boxes",
                "Attachment",
                "Tags",
                "SourceExam",
            ]
        )

        for (
            section,
            subject,
            year,
            subtype,
            season,
        ), files in sorted(exams.items()):

            # ====================================================
            # First pass:
            #
            # Collect tags from both ENONCE and CORRIGE.
            # ====================================================

            exercise_tags: dict[
                tuple[str, str, str, str, str, int],
                set[str],
            ] = defaultdict(set)

            for document_type, folder in sorted(files.items()):

                index_path = folder / "index.csv"

                if not index_path.exists():
                    print(
                        f"Warning: Missing index.csv: "
                        f"{rel(index_path)}"
                    )
                    continue

                boxes = read_boxes(index_path)

                additive_boxes = {
                    index: rows
                    for index, rows in boxes.items()
                    if index > 0
                }

                for additive_rows in additive_boxes.values():

                    for additive_row in additive_rows:

                        name = additive_row.get(
                            "Name",
                            "",
                        )

                        exercise_index = get_exercise_index(
                            name,
                            index_path,
                        )

                        if exercise_index is None:
                            continue

                        exercise_key = (
                            section,
                            subject,
                            year,
                            subtype,
                            season,
                            exercise_index,
                        )

                        exercise_tags[
                            exercise_key
                        ].update(
                            parse_tags(
                                additive_row.get("Tags")
                            )
                        )

            # ====================================================
            # Second pass:
            #
            # Write one row per exercise attachment.
            # ====================================================

            for document_type, folder in sorted(files.items()):

                index_path = folder / "index.csv"

                if not index_path.exists():
                    continue

                boxes = read_boxes(index_path)

                additive_boxes = {
                    index: rows
                    for index, rows in boxes.items()
                    if index > 0
                }

                subtractive_boxes = {
                    abs(index): rows
                    for index, rows in boxes.items()
                    if index < 0
                }

                # ------------------------------------------------
                # Source is the associated PDF path.
                #
                # This is NOT resolved.
                # ------------------------------------------------

                source_pdf = get_source_pdf(folder)

                source = (
                    rel(source_pdf)
                    if source_pdf is not None
                    else ""
                )

                # ------------------------------------------------
                # SourceExam follows ONLY the PDF symlink.
                #
                # WEBP symlinks are never followed.
                # ------------------------------------------------

                source_exam = get_source_exam(folder)

                row_count = 0

                for box_index, additive_rows in sorted(
                    additive_boxes.items()
                ):

                    for additive_row in additive_rows:

                        name = additive_row.get(
                            "Name",
                            "",
                        )

                        exercise_index = get_exercise_index(
                            name,
                            index_path,
                        )

                        if exercise_index is None:
                            continue

                        alternative_index = get_alternative_index(
                            name
                        )

                        subtractive = []

                        for subtractive_row in (
                            subtractive_boxes.get(
                                box_index,
                                [],
                            )
                        ):
                            subtractive.append(
                                format_box(
                                    subtractive_row
                                )
                            )

                        additive_box = format_box(
                            additive_row
                        )

                        subtractive_boxes_value = ";".join(
                            subtractive
                        )

                        # ------------------------------------------------
                        # Never resolve this path.
                        #
                        # Even if the WEBP is a symlink, we want the
                        # repository path here.
                        # ------------------------------------------------

                        attachment_path = (
                            folder
                            / f"{box_index}.webp"
                        )

                        attachment = rel(
                            attachment_path
                        )

                        exercise_key = (
                            section,
                            subject,
                            year,
                            subtype,
                            season,
                            exercise_index,
                        )

                        tags = format_tags(
                            exercise_tags.get(
                                exercise_key,
                                set(),
                            )
                        )

                        if document_type == "ENONCE":
                            qualifier = "STATEMENT"
                        elif document_type == "CORRIGE":
                            qualifier = "SOLUTION"
                        else:
                            qualifier = document_type

                        writer.writerow(
                            [
                                section,
                                subject,
                                year,
                                subtype,
                                season,
                                source,
                                exercise_index,
                                qualifier,
                                alternative_index,
                                additive_box,
                                subtractive_boxes_value,
                                attachment,
                                tags,
                                source_exam,
                            ]
                        )

                        row_count += 1

                print(
                    f"Info: Found {row_count} exercises for "
                    f"{section}/{subject} {year} {season} "
                    f"{subtype} {document_type}"
                )

    print(f"Wrote {OUTPUT}")


if __name__ == "__main__":
    main()
