#!/usr/bin/env python3

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
from collections import defaultdict
from pathlib import Path


# ============================================================
# Configuration
# ============================================================

SECTIONS = [
    "CA",
    "CB",
    "CC",
    "CD",
    "CE",
    "CF",
    "CG",
    "CI",
    "GACV",
    "GCC",
    "GCG",
    "GED",
    "GIG",
    "GIN",
    "GSE",
    "GSH",
    "GSI",
    "GSN",
    "GSO",
]

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

DOWNLOADS_DIR = (
    GIT_DIR
    / "men.lu"
    / "downloads"
)

EXAMS_DIR = (
    GIT_DIR
    / "exams"
)

INVALID_DIR = (
    EXAMS_DIR
    / ".invalid"
)


# ============================================================
# Destination filename pattern
# ============================================================

FILENAME_PATTERN = re.compile(
    r"""
    ^
    (?P<prefix>.+?)
    _
    (?P<year>\d{4})
    _
    (?P<season>[A-Z]+)
    (?P<retry>_REP)?
    (?:
        _
        (?P<name>.+?)
    )?
    _
    (?P<type>DATA|CORRIGE|ENONCE|ORAL)
    \.(?P<extension>pdf|zip)
    $
    """,
    re.IGNORECASE | re.VERBOSE,
)


# ============================================================
# General helpers
# ============================================================


def normalize_text(value: str) -> str:
    value = value.replace("\u00a0", " ")
    value = re.sub(r"\s+", " ", value)
    return value.strip()


def normalize_for_matching(value: str) -> str:
    value = value.lower()

    replacements = {
        "ê": "e",
        "é": "e",
        "è": "e",
        "ë": "e",
    }

    for old, new in replacements.items():
        value = value.replace(old, new)

    return normalize_text(value)


# ============================================================
# Section helpers
# ============================================================

SECTION_NAMES = sorted(
    SECTIONS,
    key=len,
    reverse=True,
)


def is_section_token(token: str) -> bool:
    return normalize_text(token).upper() in SECTIONS


def split_sectionish_token(
    token: str,
) -> tuple[str, list[str]]:
    """
    Split a token into non-section text and section codes.

    Examples:

        CB-4LANG
            -> ("4LANG", ["CB"])

        CB-CI
            -> ("", ["CB", "CI"])

        GA3D-GSN-GSO
            -> ("", ["GA3D", "GSN", "GSO"])

        4LANG
            -> ("4LANG", [])
    """

    parts = [
        normalize_text(part)
        for part in re.split(r"[-,;]", token)
        if normalize_text(part)
    ]

    if not parts:
        return "", []

    section_parts = [
        part
        for part in parts
        if is_section_token(part)
    ]

    non_section_parts = [
        part
        for part in parts
        if not is_section_token(part)
    ]

    return (
        "-".join(non_section_parts),
        section_parts,
    )


def is_sectionish_token(
    token: str,
) -> bool:
    """
    Return True if the complete token consists only of known
    section codes.
    """

    token = normalize_text(token)

    parts = [
        normalize_text(part)
        for part in re.split(r"[-,;]", token)
        if normalize_text(part)
    ]

    if not parts:
        return False

    return all(
        is_section_token(part)
        for part in parts
    )


def contains_section_metadata(
    value: str,
) -> bool:
    """
    Check whether a value contains at least one known section.
    """

    upper = value.upper()

    for section in SECTION_NAMES:
        if re.search(
            rf"(?<![A-Z0-9]){re.escape(section)}"
            rf"(?![A-Z0-9])",
            upper,
        ):
            return True

    return False


