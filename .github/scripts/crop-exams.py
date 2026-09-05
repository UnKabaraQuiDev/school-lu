#!/usr/bin/env python3

from __future__ import annotations

import argparse
import csv
import os
import re
import shutil
import subprocess
import sys
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass, field
from pathlib import Path
import traceback

import pymupdf

from PySide6.QtCore import (
    QEvent,
    QPoint,
    QPointF,
    QRectF,
    QSize,
    QStringListModel,
    Qt,
)
from PySide6.QtGui import (
    QColor,
    QImage,
    QPainter,
    QPen,
    QValidator,
    QImageWriter,
    QKeyEvent,
)
from PySide6.QtWidgets import (
    QAbstractItemView,
    QApplication,
    QCheckBox,
    QCompleter,
    QHBoxLayout,
    QHeaderView,
    QLabel,
    QLineEdit,
    QListWidget,
    QListWidgetItem,
    QMainWindow,
    QMessageBox,
    QProgressBar,
    QPushButton,
    QStyledItemDelegate,
    QSplitter,
    QTableWidget,
    QTableWidgetItem,
    QVBoxLayout,
    QWidget,
)

class WordCompleter(QCompleter):
    """
    Completer for a space-separated tag editor.

    QCompleter normally completes the whole line.  This subclass
    limits completion to the tag immediately around the cursor and
    then replaces only that tag, so completion works for every word.
    """

    def __init__(self, model, parent=None):
        super().__init__(model, parent)

        self.setCaseSensitivity(
            Qt.CaseSensitivity.CaseInsensitive
        )

        self.setFilterMode(
            Qt.MatchFlag.MatchStartsWith
        )

        self.setCompletionMode(
            QCompleter.CompletionMode.PopupCompletion
        )

        self._word_start = 0
        self._word_end = 0

    def _word_bounds(self, text: str, cursor: int) -> tuple[int, int]:
        start = cursor

        while start > 0 and not text[start - 1].isspace():
            start -= 1

        end = cursor

        while end < len(text) and not text[end].isspace():
            end += 1

        return start, end

    def splitPath(self, _path):
        editor = self.widget()

        if editor is None:
            return [_path]

        text = editor.text()
        cursor = editor.cursorPosition()

        start, end = self._word_bounds(
            text,
            cursor,
        )

        self._word_start = start
        self._word_end = end

        return [
            text[start:cursor]
        ]

    def pathFromIndex(self, index):
        editor = self.widget()

        if editor is None:
            return super().pathFromIndex(index)

        completion = super().pathFromIndex(index)

        text = editor.text()
        cursor = editor.cursorPosition()

        start, end = self._word_bounds(
            text,
            cursor,
        )

        self._word_start = start
        self._word_end = end

        return (
            text[:start]
            + completion
            + text[end:]
        )


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
TAGS_CACHE_PATH = EXAMS_DIR / ".tags-cache"

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

INDEX_HEADERS = [
    "Index",
    "Name",
    "PosX",
    "PosY",
    "Width",
    "Height",
    "Tags",
]

DEFAULT_OUTPUT_DPI = 72.0 * 2


@dataclass
class SubtractiveBox:
    x: float
    y: float
    width: float
    height: float

    @property
    def rect(self) -> QRectF:
        return QRectF(
            self.x,
            self.y,
            self.width,
            self.height,
        )


@dataclass
class Box:
    index: int
    name: str
    x: float
    y: float
    width: float
    height: float
    tags: list[str] = field(default_factory=list)
    subtractive: list[SubtractiveBox] = field(default_factory=list)

    @property
    def rect(self) -> QRectF:
        return QRectF(
            self.x,
            self.y,
            self.width,
            self.height,
        )


def is_hidden_path(path: Path) -> bool:
    try:
        relative = path.relative_to(EXAMS_DIR)
    except ValueError:
        return False

    return any(
        part.startswith(".")
        for part in relative.parts
    )


def find_pdfs() -> list[Path]:
    result = []

    for path in EXAMS_DIR.rglob("*"):
        if not path.is_file():
            continue

        if path.suffix.lower() != ".pdf":
            continue

        if is_hidden_path(path):
            continue

        result.append(path)

    return sorted(
        result,
        key=lambda p: str(p).lower(),
    )


def find_headless_jobs() -> list[tuple[Path, Path]]:
    """
    Find every directory containing an index.csv.

    Each index.csv is expected to belong to a PDF with the same
    stem as its parent directory.
    """
    jobs: list[tuple[Path, Path]] = []

    for index_path in EXAMS_DIR.rglob("index.csv"):
        if not index_path.is_file():
            continue

        if is_hidden_path(index_path):
            continue
        
        output_dir = index_path.parent
        
        if output_dir.is_symlink():
            continue

        pdf_path = output_dir.with_suffix(".pdf")

        if not pdf_path.is_file():
            print(
                f"[WARNING] No matching PDF for {index_path}: "
                f"expected {pdf_path}",
                flush=True,
            )
            continue

        jobs.append(
            (
                pdf_path,
                index_path,
            )
        )

    return sorted(
        jobs,
        key=lambda job: str(job[0]).lower(),
    )

# ----------------------------------------------------------------------
# Tags
# ----------------------------------------------------------------------

def normalize_tag(tag: str) -> str:
    """
    Normalize a single tag.

    Tags cannot contain whitespace.
    """
    return tag.strip()


def parse_tags(value: str | None) -> list[str]:
    """
    Parse the Tags CSV field.

    Tags are stored as SPACE-separated values.

    Example:

        "math physics linear-algebra"

    becomes:

        ["math", "physics", "linear-algebra"]
    """
    if not value:
        return []

    tags: list[str] = []

    for raw_tag in re.split(r"\s+", value.strip()):
        tag = normalize_tag(raw_tag)

        if tag and tag not in tags:
            tags.append(tag)

    return tags


def format_tags(tags: list[str]) -> str:
    """
    Format tags as SPACE-separated values.

    Example:

        ["math", "physics", "linear-algebra"]

    becomes:

        "math physics linear-algebra"
    """
    return " ".join(
        tag
        for tag in tags
        if tag
    )


def load_tag_cache() -> list[str]:
    """
    Load tags from EXAM_DIR/.tags-cache.

    The file uses .properties format with one tag per line:

        tag1=
        tag2=
        tag3=

    The property name is used as the tag.
    """
    if not TAGS_CACHE_PATH.exists():
        return []

    if not TAGS_CACHE_PATH.is_file():
        print(
            f"[WARNING] Tag cache is not a file: "
            f"{TAGS_CACHE_PATH}",
            flush=True,
        )
        return []

    tags: set[str] = set()

    try:
        with TAGS_CACHE_PATH.open(
            "r",
            encoding="utf-8",
        ) as f:
            for line in f:
                line = line.strip()

                if not line or line.startswith("#"):
                    continue

                if "=" not in line:
                    continue

                name, _comment = line.split(
                    "=",
                    1,
                )

                tag = normalize_tag(name)

                if tag:
                    tags.add(tag)

    except OSError as exc:
        print(
            f"[WARNING] Could not read tag cache "
            f"{TAGS_CACHE_PATH}: {exc}",
            flush=True,
        )

    return sorted(
        tags,
        key=str.lower,
    )


def ensure_tag_in_cache(tag: str) -> None:
    """
    Add a new tag to EXAM_DIR/.tags-cache.

    Tags are stored as .properties entries with an empty
    comment:

        tag=
    """
    tag = normalize_tag(tag)

    if not tag:
        return

    # A tag containing whitespace is invalid.
    if re.search(r"\s", tag):
        return

    existing_tags = set(load_tag_cache())

    if tag in existing_tags:
        return

    try:
        TAGS_CACHE_PATH.parent.mkdir(
            parents=True,
            exist_ok=True,
        )

        with TAGS_CACHE_PATH.open(
            "a",
            encoding="utf-8",
        ) as f:
            f.write(
                f"{tag}=\n"
            )

    except OSError as exc:
        print(
            f"[WARNING] Could not update tag cache "
            f"for '{tag}': {exc}",
            flush=True,
        )


def remove_tag_from_cache(tag: str) -> bool:
    """
    Remove one tag from EXAM_DIR/.tags-cache.

    Returns True when the cache file was successfully rewritten.
    The exact property-name match is removed; comments and unrelated
    properties are preserved.
    """
    tag = normalize_tag(tag)

    if not tag or re.search(r"\s", tag):
        return False

    if not TAGS_CACHE_PATH.exists():
        return False

    if not TAGS_CACHE_PATH.is_file():
        return False

    try:
        lines = TAGS_CACHE_PATH.read_text(
            encoding="utf-8"
        ).splitlines(keepends=True)

        filtered: list[str] = []
        removed = False

        for line in lines:
            stripped = line.strip()

            if (
                stripped
                and not stripped.startswith("#")
                and "=" in stripped
            ):
                name, _comment = stripped.split(
                    "=",
                    1,
                )

                if normalize_tag(name) == tag:
                    removed = True
                    continue

            filtered.append(line)

        if not removed:
            return False

        TAGS_CACHE_PATH.write_text(
            "".join(filtered),
            encoding="utf-8",
        )

        return True

    except OSError as exc:
        print(
            f"[WARNING] Could not remove tag "
            f"'{tag}' from cache "
            f"{TAGS_CACHE_PATH}: {exc}",
            flush=True,
        )

        return False


def register_box_tags(box: Box) -> None:
    for tag in box.tags:
        ensure_tag_in_cache(tag)


class TagValidator(QValidator):
    """
    Validate a space-separated list of tags.

    Each tag must not contain whitespace.
    Spaces are used to separate multiple tags.
    Commas are not allowed.
    """

    def validate(
        self,
        input_text: str,
        pos: int,
    ):
        if "," in input_text:
            return (
                QValidator.State.Invalid,
                input_text,
                pos,
            )

        return (
            QValidator.State.Acceptable,
            input_text,
            pos,
        )

class BoxTableLineEdit(QLineEdit):
    """
    Line edit used by the Name and Tags columns.

    Keyboard navigation:

        Tab:
            next editable field

        Shift+Tab:
            previous editable field

        Enter:
            same field, next row

        Shift+Enter:
            same field, previous row

    When navigation occurs, the destination field is selected
    completely.
    """

    def __init__(
        self,
        parent=None,
        table=None,
    ):
        super().__init__(parent)

        self.table = table

    def keyPressEvent(
        self,
        event: QKeyEvent,
    ) -> None:
        if self.table is None:
            super().keyPressEvent(event)
            return

        modifiers = event.modifiers()
        shift = bool(
            modifiers
            & Qt.KeyboardModifier.ShiftModifier
        )

        key = event.key()

        if key in (
            Qt.Key.Key_Tab,
            Qt.Key.Key_Backtab,
        ):
            self.finish_and_move(
                forward=not shift
            )
            event.accept()
            return

        if key in (
            Qt.Key.Key_Return,
            Qt.Key.Key_Enter,
        ):
            completer = self.completer()

            if (
                completer is not None
                and completer.popup().isVisible()
            ):
                index = completer.currentIndex()

                if index.isValid():
                    completion = (
                        completer.currentCompletion()
                    )

                    if completion:
                        text = self.text()
                        cursor = self.cursorPosition()

                        # Find the complete word currently
                        # being autocompleted.
                        start = text.rfind(
                            " ",
                            0,
                            cursor,
                        ) + 1

                        end = text.find(
                            " ",
                            cursor,
                        )

                        if end == -1:
                            end = len(text)

                        self.setText(
                            completion
                        )

                        self.setCursorPosition(
                            start + len(completion)
                        )

                        completer.popup().hide()

                        event.accept()
                        return

        if key in (
            Qt.Key.Key_Return,
            Qt.Key.Key_Enter,
        ):
            self.finish_and_move(
                forward=not shift,
                same_column=True,
            )
            event.accept()
            return

        super().keyPressEvent(event)

    def finish_and_move(
        self,
        forward: bool,
        same_column: bool = False,
    ) -> None:
        table = self.table

        if table is None:
            return

        row = table.currentRow()
        column = table.currentColumn()

        if column not in (
            1,
            6,
        ):
            return

        # Commit the current editor before moving.
        # self.editingFinished.emit()

        target = table.find_editable_cell(
            row=row,
            column=column,
            forward=forward,
            same_column=same_column,
        )

        if target is None:
            return

        target_row, target_column = target

        table.start_editing_cell(
            target_row,
            target_column,
        )

