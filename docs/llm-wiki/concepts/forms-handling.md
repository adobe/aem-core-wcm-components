# Forms handling pipeline

**Purpose** — explain how a POST to a Core Form container is intercepted, validated, and dispatched to the configured form action handler, and the OSGi service-ranking constraints that make this work alongside Sling's default POST servlet.

## Invariants

- [`CoreFormHandlingServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/CoreFormHandlingServlet.java) is registered as **both** `Servlet` and `Filter` (`service = {Servlet.class, Filter.class}`). It receives form POSTs as a Sling servlet (resource-type bound) and as a request filter (`sling.filter.scope=request`) so it can preprocess multipart bodies before Sling's POST processing runs.
- The servlet binds to resource types `RT_CORE_FORM_CONTAINER_V1` and `RT_CORE_FORM_CONTAINER_V2` (constants in [`FormConstants`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/form/FormConstants.java)) with **selector `form` and extension `html` only**. A POST to the same resource without the `.form.html` selector/extension is **not** intercepted and falls through to Sling's default POST handler.
- The component declares `service.ranking:Integer=610`, which places it above Sling's default POST servlet but below typical authoring/security filters. Lowering this ranking will cause the default POST servlet to win and form submissions will create JCR nodes instead of invoking action handlers — see [`CoreFormHandlingServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/CoreFormHandlingServlet.java).
- The servlet delegates the actual write to `FormsHandlingServletHelper` (Foundation forms) and uses `SaferSlingPostValidator` to deny disallowed property names. Customisations must go through Foundation form actions (`foundation/components/form/actions/*`) — the servlet does not invoke models directly.
- `FormStructureHelperImpl` is a `FormStructureHelper` `@Component` that teaches Foundation Forms how to discover form fields inside a Core Form container. Without it, `FormsHandlingServletHelper` cannot enumerate the input fields and submission silently drops them — see [`FormStructureHelperImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/form/FormStructureHelperImpl.java).
- The configurationPid `com.adobe.cq.wcm.core.components.commons.forms.impl.CoreFormsHandlingServlet` is the public-facing PID for OSGi config (allowed properties, allowed action types). Renaming this PID is a breaking change for customer configs.

## Resolution pipeline

1. The browser submits to `…/jcr:content/form.form.html`. Sling resolves the resource (form container) and routes the POST.
2. As a request filter, `CoreFormHandlingServlet` runs early in the chain — its `doFilter` lets `FormsHandlingServletHelper` parse the multipart/urlencoded body and surface form fields uniformly.
3. As a servlet, it owns the POST for `(form container resource type, .form.html, POST)`. Sling's default POST servlet is overridden by the higher service ranking.
4. `SaferSlingPostValidator` rejects any parameter name not in the allow-list (configured via OSGi). Disallowed parameters cause the request to abort with a 403/400.
5. `FormsHandlingServletHelper` reads the configured `:formstart` resource pointer and resolves the **action** type (mailto, store-content, custom). It iterates fields exposed by `FormStructureHelperImpl`, applies their type-specific persistence/validation, and dispatches to the action.
6. On success, the action returns a redirect/thank-you target which Foundation Forms turns into a Sling redirect response.

## Anti-patterns

- **Submitting to a Core Form container via plain `POST` without `.form.html`** — bypasses `CoreFormHandlingServlet` entirely. Sling's default POST servlet will create or modify nodes under the form resource path. This is a common authoring mistake when a custom client writes to `formresource` instead of `formresource.form.html`.
- **Lowering `service.ranking` below the default POST servlet** — the default servlet will silently win. Form actions never run. Symptom: properties appear at the form container path but no email/redirect/post-action fires.
- **Removing `FormStructureHelperImpl` or shadowing it without re-registering a `FormStructureHelper`** — Foundation Forms can't enumerate child fields and submitted values are silently dropped from the action invocation.
- **Adding a new field type that produces a property name outside the allow-list** — `SaferSlingPostValidator` will reject the request. Either configure the allow-list (preferred) or use a Foundation-compatible field name.

## Source pointers

- [`CoreFormHandlingServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/CoreFormHandlingServlet.java) — Servlet+Filter registration, ranking, selector/extension binding.
- [`FormConstants`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/form/FormConstants.java) — `RT_CORE_FORM_CONTAINER_V1`/`V2` resource type constants.
- [`FormHandlerImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/form/FormHandlerImpl.java) — model-side helper for form processing.
- [`FormStructureHelperImpl`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/form/FormStructureHelperImpl.java) — discovers fields inside a Core Form container.
- [`FormActionTypeDataSourceServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/FormActionTypeDataSourceServlet.java) and [`FormActionTypeSettingsDataSourceServlet`](../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/FormActionTypeSettingsDataSourceServlet.java) — populate the action-type dropdown in the form container dialog.