def remove_trailing_section_metadata(
    prefix: str,
) -> str:
    """
    Remove section metadata from the RIGHT side of a prefix.

    Examples:

        FRANC_CB_CC_CD_CE_CF_CG
            -> FRANC

        ANGLA_CB-CI
            -> ANGLA

        LATIN_CA_CB_CC_CD_CE_CF_CG_CI
            -> LATIN

        ESG_ALLEM_GA3D, GACV, GIG, GIN, GSE, GSN
            -> ESG_ALLEM

        CHINO_CB-4LANG
            -> CHINO_4LANG

        SOCIO_FR
            -> SOCIO_FR
    """

    value = normalize_text(prefix)

    parts = [
        normalize_text(part)
        for part in value.split("_")
        if normalize_text(part)
    ]

    trailing_count = 0
    preserved_suffix: list[str] = []

    for part in reversed(parts):
        cleaned, sections = split_sectionish_token(part)

        if not sections:
            break

        trailing_count += 1

        # Preserve non-section text, e.g. CB-4LANG -> 4LANG.
        if cleaned:
            preserved_suffix.append(cleaned)

    base_parts = parts[
        :len(parts) - trailing_count
    ]

    base_parts.extend(
        reversed(preserved_suffix)
    )

    return "_".join(
        part
        for part in base_parts
        if part
    ).strip(" _,-;")


def strip_section_suffix(
    prefix: str,
) -> str:
    """
    Remove organisation and section metadata.

    The section ALWAYS comes from the directory.
    """

    value = normalize_text(prefix)

    # Remove trailing section metadata.
    value = remove_trailing_section_metadata(
        value
    )

    # Remove trailing organisation marker.
    parts = [
        normalize_text(part)
        for part in value.split("_")
        if normalize_text(part)
    ]

    if (
        len(parts) > 1
        and parts[-1].upper() in {
            "ESG",
            "ESC",
            "EGS",
        }
    ):
        parts.pop()

    value = "_".join(parts)

    # Remove an organisation prefix.
    parts = [
        normalize_text(part)
        for part in value.split("_")
        if normalize_text(part)
    ]

    if (
        len(parts) > 1
        and parts[0].upper() in {
            "ESG",
            "ESC",
            "EGS",
        }
    ):
        parts.pop(0)

    return normalize_text(
        "_".join(parts).strip(" _,-;")
    )


# ============================================================
# Subject normalization
# ============================================================


def normalize_subject(
    subject_with_description: str,
) -> str:
    """
    Convert the raw subject into the destination subject.
    """

    value = normalize_text(
        subject_with_description
    )

    match = re.search(
        r"\(\s*([^)]*?)\s*\)",
        value,
    )

    code = (
        normalize_text(match.group(1))
        if match
        else None
    )

    base = normalize_text(
        re.sub(
            r"\s*\([^)]*\)",
            "",
            value,
        )
    )

    base_upper = base.upper()

    # --------------------------------------------------------
    # MATHE / MATIN
    # --------------------------------------------------------

    if base_upper == "MATHE" or base_upper == "MATIN":

        if code:
            code_upper = code.upper()

            if code_upper == "MATH1":
                return "MATHE1"

            if code_upper == "MATH2":
                return "MATHE2"

            if code_upper == "ANALY":
                return "MATHE1"

            if code_upper == "ANA":
                return "MATHE1"

            if code_upper == "STRUC":
                return "MATHE2"

            if code_upper == "INFOR":
                return "INFOR"

        return "MATHE"

    return base.upper().strip(" _,-;").replace("_ESC", "").replace("_ESG", "")


# ============================================================
# Name classification
# ============================================================


def classify_name(
    raw_subject: str,
) -> str | None:
    """
    Determine the optional destination name.
    """

    value = normalize_for_matching(
        raw_subject
    )

    match = re.search(
        r"\(\s*([^)]*?)\s*\)",
        value,
    )

    code = (
        normalize_text(match.group(1))
        if match
        else ""
    )

    code_upper = code.upper()

    # Explicit codes.
    if "TXINC" in code_upper:
        return "TXINC"

    if "TXCON" in code_upper:
        return "TXCON"

    if "DISLI" in code_upper:
        return "DISSERTATION"

    if "ANTXT" in code_upper:
        return "ANALYSE"

    if "ANLTI" in code_upper:
        return "ANALYSE"

    # Keyword matching.
    if "inconnu" in value:
        return "TXINC"

    if "connu" in value:
        return "TXCON"

    if "dissertation" in value:
        return "DISSERTATION"

    if "analyse" in value:
        return "ANALYSE"

    # Parenthesized short codes.
    if code:
        code_clean = re.sub(
            r"[^A-Z0-9]",
            "",
            code_upper,
        )

        descriptive_codes = {
            "ALLEMAND",
            "ANGLAIS",
            "MATHEMATIQUES",
            "MATHEMATIQUE",
            "MATHS",
            "FRANCAIS",
            "FRANÇAIS",
        }

        if (
            code_clean
            and code_clean not in descriptive_codes
            and len(code_clean) <= 12
        ):
            if code_clean not in {
                "MATH1",
                "MATH2",
                "ANALY",
                "STRUC",
                "INFOR",
            }:
                return code_clean

    return None


