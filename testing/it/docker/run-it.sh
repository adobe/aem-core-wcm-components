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
# Runs the testing/it/http integration tests against a Dockerized AEM author
# instance. Shared by both the local developer flow (testing/it/docker/README.md)
# and the CI job (.github/workflows/maven-it.yml) so the two never drift.
#
# Flow:
#   1. Start the AEM author container from a (private) Adobe AEM image.
#   2. Wait until AEM answers HTTP on the login page.
#   3. Install the built core-components + IT content packages via the CRX
#      Package Manager HTTP service.
#   4. Run the failsafe suite (testing/it/http) against that author instance.
#
# PHASE 1 SCOPE: author-only. Only the IT classes that do not use a publish
# instance are selected by default (see IT_TEST below). Bringing up a publish
# instance and running the full -Ptest-all suite is a later phase.
#
# All knobs are environment variables with sane local defaults; CI overrides
# them (image coordinates, KEEP_AEM, etc.). Nothing here is specific to a CI
# runner, so the same invocation works on a developer laptop.
#
# How the image works (verified against circleci-aem-cloudready:27830-v2-openjdk21,
# same shape as the classic circleci-aem image): it is a
# QuickProvider (qp) *server* image - its entrypoint runs
# `qp.sh start_local_server` (RMI on :55555) and then stays alive, but does NOT
# start an AEM author by itself. The author is started by a qp *client* command
# (`qp.sh start --id author --port 4502 --qs-jar .../author/cq-quickstart.jar`).
# Because the image also ships qp.sh, the quickstart jar and Maven, we run that
# client command inside the same container (via docker exec): RMI's "localhost"
# callback is then internally consistent, so we do NOT need `--network host`
# (unlike the CIF repo's two-container split). We only publish :4502 so the
# host-side Maven/curl can reach AEM.

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration (override via environment)
# ---------------------------------------------------------------------------
REGISTRY="${REGISTRY:-docker-adobe-cif-release.dr-uw2.adobeitc.com}"
AEM_IMAGE="${AEM_IMAGE:-${REGISTRY}/circleci-aem-cloudready:27830-v2-openjdk21}"
AEM_AUTHOR_PORT="${AEM_AUTHOR_PORT:-4502}"
AEM_ADMIN_USER="${AEM_ADMIN_USER:-admin}"
AEM_ADMIN_PASSWORD="${AEM_ADMIN_PASSWORD:-admin}"

# Path (inside the image) to the author quickstart jar the qp client starts from,
# and the qp working directory that holds qp.sh + .qpenv.
QP_DIR="${QP_DIR:-/home/circleci/cq}"
QS_JAR="${QS_JAR:-/home/circleci/cq/author/cq-quickstart.jar}"
# Seconds qp waits for the quickstart to finish starting.
QP_START_TIMEOUT="${QP_START_TIMEOUT:-1800}"

# Author-only IT classes (the ones that never touch a publish instance). Keep in
# sync with the classification in testing/it/docker/README.md.
IT_TEST="${IT_TEST:-AdaptiveImageServletIT,ComponentsIT,ExperienceFragmentIT,TableOfContentsFilterIT}"

# Max seconds to wait for AEM to answer HTTP before giving up.
AEM_STARTUP_TIMEOUT="${AEM_STARTUP_TIMEOUT:-600}"

# Leave the AEM container running after the script exits (for debugging). CI sets
# this to keep the instance around for a tmate/SSH session.
KEEP_AEM="${KEEP_AEM:-false}"

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
AEM_BASE_URL="http://localhost:${AEM_AUTHOR_PORT}"
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

    # Best-effort: copy AEM's own logs out of the (still-running) container so
    # the 500s etc. are diagnosable from the uploaded artifacts. The instance
    # dir varies (qp provisions per --id), so locate error.log dynamically.
    local logs_dir="${REPO_ROOT}/testing/it/http/target/aem-logs"
    local err_path
    err_path=$(docker exec "${AEM_CONTAINER}" bash -lc \
        'find /home/circleci -maxdepth 6 -path "*crx-quickstart/logs/error.log" 2>/dev/null | head -1' \
        2>/dev/null | tr -d '\r')
    if [[ -n "${err_path}" ]]; then
        mkdir -p "${logs_dir}"
        docker cp "${AEM_CONTAINER}:${err_path}" "${logs_dir}/error.log" 2>/dev/null || true
        docker cp "${AEM_CONTAINER}:$(dirname "${err_path}")/stdout.log" "${logs_dir}/stdout.log" 2>/dev/null || true
        echo "Copied AEM logs to ${logs_dir}"
    fi

    if [[ "${KEEP_AEM}" == "true" ]]; then
        log "KEEP_AEM=true - leaving ${AEM_CONTAINER} running (docker rm -f it manually when done)."
    else
        docker rm -f "${AEM_CONTAINER}" >/dev/null 2>&1 || true
    fi
    exit "${exit_code}"
}
trap cleanup EXIT

