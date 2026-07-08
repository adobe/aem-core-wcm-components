# Design: ContentAI Supported Search Core Component

**Date:** 2026-07-06 (revised 2026-07-07 ×2; 2026-07-08 auth/ACL/bucket revision)
**Status:** Proposed — tracked in [GRANITE-70028](https://jira.corp.adobe.com/browse/GRANITE-70028) (relates to GRANITE-69682), branch `semantic-gensearch-components`, PR [#3056](https://github.com/adobe/aem-core-wcm-components/pull/3056). **Code in PR #3056 predates the 2026-07-08 auth/ACL/bucket revision and needs updating to match (see Decisions locked).**
**Related but out of scope:** `GRANITE-69682` ("Quick Search Core Component: Add AI Search toggle with ?{}? prefix support") — already in review, PR [#3055](https://github.com/adobe/aem-core-wcm-components/pull/3055). That ticket adds an opt-in Semantic Search toggle to the existing Quick Search component; it does not add any new drag-and-drop component. **Semantic Search does not get its own standalone component** — it remains a toggle inside Quick Search, scoped entirely to GRANITE-69682.

## Background

`aem-core-wcm-components` today ships one search-related component: **Quick Search** (`search/v2`, `search/v3`), a fulltext search box backed by the JCR/QueryBuilder-based `SearchResultServlet`, which calls **Elasticsearch directly** via Oak (optionally `?{}?`-prefixed per GRANITE-69682). There is no drag-and-droppable component today that goes through Content AI's own microservices API.

**This design's revision (2026-07-07):** the new component uses **Content AI's API exclusively for everything** — both the results list and the generated answer. It does **not** reuse Quick Search or `SearchResultServlet` in any way; that servlet/Elasticsearch path is Quick Search's alone and stays untouched.

Adobe's internal "Launch Site Search for AEM Cloud Service" initiative (Site Search Wiki) names this as a strategic gap: *"No fully GA ContentAI Site Search component exists today... this initiative closes that gap."* This mirrors the GenSearch half of the Inside Adobe search experience (which itself blends Coveo results with a Content AI-generated answer — our component reproduces the Content AI side of that pattern using Content AI for **both** halves, not Coveo).

## Confirmed, official API: AEM Content AI APIs (`2026.06-experimental`)

Source: the actual OpenAPI spec behind Adobe's public docs page (`developer.adobe.com/.../api/experimental/contentai/`), fetched directly (`api.redocly.com/registry/bundle/adobe-developers/AEM-contentAI/aemcontentai/openapi.yaml?branch=prod`). This is the authoritative, versioned contract — it **supersedes** the earlier informal wiki-draft schema this doc previously cited (that wiki page, "20260605 - GenSearch on Content Sources", described a richer response shape — `retrievedLinks`, `questions`, `qmaId` — that is **not** present in the currently published spec; see "Discrepancy" note below).

**Base URL — environment-derived, NOT hand-configured** (per-AEM-instance, not a global gateway):
```
https://{bucket}.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI
```
where `{bucket}` is the running AEM CS environment's own identifier. **This is derived at runtime from the instance's own AEM CS environment variables — not a free-text config string** (a static string risks drift, wrong-tenant, or wrong-tier targeting). AEM CS exposes these to Java via `System.getenv(...)`:
- `AEM_DOMAIN_PUBLISH` = `publish-p{PID}-e{EID}.adobeaemcloud.com` (and an author equivalent) — the instance's own external host; the cleanest single source for the base URL host.
- `AEM_PROGRAM_ID` (e.g. `125754`), `AEM_ENV_ID` (e.g. `1283020`), `AEM_SERVICE` = `cm-p{PID}-e{EID}` — for assembling the bucket if the host env var isn't preferred.

The client builds the base URL from these, so it always targets the environment actually serving the user. (An OSGi override is allowed only for local dev / non-CS testing where these env vars are absent.)

**Auth — `X-Api-Key` (anonymous, public index), NOT BearerAuth.** Confirmed with the Content AI team (wiki/Slack) + spec `securitySchemes`:
- `ApiKeyAuth` (`X-Api-Key` header): required for anonymous/public access; reaches **public content sources only**. The key is effectively an **Adobe Developer Console client ID** — it identifies the source for abuse handling, it is *not* a user authenticator and is not treated as a high-value secret (PetPlace/BambooHR shipped it in browser JS). This is the correct and only appropriate auth for a public, anonymous end-user site.
- `BearerAuth` (IMS JWT): only for *authenticated* callers reaching *entitled/private* content, and it must carry the right scope (`contentai.api`) + a profile entitled to the bucket **and tier**. It is **explicitly dropped from this component** because: (a) a public site has no logged-in user, so no per-user token exists; (b) a single service/technical-account bearer token used for all anonymous visitors serves them under one privileged identity — over-exposure the Content AI team warns against; (c) Content AI bearer tokens expire ~1h, so a static OSGi token is non-viable anyway. Bearer belongs to a *future authenticated variant*, not this public-site component.

**Key storage:** the `X-Api-Key` (client ID) lives in the OSGi config on AEM CS, fed by a Cloud Manager environment variable / secret. Because our servlet proxies server-side, the key is not exposed in the browser at all — strictly better than the EDS/PetPlace precedent of embedding it in client JS.

**Access note (from the spec's own description):** *"The use of these APIs is subject to co-innovation arrangements with Adobe or licensing once available for public use."* A real dependency beyond a feature toggle — see Rollout section.

### 1. Results list — `POST /content-sources/search` (tag: AI Search API)

Request (`ContentSourceSearchRequest`):
```json
{
  "contentSource": { "name": "content-ai-articles-index", "type": "ACQUISITION" },
  "query": {
    "type": "composite",
    "operator": "OR",
    "queries": [
      { "type": "vector", "text": "What are the benefits of electric cars", "options": { "numCandidates": 3, "boost": 1 } },
      { "type": "fulltext", "text": "recycled", "options": { "lexicalSpaceSelection": { "space": "fulltext" }, "boost": 1.5 } }
    ]
  },
  "queryOptions": { "pagination": { "limit": 10 } }
}
```
`query.type` is a discriminated union: `vector` (semantic), `fulltext` (traditional), `filter` (exact-match), or `composite` (combination — used above for hybrid search). `contentSource.type` defaults to `ACQUISITION` if omitted (other values: `AEM_AUTHOR`, `AEM_PUBLISH`, `CUSTOM`).

Response (`ContentSourceSearchResponse`):
```json
{
  "totalResults": 5,
  "results": [
    { "id": "doc_1", "score": 0.75, "data": { "title": "...", "description": "...", "tags": [...], "metadata": { "reference": "http://...", "rank": 1 } }, "chunks": [] }
  ],
  "cursor": "opaque-cursor-string"
}
```
`data` is arbitrary — whatever was ingested for that document. There's no guaranteed `url`/`title` field; a content source's ingestion pipeline determines what's in `data`/`data.metadata`.

### 2. Generative answer — `POST /content-sources/gensearch` (blocking) / `POST /content-sources/gensearch/stream` (SSE) (tag: Generative Search API)

Request (`ContentSourceQueryRequest`):
```json
{
  "query": "What are the benefits of electric cars?",
  "contentSource": { "name": "example-website" }
}
```
Response (`ContentSourceQueryResult`):
```json
{
  "query": "What are the benefits of electric cars?",
  "result": "Electric cars offer lower operating costs, reduced emissions, and quieter operation compared to traditional vehicles.",
  "hits": [ { "id": "doc_1" }, { "id": "doc_2" } ]
}
```
Streaming variant (`/gensearch/stream`, SSE, `Accept: text/event-stream`) sends `type: START` → multiple `type: STREAMING` chunks (partial `result` text) → `type: END` (final `result` + `hits`) → or `type: ERROR`.

**Discrepancy vs. the earlier wiki-draft citation in this doc's prior revision:** the wiki page ("20260605 - GenSearch on Content Sources") described `retrievedLinks` (url+title), `questions` (follow-ups), and `qmaId` (feedback tracking) in the gensearch response. **None of those fields exist in the currently published OpenAPI spec** — only `query`, `result`, `hits` (each just `id` + optional `metadata`). Build against the published spec; treat the richer wiki shape as a possible future enhancement, not something to design around now. This also means "Sources" in the UI can only reliably show `hits[].id` (and whatever's in `hits[].metadata`, if present) — not a guaranteed clickable URL. Flagged as an open item below.

## Key architecture

| Layer | Endpoint | Backend |
|---|---|---|
| Quick Search (existing, untouched, incl. GRANITE-69682's toggle) | `SearchResultServlet` | Elasticsearch directly via Oak |
| **ContentAI Supported Search — results list** | New servlet → `POST /content-sources/search` | Content AI AI Search API |
| **ContentAI Supported Search — generative summary** | New servlet → `POST /content-sources/gensearch` | Content AI Generative Search API |

Both new-component layers go through Content AI exclusively. There is no Elasticsearch-direct call anywhere in this component, and no reuse of `SearchResultServlet`.

## Access control & ACL — deliberately none at runtime; safety is at ingest/config time

Confirmed with the Content AI team (wiki/Slack): **Content AI indexes have no embedded ACL/authorization** — *"there is nothing similar to ACLs in the content ai index,"* and it cannot evaluate AEM/Oak repository ACLs. Querying permissioned content directly through Content AI would *"basically show them all the content."*

For a **public site this is acceptable and no runtime ACL evaluation is required or meaningful** — there is no authenticated user to evaluate against, and the team's guidance is explicit: *"For fully public sites, only published content should be returned, and ACL evaluation should not be part of the result filtering."*

The safety guarantee is therefore a **content-scope invariant established at ingest/config time, not a query-time filter:**
- The component MUST be pointed only at a **public** Content AI index (`IndexAccess.public = true`), whose acquisition crawled with an **anonymous JCR session** so that only published/public content ever entered the index.
- We do **not** implement AEM-side ACL evaluation in this component (and note that AEM ACLs are JCR `rep:policy` permissions evaluated per authenticated session by Oak — they are not, and cannot be, "stored in OSGi config").
- Reusing this pattern for **authored/managed/permissioned** content (logged-in users) is a *different, out-of-scope* use case that would require routing through the AEM Search API (which post-filters by ACL) or forwarding the user's own IMS token to an entitled index. Not this component.

**Guardrail:** the design treats "public index only" as a hard constraint — documented in the dialog help text, and reinforced by the `X-Api-Key` auth (which by construction can only reach public content sources). If a private index is configured, `X-Api-Key` simply returns nothing/forbidden rather than leaking entitled content.

## Goals

Build one new, independently drag-and-droppable core component:

- **ContentAI Supported Search** — a self-contained component with:
  1. **Generative summary** (top of the component) — from `content-sources/gensearch`. Controlled by a **toggle, default ON**.
  2. **Content source results list** (below the summary) — from `content-sources/search`. **Always shown**, regardless of the toggle.

  - **Toggle ON (default):** generative summary + results list
  - **Toggle OFF:** results list only (summary section hidden; the results-list call still happens)

This ships in a **new ticket and new branch** ([GRANITE-70028](https://jira.corp.adobe.com/browse/GRANITE-70028)), separate from `GRANITE-69682`.

## Non-goals

- Modifying Quick Search v2/v3 or the GRANITE-69682 toggle behavior.
- **A standalone Semantic Search component — dropped from scope.** Semantic search as a capability remains available only via the Quick Search v3 toggle (GRANITE-69682).
- **Reusing `SearchResultServlet` / calling Elasticsearch directly — dropped from this component's scope** (this was in an earlier revision of this doc; removed per 2026-07-07 pivot). Both layers of this component go through Content AI only.
- Building or modifying the Content AI backend/API itself.
- A linked-components pattern (separate input/output components wired by ID) — the component is self-contained.

## Precedent in this codebase: the Embed component's oEmbed client

Checked whether any existing core component already calls an external third-party API — the **Embed component** does (`OEmbedClient` / `OEmbedClientImpl`, used to fetch embed metadata from providers like YouTube/Pinterest). Its shape is the pattern to follow here, not the reference repo's raw-`HttpURLConnection` demo servlets:

- **Outbound HTTP via `org.apache.http.osgi.services.HttpClientBuilderFactory`** (`@Reference`-injected), not raw `HttpURLConnection`. This factory is already provided by the AEM/uber-jar runtime — no new dependency to add. `OEmbedClientImpl` builds a `CloseableHttpClient` from it with configured connect/socket timeouts per call.
- **Separation of concerns:** the servlet (`EmbedUrlProcessorServlet`) is a thin adapter — it only translates the Sling request into a call on an OSGi **service** interface (`UrlProcessor`/`OEmbedClient`) and marshals the result to JSON with Jackson. All the actual HTTP-calling logic lives in the service impl, not the servlet. Our design should follow this: a `ContentAIClient` service interface + impl doing the actual `content-sources/search` and `content-sources/gensearch` calls, with the servlets staying thin.
- **OSGi config via `@ObjectClassDefinition`/`@Designate`** (metatype-based, editable in Felix Console / Cloud Manager OSGi config) — `OEmbedClientImplConfigurationFactory.Config` declares fields like `endpoint()`, `socketTimeout()`, `connectionTimeout()` with `default` values. Ours (`ContentAIConfigService`) should be a **single** `@Designate` config (not `factory = true` — we have one Content AI backend, not N providers like oEmbed does), with fields for base URL/bucket, bearer token, default content source, and timeouts.
- **Testing:** plain JUnit 5 + Mockito, mocking `CloseableHttpClient`/`HttpEntity` directly (`OEmbedClientImplTest`) — no WireMock or embedded HTTP server needed. Matches what was already planned.

## Component: ContentAI Supported Search

**Location:** `core/wcm/components/contentaisearch/v1/contentaisearch` (naming may adjust), `componentGroup=".core-wcm"`, self-contained (own input, own generative-summary panel, own results list, own toggle).

**Java Sling Model:** New `ContentAISupportedSearch` interface + impl — `id`, `contentSource` (name), `contentSourceType` (optional, defaults `ACQUISITION`), `resultsSize`, `genSearchEnabledByDefault` (backs the toggle's default state, `true` unless overridden), `i18nMessages`.

**Backend, following the Embed/oEmbed precedent above:**
- New service interface `ContentAIClient` (`com.adobe.cq.wcm.core.components.services.contentai`, public API package like `services.embed`) with two methods: `search(...)` → `POST /content-sources/search`, `genSearch(...)` → `POST /content-sources/gensearch`. Impl (`ContentAIClientImpl`) uses `HttpClientBuilderFactory` for the actual calls, same as `OEmbedClientImpl`.
- Two thin servlets, both registered via `sling.servlet.resourceTypes` bound to this component (not a fixed `/bin/...` path): one for results (`selector=search`), one for the generative answer (`selector=gensearch`, only invoked when the toggle is on). Each just adapts the Sling request, calls `ContentAIClient`, and writes JSON — no HTTP logic of their own.
- `ContentAIClient` builds a `composite` (vector + fulltext) query by default for the results call, using the component instance's configured `contentSource`.

Client JS fires the results-list request on every query; fires the gensearch request in parallel **only when the toggle is on**. A gensearch failure must not hide/block the results list (independent requests, independent error handling).

**OSGi config:** New `ContentAIConfigService.Config` (single `@Designate`, not a factory — one Content AI backend) — **`apiKey` (the `X-Api-Key` client ID, fed by a Cloud Manager env var/secret)**, default `contentSource` name, connect/socket timeouts (mirroring `OEmbedClientImplConfigurationFactory.Config`). **No bearer-token field.** The base URL/bucket is **not** a config field — it is derived at runtime from the AEM CS environment (`AEM_DOMAIN_PUBLISH` / `AEM_PROGRAM_ID` + `AEM_ENV_ID` via `System.getenv`), with an optional override used only for local/non-CS dev where those env vars are absent. `ContentAIClientImpl` sends the `X-Api-Key` header (not `Authorization: Bearer`).

**Dialog:** search-box placeholder text, results size, `contentSource` name (+ optional `contentSourceType` override), optional disclaimer text override, a property for the toggle's default state, and **help text stating the content source must be a public Content AI index of published content** (see Access control & ACL).

**HTL/JS layout, top to bottom:**
1. Search input
2. Toggle (default checked) — labeled clearly as controlling the AI-generated summary, not the results list
3. When toggle is on: loading state → generative summary (`result`) + a "Sources" section built from `hits[]` (rendering `id`, plus any usable field from `metadata` if present — no guaranteed URL, see open item) + persistent disclaimer (default i18n: *"AI-generated responses may be inaccurate. Verify important information."*)
4. Results list (always shown) — rendered from `content-sources/search`'s `results[]`, using whatever title/description fields are present in each result's `data`
5. Error state for the gensearch call specifically, with retry — independent of the results list, which has its own error state

**Testing:**
- Client JS unit tests: toggle on/off behavior, parallel-request timing when on, results-list independence from gensearch failure, loading/summary/error/retry states
- `ContentAIClientImpl` unit tests, JUnit 5 + Mockito mocking `CloseableHttpClient`/`HttpEntity` directly (matching `OEmbedClientImplTest`'s style): success, 401/403, timeout, malformed response, empty `hits`/`results`, **and that the request carries the `X-Api-Key` header (not `Authorization: Bearer`)**
- **Bucket-derivation unit test:** given `AEM_DOMAIN_PUBLISH` / `AEM_PROGRAM_ID`+`AEM_ENV_ID` present, the base URL is assembled correctly; given the dev-override set, it is honored; given neither, it fails cleanly
- Servlet tests confirming they correctly adapt requests to `ContentAIClient` calls and marshal responses (thin, so thin tests)
- OSGi config tests: missing `apiKey` fails cleanly, no stack trace exposed to the user

## Rollout & feature-toggle plan

**Two different "toggle" concepts:**

1. **The component's own generative-summary toggle** (end-user-facing, default on) — ships with the component, purely controls whether the summary section renders/queries. Not a release mechanism.
2. **A release feature toggle** (e.g. `FT_GRANITE-70028`) — gates whether this entire component (both layers, since both need Content AI provisioning) is available on an AEM environment. Default **off**, standard release-toggles/LaunchDarkly workflow, flipped on once access is confirmed (see below).

**Bigger dependency than a feature toggle:** per the spec's own note, these APIs are *"subject to co-innovation arrangements with Adobe or licensing once available for public use."* This means environment-level access isn't just a toggle flip — it may require an actual commercial/co-innovation arrangement per customer. This should be confirmed with the Content AI team before committing to a GA timeline; it affects the whole component, not just the release toggle.

**Staged rollout:** confirm access/licensing model with the Content AI team → internal dogfood (bucket/domain with confirmed access) → opt-in for co-innovation customers via the release toggle → broader availability once/if the API's access model opens up (the spec itself is explicitly still `-experimental` and time-boxed, `expires-20261231` in its own server URL).

## Decisions locked (2026-07-08)

- **Auth = `X-Api-Key`** (client ID), anonymous, public index only. BearerAuth dropped from this component (deferred to a future authenticated variant).
- **Bucket/base URL = environment-derived** from AEM CS env vars (`AEM_DOMAIN_PUBLISH` / `AEM_PROGRAM_ID`+`AEM_ENV_ID`), not a config string; OSGi override only for local dev.
- **ACL = no runtime evaluation**; safety comes from the "public index of published content" invariant, enforced by ingest config + `X-Api-Key`'s public-only reach, and documented in the dialog.

## Open questions

1. **Publish vs author host/tier:** the spec's server example shows an `author-` host, while the routing model treats `tier` separately. Confirm with the Content AI team whether a **publish** instance calling for **published/public** content should target its own `AEM_DOMAIN_PUBLISH` host, and that an `AEM_PUBLISH`-type public content source is reachable that way. (Derivation mechanism is settled; the exact host/tier to use is the open detail.)
2. **Sources rendering without guaranteed URLs:** `gensearch`'s `hits[]` only has `id` (+ optional `metadata`). Current decision: render `metadata.url`/`metadata.title` as a (scheme-safe) link if present, else the id as unlinked text — no ingestion convention required. Revisit if a `metadata.url` convention is later standardized.
3. **Access/licensing model:** is this experimental endpoint (`aemcontentai-expires-20261231`) buildable-against for a shipping core component, given the co-innovation/licensing note and expiry-dated URL? Needs Content AI team confirmation.
4. Exact toggle key/name for the release feature toggle — via the standard release-toggles workflow.
5. Default query composition for the results list (`composite` vector+fulltext vs. plain `fulltext`) — validate relevance/latency before locking in.

## Relationship to GRANITE-69682

Fully additive: does not touch `search/v2`, `search/v3`, or the Semantic Search toggle GRANITE-69682 ships, and (as of this revision) doesn't reuse any of Quick Search's backend either. Tracked separately as GRANITE-70028, "relates to" GRANITE-69682.