class BoxTableWidget(QTableWidget):
    """
    QTableWidget with custom keyboard navigation for
    editable Name and Tags fields.
    """

    EDITABLE_COLUMNS = (
        1,
        6,
    )

    def keyPressEvent(
        self,
        event: QKeyEvent,
    ) -> None:
        key = event.key()
        modifiers = event.modifiers()

        shift = bool(
            modifiers
            & Qt.KeyboardModifier.ShiftModifier
        )

        if key in (
            Qt.Key.Key_Return,
            Qt.Key.Key_Enter,
        ):
            row = self.currentRow()
            column = self.currentColumn()

            if (
                row >= 0
                and column in self.EDITABLE_COLUMNS
            ):
                # Commit/close whatever editor is currently active.
                self.closePersistentEditor(
                    self.item(row, column)
                )

                target = self.find_editable_cell(
                    row=row,
                    column=column,
                    forward=not shift,
                    same_column=True,
                )

                if target is not None:
                    target_row, target_column = target

                    self.setCurrentCell(
                        target_row,
                        target_column,
                    )

                    item = self.item(
                        target_row,
                        target_column,
                    )

                    if item is not None:
                        self.editItem(item)

                event.accept()
                return

        if key in (
            Qt.Key.Key_Tab,
            Qt.Key.Key_Backtab,
        ):
            row = self.currentRow()
            column = self.currentColumn()

            if (
                row >= 0
                and column in self.EDITABLE_COLUMNS
            ):
                target = self.find_editable_cell(
                    row=row,
                    column=column,
                    forward=not shift,
                    same_column=False,
                )

                if target is not None:
                    target_row, target_column = target

                    self.setCurrentCell(
                        target_row,
                        target_column,
                    )

                    item = self.item(
                        target_row,
                        target_column,
                    )

                    if item is not None:
                        self.editItem(item)

                event.accept()
                return

        super().keyPressEvent(event)

    def start_editing_cell(
        self,
        row: int,
        column: int,
    ) -> None:
        item = self.item(
            row,
            column,
        )

        if item is None:
            return

        self.setCurrentCell(
            row,
            column,
        )

        self.editItem(item)

        editor = self.findChild(
            BoxTableLineEdit
        )

        if editor is not None:
            editor.setFocus(
                Qt.FocusReason.OtherFocusReason
            )
            editor.selectAll()

    def find_editable_cell(
        self,
        row: int,
        column: int,
        forward: bool,
        same_column: bool = False,
    ) -> tuple[int, int] | None:
        if self.rowCount() == 0:
            return None

        if same_column:
            step = 1 if forward else -1

            target_row = row + step

            while (
                0
                <= target_row
                < self.rowCount()
            ):
                item = self.item(
                    target_row,
                    column,
                )

                if (
                    item is not None
                    and item.flags()
                    & Qt.ItemFlag.ItemIsEditable
                ):
                    return (
                        target_row,
                        column,
                    )

                target_row += step

            return None

        editable_columns = list(
            self.EDITABLE_COLUMNS
        )

        current_position = (
            editable_columns.index(column)
        )

        if forward:
            candidates = (
                [
                    (
                        row,
                        col,
                    )
                    for col in editable_columns[
                        current_position + 1:
                    ]
                ]
                + [
                    (
                        next_row,
                        col,
                    )
                    for next_row in range(
                        row + 1,
                        self.rowCount(),
                    )
                    for col in editable_columns
                ]
            )

        else:
            candidates = (
                [
                    (
                        row,
                        col,
                    )
                    for col in reversed(
                        editable_columns[
                            :current_position
                        ]
                    )
                ]
                + [
                    (
                        previous_row,
                        col,
                    )
                    for previous_row in range(
                        row - 1,
                        -1,
                        -1,
                    )
                    for col in reversed(
                        editable_columns
                    )
                ]
            )

        for target_row, target_column in candidates:
            item = self.item(
                target_row,
                target_column,
            )

            if item is None:
                continue

            if not (
                item.flags()
                & Qt.ItemFlag.ItemIsEditable
            ):
                continue

            return (
                target_row,
                target_column,
            )

        return None


class BoxTableDelegate(QStyledItemDelegate):
    """
    Delegate for editable Name and Tags fields.
    """

    def __init__(
        self,
        parent=None,
        get_tags=None,
    ):
        super().__init__(parent)

        self.get_tags = get_tags

    def createEditor(
        self,
        parent,
        option,
        index,
    ):
        table = self.parent()

        editor = BoxTableLineEdit(
            parent,
            table=table,
        )

        if index.column() == 6:
            editor.setValidator(
                TagValidator()
            )

            tags = []

            if callable(self.get_tags):
                tags = self.get_tags()

            model = QStringListModel(
                tags,
                editor,
            )

            completer = WordCompleter(
                model,
                editor,
            )

            editor.setCompleter(
                completer
            )

            def update_completion_prefix(_text):
                cursor = editor.cursorPosition()
                current_text = editor.text()

                start, _end = completer._word_bounds(
                    current_text,
                    cursor,
                )

                prefix = current_text[
                    start:cursor
                ]

                completer.setCompletionPrefix(
                    prefix
                )

                if prefix:
                    completer.complete()
                else:
                    completer.popup().hide()

            editor.textEdited.connect(
                update_completion_prefix
            )

        return editor

    def setEditorData(
        self,
        editor,
        index,
    ):
        editor.setText(
            index.data(
                Qt.ItemDataRole.DisplayRole
            )
            or ""
        )

        editor.selectAll()

    def setModelData(
        self,
        editor,
        model,
        index,
    ):
        model.setData(
            index,
            editor.text(),
            Qt.ItemDataRole.EditRole,
        )


# ----------------------------------------------------------------------
# PDF loading / saving
# ----------------------------------------------------------------------


def output_dir_for(pdf_path: Path) -> Path:
    return pdf_path.with_suffix("")


def load_boxes(pdf_path: Path) -> list[Box]:
    output_dir = output_dir_for(pdf_path)
    index_path = output_dir / "index.csv"

    if not index_path.exists():
        return []

    positives: dict[int, Box] = {}
    subtractives: dict[
        int,
        list[SubtractiveBox],
    ] = {}

    with index_path.open(
        "r",
        encoding="utf-8",
        newline="",
    ) as f:
        reader = csv.DictReader(f)

        for row in reader:
            raw_index = row.get(
                "Index",
                "",
            ).strip()

            if not raw_index:
                continue

            try:
                index = int(raw_index)
                x = float(row["PosX"])
                y = float(row["PosY"])
                width = float(row["Width"])
                height = float(row["Height"])
            except (
                ValueError,
                KeyError,
            ):
                continue

            if index < 0:
                parent_index = abs(index)

                subtractives.setdefault(
                    parent_index,
                    [],
                ).append(
                    SubtractiveBox(
                        x,
                        y,
                        width,
                        height,
                    )
                )

            else:
                # Backwards compatibility:
                #
                # Older index.csv files do not have a Tags column.
                # row.get() therefore returns None/"" and produces
                # an empty tag list.
                tags = parse_tags(
                    row.get("Tags")
                )

                positives[index] = Box(
                    index=index,
                    name=row.get(
                        "Name",
                        "",
                    ).strip(),
                    x=x,
                    y=y,
                    width=width,
                    height=height,
                    tags=tags,
                )

    boxes = []

    for index in sorted(positives):
        box = positives[index]

        box.subtractive = subtractives.get(
            index,
            [],
        )

        boxes.append(box)

    return boxes


def save_index(
    pdf_path: Path,
    boxes: list[Box],
) -> None:
    output_dir = output_dir_for(pdf_path)
    output_dir.mkdir(
        parents=True,
        exist_ok=True,
    )

    index_path = output_dir / "index.csv"

    with index_path.open(
        "w",
        encoding="utf-8",
        newline="",
    ) as f:
        writer = csv.writer(f)

        writer.writerow(
            INDEX_HEADERS
        )

        for box in boxes:
            writer.writerow(
                [
                    box.index,
                    box.name,
                    format_number(box.x),
                    format_number(box.y),
                    format_number(box.width),
                    format_number(box.height),
                    format_tags(box.tags),
                ]
            )

            for sub in box.subtractive:
                writer.writerow(
                    [
                        -box.index,
                        "",
                        format_number(sub.x),
                        format_number(sub.y),
                        format_number(sub.width),
                        format_number(sub.height),
                        "",
                    ]
                )


def format_number(
    value: float,
) -> str:
    if abs(
        value - round(value)
    ) < 1e-7:
        return str(
            int(round(value))
        )

    return (
        f"{value:.4f}"
        .rstrip("0")
        .rstrip(".")
    )


def clear_output_directory(
    pdf_path: Path,
) -> Path:
    output_dir = output_dir_for(pdf_path)

    output_dir.mkdir(
        parents=True,
        exist_ok=True,
    )

    for child in output_dir.iterdir():
        if child.is_file() and child.suffix.lower() == ".webp":
            child.unlink()

    return output_dir


# ----------------------------------------------------------------------
# Headless export
# ----------------------------------------------------------------------


def export_pdf_headless(
    pdf_path: Path,
    index_path: Path,
) -> None:
    print(
        f"[INFO] Starting: {pdf_path}",
        flush=True,
    )

    boxes = load_boxes(pdf_path)

    if not boxes:
        print(
            f"[WARNING] No boxes found in {index_path}",
            flush=True,
        )

    output_dir = clear_output_directory(
        pdf_path
    )

    save_index(
        pdf_path,
        boxes,
    )

    doc = None

    try:
        doc = pymupdf.open(
            pdf_path
        )

        output_scale = get_pdf_output_scale(
            doc
        )

        print(
            f"[INFO] {pdf_path.name}: "
            f"output scale = {output_scale:.4f} "
            f"({output_scale * 72:.1f} DPI)",
            flush=True,
        )

        page_infos = []

        y = 0.0
        gap = 20.0

        for page_number in range(
            len(doc)
        ):
            page = doc[page_number]

            page_infos.append(
                {
                    "number": page_number,
                    "x": 0.0,
                    "y": y,
                    "width": page.rect.width,
                    "height": page.rect.height,
                }
            )

            y += (
                page.rect.height
                + gap
            )

        for number, box in enumerate(
            boxes,
            start=1,
        ):
            output_path = (
                output_dir
                / f"{box.index}.webp"
            )

            print(
                f"[INFO]   {pdf_path.name}: "
                f"box {number}/{len(boxes)} "
                f"{box.index}: {box.name}",
                flush=True,
            )

            export_box(
                doc=doc,
                page_infos=page_infos,
                box=box,
                output_path=output_path,
                output_scale=output_scale,
            )

    finally:
        if doc is not None:
            doc.close()

    print(
        f"[INFO] Finished: {pdf_path}",
        flush=True,
    )


def run_headless(
    max_workers: int | None = None,
) -> int:
    if not EXAMS_DIR.exists():
        print(
            f"[ERROR] EXAMS_DIR does not exist: "
            f"{EXAMS_DIR}",
            flush=True,
        )
        return 1

    jobs = find_headless_jobs()

    if not jobs:
        print(
            f"[WARNING] No index.csv files found "
            f"under {EXAMS_DIR}",
            flush=True,
        )
        return 0

    if max_workers is None:
        max_workers = 1

    print(
        f"[INFO] Found {len(jobs)} indexed PDF(s)",
        flush=True,
    )

    print(
        f"[INFO] Using {max_workers} worker(s)",
        flush=True,
    )

    failures: list[
        tuple[Path, Exception]
    ] = []

    with ThreadPoolExecutor(
        max_workers=max_workers,
        thread_name_prefix="pdf-export",
    ) as executor:
        futures = {
            executor.submit(
                export_pdf_headless,
                pdf_path,
                index_path,
            ): pdf_path
            for pdf_path, index_path in jobs
        }

        for future in as_completed(
            futures
        ):
            pdf_path = futures[future]

            try:
                future.result()

            except Exception as exc:
                failures.append(
                    (
                        pdf_path,
                        exc,
                    )
                )

                print(
                    f"[ERROR] Failed: "
                    f"{pdf_path}: {exc}",
                    flush=True,
                )
                
                traceback.print_exc()
                return

    print(
        (
            f"[INFO] Batch finished: "
            f"{len(jobs) - len(failures)} succeeded, "
            f"{len(failures)} failed"
        ),
        flush=True,
    )

    if failures:
        print(
            "[ERROR] Failed files:",
            flush=True,
        )

        for pdf_path, exc in failures:
            print(
                f"[ERROR]   {pdf_path}: {exc}",
                flush=True,
            )

        return 1

    return 0


