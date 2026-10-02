#!/usr/bin/env bash
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# Copyright 2026 Adobe
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
#
# Emits one Playwright CI job per JUnit5 group tag (@Tag("playwright-groupN")),
# derived from the test sources so new groups get a job automatically.
# Fails if any *IT class has no group tag: such a class would never run in CI.
# The same rule is enforced at build time by ItGroupTagTest.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
SRC="${REPO_ROOT}/testing/it/e2e-selenium/src/test/java"
TAG_RE='@Tag\("playwright-group[0-9]+"\)'

ungrouped=()
while IFS= read -r file; do
    grep -qE "${TAG_RE}" "$file" || ungrouped+=("${file#"${SRC}"/}")
done < <(find "${SRC}" -name "*IT.java" -print | sort)

if [ "${#ungrouped[@]}" -gt 0 ]; then
    echo "ERROR: IT classes without a @Tag(\"playwright-groupN\") (add one to the fastest group):" >&2
    printf '  %s\n' "${ungrouped[@]}" >&2
    exit 1
fi

entries=()
while IFS= read -r group; do
    entries+=("{\"name\":\"${group}-playwright\",\"group\":\"${group}\",\"engine\":\"Playwright\",\"sel_groups\":\"playwright-${group}\",\"sel_it_test\":\"\"}")
done < <(grep -rhoE "${TAG_RE}" "${SRC}" | grep -oE 'group[0-9]+' | sort -u -V)

printf '{"include":[%s]}\n' "$(IFS=,; echo "${entries[*]}")"
