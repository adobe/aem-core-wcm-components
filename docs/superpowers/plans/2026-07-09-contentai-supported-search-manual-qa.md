# GRANITE-70028 — Manual QA Test Plan (ContentAI Supported Search)

**Scope:** ContentAI Supported Search component — backend servlet wiring + frontend UI  
**Ticket:** [GRANITE-70028](https://jira.corp.adobe.com/browse/GRANITE-70028)  
**Branch:** `semantic-gensearch-components`  
**Environment:** Local AEM Cloud SDK — Author `4502`, Publish `4503`  
**Content AI spec:** [Content AI OpenAPI (experimental)](https://developer.adobe.com/experience-cloud/experience-manager-apis/api/experimental/contentai/)  
**Session context:** `docs/superpowers/2026-07-09-contentai-supported-search-session-context.md`

---

## 1. Test strategy

This plan validates the integration in three layers, in order:

| Phase | What | Proves |
|-------|------|--------|
| **A** | Direct Content AI API (`curl`) | API key, host, and content source are valid before touching AEM |
| **B** | AEM servlet proxy (`curl` to `.search.json` / `.gensearch.json`) | OSGi config, `ContentAIClientImpl`, and servlet wiring |
| **C** | Browser E2E (author preview + publish) | HTL, clientlib, JS debounce/toggle/error handling |

For local SDK testing, **hardcode** the API key and base URL in OSGi config (`baseUrlOverride`). On AEM CS, the same values would come from Cloud Manager secrets and env-derived hosts — that path is out of scope for this local run.

---

## 2. Content AI test environment

**Shared E2E bucket (Content AI team, 2026-07-10):**

| Item | Value |
|------|-------|
| **Program / env** | `p158407-e1696476` |
| **Publish host** | `publish-p158407-e1696476.adobeaemcloud.com` |
| **Base URL** | `https://publish-p158407-e1696476.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI` |
| **Content source** | `aem-live` (ACQUISITION, public) |
| **List sources** | `GET {baseUrl}/content-sources` |
| **Search** | `POST {baseUrl}/content-sources/search` |
| **GenSearch** | `POST {baseUrl}/content-sources/gensearch` |
| **Auth** | `X-Api-Key: <client-id>` |

### 2.1 Local SDK OSGi

On **author (4502) and publish (4503)**, set **Core Components Content AI Client**:

| Property | Value |
|----------|-------|
| **API Key** | Your ADC client ID |
| **Base URL Override** | `https://publish-p158407-e1696476.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI` |

The shipped code path (`aemcontentai-expires-20261231`) matches this bucket — no path mismatch.

Do **not** rely on env-derived URL on local SDK — `AEM_PROGRAM_ID` / `AEM_ENV_ID` are absent locally.

### 2.2 Sample queries (aem-live)

| Query | Use for |
|-------|---------|
| `block` | Results list (search) |
| `sitemap` | Results list (search) |
| `What is a block` | GenSearch summary (toggle ON) |

---

## 3. Prerequisites

### 3.1 Build and deploy

From repo root:

```bash
# Bundle + content (author)
mvn clean install -PautoInstallPackage -pl bundles/core,content -am

# Publish instance (if using Maven publish profile)
mvn clean install -PautoInstallPackagePublish -pl bundles/core,content -am
```

Or install via Package Manager:

| Instance | Package Manager |
|----------|-----------------|
| Author | http://localhost:4502/crx/packmgr |
| Publish | http://localhost:4503/crx/packmgr |

Install at minimum:

- `core.wcm.components.core` (bundle — `ContentAIClientImpl`, servlets, Sling Model)
- `core.wcm.components.content` (HTL, clientlib, dialog)

### 3.2 Verify instances

| Instance | URL | Credentials |
|----------|-----|-------------|
| Author | http://localhost:4502 | `admin` / `admin` |
| Publish | http://localhost:4503 | `admin` / `admin` |

### 3.3 OSGi configuration (hardcoded for local QA)

**Author:** http://localhost:4502/system/console/configMgr  
Search: **Core Components Content AI Client**

| Property | Value |
|----------|-------|
| **API Key (X-Api-Key)** | `<PASTE_CLIENT_ID_HERE>` |
| **Base URL Override (dev only)** | `https://author-p160053-e1711614.adobeaemcloud.com/adobe/experimental/contentai-expires-20251231/contentAI` |
| **Default Content Source** | *(optional — component dialog overrides)* |
| **Connection Timeout** | `2000` (default) |
| **Socket Timeout** | `10000` (default) |

Repeat the same config on **publish** (4503) before publish-tier tests.

> **Security:** The key is server-side only (servlet proxy). Never put it in clientlibs, HTL, or browser-accessible config.

### 3.4 Replication (author → publish)

On **author:** http://localhost:4502/etc/replication/agents.publish.html

1. Open **Default Agent (publish)**
2. **Enabled** = checked
3. **URI** = `http://localhost:4503/bin/receive`
4. **Test Connection** → success
5. Save

### 3.5 Discover content source name

List available indexes (Phase A):

```bash
export CONTENTAI_BASE="https://author-p160053-e1711614.adobeaemcloud.com/adobe/experimental/contentai-expires-20251231/contentAI"
export CONTENTAI_KEY="<PASTE_CLIENT_ID_HERE>"

curl -sS -X GET \
  "${CONTENTAI_BASE}/content-sources" \
  -H "X-Api-Key: ${CONTENTAI_KEY}" \
  -H "Accept: application/json" | jq .
```

Record a **public** content source `name` from the response — use it in the component dialog and curl examples below as `<public-source>`.

---

## 4. Phase A — Direct Content AI API validation

Run from your machine (not necessarily from AEM). Confirms key, host, and source before servlet tests.

### A.1 List indexes

| Step | Action | Expected |
|------|--------|----------|
| 1 | `GET ${CONTENTAI_BASE}/content-sources` with `X-Api-Key` | HTTP **200**, JSON list of content sources |
| 2 | Identify a public source | `name` field usable in search/gensearch bodies |

### A.2 Search API

```bash
curl -sS -w "\nHTTP %{http_code}\n" -X POST \
  "${CONTENTAI_BASE}/content-sources/search" \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: ${CONTENTAI_KEY}" \
  -d '{
    "contentSource": {"name": "<public-source>"},
    "query": {
      "type": "composite",
      "operator": "OR",
      "queries": [
        {"type": "vector", "text": "travel", "options": {"numCandidates": 10, "boost": 1}},
        {"type": "fulltext", "text": "travel", "options": {"boost": 1.5}}
      ]
    },
    "queryOptions": {"pagination": {"limit": 10}}
  }' | jq .
```

| Expected | Detail |
|----------|--------|
| HTTP **200** | |
| Body shape | `{ totalResults, results: [{ id, score, data }], cursor? }` |
| `results` | Non-empty for a query matching indexed content |

### A.3 GenSearch API

```bash
curl -sS -w "\nHTTP %{http_code}\n" -X POST \
  "${CONTENTAI_BASE}/content-sources/gensearch" \
  -H "Content-Type: application/json" \
  -H "X-Api-Key: ${CONTENTAI_KEY}" \
  -d '{
    "query": "What travel guides are available?",
    "contentSource": {"name": "<public-source>"}
  }' | jq .
```

| Expected | Detail |
|----------|--------|
| HTTP **200** | |
| Body shape | `{ query, result, hits: [{ id, metadata? }] }` |
| `result` | Non-empty AI summary text |

### A.4 Host tier probe (optional — resolves open item 7a)

Repeat A.2 and A.3 against the **publish** host:

```
https://publish-p160053-e1711614.adobeaemcloud.com/adobe/experimental/contentai-expires-20251231/contentAI
```

| Outcome | Implication |
|---------|-------------|
| Author **200**, publish **403/404** | Local publish instance should also use `baseUrlOverride` pointing at **author** host (or tier logic needs a fix for CS) |
| Both **200** | Current tier-based URL derivation is likely correct for CS publish |

Record results in the sign-off checklist.

---

## 5. Phase B — AEM servlet backend wiring

### 5.1 Test page setup (one-time)

1. **Author** → create page, e.g. `/content/core-components-examples/library/qa-contentai-search`
2. Add **ContentAI Supported Search** component (`core/wcm/components/contentaisearch/v1/contentaisearch`)
3. Dialog:
   - **Content Source:** `<public-source>`
   - **Number of Results:** `10`
   - **Show generative summary by default:** checked
4. Ensure page clientlib **embeds** `core.wcm.components.contentaisearch.v1`  
   (add to site clientlib `embed` array if not already present)
5. **Activate** page to publish

### 5.2 Servlet URL pattern

Servlets bind to the component resource type with selectors:

| Endpoint | Pattern |
|----------|---------|
| Results | `{componentResourcePath}.search.json?q={query}` |
| GenSearch | `{componentResourcePath}.gensearch.json?q={query}` |

Example (adjust path after placing component):

```bash
# Author — replace ... with actual component node path from page CRXDE
export COMP_PATH="/content/core-components-examples/library/qa-contentai-search/jcr:content/root/contentaisearch"

curl -sS -w "\nHTTP %{http_code}\n" \
  "http://localhost:4502${COMP_PATH}.search.json?q=travel" | jq .

curl -sS -w "\nHTTP %{http_code}\n" \
  "http://localhost:4502${COMP_PATH}.gensearch.json?q=travel%20guide" | jq .
```

Repeat on publish (`4503`) after replication.

### 5.3 Backend test matrix

| # | Test | Request | Expected |
|---|------|---------|----------|
| B.1 | Search success | `.search.json?q=travel` | **200**, JSON with `results[]` |
| B.2 | GenSearch success | `.gensearch.json?q=travel` | **200**, JSON with `result` + `hits[]` |
| B.3 | Missing `q` | `.search.json` (no param) | **400** — `Missing required parameter: q` |
| B.4 | Empty `q` | `.search.json?q=` | **400** |
| B.5 | Long query | `.search.json?q=` + 513 chars | **400** — exceeds max length 512 |
| B.6 | Invalid key | Break OSGi key temporarily | **502** — `Content AI request failed` (generic, no upstream body) |
| B.7 | Invalid base URL | Break `baseUrlOverride` temporarily | **502** — same generic error |
| B.8 | No Authorization header | Inspect AEM access log / debug proxy | Only `X-Api-Key` sent upstream, **no** `Authorization` |

**Pass (B):** Servlets return proxied Content AI JSON on success; validation and errors are handled without leaking API key or upstream error bodies to the client.

---

## 6. Phase C — Browser E2E (author + publish)

**Tool:** Chrome DevTools → **Network** tab  
**Filter:** `search.json`, `gensearch.json`

### 6.1 Initial render

| Step | Action | Expected |
|------|--------|----------|
| 1 | Open test page (author preview, then publish) | Search input + "Show AI-generated summary" toggle visible |
| 2 | Toggle default | Checked if dialog "Show generative summary by default" = on |
| 3 | Disclaimer | Default disclaimer visible once summary loads |

### 6.2 Results list (always fetched)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Type `travel`, wait ~300 ms debounce | XHR to `*.search.json?q=travel` |
| 2 | Response | **200**, results rendered as list items |
| 3 | Clear input | Results list cleared, summary hidden |
| 4 | Inspect list item text | Titles escaped (no raw HTML injection from API data) |

### 6.3 Generative summary (toggle ON)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Toggle **ON**, type `travel guide` | Parallel XHR: `.search.json` + `.gensearch.json` |
| 2 | Summary area | AI text in `.cmp-contentaisearch__summary-text` |
| 3 | Sources | `hits[]` rendered under summary; links only for safe URLs (`http(s)://` or relative) |
| 4 | Loading | Loading indicator during results fetch |

### 6.4 Toggle OFF — results only

| Step | Action | Expected |
|------|--------|----------|
| 1 | Uncheck "Show AI-generated summary" | Summary + error panels hidden |
| 2 | Type new query | **Only** `.search.json` request (no `.gensearch.json`) |
| 3 | Results | List still updates |

### 6.5 Toggle change mid-query

| Step | Action | Expected |
|------|--------|----------|
| 1 | Toggle ON, search `marketing` | Both endpoints fire |
| 2 | Toggle OFF (keep input) | New search only; gensearch not called |
| 3 | Toggle ON again | Gensearch fires for current input |

### 6.6 Independent error handling (GenSearch failure)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Set invalid content source in dialog (or break gensearch upstream) | |
| 2 | Toggle ON, search | `.gensearch.json` → error |
| 3 | UI | Error panel + "Try again" button shown |
| 4 | Results | **Still shown** if `.search.json` succeeded — gensearch failure does not wipe results |
| 5 | Click **Try again** | Retries gensearch only |

### 6.7 Results failure (independent of gensearch)

| Step | Action | Expected |
|------|--------|----------|
| 1 | Break search (invalid source affecting both, or break OSGi entirely) | |
| 2 | Search | Results list empty/cleared; gensearch may show its own error independently |

### 6.8 Publish parity

Repeat §6.1–6.7 on **publish** (`4503`). Behavior should match author preview.

---

## 7. Security and abuse checks

| # | Check | How | Expected |
|---|-------|-----|----------|
| S.1 | API key not in browser | DevTools → Sources / Network → response headers & page source | No `X-Api-Key` value client-side |
| S.2 | Generic servlet errors | Invalid key / upstream 403 | Browser sees **502** with generic message only |
| S.3 | URL scheme gating | If test data includes `javascript:` URL in `metadata.url` | Rendered as plain text, not `<a href>` |
| S.4 | Query length cap | Paste 513+ chars in input | No servlet call (client) or **400** (direct curl) |
| S.5 | XSS in titles | API returns `<script>` in title | Escaped in DOM (`textContent` / `_escapeHtml`) |

---

## 8. Component dialog checks (author)

| # | Test | Expected |
|---|------|----------|
| D.1 | Content Source required | Cannot save without value |
| D.2 | Help text | Warns: public index only, no ACL evaluation |
| D.3 | Results size | Respects configured limit in `.search.json` response count |
| D.4 | Custom disclaimer | Dialog text replaces default |
| D.5 | Default toggle off | Component renders with toggle unchecked; no gensearch until enabled |

---

## 9. Out of scope (document, don't fail QA)

| Item | Note |
|------|------|
| AEM CS env-derived base URL (no override) | Requires deployed CS env with `AEM_PROGRAM_ID` / `AEM_ENV_ID` |
| Cloud Manager secret wiring | Deploy-time; local uses hardcoded OSGi |
| Release feature toggle | Not yet gated — track separately |
| SSE `/gensearch/stream` | Component uses non-streaming endpoint |
| Quick Search v3 / GRANITE-69682 | Separate component and test plan |
| Semantic result quality / ranking | Subjective; pass on wiring + non-empty results |

---

## 10. Sign-off checklist

```
Phase A — Direct Content AI API
[ ] GET /content-sources → 200, public source name recorded: _______________
[ ] POST /content-sources/search → 200, results[]
[ ] POST /content-sources/gensearch → 200, result + hits[]
[ ] (Optional) publish-host probe recorded: author ___ / publish ___

Phase B — Servlet wiring
[ ] Package deployed to author (4502) and publish (4503)
[ ] OSGi: API key + baseUrlOverride set on both tiers
[ ] .search.json?q=travel → 200
[ ] .gensearch.json?q=... → 200
[ ] Missing/empty/oversized q → 400
[ ] Bad key/config → 502 (generic)
[ ] Upstream uses X-Api-Key only (no Authorization)

Phase C — Browser E2E
[ ] Clientlib core.wcm.components.contentaisearch.v1 on page
[ ] Test page published to publish
[ ] Debounced search fires .search.json
[ ] Toggle ON → parallel .search.json + .gensearch.json
[ ] Toggle OFF → search only, summary hidden
[ ] Results render; summary + sources + disclaimer when ON
[ ] Gensearch error → retry UI; results preserved
[ ] Same behavior on author preview and publish

Security
[ ] No API key in browser
[ ] Generic 502 on upstream failure
[ ] Unsafe URLs not linked

Dialog
[ ] Content source required + help text visible
[ ] Custom disclaimer + default toggle settings honored
```

---

## 11. Jira comment template

```
Manual QA completed on local Cloud SDK (author 4502 / publish 4503).

Content AI env: author-p160053-e1711614, base path contentai-expires-20251231.
Content source tested: <public-source>.

Verified:
- Direct Content AI search + gensearch APIs (X-Api-Key)
- Servlet proxy (.search.json / .gensearch.json) on author and publish
- UI: debounced query, toggle default ON, results always shown
- Toggle OFF → results only; gensearch skipped
- Independent error/retry for gensearch; results preserved on gensearch failure
- No API key or upstream details exposed to browser
- Query length cap and generic 502 on misconfiguration

Branch: semantic-gensearch-components, packages core.wcm.components.core + .content deployed.
Host tier probe (if run): author=<status>, publish=<status>.
```

---

## 12. Quick reference — endpoints

| Layer | URL |
|-------|-----|
| Content sources | `GET {CONTENTAI_BASE}/content-sources` |
| Search | `POST {CONTENTAI_BASE}/content-sources/search` |
| GenSearch | `POST {CONTENTAI_BASE}/content-sources/gensearch` |
| AEM results | `GET http://localhost:{4502\|4503}{resourcePath}.search.json?q=` |
| AEM gensearch | `GET http://localhost:{4502\|4503}{resourcePath}.gensearch.json?q=` |
| OSGi config | `/system/console/configMgr` → **Core Components Content AI Client** |
| Clientlib category | `core.wcm.components.contentaisearch.v1` |
| Resource type | `core/wcm/components/contentaisearch/v1/contentaisearch` |