# ----------------------------------------------------------------------
# Cached image helpers
# ----------------------------------------------------------------------


def cached_image_path(
    pdf_path: Path,
    box_index: int,
) -> Path:
    return (
        output_dir_for(pdf_path)
        / f"{box_index}.webp"
    )


def find_cached_images(
    pdf_path: Path,
    boxes: list[Box],
) -> dict[int, QImage]:
    """
    Load all available exported WebP images.

    Returns only successfully loaded images.
    """
    result: dict[int, QImage] = {}

    for box in boxes:
        path = cached_image_path(
            pdf_path,
            box.index,
        )

        if not path.is_file():
            continue

        image = QImage(
            str(path)
        )

        if image.isNull():
            continue

        result[box.index] = image

    return result


# ----------------------------------------------------------------------
# Canvas
# ----------------------------------------------------------------------


class PdfCanvas(QWidget):
    """
    PDF viewer and box editor.

    Left mouse:
        - Click an existing box to select it.
        - Drag a control point to resize it.
        - Drag in an empty area to create a positive box.

    Right mouse:
        - Always creates a subtractive box.

    Middle mouse:
        - Pan.

    Wheel:
        - Normal wheel: vertical pan.
        - Shift + wheel: horizontal pan.
        - Ctrl + wheel: zoom.

    Delete:
        - Simulates clicking the MainWindow delete button.
    """

    box_changed = None
    selection_changed = None
    delete_requested = None

    HANDLE_SIZE = 8.0
    HANDLE_HIT_RADIUS = 10.0
    MIN_BOX_SIZE = 2.0

    def __init__(
        self,
        parent=None,
    ):
        super().__init__(parent)

        self.doc: pymupdf.Document | None = None
        self.pages: list[dict] = []

        self._render_cache: dict[
            tuple[int, float],
            QImage,
        ] = {}

        self.cached_mode = False
        self.cached_images: dict[
            int,
            QImage,
        ] = {}

        self.cached_layout: dict[
            int,
            QRectF,
        ] = {}

        self.zoom = 1.0
        self.offset_x = 0.0
        self.offset_y = 0.0

        self.document_width = 0.0
        self.document_height = 0.0

        self.boxes: list[Box] = []

        self.selected_box: Box | None = None
        self.selected_subtractive: (
            SubtractiveBox | None
        ) = None

        self.drag_button: (
            Qt.MouseButton | None
        ) = None

        self.drag_start = QPoint()
        self.drag_current = QPoint()

        self.resizing = False
        self.resize_target_type: (
            str | None
        ) = None
        self.resize_target_box: (
            Box | None
        ) = None
        self.resize_target_subtractive: (
            SubtractiveBox | None
        ) = None
        self.resize_handle: (
            str | None
        ) = None
        self.resize_original_rect = QRectF()

        self.pan_start = QPoint()
        self.pan_start_offset_x = 0.0
        self.pan_start_offset_y = 0.0

        self.setMouseTracking(True)
        self.setFocusPolicy(
            Qt.FocusPolicy.StrongFocus
        )
        self.setMinimumSize(
            QSize(500, 400)
        )

    # ------------------------------------------------------------------
    # Document / cached images
    # ------------------------------------------------------------------

    def set_document(
        self,
        path: Path,
        use_cached_images: bool = False,
    ) -> None:
        if self.doc is not None:
            self.doc.close()

        self.doc = None

        self.boxes = load_boxes(path)

        self.clear_selection()

        self._render_cache = {}
        self.pages.clear()
        self.cached_images.clear()
        self.cached_layout.clear()

        self.cached_mode = False

        if use_cached_images:
            cached_images = find_cached_images(
                path,
                self.boxes,
            )

            if cached_images:
                self.cached_mode = True
                self.cached_images = (
                    cached_images
                )

        if self.cached_mode:
            self.setup_cached_layout()
        else:
            self.doc = pymupdf.open(
                path
            )

            self.setup_pdf_layout()

        self.offset_x = 0.0
        self.offset_y = 0.0

        self.update()

    def setup_pdf_layout(self) -> None:
        self.pages.clear()

        if self.doc is None:
            return

        y = 0.0
        max_width = 0.0
        gap = 20.0

        for page_number in range(
            len(self.doc)
        ):
            page = self.doc[page_number]
            rect = page.rect

            page_info = {
                "number": page_number,
                "x": 0.0,
                "y": y,
                "width": rect.width,
                "height": rect.height,
            }

            self.pages.append(
                page_info
            )

            y += (
                rect.height
                + gap
            )

            max_width = max(
                max_width,
                rect.width,
            )

        if self.pages:
            self.document_width = max_width
            self.document_height = (
                self.pages[-1]["y"]
                + self.pages[-1]["height"]
            )
        else:
            self.document_width = 0.0
            self.document_height = 0.0

    def setup_cached_layout(self) -> None:
        """
        Arrange exported exercise images vertically.

        Cached images are box exports rather than full PDF pages,
        so they cannot reproduce the original PDF page coordinate
        system. They are therefore displayed as a read-only visual
        layout.

        The Box coordinates remain intact for selection and editing
        when returning to PDF mode.
        """
        self.cached_layout.clear()

        margin = 20.0
        gap = 20.0

        current_y = margin
        max_width = 0.0

        for box in self.boxes:
            image = self.cached_images.get(
                box.index
            )

            if image is None:
                continue

            width = max(
                1,
                image.width(),
            )

            height = max(
                1,
                image.height(),
            )

            rect = QRectF(
                margin,
                current_y,
                width,
                height,
            )

            self.cached_layout[
                box.index
            ] = rect

            current_y += (
                height + gap
            )

            max_width = max(
                max_width,
                width,
            )

        self.document_width = (
            max_width + margin * 2
        )

        self.document_height = (
            current_y
            if self.cached_layout
            else 0.0
        )

    def set_boxes(
        self,
        boxes: list[Box],
    ) -> None:
        self.boxes = boxes
        self.clear_selection()
        self.update()

    # ------------------------------------------------------------------
    # Selection
    # ------------------------------------------------------------------

    def clear_selection(self) -> None:
        changed = (
            self.selected_box is not None
            or self.selected_subtractive
            is not None
        )

        self.selected_box = None
        self.selected_subtractive = None

        if (
            changed
            and callable(self.selection_changed)
        ):
            self.selection_changed()

        self.update()

    def select_box(
        self,
        box: Box,
        pan_to_box: bool = False,
    ) -> None:
        changed = (
            self.selected_box is not box
            or self.selected_subtractive
            is not None
        )

        self.selected_box = box
        self.selected_subtractive = None

        if (
            changed
            and callable(self.selection_changed)
        ):
            self.selection_changed()

        if pan_to_box:
            self.pan_to_box(box)

        self.update()

    def select_subtractive(
        self,
        parent_box: Box,
        subtractive: SubtractiveBox,
    ) -> None:
        changed = (
            self.selected_subtractive
            is not subtractive
            or self.selected_box
            is not parent_box
        )

        self.selected_box = parent_box
        self.selected_subtractive = (
            subtractive
        )

        if (
            changed
            and callable(self.selection_changed)
        ):
            self.selection_changed()

        self.update()

    def selected_rect(
        self,
    ) -> QRectF | None:
        if (
            self.selected_subtractive
            is not None
        ):
            return self.selected_subtractive.rect

        if self.selected_box is not None:
            return self.selected_box.rect

        return None

    def find_item_at(
        self,
        point,
    ) -> tuple[
        str,
        Box | None,
        int | None,
    ]:
        point = QPointF(
            float(point[0]),
            float(point[1]),
        )

        if self.cached_mode:
            for box in reversed(
                self.boxes
            ):
                cached_rect = (
                    self.cached_layout.get(
                        box.index
                    )
                )

                if (
                    cached_rect is not None
                    and cached_rect.contains(
                        point
                    )
                ):
                    return (
                        "box",
                        box,
                        None,
                    )

            return (
                "none",
                None,
                None,
            )

        # Subtractive boxes first.
        for box in reversed(
            self.boxes
        ):
            for sub_index in range(
                len(box.subtractive) - 1,
                -1,
                -1,
            ):
                sub = box.subtractive[
                    sub_index
                ]

                if sub.rect.contains(
                    point
                ):
                    return (
                        "subtractive",
                        box,
                        sub_index,
                    )

        # Additive boxes.
        for box in reversed(
            self.boxes
        ):
            if box.rect.contains(
                point
            ):
                return (
                    "box",
                    box,
                    None,
                )

        return (
            "none",
            None,
            None,
        )

    # ------------------------------------------------------------------
    # Panning
    # ------------------------------------------------------------------

    def pan_to_box(
        self,
        box: Box,
    ) -> None:
        if self.cached_mode:
            target = self.cached_layout.get(
                box.index
            )

            if target is None:
                return
        else:
            target = box.rect

        target_center_x = (
            target.center().x()
            * self.zoom
        )

        target_center_y = (
            target.center().y()
            * self.zoom
        )

        self.offset_x = (
            target_center_x
            - self.width() / 2
        )

        self.offset_y = (
            target_center_y
            - self.height() / 2
        )

        self.clamp_offsets()
        self.update()

    # ------------------------------------------------------------------
    # Coordinate conversion
    # ------------------------------------------------------------------

    def canvas_to_document(
        self,
        point: QPoint,
    ) -> tuple[float, float]:
        return (
            (
                point.x()
                + self.offset_x
            )
            / self.zoom,
            (
                point.y()
                + self.offset_y
            )
            / self.zoom,
        )

    def document_to_canvas(
        self,
        x: float,
        y: float,
    ) -> QPoint:
        return QPoint(
            round(
                x * self.zoom
                - self.offset_x
            ),
            round(
                y * self.zoom
                - self.offset_y
            ),
        )

    def document_rect_to_canvas(
        self,
        rect: QRectF,
    ) -> QRectF:
        return QRectF(
            rect.x() * self.zoom
            - self.offset_x,
            rect.y() * self.zoom
            - self.offset_y,
            rect.width() * self.zoom,
            rect.height() * self.zoom,
        )

    # ------------------------------------------------------------------
    # Rendering
    # ------------------------------------------------------------------

    def render_page(
        self,
        page_number: int,
    ) -> QImage:
        if self.doc is None:
            return QImage()

        render_scale = max(
            1.0,
            min(
                self.zoom,
                2.0,
            ),
        )

        cache_key = (
            page_number,
            round(
                render_scale,
                2,
            ),
        )

        if cache_key in self._render_cache:
            return self._render_cache[
                cache_key
            ]

        page = self.doc[
            page_number
        ]

        matrix = pymupdf.Matrix(
            render_scale,
            render_scale,
        )

        pix = page.get_pixmap(
            matrix=matrix,
            alpha=False,
            colorspace=pymupdf.csRGB,
        )

        image = QImage(
            pix.samples,
            pix.width,
            pix.height,
            pix.stride,
            QImage.Format.Format_RGB888,
        ).copy()

        self._render_cache[
            cache_key
        ] = image

        if len(
            self._render_cache
        ) > 30:
            for key in list(
                self._render_cache
            )[:-20]:
                del self._render_cache[
                    key
                ]

        return image

    def paintEvent(
        self,
        event,
    ) -> None:
        painter = QPainter(self)

        painter.setRenderHint(
            QPainter.RenderHint.SmoothPixmapTransform
        )

        painter.fillRect(
            self.rect(),
            QColor("#303030"),
        )

        if self.cached_mode:
            self.paint_cached_images(
                painter
            )
        elif self.doc is not None:
            self.paint_pdf(
                painter
            )
        else:
            painter.setPen(
                Qt.GlobalColor.white
            )

            painter.drawText(
                self.rect(),
                Qt.AlignmentFlag.AlignCenter,
                "No PDF selected",
            )

        # In cached mode, the exported image itself is the content.
        # In PDF mode, draw the normal box overlays.
        if not self.cached_mode:
            self.paint_boxes(
                painter
            )

            selected_rect = (
                self.selected_rect()
            )

            if selected_rect is not None:
                self.draw_resize_handles(
                    painter,
                    self.document_rect_to_canvas(
                        selected_rect
                    ),
                )

        # Current drawing rectangle.
        if (
            not self.cached_mode
            and self.drag_button
            in (
                Qt.MouseButton.LeftButton,
                Qt.MouseButton.RightButton,
            )
            and not self.resizing
        ):
            start_x, start_y = (
                self.canvas_to_document(
                    self.drag_start
                )
            )

            current_x, current_y = (
                self.canvas_to_document(
                    self.drag_current
                )
            )

            rect = QRectF(
                min(
                    start_x,
                    current_x,
                ),
                min(
                    start_y,
                    current_y,
                ),
                abs(
                    current_x
                    - start_x
                ),
                abs(
                    current_y
                    - start_y
                ),
            )

            canvas_rect = (
                self.document_rect_to_canvas(
                    rect
                )
            )

            if (
                self.drag_button
                == Qt.MouseButton.LeftButton
            ):
                painter.setPen(
                    QPen(
                        QColor("#00d26a"),
                        2,
                        Qt.PenStyle.DashLine,
                    )
                )
                painter.setBrush(
                    QColor(
                        0,
                        210,
                        106,
                        40,
                    )
                )
            else:
                painter.setPen(
                    QPen(
                        QColor("#ff4040"),
                        2,
                        Qt.PenStyle.DashLine,
                    )
                )
                painter.setBrush(
                    QColor(
                        255,
                        0,
                        0,
                        40,
                    )
                )

            painter.drawRect(
                canvas_rect
            )

    def paint_pdf(
        self,
        painter: QPainter,
    ) -> None:
        for page_info in self.pages:
            page_number = page_info[
                "number"
            ]

            page_x = (
                page_info["x"]
                * self.zoom
                - self.offset_x
            )

            page_y = (
                page_info["y"]
                * self.zoom
                - self.offset_y
            )

            page_w = (
                page_info["width"]
                * self.zoom
            )

            page_h = (
                page_info["height"]
                * self.zoom
            )

            page_rect = QRectF(
                page_x,
                page_y,
                page_w,
                page_h,
            )

            if not page_rect.intersects(
                QRectF(self.rect())
            ):
                continue

            painter.fillRect(
                page_rect,
                Qt.GlobalColor.white,
            )

            image = self.render_page(
                page_number
            )

            painter.drawImage(
                page_rect,
                image,
            )

            painter.setPen(
                QPen(
                    QColor("#777777"),
                    1,
                )
            )

            painter.drawRect(
                page_rect
            )

    def paint_cached_images(
        self,
        painter: QPainter,
    ) -> None:
        for box in self.boxes:
            image = self.cached_images.get(
                box.index
            )

            rect = self.cached_layout.get(
                box.index
            )

            if image is None or rect is None:
                continue

            canvas_rect = (
                self.document_rect_to_canvas(
                    rect
                )
            )

            if not canvas_rect.intersects(
                QRectF(self.rect())
            ):
                continue

            painter.fillRect(
                canvas_rect,
                Qt.GlobalColor.white,
            )

            painter.drawImage(
                canvas_rect,
                image,
            )

            if (
                self.selected_box is box
                and self.selected_subtractive
                is None
            ):
                painter.setPen(
                    QPen(
                        QColor("#00ff80"),
                        3,
                    )
                )
            else:
                painter.setPen(
                    QPen(
                        QColor("#777777"),
                        1,
                    )
                )

            painter.setBrush(
                Qt.BrushStyle.NoBrush
            )

            painter.drawRect(
                canvas_rect
            )

            label_rect = QRectF(
                canvas_rect.x(),
                canvas_rect.y() - 22,
                max(
                    100,
                    canvas_rect.width(),
                ),
                22,
            )

            painter.setPen(
                Qt.PenStyle.NoPen
            )
            painter.setBrush(
                QColor("#00d26a")
            )

            painter.drawRect(
                label_rect
            )

            painter.setPen(
                Qt.GlobalColor.black
            )

            painter.drawText(
                label_rect,
                Qt.AlignmentFlag.AlignLeft
                | Qt.AlignmentFlag.AlignVCenter,
                (
                    f" {box.index}: "
                    f"{box.name}"
                ),
            )

    def paint_boxes(
        self,
        painter: QPainter,
    ) -> None:
        for box in self.boxes:
            rect = (
                self.document_rect_to_canvas(
                    box.rect
                )
            )

            is_selected = (
                self.selected_box is box
                and self.selected_subtractive
                is None
            )

            if is_selected:
                painter.setPen(
                    QPen(
                        QColor("#00ff80"),
                        3,
                    )
                )

                painter.setBrush(
                    QColor(
                        0,
                        210,
                        106,
                        35,
                    )
                )
            else:
                painter.setPen(
                    QPen(
                        QColor("#00d26a"),
                        2,
                    )
                )

                painter.setBrush(
                    Qt.BrushStyle.NoBrush
                )

            painter.drawRect(
                rect
            )

            label_rect = QRectF(
                rect.x(),
                rect.y() - 20,
                max(
                    60,
                    rect.width(),
                ),
                20,
            )

            painter.setPen(
                Qt.PenStyle.NoPen
            )

            painter.setBrush(
                QColor("#00d26a")
            )

            painter.drawRect(
                label_rect
            )

            painter.setPen(
                Qt.GlobalColor.black
            )

            painter.drawText(
                label_rect,
                Qt.AlignmentFlag.AlignLeft
                | Qt.AlignmentFlag.AlignVCenter,
                f" {box.index}: {box.name}",
            )

            for sub in box.subtractive:
                sub_rect = (
                    self.document_rect_to_canvas(
                        sub.rect
                    )
                )

                sub_selected = (
                    self.selected_box is box
                    and self.selected_subtractive
                    is sub
                )

                if sub_selected:
                    painter.setPen(
                        QPen(
                            QColor("#ff2020"),
                            3,
                        )
                    )

                    painter.setBrush(
                        QColor(
                            255,
                            0,
                            0,
                            90,
                        )
                    )
                else:
                    painter.setPen(
                        QPen(
                            QColor("#ff4040"),
                            2,
                        )
                    )

                    painter.setBrush(
                        QColor(
                            255,
                            0,
                            0,
                            60,
                        )
                    )

                painter.drawRect(
                    sub_rect
                )

    # ------------------------------------------------------------------
    # Resize handles
    # ------------------------------------------------------------------

    def handle_positions(
        self,
        canvas_rect: QRectF,
    ) -> dict[str, QPoint]:
        left = round(
            canvas_rect.left()
        )

        center_x = round(
            canvas_rect.center().x()
        )

        right = round(
            canvas_rect.right()
        )

        top = round(
            canvas_rect.top()
        )

        center_y = round(
            canvas_rect.center().y()
        )

        bottom = round(
            canvas_rect.bottom()
        )

        return {
            "nw": QPoint(
                left,
                top,
            ),
            "n": QPoint(
                center_x,
                top,
            ),
            "ne": QPoint(
                right,
                top,
            ),
            "e": QPoint(
                right,
                center_y,
            ),
            "se": QPoint(
                right,
                bottom,
            ),
            "s": QPoint(
                center_x,
                bottom,
            ),
            "sw": QPoint(
                left,
                bottom,
            ),
            "w": QPoint(
                left,
                center_y,
            ),
        }

    def draw_resize_handles(
        self,
        painter: QPainter,
        canvas_rect: QRectF,
    ) -> None:
        handles = self.handle_positions(
            canvas_rect
        )

        painter.setPen(
            QPen(
                QColor("#ffffff"),
                1,
            )
        )

        painter.setBrush(
            QColor("#008cff")
        )

        half = (
            self.HANDLE_SIZE / 2
        )

        for point in handles.values():
            handle_rect = QRectF(
                point.x() - half,
                point.y() - half,
                self.HANDLE_SIZE,
                self.HANDLE_SIZE,
            )

            painter.drawRect(
                handle_rect
            )

    def hit_test_handle(
        self,
        canvas_point: QPoint,
    ) -> str | None:
        if self.cached_mode:
            return None

        selected_rect = (
            self.selected_rect()
        )

        if selected_rect is None:
            return None

        canvas_rect = (
            self.document_rect_to_canvas(
                selected_rect
            )
        )

        handles = self.handle_positions(
            canvas_rect
        )

        for name, point in handles.items():
            dx = (
                canvas_point.x()
                - point.x()
            )

            dy = (
                canvas_point.y()
                - point.y()
            )

            if (
                dx * dx
                + dy * dy
                <= self.HANDLE_HIT_RADIUS
                * self.HANDLE_HIT_RADIUS
            ):
                return name

        return None

    def cursor_for_handle(
        self,
        handle: str | None,
    ) -> Qt.CursorShape:
        if handle in (
            "nw",
            "se",
        ):
            return Qt.CursorShape.SizeFDiagCursor

        if handle in (
            "ne",
            "sw",
        ):
            return Qt.CursorShape.SizeBDiagCursor

        if handle in (
            "n",
            "s",
        ):
            return Qt.CursorShape.SizeVerCursor

        if handle in (
            "e",
            "w",
        ):
            return Qt.CursorShape.SizeHorCursor

        return Qt.CursorShape.ArrowCursor

    # ------------------------------------------------------------------
    # Keyboard / mouse
    # ------------------------------------------------------------------

    def keyPressEvent(
        self,
        event,
    ) -> None:
        if (
            event.key()
            == Qt.Key.Key_Delete
        ):
            # PdfCanvas does not own the Delete button.
            # Ask MainWindow to simulate clicking it.
            if callable(
                self.delete_requested
            ):
                self.delete_requested()

            event.accept()
            return

        super().keyPressEvent(
            event
        )

    def mousePressEvent(
        self,
        event,
    ) -> None:
        button = event.button()
        position = (
            event.position().toPoint()
        )

        if (
            button
            == Qt.MouseButton.MiddleButton
        ):
            self.drag_button = button
            self.pan_start = position
            self.pan_start_offset_x = (
                self.offset_x
            )
            self.pan_start_offset_y = (
                self.offset_y
            )

            self.setCursor(
                Qt.CursorShape.ClosedHandCursor
            )

            return

        if self.cached_mode:
            if (
                button
                == Qt.MouseButton.LeftButton
            ):
                doc_point = (
                    self.canvas_to_document(
                        position
                    )
                )

                item_type, box, _sub = (
                    self.find_item_at(
                        doc_point
                    )
                )

                if (
                    item_type == "box"
                    and box is not None
                ):
                    self.select_box(
                        box,
                        pan_to_box=False,
                    )

            return

        # Right mouse always draws subtractive.
        if (
            button
            == Qt.MouseButton.RightButton
        ):
            self.drag_button = button
            self.drag_start = position
            self.drag_current = position
            self.resizing = False

            self.setCursor(
                Qt.CursorShape.CrossCursor
            )

            self.update()

            return

        if (
            button
            != Qt.MouseButton.LeftButton
        ):
            return

        handle = self.hit_test_handle(
            position
        )

        if handle is not None:
            self.resizing = True
            self.resize_handle = handle
            self.drag_button = button

            if (
                self.selected_subtractive
                is not None
            ):
                self.resize_target_type = (
                    "subtractive"
                )

                self.resize_target_box = (
                    self.selected_box
                )

                self.resize_target_subtractive = (
                    self.selected_subtractive
                )

                self.resize_original_rect = (
                    self.selected_subtractive.rect
                )

            elif self.selected_box is not None:
                self.resize_target_type = (
                    "box"
                )

                self.resize_target_box = (
                    self.selected_box
                )

                self.resize_target_subtractive = (
                    None
                )

                self.resize_original_rect = (
                    self.selected_box.rect
                )

            self.setCursor(
                self.cursor_for_handle(
                    handle
                )
            )

            return

        doc_point = (
            self.canvas_to_document(
                position
            )
        )

        item_type, box, sub = (
            self.find_item_at(
                doc_point
            )
        )

        if item_type != "none":
            if (
                item_type == "subtractive"
            ):
                if (
                    box is not None
                    and sub is not None
                    and 0 <= sub
                    < len(
                        box.subtractive
                    )
                ):
                    self.select_subtractive(
                        box,
                        box.subtractive[
                            sub
                        ],
                    )
            else:
                if box is not None:
                    self.select_box(
                        box
                    )

            self.drag_button = None
            self.resizing = False

            self.setCursor(
                Qt.CursorShape.ArrowCursor
            )

            return

        self.clear_selection()

        self.drag_button = button
        self.drag_start = position
        self.drag_current = position
        self.resizing = False

        self.setCursor(
            Qt.CursorShape.CrossCursor
        )

        self.update()

    def mouseMoveEvent(
        self,
        event,
    ) -> None:
        position = (
            event.position().toPoint()
        )

        if (
            self.drag_button
            == Qt.MouseButton.MiddleButton
        ):
            delta = (
                position
                - self.pan_start
            )

            self.offset_x = (
                self.pan_start_offset_x
                - delta.x()
            )

            self.offset_y = (
                self.pan_start_offset_y
                - delta.y()
            )

            self.clamp_offsets()
            self.update()

            return

        if (
            self.resizing
            and self.drag_button
            == Qt.MouseButton.LeftButton
        ):
            self.update_resize(
                position
            )

            return

        if self.drag_button in (
            Qt.MouseButton.LeftButton,
            Qt.MouseButton.RightButton,
        ):
            self.drag_current = position
            self.update()

            return

        handle = self.hit_test_handle(
            position
        )

        if handle is not None:
            self.setCursor(
                self.cursor_for_handle(
                    handle
                )
            )

            return

        doc_point = (
            self.canvas_to_document(
                position
            )
        )

        item_type, _box, _sub = (
            self.find_item_at(
                doc_point
            )
        )

        if item_type != "none":
            self.setCursor(
                Qt.CursorShape.PointingHandCursor
            )
        else:
            self.setCursor(
                Qt.CursorShape.ArrowCursor
            )

    def mouseReleaseEvent(
        self,
        event,
    ) -> None:
        button = event.button()

        if (
            button
            == Qt.MouseButton.MiddleButton
        ):
            self.drag_button = None

            self.setCursor(
                Qt.CursorShape.ArrowCursor
            )

            return

        if (
            button
            == Qt.MouseButton.LeftButton
        ):
            if self.resizing:
                self.resizing = False
                self.resize_handle = None
                self.resize_target_type = None
                self.resize_target_box = None
                self.resize_target_subtractive = None
                self.drag_button = None

                self.setCursor(
                    Qt.CursorShape.ArrowCursor
                )

                if callable(
                    self.box_changed
                ):
                    self.box_changed()

                self.update()

                return

        if button not in (
            Qt.MouseButton.LeftButton,
            Qt.MouseButton.RightButton,
        ):
            return

        if self.drag_button != button:
            return

        self.drag_current = (
            event.position().toPoint()
        )

        x1, y1 = (
            self.canvas_to_document(
                self.drag_start
            )
        )

        x2, y2 = (
            self.canvas_to_document(
                self.drag_current
            )
        )

        rect = QRectF(
            min(x1, x2),
            min(y1, y2),
            abs(x2 - x1),
            abs(y2 - y1),
        )

        self.drag_button = None

        self.setCursor(
            Qt.CursorShape.ArrowCursor
        )

        if (
            rect.width()
            < self.MIN_BOX_SIZE
            or rect.height()
            < self.MIN_BOX_SIZE
        ):
            self.update()
            return

        if (
            button
            == Qt.MouseButton.LeftButton
        ):
            self.create_positive_box(
                rect
            )
        else:
            self.add_subtractive_box(
                rect
            )

        self.update()

    # ------------------------------------------------------------------
    # Resize
    # ------------------------------------------------------------------

    def update_resize(
        self,
        canvas_position: QPoint,
    ) -> None:
        if self.resize_handle is None:
            return

        doc_x, doc_y = (
            self.canvas_to_document(
                canvas_position
            )
        )

        original = (
            self.resize_original_rect
        )

        left = original.left()
        right = original.right()
        top = original.top()
        bottom = original.bottom()

        handle = self.resize_handle

        if "w" in handle:
            left = doc_x

        if "e" in handle:
            right = doc_x

        if "n" in handle:
            top = doc_y

        if "s" in handle:
            bottom = doc_y

        new_left = min(
            left,
            right,
        )

        new_right = max(
            left,
            right,
        )

        new_top = min(
            top,
            bottom,
        )

        new_bottom = max(
            top,
            bottom,
        )

        if (
            new_right - new_left
            < self.MIN_BOX_SIZE
        ):
            if "w" in handle:
                new_left = (
                    new_right
                    - self.MIN_BOX_SIZE
                )
            else:
                new_right = (
                    new_left
                    + self.MIN_BOX_SIZE
                )

        if (
            new_bottom - new_top
            < self.MIN_BOX_SIZE
        ):
            if "n" in handle:
                new_top = (
                    new_bottom
                    - self.MIN_BOX_SIZE
                )
            else:
                new_bottom = (
                    new_top
                    + self.MIN_BOX_SIZE
                )

        new_rect = QRectF(
            new_left,
            new_top,
            new_right - new_left,
            new_bottom - new_top,
        )

        if (
            self.resize_target_type
            == "box"
        ):
            box = (
                self.resize_target_box
            )

            if box is not None:
                box.x = new_rect.x()
                box.y = new_rect.y()
                box.width = (
                    new_rect.width()
                )
                box.height = (
                    new_rect.height()
                )

        elif (
            self.resize_target_type
            == "subtractive"
        ):
            sub = (
                self.resize_target_subtractive
            )

            if sub is not None:
                sub.x = new_rect.x()
                sub.y = new_rect.y()
                sub.width = (
                    new_rect.width()
                )
                sub.height = (
                    new_rect.height()
                )

        self.update()

    # ------------------------------------------------------------------
    # Box creation
    # ------------------------------------------------------------------

    def create_positive_box(
        self,
        rect: QRectF,
    ) -> None:
        next_index = (
            max(
                (
                    box.index
                    for box in self.boxes
                ),
                default=0,
            )
            + 1
        )

        box = Box(
            index=next_index,
            name=(
                f"Question "
                f"{next_index}"
            ),
            x=rect.x(),
            y=rect.y(),
            width=rect.width(),
            height=rect.height(),
        )

        self.boxes.append(
            box
        )

        self.select_box(
            box
        )

        if callable(
            self.box_changed
        ):
            self.box_changed()

    def add_subtractive_box(
        self,
        rect: QRectF,
    ) -> None:
        best_box = None
        best_area = 0.0

        for box in self.boxes:
            intersection = (
                rect_intersection(
                    rect,
                    box.rect,
                )
            )

            area = (
                intersection.width()
                * intersection.height()
            )

            if area > best_area:
                best_area = area
                best_box = box

        if best_box is None:
            QMessageBox.warning(
                self,
                "Subtractive box",
                (
                    "Draw the subtractive "
                    "box inside an existing "
                    "box."
                ),
            )

            return

        sub = SubtractiveBox(
            rect.x(),
            rect.y(),
            rect.width(),
            rect.height(),
        )

        best_box.subtractive.append(
            sub
        )

        self.select_subtractive(
            best_box,
            sub,
        )

        if callable(
            self.box_changed
        ):
            self.box_changed()

    # ------------------------------------------------------------------
    # Wheel / zoom
    # ------------------------------------------------------------------

    def wheelEvent(
        self,
        event,
    ) -> None:
        delta = (
            event.angleDelta().y()
        )

        if delta == 0:
            return

        modifiers = event.modifiers()

        if (
            modifiers
            & Qt.KeyboardModifier.ControlModifier
        ):
            self.zoom_at(
                event.position().toPoint(),
                delta,
            )

        elif (
            modifiers
            & Qt.KeyboardModifier.ShiftModifier
        ):
            self.offset_x -= delta
            self.clamp_offsets()
            self.update()

        else:
            self.offset_y -= delta
            self.clamp_offsets()
            self.update()

    def zoom_at(
        self,
        mouse_pos: QPoint,
        delta: int,
    ) -> None:
        old_zoom = self.zoom

        factor = (
            1.15
            if delta > 0
            else 1 / 1.15
        )

        new_zoom = max(
            0.15,
            min(
                5.0,
                old_zoom * factor,
            ),
        )

        if abs(
            new_zoom - old_zoom
        ) < 1e-6:
            return

        doc_x, doc_y = (
            self.canvas_to_document(
                mouse_pos
            )
        )

        self.zoom = new_zoom

        self.offset_x = (
            doc_x * new_zoom
            - mouse_pos.x()
        )

        self.offset_y = (
            doc_y * new_zoom
            - mouse_pos.y()
        )

        self.clamp_offsets()
        self.update()

    def clamp_offsets(self) -> None:
        content_width = (
            self.document_width
            * self.zoom
        )

        content_height = (
            self.document_height
            * self.zoom
        )

        max_x = max(
            0.0,
            content_width
            - self.width(),
        )

        max_y = max(
            0.0,
            content_height
            - self.height(),
        )

        self.offset_x = max(
            0.0,
            min(
                self.offset_x,
                max_x,
            ),
        )

        self.offset_y = max(
            0.0,
            min(
                self.offset_y,
                max_y,
            ),
        )

    def resizeEvent(
        self,
        event,
    ) -> None:
        self.clamp_offsets()
        super().resizeEvent(
            event
        )


