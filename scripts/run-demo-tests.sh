#!/usr/bin/env bash
# Back-compat wrapper — use run-tests.sh for new projects.
exec "$(cd "$(dirname "$0")" && pwd)/run-tests.sh" "$@"
