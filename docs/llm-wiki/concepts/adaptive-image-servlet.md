# Adaptive Image servlet registration

**Purpose** — explain how `AdaptiveImageServlet` (the servlet that serves resized renditions for the Image component) is **dynamically** registered from one or more OSGi factory configurations rather than via static `@Component(property=…)`.

## Invariants

- `AdaptiveImageServlet` is **not** registered by an `@Component` of its own; it is instantiated and registered as a Sling Servlet by [`AdaptiveImageServletMappingConfigurationConsumer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMappingConfigurationConsumer.java) once at least one [`AdaptiveImageServletMappingConfigurationFactory`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMappingConfigurationFactory.java) configuration is present.
- Each factory configuration declares a tuple of `{resourceTypes, selectors, extensions}`. The consumer registers **one Sling servlet `ServiceRegistration` per configuration**. Removing the configuration unregisters that registration. The bundle ships out-of-the-box configs for: `RTs=[core/wcm/components/image], selectors=[img]` (Image v1) and `RTs=[core/wcm/components/image, cq/Page], selectors=[coreimg]` (Image v2+) over `[jpg, jpeg, gif, png, svg]` extensions — see header comment in [`AdaptiveImageServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServlet.java).
- The servlet expects **1, 2 or 3 selectors** in addition to the matched first selector (`img`/`coreimg`). Anything else throws `IllegalArgumentException` from `doGet` — see [`AdaptiveImageServlet.doGet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServlet.java) (lines around the "Expected 1, 2 or 3 selectors" check).
- Resource-type checks at request time use the Image v1 resource type constant `IMAGE_RESOURCE_TYPE` (`core/wcm/components/image`). A request whose resource is **not** of that type takes a different rendition path (delegated/page resource), so requests under `cq/Page` only render the page's featured image — see the `isResourceType(IMAGE_RESOURCE_TYPE)` branch in [`AdaptiveImageServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServlet.java).
- The consumer also tracks the legacy `defaultResizeWidth` value in `oldAISDefaultResizeWidth` so it can re-read and re-apply policy changes; do not assume the registration is immutable for the lifetime of the bundle.
- Metrics are emitted via [`AdaptiveImageServletMetrics`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMetrics.java) (Sling commons metrics). Removing the metrics service breaks instantiation — it is a mandatory `@Reference` on the consumer.

## Resolution pipeline

1. `AdaptiveImageServletMappingConfigurationFactory` is an OSGi factory PID. Each configuration creates a `Map<String, AdaptiveImageServletMappingConfigurationFactory>` entry on the consumer's `configs` map.
2. On `bindAdaptiveImageServletMappingConfigurationFactory(...)`, the consumer:
   - reads `resourceTypes`, `selectors`, `extensions`, `defaultResizeWidth`;
   - constructs an `AdaptiveImageServlet` instance with `mimeTypeService`, `assetStore`, `metrics`, default width;
   - builds Sling servlet properties (`sling.servlet.resourceTypes`, `sling.servlet.selectors`, `sling.servlet.extensions`, `sling.servlet.methods=GET`);
   - calls `bundleContext.registerService(Servlet.class, ais, props)` and stores the `ServiceRegistration` in `serviceRegistrations`.
3. On unbind / configuration removal, the matching `ServiceRegistration.unregister()` is called and the entry is removed from `configs`.
4. At request time, Sling routes a GET like `…/jcr:content/root/image.coreimg.800.jpeg/1234567890.jpeg` to one of the registered servlet instances, based on resource-type + selector + extension match.
5. `doGet` validates selector count, locates the underlying asset (or page-level featured image when the resource type is `cq/Page`), applies the size selector against the policy-configured allowed widths, and streams the resized rendition.

## Anti-patterns

- **Adding a new `selectors=[…]` to `AdaptiveImageServlet` via static `@Component(property=…)`** — duplicates the dynamic registration and produces ambiguous Sling servlet resolution. New mappings must be added by registering an OSGi factory configuration of `AdaptiveImageServletMappingConfigurationFactory`, not by editing the servlet class.
- **Calling the servlet path with 4+ selectors** — throws on every request and is logged at WARN level. URL-construction logic in customer code or HTL must respect the 1–3 selector budget after `img`/`coreimg`.
- **Assuming the servlet is always present at OSGi startup** — until the consumer's first config bind fires, no `AdaptiveImageServlet` is registered. Tests that probe the servlet must wait for the factory configuration; integration tests in `testing/it/it.core` rely on the shipped config in `config/`.
- **Hard-coding allowed widths in HTL or model code** — widths are policy-driven and the servlet validates them. A request for a width not in the resource's policy-allowed widths is denied (404), regardless of selector match.

## Source pointers

- [`AdaptiveImageServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServlet.java) — the servlet itself (no static `@Component`); rendition logic, asset/page branching, selector validation.
- [`AdaptiveImageServletMappingConfigurationFactory`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMappingConfigurationFactory.java) — OSGi factory PID with the `{RTs, selectors, extensions, defaultWidth}` tuple.
- [`AdaptiveImageServletMappingConfigurationConsumer`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMappingConfigurationConsumer.java) — does the dynamic `bundleContext.registerService(...)` per config.
- [`AdaptiveImageServletMetrics`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/AdaptiveImageServletMetrics.java) — Sling-metrics counters/timers.
- Out-of-box configurations: [`config/`](../../../config/) for runmode-specific factory instances.
