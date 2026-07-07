# Design: ContentAI Supported Search Core Component

**Date:** 2026-07-06 (revised 2026-07-07)
**Status:** Proposed — pending new Jira ticket + separate feature branch
**Related but out of scope:** `GRANITE-69682` ("Quick Search Core Component: Add AI Search toggle with ?{}? prefix support") — already in review, PR [#3055](https://github.com/adobe/aem-core-wcm-components/pull/3055). That ticket adds an opt-in Semantic Search toggle to the existing Quick Search component; it does not add any new drag-and-drop component. **Semantic Search does not get its own standalone component** — it remains a toggle inside Quick Search, scoped entirely to GRANITE-69682.

## Background

`aem-core-wcm-components` today ships one search-related component: **Quick Search** (`search/v2`, `search/v3`), a fulltext search box backed by the JCR/QueryBuilder-based `SearchResultServlet`. `GRANITE-69682` adds a Semantic Search toggle to `search/v3` that prepends a `?{}?` prefix to the fulltext query. Both the plain fulltext path and the `?{}?`-prefixed semantic path currently **call Elasticsearch directly** (via Oak's `?{}?` resolution / Oak Search Elastic integration) — there is no Content AI microservice API in that request path today.

There is no dedicated, drag-and-droppable **generative/AI-answer search** component in the component browser. Adobe's internal "Launch Site Search for AEM Cloud Service" initiative (Site Search Wiki) explicitly names this as a strategic gap: *"No fully GA ContentAI Site Search component exists today... this initiative closes that gap."*

This is the capability already running in production for **Inside Adobe search** (per internal Content AI Execution documentation, which lists "Inside Adobe" and "Generative Search" among live Content AI usages) — this project is not building that backend, it's building the AEM authoring component and thin proxy layer that exposes the same kind of capability inside `aem-core-wcm-components`.

A reference implementation exists at `OneAdobe/cais-trial-reference-content` (a standalone AEM demo package, not part of core-wcm-components) showing one working pattern: linked components communicating via a shared JS store, backed by Sling servlets that proxy to Content AI's experimental REST APIs using IMS OAuth credentials. This design borrows ideas from that reference (server-side proxy servlet, OSGi config for credentials) but adapts them to core-wcm-components' own conventions (resourceType-bound servlets, not fixed `/bin/` paths) rather than copying it directly.

## Key architecture distinction (critical)

| Component | Query path | Backend called |
|---|---|---|
| Quick Search (existing, incl. GRANITE-69682's Semantic Search toggle) | JCR/QueryBuilder via `SearchResultServlet`, optionally `?{}?`-prefixed | **Elasticsearch directly** (via Oak Search Elastic) |
| **ContentAI Supported Search** (new, this ticket) | New servlet proxying to a **Content AI microservices API** | **Content AI API**, which itself calls Elasticsearch — the AEM component never talks to Elasticsearch directly |

This is the core reason "ContentAI Supported Search" needs its own component rather than reusing Quick Search's path: Quick Search and its Semantic Search toggle are wired directly to Elasticsearch through Oak, with no Content AI service layer in between. ContentAI Supported Search must go through Content AI's microservices API — the same class of API the reference repo's servlets call (`contentAI/search`, `contentAI/gensearch`), and (pending confirmation) the same API layer Inside Adobe search uses.

## Goals

Build one new, independently drag-and-droppable core component:

- **ContentAI Supported Search** — a self-contained search component whose queries go through the Content AI microservices API (not directly to Elasticsearch), modeled on the Inside Adobe search experience. Depending on which Content AI API it calls, this may return either enriched/semantic search results, a generated answer with sources, or both — to be confirmed by the API research below.

This ships in a **new ticket and new branch**, separate from `GRANITE-69682`.

## Non-goals

- Modifying Quick Search v2/v3 or the GRANITE-69682 toggle behavior.
- **A standalone Semantic Search component — dropped from scope.** Semantic search as a capability remains available only via the Quick Search v3 toggle (GRANITE-69682).
- Building or modifying the Content AI backend/API itself — it already exists and is in production use elsewhere (Inside Adobe, PetPlace, Leviton).
- A linked-components pattern (separate input/output components wired by ID) — the component is self-contained, matching the authoring experience of the existing Quick Search component.

## Open research: which Content AI API to call

**This is the current blocker before the design can be finalized.** The reference repo (`cais-trial-reference-content`) calls an experimental, time-boxed endpoint (`/adobe/experimental/contentai-expires-20251231/contentAI/search` and `.../contentAI/gensearch`) — not necessarily the API Inside Adobe search actually runs on in production, and not necessarily stable enough to build a shipping core component against.

Questions to resolve (fluffyjaws first; escalate to the Content AI team if it can't answer):
1. What is the current, production-grade Content AI microservices API for search/answer generation — is it the same experimental endpoint the reference repo uses, a newer versioned replacement, or the public API surfaced in Adobe Developer Console (`developer.adobe.com/experience-cloud/experience-manager-apis/api/experimental/contentai/`)?
2. Which specific API does Inside Adobe search call today?
3. Does that API return a generated answer with citations (gensearch-style), enriched/hybrid search results (search-style), or both in one call?
4. What auth model does that API expect from an AEM-side caller (IMS OAuth client credentials, as the reference repo uses, or something else)?

*(Status: fluffyjaws session needs re-authentication (`fj login`) before this research can run — pending.)*

## Component: ContentAI Supported Search

**Location:** `core/wcm/components/contentaisearch/v1/contentaisearch` (naming may adjust once the ticket exists), `componentGroup=".core-wcm"`, self-contained (own input + own results/answer display).

**Backend servlet:** A new servlet, registered idiomatically via `sling.servlet.resourceTypes` bound to this component's resource type (following `SearchResultServlet`'s pattern), not a fixed `/bin/...` path like the reference repo's demo servlets. Because the servlet resolves against the specific dropped component instance's resource, it can read that instance's dialog config (e.g. an integrator/index override) directly via `request.getResource()`.

The servlet proxies server-side to the Content AI microservices API identified by the research above, using IMS OAuth credentials — **exact request/response shape depends on which API is confirmed.**

**OSGi config:** New `ContentAIConfigService` (global, instance-wide) — domain, IMS client id/secret (via AEM Crypto or Cloud Manager environment variables, consistent with how GRANITE-69682's discussion resolved credential handling), default integrator/index id.

**Dialog:** placeholder text, optional per-instance integrator/index override (for multi-brand pages), optional disclaimer text override if the response includes a generated answer.

**HTL/JS:** shape depends on the confirmed API response (results list vs. generated answer vs. both) — to be finalized once the API research above is resolved. Expected in either case:
- Loading state while awaiting the Content AI response
- Error state with a retry action; generic user-facing error message, real failure detail (auth errors, timeouts, malformed responses) logged server-side only
- If the API returns a generated answer: rendered answer text + a **Sources** list, plus a persistent disclaimer (default i18n: *"AI-generated responses may be inaccurate. Verify important information."*)

**Testing:**
- Client JS unit tests: loading / results-or-answer / error / retry states
- Java servlet tests with a mocked HTTP client for the Content AI call: success, 401/403, timeout, malformed response
- OSGi config service tests: missing/invalid credentials fail cleanly, no stack trace exposed to the user

## Rollout & feature-toggle plan

New toggle required (e.g. `FT_GRANITE-<new-ticket>`), because this calls a Content AI microservices API that (pending the research above) likely requires per-customer onboarding, not a blanket AEM-layer capability like `FT_GRANITE-56831`.
- Default **off**, created via the standard release-toggles/LaunchDarkly workflow (same operating model as `FT_GRANITE-56831`)
- Flip default to **on** once the target Content AI API is confirmed stable/GA
- Toggle gates both servlet registration and component visibility in the component browser, so it can't be dropped where the backend isn't provisioned

**Staged rollout:** internal dogfood first → confirm whether Inside Adobe search's existing implementation maps to the same API this proxies to → opt-in for early customers via the toggle → GA once the API is stable.

## Open questions

1. **Which Content AI microservices API to call — see "Open research" above. Blocking.**
2. Exact toggle key/name — to be created via the standard release-toggles workflow once the new ticket exists.
3. Component and resource-type naming ("ContentAI Supported Search" vs a shorter internal name) — to confirm once the ticket is filed.

## Relationship to GRANITE-69682

This work is fully additive: it does not touch `search/v2`, `search/v3`, or the Semantic Search toggle GRANITE-69682 ships. It goes in a separate new ticket and separate branch. Semantic Search as a standalone component is explicitly out of scope — dropped per this revision.
