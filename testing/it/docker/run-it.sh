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
# Runs the testing/it/http integration tests against a Dockerized AEM instance.
# Shared by both the local developer flow (testing/it/docker/README.md) and the
# CI job (.github/workflows/maven-it.yml) so the two never drift.
#
# Flow:
#   1. Start the AEM container from a (private) Adobe AEM image, then start an
#      author instance (and a publish instance when WITH_PUBLISH=true) inside it.
#   2. Wait until each instance answers HTTP on the login page.
#   3. Install the built core-components + IT content packages via the CRX
#      Package Manager on each instance (the publish tests GET pre-deployed
#      content, so publish gets the same packages - no replication needed).
#   4. Run the failsafe suite (testing/it/http) against the instance(s).
#
# SCOPE:
#   - Default (WITH_PUBLISH=false): author-only. Only the IT classes that do not
#     use a publish instance are selected (see AUTHOR_ONLY_IT_TEST below).
#   - WITH_PUBLISH=true: also starts/provisions a publish instance and runs the
#     full suite (all *IT.java) against author + publish.
#
# All knobs are environment variables with sane local defaults; CI overrides them
# (image coordinates, KEEP_AEM, etc.). Nothing here is CI-specific, so the same
# invocation works on a developer laptop.
#
# How the image works (verified against circleci-aem-cloudready:27830-v2-openjdk21,
# same shape as the classic circleci-aem image): it is a QuickProvider (qp)
# *server* image - its entrypoint runs `qp.sh start_local_server` (RMI on :55555)
# and then stays alive, but does NOT start any AEM instance by itself. Instances
# are started by qp *client* commands (`qp.sh start --id <id> --port <p> --qs-jar
# .../cq-quickstart.jar`). Because the image also ships qp.sh, the quickstart jar
# and Maven, we run those client commands inside the same container (via docker
# exec): RMI's "localhost" callback is then internally consistent, so we do NOT
# need `--network host` (unlike the CIF repo's two-container split). We only
# publish the instance HTTP ports so host-side Maven/curl can reach them.

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration (override via environment)
# ---------------------------------------------------------------------------
REGISTRY="${REGISTRY:-docker-adobe-cif-release.dr-uw2.adobeitc.com}"
AEM_IMAGE="${AEM_IMAGE:-${REGISTRY}/circleci-aem-cloudready:27830-v2-openjdk21}"
# Infer the product for local runs; CI sets it explicitly alongside the image.
if [[ -z "${AEM_TYPE:-}" ]]; then
    case "${AEM_IMAGE}" in
        */circleci-aem-cloudready:*) AEM_TYPE=sdk ;;
        */circleci-aem-lts:*) AEM_TYPE=lts ;;
        */circleci-aem:*) AEM_TYPE=65 ;;
        *) echo "Set AEM_TYPE=sdk, 65, or lts for image ${AEM_IMAGE}" >&2; exit 1 ;;
    esac
fi
case "${AEM_TYPE}" in
    sdk)
        DEFAULT_IT_EXCLUDED_GROUPS="com.adobe.cq.wcm.core.components.it.http.IgnoreOnCloud"
        DEFAULT_SEL_EXCLUDED_GROUPS="failing,nested,IgnoreOnSDK"
        ;;
    65)
        DEFAULT_IT_EXCLUDED_GROUPS="com.adobe.cq.wcm.core.components.it.http.IgnoreOn65"
        DEFAULT_SEL_EXCLUDED_GROUPS="failing,nested,IgnoreOn65"
        ;;
    lts)
        DEFAULT_IT_EXCLUDED_GROUPS=""
        DEFAULT_SEL_EXCLUDED_GROUPS="failing,nested"
        ;;
    *) echo "Unsupported AEM_TYPE: ${AEM_TYPE} (expected sdk, 65, or lts)" >&2; exit 1 ;;
esac
AEM_AUTHOR_PORT="${AEM_AUTHOR_PORT:-4502}"
AEM_PUBLISH_PORT="${AEM_PUBLISH_PORT:-4503}"
AEM_ADMIN_USER="${AEM_ADMIN_USER:-admin}"
AEM_ADMIN_PASSWORD="${AEM_ADMIN_PASSWORD:-admin}"

# When true, also start an AEM publish instance, provision it, and run the full
# http IT suite (author + publish) instead of the author-only subset.
WITH_PUBLISH="${WITH_PUBLISH:-false}"

