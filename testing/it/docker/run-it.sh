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
        DEFAULT_E2E_EXCLUDED_GROUPS="failing,nested,IgnoreOnSDK"
        ;;
    65)
        DEFAULT_IT_EXCLUDED_GROUPS="com.adobe.cq.wcm.core.components.it.http.IgnoreOn65"
        DEFAULT_E2E_EXCLUDED_GROUPS="failing,nested,IgnoreOn65"
        ;;
    lts)
        DEFAULT_IT_EXCLUDED_GROUPS="com.adobe.cq.wcm.core.components.it.http.IgnoreOnLTS"
        DEFAULT_E2E_EXCLUDED_GROUPS="failing,nested,IgnoreOnLTS"
        ;;
    *) echo "Unsupported AEM_TYPE: ${AEM_TYPE} (expected sdk, 65, or lts)" >&2; exit 1 ;;
esac
AEM_AUTHOR_PORT="${AEM_AUTHOR_PORT:-4502}"
AEM_PUBLISH_PORT="${AEM_PUBLISH_PORT:-4503}"
AEM_ADMIN_USER="${AEM_ADMIN_USER:-admin}"
AEM_ADMIN_PASSWORD="${AEM_ADMIN_PASSWORD:-admin}"
# Servlet context path both instances are served under (e.g. /cp), as on the
# internal pipeline; empty serves AEM at the root. Every URL below carries it.
AEM_CONTEXT_PATH="${AEM_CONTEXT_PATH:-}"
if [[ -n "${AEM_CONTEXT_PATH}" && ! "${AEM_CONTEXT_PATH}" =~ ^(/[A-Za-z0-9._-]+)+$ ]]; then
    echo "AEM_CONTEXT_PATH must be empty or like /cp (leading slash, no trailing slash): ${AEM_CONTEXT_PATH}" >&2
    exit 1
fi

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

# The Selenium-era WITH_SELENIUM/SEL_* variables were renamed to WITH_E2E/E2E_*. Fail fast rather than
# silently running the http ITs when an old invocation is used.
for legacy_var in WITH_SELENIUM SEL_BROWSER SEL_EXCLUDED_GROUPS SEL_GROUPS SEL_IT_TEST SEL_RERUN; do
    if declare -p "${legacy_var}" >/dev/null 2>&1; then
        echo "${legacy_var} is no longer supported; use WITH_E2E / E2E_* instead (see testing/it/docker/README.md)" >&2
        exit 1
    fi
done
unset legacy_var

# --- Browser e2e (e2e-testing) mode ---
# When true, run the Playwright UI suite (testing/it/e2e-testing) instead of the
# http ITs, driving a LOCAL browser on the host. The browser runs where Maven
# runs, so it reaches AEM at the published localhost port (the e2e-testing pom
# hard-codes the author URL to localhost:4502) - no browser-container networking.
WITH_E2E="${WITH_E2E:-false}"
# JUnit5 tag include (e.g. group1) so CI can shard the suite across parallel jobs.
E2E_GROUPS="${E2E_GROUPS:-}"
# JUnit5 tag excludes. Keep the pom's failing,nested and add IgnoreOnSDK for the
# cloud(-ready) target (mirrors the pipeline; the module tags cloud-unsupported
# UI tests with @Tag("IgnoreOnSDK")).
E2E_EXCLUDED_GROUPS="${E2E_EXCLUDED_GROUPS-${DEFAULT_E2E_EXCLUDED_GROUPS}}"
# Re-run failing browser tests up to N times before marking them failed. UI tests
# are prone to transient timing flakiness (and search tests can miss the async Oak
# index on the first attempt); a couple of reruns absorb that. 0 disables.
E2E_RERUN="${E2E_RERUN:-2}"
# Failsafe class selection (comma-separated FQNs). Fall back to a single smoke
# class ONLY when neither an explicit selection nor a tag group is given. Do NOT
# use ${E2E_IT_TEST:-<class>} here: an empty E2E_IT_TEST alongside E2E_GROUPS
# (the CI group-shard case) must stay empty so the WHOLE group runs - a :- default
# would wrongly intersect the group with the single smoke class (running 0 tests
# for any group that doesn't contain it).
if [[ -z "${E2E_IT_TEST:-}" && -z "${E2E_GROUPS}" ]]; then
    E2E_IT_TEST="com.adobe.cq.wcm.core.components.it.pw.list.ListV2PwIT"
fi
E2E_IT_TEST="${E2E_IT_TEST:-}"