start_aem() {
    log "Starting qp server container from ${AEM_IMAGE}"
    # The container's entrypoint brings up the qp RMI server and stays alive; we
    # publish 4502 so the host can reach the author once the qp client starts it.
    docker run -d --name "${AEM_CONTAINER}" -p "${AEM_AUTHOR_PORT}:4502" "${AEM_IMAGE}"

    log "Waiting for the qp server to accept client commands"
    local deadline=$(( SECONDS + 120 ))
    until docker exec "${AEM_CONTAINER}" bash -lc "cd ${QP_DIR} && ./qp.sh get_quickstarts" >/dev/null 2>&1; do
        if (( SECONDS >= deadline )); then
            echo "qp server did not become ready within 120s" >&2
            return 1
        fi
        sleep 3
    done

    log "Starting AEM author (qp start --id author --port 4502)"
    # Same invocation shape the CIF repo uses. qp `start` provisions from the
    # local quickstart jar then starts it, returning once the instance is up.
    docker exec "${AEM_CONTAINER}" bash -lc \
        "cd ${QP_DIR} && ./qp.sh -v start --id author --runmode author --port 4502 --qs-jar ${QS_JAR} --timeout ${QP_START_TIMEOUT}"
}

wait_for_aem() {
    log "Waiting for AEM at ${AEM_BASE_URL} (timeout ${AEM_STARTUP_TIMEOUT}s)"
    local deadline=$(( SECONDS + AEM_STARTUP_TIMEOUT ))
    until [[ "$(curl -sf -o /dev/null -w '%{http_code}' \
        -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        "${AEM_BASE_URL}/libs/granite/core/content/login.html" 2>/dev/null)" == "200" ]]; do
        if (( SECONDS >= deadline )); then
            echo "AEM did not become available within ${AEM_STARTUP_TIMEOUT}s" >&2
            return 1
        fi
        sleep 5
    done
    log "AEM is up."
}

# Upload + install one content package via the CRX Package Manager service.
install_package() {
    local zip="$1"
    if [[ ! -f "${zip}" ]]; then
        echo "Package not found: ${zip} (did you build the project first? see README)" >&2
        return 1
    fi
    log "Installing $(basename "${zip}")"
    curl -sf -u "${AEM_ADMIN_USER}:${AEM_ADMIN_PASSWORD}" \
        -F file=@"${zip}" -F name="$(basename "${zip}")" -F force=true -F install=true \
        "${AEM_BASE_URL}/crx/packmgr/service.jsp" >/dev/null
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

provision() {
    log "Provisioning core-components + IT content packages"
    # Order matters: components first, then immutable test apps, OSGi config, then
    # mutable test content.
    #
    # The `all` module builds both a classic zip (no classifier) and a `-cloud`
    # classified zip. We target a cloud-ready instance, so install the `-cloud`
    # variant.
    install_package "$(find_zip "${REPO_ROOT}/all/target" 'core.wcm.components.all-*-cloud.zip')"
    install_package "$(find_zip "${REPO_ROOT}/testing/it/it.ui.apps/target" 'core.wcm.components.it.ui.apps-*.zip')"
    install_package "$(find_zip "${REPO_ROOT}/testing/it/it.ui.config/target" 'core.wcm.components.it.ui.config-*.zip')"
    install_package "$(find_zip "${REPO_ROOT}/testing/it/it.ui.content/target" 'core.wcm.components.it.ui.content-*.zip')"
}

run_tests() {
    log "Running http ITs (author-only): ${IT_TEST}"
    # -Dit activates the test-all profile (failsafe). The Sling testing clients
    # are pointed at a single author instance via the documented sling.it.*
    # system properties, which is what makes the author-only selection work.
    mvn -B -f "${REPO_ROOT}/testing/it/http/pom.xml" verify \
        -Dit \
        -Dit.test="${IT_TEST}" \
        -Dsling.it.instances=1 \
        -Dsling.it.instance.url.1="${AEM_BASE_URL}" \
        -Dsling.it.instance.runmode.1=author \
        -Dsling.it.instance.adminUser.1="${AEM_ADMIN_USER}" \
        -Dsling.it.instance.adminPassword.1="${AEM_ADMIN_PASSWORD}" \
        -Dgranite.it.author.url="${AEM_BASE_URL}"
}

main() {
    command -v docker >/dev/null || { echo "docker is required" >&2; exit 1; }
    start_aem
    wait_for_aem
    provision
    run_tests
}

main "$@"
