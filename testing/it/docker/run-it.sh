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

# JUnit categories to exclude. We target a cloud(-ready) instance, so mirror the
# core-components pipeline and skip @Category(IgnoreOnCloud) classes (SeoIT,
# TableOfContentsFilterIT, ClientlibsIncludeIT) - features that are not exercised
# on AEM as a Cloud Service. Override with IT_EXCLUDED_GROUPS="" to force them.
IT_EXCLUDED_GROUPS="${IT_EXCLUDED_GROUPS:-com.adobe.cq.wcm.core.components.it.http.IgnoreOnCloud}"

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
SEL_EXCLUDED_GROUPS="${SEL_EXCLUDED_GROUPS:-failing,nested,IgnoreOnSDK}"
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
    SEL_IT_TEST="com.adobe.cq.wcm.core.components.it.seljup.tests.list.v2.ListIT"
fi
SEL_IT_TEST="${SEL_IT_TEST:-}"

# Max seconds to wait for an instance to answer HTTP before giving up.
AEM_STARTUP_TIMEOUT="${AEM_STARTUP_TIMEOUT:-600}"

# Leave the container running after the script exits (for debugging).
KEEP_AEM="${KEEP_AEM:-false}"

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
trap cleanup EXIT

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
# `-cloud` classified zip; we target a cloud-ready instance, so install `-cloud`.
provision_packages() {
    local base="$1"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*-cloud.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.apps/target" 'core.wcm.components.it.ui.apps-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.config/target" 'core.wcm.components.it.ui.config-*.zip')"
    install_package "${base}" "$(find_zip "${REPO_ROOT}/testing/it/it.ui.content/target" 'core.wcm.components.it.ui.content-*.zip')"
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
        log "Provisioning publish"
        # Publish tests GET pre-deployed content, so the publish instance gets the same
        # content packages (no replication needed).
        provision_packages "${AEM_PUBLISH_URL}"
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
    if [[ -n "${IT_EXCLUDED_GROUPS}" ]]; then
        args+=(-DexcludedGroups="${IT_EXCLUDED_GROUPS}")
    fi
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
    log "Running Selenium ITs (local ${SEL_BROWSER}): ${SEL_IT_TEST:-<all>}"
    # -Dsel.jup.default.browser selects a LOCAL browser (vs the module's default
    # Chrome-in-Docker). The pom's test-all profile pins the author URL to
    # localhost:4502, which the host-local browser can reach directly.
    local -a args=(
        -B -f "${REPO_ROOT}/testing/it/e2e-selenium/pom.xml" verify -Ptest-all
        -Dsel.jup.default.browser="${SEL_BROWSER}"
    )
    if [[ -n "${SEL_IT_TEST}" ]]; then
        args+=(-Dit.test="${SEL_IT_TEST}")
    fi
    if [[ -n "${SEL_GROUPS}" ]]; then
        args+=(-Dgroups="${SEL_GROUPS}")
    fi
    if [[ -n "${SEL_EXCLUDED_GROUPS}" ]]; then
        args+=(-DexcludedGroups="${SEL_EXCLUDED_GROUPS}")
    fi
    if [[ "${SEL_RERUN}" != "0" ]]; then
        args+=(-Dfailsafe.rerunFailingTestsCount="${SEL_RERUN}")
    fi
    # Headless display: on a Linux runner with no DISPLAY, run under Xvfb so the
    # host-local Chrome has a virtual screen. On macOS (no xvfb-run) this branch is
    # skipped - Chrome runs natively against the runner's real GUI session (the CI
    # self-hosted runner and a local Mac both work this way).
    if [[ -z "${DISPLAY:-}" ]] && command -v xvfb-run >/dev/null 2>&1; then
        xvfb-run -a mvn "${args[@]}"
    else
        mvn "${args[@]}"
    fi
}

main() {
    command -v docker >/dev/null || { echo "docker is required" >&2; exit 1; }
    start_aem
    wait_for_aem "${AEM_BASE_URL}" author
    if [[ "${WITH_PUBLISH}" == "true" ]]; then
        wait_for_aem "${AEM_PUBLISH_URL}" publish
    fi
    provision
    if [[ "${WITH_SELENIUM}" == "true" ]]; then
        run_selenium
    else
        run_tests
    fi
}

main "$@"
