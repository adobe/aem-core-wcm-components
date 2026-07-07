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
| **ContentAI Supported Search — results layer** (new component, reused mechanism) | Same `SearchResultServlet`, always plain (no `?{}?`) | **Elasticsearch directly**, same as Quick Search v2 |
| **ContentAI Supported Search — GenSearch layer** (new, this ticket) | New servlet proxying to a **Content AI microservices API** | **Content AI API** (`content-sources/gensearch`), which itself calls Elasticsearch internally — the AEM component never talks to Elasticsearch for this layer |

ContentAI Supported Search issues **two independent requests per query** when its toggle is on: one to the existing `SearchResultServlet` for the results list (identical mechanism to Quick Search v2, no new backend needed for this part), and one to the new servlet proxying `content-sources/gensearch` for the AI answer. When the toggle is off, only the first request fires.

## Goals

Build one new, independently drag-and-droppable core component:

- **ContentAI Supported Search** — a self-contained search component with **two layers**:
  1. **Baseline: Quick Search results** — plain lexical fulltext search, reusing the existing `SearchResultServlet` (same mechanism as Quick Search v2's plain mode). Always runs.
  2. **GenSearch AI answer** — a generated answer with cited sources from Content AI's `content-sources/gensearch` API, layered on top of the baseline results.

  A **toggle, default ON**, controls whether layer 2 runs:
  - **Toggle ON (default):** shows Quick Search results **and** the GenSearch AI answer
  - **Toggle OFF:** shows Quick Search results only (plain lexical, no `?{}?` prefix — this fallback is intentionally simpler than Quick Search v3's own semantic toggle, and independent of it)

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

**Location:** `core/wcm/components/contentaisearch/v1/contentaisearch` (naming may adjust once the ticket exists), `componentGroup=".core-wcm"`, self-contained (own input, own results list, own AI-answer panel, own toggle).

**Java Sling Model:** New `ContentAISupportedSearch` interface + impl, combining Quick Search's existing properties (`id`, `relativePath`, `searchTermMinimumLength`, `resultsSize`, `i18nMessages`) with GenSearch-specific ones (`contentSource`, `configName`, `genSearchEnabledByDefault` — backs the toggle's default state, `true` unless overridden).

**Backend — two servlets, two calls per query when the toggle is on:**
1. **Results layer:** reuses the existing `SearchResultServlet` as-is (same call Quick Search v2 makes — plain fulltext, no `?{}?` prefix). No new backend code for this layer.
2. **GenSearch layer:** new `ContentAISupportedSearchServlet`, registered idiomatically via `sling.servlet.resourceTypes` bound to this component's resource type (following `SearchResultServlet`'s pattern), not a fixed `/bin/...` path like the reference repo's demo servlets. Because it resolves against the specific dropped component instance's resource, it reads that instance's dialog config (`contentSource`, `configName`) directly via `request.getResource()`. Proxies server-side to `POST /contentAI/content-sources/gensearch` (blocking; `/gensearch/stream` is a possible v2 enhancement) using an IMS service token from the OSGi config below.

Client JS fires both requests in parallel on each query when the toggle is on, and only the results-layer request when it's off.

**OSGi config:** New `ContentAIConfigService` (global, instance-wide) — Content AI domain/base URL, IMS service-token credentials (via AEM Crypto or Cloud Manager environment variables, consistent with how GRANITE-69682's discussion resolved credential handling), default `contentSource` name and `configName`.

**Dialog:** search root path, min length, results size (same as Quick Search v2) + `contentSource`/`configName` override (for multi-brand pages) + optional disclaimer text override + a property controlling the toggle's **default** state (`true`/enabled unless an author explicitly turns it off for a given instance).

**HTL/JS:**
- A visible toggle (default **checked/on**) — same UI convention as Quick Search v3's AI-toggle checkbox, but semantically controls the GenSearch answer layer, not a search-mode switch
- Results list (identical rendering to Quick Search's existing results template/logic — reused, not reimplemented)
- When the toggle is on: loading state for the GenSearch call, then the rendered answer (`result`) + a **Sources** list from `retrievedLinks` (url + title), positioned above or alongside the results list
- Optionally surface `questions` (API-suggested follow-ups) as clickable chips that re-trigger a search — nice-to-have, not required for v1
- Error state for the GenSearch call specifically, with a retry action; a failure in the GenSearch layer must **not** block or hide the results layer (they're independent requests) — generic user-facing error message, real failure detail (auth errors, timeouts, malformed responses) logged server-side only
- Persistent disclaimer text below the answer when shown (default i18n: *"AI-generated responses may be inaccurate. Verify important information."*)
- `qmaId` retained client-side (not rendered) for potential future feedback/thumbs-up-down wiring — out of scope for v1, but worth not discarding since the API already returns it

**Testing:**
- Client JS unit tests: toggle on/off behavior, both-requests-in-parallel timing, results-independent-of-answer-failure, loading/answer/error/retry states for the GenSearch layer
- Java servlet tests for `ContentAISupportedSearchServlet` with a mocked HTTP client for the `gensearch` call: success, 401/403, timeout, malformed response
- OSGi config service tests: missing/invalid credentials fail cleanly, no stack trace exposed to the user
- Confirm the results layer continues to work identically to Quick Search v2's when reused here (no regressions from sharing the servlet)

## Rollout & feature-toggle plan

**Two different "toggle" concepts — do not conflate them:**

1. **The component's own AI-answer toggle** (end-user-facing UI switch, described above) — ships with the component, **default on**, purely a display/behavior control for whether the GenSearch layer runs for a given query. Not a release mechanism.
2. **A release feature toggle** (e.g. `FT_GRANITE-<new-ticket>`) — gates whether the GenSearch layer/servlet is available on an AEM environment at all, because it calls a Content AI microservices API that requires per-customer onboarding (a provisioned Content Source + config), not a blanket AEM-layer capability like `FT_GRANITE-56831`.
   - Default **off** at the environment level, created via the standard release-toggles/LaunchDarkly workflow (same operating model as `FT_GRANITE-56831`)
   - Flip default to **on** once `content-sources/gensearch` is confirmed stable/GA (see residual open item above)
   - When this environment-level toggle is off, the component's own UI toggle should not be shown at all (falls back to results-only, no dead-end AI switch that always errors)

**Staged rollout:** internal dogfood first → confirm with the Content AI team that `content-sources/gensearch` is the right/stable target (not still shifting) → opt-in for early customers via the release toggle → GA once the API is stable.

## Open questions

1. Is `content-sources/gensearch` stable/GA enough to build against now, or still pre-GA? (see residual open item above — needs Content AI team confirmation)
2. Exact toggle key/name — to be created via the standard release-toggles workflow once the new ticket exists.
3. Component and resource-type naming ("ContentAI Supported Search" vs a shorter internal name) — to confirm once the ticket is filed.
4. Exact IMS service-token acquisition flow for an AEM-side caller of `content-sources/gensearch` — the Content-AI Services development plan doc names "api-first service-token + user service-token" as the token type but doesn't fully spell out the AEM-side integration steps; confirm with the Content AI team before implementation.

## Relationship to GRANITE-69682

This work is fully additive: it does not touch `search/v2`, `search/v3`, or the Semantic Search toggle GRANITE-69682 ships. It goes in a separate new ticket and separate branch. Semantic Search as a standalone component is explicitly out of scope — dropped per this revision.
