# HTL components and AMP overrides

Core Components ship paired artifacts: a Sling Model in `bundles/core/` and an HTL component
in `content/`. The `extensions/amp/` reactor module overlays selected HTL scripts to produce
AMP-compatible markup while reusing the same Sling Models. This page is a navigation entry
to find the right files for a given component.

## Related

- [Sling Model versioning and ImplementationPicker](sling-model-versioning.md)
- [Link resolution pipeline](link-resolution.md)

## Key entry points

- [`content/src/content/jcr_root/apps/core/wcm/components/`](../../../content/src/content/jcr_root/apps/core/wcm/components/) — root for every shipped HTL component, organised as `<component>/<vN>/<component>/`.
- `<component>/<vN>/<component>/<component>.html` — the HTL script that calls `data-sly-use.X="com.adobe.cq.wcm.core.components.models.X"` to bind the public Sling Model.
- `<component>/<vN>/<component>/_cq_dialog/.content.xml` — Granite dialog definition.
- `<component>/<vN>/<component>/_cq_editConfig.xml` — editor in-place behaviour, listeners, drop targets.
- `<component>/<vN>/<component>/_cq_template/.content.xml` — initial child node tree placed when an instance is dropped on a page.
- `<component>/<vN>/<component>/clientlibs/site/` and `clientlibs/editor/` — published / authoring JavaScript and CSS, packaged via `aem-clientlib-generator` (`npm run build` in `content/`).
- [`extensions/amp/`](../../../extensions/amp/) — separate Maven reactor that overlays HTL scripts under `apps/core/wcm/components/<component>/<vN>/<component>` with AMP-specific markup. The Sling Model is **not** duplicated; AMP variants depend on the same `bundles/core/` JAR.
- [`testing/it/it.ui.apps`](../../../testing/it/it.ui.apps/) and [`testing/it/it.ui.content`](../../../testing/it/it.ui.content/) — content packages installed alongside the bundle for HTTP/UI integration tests.

## Gotchas

- HTL files are checked in under `content/src/content/jcr_root/...` rather than `src/main/content/...`. The Maven `content-package-maven-plugin` build picks up that path; do not move scripts.
- The `_cq_dialog`, `_cq_editConfig`, and `_cq_template` directories are **FileVault** representations — editing them by hand is supported, but never delete the leading dot in `.content.xml` and keep `jcr:primaryType` declarations intact.
- AMP variants override the **HTML script only**. If a component renders custom JS via `data-sly-resource` to a separate node, the AMP overlay must include a script for that node too, otherwise the AMP page falls back to the base script and embeds disallowed JS.
- Bumping a component to `vN+1` requires creating a parallel directory tree under `content/.../<component>/vN+1/` (HTL, dialog, editConfig, template, clientlibs) **and** the matching `internal/models/vN+1/` impl in `bundles/core/`. See [Sling Model versioning](sling-model-versioning.md) for what the picker requires.
- Some clientlibs are **embedded** into others (e.g. the data-layer client lib into the page's site clientlib). Embedding is configured in the per-component `clientlib.config.json` consumed by `npm run build`; editing only the source JS without re-running `build` will not update the published clientlib.
- The Karma/Jasmine JS unit-test fixtures live in `content/test/` and run via `npm run unit` from `content/`. They are independent of the JUnit suite under `bundles/core/`.