# ============================================================
# Version classification
# ============================================================


def classify_version(
    stem: str,
) -> str | None:
    """
    "(2)" and TRAD are VF.
    """

    value = normalize_for_matching(stem)

    if re.search(
        r"\(\s*2\s*\)",
        value,
    ):
        return "VF"

    if re.search(
        r"\btrad\b",
        value,
    ):
        return "VF"

    return None


def resolve_versions(
    files: list[dict],
) -> None:
    """
    If a VF counterpart exists, an otherwise unversioned file
    becomes VD.

    Examples:

        file.pdf
        file (2).pdf

            normal -> VD
            (2)    -> VF

        file.pdf
        file TRAD.pdf

            normal -> VD
            TRAD   -> VF
    """

    groups: dict[
        tuple,
        list[dict],
    ] = defaultdict(list)

    for file in files:
        key = (
            file["section"],
            file["subject"],
            file["year"],
            file["destination_season"],
            file["destination_retry"],
            file["name"],
            file["oral"],
            file["extension"],
        )

        groups[key].append(file)

    for group in groups.values():

        has_vf = any(
            file["version"] == "VF"
            for file in group
        )

        for file in group:
            if file["version"] == "VF":
                file["destination_version"] = "VF"

        if has_vf:
            for file in group:
                if file["version"] is None:
                    file["destination_version"] = "VD"

        else:
            for file in group:
                if file["version"] is None:
                    file["destination_version"] = None


# ============================================================
# Oral detection
# ============================================================


def is_oral(
    value: str,
) -> bool:
    return bool(
        re.search(
            r"\boral\b",
            normalize_for_matching(value),
        )
    )


# ============================================================
# Season classification
# ============================================================


SEASON_RETRY_PATTERN = re.compile(
    r"""
    (?:
        ^
        |
        [^a-z0-9]
    )
    (?:
        repechage
        |
        epechage
        |
        epêchage
        |
        rep
        |
        rpêchage
    )
    (?:
        $
        |
        [^a-z0-9]
    )
    """,
    re.IGNORECASE | re.VERBOSE,
)


def classify_season(
    suffix: str,
) -> tuple[str | None, bool]:
    """
    Return:

        (season, retry)

    Rules:

        septembre / octobre / automne
            -> SEPT

        juin / mai / été
            -> ETE

        explicit retry
            -> retry=True

        juin_rep
            -> ETE + retry=True

    IMPORTANT:

        juin is NOT automatically ETE_REP.

        The special mai/juin rule is handled later by
        resolve_seasons(), and only applies when the two files
        would otherwise collide.
    """

    value = normalize_for_matching(
        suffix
    )

    retry = bool(
        SEASON_RETRY_PATTERN.search(value)
    )

    if "ajournement" in value:
        return "AJO", retry

    if any(
        keyword in value
        for keyword in (
            "septembre",
            "octobre",
            "automne",
            "auomne",
        )
    ):
        return "SEPT", retry

    if any(
        keyword in value
        for keyword in (
            "juin",
            "mai",
            "ete",
        )
    ):
        return "ETE", retry

    if retry:
        return "ETE", True

    return None, False


# ============================================================
# Input parsing
# ============================================================


SEASON_SEARCH_PATTERN = re.compile(
    r"""
    (?:
        septembre
        |
        octobre
        |
        automne
        |
        auomne
        |
        juin
        |
        mai
        |
        été
        |
        ete
        |
        ajournement
        |
        repechage
        |
        epechage
        |
        epêchage
        |
        rpêchage
    )
    """,
    re.IGNORECASE | re.VERBOSE,
)


