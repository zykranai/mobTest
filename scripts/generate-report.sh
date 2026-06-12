#!/usr/bin/env bash
# Generate a static Allure HTML report from the last test run.
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if [[ ! -d target/allure-results ]] || [[ -z "$(ls -A target/allure-results 2>/dev/null)" ]]; then
  echo "No Allure results in target/allure-results — run tests first."
  exit 1
fi

mvn -q allure:report
REPORT="$ROOT/target/site/allure-maven-plugin/index.html"
echo ""
echo "Report ready: file://$REPORT"
echo "Or run: mvn allure:serve"
