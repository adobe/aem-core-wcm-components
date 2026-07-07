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

This is the core reason "ContentAI Supported Search" needs its own component rather than reusing Quick Search's path: Quick Search and its Semantic Search toggle are wired directly to Elasticsearch through Oak, with no Content AI service layer in between. ContentAI Supported Search must go through Content AI's microservices API — see "Confirmed API" below for the specific endpoint.

## Goals

Build one new, independently drag-and-droppable core component:

- **ContentAI Supported Search** — a self-contained search component whose queries go through Content AI's `content-sources/gensearch` API (not directly to Elasticsearch), modeled on the GenSearch half of the Inside Adobe search experience. Returns a generated answer with cited sources, not a raw results list.

This ships in a **new ticket and new branch**, separate from `GRANITE-69682`.

## Non-goals

- Modifying Quick Search v2/v3 or the GRANITE-69682 toggle behavior.
- **A standalone Semantic Search component — dropped from scope.** Semantic search as a capability remains available only via the Quick Search v3 toggle (GRANITE-69682).
- Building or modifying the Content AI backend/API itself — it already exists and is in production use elsewhere (Inside Adobe, PetPlace, Leviton).
- A linked-components pattern (separate input/output components wired by ID) — the component is self-contained, matching the authoring experience of the existing Quick Search component.

## Confirmed API: Content AI `content-sources/gensearch`

Researched via fluffyjaws (internal wiki/Slack). The Content AI team officially launched a new, forward-looking **"Content AI APIs"** suite (~2026-06-25): **AI Search API** (hybrid/vector/full-text/filter, multi-index — search-only, no generated answer), **Content Sources API**, and **Discovery API**. This supersedes the older `/adobe/experimental/contentAI/...` "nexus" endpoints (which the reference repo's demo servlets call, and which remain supported for existing configs for at least 3 more months — no forced migration for those, but not the basis to build new work on).

For the generated-answer capability specifically, a design wiki dated 2026-06-10 (*"20260605 - GenSearch on Content Sources"*) documents the current, actively-designed endpoint — a direct sibling of content-source search on the new Content Sources surface, reusing "GenSearch's existing RAG pipeline":

```
POST /contentAI/content-sources/gensearch          — blocking RAG search
POST /contentAI/content-sources/gensearch/stream    — streaming (SSE), Accept: text/event-stream

Request:
{
  "contentSource": { "name": "leviton-website" },   // Content Source name, not a raw index
  "configName": "support",                          // named config; defaults to "default"
  "query": "How do I install a smart switch?",
  "metadata": { "geography": "US" }                 // optional
}

Response (200 OK, QueryResult):
{
  "query": "...",
  "result": "To install a Decora smart switch, first turn off power at the breaker ...",
  "retrievedLinks": [ { "url": "https://leviton.com/install-guide", "title": "Installation Guide" } ],
  "questions": [ "How do I reset the switch?", "Is a neutral wire required?" ],
  "qmaId": "qma-7f3a...",
  "type": "end"
}
```

This is real, in-use for an external customer (Leviton, not just Inside Adobe) — an earlier Slack thread suggesting gensearch was "Inside Adobe only" turned out to describe a narrower context, not a blanket restriction. **This is the API `ContentAISupportedSearchServlet` will proxy to.**

**Auth:** service-token based (IMS "api-first" service token / user service-token), per the Content-AI Services development plan doc — consistent with holding credentials in a global OSGi config rather than per-component dialog fields, as already planned.

**Note on "Inside Adobe" as a reference (per your suggestion):** found *"Inside Adobe Gen AI Search integration with ServiceNow"* (wiki) — Inside Adobe's actual search UX blends **Coveo** (results list) with **Content AI's GenSearch** (the AI answer) as two separate backends, not one unified call. Our component's scope (answer + sources) matches the GenSearch half of that pattern only; it does not reproduce Coveo's results-list half, which is out of scope here.

**Residual open item:** confirm with the Content AI team whether `content-sources/gensearch` is stable enough to build against now, or still pre-GA (the wiki proposal is dated 2026-06-10, three and a half weeks before this design). Given Content AI's stated GA milestone ("CGA - Content AI is GA - end of June"), it is likely at or near GA, but should be verified before implementation starts.

## Component: ContentAI Supported Search

**Location:** `core/wcm/components/contentaisearch/v1/contentaisearch` (naming may adjust once the ticket exists), `componentGroup=".core-wcm"`, self-contained (own input + own results/answer display).

**Backend servlet:** `ContentAISupportedSearchServlet`, registered idiomatically via `sling.servlet.resourceTypes` bound to this component's resource type (following `SearchResultServlet`'s pattern), not a fixed `/bin/...` path like the reference repo's demo servlets. Because the servlet resolves against the specific dropped component instance's resource, it reads that instance's dialog config (content source name, config name) directly via `request.getResource()`.