MODE_PATTERN = re.compile(
    r"""
    (?:
        ^
        |
        [_\s]
    )
    (?P<mode>écrit|ecrit|oral)
    (?=
        [_\s]
        |
        $
    )
    """,
    re.IGNORECASE | re.VERBOSE,
)


def split_input_stem(
    stem: str,
) -> tuple[str, str, str]:
    """
    Split an input filename into:

        prefix
        mode
        suffix

    If there is no explicit mode, assume ecrit.
    """

    stem = normalize_text(stem)

    # Explicit mode.
    mode_matches = list(
        MODE_PATTERN.finditer(stem)
    )

    if mode_matches:
        match = mode_matches[-1]

        prefix = stem[
            :match.start()
        ].strip(" _")

        suffix = stem[
            match.end():
        ].strip(" _")

        return (
            prefix,
            match.group("mode").lower(),
            suffix,
        )

    # No explicit mode. Find the season/retry portion.
    season_matches = list(
        SEASON_SEARCH_PATTERN.finditer(stem)
    )

    if season_matches:
        match = season_matches[-1]

        prefix = stem[
            :match.start()
        ].strip(" _")

        suffix = stem[
            match.start():
        ].strip(" _")

        return (
            prefix,
            "ecrit",
            suffix,
        )

    # No mode and no season.
    return (
        stem,
        "ecrit",
        "",
    )


def parse_input_file(
    path: Path,
    section: str,
    year: int,
) -> dict:

    extension = (
        path.suffix
        .lower()
        .lstrip(".")
    )

    if extension not in {
        "pdf",
        "zip",
    }:
        raise ValueError(
            f"Unsupported extension: .{extension}"
        )

    prefix, mode, suffix = split_input_stem(
        path.stem
    )

    if not prefix:
        raise ValueError(
            "Could not determine subject prefix"
        )

    # Subject.
    subject_with_description = (
        strip_section_suffix(prefix)
    )

    if not subject_with_description:
        raise ValueError(
            f"Could not determine subject "
            f"from '{prefix}'"
        )

    subject = normalize_subject(
        subject_with_description
    )

    if not subject:
        raise ValueError(
            f"Could not normalize subject "
            f"from '{subject_with_description}'"
        )

    # Name.
    name = classify_name(
        subject_with_description
    )

    # Oral.
    oral = (
        mode == "oral"
        or is_oral(prefix)
    )

    # Season.
    season, retry = classify_season(
        suffix
    )

    if season is None:
        raise ValueError(
            f"Could not determine season "
            f"from '{suffix}'"
        )

    # Version.
    version = classify_version(
        path.stem
    )

    return {
        "path": path,
        "section": section.upper(),
        "year": year,
        "subject": subject,
        "subject_with_description": (
            subject_with_description
        ),
        "name": name,
        "season": season,
        "retry": retry,
        "oral": oral,
        "version": version,
        "extension": extension,
    }


# ============================================================
# Season disambiguation
# ============================================================


def is_mai_file(
    file: dict,
) -> bool:
    """
    Determine whether the original filename explicitly contains
    "mai".
    """

    stem = normalize_for_matching(
        file["path"].stem
    )

    return bool(
        re.search(
            r"\bmai\b",
            stem,
        )
    )


def is_juin_file(
    file: dict,
) -> bool:
    """
    Determine whether the original filename explicitly contains
    "juin".
    """

    stem = normalize_for_matching(
        file["path"].stem
    )

    return bool(
        re.search(
            r"\bjuin\b",
            stem,
        )
    )


def season_collision_key(
    file: dict,
) -> tuple:
    """
    Return the parts of a destination that are identical for
    ordinary May and June files.

    Season and retry are deliberately excluded.

    If a May file and a June file have the same key, they would
    otherwise produce the same ETE destination.
    """

    return (
        file["section"],
        file["subject"],
        file["year"],
        file["name"],
        file["oral"],
        file["extension"],
        file.get("version"),
    )


