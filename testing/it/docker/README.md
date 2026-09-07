# Integration tests against a Dockerized AEM

This runs the [`testing/it/http`](../http) integration tests against an AEM
author instance started from a Docker image, so you can exercise the HTTP ITs
locally the same way CI does. The orchestration lives in [`run-it.sh`](run-it.sh)
and is shared by the [`Integration Tests (AEM)`](../../../.github/workflows/maven-it.yml)
GitHub Actions workflow.

## Phase 1 scope

- **Author only, cloud-ready AEM image.** Runs against the `circleci-aem-cloudready`
  image with the cloud (`-cloud`) core-components package. Only the IT classes
  that do not use a publish instance are run by default:
  `AdaptiveImageServletIT`, `ComponentsIT`, `ExperienceFragmentIT`,
  `TableOfContentsFilterIT`. The remaining classes replicate to and read from a
  publish instance and are enabled in a later phase (see "Roadmap").

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
```

The script starts the AEM container, waits for it to answer HTTP, installs the
cloud (`-cloud`) `all` package + `it.ui.apps` + `it.ui.config` + `it.ui.content`
via the CRX Package Manager, then runs the failsafe suite against
`http://localhost:4502`. On exit it dumps the container logs and removes the
container.

### Configuration

All knobs are environment variables (see the top of `run-it.sh`). The common ones:

| Variable | Default | Purpose |
|---|---|---|
| `AEM_IMAGE` | `…/circleci-aem-cloudready:26125-openjdk21` | AEM Docker image to run |
| `AEM_AUTHOR_PORT` | `4502` | Host port the author is published on |
| `IT_TEST` | the four author-only classes | Comma-separated failsafe test selection |
| `AEM_STARTUP_TIMEOUT` | `600` | Seconds to wait for AEM to answer HTTP |
| `KEEP_AEM` | `false` | Leave the container running after the run (for debugging) |

Example — run a single test class and keep AEM up afterwards:

```bash
IT_TEST=ComponentsIT KEEP_AEM=true bash testing/it/docker/run-it.sh
```

## How the image works (verified)

`circleci-aem-cloudready:26125-openjdk21` is a QuickProvider (qp) *server* image
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

## Roadmap

1. **(this phase)** Author-only http ITs, cloud-ready image.
2. Add a publish instance (`:4503`) and run the full `-Ptest-all` http suite.
3. Matrix across AEM flavors (LTS, cloud-ready), mirroring the CIF `test-aem` job.
4. Add the Selenium/e2e suite (`testing/it/e2e-selenium`).
