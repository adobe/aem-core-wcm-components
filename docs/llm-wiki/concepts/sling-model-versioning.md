# Sling Model versioning and ImplementationPicker

**Purpose** — explain how the Core Components bundle resolves which `*Impl` class implements a public model interface (e.g. `Image`, `Page`, `Teaser`) at request time, given that several versioned implementations coexist in the same bundle.

## Invariants

- A public model interface lives in `com.adobe.cq.wcm.core.components.models` (e.g. [`Image`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/Image.java)). Every concrete implementation lives under `com.adobe.cq.wcm.core.components.internal.models.v<N>` and the version segment in the package path is the version segment used by `LatestVersionImplementationPicker`. Renaming the package breaks version selection — see [`LatestVersionImplementationPicker`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/LatestVersionImplementationPicker.java).
- When the same public interface is `@Model(adapters = …)` from multiple `vN` impls, the **highest** numeric `vN` wins. The picker compares `INTERNAL_MODEL_PATTERN` matches and returns the higher integer — see [`LatestVersionImplementationPicker.pick`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/LatestVersionImplementationPicker.java).
- The picker only fires for adapters whose package equals `com.adobe.cq.wcm.core.components.models`. Adapters in any other package fall through (`return null`) and Sling's default picker chain continues. Do not rely on this picker to disambiguate downstream/customer models.
- Downstream Adobe internal models (anything under `com.adobe.cq.*` that is **not** in `com.adobe.cq.wcm.core.components.internal.models`) are filtered out before version comparison. A consumer cannot win the picker by adding an `@Model` in a sibling Adobe package.
- The picker's `@ServiceRanking(1)` places it after `ResourceTypeBasedResourcePicker` (ranking 0/default). Resource-type-based picks always win over version-based picks. A `vN` impl bound to a more specific `resourceType` than `vN+1` will still be selected for that resource type.
- Version bumps are an API event: a new `vN+1` impl must coexist with `vN` (users depend on it) and the matching HTL must live at `content/.../foo/vN+1/foo`. Removing or renaming a `vN` package is a breaking change even if no public interface changes.

## Resolution pipeline

1. Sling adapts a `Resource` (or `SlingHttpServletRequest`) to a public interface like `Image.class`.
2. Sling Models gathers every `@Model(adapters = Image.class)` candidate visible to the bundle.
3. The `ResourceTypeBasedResourcePicker` (registered by Sling Models, higher service ranking) tries to pick by `resourceType` match. If a single candidate matches the resource's `sling:resourceType` (or supertype chain), it wins and the chain stops.
4. If no resource-type pick is decisive, `LatestVersionImplementationPicker` runs. It:
   - filters to internal Core Components models or third-party (non-`com.adobe.cq.*`) models;
   - parses `vN` out of each FQN with `INTERNAL_MODEL_PATTERN`;
   - returns the candidate with the largest `N`. Ties or non-matches fall back to `implementationsTypes[0]`.
5. The picked class is instantiated by Sling Models. From here, `@ScriptVariable`, `@Self`, `@OSGiService`, and `@PostConstruct` run inside that single picked impl — there is no further dispatch between versions.

## Anti-patterns

- **Cross-version inheritance for "code reuse"** — making `v2.FooImpl extends v1.FooImpl` couples the public surface of two versions. A bug fix in `v1` to satisfy the older HTL contract will leak into `v2` and vice versa. Prefer composition or a private helper in `internal/helper/`.
- **Adding a `vN+1` impl without HTL** — the picker will start returning `vN+1` for every `Image.class` adaptation in the bundle, even where the JCR resource still references `core/wcm/components/image/v2/image`. Until the HTL exists, `data-sly-use.image="com.adobe.cq.wcm.core.components.models.Image"` will run code that doesn't match the markup contract. Always land impl + HTL together.
- **Placing a `vN` impl in `com.adobe.cq.wcm.core.components.models`** — the picker treats only `internal.models.v<N>` as version-aware. An impl in the public package is filtered out and never wins a version comparison.
- **Relying on `vN+1` to override a customer's downstream `@Model`** — a downstream model in a non-`com.adobe.cq` package can outrank Core Components by `service.ranking` or by binding to a more specific `resourceType`. Don't assume newest Core wins for customer-overlay components.

## Source pointers

- [`LatestVersionImplementationPicker`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/LatestVersionImplementationPicker.java) — the picker; `INTERNAL_MODEL_PATTERN` is the source of truth for the package convention.
- [`models/`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/) — public, versionless model interfaces (the contract).
- [`internal/models/v1`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/), [`v2`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v2/), [`v3`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v3/), [`v4`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v4/) — versioned implementations that the picker ranks.
- [`VERSIONS.md`](../../../VERSIONS.md) — historical version-to-AEM matrix; update when bumping.