# Max seconds to wait for an instance to answer HTTP before giving up.
AEM_STARTUP_TIMEOUT="${AEM_STARTUP_TIMEOUT:-600}"

# Leave the container running after the script exits (for debugging).
KEEP_AEM="${KEEP_AEM:-false}"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
AEM_BASE_URL="http://localhost:${AEM_AUTHOR_PORT}${AEM_CONTEXT_PATH}"
AEM_PUBLISH_URL="http://localhost:${AEM_PUBLISH_PORT}${AEM_CONTEXT_PATH}"
AEM_CONTAINER="wcm-it-aem-${GITHUB_RUN_ID:-local}-$$"

# --- Code coverage (JaCoCo) ---
# When true, every AEM JVM runs the JaCoCo agent (limited to this repo's Java
# packages) as a tcpserver bound to the container's loopback. On exit the
# execution data is dumped from each instance and reported (XML + HTML) against
# the built core and AMP bundle jars into IT_COVERAGE_DIR. The agent version is
# independent of the unit-test JaCoCo and must support the images' Java 21.
IT_COVERAGE="${IT_COVERAGE:-false}"
JACOCO_IT_VERSION="${JACOCO_IT_VERSION:-0.8.13}"
if [[ "${WITH_E2E}" == "true" ]]; then
    IT_COVERAGE_DIR="${IT_COVERAGE_DIR:-${REPO_ROOT}/testing/it/e2e-testing/target/jacoco-it}"
else
    IT_COVERAGE_DIR="${IT_COVERAGE_DIR:-${REPO_ROOT}/testing/it/http/target/jacoco-it}"
fi
# Where the JaCoCo agent + CLI jars are kept; CI fetches them once and ships them here.
JACOCO_LIB_DIR="${JACOCO_LIB_DIR:-${REPO_ROOT}/testing/it/docker/target/jacoco-${JACOCO_IT_VERSION}}"
# Core + AMP extension packages (JaCoCo separates patterns with ":").
JACOCO_INCLUDES="com.adobe.cq.wcm.core.components.*:com.adobe.cq.wcm.core.extensions.amp.*"
JACOCO_CONTAINER_DIR=/tmp/jacoco
JACOCO_AUTHOR_PORT=6300
JACOCO_PUBLISH_PORT=6301

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

