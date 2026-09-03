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


def alternative_number(name: str) -> int | None:
    match = ALTERNATIVE_PATTERN.search(name)
    if match:
        return int(match.group(1))
    return None


def top_position(row: dict[str, str]) -> float:
    pos_y = float(row["PosY"])
    height = float(row["Height"])
    return min(pos_y, pos_y + height)


def process_file(path: Path) -> str:
    with path.open("r", newline="", encoding="utf-8") as file:
        reader = csv.DictReader(file)

        if reader.fieldnames is None:
            raise ValueError("CSV has no header")

        rows = list(reader)
        fieldnames = reader.fieldnames

    # ------------------------------------------------------------------
    # Validate the existing indices.
    # ------------------------------------------------------------------

    positive_rows: dict[int, dict[str, str]] = {}
    negative_rows: dict[int, list[dict[str, str]]] = {}

    for row_number, row in enumerate(rows, start=2):
        try:
            index = int(row["Index"])
        except ValueError as exc:
            raise ValueError(
                f"Invalid index {row['Index']!r} on row {row_number}"
            ) from exc

        if index > 0:
            if index in positive_rows:
                raise ValueError(
                    f"Index conflict: {index} occurs more than once"
                )

            positive_rows[index] = row

        elif index < 0:
            negative_rows.setdefault(-index, []).append(row)

    # Every negative index must have a positive counterpart.
    for index in negative_rows:
        if index not in positive_rows:
            raise ValueError(
                f"Index conflict: -{index} has no corresponding {index}"
            )

    # ------------------------------------------------------------------
    # Build groups.
    #
    # A group consists of:
    #
    #   positive row
    #   -positive row
    #   -positive row
    #   ...
    #
    # The whole group moves together.
    # ------------------------------------------------------------------

    groups = []

    for index, row in positive_rows.items():
        number = exercise_number(row["Name"])
        alternative = alternative_number(row["Name"])

        related_rows = sorted(
            negative_rows.get(index, []),
            key=top_position,
        )

        group = [row, *related_rows]

        if number is not None:
            # Numbered exercises are ordered by:
            #   exercise number
            #   alternative number
            #
            # The top position is only a tie-breaker.
            sort_key = (
                0,
                number,
                alternative if alternative is not None else 0,
                top_position(row),
            )
        else:
            # Rows without an exercise number are positioned by their
            # physical position in the document.
            sort_key = (
                1,
                top_position(row),
            )

        groups.append((sort_key, group))

    # ------------------------------------------------------------------
    # Important:
    #
    # Numbered exercises and unnumbered rows need to be mixed according
    # to the actual document position.
    #
    # Numbered exercises normally use their exercise number for ordering,
    # but an unnumbered row such as "Theorie" is placed by PosY.
    #
    # To achieve that, determine the position where each group belongs.
    # ------------------------------------------------------------------

    def group_sort_key(item):
        _, group = item
        positive_row = group[0]

        number = exercise_number(positive_row["Name"])

        if number is None:
            # Unnumbered rows are sorted by their actual position.
            return (
                top_position(positive_row),
                0,
                0,
            )

        alternative = alternative_number(positive_row)

        # Numbered exercises are ordered by exercise number and alternative.
        #
        # The document position is used only as a final tie-breaker.
        return (
            float("inf"),
            number,
            alternative if alternative is not None else 0,
            top_position(positive_row),
        )

    # The above still cannot mix "Theorie" correctly with numbered rows:
    # "Theorie" needs to participate in the same ordering based on its
    # physical position whenever it falls before/after numbered exercises.
    #
    # Therefore calculate the intended sequence incrementally below.

    numbered_groups = []
    unnumbered_groups = []

    for _, group in groups:
        positive_row = group[0]
        number = exercise_number(positive_row["Name"])

        if number is None:
            unnumbered_groups.append(group)
        else:
            numbered_groups.append(group)

    numbered_groups.sort(
        key=lambda group: (
            exercise_number(group[0]["Name"]),
            (
                alternative_number(group[0]["Name"])
                if alternative_number(group[0]["Name"]) is not None
                else 0
            ),
            top_position(group[0]),
        )
    )

    unnumbered_groups.sort(
        key=lambda group: top_position(group[0])
    )

    # ------------------------------------------------------------------
    # Merge them according to document position.
    #
    # Numbered exercises keep their exercise-number ordering relative to
    # each other. Unnumbered rows are inserted according to PosY.
    # ------------------------------------------------------------------

    result_groups = []

    numbered_index = 0
    unnumbered_index = 0

    while (
        numbered_index < len(numbered_groups)
        or unnumbered_index < len(unnumbered_groups)
    ):
        numbered_group = (
            numbered_groups[numbered_index]
            if numbered_index < len(numbered_groups)
            else None
        )

        unnumbered_group = (
            unnumbered_groups[unnumbered_index]
            if unnumbered_index < len(unnumbered_groups)
            else None
        )

        if numbered_group is None:
            result_groups.append(unnumbered_group)
            unnumbered_index += 1
            continue

        if unnumbered_group is None:
            result_groups.append(numbered_group)
            numbered_index += 1
            continue

        # An unnumbered row belongs before the next numbered exercise
        # if it appears physically before that exercise.
        if top_position(unnumbered_group[0]) < top_position(numbered_group[0]):
            result_groups.append(unnumbered_group)
            unnumbered_index += 1
        else:
            result_groups.append(numbered_group)
            numbered_index += 1

    sorted_rows = [
        row
        for group in result_groups
        for row in group
    ]

    # ------------------------------------------------------------------
    # Assign new indices.
    #
    # Every positive row gets a new sequential index.
    # All of its related negative rows receive the corresponding negative
    # index.
    # ------------------------------------------------------------------

    new_rows = []

    for new_index, group in enumerate(result_groups, start=1):
        positive_row = group[0]
        old_index = int(positive_row["Index"])

        updated_positive = positive_row.copy()
        updated_positive["Index"] = str(new_index)
        new_rows.append(updated_positive)

        for related_row in group[1:]:
            # Make sure this really is a negative row belonging to the
            # positive row.
            if int(related_row["Index"]) != -old_index:
                raise ValueError(
                    f"Internal index conflict for {old_index}"
                )

            updated_related = related_row.copy()
            updated_related["Index"] = str(-new_index)
            new_rows.append(updated_related)

    # ------------------------------------------------------------------
    # Serialize and compare.
    # ------------------------------------------------------------------

    def serialize(rows_to_write):
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

    path.write_text(new_content, encoding="utf-8")

    print(f"[INFO] Edited: {path}")
    return "edited"


def main():
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