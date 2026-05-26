# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Adobe AEM WCM Core Components — a Maven reactor of OSGi bundles, content packages, and integration tests that ship ~29 production-ready HTL components for AEM (AEMaaCS, 6.5 LTS, 6.5). All component models are versioned (`v1`, `v2`, …) and coexist side by side.

- Java source target: `aem.java.version=8` (parent `pom.xml`). CI matrix builds on JDK 11, 17, 21.
- Artifact version lives in `parent/pom.xml` (`<version>`). `npm run sync-pom-version` keeps `content/package.json` in sync.
- `main` is the trunk development branch; all PRs target `main`.

## Reactor layout (root `pom.xml` modules)

- `parent/` — dependency management and plugin config shared by every module.
- `bundles/core/` — **the** OSGi bundle. Public API in `com.adobe.cq.wcm.core.components.{models,services,commons,util}`; Sling Model implementations and internal services in `com.adobe.cq.wcm.core.components.internal.{models/v1,v2,v3,v4,…,servlets,link,form,resource,helper,jackson,services}`. Version suffixes in package paths mirror the HTL component versions in `content/`.
- `content/` — the `ui.apps`-style content package with `apps/core/wcm/components/<component>/<vN>/…`. Also an npm project that builds component clientlibs and runs Karma/Jasmine JS unit tests.
- `config/` — runmode OSGi configurations.
- `all/` — aggregator FileVault package installed to AEM.
- `examples/` — sample pages / Component Library content.
- `extensions/amp/` — AMP variant of the components (separate module tree).
- `testing/junit/core/` — JUnit helpers for HTL/Sling-Model component tests.
- `testing/aem-mock-plugin/` — wcm.io `aem-mock` plugin for unit-test setup.
- `testing/it/{http,it.core,e2e-selenium,e2e-selenium-utils,it.ui.apps,it.ui.config,it.ui.content}` — HTTP integration tests, Selenide/Selenium E2E UI tests, and the test content packages they install.

## Build & test

All Maven commands run from repo root unless noted.

```
mvn clean install                              # full build + unit tests
mvn clean install -pl bundles/core -am         # build one module and its deps
mvn clean install -pl bundles/core -am -Dtest=ContentFragmentImplTest    # single JUnit test
mvn clean install -PautoInstallPackage         # build + deploy to local AEM (default aem.host=localhost, aem.port=4502)
mvn clean install -PautoInstallPackage,cloud   # AEMaaCS SDK: deploy into /libs instead of /apps
mvn clean install -PautoInstallSinglePackage   # deploy `all/` aggregate to author
mvn clean install -PautoInstallPackagePublish  # deploy to publish (aem.publish.host/port)
```

Override target instance with `-Daem.host=… -Daem.port=… -Dsling.user=… -Dsling.password=…`.

Front-end (`content/` directory) — run from `content/`:

```
npm run lint        # eslint + stylelint in parallel
npm run eslint[:fix]
npm run stylelint[:fix]
npm run unit        # Karma/Jasmine JS unit tests (fixtures in content/test/)
npm run build       # aem-clientlib-generator — rebuild clientlibs (e.g. Data Layer embedding)
npm run sync-pom-version
```

Integration/E2E tests live under `testing/it/*` and require a running AEM. They are built by the reactor but not typically run locally unless you have an instance wired up — see each module's own docs.

## Architectural notes

- **Sling Model = public interface in `models/` + `@Model` impl in `internal/models/vN/`.** Public interfaces in `com.adobe.cq.wcm.core.components.models` (e.g. `Image`, `Teaser`, `ContentFragment`) are the contract; `internal/models/vN/*Impl.java` is the versioned implementation bound via `resourceType`. To add a new version of a component, create `internal/models/vN+1/FooImpl.java` and the matching HTL under `content/.../foo/vN+1/`. Never break the public interface in `models/` without a new version.
- **Component policy via Sling CA-config, not hardcoded constants.** Policies, config factories, and runmode OSGi configs are in `config/` and consumed through `@ScriptVariable` / context-aware configuration APIs — see `CONFIGS.md`.
- **Data Layer.** Components populate the Adobe Client Data Layer via `models/datalayer/*`. See `DATA_LAYER_INTEGRATION.md`. `internal/DataLayerConfig.java` is the kill-switch.
- **Link handling.** Use `internal/link/LinkManager` / `LinkBuilder` for all URL rendering — don't build links by hand in models or HTL.
- **HTL only**, no JSP. Scripts live in `content/src/content/jcr_root/apps/core/wcm/components/<comp>/vN/<comp>/<comp>.html`. Dialogs under `_cq_dialog/`, editor config under `_cq_editConfig.xml`.
- **XSS/encoding** is expected to be handled by HTL; never concatenate untrusted strings into markup. When touching JS/JSP/HTL, prefer `xssAPI`, `Granite.UI.Foundation.XSS`, or `CQ.shared.XSS` — see `fix-xss-vulnerability` skill for the canonical patterns.
- **AMP** variants in `extensions/amp/` override selected HTL scripts; keep the Sling-Model layer shared.

## Component versioning (critical)

- Bumping a component to `vN+1` is an API event. Create new `models/vN/Foo.java` interface (or reuse if compatible), new `internal/models/vN/FooImpl.java`, new `content/.../foo/vN/foo`. Keep previous versions intact — users depend on them.
- Version tables in `VERSIONS.md` must stay accurate.

## Commit & PR conventions

- PRs target `main`. Branch names: `issue/<number>`, `feature/<name>`, `release/<version>`.
- PR title prefix with bracketed component when relevant (`[Image] …`). Many commits use `[SITES-xxxxx] …` or `[CQ-xxxxx] …` for Jira tickets.
- PR description must follow `.github/pull_request_template.md` — the `Fixed Issues?`, `Patch/Minor/Major`, `Tests Added + Pass?` table is mandatory.
- Every change is expected to ship with tests (JUnit for models, Karma for component JS, E2E under `testing/it/e2e-selenium` for UI flows).

## CI

GitHub Actions (`.github/workflows/maven-test.yml`) runs `mvn -B -U clean install -Pcloud,adobe-public` on JDK 11/17/21 plus a Node 14 JS job in `content/`. CodeQL runs on push/PR to `main`. Don't land a change that fails `-Pcloud,adobe-public`.

## Docs already in the repo (read before guessing)

- `README.md` — component list, system requirements (AEM/Java/Maven), build profiles.
- `BUILDING.md` — full list of install profiles and npm scripts.
- `CONFIGS.md` — OSGi + Sling CA-config inventory.
- `DATA_LAYER_INTEGRATION.md` — Data Layer contract.
- `VERSIONS.md` — historical system-requirements matrix.
- `Guidelines.md` — production-ready component checklist (security/perf/a11y/i18n).
- `CONTRIBUTING.md` — issue + PR workflow.

## Wiki

Detailed knowledge about individual subsystems lives in [`docs/llm-wiki/index.md`](docs/llm-wiki/index.md).
Concept pages cover entry points, invariants, and source pointers per subsystem. Read
the wiki page for a subsystem before grepping its source files.
