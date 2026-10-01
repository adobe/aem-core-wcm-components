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
# Emits one Selenium-only and one Playwright-only entry for each matching test
# group, so CI can compare the same coverage independently. Untagged Selenium
# classes are selected explicitly; their Playwright equivalents use a tag.

set -euo pipefail

TAG_GROUPS=("group1" "group2" "group3" "group4")
REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
SRC="${REPO_ROOT}/testing/it/e2e-selenium/src/test/java"
SEL_ROOT="${SRC}/com/adobe/cq/wcm/core/components/it/seljup/tests"

fqn_of() {
    local rel="${1#"${SRC}"/}"
    rel="${rel%.java}"
    echo "${rel//\//.}"
}

group_of() {
    grep -oE '@Tag\("group[0-9]+"\)' "$1" 2>/dev/null | head -1 | grep -oE 'group[0-9]+' || true
}

entries=()
for group in "${TAG_GROUPS[@]}"; do
    entries+=("{\"name\":\"${group}-selenium\",\"group\":\"${group}\",\"engine\":\"Selenium\",\"sel_groups\":\"${group}\",\"sel_it_test\":\"\"}")
    entries+=("{\"name\":\"${group}-playwright\",\"group\":\"${group}\",\"engine\":\"Playwright\",\"sel_groups\":\"playwright-${group}\",\"sel_it_test\":\"\"}")
done

ungrouped=()
while IFS= read -r file; do
    [ -z "$file" ] && continue
    if [ -z "$(group_of "$file")" ]; then
        ungrouped+=("$(fqn_of "$file")")
    fi
done < <(find "${SEL_ROOT}" -name "*IT.java" -print | sort)

if [ "${#ungrouped[@]}" -gt 0 ]; then
    joined=$(IFS=,; echo "${ungrouped[*]}")
    entries+=("{\"name\":\"ungrouped-selenium\",\"group\":\"ungrouped\",\"engine\":\"Selenium\",\"sel_groups\":\"\",\"sel_it_test\":\"${joined}\"}")
fi
entries+=("{\"name\":\"ungrouped-playwright\",\"group\":\"ungrouped\",\"engine\":\"Playwright\",\"sel_groups\":\"playwright-ungrouped\",\"sel_it_test\":\"\"}")

printf '{"include":[%s]}\n' "$(IFS=,; echo "${entries[*]}")"
