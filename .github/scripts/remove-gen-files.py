from pathlib import Path
import subprocess


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

EXAMS_DB_DIR = ROOT_DIR / "exam-db"

for path in EXAMS_DB_DIR.rglob("*.html"):
    if path.name.endswith(".template.html"):
        continue

    path.unlink()

for path in sorted(
    EXAMS_DB_DIR.rglob("*"),
    key=lambda p: len(p.parts),
    reverse=True,
):
    if path.is_dir() and not any(path.iterdir()):
        path.rmdir()