The servlet proxies server-side to `POST /contentAI/content-sources/gensearch` (blocking) — streaming via `/gensearch/stream` is a possible v2 enhancement, not required for v1 — using an IMS service token obtained via the OSGi config below. Request/response shape as documented in "Confirmed API" above.

**OSGi config:** New `ContentAIConfigService` (global, instance-wide) — Content AI domain/base URL, IMS service-token credentials (via AEM Crypto or Cloud Manager environment variables, consistent with how GRANITE-69682's discussion resolved credential handling), default `contentSource` name and `configName`.

**Dialog:** placeholder text, optional per-instance `contentSource`/`configName` override (for multi-brand pages), optional disclaimer text override.

**HTL/JS:**
- Loading state while awaiting the Content AI response
- Rendered answer (`result`) + a **Sources** list from `retrievedLinks` (url + title)
- Optionally surface `questions` (API-suggested follow-ups) as clickable chips that re-trigger a search — nice-to-have, not required for v1
- Error state with a retry action; generic user-facing error message, real failure detail (auth errors, timeouts, malformed responses) logged server-side only
- Persistent disclaimer text below the answer (default i18n: *"AI-generated responses may be inaccurate. Verify important information."*)
- `qmaId` retained client-side (not rendered) for potential future feedback/thumbs-up-down wiring — out of scope for v1, but worth not discarding since the API already returns it

**Testing:**
- Client JS unit tests: loading / answer / error / retry states
- Java servlet tests with a mocked HTTP client for the `gensearch` call: success, 401/403, timeout, malformed response
- OSGi config service tests: missing/invalid credentials fail cleanly, no stack trace exposed to the user

## Rollout & feature-toggle plan

New toggle required (e.g. `FT_GRANITE-<new-ticket>`), because this calls a Content AI microservices API that requires per-customer onboarding (a provisioned Content Source + config), not a blanket AEM-layer capability like `FT_GRANITE-56831`.
- Default **off**, created via the standard release-toggles/LaunchDarkly workflow (same operating model as `FT_GRANITE-56831`)
- Flip default to **on** once `content-sources/gensearch` is confirmed stable/GA (see residual open item above)
- Toggle gates both servlet registration and component visibility in the component browser, so it can't be dropped where the backend isn't provisioned

**Staged rollout:** internal dogfood first → confirm with the Content AI team that `content-sources/gensearch` is the right/stable target (not still shifting) → opt-in for early customers via the toggle → GA once the API is stable.

## Open questions

1. Is `content-sources/gensearch` stable/GA enough to build against now, or still pre-GA? (see residual open item above — needs Content AI team confirmation)
2. Exact toggle key/name — to be created via the standard release-toggles workflow once the new ticket exists.
3. Component and resource-type naming ("ContentAI Supported Search" vs a shorter internal name) — to confirm once the ticket is filed.
4. Exact IMS service-token acquisition flow for an AEM-side caller of `content-sources/gensearch` — the Content-AI Services development plan doc names "api-first service-token + user service-token" as the token type but doesn't fully spell out the AEM-side integration steps; confirm with the Content AI team before implementation.

## Relationship to GRANITE-69682

This work is fully additive: it does not touch `search/v2`, `search/v3`, or the Semantic Search toggle GRANITE-69682 ships. It goes in a separate new ticket and separate branch. Semantic Search as a standalone component is explicitly out of scope — dropped per this revision.
