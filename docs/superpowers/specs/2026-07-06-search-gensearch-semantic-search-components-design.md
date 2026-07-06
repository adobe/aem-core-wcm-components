# Design: Semantic Search & GenSearch Core Components

**Date:** 2026-07-06
**Status:** Proposed — pending new Jira ticket + separate feature branch
**Related but out of scope:** `GRANITE-69682` ("Quick Search Core Component: Add AI Search toggle with ?{}? prefix support") — already in review, PR [#3055](https://github.com/adobe/aem-core-wcm-components/pull/3055). That ticket only adds an opt-in AI-search toggle to the existing `Search` component; it does not add any new drag-and-drop component.

## Background

`aem-core-wcm-components` today ships one search-related component: **Search** (`search/v2`, `search/v3`), a fulltext search box backed by the JCR/QueryBuilder-based `SearchResultServlet`. `GRANITE-69682` adds an "AI Search" toggle to `search/v3` that prepends a `?{}?` prefix to the fulltext query, routing it through Content AI-powered semantic search — but this is a modifier on the existing component, not a new one.

There is currently no dedicated, drag-and-droppable **Semantic Search** or **GenSearch** component in the component browser — authors who want either capability as its own building block (rather than a checkbox on Search) have no OOTB option. Adobe's internal "Launch Site Search for AEM Cloud Service" initiative (wiki: Site Search Wiki) explicitly names this as a strategic gap: *"No fully GA ContentAI Site Search component exists today... this initiative closes that gap."*

A reference implementation exists at `OneAdobe/cais-trial-reference-content` (a standalone AEM demo package, not part of core-wcm-components) showing one working pattern: three linked components (Search Input / AI Answer / Search Results) communicating via a shared JS store, backed by two Sling servlets (`/bin/caid/search`, `/bin/caid/gensearch`) that proxy to Content AI's experimental REST APIs using IMS OAuth credentials. This design borrows ideas from that reference but adapts them to core-wcm-components' own conventions (resourceType-bound servlets, not fixed `/bin/` paths) rather than copying it directly.

Content AI's semantic search and generative search capabilities are already live and used in production (e.g. Inside Adobe search, PetPlace, Leviton, per internal Content AI Execution documentation) — the backend AI capability is not something this project builds; this project builds the AEM-side authoring components and thin proxy layer that expose it.

## Goals

Build two new, independently drag-and-droppable core components:

1. **Semantic Search** — a component whose search is always Content AI-powered semantic search (no toggle).
2. **GenSearch** — a component that shows an AI-generated answer with cited sources for a user's natural-language query.

Both ship in a **new ticket and new branch**, separate from `GRANITE-69682`. The existing `Search` component (v2/v3, including its AI toggle) is untouched.

## Non-goals

- Modifying `Search` v2/v3 or the `GRANITE-69682` toggle behavior.
- Building or modifying the Content AI backend/API itself — it already exists and is in production use elsewhere.
- A linked-components pattern (separate Search Input + Output components wired by ID) — both new components are self-contained, matching the authoring experience of the existing Search component.

## Architecture overview

| Component | Backend mechanism | New servlet? | New OSGi config? |
|---|---|---|---|
| Search (existing) | JCR/QueryBuilder via `SearchResultServlet`; optional `?{}?` prefix (GRANITE-69682) | No (existing) | No |
| **Semantic Search** (new) | Same `SearchResultServlet` endpoint, always `?{}?`-prefixed | No — reuses existing servlet | No |
| **GenSearch** (new) | Proxy to Content AI's `gensearch` REST API | **Yes** — `GenSearchServlet` | **Yes** — `ContentAIConfigService` |

Semantic Search needs no new backend because AEM/Oak already resolves a `?{}?`-prefixed fulltext query as semantic search once `FT_GRANITE-56831` (Foundation Search's master Semantic Search toggle, enabled by default) is active on the environment — this is the same mechanism GRANITE-69682 validated for the Search toggle.

GenSearch is fundamentally different: it returns a generated answer + citations, not JCR search hits, so there is no Oak-level equivalent. It requires an actual server-side call to the (already-live) Content AI `gensearch` API.

## Component 1: Semantic Search

**Location:** `core/wcm/components/semanticsearch/v1/semanticsearch`, `componentGroup=".core-wcm"`.

**Design choice:** Fully independent component — own dialog, HTL, JS, and Sling Model. Not built via `sling:resourceSuperType` inheritance from `search/v3`, so its behavior stays decoupled from any future changes to Search.

**Java Sling Model:** New `SemanticSearch` interface + impl (`com.adobe.cq.wcm.core.components.models` / `.internal.models.v1`), modeled on `Search`'s existing properties: `id`, `relativePath`, `searchTermMinimumLength`, `resultsSize`, `i18nMessages`. No index/integrator-id fields — this scopes to the same site content Search already covers, not a separate Content AI index.

**Dialog:** Same shape as `Search` v2 (search root path, min length, results size), plus a documentation/help-text field noting the component requires Content AI Semantic Search (`FT_GRANITE-56831`) enabled on the environment. No AI-toggle field — this component's whole purpose is semantic search, always on.

**HTL (`semanticsearch.html`):** Same structural pattern as `search.html` (form, input, results listbox, ARIA attributes), minus the toggle checkbox. Distinct i18n strings and ARIA labels (e.g. "Semantic Search") so it's distinguishable from plain fulltext Search for authors and assistive technology. Optional: a small "AI-powered" caption near the input, consistent with how Assets View labels its Semantic mode — nice-to-have, not required for v1.

**Client JS (`semanticsearch.js`):** Adapted from `search/v3`'s proven logic (debounce, keyboard navigation, accessibility, infinite-scroll pagination) as its own standalone file. No toggle wiring — `_updateResults()` always prepends `?{}?` to the `fulltext` parameter.

**Backend:** No new servlet. POSTs to the same existing `SearchResultServlet` endpoint (`{page}.searchresults.json`) that Search already uses.

**Feature toggle:** None needed for this ticket. Relies on `FT_GRANITE-56831`, which is already enabled by default.

## Component 2: GenSearch

**Location:** `core/wcm/components/gensearch/v1/gensearch`, self-contained (own input + own answer/sources display).

**Backend servlet:** `GenSearchServlet`, registered idiomatically via `sling.servlet.resourceTypes = GenSearch.RESOURCE_TYPE_V1` + selector `gensearch` (i.e. `{page}.gensearch.json?q=...`), GET-based like `SearchResultServlet` — not a fixed `/bin/...` path like the reference repo's demo servlets. Because the servlet resolves against the specific dropped component instance's resource, it reads that instance's dialog config (e.g. `integratorId` override) directly via `request.getResource()`, with no need to pass a `componentPath` in the request payload.

The servlet proxies server-side to Content AI's `gensearch` REST API using IMS OAuth credentials — the same live API already powering GenSearch elsewhere (subject to confirming with the Content AI team that the "Inside Adobe" usage maps to this exact endpoint).

**OSGi config:** New `ContentAIConfigService` (global, instance-wide) — domain, IMS client id/secret (via AEM Crypto or Cloud Manager environment variables, consistent with how GRANITE-69682's discussion resolved credential handling), default `integratorId`.

**Dialog:** placeholder text, optional per-instance `integratorId` override (for multi-brand pages), optional disclaimer text override. Default i18n disclaimer: *"AI-generated responses may be inaccurate. Verify important information."* — reusing the standard disclaimer pattern already established across Adobe's other GenAI UI surfaces (e.g. internal QnAView/AI Assistant components).

**HTL/JS:**
- Loading state while awaiting the Content AI response
- Rendered answer text + a **Sources** list (cited links)
- Error state with a **retry** action; generic user-facing error message, real failure detail (auth errors, timeouts, malformed responses) logged server-side only — never surfaced to the browser
- Persistent disclaimer text below the answer

**Testing:**
- Client JS unit tests: loading / answer / error / retry states
- Java servlet tests with a mocked HTTP client for the Content AI call: success, 401/403, timeout, malformed response
- OSGi config service tests: missing/invalid credentials fail cleanly, no stack trace exposed to the user

## Rollout & feature-toggle plan

**Semantic Search:** No new toggle. `FT_GRANITE-56831` already gates the backend capability at the AEM layer and is enabled by default. The component ships directly; the only "gating" is dialog documentation warning authors it needs Content AI Semantic Search enabled on their environment.

**GenSearch:** New toggle required (e.g. `FT_GRANITE-<new-ticket>`), because unlike semantic search this calls an **experimental** Content AI endpoint that requires per-customer onboarding (index/integrator ID setup), not a blanket AEM-layer capability.
- Default **off**, created via the standard release-toggles/LaunchDarkly workflow (same operating model as `FT_GRANITE-56831`)
- Flip default to **on** once Content AI confirms `gensearch` is GA (no longer experimental)
- Toggle gates both servlet registration and component visibility in the component browser, so it can't be dropped where the backend isn't provisioned

**Staged rollout:** internal dogfood first → confirm whether "Inside Adobe" search's existing GenSearch usage maps to this same `gensearch` API (open question for the Content AI team) → opt-in for early customers via the toggle → GA once Content AI's endpoint stabilizes.

## Open questions

1. Does "Inside Adobe" search's current GenSearch usage hit the same `contentai/gensearch` API this design proxies to, or a separate integration? Needs confirming with the Content AI team before claiming backend parity.
2. Exact toggle key/name for GenSearch — to be created via the standard release-toggles workflow once the new ticket exists.
3. Whether Semantic Search should carry a visible "AI-powered" UI cue by default (nice-to-have, decided as optional for v1 above).

## Relationship to GRANITE-69682

This work is fully additive: it does not touch `search/v2`, `search/v3`, or the AI-toggle behavior GRANITE-69682 ships. It goes in a separate new ticket and separate branch.