# Path (inside the image) to the quickstart jar the qp client starts from, and the
# qp working directory that holds qp.sh + .qpenv.
QP_DIR="${QP_DIR:-/home/circleci/cq}"
QS_JAR="${QS_JAR:-/home/circleci/cq/author/cq-quickstart.jar}"
# Seconds qp waits for a quickstart to finish starting.
QP_START_TIMEOUT="${QP_START_TIMEOUT:-1800}"

# JVM options for the AEM quickstart. qp's default (-Xmx1536m -XX:MaxMetaspaceSize=256m)
# is far too small for a cloud-ready instance on Java 21 - metaspace fills mid-run and
# every subsequent request then fails with OutOfMemoryError: Metaspace. Give it room.
QP_VM_OPTIONS="${QP_VM_OPTIONS:--Xmx4g -XX:MaxMetaspaceSize=1g -Djava.awt.headless=true}"
# JVM options for the publish instance (defaults to the author's).
PUBLISH_VM_OPTIONS="${PUBLISH_VM_OPTIONS:-${QP_VM_OPTIONS}}"

# Test selection. Author-only default keeps the publish-touching classes out when
# WITH_PUBLISH is false; with publish enabled we default to the full suite (empty
# = all *IT.java per the pom's test-all profile). Keep the list in sync with README.
AUTHOR_ONLY_IT_TEST="AdaptiveImageServletIT,ComponentsIT,ExperienceFragmentIT"
if [[ "${WITH_PUBLISH}" == "true" ]]; then
    IT_TEST="${IT_TEST:-}"
else
    IT_TEST="${IT_TEST:-${AUTHOR_ONLY_IT_TEST}}"
fi

# Product-specific JUnit categories; an explicit empty override runs all categories.
IT_EXCLUDED_GROUPS="${IT_EXCLUDED_GROUPS-${DEFAULT_IT_EXCLUDED_GROUPS}}"

# --- Selenium (e2e-selenium) mode ---
# When true, run the Selenium UI suite (testing/it/e2e-selenium) instead of the
# http ITs, driving a LOCAL browser on the host. The browser runs where Maven
# runs, so it reaches AEM at the published localhost port (the e2e-selenium pom
# hard-codes the author URL to localhost:4502) - no browser-container networking.
WITH_SELENIUM="${WITH_SELENIUM:-false}"
SEL_BROWSER="${SEL_BROWSER:-chrome}"
# JUnit5 tag include (e.g. group1) so CI can shard the suite across parallel jobs.
SEL_GROUPS="${SEL_GROUPS:-}"
# JUnit5 tag excludes. Keep the pom's failing,nested and add IgnoreOnSDK for the
# cloud(-ready) target (mirrors the pipeline; the module tags cloud-unsupported
# UI tests with @Tag("IgnoreOnSDK")).
SEL_EXCLUDED_GROUPS="${SEL_EXCLUDED_GROUPS-${DEFAULT_SEL_EXCLUDED_GROUPS}}"
# Re-run failing Selenium tests up to N times before marking them failed. UI tests
# are prone to transient timing flakiness (and search tests can miss the async Oak
# index on the first attempt); a couple of reruns absorb that. 0 disables.
SEL_RERUN="${SEL_RERUN:-2}"
# Failsafe class selection (comma-separated FQNs). Fall back to a single smoke
# class ONLY when neither an explicit selection nor a tag group is given. Do NOT
# use ${SEL_IT_TEST:-<class>} here: an empty SEL_IT_TEST alongside SEL_GROUPS
# (the CI group-shard case) must stay empty so the WHOLE group runs - a :- default
# would wrongly intersect the group with the single smoke class (running 0 tests
# for any group that doesn't contain it).
if [[ -z "${SEL_IT_TEST:-}" && -z "${SEL_GROUPS}" ]]; then
    SEL_IT_TEST="com.adobe.cq.wcm.core.components.it.pw.list.ListV2PwIT"
fi
SEL_IT_TEST="${SEL_IT_TEST:-}"

# Max seconds to wait for an instance to answer HTTP before giving up.
AEM_STARTUP_TIMEOUT="${AEM_STARTUP_TIMEOUT:-600}"

# Leave the container running after the script exits (for debugging).
KEEP_AEM="${KEEP_AEM:-false}"

