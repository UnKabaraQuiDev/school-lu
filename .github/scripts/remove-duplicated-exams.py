#!/usr/bin/env python3

from __future__ import annotations

import argparse
import hashlib
import os
import shutil
import subprocess
import sys
from collections import defaultdict
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

CHUNK_SIZE = 1024 * 1024


def file_hash(path: Path) -> str:
    """Return the SHA-256 hash of a file."""
    hasher = hashlib.sha256()

    with path.open("rb") as file:
        while chunk := file.read(CHUNK_SIZE):
            hasher.update(chunk)

    return hasher.hexdigest()


def find_pdfs(directory: Path) -> list[Path]:
    """Recursively find all PDF files."""
    return sorted(
        (
            path
            for path in directory.rglob("*")
            if path.is_file() and path.suffix.lower() == ".pdf"
        ),
        key=lambda path: path.relative_to(directory).as_posix().lower(),
    )


def find_duplicates(pdfs: list[Path]) -> dict[str, list[Path]]:
    """Hash PDFs and return groups containing duplicates."""
    hashes: dict[str, list[Path]] = defaultdict(list)

    total = len(pdfs)
    last_percent = 0

    for index, path in enumerate(pdfs, start=1):
        try:
            digest = file_hash(path)
            hashes[digest].append(path)
        except OSError as error:
            print(
                f"[WARNING] Could not hash {path}: {error}",
                file=sys.stderr,
            )

        percent = index * 100 // total

        if percent >= last_percent + 10:
            last_percent = (percent // 10) * 10
            print(f"[INFO] {last_percent}% scanned")

    return {
        digest: paths
        for digest, paths in hashes.items()
        if len(paths) > 1
    }


def associated_dir(pdf: Path) -> Path:
    """
    Return the directory associated with a PDF.

    Example:
        a/b/c.pdf -> a/b/c/
    """
    return pdf.with_suffix("")


def relative_target(target: Path, link: Path) -> Path:
    """Return target as a relative path from the link's parent directory."""
    return Path(os.path.relpath(target, start=link.parent))


def replace_file_with_symlink(target: Path, duplicate: Path) -> None:
    """Replace a file with a relative symlink."""
    duplicate.unlink()
    duplicate.symlink_to(relative_target(target, duplicate))


def replace_dir_with_symlink(target: Path, duplicate: Path) -> None:
    """
    Replace an existing directory, symlink, or other filesystem entry
    with a relative symlink to the target directory.

    If the directory does not exist, it is simply created as a symlink.
    """
    if duplicate.is_symlink():
        duplicate.unlink()
    elif duplicate.exists():
        shutil.rmtree(duplicate)

    duplicate.symlink_to(
        relative_target(target, duplicate),
        target_is_directory=True,
    )


def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Find duplicate PDFs and replace them with symlinks. "
            "Associated directories are handled as well."
        )
    )

    parser.add_argument(
        "--apply",
        action="store_true",
        help=(
            "Actually replace duplicate PDFs and directories with symlinks. "
            "Without this option, the script only performs a dry run."
        ),
    )

    args = parser.parse_args()

    if not EXAMS_DIR.is_dir():
        print(
            f"[WARNING] Exams directory does not exist: {EXAMS_DIR}",
            file=sys.stderr,
        )
        return 1

    mode = "APPLY" if args.apply else "DRY-RUN"

    print(f"[INFO] Scanning: {EXAMS_DIR}")
    print(f"[INFO] Mode: {mode}")

    try:
        pdfs = find_pdfs(EXAMS_DIR)
    except OSError as error:
        print(
            f"[WARNING] Could not scan {EXAMS_DIR}: {error}",
            file=sys.stderr,
        )
        return 1

    print(f"[INFO] Found {len(pdfs)} PDF files")

    if not pdfs:
        print("[INFO] No PDFs found")
        return 0

    print("[INFO] Scanning PDFs...")

    duplicates = find_duplicates(pdfs)

    # This is useful when there are fewer than 10 PDFs, or when the
    # progress output did not reach exactly 100%.
    print("[INFO] 100% scanned")

    if not duplicates:
        print("[INFO] No duplicates found")
        return 0

    duplicate_count = sum(len(paths) - 1 for paths in duplicates.values())

    print(
        f"[INFO] Found {len(duplicates)} duplicate group(s), "
        f"{duplicate_count} duplicate PDF(s)"
    )

    for paths in duplicates.values():
        # Keep the alphabetically lowest relative path.
        paths = sorted(
            paths,
            key=lambda path: path.relative_to(EXAMS_DIR).as_posix().lower(),
        )

        target = paths[0]
        target_dir = associated_dir(target)

        # The target directory is the source of truth.
        target_has_dir = target_dir.is_dir()

        duplicates_to_replace = paths[1:]

        print()
        print(f"[DUPLICATE] => {target.relative_to(EXAMS_DIR)}")

        if target_has_dir:
            print(
                f"  + associated directory: "
                f"{target_dir.relative_to(EXAMS_DIR)}"
            )

        for duplicate in duplicates_to_replace:
            duplicate_dir = associated_dir(duplicate)

            print(f" * {duplicate.relative_to(EXAMS_DIR)}")

            if target_has_dir:
                if duplicate_dir.exists() or duplicate_dir.is_symlink():
                    print(
                        f"   + directory: "
                        f"{duplicate_dir.relative_to(EXAMS_DIR)} "
                        f"-> {target_dir.relative_to(EXAMS_DIR)}"
                    )
                else:
                    print(
                        f"   + directory will be created: "
                        f"{duplicate_dir.relative_to(EXAMS_DIR)} "
                        f"-> {target_dir.relative_to(EXAMS_DIR)}"
                    )

        for duplicate in duplicates_to_replace:
            duplicate_dir = associated_dir(duplicate)

            # ------------------------------------------------------------
            # Replace the PDF
            # ------------------------------------------------------------

            if not args.apply:
                print(
                    f"[DRY-RUN] Would replace "
                    f"{duplicate.relative_to(EXAMS_DIR)} "
                    f"with symlink to "
                    f"{target.relative_to(EXAMS_DIR)}"
                )
            else:
                try:
                    replace_file_with_symlink(target, duplicate)

                    print(
                        f"[INFO] Replaced "
                        f"{duplicate.relative_to(EXAMS_DIR)} "
                        f"with symlink to "
                        f"{target.relative_to(EXAMS_DIR)}"
                    )
                except OSError as error:
                    print(
                        f"[WARNING] Could not replace {duplicate}: {error}",
                        file=sys.stderr,
                    )

                    # Do not touch the associated directory if replacing
                    # the PDF failed.
                    continue

            # ------------------------------------------------------------
            # Replace/create the associated directory
            # ------------------------------------------------------------

            if target_has_dir:
                if not args.apply:
                    if (
                        duplicate_dir.exists()
                        or duplicate_dir.is_symlink()
                    ):
                        print(
                            f"[DRY-RUN] Would remove "
                            f"{duplicate_dir.relative_to(EXAMS_DIR)} "
                            f"and replace it with symlink to "
                            f"{target_dir.relative_to(EXAMS_DIR)}"
                        )
                    else:
                        print(
                            f"[DRY-RUN] Would create symlink "
                            f"{duplicate_dir.relative_to(EXAMS_DIR)} "
                            f"-> {target_dir.relative_to(EXAMS_DIR)}"
                        )
                else:
                    try:
                        replace_dir_with_symlink(
                            target_dir,
                            duplicate_dir,
                        )

                        print(
                            f"[INFO] Replaced/created directory symlink "
                            f"{duplicate_dir.relative_to(EXAMS_DIR)} "
                            f"-> {target_dir.relative_to(EXAMS_DIR)}"
                        )
                    except OSError as error:
                        print(
                            f"[WARNING] Could not replace directory "
                            f"{duplicate_dir}: {error}",
                            file=sys.stderr,
                        )

    print()
    print("[INFO] Done")

    if not args.apply:
        print(
            "[INFO] Nothing was changed. "
            "Use --apply to make the changes."
        )

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