# ----------------------------------------------------------------------
# Main window
# ----------------------------------------------------------------------


class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()

        self.setWindowTitle(
            "Exam PDF Box Exporter"
        )

        self.resize(
            1600,
            1000,
        )

        self.pdfs = find_pdfs()

        self.current_pdf: (
            Path | None
        ) = None

        self.current_index = -1

        self.available_tags = (
            load_tag_cache()
        )

        self.canvas = PdfCanvas()

        self.canvas.box_changed = (
            self.refresh_box_table
        )

        self.canvas.selection_changed = (
            self.update_table_selection
        )

        self.canvas.delete_requested = (
            self.delete_selected_box
        )

        self.file_list = QListWidget()

        self.file_list.setSelectionMode(
            QAbstractItemView.SelectionMode.SingleSelection
        )

        self.file_list.currentRowChanged.connect(
            self.open_file_by_index
        )

        self.box_table = BoxTableWidget(
            0,
            len(INDEX_HEADERS),
        )

        self.box_table.setHorizontalHeaderLabels(
            INDEX_HEADERS
        )

        self.box_table.setSelectionBehavior(
            QAbstractItemView.SelectionBehavior.SelectRows
        )

        self.box_table.setEditTriggers(
            QAbstractItemView.EditTrigger.DoubleClicked
            | QAbstractItemView.EditTrigger.EditKeyPressed
        )

        header = (
            self.box_table.horizontalHeader()
        )

        header.setSectionsMovable(True)
        header.moveSection(6, 2)

        # Keep every column interactively resizable.  Set useful
        # initial widths, but let the user drag any header divider.
        header.setSectionResizeMode(
            QHeaderView.ResizeMode.Interactive
        )
        header.setStretchLastSection(
            False
        )

        initial_widths = [
            60,   # Index
            220,  # Name
            80,   # PosX
            80,   # PosY
            80,   # Width
            80,   # Height
            260,  # Tags
        ]

        for column, width in enumerate(
            initial_widths
        ):
            header.resizeSection(
                column,
                width,
            )

        self.box_table_delegate = BoxTableDelegate(
            self.box_table,
            self.get_available_tags,
        )

        self.box_table.setItemDelegateForColumn(
            1,
            self.box_table_delegate,
        )

        self.box_table.setItemDelegateForColumn(
            6,
            self.box_table_delegate,
        )

        self.box_table.itemChanged.connect(
            self.box_table_changed
        )

        self.box_table.itemSelectionChanged.connect(
            self.table_selection_changed
        )

        self.status_label = QLabel()

        self.progress_bar = QProgressBar()
        self.progress_bar.setRange(
            0,
            1,
        )
        self.progress_bar.setValue(
            0
        )
        self.progress_bar.setVisible(
            False
        )

        self.cancel_button = QPushButton(
            "Cancel"
        )

        self.save_button = QPushButton(
            "Save"
        )

        self.previous_button = QPushButton(
            "Previous file"
        )

        self.next_button = QPushButton(
            "Next file"
        )

        self.delete_button = QPushButton(
            "Delete selected box"
        )

        self.tag_cache_list = QListWidget()

        self.tag_cache_list.setSelectionMode(
            QAbstractItemView.SelectionMode.SingleSelection
        )

        self.remove_tag_button = QPushButton(
            "Remove selected tag"
        )

        self.only_export_data_checkbox = (
            QCheckBox(
                "Export data only"
            )
        )

        self.only_export_data_checkbox.setChecked(
            True
        )

        self.load_cached_images_checkbox = (
            QCheckBox(
                "Load cached images"
            )
        )
        
        self.load_cached_images_checkbox.setChecked(
            True
        )

        self.delete_button.clicked.connect(
            self.delete_selected_box
        )

        self.delete_button.setEnabled(
            False
        )

        self.remove_tag_button.clicked.connect(
            self.remove_selected_tag
        )

        self.tag_cache_list.itemSelectionChanged.connect(
            self.update_remove_tag_button
        )

        self.cancel_button.clicked.connect(
            self.cancel_changes
        )

        self.save_button.clicked.connect(
            self.save_current
        )

        self.previous_button.clicked.connect(
            self.previous_file
        )

        self.next_button.clicked.connect(
            self.next_file
        )

        self.load_cached_images_checkbox.stateChanged.connect(
            self.reload_current_view
        )

        self.build_ui()
        self.refresh_file_list()
        self.refresh_tag_cache_list()

        if self.pdfs:
            self.file_list.setCurrentRow(
                0
            )

    # ------------------------------------------------------------------
    # Tags
    # ------------------------------------------------------------------

    def get_available_tags(self) -> list[str]:
        # Reload so newly created/removed tags immediately appear in
        # autocomplete editors and in the cache list.
        self.available_tags = (
            load_tag_cache()
        )

        return list(
            self.available_tags
        )

    def refresh_tag_cache_list(self) -> None:
        tags = load_tag_cache()

        self.available_tags = list(tags)

        self.tag_cache_list.blockSignals(
            True
        )

        self.tag_cache_list.clear()

        for tag in tags:
            self.tag_cache_list.addItem(tag)

        self.tag_cache_list.blockSignals(
            False
        )

        self.update_remove_tag_button()

    def update_remove_tag_button(self) -> None:
        self.remove_tag_button.setEnabled(
            self.tag_cache_list.currentItem()
            is not None
        )

    def remove_selected_tag(self) -> None:
        item = self.tag_cache_list.currentItem()

        if item is None:
            return

        tag = item.text().strip()

        if not tag:
            return

        if not remove_tag_from_cache(tag):
            return

        self.refresh_tag_cache_list()

    def register_tags_from_box(
        self,
        box: Box,
    ) -> None:
        for tag in box.tags:
            ensure_tag_in_cache(
                tag
            )

        self.refresh_tag_cache_list()

    def parse_and_register_tags(
        self,
        text: str,
    ) -> list[str]:
        tags = parse_tags(
            text
        )

        for tag in tags:
            ensure_tag_in_cache(
                tag
            )

        self.refresh_tag_cache_list()

        return tags

    # ------------------------------------------------------------------
    # UI
    # ------------------------------------------------------------------

    def build_ui(self) -> None:
        left_panel = QWidget()
        left_layout = QVBoxLayout(
            left_panel
        )

        left_layout.setContentsMargins(
            5,
            5,
            5,
            5,
        )

        left_layout.addWidget(
            QLabel("<b>PDF files</b>")
        )

        left_layout.addWidget(
            self.file_list
        )

        middle_panel = QWidget()

        middle_layout = QVBoxLayout(
            middle_panel
        )

        middle_layout.setContentsMargins(
            0,
            0,
            0,
            0,
        )

        middle_layout.setSpacing(
            5
        )

        middle_layout.addWidget(
            self.canvas,
            1,
        )

        controls = QHBoxLayout()

        controls.setContentsMargins(
            0,
            0,
            0,
            0,
        )

        controls.addWidget(
            self.previous_button
        )

        controls.addWidget(
            self.next_button
        )

        controls.addStretch()

        controls.addWidget(
            self.only_export_data_checkbox
        )

        controls.addWidget(
            self.load_cached_images_checkbox
        )

        controls.addWidget(
            self.cancel_button
        )

        controls.addWidget(
            self.save_button
        )

        middle_layout.addLayout(
            controls
        )

        middle_layout.addWidget(
            self.status_label
        )

        middle_layout.addWidget(
            self.progress_bar
        )

        right_panel = QWidget()

        right_layout = QVBoxLayout(
            right_panel
        )

        right_layout.setContentsMargins(
            5,
            5,
            5,
            5,
        )

        right_layout.addWidget(
            QLabel("<b>Boxes</b>")
        )

        right_layout.addWidget(
            self.box_table,
            3,
        )

        right_layout.addWidget(
            self.delete_button
        )

        right_layout.addWidget(
            QLabel("<b>Tags cache</b>")
        )

        right_layout.addWidget(
            self.tag_cache_list,
            1,
        )

        right_layout.addWidget(
            self.remove_tag_button
        )

        splitter = QSplitter(
            Qt.Orientation.Horizontal
        )

        splitter.addWidget(
            left_panel
        )

        splitter.addWidget(
            middle_panel
        )

        splitter.addWidget(
            right_panel
        )

        splitter.setSizes(
            [
                280,
                1000,
                600,
            ]
        )

        self.setCentralWidget(
            splitter
        )

    # ------------------------------------------------------------------
    # Table selection
    # ------------------------------------------------------------------

    def table_selection_changed(
        self,
    ) -> None:
        selected_rows = (
            self.box_table.selectionModel()
            .selectedRows()
        )

        if not selected_rows:
            self.canvas.clear_selection()
            self.update_delete_button()
            return

        row = selected_rows[
            0
        ].row()

        index_item = (
            self.box_table.item(
                row,
                0,
            )
        )

        if index_item is None:
            return

        try:
            index = int(
                index_item.text()
            )
        except ValueError:
            return

        if index > 0:
            box = next(
                (
                    box
                    for box in self.canvas.boxes
                    if box.index == index
                ),
                None,
            )

            if box is not None:
                self.canvas.select_box(
                    box,
                    pan_to_box=True,
                )

        elif index < 0:
            parent_index = abs(
                index
            )

            box = next(
                (
                    box
                    for box in self.canvas.boxes
                    if box.index
                    == parent_index
                ),
                None,
            )

            if box is not None:
                sub_number = (
                    self.get_subtractive_number(
                        row,
                        parent_index,
                    )
                )

                if (
                    sub_number is not None
                    and 1
                    <= sub_number
                    <= len(
                        box.subtractive
                    )
                ):
                    self.canvas.select_subtractive(
                        box,
                        box.subtractive[
                            sub_number - 1
                        ],
                    )

                    self.canvas.pan_to_box(
                        box
                    )

        self.update_delete_button()

    def update_table_selection(
        self,
    ) -> None:
        selected_box = (
            self.canvas.selected_box
        )

        selected_sub = (
            self.canvas.selected_subtractive
        )

        if selected_box is None:
            self.box_table.clearSelection()
            self.update_delete_button()
            return

        target_row = None

        for row in range(
            self.box_table.rowCount()
        ):
            index_item = (
                self.box_table.item(
                    row,
                    0,
                )
            )

            if index_item is None:
                continue

            try:
                index = int(
                    index_item.text()
                )
            except ValueError:
                continue

            if selected_sub is None:
                if (
                    index
                    == selected_box.index
                ):
                    target_row = row
                    break

            else:
                if (
                    index
                    != -selected_box.index
                ):
                    continue

                sub_number = (
                    self.get_subtractive_number(
                        row,
                        selected_box.index,
                    )
                )

                if (
                    sub_number is not None
                    and selected_box.subtractive[
                        sub_number - 1
                    ]
                    is selected_sub
                ):
                    target_row = row
                    break

        if target_row is not None:
            self.box_table.blockSignals(
                True
            )

            self.box_table.clearSelection()

            self.box_table.selectRow(
                target_row
            )

            self.box_table.blockSignals(
                False
            )

        self.update_delete_button()

    def get_subtractive_number(
        self,
        row: int,
        parent_index: int,
    ) -> int | None:
        if row < 0:
            return None

        index_item = (
            self.box_table.item(
                row,
                0,
            )
        )

        if index_item is None:
            return None

        try:
            index = int(
                index_item.text()
            )
        except ValueError:
            return None

        if (
            index
            != -parent_index
        ):
            return None

        number_item = (
            self.box_table.item(
                row,
                1,
            )
        )

        if number_item is None:
            return None

        text = (
            number_item.text()
            .strip()
        )

        match = re.match(
            r"subtract\s+(\d+)",
            text,
            re.IGNORECASE,
        )

        if not match:
            return None

        return int(
            match.group(1)
        )

    def update_delete_button(
        self,
    ) -> None:
        selected_rows = (
            self.box_table.selectionModel()
            .selectedRows()
        )

        self.delete_button.setEnabled(
            bool(selected_rows)
        )

    # ------------------------------------------------------------------
    # Delete
    # ------------------------------------------------------------------

    def delete_selected_box(
        self,
    ) -> None:
        selected_rows = (
            self.box_table.selectionModel()
            .selectedRows()
        )

        if not selected_rows:
            return

        row = selected_rows[
            0
        ].row()

        index_item = (
            self.box_table.item(
                row,
                0,
            )
        )

        if index_item is None:
            return

        try:
            index = int(
                index_item.text()
            )
        except ValueError:
            return

        if index < 0:
            parent_index = abs(
                index
            )

            box = next(
                (
                    box
                    for box in self.canvas.boxes
                    if box.index
                    == parent_index
                ),
                None,
            )

            if box is None:
                return

            sub_number = (
                self.get_subtractive_number(
                    row,
                    parent_index,
                )
            )

            if (
                sub_number is None
                or not (
                    1
                    <= sub_number
                    <= len(
                        box.subtractive
                    )
                )
            ):
                return

            sub = box.subtractive[
                sub_number - 1
            ]

            answer = QMessageBox.question(
                self,
                "Delete subtractive box",
                (
                    f"Delete subtractive "
                    f"box {sub_number} "
                    f"from box "
                    f"{box.index}: "
                    f"{box.name}?"
                ),
                QMessageBox.StandardButton.Yes
                | QMessageBox.StandardButton.No,
                QMessageBox.StandardButton.No,
            )

            if (
                answer
                != QMessageBox.StandardButton.Yes
            ):
                return

            if (
                self.canvas.selected_subtractive
                is sub
            ):
                self.canvas.clear_selection()

            box.subtractive.pop(
                sub_number - 1
            )

            self.refresh_box_table()
            self.canvas.update()
            self.update_delete_button()
            self.update_status()

            return

        box = next(
            (
                box
                for box in self.canvas.boxes
                if box.index == index
            ),
            None,
        )

        if box is None:
            return

        answer = QMessageBox.question(
            self,
            "Delete box",
            (
                f"Delete box "
                f"{box.index}: "
                f"{box.name}?"
            ),
            QMessageBox.StandardButton.Yes
            | QMessageBox.StandardButton.No,
            QMessageBox.StandardButton.No,
        )

        if (
            answer
            != QMessageBox.StandardButton.Yes
        ):
            return

        self.canvas.boxes.remove(
            box
        )

        for new_index, remaining_box in enumerate(
            self.canvas.boxes,
            start=1,
        ):
            remaining_box.index = (
                new_index
            )

        self.canvas.clear_selection()

        self.refresh_box_table()
        self.canvas.update()
        self.update_delete_button()
        self.update_status()

    # ------------------------------------------------------------------
    # Files
    # ------------------------------------------------------------------

    def refresh_file_list(
        self,
    ) -> None:
        self.file_list.blockSignals(
            True
        )

        self.file_list.clear()

        for path in self.pdfs:
            item = QListWidgetItem(
                self.flattened_name(
                    path
                )
            )

            output_dir = (
                output_dir_for(path)
            )

            if (
                self.current_pdf
                is not None
                and path.resolve()
                == self.current_pdf.resolve()
            ):
                item.setForeground(
                    QColor("#f39c12")
                )

            elif (output_dir / "index.csv").exists():
                item.setForeground(
                    QColor("#2ecc71")
                )

            else:
                item.setForeground(
                    QColor("#e74c3c")
                )

            item.setToolTip(
                str(path)
            )

            self.file_list.addItem(
                item
            )

        if (
            0
            <= self.current_index
            < self.file_list.count()
        ):
            self.file_list.setCurrentRow(
                self.current_index
            )

        self.file_list.blockSignals(
            False
        )

    @staticmethod
    def flattened_name(
        path: Path,
    ) -> str:
        relative = path.relative_to(
            EXAMS_DIR
        )

        return " / ".join(
            relative.parts
        )

    def open_file_by_index(
        self,
        index: int,
    ) -> None:
        if (
            index < 0
            or index >= len(self.pdfs)
        ):
            return

        path = self.pdfs[
            index
        ]

        if (
            self.current_pdf
            is not None
        ):
            if (
                path.resolve()
                != self.current_pdf.resolve()
                and self.has_unsaved_changes()
            ):
                answer = QMessageBox.question(
                    self,
                    "Unsaved changes",
                    (
                        "There are unsaved "
                        "changes. Save them?"
                    ),
                    QMessageBox.StandardButton.Yes
                    | QMessageBox.StandardButton.No
                    | QMessageBox.StandardButton.Cancel,
                    QMessageBox.StandardButton.Yes,
                )

                if (
                    answer
                    == QMessageBox.StandardButton.Cancel
                ):
                    self.file_list.blockSignals(
                        True
                    )

                    self.file_list.setCurrentRow(
                        self.current_index
                    )

                    self.file_list.blockSignals(
                        False
                    )

                    return

                if (
                    answer
                    == QMessageBox.StandardButton.Yes
                ):
                    self.save_current()

        self.current_index = index
        self.current_pdf = path

        self.load_current_view()

        self.refresh_box_table()
        self.update_file_list_colors()
        self.update_status()

    def load_current_view(
        self,
    ) -> None:
        if self.current_pdf is None:
            return

        self.canvas.set_document(
            self.current_pdf,
            use_cached_images=(
                self.load_cached_images_checkbox.isChecked()
            ),
        )

    def reload_current_view(
        self,
        _state=None,
    ) -> None:
        if self.current_pdf is None:
            return

        # Preserve the selected box while changing display mode.
        selected_index = None

        if self.canvas.selected_box is not None:
            selected_index = (
                self.canvas.selected_box.index
            )

        self.load_current_view()

        if selected_index is not None:
            box = next(
                (
                    box
                    for box in self.canvas.boxes
                    if box.index
                    == selected_index
                ),
                None,
            )

            if box is not None:
                self.canvas.select_box(
                    box,
                    pan_to_box=True,
                )

        self.refresh_box_table()
        self.update_status()

    def update_file_list_colors(
        self,
    ) -> None:
        for index, path in enumerate(
            self.pdfs
        ):
            item = (
                self.file_list.item(
                    index
                )
            )

            if item is None:
                continue

            output_dir = (
                output_dir_for(path)
            )

            if (
                self.current_pdf
                is not None
                and path.resolve()
                == self.current_pdf.resolve()
            ):
                item.setForeground(
                    QColor("#f39c12")
                )

            elif (output_dir / "index.csv").is_file():
                item.setForeground(
                    QColor("#2ecc71")
                )

            else:
                item.setForeground(
                    QColor("#e74c3c")
                )

    # ------------------------------------------------------------------
    # Change tracking
    # ------------------------------------------------------------------

    def has_unsaved_changes(
        self,
    ) -> bool:
        if self.current_pdf is None:
            return False

        disk_boxes = load_boxes(
            self.current_pdf
        )

        current_boxes = (
            self.canvas.boxes
        )

        return not self.boxes_equal(
            current_boxes,
            disk_boxes,
        )

    @staticmethod
    def boxes_equal(
        a: list[Box],
        b: list[Box],
    ) -> bool:
        if len(a) != len(b):
            return False

        for box_a, box_b in zip(
            a,
            b,
        ):
            if (
                box_a.index
                != box_b.index
                or box_a.name
                != box_b.name
                or box_a.tags
                != box_b.tags
                or not nearly_equal(
                    box_a.x,
                    box_b.x,
                )
                or not nearly_equal(
                    box_a.y,
                    box_b.y,
                )
                or not nearly_equal(
                    box_a.width,
                    box_b.width,
                )
                or not nearly_equal(
                    box_a.height,
                    box_b.height,
                )
                or len(
                    box_a.subtractive
                )
                != len(
                    box_b.subtractive
                )
            ):
                return False

            for sub_a, sub_b in zip(
                box_a.subtractive,
                box_b.subtractive,
            ):
                if not all(
                    [
                        nearly_equal(
                            sub_a.x,
                            sub_b.x,
                        ),
                        nearly_equal(
                            sub_a.y,
                            sub_b.y,
                        ),
                        nearly_equal(
                            sub_a.width,
                            sub_b.width,
                        ),
                        nearly_equal(
                            sub_a.height,
                            sub_b.height,
                        ),
                    ]
                ):
                    return False

        return True

    # ------------------------------------------------------------------
    # Table
    # ------------------------------------------------------------------

    def refresh_box_table(
        self,
    ) -> None:
        selected_box = (
            self.canvas.selected_box
        )

        selected_sub = (
            self.canvas.selected_subtractive
        )

        self.box_table.blockSignals(
            True
        )

        self.box_table.setRowCount(
            0
        )

        for box in self.canvas.boxes:
            row = (
                self.box_table.rowCount()
            )

            self.box_table.insertRow(
                row
            )

            values = [
                str(box.index),
                box.name,
                format_number(box.x),
                format_number(box.y),
                format_number(box.width),
                format_number(box.height),
                format_tags(box.tags),
            ]

            for column, value in enumerate(
                values
            ):
                item = QTableWidgetItem(
                    value
                )

                # Name and Tags are editable.
                if column not in (
                    1,
                    6,
                ):
                    item.setFlags(
                        item.flags()
                        & ~Qt.ItemFlag.ItemIsEditable
                    )

                self.box_table.setItem(
                    row,
                    column,
                    item,
                )

            for sub_number, sub in enumerate(
                box.subtractive,
                start=1,
            ):
                row = (
                    self.box_table.rowCount()
                )

                self.box_table.insertRow(
                    row
                )

                values = [
                    f"-{box.index}",
                    f"subtract {sub_number}",
                    format_number(sub.x),
                    format_number(sub.y),
                    format_number(sub.width),
                    format_number(sub.height),
                    "",
                ]

                for column, value in enumerate(
                    values
                ):
                    item = QTableWidgetItem(
                        value
                    )

                    item.setFlags(
                        item.flags()
                        & ~Qt.ItemFlag.ItemIsEditable
                    )

                    self.box_table.setItem(
                        row,
                        column,
                        item,
                    )

        self.box_table.blockSignals(
            False
        )

        if selected_sub is not None:
            self.canvas.select_subtractive(
                selected_box,
                selected_sub,
            )

        elif selected_box is not None:
            self.canvas.select_box(
                selected_box
            )

        self.update_table_selection()
        self.update_delete_button()

    def box_table_changed(
        self,
        item: QTableWidgetItem,
    ) -> None:
        if item.column() not in (
            1,
            6,
        ):
            return

        row = item.row()

        index_item = (
            self.box_table.item(
                row,
                0,
            )
        )

        if index_item is None:
            return

        try:
            box_index = int(
                index_item.text()
            )
        except ValueError:
            return

        if box_index < 0:
            return

        box = next(
            (
                box
                for box in self.canvas.boxes
                if box.index
                == box_index
            ),
            None,
        )

        if box is None:
            return

        if item.column() == 1:
            box.name = (
                item.text().strip()
            )

        elif item.column() == 6:
            tags = (
                self.parse_and_register_tags(
                    item.text()
                )
            )

            box.tags = tags

            # Normalize the table value.
            normalized_text = (
                format_tags(tags)
            )

            if (
                item.text()
                != normalized_text
            ):
                self.box_table.blockSignals(
                    True
                )

                item.setText(
                    normalized_text
                )

                self.box_table.blockSignals(
                    False
                )

        self.canvas.update()

    # ------------------------------------------------------------------
    # Cancel / save / export
    # ------------------------------------------------------------------

    def cancel_changes(
        self,
    ) -> None:
        if self.current_pdf is None:
            return

        self.load_current_view()

        self.refresh_box_table()
        self.refresh_file_list()
        self.update_status()

    def save_current(
        self,
    ) -> None:
        if self.current_pdf is None:
            return

        saved_index = self.current_index

        try:
            self.progress_bar.setVisible(
                True
            )

            self.progress_bar.setMinimum(
                0
            )

            self.progress_bar.setMaximum(
                max(
                    1,
                    len(
                        self.canvas.boxes
                    ),
                )
            )

            self.progress_bar.setValue(
                0
            )

            QApplication.processEvents()

            self.export_pdf(
                self.current_pdf,
                self.canvas.boxes,
            )

        except Exception as exc:
            QMessageBox.critical(
                self,
                "Export failed",
                (
                    "Could not export "
                    "the document:\n\n"
                    f"{exc}"
                ),
            )

            return

        finally:
            self.progress_bar.setVisible(
                False
            )

        self.refresh_file_list()

        self.current_index = saved_index

        self.file_list.blockSignals(
            True
        )

        if (
            0
            <= saved_index
            < self.file_list.count()
        ):
            self.file_list.setCurrentRow(
                saved_index
            )

        self.file_list.blockSignals(
            False
        )

        # Reload using the current cached-image setting.
        self.load_current_view()

        self.refresh_box_table()
        self.update_status()

    def export_pdf(
        self,
        pdf_path: Path,
        boxes: list[Box],
    ) -> None:
        save_index(
            pdf_path,
            boxes,
        )

        for box in boxes:
            register_box_tags(
                box
            )

        only_export_data = (
            self.only_export_data_checkbox.isChecked()
        )

        if only_export_data:
            return
        
        output_dir = (
            clear_output_directory(
                pdf_path
            )
        )

        doc = pymupdf.open(
            pdf_path
        )

        try:
            output_scale = (
                get_pdf_output_scale(
                    doc
                )
            )

            print(
                f"[INFO] {pdf_path.name}: "
                f"output scale = "
                f"{output_scale:.4f} "
                f"({output_scale * 72:.1f} DPI)",
                flush=True,
            )

            page_infos = []

            y = 0.0
            gap = 20.0

            for page_number in range(
                len(doc)
            ):
                page = doc[
                    page_number
                ]

                page_infos.append(
                    {
                        "number": page_number,
                        "x": 0.0,
                        "y": y,
                        "width": page.rect.width,
                        "height": page.rect.height,
                    }
                )

                y += (
                    page.rect.height
                    + gap
                )

            total = len(boxes)

            if total == 0:
                self.progress_bar.setValue(
                    1
                )

                QApplication.processEvents()

                return

            for number, box in enumerate(
                boxes,
                start=1,
            ):
                self.status_label.setText(
                    (
                        f"Exporting box "
                        f"{number}/{total}: "
                        f"{box.index}: "
                        f"{box.name}"
                    )
                )

                QApplication.processEvents()

                export_box(
                    doc=doc,
                    page_infos=page_infos,
                    box=box,
                    output_path=(
                        output_dir
                        / f"{box.index}.webp"
                    ),
                    output_scale=output_scale,
                )

                self.progress_bar.setValue(
                    number
                )

                QApplication.processEvents()

        finally:
            doc.close()

    # ------------------------------------------------------------------
    # File navigation
    # ------------------------------------------------------------------

    def next_file(
        self,
    ) -> None:
        if not self.pdfs:
            return

        next_index = min(
            self.current_index + 1,
            len(self.pdfs) - 1,
        )

        if (
            next_index
            == self.current_index
        ):
            return

        self.file_list.setCurrentRow(
            next_index
        )

    def previous_file(
        self,
    ) -> None:
        if not self.pdfs:
            return

        previous_index = max(
            self.current_index - 1,
            0,
        )

        if (
            previous_index
            == self.current_index
        ):
            return

        self.file_list.setCurrentRow(
            previous_index
        )

    def update_status(
        self,
    ) -> None:
        if self.current_pdf is None:
            self.status_label.setText(
                ""
            )
            return

        mode = (
            "cached images"
            if self.canvas.cached_mode
            else "PDF"
        )

        self.status_label.setText(
            (
                f"{self.current_pdf}    "
                f"| {len(self.canvas.boxes)} "
                f"box(es)    "
                f"| {mode}"
            )
        )

    def closeEvent(
        self,
        event,
    ) -> None:
        if (
            self.current_pdf is not None
            and self.has_unsaved_changes()
        ):
            answer = QMessageBox.question(
                self,
                "Unsaved changes",
                (
                    "There are unsaved "
                    "changes. Exit anyway?"
                ),
                QMessageBox.StandardButton.Yes
                | QMessageBox.StandardButton.No,
                QMessageBox.StandardButton.No,
            )

            if (
                answer
                != QMessageBox.StandardButton.Yes
            ):
                event.ignore()
                return

        if self.canvas.doc is not None:
            self.canvas.doc.close()
            self.canvas.doc = None

        event.accept()


