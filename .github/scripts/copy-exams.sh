#!/usr/bin/env bash

set -euo pipefail

SOURCE="nico/exams"
DEST="exams"

find "$SOURCE" -type f -iname '*.pdf' -print0 |
while IFS= read -r -d '' src; do
    relative="${src#"$SOURCE"/}"
    dst="$DEST/$relative"

    if [ ! -f "$dst" ]; then
        echo "Missing: $dst"
        mkdir -p "$(dirname "$dst")"
        cp -p -- "$src" "$dst"
    elif ! cmp -s -- "$src" "$dst"; then
        echo "Different: $dst"
        cp -p -- "$src" "$dst"
    fi
done
