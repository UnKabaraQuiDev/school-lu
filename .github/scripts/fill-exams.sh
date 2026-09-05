#!/bin/bash

set -e

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

python "$SCRIPT_DIR/fill-exam-exam.py"
python "$SCRIPT_DIR/fill-exam-all.py"
python "$SCRIPT_DIR/fill-exam-index.py"
python "$SCRIPT_DIR/fill-exam-sections.py"
python "$SCRIPT_DIR/fill-exam-subjects.py"
python "$SCRIPT_DIR/fill-exam-years.py"