# ----------------------------------------------------------------------
# PDF export helpers
# ----------------------------------------------------------------------


def nearly_equal(
    a: float,
    b: float,
) -> bool:
    return abs(
        a - b
    ) < 1e-5


def rect_intersection(
    a: QRectF,
    b: QRectF,
) -> QRectF:
    return a.intersected(
        b
    )


def subtractive_cut_ranges(
    box: Box,
    output_scale: float,
) -> tuple[
    list[tuple[int, int]],
    list[tuple[int, int]],
]:
    horizontal_cuts = []
    vertical_cuts = []

    box_rect = box.rect

    tolerance = 1e-5

    for sub in box.subtractive:
        sub_rect = (
            sub.rect.intersected(
                box_rect
            )
        )

        if sub_rect.isEmpty():
            continue

        if (
            sub_rect.left()
            <= box_rect.left()
            + tolerance
            and sub_rect.right()
            >= box_rect.right()
            - tolerance
        ):
            start = round(
                (
                    sub_rect.top()
                    - box_rect.top()
                )
                * output_scale
            )

            end = round(
                (
                    sub_rect.bottom()
                    - box_rect.top()
                )
                * output_scale
            )

            if end > start:
                horizontal_cuts.append(
                    (
                        start,
                        end,
                    )
                )

        if (
            sub_rect.top()
            <= box_rect.top()
            + tolerance
            and sub_rect.bottom()
            >= box_rect.bottom()
            - tolerance
        ):
            start = round(
                (
                    sub_rect.left()
                    - box_rect.left()
                )
                * output_scale
            )

            end = round(
                (
                    sub_rect.right()
                    - box_rect.left()
                )
                * output_scale
            )

            if end > start:
                vertical_cuts.append(
                    (
                        start,
                        end,
                    )
                )

    return (
        merge_ranges(
            horizontal_cuts
        ),
        merge_ranges(
            vertical_cuts
        ),
    )


