from pathlib import Path
import json
import re
import subprocess
import sys


# ============================================================
# Configuration
# ============================================================

LANGUAGES = ["en", "de", "fr"]

PLACEHOLDER = "<translation missing>"

I18N_SCRIPT_PATTERN = re.compile(
    r'<script\b'
    r'(?=[^>]*\bsrc\s*=\s*["\'][^"\']*i18n\.js[^"\']*["\'])'
    r'(?=[^>]*\bdata-rel\s*=\s*["\']([^"\']+)["\'])'
    r'[^>]*>',
    re.IGNORECASE,
)

I18N_PATTERN = re.compile(
    r'data-i18n\s*=\s*["\']([^"\']+)["\']',
    re.IGNORECASE,
)


# ============================================================
# Git
# ============================================================

def get_repo_root():
    return Path(
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


def get_current_branch():
    return subprocess.check_output(
        [
            "git",
            "-C",
            str(Path(__file__).resolve().parent),
            "branch",
            "--show-current",
        ],
        text=True,
    ).strip()


# ============================================================
# HTML
# ============================================================

def get_i18n_info(html_file):
    """
    Return the i18n directory and all data-i18n keys
    used by an HTML file.
    """

    content = html_file.read_text(encoding="utf-8")

    matches = I18N_SCRIPT_PATTERN.findall(content)

    if not matches:
        #print(
        #    f"[WARNING] No i18n.js script with data-rel found: "
        #    f"{html_file.relative_to(ROOT_DIR)}"
        #)
        #return None, set()
        relative_directory = "."
    elif len(matches) > 1:
        print(
            f"[WARNING] Multiple i18n.js scripts found: "
            f"{html_file.relative_to(ROOT_DIR)}"
        )
        relative_directory = matches[0]
    else:
        # Use the first matching data-rel.
        relative_directory = matches[0]

    i18n_directory = (
        html_file.parent / relative_directory / "i18n"
    ).resolve()

    keys = set(I18N_PATTERN.findall(content))

    return i18n_directory, keys


# ============================================================
# JSON
# ============================================================

def load_json(json_file):
    """Load a translation JSON file."""

    try:
        with json_file.open("r", encoding="utf-8") as file:
            data = json.load(file)

    except json.JSONDecodeError as error:
        print(f"[ERROR] Invalid JSON: {json_file}")
        print(f"        {error}")
        return None

    if not isinstance(data, dict):
        print(f"[ERROR] Expected an object in: {json_file}")
        return None

    return data


def create_translation_file(json_file):
    """Create an empty translation JSON file."""

    print(f"[CREATE] {json_file.relative_to(ROOT_DIR)}")

    with json_file.open(
        "w",
        encoding="utf-8",
        newline="\n",
    ) as file:
        json.dump(
            {},
            file,
            ensure_ascii=False,
            indent=4,
        )
        file.write("\n")


def update_translation_file(json_file, required_keys):
    """
    Add missing translation keys to a JSON file.

    Returns the number of keys added.
    """

    data = load_json(json_file)

    if data is None:
        data = {}

    missing = sorted(
        key
        for key in required_keys
        if key not in data
    )

    if not missing:
        return 0

    print(f"[UPDATE] {json_file.relative_to(ROOT_DIR)}")

    for key in missing:
        print(f"         + {key}")
        data[key] = PLACEHOLDER

    with json_file.open(
        "w",
        encoding="utf-8",
        newline="\n",
    ) as file:
        json.dump(
            data,
            file,
            ensure_ascii=False,
            indent=4,
        )
        file.write("\n")

    return len(missing)


# ============================================================
# Main
# ============================================================

def main():

    global ROOT_DIR

    ROOT_DIR = get_repo_root()

    branch = get_current_branch()

    if branch != "pages":
        print(f"[ERROR] Current branch is '{branch}', not 'pages'.")
        print("       Checkout the pages branch before running this script.")
        sys.exit(1)

    print(f"Repository: {ROOT_DIR}")
    print(f"Branch:     {branch}")
    print(f"Languages:  {', '.join(LANGUAGES)}")
    print()

    html_files = sorted(ROOT_DIR.rglob("*.html"))

    # Don't accidentally scan generated files or dependencies.
    html_files = [
        path
        for path in html_files
        if ".git" not in path.parts and ".template.html" not in path.name
    ]

    print(f"Found {len(html_files)} HTML files.")
    print()

    # --------------------------------------------------------
    # Collect keys per HTML directory
    # --------------------------------------------------------

    keys_by_directory = {}

    for html_file in html_files:

        i18n_directory, keys = get_i18n_info(html_file)

        if not keys:
            continue

        if i18n_directory is None:
            continue

        if i18n_directory not in keys_by_directory:
            keys_by_directory[i18n_directory] = set()

        keys_by_directory[i18n_directory].update(keys)

        print(
            f"[SCAN] {html_file.relative_to(ROOT_DIR)} "
            f"({len(keys)} keys)"
        )

    print()

    # --------------------------------------------------------
    # Update translation files
    # --------------------------------------------------------

    total_missing = 0
    total_files = 0
    total_created = 0

    for i18n_directory, required_keys in sorted(
        keys_by_directory.items()
    ):

        # Create the i18n directory if it doesn't exist.
        if not i18n_directory.exists():
            print(
                f"[CREATE] {i18n_directory.relative_to(ROOT_DIR)}"
            )
            i18n_directory.mkdir(
                parents=True,
                exist_ok=True,
            )

        # ----------------------------------------------------
        # Ensure every configured language file exists
        # ----------------------------------------------------

        for language in LANGUAGES:

            json_file = i18n_directory / f"{language}.json"

            if not json_file.exists():
                create_translation_file(json_file)
                total_created += 1

        print()

        # ----------------------------------------------------
        # Add missing keys to every language
        # ----------------------------------------------------

        for language in LANGUAGES:

            json_file = i18n_directory / f"{language}.json"

            added = update_translation_file(
                json_file,
                required_keys,
            )

            if added == 0:
                continue

            total_missing += added
            total_files += 1

            print(
                f"         Added {added} "
                f"placeholder(s)."
            )
            print()

    # --------------------------------------------------------
    # Summary
    # --------------------------------------------------------

    print("Done.")
    print(f"HTML directories scanned: {len(keys_by_directory)}")
    print(f"Translation files created: {total_created}")
    print(f"Translation files updated: {total_files}")
    print(f"Missing translations added: {total_missing}")

    # Missing keys were found and placeholders were added.
    # Return non-zero so CI reports that translations are missing.
    if total_missing > 0:
        print()
        print(
            f"[ERROR] {total_missing} missing translation(s) "
            "were added as placeholders."
        )


if __name__ == "__main__":
    main()