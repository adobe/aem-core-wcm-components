# JSON model export (Sling Model Exporter)

**Purpose** — explain how Core Components produce the JSON shape consumed by the SPA Editor and headless clients via the `model.json` Sling Model Exporter.

## Invariants

- A component is exported as JSON when its public Sling Model is annotated `@Exporter(name = "jackson", extensions = "json")`. The export endpoint is `<page>.model.json` for the Page model and `<resource>.model.json` for individual components, served by the Sling Model Exporter — the bundle does not register a custom servlet for it.
- Jackson behaviour for these models is customised by registered `ModuleProvider` services. [`PageModuleProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/PageModuleProvider.java) is the global provider that wires in module-level configuration; [`DefaultMethodSkippingModuleProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/DefaultMethodSkippingModuleProvider.java) excludes `default` interface methods from serialisation. Without the latter, default methods on public model interfaces would be serialised as JSON properties — breaking the documented JSON contract.
- [`PageSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/PageSerializer.java) produces the SPA-Editor-compatible page tree (`:items`, `:itemsOrder`, `:type`). Children of a page are serialised in **`:itemsOrder` order**, not JCR child-node order; consumers parse `:itemsOrder` to render in the correct sequence.
- [`LinkHtmlAttributesSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/LinkHtmlAttributesSerializer.java) controls the JSON shape of `Link.htmlAttributes`. The map is serialised flat (no nested `attrs:` envelope) — see [Link resolution pipeline](link-resolution.md) for how the underlying `Link` is built.
- The `*Data` interfaces in [`models/datalayer/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/) are serialised through dedicated mixins under [`models/datalayer/jackson/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/jackson/). Adding a getter without a corresponding mixin entry leaks the value into the published JSON — see [Data Layer integration](data-layer.md).
- The `model.json` shape is **public API**. A renamed JSON property is a breaking change for SPA Editor consumers and headless clients regardless of whether the underlying Java getter changed signature.

## Resolution pipeline

1. A request hits `<page>.model.json`. Sling Models Jackson Exporter resolves the page resource to the highest-versioned `Page` impl (via [`LatestVersionImplementationPicker`](sling-model-versioning.md)).
2. The Jackson `ObjectMapper` for the export is built by the registered `ModuleProvider` services. `PageModuleProvider` and `DefaultMethodSkippingModuleProvider` contribute modules that:
   - skip `default` interface methods (so they don't show up as JSON properties);
   - apply mixins to data-layer types and links;
   - add the SPA-Editor-specific serialisers.
3. `PageSerializer` walks the page's container tree, serialising each child component via its own `@Exporter`-annotated model. The traversal honours `:itemsOrder` from the container model rather than JCR child-node order.
4. Each child component's getter chain runs synchronously inside the same request, on the same `ResourceResolver`. There is no caching layer between Sling Models and Jackson — every `model.json` request re-runs the Sling Model lifecycle.
5. The serialised tree is returned as `application/json`. Headless clients parse `:items` / `:itemsOrder` / `:type` to drive rendering or routing.

## Anti-patterns

- **Adding a public getter to a model interface to expose internal state** — the Jackson exporter serialises every public getter that isn't `default` or annotated `@JsonIgnore`. New getters become part of the public JSON contract instantly. If the value is not meant to be public, mark it `@JsonIgnore` or keep the method on the impl, not the interface.
- **Returning a non-JSON-serialisable type from a getter** — `Resource`, `Node`, `ResourceResolver`, etc. will fail Jackson serialisation at request time and the whole `model.json` response will 500. Always return DTOs or primitives from exported getters.
- **Reordering JCR child nodes to change render order** — has no effect on `model.json` because `PageSerializer` follows `:itemsOrder`. Changing render order requires updating the container's `:itemsOrder` payload (e.g. via the container's reorder logic), not JCR sibling order.
- **Bypassing `DefaultMethodSkippingModuleProvider`** — registering a custom Jackson module that overrides the skip-default-methods behaviour will leak every default method on every Core model into JSON, including `Link`, `Component`, `Container` defaults that downstream consumers do not expect.

## Source pointers

- [`PageModuleProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/PageModuleProvider.java) — registers Jackson module for page-level export.
- [`PageSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/PageSerializer.java) — emits SPA-Editor `:items` / `:itemsOrder` / `:type` shape.
- [`DefaultMethodSkippingModuleProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/DefaultMethodSkippingModuleProvider.java) — strips `default` methods from serialisation.
- [`LinkHtmlAttributesSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/LinkHtmlAttributesSerializer.java) — flattens `Link.htmlAttributes` for export.
- [`models/datalayer/jackson/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/datalayer/jackson/) — mixins controlling data-layer JSON.
