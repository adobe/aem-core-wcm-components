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
# Emits the GitHub Actions matrix (JSON: {"include":[...]}) for the Selenium
# suite, sharded by the tests' JUnit @Tag groups: one leg per group1..group4
# (selected via -Dgroups, i.e. the tag itself), plus one "ungrouped" leg
# (explicit class list) for the handful of classes that carry no group tag - so
# every e2e-selenium test still runs. Five legs total.
#
# Each matrix entry has:
#   name        - leg id (group1..group4, ungrouped)
#   sel_groups  - JUnit tag to pass as -Dgroups (empty for the ungrouped leg)
#   sel_it_test - comma-separated failsafe -Dit.test class FQNs (only set for
#                 the ungrouped leg; empty for tag-based legs so the whole
#                 group runs)
#
# Consumed by .github/workflows/maven-it.yml via fromJSON(). Run standalone to
# inspect the split:  bash testing/it/docker/gen-selenium-matrix.sh | jq .

set -uo pipefail

TAG_GROUPS=("group1" "group2" "group3" "group4")

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
SRC="${REPO_ROOT}/testing/it/e2e-selenium/src/test/java"
PKG_ROOT="com/adobe/cq/wcm/core/components/it/seljup/tests"

fqn_of() {
    local rel="${1#"${SRC}"/}"
    rel="${rel%.java}"
    echo "${rel//\//.}"
}

group_of() {
    grep -oE '@Tag\("group[0-9]+"\)' "$1" 2>/dev/null | head -1 | grep -oE 'group[0-9]+'
}

entries=()

for grp in "${TAG_GROUPS[@]}"; do
    entries+=("{\"name\":\"${grp}\",\"sel_groups\":\"${grp}\",\"sel_it_test\":\"\"}")
done

ungrouped=()
while IFS= read -r f; do
    [ -z "$f" ] && continue
    if [ -z "$(group_of "$f")" ]; then
        ungrouped+=("$(fqn_of "$f")")
    fi
done < <(find "${SRC}/${PKG_ROOT}" -name "*IT.java" 2>/dev/null | sort)

if [ "${#ungrouped[@]}" -gt 0 ]; then
    joined=$(IFS=,; echo "${ungrouped[*]}")
    entries+=("{\"name\":\"ungrouped\",\"sel_groups\":\"\",\"sel_it_test\":\"${joined}\"}")
fi

printf '{"include":[%s]}\n' "$(IFS=,; echo "${entries[*]}")"
