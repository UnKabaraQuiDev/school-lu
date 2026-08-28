#!/usr/bin/env python3

from pathlib import Path
import hashlib
import subprocess
import sys
import zipfile


def get_repo_root():
    return Path(
        subprocess.check_output(
            ["git", "-C", str(Path(__file__).resolve().parent), "rev-parse", "--show-toplevel"],
            text=True,
        ).strip()
    )


def files_are_identical(file_a: Path, file_b: Path) -> bool:
    """Compare two files by SHA-256."""

    def sha256(path: Path) -> str:
        digest = hashlib.sha256()

        with path.open("rb") as file:
            for chunk in iter(lambda: file.read(1024 * 1024), b""):
                digest.update(chunk)

        return digest.hexdigest()

    return sha256(file_a) == sha256(file_b)


def main():
    root_dir = get_repo_root()
    exams_dir = root_dir / "exams"

    if not exams_dir.is_dir():
        print(f"[ERROR] Directory does not exist: {exams_dir}")
        return 1

    zip_files = sorted(exams_dir.rglob("*.zip"))

    print(f"Found {len(zip_files)} ZIP files.")

    errors = 0
    extracted = 0

    for zip_path in zip_files:
        print(f"[ZIP] {zip_path.relative_to(root_dir)}")

        try:
            with zipfile.ZipFile(zip_path, "r") as archive:
                for member in archive.infolist():

                    if member.is_dir():
                        continue

                    filename = Path(member.filename).name

                    if not filename.endswith(
                        ("ENONCE.pdf", "CORRIGE.pdf", "ORAL.pdf")
                    ):
                        continue

                    destination = zip_path.parent / filename

                    # Extract to a temporary file first so that a failed
                    # comparison never modifies an existing file.
                    temp_path = destination.with_name(
                        f".{destination.name}.tmp"
                    )

                    try:
                        with archive.open(member) as source, \
                                temp_path.open("wb") as target:

                            while True:
                                chunk = source.read(1024 * 1024)

                                if not chunk:
                                    break

                                target.write(chunk)

                        if destination.exists():

                            if files_are_identical(
                                destination,
                                temp_path,
                            ):
                                print(
                                    f"  [OK] Already exists: "
                                    f"{destination.name}"
                                )
                            else:
                                print(
                                    f"  [WARNING] Existing file differs: "
                                    f"{destination}"
                                )
                                errors += 1

                        else:
                            temp_path.replace(destination)

                            print(
                                f"  [EXTRACT] {destination.name}"
                            )

                            extracted += 1

                    finally:
                        if temp_path.exists():
                            temp_path.unlink()

        except zipfile.BadZipFile:
            print(f"  [ERROR] Invalid ZIP: {zip_path}")
            errors += 1

        except Exception as error:
            print(
                f"  [ERROR] Failed to process {zip_path}: "
                f"{error}"
            )
            errors += 1

    print()
    print("Done.")
    print(f"ZIP files:  {len(zip_files)}")
    print(f"Extracted:  {extracted}")
    print(f"Warnings:   {errors}")

    return 1 if errors else 0


if __name__ == "__main__":
    sys.exit(main())