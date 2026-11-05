# Data Layer integration

**Purpose** — explain how Core Components produce the JSON object consumed by the Adobe Client Data Layer (`adobeDataLayer`), the kill-switch contract, and the per-component contribution model.

## Invariants

- The Data Layer is **off by default**. It is enabled per page via Sling context-aware configuration `DataLayerConfig.enabled=true`. The default in [`DataLayerConfig`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/DataLayerConfig.java) is `false` — components must check this before emitting any `data-cmp-data-layer` payload.
- The data-layer global object name defaults to `adobeDataLayer` (constant `DATALAYER_OBJECT_NAME_ADOBE` in [`DataLayerConfig`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/DataLayerConfig.java)). Consumers can override via `name`, but components must not hard-code `window.adobeDataLayer` — they read the configured name through the Page model.
- Each component contributes a single root entry whose key is the **component ID** and whose value implements one of the `*Data` interfaces: [`ComponentData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/ComponentData.java), [`ContainerData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/ContainerData.java), [`ImageData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/ImageData.java), [`PageData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/PageData.java), [`ContentFragmentData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/ContentFragmentData.java), [`AssetData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/AssetData.java), [`EmbeddableData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/EmbeddableData.java). New component types must add a new interface, not overload an existing one.
- The data-layer JSON for a component is serialised via the `models/datalayer/jackson` mixins. Adding a getter to a `*Data` interface that is not annotated `@JsonInclude(NON_NULL)` will leak `null` into the published JSON and may break consumer dashboards.
- `DataLayerConfig.skipClientlibInclude` controls whether the `core.wcm.components.commons.datalayer.v1` clientlib is auto-injected by the Page component. When `true`, the customer is responsible for loading the client data layer themselves. The Page model must not bypass this flag.
- The Data Layer kill-switch is the CA-config (`DataLayerConfig`), not an OSGi config. There is no global toggle: enabling/disabling per-template-tree happens via `/conf/.../sling:configs/com.adobe.cq.wcm.core.components.internal.DataLayerConfig`.

## Resolution pipeline

1. The Page model resolves `DataLayerConfig` via Sling CA-config from the current resource. If `enabled=false`, no per-component data-layer attributes are rendered and the auto-include of the client clientlib is skipped.
2. When enabled, each Core Component's HTL emits a `data-cmp-data-layer='{ "<id>": { … } }'` attribute on its root element. The JSON is produced by the model's `getData()` (returning a `*Data` interface) routed through Jackson via the mixins under `models/datalayer/jackson/`.
3. The Page model emits a top-level `<script>` snippet that pushes `{event: 'cmp:show', component: <id>}` into the configured global array (default `adobeDataLayer`).
4. Container components (Carousel, Tabs, Accordion, Container) implement [`ContainerData`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/ContainerData.java) which exposes `shownItems` so client-side state changes (slide change, tab switch) can dispatch `cmp:show`/`cmp:hide` events with the right child id.
5. The browser-side data-layer client (loaded via the bundled clientlib unless `skipClientlibInclude=true`) reads the per-component DOM attributes and pushes them into the global array as components mount.

## Anti-patterns

- **Emitting data-layer attributes without checking `DataLayerConfig.enabled`** — leaks the `data-cmp-data-layer` attribute on every page even when authors think the feature is off. Any new component must read the Page model's `isDataLayerEnabled()` (or the underlying CA-config) before contributing.
- **Hard-coding `window.adobeDataLayer.push(...)` in component JS** — bypasses the configurable name. Use the value exposed by the Page component (read from `DataLayerConfig.name`).
- **Putting Data Layer logic inside HTL string-concatenation** — produces unencoded JSON that breaks for any title/description containing quotes. Always serialise via the `*Data` interface and the configured Jackson mapping.
- **Reusing one `*Data` impl across multiple component types** — the JSON shape is part of the public contract; sharing an impl between `Image` and `Teaser` couples them. Each component owns its `*Data` impl in its `internal/models/vN/` package.

## Source pointers

- [`DataLayerConfig`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/DataLayerConfig.java) — CA-config, default `enabled=false`, default name `adobeDataLayer`, `skipClientlibInclude`.
- [`models/datalayer/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/) — public `*Data` interfaces.
- [`models/datalayer/builder/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/builder/) — fluent builders used by `*Impl` classes to assemble `*Data`.
- [`models/datalayer/jackson/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/jackson/) — Jackson mixins controlling JSON shape.
- [`DATA_LAYER_INTEGRATION.md`](../../../DATA_LAYER_INTEGRATION.md) — public contract for downstream consumers.
