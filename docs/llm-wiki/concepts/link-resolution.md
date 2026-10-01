# Link resolution pipeline

**Purpose** — every URL rendered by a Core Component (button, teaser, navigation entry, content fragment field, image link…) flows through `LinkManager`/`LinkBuilder`. This page documents how a raw `linkURL` property becomes a final href, including shadowing, validation, and `PathProcessor` extension points.

## Invariants

- All Core Components must obtain links via [`LinkManager`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/commons/link/LinkManager.java) (adapted from `SlingHttpServletRequest`) and never construct hrefs by string concatenation. `LinkManager` is the only path that runs the configured `PathProcessor` chain — see [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java).
- The set of allowed `target` attribute values is the unmodifiable set `{_blank, _parent, _top}`. `_self` is accepted in the dialog but produces **no** target attribute on render. Any other value is dropped — see `VALID_LINK_TARGETS` in [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java).
- "Shadowing" (a redirecting page silently rendered as its target) is **on by default**. To preserve the original page, the resource property `disableShadowing=true` must be set explicitly. The default constant is `PROP_DISABLE_SHADOWING_DEFAULT = false`. See [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java).
- `PathProcessor` is a public SPI — see [`services/link/PathProcessor`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/link/PathProcessor.java). Multiple processors are tried in OSGi service-ranking order; the first one whose `accepts(path, request)` returns `true` handles the link. The built-in [`DefaultPathProcessor`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/DefaultPathProcessor.java) accepts everything and runs last. Customers register processors at higher ranking to intercept specific path shapes (e.g. asset URLs, vanity prefixes).
- `LinkBuilder` is mutable and request-scoped: it is instantiated per call to `LinkManager.get(...)` and not safe to share across threads or requests. Each `LinkBuilder.build()` produces a new immutable `Link`.

## Resolution pipeline

1. An HTL script or Sling Model calls `linkManager.get(resource)` (or `.get(page)` / `.get(asset)`). `LinkManager` is a request-scoped `@Model`.
2. `LinkManager` reads `linkURL`, `linkTarget`, and `linkAccessibilityLabel`/`linkTitle` from the resource (via `@ScriptVariable properties` if injected through HTL).
3. The resolved path is offered to the registered `PathProcessor` chain in service-ranking order. The first accepting processor:
   - normalises/transforms the path,
   - decides whether the link should be externalised, and
   - may attach selectors/extension/fragment/query parts.
4. If the resolved target is a `Page` and shadowing is enabled (default), the chain follows `cq:redirectTarget` until it lands on a non-redirecting page or the chain ends. Setting `disableShadowing=true` on the source resource short-circuits this step and keeps the original page.
5. Target validation: if `linkTarget` is not in `VALID_LINK_TARGETS`, no `target` attribute is rendered. `_self` is also dropped (kept only as a dialog default).
6. The `LinkBuilder` produces a `Link` whose `getURL()` is the final href and whose `getHtmlAttributes()` is the map serialised by [`LinkHtmlAttributesSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/LinkHtmlAttributesSerializer.java) when the model is exported to JSON.

## Anti-patterns

- **Building a link in HTL via `${'http://...' @ context='uri'}`** — bypasses `PathProcessor`, shadowing, and target validation. Always go through the model's `@getLink` (or equivalent) which delegates to `LinkManager`. Customer overlays that "fix" a link in HTL will silently regress shadowing.
- **Setting an arbitrary `linkTarget`** — values outside `{_blank, _parent, _top, _self}` are dropped on output. Authors expecting `target="_new"` will see no target attribute in published HTML. Validate authoring dialogs against `VALID_LINK_TARGETS`.
- **Caching a `LinkBuilder` across requests** — it carries request state. The OSGi `LinkManager` is request-scoped (`@Model(adaptables = SlingHttpServletRequest.class)`); never store its `LinkBuilder` in a static or service field.
- **Registering a `PathProcessor` without a guard in `accepts`** — a processor that returns `true` unconditionally and is registered above `DefaultPathProcessor` will swallow every link in the system, including pages that should be shadow-resolved. Always scope `accepts` to a specific path prefix or resource type.

## Source pointers

- [`LinkManager`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/commons/link/LinkManager.java) — public adaptable contract.
- [`LinkBuilder`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/commons/link/LinkBuilder.java) — fluent builder.
- [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java) — request-scoped `@Model`, defines validation constants and shadowing flag.
- [`LinkBuilderImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkBuilderImpl.java) — runs the `PathProcessor` chain.
- [`DefaultPathProcessor`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/DefaultPathProcessor.java) — terminal/default processor.
- [`PathProcessor` SPI](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/link/PathProcessor.java) — extension point.
- [`LinkHtmlAttributesSerializer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/jackson/LinkHtmlAttributesSerializer.java) — JSON shape of links.
