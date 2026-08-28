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


def rel(path: Path) -> str:
    return path.relative_to(EXAMS_DIR).as_posix()


def read_index(path: Path) -> list[int]:
    exercises = []
    with path.open(newline="", encoding="utf-8") as f:
        reader = csv.DictReader(f)
        for row in reader:
            try:
                idx = int(row["Index"])
            except (ValueError, KeyError):
                continue

            if idx > 0:
                exercises.append(idx)

    return exercises


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
                "Exercise Index",
                "Mission Statement",
                "Solution",
            ]
        )

        for (section, subject, year, season, retry), files in sorted(exams.items()):
            statement_dir = files.get("ENONCE")
            solution_dir = files.get("CORRIGE")

            if statement_dir is None:
                print(
                    f"Warning: Missing statement folder for {section}/{subject} {year} {season}{'_REP' if retry else ''}"
                )
            if solution_dir is None:
                print(
                    f"Warning: Missing solution folder for {section}/{subject} {year} {season}{'_REP' if retry else ''}"
                )

            exercise_set = set()

            if statement_dir and (statement_dir / "index.csv").exists():
                exercise_set.update(read_index(statement_dir / "index.csv"))

            if solution_dir and (solution_dir / "index.csv").exists():
                exercise_set.update(read_index(solution_dir / "index.csv"))

            for exercise in sorted(exercise_set):
                statement_path = ""
                solution_path = ""

                if statement_dir:
                    candidate = statement_dir / f"{exercise}.webp"
                    if candidate.exists():
                        statement_path = rel(candidate)
                    else:
                        print(
                            f"Warning: Missing statement for {section}/{subject} {year} {season} exercise {exercise}"
                        )

                if solution_dir:
                    candidate = solution_dir / f"{exercise}.webp"
                    if candidate.exists():
                        solution_path = rel(candidate)
                    else:
                        print(
                            f"Warning: Missing solution for {section}/{subject} {year} {season} exercise {exercise}"
                        )

                writer.writerow(
                    [
                        section,
                        subject,
                        year,
                        "Yes" if retry else "No",
                        season,
                        exercise,
                        statement_path,
                        solution_path,
                    ]
                )
                
            print(
                f"Info: Found {len(exercise_set)} for {section}/{subject} {year} {season}{'_REP' if retry else ''}"
            )

    print(f"Wrote {output}")


if __name__ == "__main__":
    main()