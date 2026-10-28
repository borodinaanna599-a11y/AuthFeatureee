#!/usr/bin/env bash
# Использование: bump_version.sh release/1.2.0 <номер_запуска>
# versionName берётся из имени ветки, versionCode = номер запуска workflow.
set -euo pipefail
BRANCH="$1"
BUILD="$2"
NAME="${BRANCH#release/}"
FILE="app/build.gradle.kts"
sed -i -E "s/versionCode = [0-9]+/versionCode = ${BUILD}/" "$FILE"
sed -i -E "s/versionName = \"[^\"]*\"/versionName = \"${NAME}\"/" "$FILE"
grep -nE "versionCode|versionName" "$FILE"
