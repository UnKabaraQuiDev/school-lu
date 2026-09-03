import csv
import re
import subprocess
from pathlib import Path


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

EXERCISE_NUMBER_PATTERN = re.compile(r"\d+")
ALTERNATIVE_PATTERN = re.compile(r"#(\d+)$")


def exercise_number(name: str) -> int | None:
    match = EXERCISE_NUMBER_PATTERN.search(name)
    return int(match.group()) if match else None


def alternative_number(name: str) -> int:
    match = ALTERNATIVE_PATTERN.search(name)
    return int(match.group(1)) if match else 0


def top_position(row: dict[str, str]) -> float:
    pos_y = float(row["PosY"])
    height = float(row["Height"])
    return min(pos_y, pos_y + height)


def sort_rows(rows: list[dict[str, str]]) -> list[dict[str, str]]:
    """
    Sort normal exercise rows by:
      1. exercise number
      2. alternative number
      3. top position

    Rows with negative indices are placed directly after their
    corresponding positive-index row, ordered by top position.

    Rows without an exercise number are placed according to their
    top position.
    """
    positive_rows = {
        int(row["Index"]): row
        for row in rows
        if int(row["Index"]) > 0
    }

    negative_rows: dict[int, list[dict[str, str]]] = {}

    for row in rows:
        index = int(row["Index"])
        if index < 0:
            negative_rows.setdefault(-index, []).append(row)

    # Validate that every negative index refers to an existing positive index.
    for index in negative_rows:
        if index not in positive_rows:
            raise ValueError(
                f"Negative index -{index} has no corresponding positive index"
            )

    def positive_sort_key(row: dict[str, str]):
        number = exercise_number(row["Name"])
        if number is None:
            return (1, float("inf"), float("inf"), top_position(row))

        return (
            0,
            number,
            alternative_number(row["Name"]),
            top_position(row),
        )

    sorted_positive = sorted(positive_rows.values(), key=positive_sort_key)

    result: list[dict[str, str]] = []

    for row in sorted_positive:
        old_index = int(row["Index"])
        result.append(row)

        related = sorted(
            negative_rows.get(old_index, []),
            key=top_position,
        )
        result.extend(related)

    # Any rows not covered above are rows with invalid/duplicate positive
    # indices, which are handled by validation before this point.
    return result


def process_file(path: Path) -> str:
    with path.open("r", newline="", encoding="utf-8") as file:
        reader = csv.DictReader(file)

        if reader.fieldnames is None:
            raise ValueError("CSV has no header")

        required_fields = {
            "Index",
            "Name",
            "PosX",
            "PosY",
            "Width",
            "Height",
        }
        missing = required_fields - set(reader.fieldnames)
        if missing:
            raise ValueError(
                f"Missing required columns: {', '.join(sorted(missing))}"
            )

        rows = list(reader)

    # Check the starting list for index conflicts.
    positive_indices: dict[int, int] = {}
    negative_indices: list[tuple[int, int]] = []

    for row_number, row in enumerate(rows, start=2):
        try:
            index = int(row["Index"])
        except ValueError as exc:
            raise ValueError(
                f"Invalid Index {row['Index']!r} on CSV row {row_number}"
            ) from exc

        if index > 0:
            if index in positive_indices:
                raise ValueError(
                    f"Index conflict: positive index {index} occurs more than once"
                )
            positive_indices[index] = row_number

        elif index < 0:
            negative_indices.append((-index, row_number))

    # Every negative index must have exactly one positive counterpart.
    for related_index, row_number in negative_indices:
        if related_index not in positive_indices:
            raise ValueError(
                f"Index conflict: row {row_number} uses -{related_index}, "
                f"but {related_index} does not exist"
            )

    sorted_rows = sort_rows(rows)

    # Assign new indices. Negative rows inherit the index of the positive
    # exercise they belong to.
    new_rows: list[dict[str, str]] = []

    for new_index, row in enumerate(
        (row for row in sorted_rows if int(row["Index"]) > 0),
        start=1,
    ):
        old_index = int(row["Index"])

        updated_row = row.copy()
        updated_row["Index"] = str(new_index)
        new_rows.append(updated_row)

        related_rows = sorted(
            negative_rows := [
                candidate
                for candidate in sorted_rows
                if int(candidate["Index"]) == -old_index
            ],
            key=top_position,
        )

        for related_row in related_rows:
            updated_related = related_row.copy()
            updated_related["Index"] = str(-new_index)
            new_rows.append(updated_related)

    # Compare the complete CSV content, not just the ordering.
    fieldnames = reader.fieldnames

    def serialize(rows_to_write: list[dict[str, str]]) -> str:
        from io import StringIO

        output = StringIO()
        writer = csv.DictWriter(
            output,
            fieldnames=fieldnames,
            lineterminator="\n",
        )
        writer.writeheader()
        writer.writerows(rows_to_write)
        return output.getvalue()

    original_content = serialize(rows)
    new_content = serialize(new_rows)

    if original_content == new_content:
        print(f"[INFO] Already correct: {path}")
        return "correct"

    # Do not write anything if the resulting list contains conflicts.
    seen_positive: set[int] = set()

    for row in new_rows:
        index = int(row["Index"])

        if index > 0:
            if index in seen_positive:
                raise ValueError(
                    f"Generated index conflict: {index} occurs more than once"
                )
            seen_positive.add(index)

    path.write_text(new_content, encoding="utf-8")
    print(f"[INFO] Edited: {path}")

    return "edited"


def main() -> None:
    files = sorted(EXAMS_DIR.rglob("index.csv"))

    stats = {
        "found": len(files),
        "correct": 0,
        "edited": 0,
        "errors": 0,
    }

    for path in files:
        try:
            result = process_file(path)
            stats[result] += 1
        except Exception as exc:
            stats["errors"] += 1
            print(f"[WARNING] Skipped {path}: {exc}")

    print()
    print("Statistics:")
    print(f"  Files found:   {stats['found']}")
    print(f"  Already right: {stats['correct']}")
    print(f"  Edited:        {stats['edited']}")
    print(f"  Errors:        {stats['errors']}")


if __name__ == "__main__":
    main()