# Component policies via Sling CA-config

**Purpose** — explain how a component's "design / policy" values reach a Sling Model at request time, and how that interacts with editable templates and the `cq:Style` chain.

## Invariants

- Core Components do **not** read OSGi configs or hard-coded constants for authoring options. Allowed widths, allowed headings, allowed colour swatches, link targets, etc. come from a `Style` (the policy bound to the component on a template) injected via `@ScriptVariable`. Compare this with the `LinkManagerImpl` `currentStyle` field — see [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java).
- A policy is bound by **resource type**. A policy authored at `core/wcm/components/page/v2/page` does not affect a page whose template uses `core/wcm/components/page/v3/page`. Bumping a component to `vN+1` requires either a new policy or updating the template's `cq:policyMapping` — see [`Sling Model versioning`](sling-model-versioning.md).
- Sling CA-config resources (`/conf/<…>/sling:configs/<config-class-fqn>`) are the storage layer for typed configs like `DataLayerConfig`. They are looked up via `ConfigurationResourceResolver`/`ConfigurationManager` from a `Resource`, which honours the `sling:configRef` chain — see [`CaConfigReferenceProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/CaConfigReferenceProvider.java).
- [`CaConfigReferenceProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/CaConfigReferenceProvider.java) registers as a WCM `ReferenceProvider`, which means CA-config resources used by a page are picked up by AEM's reference/replication machinery (page activation, MSM rollout). Configs that should travel with a page **must** be discoverable through the resource resolver chain (`sling:configs` child of a `sling:configRef` ancestor), not stored elsewhere.
- The bundled `commons.datalayer.v1` and similar clientlib categories are policy-controlled via `DataLayerConfig` rather than per-component dialogs. Toggling them from the dialog is **not** an option — components read the CA-config exclusively.
- `ContainerPostProcessor` (in the same package) handles default child-resource population on policy or template changes; do not write component logic that assumes children are immutable across a re-policy.

## Resolution pipeline

1. A request hits a page. Sling resolves the template policy mapping for each component resource and exposes it as `currentStyle` (an instance of `com.day.cq.wcm.api.designer.Style`).
2. The component's Sling Model receives `currentStyle` via `@ScriptVariable(injectionStrategy = OPTIONAL)`. Optional because some renders (preview, headless) may run without a template.
3. For typed CA-configs (e.g. `DataLayerConfig`), the model adapts a `Resource` (typically the page's `jcr:content`) to the config interface via `ConfigurationBuilder.as(Class)`. Lookup walks the resource's ancestors for `sling:configRef` and resolves the matching `sling:configs` child.
4. AEM activation / replication asks every registered `ReferenceProvider` for references rooted at a page. `CaConfigReferenceProvider` returns the `sling:configs` resources discovered for that page so they are replicated alongside content.
5. Policy and CA-config values are read **per request** — they are not cached at component instantiation. Changing a policy and re-rendering the page will reflect the new value without restart.

## Anti-patterns

- **Reading from a hardcoded path like `/etc/designs/...` or `/conf/global/...`** — bypasses the CA-config resolver chain, doesn't follow `sling:configRef`, and silently breaks for sites in a different config hierarchy. Always go through `ConfigurationBuilder.as(Class)`.
- **Putting authoring options in OSGi configuration** — OSGi configs are global. Authors expect per-template / per-site overrides. Anything author-tunable belongs in a CA-config or a `cq:Style` policy.
- **Assuming a policy is shared between `vN` and `vN+1`** — a policy bound to one resource type does not apply to another. When introducing a new component version, either alias the resource type chain or migrate authored policies.
- **Skipping `CaConfigReferenceProvider`-style registration when adding a new CA-config that should travel with content** — without it, page activation will not include the configuration and replicated/published pages will silently fall back to defaults.

## Source pointers

- [`DataLayerConfig`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/DataLayerConfig.java) — example typed CA-config interface.
- [`CaConfigReferenceProvider`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/CaConfigReferenceProvider.java) — wires CA-config resources into AEM's reference graph for replication.
- [`ContainerPostProcessor`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/ContainerPostProcessor.java) — runs on container resource changes; relevant for policy-driven default children.
- [`CONFIGS.md`](../../../CONFIGS.md) — inventory of every OSGi config and CA-config the bundle ships.
- Style/policy injection example: [`LinkManagerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/link/LinkManagerImpl.java) `@ScriptVariable currentStyle`.