def resolve_seasons(
    files: list[dict],
) -> None:
    """
    Resolve summer exam seasons.

    Rules:

        1. June is normally ETE.

        2. May is normally ETE.

        3. If a May file and a June file would produce the SAME
           destination, then:
           
               May  -> ETE
               June -> ETE_REP

        4. If there is no May/June collision, June remains ETE.

        5. Explicit retry markers such as:
           
               juin_rep
               juin repêchage
               repechage

           always remain ETE_REP.

        6. SEPT retry becomes SEPT_REP.

    This means:

        subject_mai.pdf
        subject_juin.pdf

    only become:

        ..._ETE_ENONCE.pdf
        ..._ETE_REP_ENONCE.pdf

    when they actually collide.
    """

    # --------------------------------------------------------
    # First assign the normal destination season.
    # --------------------------------------------------------

    for file in files:

        file["destination_season"] = (
            file["season"]
        )

        file["destination_retry"] = (
            file["retry"]
        )

    # --------------------------------------------------------
    # Find May/June pairs which would actually collide.
    #
    # We group by everything except season/retry.
    # --------------------------------------------------------

    groups: dict[
        tuple,
        list[dict],
    ] = defaultdict(list)

    for file in files:

        # Only ordinary ETE files participate in the special
        # May/June rule.
        if (
            file["season"] == "ETE"
            and not file["retry"]
        ):
            groups[
                season_collision_key(file)
            ].append(file)

    # --------------------------------------------------------
    # Apply the special rule only when BOTH May and June exist
    # in the same destination group.
    # --------------------------------------------------------

    for group in groups.values():

        mai_files = [
            file
            for file in group
            if is_mai_file(file)
        ]

        juin_files = [
            file
            for file in group
            if is_juin_file(file)
        ]

        if not mai_files or not juin_files:
            continue

        # There is an actual May/June destination collision.
        #
        # May stays ETE.
        for file in mai_files:
            file["destination_season"] = "ETE"
            file["destination_retry"] = False

        # June becomes ETE_REP.
        for file in juin_files:
            file["destination_season"] = "ETE"
            file["destination_retry"] = True


# ============================================================
# Destination
# ============================================================


def make_destination_name(
    file: dict,
) -> str:

    section = file["section"]
    subject = file["subject"]
    year = file["year"]

    season = file[
        "destination_season"
    ]

    retry = file[
        "destination_retry"
    ]

    name = file["name"]

    version = file.get(
        "destination_version"
    )

    extension = file["extension"]

    result = (
        f"{section}_{subject}_{year}_{season}"
    )

    if retry:
        result += "_REP"

    if name:
        result += f"_{name}"

    if version:
        result += f"_{version}"

    if file["oral"]:
        result += "_ORAL"
    else:
        result += "_ENONCE"

    return (
        f"{result}.{extension}"
    )


def make_destination(
    file: dict,
) -> Path:

    section = file["section"]
    subject = file["subject"]

    destination_dir = (
        EXAMS_DIR
        / section
        / f"{section}_{subject}"
    )

    return (
        destination_dir
        / make_destination_name(file)
    )


# ============================================================
# Invalid file handling
# ============================================================


def invalid_destination(
    source: Path,
) -> Path:
    """
    Preserve the complete path relative to DOWNLOADS_DIR.

    Example:

        DOWNLOADS_DIR / ESC / 2023 / CB / file.pdf

    becomes:

        EXAMS_DIR / .invalid / ESC / 2023 / CB / file.pdf
    """

    try:
        relative = source.relative_to(
            DOWNLOADS_DIR
        )

    except ValueError:
        # This should not normally happen.
        relative = Path(
            source.name
        )

    return (
        INVALID_DIR
        / relative
    )


