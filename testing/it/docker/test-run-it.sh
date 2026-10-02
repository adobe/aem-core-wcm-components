#!/usr/bin/env bash
# Copyright 2026 Adobe
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#     http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -euo pipefail
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Exercise product configuration and orchestration without Docker or live AEM.
for type in sdk 65 lts; do
    (
        unset AEM_TYPE IT_EXCLUDED_GROUPS SEL_EXCLUDED_GROUPS
        case "${type}" in
            sdk) export AEM_IMAGE="registry/circleci-aem-cloudready:test" ;;
            65) export AEM_IMAGE="registry/circleci-aem:test" ;;
            lts) export AEM_IMAGE="registry/circleci-aem-lts:test" ;;
        esac
        export AEM_AUTHOR_PORT=4504
        source "${SCRIPT_DIR}/run-it.sh"
        [[ "${AEM_TYPE}" == "${type}" ]]
        curl() {
            case "${*: -1}" in
                */status-slingsettings.txt)
                    if [[ "${type}" == sdk ]]; then
                        echo 'Run Modes = [author, sdk]'
                    else
                        echo 'Run Modes = [author]'
                    fi ;;
                */status-productinfo.txt)
                    if [[ "${type}" == lts ]]; then
                        echo 'Installed Products = Adobe Experience Manager (6.5.2.LTS)'
                    else
                        echo 'Installed Products = Adobe Experience Manager (6.5.24.0)'
                    fi ;;
                *) echo "Unexpected curl request: $*" >&2; return 1 ;;
            esac
        }
        verify_aem_type "${AEM_BASE_URL}"
        AEM_TYPE=wrong
        if verify_aem_type "${AEM_BASE_URL}" >/dev/null 2>&1; then
            echo 'Runtime mismatch was not rejected' >&2
            exit 1
        fi
        AEM_TYPE="${type}"
        find_zip() { printf '%s\n' "$*"; }
        install_package() { printf '%s\n' "$2"; }
        dedupe_bundle() { echo "dedupe $2"; }
        packages=$(provision_packages "${AEM_BASE_URL}")
        if [[ "${type}" == sdk ]]; then
            [[ "${packages}" == *"all-*-cloud.zip"* && "${packages}" == *"dedupe "* ]]
            [[ "${IT_EXCLUDED_GROUPS}" == *IgnoreOnCloud && "${SEL_EXCLUDED_GROUPS}" == *IgnoreOnSDK ]]
        else
            [[ "${packages}" == *"all-*.zip"* && "${packages}" != *"dedupe "* ]]
            [[ "${SEL_EXCLUDED_GROUPS}" != *IgnoreOnSDK* && "${IT_EXCLUDED_GROUPS}" != *IgnoreOnCloud* ]]
            [[ "${SEL_EXCLUDED_GROUPS}" != *IgnoreOn64* && "${IT_EXCLUDED_GROUPS}" != *IgnoreOn64* ]]
            if [[ "${type}" == 65 ]]; then
                [[ "${SEL_EXCLUDED_GROUPS}" == *IgnoreOn65* ]]
            else
                [[ "${SEL_EXCLUDED_GROUPS}" == "failing,nested,IgnoreOnLTS" && "${IT_EXCLUDED_GROUPS}" == *IgnoreOnLTS ]]
            fi
        fi
        mvn() { printf '%s\n' "$*"; }
        export DISPLAY=:test
        browser_args=$(run_selenium)
        [[ "${browser_args}" == *"-Dsling.it.instance.url.1=http://localhost:4504"* ]]
        [[ "${browser_args}" == *"-DexcludedGroups=${SEL_EXCLUDED_GROUPS}"* ]]
        WITH_PUBLISH=true
        http_args=$(run_tests)
        [[ "${http_args}" == *"-Dsling.it.instances=2"* ]]
        [[ "${http_args}" == *"-Dsling.it.instance.url.2=${AEM_PUBLISH_URL}"* ]]
        [[ "${http_args}" == *"-DexcludedGroups=${IT_EXCLUDED_GROUPS}"* ]]
        echo "Passed ${type}: product verification, packages, exclusions, browser URL, HTTP author/publish."
    )
done

(
    export AEM_TYPE=65 IT_EXCLUDED_GROUPS="" SEL_EXCLUDED_GROUPS=""
    source "${SCRIPT_DIR}/run-it.sh"
    [[ -z "${IT_EXCLUDED_GROUPS}" && -z "${SEL_EXCLUDED_GROUPS}" ]]
)
echo 'Passed explicit empty exclusion overrides.'

(
    export AEM_TYPE=65 WITH_PUBLISH=true AEM_PUBLISH_PORT=4505
    source "${SCRIPT_DIR}/run-it.sh"
    state=$(mktemp)
    echo 'Adobe Experience Manager (6.5.0)' > "${state}"
    sleep() { :; }
    wait_for_bundles_settled() { :; }
    wait_for_aem() { :; }
    curl() {
        case "$*" in
            *4502/system/console/status-productinfo.txt) echo 'Installed Products = Adobe Experience Manager (6.5.24.0)' ;;
            *4505/system/console/status-productinfo.txt) echo "Installed Products = $(cat "${state}")" ;;
            *cmd=ls) printf '%s' '<crx><response><data><packages>' \
                '<package><group>adobe/cq650/servicepack</group><name>aem-service-pkg</name><version>6.5.9.0</version><downloadName>aem-service-pkg-6.5.9.0.zip</downloadName></package>' \
                '<package><group>adobe/cq650/servicepack</group><name>aem-service-pkg</name><version>6.5.24.0</version><downloadName>aem-service-pkg-6.5.24.0.zip</downloadName></package>' \
                '<package><group>day/cq650</group><name>other</name><version>1</version><downloadName>other.zip</downloadName></package>' \
                '</packages></data></response></crx>' ;;
            *4502/etc/packages/adobe/cq650/servicepack/aem-service-pkg-6.5.24.0.zip*) touch "${*: -2:1}" ;;
            *4505/crx/packmgr/service.jsp) echo 'Adobe Experience Manager (6.5.24.0)' > "${state}" ;;
            *) echo "Unexpected curl request: $*" >&2; return 1 ;;
        esac
    }
    align_publish_with_author >/dev/null
    [[ "$(product_version "${AEM_PUBLISH_URL}")" == 'Adobe Experience Manager (6.5.24.0)' ]]
    curl() { [[ "$*" == *status-productinfo.txt ]] && echo 'Installed Products = Adobe Experience Manager (6.5.24.0)'; }
    align_publish_with_author
    rm -f "${state}"
)
echo 'Passed publish service-pack alignment.'