# prepare: provision and export a stopped author/publish snapshot.
# test: start fresh instances and provision (the local default).
# prepared: start the snapshot and run tests without provisioning.
IT_MODE="${IT_MODE:-test}"
case "${IT_MODE}" in
    test|prepare|prepared) ;;
    *) echo "Unsupported IT_MODE: ${IT_MODE}" >&2; exit 1 ;;
esac
PREPARED_IMAGE="${PREPARED_IMAGE:-wcm-it-prepared:${AEM_TYPE}}"
PREPARED_IMAGE_TAR="${PREPARED_IMAGE_TAR:-/tmp/aem-prepared.tar.gz}"
AEM_BUILD_REF="${AEM_BUILD_REF:-$(git -C "$(dirname "${BASH_SOURCE[0]}")" rev-parse HEAD)}"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
AEM_BASE_URL="http://localhost:${AEM_AUTHOR_PORT}"
AEM_PUBLISH_URL="http://localhost:${AEM_PUBLISH_PORT}"
AEM_CONTAINER="wcm-it-aem-${GITHUB_RUN_ID:-local}-$$"

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

# ---------------------------------------------------------------------------
# Lifecycle
# ---------------------------------------------------------------------------
cleanup() {
    local exit_code=$?
    echo "::group::AEM container logs (${AEM_CONTAINER})"
    docker logs "${AEM_CONTAINER}" 2>&1 || true
    echo "::endgroup::"

    # Best-effort: copy each instance's error.log/stdout.log out of the (still
    # running) container so 500s etc. are diagnosable from the uploaded artifacts.
    # There is one crx-quickstart/logs per started instance (author, publish).
    local logs_dir="${REPO_ROOT}/testing/it/http/target/aem-logs"
    local n=0
    while IFS= read -r err_path; do
        [[ -z "${err_path}" ]] && continue
        n=$((n + 1))
        # Name each dump after the instance folder (…/<id>/crx-quickstart/logs/error.log).
        local inst
        inst=$(echo "${err_path}" | sed -E 's#.*/([^/]+)/crx-quickstart/logs/error.log#\1#')
        mkdir -p "${logs_dir}/${inst}"
        docker cp "${AEM_CONTAINER}:${err_path}" "${logs_dir}/${inst}/error.log" 2>/dev/null || true
        docker cp "${AEM_CONTAINER}:$(dirname "${err_path}")/stdout.log" "${logs_dir}/${inst}/stdout.log" 2>/dev/null || true
    done < <(docker exec "${AEM_CONTAINER}" bash -lc \
        'find /home/circleci -maxdepth 6 -path "*crx-quickstart/logs/error.log" 2>/dev/null' 2>/dev/null | tr -d '\r')
    [[ "${n}" -gt 0 ]] && echo "Copied AEM logs from ${n} instance(s) to ${logs_dir}"

    if [[ "${KEEP_AEM}" == "true" ]]; then
        log "KEEP_AEM=true - leaving ${AEM_CONTAINER} running (docker rm -f it manually when done)."
    else
        docker rm -f "${AEM_CONTAINER}" >/dev/null 2>&1 || true
    fi
    exit "${exit_code}"
}

# Start one AEM instance inside the container via the qp client.
# Args: <id> <runmode> <port> <vm-options>
# qp.sh runs the quickstart via `eval "java ... $*"`, which word-splits its args.
# The multi-token --vm-options value must therefore reach that eval still wrapped
# in literal single quotes, so it survives as ONE java argument. The \"'...'\"
# wrapping keeps the single quotes literal through the container's `bash -lc`.
start_instance() {
    local id="$1" runmode="$2" port="$3" vmopts="$4"
    log "Starting AEM ${id} (qp start --id ${id} --runmode ${runmode} --port ${port})"
    docker exec "${AEM_CONTAINER}" bash -lc \
        "cd ${QP_DIR} && ./qp.sh -v start --id ${id} --runmode ${runmode} --port ${port} --qs-jar ${QS_JAR} --timeout ${QP_START_TIMEOUT} --vm-options \"'${vmopts}'\""
}

