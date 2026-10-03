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

(
    export AEM_TYPE=sdk
    source "${SCRIPT_DIR}/run-it.sh"
    zip_file="$(mktemp -d)/pkg.zip"
    trap 'rm -rf "$(dirname "${zip_file}")"' EXIT
    echo zip > "${zip_file}"
    curl() { echo '<crx><response><status code="500">Package is broken</status></response></crx>'; }
    if install_package "${AEM_BASE_URL}" "${zip_file}" >/dev/null 2>&1; then
        echo 'Failed package install was not rejected' >&2
        exit 1
    fi
    curl() { echo '<crx><response><data/><status code="200">ok</status></response></crx>'; }
    install_package "${AEM_BASE_URL}" "${zip_file}" >/dev/null
)
echo 'Passed package manager status checks.'

(
    export AEM_TYPE=sdk
    source "${SCRIPT_DIR}/run-it.sh"
    work=$(mktemp -d)
    trap 'rm -rf "${work}"' EXIT
    python3 - "${work}/all.zip" <<'PY'
import io, sys, zipfile
jar = io.BytesIO()
with zipfile.ZipFile(jar, "w") as j:
    j.writestr("META-INF/MANIFEST.MF", "Manifest-Version: 1.0\r\nBundle-SymbolicName: com.example.core;singlet\r\n on:=true\r\nBundle-Version: 2.0.0.SNAPSHOT\r\n")
inner = io.BytesIO()
with zipfile.ZipFile(inner, "w") as z:
    z.writestr("jcr_root/apps/install/core.jar", jar.getvalue())
with zipfile.ZipFile(sys.argv[1], "w") as z:
    z.writestr("jcr_root/etc/packages/content.zip", inner.getvalue())
PY
    [[ "$(bundle_version_in_package "${work}/all.zip" com.example.core)" == 2.0.0.SNAPSHOT ]]
    sleep() { :; }
    wait_for_bundles_settled() { echo settled >> "${work}/calls"; }
    curl() {
        case "$*" in
            */bundles.json) echo '{"data":[{"id":1,"symbolicName":"com.example.core","version":"3.0.0"},{"id":2,"symbolicName":"com.example.core","version":"2.0.0.SNAPSHOT"}]}' ;;
            *) echo "$*" >> "${work}/calls" ;;
        esac
    }
    dedupe_bundle "${AEM_BASE_URL}" com.example.core "${work}/all.zip" >/dev/null
    calls=$(cat "${work}/calls")
    # The newer product copy (3.0.0) is removed; this repo's build stays.
    [[ "${calls}" == *"bundles/1 -d action=uninstall"* && "${calls}" != *"bundles/2 "* ]]
    [[ "${calls}" == *"action=refreshPackages"*settled* ]]
    curl() { echo '{"data":[{"id":1,"symbolicName":"com.example.core","version":"3.0.0"}]}'; }
    if dedupe_bundle "${AEM_BASE_URL}" com.example.core "${work}/all.zip" >/dev/null 2>&1; then
        echo 'Missing repository bundle was not rejected' >&2
        exit 1
    fi
)
echo 'Passed bundle de-duplication by package version.'
