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
    (?P<retry>_REP)?
    _
    (?P<type>ENONCE|CORRIGE)
    $
    """,
    re.IGNORECASE | re.VERBOSE,
)

EXERCISE_NUMBER_PATTERN = re.compile(r"\d+")
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


def main():
    exams = defaultdict(dict)

    for index_csv in EXAMS_DIR.rglob("index.csv"):
        folder = index_csv.parent
        match = DIR_PATTERN.match(folder.name)

        if not match:
            print(f"Warning: Skipping unrecognized directory: {rel(folder)}")
            continue

        data = match.groupdict()

        key = (
            data["section"],
            data["subject"],
            data["year"],
            data["season"],
            bool(data["retry"]),
        )

        exams[key][data["type"].upper()] = folder

    output = EXAMS_DIR / "exercises.csv"

    with output.open("w", newline="", encoding="utf-8") as f:
        writer = csv.writer(f)

        writer.writerow(
            [
                "Section",
                "Subject",
                "Year",
                "Retry",
                "Season",
                "Source",
                "Exercise Index",
                "Qualifier",
                "Alternative Index",
                "Additive box",
                "Subtractive boxes",
                "Attachment",
            ]
        )

        for (section, subject, year, season, retry), files in sorted(exams.items()):
            for document_type, folder in sorted(files.items()):
                index_path = folder / "index.csv"

                if not index_path.exists():
                    print(f"Warning: Missing index.csv: {rel(index_path)}")
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

                for box_index, additive_rows in sorted(additive_boxes.items()):
                    for additive_row in additive_rows:
                        name = additive_row["Name"]

                        exercise_index = get_exercise_index(name, index_path)

                        if exercise_index is None:
                            continue

                        alternative_index = get_alternative_index(name)

                        subtractive = []

                        for subtractive_row in subtractive_boxes.get(
                            box_index, []
                        ):
                            subtractive.append(format_box(subtractive_row))

                        additive_box = format_box(additive_row)
                        subtractive_boxes_value = ";".join(subtractive)

                        attachment_path = folder / f"{box_index}.webp"
                        attachment = rel(attachment_path)

                        writer.writerow(
                            [
                                section,
                                subject,
                                year,
                                "Yes" if retry else "No",
                                season,
                                source,
                                exercise_index,
                                "STATEMENT" if document_type == "ENONCE" else "SOLUTION" if document_type == "CORRIGE" else "???",
                                alternative_index,
                                additive_box,
                                subtractive_boxes_value,
                                attachment,
                            ]
                        )

                        row_count += 1

                print(
                    f"Info: Found {row_count} boxes for "
                    f"{section}/{subject} {year} {season}"
                    f"{'_REP' if retry else ''} {document_type}"
                )

    print(f"Wrote {output}")


if __name__ == "__main__":
    main()