start_aem() {
    log "Starting qp server container from ${AEM_IMAGE}"
    # Publish the instance HTTP ports so the host can reach them once started.
    local ports=(-p "${AEM_AUTHOR_PORT}:4502")
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        ports+=(-p "${AEM_PUBLISH_PORT}:4503")
    fi
    docker run -d --name "${AEM_CONTAINER}" "${ports[@]}" "${AEM_IMAGE}"

    log "Waiting for the qp server to accept client commands"
    local deadline=$(( SECONDS + 120 ))
    until docker exec "${AEM_CONTAINER}" bash -lc "cd ${QP_DIR} && ./qp.sh get_quickstarts" >/dev/null 2>&1; do
        if (( SECONDS >= deadline )); then
            echo "qp server did not become ready within 120s" >&2
            return 1
        fi
        sleep 3
    done

    start_instance author author 4502 "${QP_VM_OPTIONS}"
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        start_instance publish publish 4503 "${PUBLISH_VM_OPTIONS}"
    fi
}

# Wait for one instance to answer HTTP. Args: <base-url> <label>
wait_for_aem() {
    local base="$1" label="$2"
    log "Waiting for AEM ${label} at ${base} (timeout ${AEM_STARTUP_TIMEOUT}s)"
    local deadline=$(( SECONDS + AEM_STARTUP_TIMEOUT ))
    until [[ "$(curl -sf -o /dev/null -w '%{http_code}' \
        -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        "${base}/libs/granite/core/content/login.html" 2>/dev/null)" == "200" ]]; do
        if (( SECONDS >= deadline )); then
            echo "AEM ${label} did not become available within ${AEM_STARTUP_TIMEOUT}s" >&2
            return 1
        fi
        sleep 5
    done
    log "AEM ${label} is up."
}

# Validate the product from the runtime, not just the image tag.
verify_aem_type() {
    local base="$1" settings product detected
    settings=$(curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        "${base}/system/console/status-slingsettings.txt")
    if [[ "${settings}" =~ Run\ Modes.*sdk ]]; then
        detected=sdk
    else
        product=$(curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
            "${base}/system/console/status-productinfo.txt")
        if [[ "${product}" == *".LTS"* ]]; then
            detected=lts
        elif [[ "${product}" == *"Adobe Experience Manager (6.5."* ]]; then
            detected=65
        else
            echo "Unrecognized AEM product at ${base}: ${product}" >&2
            return 1
        fi
    fi
    if [[ "${detected}" != "${AEM_TYPE}" ]]; then
        echo "AEM product mismatch at ${base}: expected ${AEM_TYPE}, detected ${detected}" >&2
        return 1
    fi
    log "Verified AEM ${detected} at ${base}"
}

# Upload + install one content package. Args: <base-url> <zip>
install_package() {
    local base="$1" zip="$2"
    if [[ ! -f "${zip}" ]]; then
        echo "Package not found: ${zip} (did you build the project first? see README)" >&2
        return 1
    fi
    log "Installing $(basename "${zip}") -> ${base}"
    curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        -F file=@"${zip}" -F name="$(basename "${zip}")" -F force=true -F install=true \
        "${base}/crx/packmgr/service.jsp" >/dev/null
}

# Install + start an OSGi bundle jar via the Felix web console. Args: <base-url> <jar>
install_bundle() {
    local base="$1" jar="$2"
    if [[ ! -f "${jar}" ]]; then
        echo "Bundle not found: ${jar} (did you build the project first? see README)" >&2
        return 1
    fi
    log "Installing bundle $(basename "${jar}") -> ${base}"
    curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        -F action=install -F bundlestartlevel=20 -F bundlestart=start -F refreshPackages=true \
        -F bundlefile=@"${jar}" \
        "${base}/system/console/bundles" >/dev/null
}