def copy_invalid_file(
    source: Path,
    dry_run: bool,
) -> bool:
    """
    Copy an invalid/problematic source into .invalid while
    preserving its path relative to DOWNLOADS_DIR.

    Returns True on success.
    """

    destination = invalid_destination(
        source
    )

    if dry_run:

        print(
            f"[INVALID] {source}"
        )
        print(
            f"        -> {destination}"
        )

        return True

    try:

        destination.parent.mkdir(
            parents=True,
            exist_ok=True,
        )

        shutil.copy2(
            source,
            destination,
        )

    except OSError as exc:

        print(
            "[ERROR] Could not copy invalid file:"
        )
        print(
            f"        {source}"
        )
        print(
            f"        -> {destination}"
        )
        print(
            f"        {exc}"
        )

        return False

    print(
        f"[INVALID] {source}"
    )
    print(
        f"        -> {destination}"
    )

    return True


# ============================================================
# Indexing
# ============================================================


def index_files(
    invalid_sources: set[Path],
) -> tuple[list[dict], int]:

    indexed: list[dict] = []
    skipped = 0

    for institution in (
        "ESG",
        "ESC",
    ):

        institution_dir = (
            DOWNLOADS_DIR
            / institution
        )

        if not institution_dir.is_dir():
            continue

        for year_dir in sorted(
            institution_dir.iterdir()
        ):

            if not year_dir.is_dir():
                continue

            if not year_dir.name.isdigit():
                continue

            year = int(
                year_dir.name
            )

            for section in SECTIONS:

                section_dir = (
                    year_dir
                    / section
                )

                if not section_dir.is_dir():
                    continue

                # The directory is authoritative.
                #
                # The filename never determines the section.

                for path in sorted(
                    section_dir.iterdir()
                ):

                    if not path.is_file():
                        continue

                    if path.suffix.lower() not in {
                        ".pdf",
                        ".zip",
                    }:
                        continue

                    try:

                        file = parse_input_file(
                            path,
                            section,
                            year,
                        )

                        indexed.append(
                            file
                        )

                    except ValueError as exc:

                        skipped += 1

                        invalid_sources.add(
                            path
                        )

                        print(
                            f"[SKIP] {path}"
                        )
                        print(
                            f"       {exc}"
                        )

    return indexed, skipped


# ============================================================
# Destination collision detection
# ============================================================


def find_destination_collisions(
    files: list[dict],
) -> dict[Path, list[dict]]:

    destinations: dict[
        Path,
        list[dict],
    ] = defaultdict(list)

    for file in files:

        destination = make_destination(
            file
        )

        destinations[
            destination
        ].append(file)

    return {
        destination: source_files
        for destination, source_files
        in destinations.items()
        if len(source_files) > 1
    }


# ============================================================
# Processing
# ============================================================


def process_file(
    file: dict,
    dry_run: bool,
) -> str:

    source = file["path"]

    destination = make_destination(
        file
    )

    # --------------------------------------------------------
    # Validate generated destination filename.
    # --------------------------------------------------------

    if not FILENAME_PATTERN.match(
        destination.name
    ):

        print(
            f"[ERROR] {source}"
        )
        print(
            "        Generated filename does not "
            "match destination pattern:"
        )
        print(
            f"        {destination.name}"
        )

        return "error"

    # --------------------------------------------------------
    # Already exists.
    # --------------------------------------------------------

    if destination.exists():

        print(
            f"[EXISTS] {destination}"
        )
        print(
            f"         Source: {source}"
        )

        return "exists"

    # --------------------------------------------------------
    # Dry run.
    # --------------------------------------------------------

    if dry_run:

        print(
            f"[COPY]   {source}"
        )
        print(
            f"      -> {destination}"
        )

        return "dry-run"

    # --------------------------------------------------------
    # Actual copy.
    # --------------------------------------------------------

    try:

        destination.parent.mkdir(
            parents=True,
            exist_ok=True,
        )

        shutil.copy2(
            source,
            destination,
        )

    except OSError as exc:

        print(
            f"[ERROR] {source}"
        )
        print(
            f"        Destination: "
            f"{destination}"
        )
        print(
            f"        {exc}"
        )

        return "error"

    print(
        f"[COPIED] {source}"
    )
    print(
        f"       -> {destination}"
    )

    return "copied"


# ============================================================
# Main
# ============================================================