def merge_ranges(
    ranges: list[
        tuple[int, int]
    ],
) -> list[
    tuple[int, int]
]:
    if not ranges:
        return []

    ranges = sorted(
        ranges
    )

    merged = [
        ranges[0]
    ]

    for start, end in ranges[1:]:
        previous_start, previous_end = (
            merged[-1]
        )

        if start <= previous_end:
            merged[-1] = (
                previous_start,
                max(
                    previous_end,
                    end,
                ),
            )

        else:
            merged.append(
                (
                    start,
                    end,
                )
            )

    return merged


def ranges_to_indices(
    size: int,
    removed_ranges: list[
        tuple[int, int]
    ],
) -> list[int]:
    removed = [
        False
    ] * size

    for start, end in removed_ranges:
        start = max(
            0,
            min(
                size,
                start,
            ),
        )

        end = max(
            0,
            min(
                size,
                end,
            ),
        )

        for index in range(
            start,
            end,
        ):
            removed[index] = True

    return [
        index
        for index, is_removed
        in enumerate(removed)
        if not is_removed
    ]


def crop_removed_strips(
    image: QImage,
    horizontal_cuts: list[
        tuple[int, int]
    ],
    vertical_cuts: list[
        tuple[int, int]
    ],
) -> QImage:
    keep_x = ranges_to_indices(
        image.width(),
        vertical_cuts,
    )

    keep_y = ranges_to_indices(
        image.height(),
        horizontal_cuts,
    )

    if (
        not keep_x
        or not keep_y
    ):
        raise RuntimeError(
            "Subtractive boxes remove "
            "the entire output."
        )

    output = QImage(
        len(keep_x),
        len(keep_y),
        QImage.Format.Format_RGBA8888,
    )

    output.fill(
        Qt.GlobalColor.transparent
    )

    source_bits = image.bits()
    source_bytes_per_line = (
        image.bytesPerLine()
    )

    source_bpp = 4

    destination_bits = (
        output.bits()
    )

    destination_bytes_per_line = (
        output.bytesPerLine()
    )

    for destination_y, source_y in enumerate(
        keep_y
    ):
        source_row_start = (
            source_y
            * source_bytes_per_line
        )

        destination_row_start = (
            destination_y
            * destination_bytes_per_line
        )

        for destination_x, source_x in enumerate(
            keep_x
        ):
            source_offset = (
                source_row_start
                + source_x
                * source_bpp
            )

            destination_offset = (
                destination_row_start
                + destination_x
                * source_bpp
            )

            destination_bits[
                destination_offset:
                destination_offset + 4
            ] = source_bits[
                source_offset:
                source_offset + 4
            ]

    return output


