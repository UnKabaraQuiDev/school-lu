#!/bin/bash

set -e

python fill-exam-index.py
python fill-exam-sections.py
python fill-exam-subjects.py
python fill-exam-all.py