# The base cloud-ready SDK image ships Core Components as a product feature (bundles
# in the QuickStart's launchpad). After we install this repo's `all` package, BOTH the
# product version and this repo's version of a bundle are Active, and they cross-wire
# the exported `...models.*` packages -> OSGi ClassCastExceptions (e.g. the Search
# servlet 500s -> SearchIT shows 0 results). Fix: keep the highest version (this repo's
# build) and UNINSTALL the rest. Uninstall - not stop - is required: a *stopped* product
# bundle is reverted to Active by the SDK's Sling installer, whereas an uninstalled one
# stays gone for the run. Args: <base-url> <symbolic-name>
dedupe_bundle() {
    local base="$1" bsn="$2"
    local ids=""
    # The `all` package installs its bundles ASYNChronously, so this repo's version may
    # not be registered the instant the package upload returns. Poll (up to ~2 min) until
    # a second (duplicate) version shows up, then uninstall the older (product) copies.
    for _ in $(seq 1 40); do
        ids=$(curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/bundles.json" 2>/dev/null \
            | python3 -c "
import json, re, sys
d = json.load(sys.stdin)
bs = [b for b in d.get('data', []) if b.get('symbolicName') == '${bsn}']
def ver(b):
    return [int(x) for x in re.findall(r'\d+', b.get('version', ''))]
if len(bs) > 1:
    bs.sort(key=ver)                      # highest version last (= this repo's build)
    print(' '.join(str(b['id']) for b in bs[:-1]))   # ids of the older product copies
" 2>/dev/null)
        [[ -n "${ids}" ]] && break
        sleep 3
    done
    if [[ -z "${ids}" ]]; then
        log "No duplicate ${bsn} bundle found to remove (only one version present)"
        return 0
    fi
    local id
    for id in ${ids}; do
        log "Uninstalling duplicate product bundle ${bsn} (id ${id})"
        curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
            -X POST "${base}/system/console/bundles/${id}" -d action=uninstall >/dev/null || true
    done
    sleep 3
    # Uninstalling the product copy disrupts Sling Model registration for the surviving
    # bundle (its models drop out -> "Could not find an adapter factory for ...Search"
    # -> component 500s). Restart the survivor so its @Model adapter factories re-register.
    local keep
    keep=$(curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/bundles.json" 2>/dev/null \
        | python3 -c "import json,sys; d=json.load(sys.stdin); print(next((str(b['id']) for b in d.get('data',[]) if b.get('symbolicName')=='${bsn}'), ''))" 2>/dev/null)
    if [[ -n "${keep}" ]]; then
        log "Restarting ${bsn} (id ${keep}) to re-register its Sling models"
        curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" -X POST "${base}/system/console/bundles/${keep}" -d action=stop >/dev/null || true
        sleep 3
        curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" -X POST "${base}/system/console/bundles/${keep}" -d action=start >/dev/null || true
        sleep 6
    fi
}

# Resolve the newest matching built package zip for a module target dir. Any glob
# in $2 is expanded; $3 (optional) is an extended-regex of basenames to EXCLUDE.
find_zip() {
    local dir="$1" pattern="$2" exclude="${3:-}"
    # shellcheck disable=SC2012
    local matches
    matches=$(ls -t "${dir}"/${pattern} 2>/dev/null || true)
    if [[ -n "${exclude}" ]]; then
        matches=$(printf '%s\n' "${matches}" | grep -Ev "${exclude}" || true)
    fi
    printf '%s\n' "${matches}" | head -n 1
}

# Install the core-components + IT content packages onto one instance. Args: <base-url>
# Order matters: components first, then immutable test apps, OSGi config, then mutable
# test content. The `all` module builds both a classic zip (no classifier) and a
# `-cloud` classified zip; choose the package matching the product.
provision_packages() {
    local base="$1"
    if [[ "${AEM_TYPE}" == "sdk" ]]; then
        install_package "${base}" "$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*-cloud.zip')"
    else
        install_package "${base}" "$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*.zip' '\-cloud\.zip$')"
    fi
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.apps/target" 'core.wcm.components.it.ui.apps-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.config/target" 'core.wcm.components.it.ui.config-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.content/target" 'core.wcm.components.it.ui.content-*.zip')"
    # The base SDK image already ships Core Components as a product feature, so the `all`
    # package above leaves TWO active versions of the core/AMP bundles. Remove the older
    # (product) copies so only this repo's build remains - otherwise the two versions
    # cross-wire the models packages and components fail with ClassCastExceptions.
    if [[ "${AEM_TYPE}" == "sdk" ]]; then
        dedupe_bundle "${base}" "com.adobe.cq.core.wcm.components.core"
        dedupe_bundle "${base}" "com.adobe.cq.core.wcm.components.extensions.amp"
    fi
}

product_version() {
    curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "$1/system/console/status-productinfo.txt" \
        | grep -m1 -oE 'Adobe Experience Manager \([^)]*\)' || true
}

# Some images ship a service-packed author but start publish from the GA
# quickstart jar. Copy the author's service pack so both run the same product.
align_publish_with_author() {
    local author_version publish_version sp_path sp_zip deadline
    author_version=$(product_version "${AEM_BASE_URL}")
    publish_version=$(product_version "${AEM_PUBLISH_URL}")
    [[ "${author_version}" == "${publish_version}" ]] && return
    log "Publish runs ${publish_version:-unknown}, author runs ${author_version:-unknown}; installing author's service pack on publish"
    sp_path=$(curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${AEM_BASE_URL}/crx/packmgr/service.jsp?cmd=ls" \
        | python3 -c '
import sys, xml.etree.ElementTree as ET
packages = [p for p in ET.parse(sys.stdin).iter("package") if (p.findtext("name") or "").startswith("aem-service-pkg")]
packages.sort(key=lambda p: [int(x) if x.isdigit() else 0 for x in (p.findtext("version") or "").split(".")])
if packages:
    print("/etc/packages/" + packages[-1].findtext("group") + "/" + packages[-1].findtext("downloadName"))
')
    if [[ -z "${sp_path}" ]]; then
        echo "No aem-service-pkg package found on author to align publish" >&2
        return 1
    fi
    sp_zip="$(mktemp -d)/$(basename "${sp_path}")"
    curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" -o "${sp_zip}" "${AEM_BASE_URL}${sp_path}"
    log "Installing ${sp_path} -> ${AEM_PUBLISH_URL}"
    # The service pack restarts bundles while installing, so the upload request
    # may be cut off; the product version below is the source of truth.
    curl -sS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" --max-time 1800 \
        -F file=@"${sp_zip}" -F name="$(basename "${sp_zip}")" -F force=true -F install=true \
        "${AEM_PUBLISH_URL}/crx/packmgr/service.jsp" >/dev/null || true
    rm -rf "$(dirname "${sp_zip}")"
    deadline=$(( SECONDS + 1800 ))
    until [[ "$(product_version "${AEM_PUBLISH_URL}")" == "${author_version}" ]]; do
        if (( SECONDS >= deadline )); then
            echo "Publish did not reach ${author_version} within 1800s" >&2
            return 1
        fi
        sleep 15
    done
    wait_for_bundles_settled "${AEM_PUBLISH_URL}"
    wait_for_aem "${AEM_PUBLISH_URL}" publish
    log "Publish aligned to ${author_version}"
}

# Wait until the Felix bundle summary stops changing for three polls.
wait_for_bundles_settled() {
    local base="$1" previous="" current stable=0 deadline=$(( SECONDS + 900 ))
    while (( stable < 3 )); do
        if (( SECONDS >= deadline )); then
            echo "Bundles at ${base} did not settle within 900s" >&2
            return 1
        fi
        sleep 10
        current=$(curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/bundles.json" 2>/dev/null \
            | python3 -c 'import json, sys; print(json.load(sys.stdin)["s"])' 2>/dev/null || true)
        if [[ -n "${current}" && "${current}" == "${previous}" ]]; then
            stable=$(( stable + 1 ))
        else
            stable=0
        fi
        previous="${current}"
    done
}

provision() {
    log "Provisioning author"
    provision_packages "${AEM_BASE_URL}"
    # Server-side IT support bundle (TestTransformerFactory etc.), used by author-side
    # ITs like TableOfContentsFilterIT (the 'core-components-test-transformer' rewriter).
    # It is a plain OSGi bundle deployed via sling:install in the normal pipeline - not
    # embedded in any content package - so install it explicitly on the author.
    install_bundle "${AEM_BASE_URL}" \
        "$(find_zip "${REPO_ROOT}/testing/it/it.core/target" 'core.wcm.components.it.core-*.jar' '\-(sources|javadoc)\.jar$')"

    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        align_publish_with_author
        log "Provisioning publish"
        # Publish tests GET pre-deployed content, so the publish instance gets the same
        # content packages (no replication needed).
        provision_packages "${AEM_PUBLISH_URL}"
    fi
}

# Package installation starts bundles asynchronously. Do not freeze an image
# until the repository build and author-side test support are active.
wait_for_provisioning() {
    local base="$1" support="$2" deadline=$(( SECONDS + 180 ))
    while true; do
        if curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/bundles.json" \
            | python3 -c '
import json, sys
data = json.load(sys.stdin)["data"]
required = ["com.adobe.cq.core.wcm.components.core", "com.adobe.cq.core.wcm.components.extensions.amp"]
if sys.argv[1] == "true":
    required.append("com.adobe.cq.core.wcm.components.it.core")
missing = [name for name in required if not any(b.get("symbolicName") == name and b.get("state") == "Active" for b in data)]
if missing:
    print("Waiting for active bundles: " + ", ".join(missing), file=sys.stderr)
    sys.exit(1)
' "${support}"; then
            return
        fi
        if (( SECONDS >= deadline )); then
            echo "Provisioning did not settle at ${base} within 180s" >&2
            return 1
        fi
        sleep 5
    done
}

export_prepared_image() {
    if [[ "${WITH_PUBLISH}" != "true" ]]; then
        echo "IT_MODE=prepare requires WITH_PUBLISH=true" >&2
        return 1
    fi
    wait_for_provisioning "${AEM_BASE_URL}" true
    wait_for_provisioning "${AEM_PUBLISH_URL}" false
    # SIGTERM runs AEM's JVM shutdown hook. qp stop can hang after the JVM exits.
    # Never force-kill or snapshot a JVM that did not close its repository.
    for id in publish author; do
        local pid
        pid=$(docker exec "${AEM_CONTAINER}" pgrep -f "^java .* -jar ${QP_DIR}/${id}/cq-quickstart.jar ")
        if [[ ! "${pid}" =~ ^[0-9]+$ ]]; then
            echo "Expected exactly one running AEM ${id} JVM, found: ${pid}" >&2
            return 1
        fi
        docker exec "${AEM_CONTAINER}" kill -TERM "${pid}"
        local deadline=$(( SECONDS + 180 ))
        while docker exec "${AEM_CONTAINER}" pgrep -f "^java .* -jar ${QP_DIR}/${id}/cq-quickstart.jar "; do
            if (( SECONDS >= deadline )); then
                echo "AEM ${id} did not stop cleanly within 180s; refusing to snapshot" >&2
                return 1
            fi
            sleep 3
        done
    done
    printf '%s\n%s\n' "${AEM_TYPE}" "${AEM_BUILD_REF}" \
        | docker exec -i "${AEM_CONTAINER}" bash -c 'cat > /home/circleci/cq/.core-components-prepared'
    log "Exporting prepared author/publish image ${PREPARED_IMAGE}"
    # Export/import flattens the stopped filesystem into a self-contained layer.
    # Preserve the base image startup configuration; no registry layers are needed
    # to load it on another runner (including containerd-backed Docker engines).
    python3 - "${AEM_CONTAINER}" "${PREPARED_IMAGE}" <<'PY'
import json, subprocess, sys
container, image = sys.argv[1:]
info = json.loads(subprocess.check_output(["docker", "inspect", container]))[0]
config = info["Config"]
base = json.loads(subprocess.check_output(["docker", "image", "inspect", info["Image"]]))[0]
args = ["docker", "import", "--platform", base["Os"] + "/" + base["Architecture"]]
for key in ("Entrypoint", "Cmd"):
    if config.get(key):
        args += ["--change", key.upper() + " " + json.dumps(config[key])]
for key in ("User", "WorkingDir"):
    if config.get(key):
        args += ["--change", ("WORKDIR" if key == "WorkingDir" else "USER") + " " + config[key]]
for entry in config.get("Env", []):
    key, value = entry.split("=", 1)
    args += ["--change", "ENV " + key + "=" + json.dumps(value)]
args += ["-", image]
export = subprocess.Popen(["docker", "export", container], stdout=subprocess.PIPE)
try:
    subprocess.run(args, stdin=export.stdout, check=True)
finally:
    export.stdout.close()
    result = export.wait()
if result:
    raise SystemExit("docker export failed: " + str(result))
PY
    docker save "${PREPARED_IMAGE}" | gzip -1 > "${PREPARED_IMAGE_TAR}"
}

verify_prepared_image() {
    local marker
    marker=$(docker exec "${AEM_CONTAINER}" cat /home/circleci/cq/.core-components-prepared)
    if [[ "${marker}" != "$(printf '%s\n%s' "${AEM_TYPE}" "${AEM_BUILD_REF}")" ]]; then
        echo "Prepared image does not match AEM ${AEM_TYPE} / build ${AEM_BUILD_REF}" >&2
        return 1
    fi
    wait_for_provisioning "${AEM_BASE_URL}" true
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        wait_for_provisioning "${AEM_PUBLISH_URL}" false
    fi
}

run_tests() {
    local -a args=(
        -B -f "${REPO_ROOT}/testing/it/http/pom.xml" verify
        -Dit
        -Dsling.it.instance.url.1="${AEM_BASE_URL}"
        -Dsling.it.instance.runmode.1=author
        -Dsling.it.instance.adminUser.1="${AEM_ADMIN_USER}"
        -Dsling.it.instance.adminPassword.1="${AEM_ADMIN_PASSWORD}"
        -Dgranite.it.author.url="${AEM_BASE_URL}"
    )
    if [[ -n "${IT_TEST}" ]]; then
        args+=(-Dit.test="${IT_TEST}")
    fi
    args+=(-DexcludedGroups="${IT_EXCLUDED_GROUPS}")
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        log "Running http ITs (author + publish): ${IT_TEST:-<all>}"
        args+=(
            -Dsling.it.instances=2
            -Dsling.it.instance.url.2="${AEM_PUBLISH_URL}"
            -Dsling.it.instance.runmode.2=publish
            -Dsling.it.instance.adminUser.2="${AEM_ADMIN_USER}"
            -Dsling.it.instance.adminPassword.2="${AEM_ADMIN_PASSWORD}"
            -Dgranite.it.publish.url="${AEM_PUBLISH_URL}"
        )
    else
        log "Running http ITs (author-only): ${IT_TEST}"
        args+=(-Dsling.it.instances=1)
    fi
    mvn "${args[@]}"
}

run_selenium() {
    # The test module is built standalone, so it would otherwise resolve
    # e2e-selenium-utils from ~/.m2 - which may be a stale SNAPSHOT (e.g. CI's
    # Maven cache is keyed on pom.xml hashes only). Install it from source first.
    log "Installing e2e-selenium-utils from source"
    mvn -B -q -f "${REPO_ROOT}/testing/it/e2e-selenium-utils/pom.xml" install -DskipTests

    log "Running Playwright ITs (AEM ${AEM_TYPE}, local Chrome): ${SEL_IT_TEST:-<all>}"
    # -Dsel.jup.default.browser selects a LOCAL browser (vs the module's default
    # Chrome-in-Docker). The pom's test-all profile pins the author URL to
    # localhost:4502, which the host-local browser can reach directly.
    local -a args=(
        -B -f "${REPO_ROOT}/testing/it/e2e-selenium/pom.xml" verify -Ptest-all
        -Dsel.jup.default.browser="${SEL_BROWSER}"
        -Dsling.it.instance.url.1="${AEM_BASE_URL}"
        -Dsling.it.instance.adminUser.1="${AEM_ADMIN_USER}"
        -Dsling.it.instance.adminPassword.1="${AEM_ADMIN_PASSWORD}"
        -Dgranite.it.author.url="${AEM_BASE_URL}"
    )
    if [[ -n "${SEL_IT_TEST}" ]]; then
        args+=(-Dit.test="${SEL_IT_TEST}")
    fi
    if [[ -n "${SEL_GROUPS}" ]]; then
        args+=(-Dgroups="${SEL_GROUPS}")
    fi
    args+=(-DexcludedGroups="${SEL_EXCLUDED_GROUPS}")
    if [[ "${SEL_RERUN}" != "0" ]]; then
        args+=(-Dfailsafe.rerunFailingTestsCount="${SEL_RERUN}")
    fi
    # Headless display: the CI workflow (GitHub-hosted ubuntu-latest, no GUI
    # session) already starts Xvfb + fluxbox and exports DISPLAY before this
    # script runs, so this is normally a no-op there. This branch exists for
    # running the script standalone on a Linux box without that setup (falls back
    # to xvfb-run if available). On macOS (no xvfb-run, a real GUI session) it's
    # skipped and Chrome runs natively - the flow validated for local dev.
    if [[ -z "${DISPLAY:-}" ]] && command -v xvfb-run >/dev/null 2>&1; then
        xvfb-run -a mvn "${args[@]}"
    else
        mvn "${args[@]}"
    fi
}

main() {
    trap cleanup EXIT
    command -v docker >/dev/null || { echo "docker is required" >&2; exit 1; }
    start_aem
    wait_for_aem "${AEM_BASE_URL}" author
    verify_aem_type "${AEM_BASE_URL}"
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        wait_for_aem "${AEM_PUBLISH_URL}" publish
        verify_aem_type "${AEM_PUBLISH_URL}"
    fi
    if [[ "${IT_MODE}" == "prepared" ]]; then
        verify_prepared_image
    else
        provision
    fi
    if [[ "${IT_MODE}" == "prepare" ]]; then
        export_prepared_image
        return
    fi
    if [[ "${WITH_SELENIUM}" == "true" ]]; then
        run_selenium
    else
        run_tests
    fi
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
    main "$@"
fi