def main() -> None:

    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--dry-run",
        action="store_true",
        help=(
            "Print what would happen without "
            "copying anything."
        ),
    )

    args = parser.parse_args()

    print(
        f"Git directory: {GIT_DIR}"
    )
    print(
        f"Input:         {DOWNLOADS_DIR}"
    )
    print(
        f"Destination:   {EXAMS_DIR}"
    )
    print(
        f"Invalid:       {INVALID_DIR}"
    )
    print(
        "Mode:          "
        + (
            "DRY RUN"
            if args.dry_run
            else "COPY"
        )
    )
    print()

    # --------------------------------------------------------
    # Collect all invalid/problematic source files here.
    #
    # A set prevents the same source from being copied to
    # .invalid more than once.
    # --------------------------------------------------------

    invalid_sources: set[Path] = set()

    # --------------------------------------------------------
    # 1. Index.
    # --------------------------------------------------------

    files, skipped = index_files(
        invalid_sources
    )

    print()
    print(
        f"Indexed {len(files)} file(s)."
    )
    print(
        f"Skipped {skipped} file(s)."
    )
    print()

    # --------------------------------------------------------
    # 2. Resolve seasons.
    #
    # IMPORTANT:
    #
    # This now happens BEFORE collision detection.
    #
    # A June file only becomes ETE_REP if a May file would
    # otherwise collide with it.
    # --------------------------------------------------------

    grouped: dict[
        tuple[str, str, int],
        list[dict],
    ] = defaultdict(list)

    for file in files:

        grouped[
            (
                file["section"],
                file["subject"],
                file["year"],
            )
        ].append(file)

    for group in grouped.values():
        resolve_seasons(group)

    # --------------------------------------------------------
    # 3. Resolve versions.
    # --------------------------------------------------------

    resolve_versions(
        files
    )

    # --------------------------------------------------------
    # 4. Detect destination collisions before copying.
    # --------------------------------------------------------

    collisions = find_destination_collisions(
        files
    )

    collision_sources: set[Path] = set()

    for destination, source_files in sorted(
        collisions.items(),
        key=lambda item: str(item[0]),
    ):

        print(
            "[ERROR] Destination collision:"
        )

        for file in source_files:

            print(
                f"        {file['path']}"
            )

            collision_sources.add(
                file["path"]
            )

            invalid_sources.add(
                file["path"]
            )

        print(
            f"        -> {destination}"
        )
        print()

    # --------------------------------------------------------
    # 5. Process valid files.
    # --------------------------------------------------------

    stats = defaultdict(int)

    for file in files:

        if file["path"] in collision_sources:

            stats["error"] += 1

            continue

        result = process_file(
            file,
            args.dry_run,
        )

        if result == "error":

            invalid_sources.add(
                file["path"]
            )

        stats[result] += 1

    # --------------------------------------------------------
    # 6. Copy every invalid/problematic file to .invalid.
    #
    # This includes:
    #
    #   - skipped files
    #   - destination collision files
    #   - files which failed during copying
    #
    # Their original filename and complete relative directory
    # structure are preserved.
    # --------------------------------------------------------

    invalid_copied = 0
    invalid_copy_errors = 0

    for source in sorted(
        invalid_sources,
    ):

        if copy_invalid_file(
            source,
            args.dry_run,
        ):

            invalid_copied += 1

        else:

            invalid_copy_errors += 1

    # --------------------------------------------------------
    # 7. Summary.
    # --------------------------------------------------------

    print()
    print("=" * 60)
    print("Summary")
    print("=" * 60)

    print(
        f"Indexed:    {len(files)}"
    )

    if args.dry_run:

        print(
            f"Would copy: {stats['dry-run']}"
        )

    else:

        print(
            f"Copied:     {stats['copied']}"
        )

    print(
        f"Existing:   {stats['exists']}"
    )

    print(
        f"Skipped:    {skipped}"
    )

    print(
        f"Errors:     {stats['error']}"
    )

    print(
        f"Invalid:    {invalid_copied}"
    )

    if invalid_copy_errors:

        print(
            f"Invalid errors: {invalid_copy_errors}"
        )


if __name__ == "__main__":
    main()
