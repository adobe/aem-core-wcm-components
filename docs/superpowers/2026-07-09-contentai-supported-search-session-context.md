# ContentAI Supported Search — Session Context & Testing Plan

**Date:** 2026-07-09
**Branch:** `semantic-gensearch-components` (HEAD `debbeff` at time of writing)
**Ticket:** [GRANITE-70028](https://jira.corp.adobe.com/browse/GRANITE-70028) (relates to GRANITE-69682)
**PR:** [adobe/aem-core-wcm-components#3056](https://github.com/adobe/aem-core-wcm-components/pull/3056) (from fork `Apoorv-R98`)
**Design spec:** `docs/superpowers/specs/2026-07-06-search-gensearch-semantic-search-components-design.md`
**Impl plan:** `docs/superpowers/plans/2026-07-07-contentai-supported-search.md`

This document captures (1) what was done this session and why, and (2) what still needs to be tested/confirmed — so the work can be picked up, reviewed, or handed to the Content AI team without re-deriving the history.

---

## 1. What we set out to do

Build UI components for **Search, GenSearch, and Semantic Search** in `aem-core-wcm-components`, informed by the Content AI reference content (`OneAdobe/cais-trial-reference-content`) and the live demo (`main--frescopa--posabogdanpetre.aem.live/stories`).

## 2. How the scope evolved (decisions, in order)

1. **Existing state found:** the repo already has **Quick Search** (`search/v2`, `search/v3`). An in-flight ticket **GRANITE-69682** (PR #3055) adds a Semantic Search *toggle* to `search/v3` via the `?{}?` fulltext prefix (Oak/Elasticsearch handles it downstream — no Content AI service call). That covers "Semantic Search" as a toggle.
2. **Standalone Semantic Search component: dropped.** Semantic search stays as the GRANITE-69682 toggle; no separate component.
3. **New component = one component, "ContentAI Supported Search"** (GRANITE-70028), modeled on the GenSearch half of Inside Adobe search. It shows:
   - a **results list** (always shown), and
   - a **generative AI summary** above it, controlled by a **visitor-facing toggle, default ON** (off → results only).
4. **Both layers go through Content AI's API exclusively** — no reuse of `SearchResultServlet`, no direct Elasticsearch. (An earlier draft reused Quick Search for the results layer; that was dropped.)
5. **Public-site / end-user framing forced an auth/ACL/bucket rethink (2026-07-08–09)** — see §4. This is the most important part of the session.

## 3. Confirmed Content AI API (published OpenAPI spec, `2026.06-experimental`)

Source: `api.redocly.com/registry/bundle/adobe-developers/AEM-contentAI/aemcontentai/openapi.yaml?branch=prod` (behind `developer.adobe.com/.../api/experimental/contentai/`).

- **Base URL:** `https://{bucket}.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI`
- **Results:** `POST /content-sources/search` — body `{contentSource:{name}, query:{type:"composite",operator:"OR",queries:[vector,fulltext]}, queryOptions:{pagination:{limit}}}` → `{totalResults, results:[{id,score,data}], cursor}`
- **Generative:** `POST /content-sources/gensearch` (+ `/stream` SSE) — body `{query, contentSource:{name}}` → `{query, result, hits:[{id, metadata?}]}`
- **Auth (spec `securitySchemes`):** `ApiKeyAuth` (`X-Api-Key`, anonymous, **public content sources only**) **or** `BearerAuth` (IMS JWT, entitled/private).
- **Access caveat (from the spec):** *"subject to co-innovation arrangements with Adobe or licensing once available for public use."* Still `-experimental`, URL is expiry-dated (`expires-20261231`).

## 4. Auth / ACL / bucket — the corrected model (validated against spec + Content AI team wiki/Slack + ops repos)

### Auth → `X-Api-Key` (NOT BearerAuth)
- Public site = anonymous visitors, **no per-user token**. `X-Api-Key` is an Adobe Developer Console **client ID** — it identifies the caller for abuse handling, not a user authenticator (PetPlace/BambooHR shipped it in browser JS).
- **BearerAuth dropped** from this component: no per-user token exists on a public site; a single service/technical-account bearer would serve all anonymous visitors under one privileged identity (over-exposure the Content AI team explicitly warns against); and Content AI bearer tokens expire ~1h, so a static config token is non-viable. A *pure* IMS service token is org-unbound / Adobe-internal only; only a Technical-Account S2S credential authenticates, and even then it's wrong for a public site. **Bearer belongs to a future authenticated variant.**
- **Key storage:** `X-Api-Key` lives in the OSGi config (`ContentAIConfig.apiKey()`, `PASSWORD`-typed). On AEM CS it is supplied via a **Cloud Manager environment variable / secret**, referenced in the OSGi config with AEM's `$[env:VAR]` / `$[secret:VAR]` placeholder syntax (customer secrets mount at `/mnt/osgisecrets/customer-secrets`). Because our servlet proxies server-side, the key is never in the browser — better than the EDS precedent. **No code change is needed for the key source; wiring it is a deploy-time step.**

### ACL → no runtime evaluation (and none needed for a public site)
- Content AI team, verbatim: *"there is nothing similar to ACLs in the content ai index."* Content AI cannot evaluate AEM/Oak ACLs.
- For a fully public site there is no authenticated user, so nothing to evaluate: *"For fully public sites, only published content should be returned, and ACL evaluation should not be part of the result filtering."*
- **Safety is a content-scope invariant at ingest/config time, not a query-time filter:** the component must point only at a **public** Content AI index (`IndexAccess.public = true`) whose acquisition crawled with an **anonymous JCR session**. Enforced by: `X-Api-Key`'s public-only reach + dialog help text. (AEM ACLs are JCR `rep:policy`, per-user, Oak-evaluated — not stored in OSGi config.)
- Permissioned/managed content is a **different, out-of-scope** use case (route via AEM Search API, which post-filters by ACL, or forward the user's IMS token to an entitled index).

### Bucket / base URL → environment-derived at runtime (NOT a config string, NOT `AEM_DOMAIN_PUBLISH`)
Validated against **`aem-k8-base`** and **`skyline-ops`** (read-only):
- **`AEM_DOMAIN_PUBLISH` is the CDN/customer FQDN** (`serviceMetadata.cdnFqdn.publish`), *not* the `{bucket}.adobeaemcloud.com` host Content AI is served on — **must not** be used for the base URL. (This was a real bug caught during the session.)
- Program/env/tier are on AEM pods as env vars sourced from pod annotations: **`AEM_PROGRAM_ID`** (`aemProgramId`), **`AEM_ENV_ID`** (`aemEnvId`), **`AEM_TIER`** (`aemTier`), **`AEM_SERVICE`** = `cm-p{PID}-e{EID}` (`aemService`).
- **Host built as `{tier}-p{PID}-e{EID}.adobeaemcloud.com`** from `AEM_PROGRAM_ID`+`AEM_ENV_ID` (fallback: parse `AEM_SERVICE`), **tier from Sling run modes** (author vs publish; ambiguous → publish). OSGi `baseUrlOverride` only for local/non-CS dev.

## 5. What was built (code, all on the branch)

Backend (`bundles/core`):
- `services/contentai/` (public API): `ContentAIClient`, `ContentAIClientException`, `ContentSourceSearchResult`, `ContentSourceQueryResult`, `package-info`.
- `internal/services/contentai/`: `ContentAIConfig` (OSGi, single `@Designate`), `ContentAIClientImpl` (HTTP via `HttpClientBuilderFactory`, mirrors `OEmbedClientImpl`; `X-Api-Key`; env-derived base URL).
- `models/ContentAISupportedSearch` + `internal/models/v1/ContentAISupportedSearchImpl` (Sling Model, `RESOURCE_TYPE = core/wcm/components/contentaisearch/v1/contentaisearch`).
- `internal/servlets/contentaisearch/`: `AbstractContentAISearchServlet` (shared base) + `ContentAISearchResultsServlet` (selector `search`) + `ContentAIGenSearchServlet` (selector `gensearch`).
- `bundles/core/pom.xml`: added bnd `_dsannotations-options: inherit` (so the `@Reference` on the abstract servlet base is emitted in the DS descriptor — blast-radius verified low-risk).

Content package (`content`): component def + `_cq_dialog` (with public-index help text), `contentaisearch.html`, clientlib (`js/contentaisearch.js`, `css/contentaisearch.less`, `js.txt`/`css.txt`, clientlib nodes).

Client JS behavior: debounced query; always fetch results; fetch gensearch in parallel only when toggle on; independent error/retry (a gensearch failure never hides results); DOM output escaped; cited-source `href` gated by a scheme allow-list (`_isSafeUrl` strips interior control chars — blocks `java\tscript:`); `MAX_QUERY_LENGTH=512` guard.

Delivered via subagent-driven development: 10 tasks, each spec+quality reviewed, plus a whole-branch review (opus) that found and fixed an Important XSS (URL scheme) and added the query cap; then the 2026-07-08/09 auth/bucket revisions.

## 6. Verification status (automated)

- Full `bundles/core` reactor: **1071 tests pass, SpotBugs clean** (`mvn clean test -pl bundles/core -am -DfailIfNoTests=false` → BUILD SUCCESS).
- Content package builds (`mvn clean package -pl content -am` → BUILD SUCCESS; ESLint + stylelint + LESS).
- `ContentAIClientImplTest` (10): search/gensearch success + error; `X-Api-Key` header present and no `Authorization`; missing-key clean failure; base-URL derivation from `AEM_PROGRAM_ID`+`AEM_ENV_ID`, from `AEM_SERVICE` fallback, author-tier prefix, and clean failure when neither env nor override present.
- **Build-gate note:** the real gate is `BUILD SUCCESS` (SpotBugs runs at `process-classes`), not just surefire counts; always use `mvn clean test` (non-clean re-runs can hit a pre-existing SpotBugs caching flake); `-am` is required.

## 7. Architecture decisions — confirmed by Content AI team (2026-07-10)

| Topic | Decision | Implication for Core Components |
|-------|----------|--------------------------------|
| **Publish-tier host** | Content AI query APIs (`/content-sources/search`, `/gensearch`) are available on **both** author and publish tier hosts. | Current `ContentAIClientImpl` tier logic is **correct**: publish instance → `publish-p{PID}-e{EID}`, author → `author-p{PID}-e{EID}`. No need to force author host from publish. |
| **Base URL / experimental path** | Fixed in code: `/adobe/experimental/aemcontentai-expires-20261231/contentAI` | Matches `CONTENT_AI_PATH` constant in `ContentAIClientImpl`. `baseUrlOverride` remains **dev-only** for local SDK / non-standard test envs. |
| **Auth (anonymous public search)** | Same for all endpoints: **`X-Api-Key` required** (indexes, search, gensearch). | No Bearer/IMS for this component variant. Servlet proxy + OSGi `apiKey` / Cloud Manager secret. |
| **Public content source type** | **ACQUISITION** | Component dialog / docs should steer authors to an ACQUISITION index of published public content. |
| **ACL / content scope at query time** | **Not our concern for now** | No runtime ACL filtering in the component; safety via public ACQUISITION index + dialog help text. |
| **Caching / cost** | **Skip for now** | No Dispatcher/CDN or in-AEM cache in v1; revisit later. Existing mitigations: debounce, 512-char cap, server-side proxy. |
| **Outbound connectivity from publish** | **No problem** as long as `X-Api-Key` is configured. | Standard AEM CS publish → Content AI HTTPS; no special routing via author. |
| **GenSearch v1** | **Non-streaming** `POST /content-sources/gensearch` | `/gensearch/stream` (SSE) deferred; revisit later. |

**Local QA note:** shared E2E bucket from Content AI team (2026-07-10): `publish-p158407-e1696476`, content source **`aem-live`**, path `aemcontentai-expires-20261231` (matches code). Local SDK uses `baseUrlOverride` → that publish host.

## 8. What still needs testing / confirmation

### Still open
- **API key entitlements:** key may return 200 on `/indexes` but 401 on `/search` and `/gensearch` until query access is granted for the client ID (observed in local QA).
- **Access/licensing:** co-innovation/licensing for shipping against expiry-dated experimental URL.
- **Release feature toggle** key/name (via release-toggles workflow); gate component, default off.
- **Sources rendering:** `hits[]` metadata shape (`id` vs `metadata.url`/`title`) — UX acceptable as-is?
- **GenSearch streaming:** SSE `/gensearch/stream` — follow-up after v1.
- **Caching / abuse:** explicitly deferred; revisit when product asks.

## 9. Manual / integration test plan (to run on a Content AI-provisioned AEM CS env)

Prerequisites:
1. A **public** Content AI index/content source of published content exists for the environment (created public; acquisition used an anonymous session).
2. `X-Api-Key` (ADC client ID) set via a Cloud Manager secret env var and referenced in the `Core Components Content AI Client` OSGi config `apiKey` (e.g. `$[secret:CONTENTAI_API_KEY]`).
3. Component deployed: `mvn clean install -PautoInstallPackage -pl content -am`.

**Step A — validate the API + auth + host directly:** from the target tier, curl the derived URL:
```
curl -sS -X POST \
  "https://{tier}-p{PID}-e{EID}.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI/content-sources/search" \
  -H "Content-Type: application/json" -H "X-Api-Key: <client-id>" \
  -d '{"contentSource":{"name":"<public-source>"},"query":{"type":"composite","operator":"OR","queries":[{"type":"vector","text":"test","options":{"numCandidates":10,"boost":1}},{"type":"fulltext","text":"test","options":{"boost":1.5}}]},"queryOptions":{"pagination":{"limit":10}}}'
```
Repeat for `/content-sources/gensearch` with `{"query":"test","contentSource":{"name":"<public-acquisition-source>"}}`. Per §7, both author and publish hosts should work; confirm 200 with a valid `X-Api-Key` and an **ACQUISITION** content source.

**Step B — component E2E (author preview + published page):**
1. Drop "ContentAI Supported Search", set a valid public `contentSource`.
2. Type a query: confirm results list appears; with toggle ON, AI summary + sources appear above; disclaimer shown.
3. Toggle OFF: summary hidden, results still update.
4. Break the config (invalid key / unreachable host): summary shows error+retry; **results behavior is independent**; no key/token or upstream detail leaks to the browser (generic 4xx/5xx only).
5. Verify the servlet request uses `X-Api-Key` and no `Authorization` header (network tab / access logs).

## 10. Key references
- Design spec / impl plan: see header.
- Reference implementation: `OneAdobe/cais-trial-reference-content`; live demo `main--frescopa--posabogdanpetre.aem.live/stories`.
- Content AI OpenAPI: `developer.adobe.com/experience-cloud/experience-manager-apis/api/experimental/contentai/`.
- Ops repos consulted (read-only, not modified): `aem-k8-base` (env var / CDN FQDN facts), `skyline-ops` (`configsets/tenant/content-restore-job-template.yaml.mustache` — the `AEM_PROGRAM_ID`/`AEM_ENV_ID`/`AEM_TIER`/`AEM_SERVICE` annotation-sourced env vars).
- Precedent in-repo for external-API calls: Embed component `OEmbedClient`/`OEmbedClientImpl`.
