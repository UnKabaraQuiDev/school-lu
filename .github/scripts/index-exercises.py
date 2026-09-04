#!/usr/bin/env python3

import csv
import re
import subprocess
from pathlib import Path
from collections import defaultdict

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


def rel(path: Path) -> str:
    return path.relative_to(EXAMS_DIR.parent).as_posix()


def format_box(row: dict) -> str:
    x1 = float(row["PosX"])
    y1 = float(row["PosY"])
    x2 = x1 + float(row["Width"])
    y2 = y1 + float(row["Height"])

    return f"(({x1:g}, {y1:g}), ({x2:g}, {y2:g}))"


def read_boxes(path: Path) -> dict[int, list[dict]]:
    boxes = defaultdict(list)

    with path.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)

        for row in reader:
            try:
                index = int(row["Index"])
            except (ValueError, KeyError):
                continue

            boxes[index].append(row)

    return boxes


def get_exercise_index(name: str, path: Path) -> int | None:
    match = EXERCISE_NUMBER_PATTERN.search(name)

    if not match:
        print(
            f"Warning: No exercise number found in box name "
            f"{name!r} in {rel(path)}, skipping box"
        )
        return None

    return int(match.group())


def get_alternative_index(name: str) -> int:
    match = ALTERNATIVE_PATTERN.search(name)
    return int(match.group(1)) if match else 0


def get_source(folder: Path) -> str:
    pdf = folder.with_suffix(".pdf")

    if not pdf.exists():
        print(f"Warning: Missing source PDF: {rel(pdf)}")
        return ""

    return rel(pdf)


def parse_tags(value: str | None) -> list[str]:
    """
    Parse the Tags column.

    Tags are stored as SPACE-separated values.
    """
    if not value:
        return []

    tags: list[str] = []

    for tag in re.split(r"\s+", value.strip()):
        if tag and tag not in tags:
            tags.append(tag)

    return tags


def format_tags(tags: set[str]) -> str:
    """
    Format tags as SPACE-separated values.
    """
    return " ".join(
        sorted(
            tags,
            key=str.lower,
        )
    )


def main():
    exams = defaultdict(dict)

    for index_csv in EXAMS_DIR.rglob("index.csv"):
        folder = index_csv.parent
        match = DIR_PATTERN.match(folder.name)

        if not match:
            print(
                f"Warning: Skipping unrecognized directory: "
                f"{rel(folder)}"
            )
            continue

        data = match.groupdict()

        key = (
            data["section"],
            data["subject"],
            data["year"],
            data["season"],
            (
                "REP"
                if data["retry"]
                and data["retry"] == "_REP"
                else "AJOU"
                if data["retry"]
                and data["retry"] == "_AJOU"
                else "NORMAL"
            ),
        )

        exams[key][data["type"].upper()] = folder

    output = EXAMS_DIR / "exercises.csv"

    with output.open(
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
            ]
        )

        for (
            section,
            subject,
            year,
            season,
            subtype,
        ), files in sorted(exams.items()):

            # ----------------------------------------------------------
            # First pass:
            #
            # Collect ALL tags belonging to each exercise across both
            # ENONCE and CORRIGE.
            #
            # Exercise identity is ONLY:
            #
            #   Section
            #   Subject
            #   Year
            #   Subtype
            #   Season
            #   Exercise Index
            #
            # Qualifier, alternative index, boxes and attachment are
            # deliberately ignored.
            # ----------------------------------------------------------

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

            # ----------------------------------------------------------
            # Second pass:
            #
            # Write all rows, using the tags collected above.
            # ----------------------------------------------------------

            for document_type, folder in sorted(files.items()):
                index_path = folder / "index.csv"

                if not index_path.exists():
                    continue

                boxes = read_boxes(index_path)
                source = get_source(folder)

                # Positive indexes are additive boxes.
                # Negative indexes are subtractive boxes.
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

                        for subtractive_row in subtractive_boxes.get(
                            box_index,
                            [],
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

                        writer.writerow(
                            [
                                section,
                                subject,
                                year,
                                subtype,
                                season,
                                source,
                                exercise_index,
                                (
                                    "STATEMENT"
                                    if document_type == "ENONCE"
                                    else "SOLUTION"
                                    if document_type == "CORRIGE"
                                    else "???"
                                ),
                                alternative_index,
                                additive_box,
                                subtractive_boxes_value,
                                attachment,
                                tags,
                            ]
                        )

                        row_count += 1

                print(
                    f"Info: Found {row_count} boxes for "
                    f"{section}/{subject} {year} {season}"
                    f" {subtype} {document_type}"
                )

    print(f"Wrote {output}")


if __name__ == "__main__":
    main()