# ----------------------------------------------------------------------
# Native raster DPI detection
# ----------------------------------------------------------------------


def get_raster_page_dpi(
    page: pymupdf.Page,
) -> float | None:
    """
    Return the native DPI of a full-page raster image.

    A PDF page is considered rasterized when an image covers
    essentially the complete page.

    PDF dimensions are measured in points:

        72 points = 1 inch

    Therefore:

        DPI = pixels / (PDF points / 72)

    Returns None when the page does not appear to be a
    full-page raster image.
    """
    page_rect = page.rect

    if (
        page_rect.width <= 0
        or page_rect.height <= 0
    ):
        return None

    page_area = (
        page_rect.width
        * page_rect.height
    )

    for image_info in page.get_images(
        full=True
    ):
        xref = image_info[0]

        try:
            image_rects = [
                QRectF(
                    rect.x0,
                    rect.y0,
                    rect.width,
                    rect.height,
                )
                for rect in page.get_image_rects(xref)
            ]
        except Exception:
            continue

        if not image_rects:
            continue

        try:
            pix = pymupdf.Pixmap(
                page.parent,
                xref,
            )

            pixel_width = pix.width
            pixel_height = pix.height

            pix = None

        except Exception:
            continue

        if (
            pixel_width <= 0
            or pixel_height <= 0
        ):
            continue

        for image_rect in image_rects:
            intersection = (
                image_rect.intersected(
                    QRectF(
                        page_rect.x0,
                        page_rect.y0,
                        page_rect.width,
                        page_rect.height,
                    )
                )
            )

            if intersection.isEmpty():
                continue

            intersection_area = (
                intersection.width()
                * intersection.height()
            )

            coverage = (
                intersection_area
                / page_area
            )

            if coverage < 0.99:
                continue

            dpi_x = (
                pixel_width
                * 72.0
                / intersection.width()
            )

            dpi_y = (
                pixel_height
                * 72.0
                / intersection.height()
            )

            dpi = (
                dpi_x + dpi_y
            ) / 2.0

            if dpi > 1:
                return dpi

    return None


