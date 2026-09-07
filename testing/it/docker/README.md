# Integration tests against a Dockerized AEM

This runs the [`testing/it/http`](../http) integration tests against an AEM
author instance started from a Docker image, so you can exercise the HTTP ITs
locally the same way CI does. The orchestration lives in [`run-it.sh`](run-it.sh)
and is shared by the [`Integration Tests (AEM)`](../../../.github/workflows/maven-it.yml)
GitHub Actions workflow.

## Scope

Runs against the `circleci-aem-cloudready` image with the cloud (`-cloud`)
core-components package. Two modes:

- **Author only (default).** Runs just the IT classes that do not use a publish
  instance: `AdaptiveImageServletIT`, `ComponentsIT`, `ExperienceFragmentIT`.
- **Author + publish (`WITH_PUBLISH=true`).** Also starts and provisions a
  publish instance (port 4503) and runs the full `*IT.java` suite. The
  publish-touching tests just `GET` pre-deployed content from publish, so the
  same content packages are installed there (no replication configured).

Because the target is a cloud(-ready) image, classes annotated
`@Category(IgnoreOnCloud)` are excluded (via `excludedGroups`), mirroring the
core-components pipeline's cloud run — currently `SeoIT`,
`TableOfContentsFilterIT`, and `ClientlibsIncludeIT`. Override with
`IT_EXCLUDED_GROUPS=""` to force them.

## Prerequisites

- Docker running locally.
- JDK 11 and Maven.
- Access to the private Adobe AEM image registry. Log in once:
  ```
  docker login docker-adobe-cif-release.dr-uw2.adobeitc.com
  ```

## Usage

From the repository root, build the deployable packages first, then run the ITs:

```bash
# 1. Build the core-components + IT content packages the script installs into AEM
mvn -B clean install -Pcloud,adobe-public -DskipTests

# 2. Start AEM in Docker, provision it, and run the author-only http ITs
bash testing/it/docker/run-it.sh

# ...or run the full suite against author + publish
WITH_PUBLISH=true bash testing/it/docker/run-it.sh
```

> **Memory:** two AEM instances need headroom. On Docker Desktop, raise
> Settings > Resources > Memory to ~16 GB before running with `WITH_PUBLISH=true`
> (the default 7-8 GB is enough for author-only but not for author + publish).

The script starts the AEM container, waits for it to answer HTTP, installs the
cloud (`-cloud`) `all` package + `it.ui.apps` + `it.ui.config` + `it.ui.content`
via the CRX Package Manager, then runs the failsafe suite against
`http://localhost:4502`. On exit it dumps the container logs and removes the
container.

### Configuration

All knobs are environment variables (see the top of `run-it.sh`). The common ones:

| Variable | Default | Purpose |
|---|---|---|
| `AEM_IMAGE` | `…/circleci-aem-cloudready:27830-v2-openjdk21` | AEM Docker image to run |
| `AEM_AUTHOR_PORT` | `4502` | Host port the author is published on |
| `WITH_PUBLISH` | `false` | Also start/provision publish and run the full suite |
| `AEM_PUBLISH_PORT` | `4503` | Host port the publish is published on |
| `IT_TEST` | author-only classes (empty=all when publish on) | Comma-separated failsafe test selection |
| `IT_EXCLUDED_GROUPS` | `…it.http.IgnoreOnCloud` | JUnit categories excluded (cloud target); set empty to force them |
| `AEM_STARTUP_TIMEOUT` | `600` | Seconds to wait for each instance to answer HTTP |
| `QP_VM_OPTIONS` | `-Xmx4g -XX:MaxMetaspaceSize=1g …` | JVM options for the author quickstart (qp's 256m metaspace default OOMs cloud AEM) |
| `PUBLISH_VM_OPTIONS` | same as `QP_VM_OPTIONS` | JVM options for the publish quickstart |
| `KEEP_AEM` | `false` | Leave the container running after the run (for debugging) |

Example — run a single test class and keep AEM up afterwards:

```bash
IT_TEST=ComponentsIT KEEP_AEM=true bash testing/it/docker/run-it.sh
```

## How the image works (verified)

`circleci-aem-cloudready:27830-v2-openjdk21` is a QuickProvider (qp) *server* image
(same shape as the classic `circleci-aem` image): its entrypoint starts the qp
RMI server (`:55555`) and stays alive, but does **not** boot an AEM author by
itself. `run-it.sh` starts the author with a qp *client*
command executed **inside the same container** (`qp.sh start --id author
--port 4502 --qs-jar /home/circleci/cq/author/cq-quickstart.jar`). Running the
client in the same container keeps RMI's `localhost` callback consistent, so —
unlike the CIF repo's two-container split — **no `--network host` is needed**;
we only publish `:4502` for the host-side Maven/curl.

## Platform note (Apple Silicon)

The image is `linux/amd64`. On an arm64 (Apple Silicon) laptop it runs under
emulation, so the local AEM boot is **slow and can be flaky**. The authoritative
run is the CI job on native amd64 runners; treat the local flow on arm64 as
best-effort. On an amd64 Linux host it runs natively.

## Open items

- **CI secrets.** `ARTIFACTORY_CLOUD_USER` / `ARTIFACTORY_CLOUD_PASS` must be
  added as repository secrets for the workflow to pull the image.

## Selenium UI suite (`WITH_SELENIUM=true`)

Runs the `testing/it/e2e-selenium` suite with a **local** browser on the host
(`-Dsel.jup.default.browser=chrome`), which reaches AEM at the published
`localhost:4502` — no browser-container networking. The module's default
Chrome-in-Docker (Selenoid) mode is not used because the e2e-selenium pom pins
the author URL to `localhost:4502`, which a browser in a separate container
cannot reach.

```bash
# Full suite (author instance)
WITH_SELENIUM=true bash testing/it/docker/run-it.sh

# A single class / method
WITH_SELENIUM=true SEL_IT_TEST='com.adobe.cq.wcm.core.components.it.seljup.tests.list.v2.ListIT' \
  bash testing/it/docker/run-it.sh
```

- **Local:** needs Chrome installed (native, not emulated — so fast). Runs
  headed unless a virtual display is used.
- **CI:** the workflow's `selenium` job (manual `workflow_dispatch`) runs Chrome
  headless under **Xvfb + fluxbox** on the runner. It is **sharded into a parallel
  matrix**: each `@Tag` group (`group1`..`group4`) is split into 4 class buckets,
  plus an `ungrouped` leg — 17 legs total, generated by
  [`gen-selenium-matrix.sh`](gen-selenium-matrix.sh). A shared `prep` job builds
  the packages and primes the AEM image cache **once**; every test leg (http and
  selenium) just downloads the packages and `docker load`s the cached image, so
  the build/pull is not repeated per leg. Not on every push (slower than http ITs).
- Knobs: `SEL_BROWSER` (default `chrome`), `SEL_IT_TEST` (class selection),
  `SEL_GROUPS` (JUnit tag include, e.g. `group1`), `SEL_EXCLUDED_GROUPS` (default
  `failing,nested,IgnoreOnSDK` — the last mirrors the pipeline's cloud/SDK skip).

## Roadmap

1. ✅ Author-only http ITs, cloud-ready image.
2. ✅ Publish instance (`:4503`) + full `*IT.java` http suite (`WITH_PUBLISH=true`).
3. ✅ Selenium/e2e suite (`testing/it/e2e-selenium`, `WITH_SELENIUM=true`).
4. Matrix across AEM flavors (classic 6.5, LTS), mirroring the CIF `test-aem` job.
