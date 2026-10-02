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

For the default cloud(-ready) image, classes annotated
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
product-specific `all` package + `it.ui.apps` + `it.ui.config` + `it.ui.content`
via the CRX Package Manager, then runs the failsafe suite against
`http://localhost:4502`. On exit it dumps the container logs and removes the
container.

### Configuration

All knobs are environment variables (see the top of `run-it.sh`). The common ones:

| Variable | Default | Purpose |
|---|---|---|
| `AEM_IMAGE` | `…/circleci-aem-cloudready:27830-v2-openjdk21` | AEM Docker image to run |
| `AEM_TYPE` | Inferred from image repository | `sdk`, `65`, or `lts`; verified against runtime run modes/product info before provisioning |
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

The image is `linux/amd64`. On an arm64 (Apple Silicon) Mac it runs under Docker
emulation, so AEM boot is **slower than on native amd64** (author/publish took
~3.5 min each to come up in testing, vs. seconds/low-minutes natively) and can be
flaky — this only affects local dev on Apple Silicon. CI runs on GitHub-hosted
`ubuntu-latest`, which is native amd64, so it doesn't hit this. On a native amd64
host (Linux or Mac) it runs natively.

## Open items

- **CI secrets.** `ARTIFACTORY_CLOUD_USER` / `ARTIFACTORY_CLOUD_PASS` must be
  added as repository secrets for the workflow to pull the image.

## Browser UI suite (`WITH_SELENIUM=true`)

Runs the `testing/it/e2e-selenium` suite (Playwright tests; the module keeps its
historical name) with a **local** Chrome on the host, which reaches
AEM at the published `localhost:4502` — no browser-container networking. The
module's default Chrome-in-Docker (Selenoid) mode is not used because the
e2e-selenium pom pins the author URL to `localhost:4502`, which a browser in a
separate container cannot reach.

```bash
# Full suite (author instance)
WITH_SELENIUM=true bash testing/it/docker/run-it.sh

# A single class / method
WITH_SELENIUM=true SEL_IT_TEST='com.adobe.cq.wcm.core.components.it.pw.list.ListV2PwIT' \
  bash testing/it/docker/run-it.sh
```

- **Local:** needs Chrome installed (native, not emulated — so fast). Runs
  headed unless a virtual display is used.
- **CI:** the workflow's browser-test matrix runs Chrome headless under
  **Xvfb + fluxbox** on the runner, one job per `@Tag("playwright-groupN")`
  **per AEM product** (12 browser jobs). Each job uses its own AEM instance.
  A shared `prep` job builds both classic and cloud packages, and three
  image-cache jobs prime each image **once**, so test legs do not repeat the
  expensive build or image pull. The group matrix is generated by
  [`gen-browser-matrix.sh`](gen-browser-matrix.sh) from the group tags found in
  the sources.
- **Every IT class must have a `playwright-groupN` tag** (add new ones to the
  fastest group, currently `playwright-group2`). Untagged ITs fail the build
  (`ItGroupTagTest`, surefire) and the CI `prep` job (`gen-browser-matrix.sh`).
- Knobs: `SEL_BROWSER` (default `chrome`), `SEL_IT_TEST` (class selection),
  `SEL_GROUPS` (JUnit tag include, e.g. `playwright-group1`), `SEL_EXCLUDED_GROUPS` (default
  `failing,nested,IgnoreOnSDK` — the last mirrors the pipeline's cloud/SDK skip).

To run a single group locally against a provisioned AEM instance:

```bash
WITH_SELENIUM=true SEL_GROUPS=playwright-group1 SEL_RERUN=0 bash testing/it/docker/run-it.sh
```

## Roadmap

1. ✅ Author-only http ITs, cloud-ready image.
2. ✅ Publish instance (`:4503`) + full `*IT.java` http suite (`WITH_PUBLISH=true`).
3. ✅ Browser e2e suite (Playwright) (`testing/it/e2e-selenium`, `WITH_SELENIUM=true`).
4. ✅ Matrix across AEM SDK, classic 6.5, and LTS.

## Cross-version validation

CI runs all four Playwright groups and the HTTP author/publish suite against each image,
with product-specific report/check names and artifacts:

| Product | Image (under `docker-adobe-cif-release.dr-uw2.adobeitc.com/`) | Package | Exclusions beyond `failing,nested` for browsers |
| --- | --- | --- | --- |
| SDK | `circleci-aem-cloudready:27830-v2-openjdk21` | `all-*-cloud.zip` | Browser: `IgnoreOnSDK`; HTTP: `IgnoreOnCloud` |
| AEM 6.5 | `circleci-aem:6.5.24.0-openjdk11` | Classic `all-*.zip` | Browser and HTTP: `IgnoreOn65` |
| AEM LTS | `circleci-aem-lts:6.6.2-openjdk21` | Classic `all-*.zip` | No product exclusions |

LTS is validated separately from 6.5: `IgnoreOn65` does not suppress LTS tests.

The 6.5 image ships a service-packed author, but its publish starts from the GA
quickstart jar. With `WITH_PUBLISH=true`, `run-it.sh` installs the author's
`aem-service-pkg` on publish whenever their product versions differ.
The AEM JVM comes from the image; the host build/test JVM remains JDK 11.
Cloud-only bundle deduplication is not applied to on-prem instances.
Explicit empty `IT_EXCLUDED_GROUPS=""` or `SEL_EXCLUDED_GROUPS=""` overrides clear exclusions.

Local examples (build packages first as shown above):

```bash
# AEM 6.5 HTTP author/publish
AEM_IMAGE=docker-adobe-cif-release.dr-uw2.adobeitc.com/circleci-aem:6.5.24.0-openjdk11 \
  WITH_PUBLISH=true bash testing/it/docker/run-it.sh

# LTS Playwright group2
AEM_IMAGE=docker-adobe-cif-release.dr-uw2.adobeitc.com/circleci-aem-lts:6.6.2-openjdk21 \
  WITH_SELENIUM=true SEL_GROUPS=playwright-group2 SEL_RERUN=0 \
  bash testing/it/docker/run-it.sh

# Product selection, package/exclusion routing, URL and mismatch guard checks (no AEM needed)
bash testing/it/docker/test-run-it.sh
```