# ---------------------------------------------------------------------------
# Lifecycle
# ---------------------------------------------------------------------------
cleanup() {
    local exit_code=$?
    # Coverage is best-effort: it must neither mask the test outcome nor skip teardown.
    collect_coverage || echo "WARNING: collecting IT coverage failed" >&2
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

# Download the JaCoCo agent + CLI jars into JACOCO_LIB_DIR, unless already there
# (CI ships them from the prep job). Run outside any pom so the goal executes
# standalone instead of once per reactor module.
fetch_jacoco() {
    if [[ -f "${JACOCO_LIB_DIR}/org.jacoco.agent-runtime.jar" && -f "${JACOCO_LIB_DIR}/org.jacoco.cli-nodeps.jar" ]]; then
        return 0
    fi
    mkdir -p "${JACOCO_LIB_DIR}" || return 1
    local artifact
    for artifact in "org.jacoco.agent:${JACOCO_IT_VERSION}:jar:runtime" "org.jacoco.cli:${JACOCO_IT_VERSION}:jar:nodeps"; do
        (cd "${JACOCO_LIB_DIR}" && mvn -B -q org.apache.maven.plugins:maven-dependency-plugin:3.6.1:copy \
            -Dartifact="org.jacoco:${artifact}" -DoutputDirectory="${JACOCO_LIB_DIR}" -Dmdep.stripVersion=true) || return 1
    done
}

# Extra JVM option serving the instance under AEM_CONTEXT_PATH. qp has no CLI flag
# for it; its config-file contextPath sets this same Felix HTTP property.
context_path_opts() {
    [[ -n "${AEM_CONTEXT_PATH}" ]] || return 0
    printf ' -Dorg.apache.felix.http.context_path=%s' "${AEM_CONTEXT_PATH}"
}

# Extra JVM options attaching the JaCoCo agent. Args: <session-id> <port>
jacoco_agent_opts() {
    [[ "${IT_COVERAGE}" == "true" ]] || return 0
    printf ' -javaagent:%s/jacocoagent.jar=output=tcpserver,address=127.0.0.1,port=%s,sessionid=%s,includes=%s' \
        "${JACOCO_CONTAINER_DIR}" "$2" "$1" "${JACOCO_INCLUDES}"
}

# Copy the JaCoCo jars into the container, where the AEM JVMs and the dump run.
install_jacoco() {
    log "Enabling JaCoCo ${JACOCO_IT_VERSION} coverage (${JACOCO_INCLUDES})"
    fetch_jacoco || return 1
    docker exec "${AEM_CONTAINER}" mkdir -p "${JACOCO_CONTAINER_DIR}" || return 1
    docker cp "${JACOCO_LIB_DIR}/org.jacoco.agent-runtime.jar" "${AEM_CONTAINER}:${JACOCO_CONTAINER_DIR}/jacocoagent.jar" || return 1
    docker cp "${JACOCO_LIB_DIR}/org.jacoco.cli-nodeps.jar" "${AEM_CONTAINER}:${JACOCO_CONTAINER_DIR}/jacococli.jar" || return 1
}

# Dump each instance's coverage and report it against this repo's bundle jars.
# The agents listen on the container's loopback only, so the dump runs inside it.
collect_coverage() {
    [[ "${IT_COVERAGE}" == "true" ]] || return 0
    local -a instances=("author:${JACOCO_AUTHOR_PORT}") execs=() report=()
    [[ "${WITH_PUBLISH}" == "true" ]] && instances+=("publish:${JACOCO_PUBLISH_PORT}")
    local entry inst port
    mkdir -p "${IT_COVERAGE_DIR}" || return 1
    for entry in "${instances[@]}"; do
        inst="${entry%%:*}" port="${entry##*:}"
        if docker exec "${AEM_CONTAINER}" bash -lc \
                "java -jar ${JACOCO_CONTAINER_DIR}/jacococli.jar dump --quiet --address 127.0.0.1 --port ${port} --destfile ${JACOCO_CONTAINER_DIR}/${inst}.exec" \
            && docker cp "${AEM_CONTAINER}:${JACOCO_CONTAINER_DIR}/${inst}.exec" "${IT_COVERAGE_DIR}/${inst}.exec"; then
            execs+=("${IT_COVERAGE_DIR}/${inst}.exec")
        else
            echo "WARNING: no JaCoCo coverage dumped from AEM ${inst}" >&2
        fi
    done
    [[ ${#execs[@]} -gt 0 ]] || return 1

    local core_jar amp_jar
    core_jar="$(find_zip "${REPO_ROOT}/bundles/core/target" 'core.wcm.components.core-*.jar' '\-(sources|javadoc|tests)\.jar$')"
    amp_jar="$(find_zip "${REPO_ROOT}/extensions/amp/bundle/target" 'core.wcm.components.extensions.amp-*.jar' '\-(sources|javadoc|tests)\.jar$')"
    if [[ -z "${core_jar}" || -z "${amp_jar}" ]]; then
        echo "Bundle jars not found under bundles/core/target and extensions/amp/bundle/target (build the project first)" >&2
        return 1
    fi
    report=(
        "${execs[@]}"
        --classfiles "${core_jar}" --classfiles "${amp_jar}"
        --sourcefiles "${REPO_ROOT}/bundles/core/src/main/java"
        --sourcefiles "${REPO_ROOT}/extensions/amp/bundle/src/main/java"
        --name "Core Components ITs (AEM ${AEM_TYPE})"
        --xml "${IT_COVERAGE_DIR}/jacoco.xml" --html "${IT_COVERAGE_DIR}/html"
    )
    java -jar "${JACOCO_LIB_DIR}/org.jacoco.cli-nodeps.jar" report "${report[@]}" || return 1
    log "IT coverage report: ${IT_COVERAGE_DIR}/html/index.html"
}

start_aem() {
    log "Starting qp server container from ${AEM_IMAGE}"
    # Publish the instance HTTP ports on loopback only (admin/admin must not be
    # reachable from the network) so the host can reach them once started.
    local ports=(-p "127.0.0.1:${AEM_AUTHOR_PORT}:4502")
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        ports+=(-p "127.0.0.1:${AEM_PUBLISH_PORT}:4503")
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

    # Coverage is best effort: a setup failure must not cost the test run.
    if [[ "${IT_COVERAGE}" == "true" ]] && ! install_jacoco; then
        echo "WARNING: JaCoCo setup failed, running without IT coverage" >&2
        IT_COVERAGE=false
    fi

    start_instance author author 4502 "${QP_VM_OPTIONS}$(context_path_opts)$(jacoco_agent_opts author "${JACOCO_AUTHOR_PORT}")"
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        start_instance publish publish 4503 "${PUBLISH_VM_OPTIONS}$(context_path_opts)$(jacoco_agent_opts publish "${JACOCO_PUBLISH_PORT}")"
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
    local base="$1" settings runmodes product detected
    settings=$(curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        "${base}/system/console/status-slingsettings.txt")
    # Only the "Run Modes = [...]" line counts: bash's =~ lets "." span newlines.
    runmodes=$(grep -m1 '^Run Modes = ' <<<"${settings}" || true)
    if [[ "${runmodes}" =~ [[,\ ]sdk[],] ]]; then
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
    local response
    response=$(curl -sSf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        -F file=@"${zip}" -F name="$(basename "${zip}")" -F force=true -F install=true \
        "${base}/crx/packmgr/service.jsp")
    check_packmgr_response "${response}" "$(basename "${zip}")"
}

# service.jsp answers HTTP 200 even when the upload or install failed; the real
# outcome is the <status code="..."> element of its XML body. Args: <body> <label>
check_packmgr_response() {
    local response="$1" label="$2"
    if [[ "${response}" != *'<status code="200">'* ]]; then
        echo "Package install failed for ${label}:" >&2
        printf '%s\n' "${response}" | tail -n 20 >&2
        return 1
    fi
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
# servlet 500s -> SearchIT shows 0 results). Fix: keep exactly this repo's build
# (matched by the Bundle-Version embedded in the installed `all` package, not by
# "highest version", which a newer product release would win) and UNINSTALL the rest.
# Uninstall - not stop - is required: a *stopped* product bundle is reverted to Active
# by the SDK's Sling installer, whereas an uninstalled one stays gone for the run.
# Then refresh packages so bundles wired to the removed copy (including the survivor,
# which imports its own exports) are rewired and restarted, re-registering its Sling
# models. Args: <base-url> <symbolic-name> <all-package-zip>
dedupe_bundle() {
    local base="$1" bsn="$2" zip="$3"
    local expected ids=""
    expected=$(bundle_version_in_package "${zip}" "${bsn}")
    if [[ -z "${expected}" ]]; then
        echo "Could not find ${bsn} in $(basename "${zip}")" >&2
        return 1
    fi
    # The `all` package installs its bundles ASYNChronously, so this repo's version may
    # not be registered the instant the package upload returns. Poll (up to ~2 min) until
    # it shows up, then uninstall every other copy.
    local found=false
    for _ in $(seq 1 40); do
        if ids=$(curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/bundles.json" 2>/dev/null \
            | python3 -c '
import json, sys
bsn, expected = sys.argv[1], sys.argv[2]
bs = [b for b in json.load(sys.stdin).get("data", []) if b.get("symbolicName") == bsn]
if not any(b.get("version") == expected for b in bs):
    sys.exit(1)
print(" ".join(str(b["id"]) for b in bs if b.get("version") != expected))
' "${bsn}" "${expected}" 2>/dev/null); then
            found=true
            break
        fi
        sleep 3
    done
    if [[ "${found}" != "true" ]]; then
        echo "${bsn} ${expected} was not installed at ${base}" >&2
        return 1
    fi
    if [[ -z "${ids}" ]]; then
        log "Only ${bsn} ${expected} is installed; nothing to remove"
        return 0
    fi
    local id
    for id in ${ids}; do
        log "Uninstalling duplicate product bundle ${bsn} (id ${id}); keeping ${expected}"
        curl -sSf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
            -X POST "${base}/system/console/bundles/${id}" -d action=uninstall >/dev/null
    done
    log "Refreshing package wiring after removing ${bsn} duplicates"
    curl -sSf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        -X POST "${base}/system/console/bundles" -d action=refreshPackages >/dev/null
    wait_for_bundles_settled "${base}"
}

# Print the Bundle-Version of the <symbolic-name> jar embedded in a content package.
# Args: <zip> <symbolic-name>
bundle_version_in_package() {
    local zip="$1" symbolic_name="$2"
    python3 - "${zip}" "${symbolic_name}" <<'PY'
import io, re, sys, zipfile

def manifest(jar):
    text = jar.read("META-INF/MANIFEST.MF").decode("utf-8")
    headers = {}
    for line in re.sub(r"\r?\n ", "", text).splitlines():
        if ":" in line:
            key, value = line.split(":", 1)
            headers[key.strip()] = value.strip()
    return headers

def search(archive, bsn):
    for name in archive.namelist():
        if name.endswith(".jar"):
            with zipfile.ZipFile(io.BytesIO(archive.read(name))) as jar:
                headers = manifest(jar)
                if headers.get("Bundle-SymbolicName", "").split(";")[0] == bsn:
                    return headers.get("Bundle-Version")
        elif name.endswith(".zip"):
            with zipfile.ZipFile(io.BytesIO(archive.read(name))) as nested:
                version = search(nested, bsn)
                if version:
                    return version
    return None

with zipfile.ZipFile(sys.argv[1]) as package:
    print(search(package, sys.argv[2]) or "")
PY
}

# Resolve the newest matching built package zip for a module target dir. Any glob
# in $2 is expanded; $3 (optional) is an extended-regex of basenames to EXCLUDE.
find_zip() {
    local dir="$1" pattern="$2" exclude="${3:-}"
    local matches
    # shellcheck disable=SC2012,SC2086 # ${pattern} is an intentional glob
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
    local base="$1" all_zip
    if [[ "${AEM_TYPE}" == "sdk" ]]; then
        all_zip="$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*-cloud.zip')"
    else
        all_zip="$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*.zip' '\-cloud\.zip$')"
    fi
    install_package "${base}" "${all_zip}"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.apps/target" 'core.wcm.components.it.ui.apps-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.config/target" 'core.wcm.components.it.ui.config-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.content/target" 'core.wcm.components.it.ui.content-*.zip')"
    # The base SDK image already ships Core Components as a product feature, so the `all`
    # package above leaves TWO active versions of the core/AMP bundles. Remove the older
    # (product) copies so only this repo's build remains - otherwise the two versions
    # cross-wire the models packages and components fail with ClassCastExceptions.
    if [[ "${AEM_TYPE}" == "sdk" ]]; then
        dedupe_bundle "${base}" "com.adobe.cq.core.wcm.components.core" "${all_zip}"
        dedupe_bundle "${base}" "com.adobe.cq.core.wcm.components.extensions.amp" "${all_zip}"
    fi
}

product_version() {
    local base="$1"
    curl -fsS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" "${base}/system/console/status-productinfo.txt" \
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
    # may be cut off; then the product version below is the source of truth. A
    # complete response, however, must report success.
    local response
    response=$(curl -sS -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" --max-time 1800 \
        -F file=@"${sp_zip}" -F name="$(basename "${sp_zip}")" -F force=true -F install=true \
        "${AEM_PUBLISH_URL}/crx/packmgr/service.jsp" 2>/dev/null || true)
    rm -rf "$(dirname "${sp_zip}")"
    if [[ "${response}" == *'</crx>'* ]]; then
        check_packmgr_response "${response}" "$(basename "${sp_path}") on publish"
    fi
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

# Package installation starts bundles asynchronously. Do not start tests until
# the core bundles (and author-side test support) are active.
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

run_e2e() {
    # The test module is built standalone, so it would otherwise resolve the
    # parent pom from ~/.m2, which may hold a stale SNAPSHOT or none at all
    # (CI's Maven cache excludes this repo's SNAPSHOTs). Install it from source first.
    log "Installing the parent pom from source"
    mvn -B -q -N -f "${REPO_ROOT}/parent/pom.xml" install

    log "Running Playwright ITs (AEM ${AEM_TYPE}, local Chrome): ${E2E_IT_TEST:-<all>}"
    # The pom's test-all profile pins the author URL to localhost:4502, which the
    # host-local browser can reach directly.
    local -a args=(
        -B -f "${REPO_ROOT}/testing/it/e2e-testing/pom.xml" verify -Ptest-all
        -Dsling.it.instance.url.1="${AEM_BASE_URL}"
        -Dsling.it.instance.adminUser.1="${AEM_ADMIN_USER}"
        -Dsling.it.instance.adminPassword.1="${AEM_ADMIN_PASSWORD}"
        -Dgranite.it.author.url="${AEM_BASE_URL}"
    )
    if [[ -n "${E2E_IT_TEST}" ]]; then
        args+=(-Dit.test="${E2E_IT_TEST}")
    fi
    if [[ -n "${E2E_GROUPS}" ]]; then
        args+=(-Dgroups="${E2E_GROUPS}")
    fi
    args+=(-DexcludedGroups="${E2E_EXCLUDED_GROUPS}")
    if [[ "${E2E_RERUN}" != "0" ]]; then
        args+=(-Dfailsafe.rerunFailingTestsCount="${E2E_RERUN}")
    fi
    # Headless display: the CI workflow (GitHub-hosted ubuntu-26.04, no GUI
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
    provision
    if [[ "${WITH_E2E}" == "true" ]]; then
        run_e2e
    else
        run_tests
    fi
}

if [[ "${BASH_SOURCE[0]}" == "$0" ]]; then
    main "$@"
fi