def get_pdf_output_scale(
    doc: pymupdf.Document,
) -> float:
    """
    Determine the appropriate rendering scale.

    Normal/text/vector PDFs:
        DEFAULT_OUTPUT_DPI, normally 72 DPI.

    Rasterized/scanned PDFs:
        Use the native DPI of the full-page raster image.

    If multiple rasterized pages have different DPI values,
    the median DPI is used.
    """
    raster_dpis: list[float] = []

    for page_number in range(
        len(doc)
    ):
        page = doc[
            page_number
        ]

        text = (
            page.get_text(
                "text"
            ).strip()
        )

        if text:
            continue

        dpi = get_raster_page_dpi(
            page
        )

        if dpi is not None:
            raster_dpis.append(
                dpi
            )

    if not raster_dpis:
        return (
            DEFAULT_OUTPUT_DPI
            / 72.0
        )

    raster_dpis.sort()

    middle = (
        len(raster_dpis)
        // 2
    )

    if (
        len(raster_dpis)
        % 2
    ):
        dpi = raster_dpis[
            middle
        ]

    else:
        dpi = (
            raster_dpis[
                middle - 1
            ]
            + raster_dpis[
                middle
            ]
        ) / 2.0

    return min((dpi / 72.0), 2.5)


def export_box(
    doc: pymupdf.Document,
    page_infos: list[dict],
    box: Box,
    output_path: Path,
    output_scale: float = 1.0,
) -> None:
    box_rect = box.rect

    width = max(
        1,
        round(
            box.width
            * output_scale
        ),
    )

    height = max(
        1,
        round(
            box.height
            * output_scale
        ),
    )

    output = QImage(
        width,
        height,
        QImage.Format.Format_RGBA8888,
    )

    output.fill(
        Qt.GlobalColor.transparent
    )

    painter = QPainter(
        output
    )

    painter.setRenderHint(
        QPainter.RenderHint.SmoothPixmapTransform
    )

    for page_info in page_infos:
        page_rect = QRectF(
            page_info["x"],
            page_info["y"],
            page_info["width"],
            page_info["height"],
        )

        intersection = (
            box_rect.intersected(
                page_rect
            )
        )

        if intersection.isEmpty():
            continue

        page = doc[
            page_info["number"]
        ]

        clip = pymupdf.Rect(
            intersection.x()
            - page_info["x"],
            intersection.y()
            - page_info["y"],
            intersection.right()
            - page_info["x"],
            intersection.bottom()
            - page_info["y"],
        )

        matrix = pymupdf.Matrix(
            output_scale,
            output_scale,
        )

        pix = page.get_pixmap(
            matrix=matrix,
            clip=clip,
            alpha=True,
            colorspace=pymupdf.csRGB,
        )

        image = QImage(
            pix.samples,
            pix.width,
            pix.height,
            pix.stride,
            QImage.Format.Format_RGBA8888,
        ).copy()

        destination_x = round(
            (
                intersection.x()
                - box_rect.x()
            )
            * output_scale
        )

        destination_y = round(
            (
                intersection.y()
                - box_rect.y()
            )
            * output_scale
        )

        painter.drawImage(
            destination_x,
            destination_y,
            image,
        )

    painter.setCompositionMode(
        QPainter.CompositionMode.CompositionMode_Clear
    )

    for sub in box.subtractive:
        sub_rect = sub.rect

        intersection = (
            box_rect.intersected(
                sub_rect
            )
        )

        if intersection.isEmpty():
            continue

        clear_rect = QRectF(
            (
                intersection.x()
                - box_rect.x()
            )
            * output_scale,
            (
                intersection.y()
                - box_rect.y()
            )
            * output_scale,
            intersection.width()
            * output_scale,
            intersection.height()
            * output_scale,
        )

        painter.fillRect(
            clear_rect,
            Qt.GlobalColor.transparent,
        )

    painter.end()

    horizontal_cuts, vertical_cuts = (
        subtractive_cut_ranges(
            box,
            output_scale,
        )
    )

    if (
        horizontal_cuts
        or vertical_cuts
    ):
        output = crop_removed_strips(
            output,
            horizontal_cuts,
            vertical_cuts,
        )

    clean_output = QImage(
        output.size(),
        QImage.Format.Format_RGBA8888,
    )

    clean_output.fill(
        Qt.GlobalColor.transparent
    )

    clean_painter = QPainter(
        clean_output
    )

    clean_painter.drawImage(
        0,
        0,
        output,
    )

    clean_painter.end()

    if not clean_output.save(
        str(output_path),
        "WEBP",
        95,
    ):
        raise RuntimeError(
            "Could not write WebP file: "
            f"{output_path}"
        )


# ----------------------------------------------------------------------
# Main
# ----------------------------------------------------------------------


def main() -> int:
    parser = argparse.ArgumentParser(
        description=(
            "Exam PDF Box Exporter"
        ),
    )

    parser.add_argument(
        "--headless",
        action="store_true",
        help=(
            "Re-export every PDF that has "
            "an index.csv without opening "
            "the GUI."
        ),
    )

    parser.add_argument(
        "--workers",
        type=int,
        default=None,
        help=(
            "Number of parallel export workers "
            "in headless mode."
        ),
    )

    args = parser.parse_args()

    if (
        args.workers is not None
        and args.workers < 1
    ):
        parser.error(
            "--workers must be at least 1"
        )

    print(
        "Supported image formats:",
        [
            bytes(fmt).decode(
                "ascii",
                errors="replace",
            )
            for fmt
            in QImageWriter.supportedImageFormats()
        ],
        flush=True,
    )

    if not EXAMS_DIR.exists():
        if args.headless:
            print(
                f"[ERROR] EXAMS_DIR does not exist: "
                f"{EXAMS_DIR}",
                flush=True,
            )

        else:
            app = QApplication(
                sys.argv
            )

            QMessageBox.critical(
                None,
                "Invalid exams directory",
                (
                    "EXAMS_DIR does not exist:"
                    f"\n\n{EXAMS_DIR}"
                ),
            )

        return 1

    if args.headless:
        os.environ.setdefault(
            "QT_QPA_PLATFORM",
            "offscreen",
        )

        app = QApplication(
            sys.argv
        )

        return run_headless(
            max_workers=args.workers,
        )

    app = QApplication(
        sys.argv
    )

    window = MainWindow()
    window.show()

    return app.exec()


if __name__ == "__main__":
    raise SystemExit(
        main()
    )