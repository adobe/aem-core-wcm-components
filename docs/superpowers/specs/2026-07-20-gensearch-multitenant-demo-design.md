# Gensearch Multi-Tenant Demo: Cloud Manager Redirect + Auth Bridge

**Date:** 2026-07-20
**Status:** Draft — pending user review, CAIS Onboarding team confirmation, and hosting-model decision (see Open Questions)

## 1. Problem

Cloud Manager's "Content AI Sources" tab (environment detail page) shows a search icon next to each configured content source (e.g. "wknd"). Today, clicking it opens an in-app modal search dialog inside Cloud Manager itself — no redirect, no branded page.

The ask: clicking that icon should instead redirect the user to a subdomain-hosted, branded gensearch demo page for that customer/org — functionally identical to the existing WKND `gensearch-demo` page built this session (search input, AI-generated answer, source citations, results list), but reskinned per org and reachable at an org-derived subdomain (e.g. `acme.gensearch-demo.adobe.com`).

## 2. Goals

- Clicking the search icon on a given org's Content AI Sources row navigates to a subdomain demo page for that org, with working search/gensearch against that org's content source.
- The demo page is gated: only reachable by someone who came through an authenticated Cloud Manager session with legitimate access to that environment — not a public, anonymous, indefinitely-linkable page.
- Visual branding (logo, colors, title) differs per org; underlying component functionality is identical to WKND's.
- The auth mechanism reuses existing, already-reviewed Adobe infrastructure and patterns wherever one exists, rather than inventing new identity/session machinery.

## 3. Non-goals

- Building a new content-authoring workflow for demo pages (onboarding/branding setup can be manual for v1; automation is a later concern).
- Solving generative-search streaming/time-to-first-word (separate, already-scoped work on the core component itself).
- Making the demo page's own Content-AI-API call use anything other than the existing anonymous `x-api-key` mechanism (see §6) — no new Content AI auth surface is introduced by this feature.

## 4. Prior art considered

- **CAIS Onboarding App** (Adobe App Builder app, `github.com/OneAdobe/cais-demobox-app` + `cais-trial-reference-content`, wiki: *Content AI demo page: handover notes*): already onboards a site to Content AI, creates an AEM demo page using the same component family (Search Input / AI Answer / Search Results), adds users to IMS groups, assigns AEM permissions, and its `/check-auth` action validates a user's IMS profile + group membership before issuing a bearer token. Closest existing system to what we're building, but scoped to an internal AEM Sites Trial org, not a public per-customer subdomain, and its own docs show no subdomain pattern or per-customer isolation model.
  **Decided:** not extending it. We build our own, independent system, using CAIS's architecture as a reference for shape (its onboard/add-users/check-auth/deboard action split, its IMS-group-based gating, its "Security Best Practices" checklist) rather than as shared infrastructure or a dependency on that team.
- **ExCat/Edge Delivery Services demos**: a different, unrelated tool, but it does use a genuine per-customer subdomain pattern (`main--xcat-<customer>--<user>.aem.live`) — evidence this general shape of URL is a known, workable pattern at Adobe, just not one built for Content AI specifically.
- **"Super User"**: investigated and resolved as **not applicable**. It's a real IMS term, but scoped to two internal-Adobe-engineer-only access patterns (IMSS admin tier, restricted to Identity Engineering; and AEM Launchpad's allow-listed-client-ID + "super org" pattern for engineers reading customer CMK configs). Neither grants customer/org-facing elevated access. The *pattern* (allow-listed client + specific scope + membership check) is a legitimate style, but not literally reusable here.

## 5. Architecture

### 5.1 Confirmed facts this design is built on

- Cloud Manager's frontend (IMS client `ssp_ui`, a React/Redux SPA) already calls Content AI's publish-side endpoints **directly from the browser**, cross-origin, with a live Bearer IMS user token attached client-side — confirmed both from captured request headers (`Origin`, `Sec-Fetch-Site: cross-site`, etc., which only ever appear on real browser-originated cross-origin fetches, never server-to-server calls) and from a first-party Slack incident where this exact call pattern hit a CORS failure that was fixed by allow-listing `experience.adobe.com`, not by introducing a proxy. This is a named, documented architecture pattern ("Cloud UI requests Content Provider directly"), not an accident.
- That token's scope includes `contentai.api`, `additional_info.projectedProductContext`, and `additional_info.roles` — i.e. CM's frontend, at the moment the user is on that page, already holds a token carrying (a) which AEM environments/buckets the user is entitled to and (b) their FI-code role for that environment (`dx_aem_b_env_user` / `dx_aem_b_administrator`).
- Content AI's documented authorization model (canonical "Authorization" wiki page) has exactly two supported access options: **(A)** `Authorization: Bearer <IMS_USER_TOKEN>`, gated by FI code, per-user audited; **(B)** anonymous `x-api-key`, for public content sources only, gated by an Allowed-Origins allowlist instead of FI codes. There is no third, finalized "service token" option for gensearch — S2S/service-token support for gensearch is explicitly documented (as recently as mid-2026) as an interim workaround, not a stable path.
- Content AI credentials/access are scoped per customer environment ("bucket"), not shareable across orgs — reinforced by a documented 20-technical-accounts-per-environment cap.
- Custom AEM Cloud Service code should not attempt to implement its own AEM/JCR identity or session mechanism — Adobe documentation explicitly discourages a custom `TokenProvider`/JAAS integration ("not advised and won't be supported by Adobe"). Application-level authorization (a servlet checking a signed token/cookie without making the visitor a real AEM user) is unaffected by this — it's exactly what our existing `.search.json`/`.gensearch.json` servlets already do today.
- AEM Cloud Service's CDN (Fastly) caches full URLs including query strings by default if the response is cacheable; Dispatcher does not cache query-string GETs at all. A token in a URL query string is a real leak risk at the CDN layer specifically, with documented precedent (an internal ticket titled "Possible data leak when using the dispatcher model for caching private content"), and a known, standard mitigation (`Cache-Control: private, no-store` + a dispatcher cache-deny rule for that path).

### 5.2 Flow

1. **User is on Cloud Manager's Content AI Sources tab**, already holding a valid Bearer IMS user token (scope includes `contentai.api`, `projectedProductContext`, `roles`) via CM's existing frontend session.
2. **User clicks the search icon** for an org's content source row. CM's frontend makes one additional direct browser call (same pattern as its existing direct-to-Content-AI calls) to a **new minting endpoint**, attaching the same Bearer token, plus the env ID / program ID / content source name already in context on that page. **CM has no knowledge of, and never handles, the demo page's `x-api-key`** - that credential is entirely a destination-side onboarding concern (§5.2 step 6), configured independently of CM and never brokered through it. CM's only ever contribution to auth is the Bearer token it already holds; the choice of which Content AI auth option the destination ultimately uses is made downstream, by the minting endpoint and destination page, not by CM.
3. **The minting endpoint** validates the incoming Bearer token and decodes `projectedProductContext` (does this user have entitlement to this specific environment/bucket?) and `roles` (do they have `dx_aem_b_env_user` or `dx_aem_b_administrator`?). *Validation mechanism is an implementation-level choice not yet pinned down*: either local signature verification against IMS's published signing keys (faster, no extra round trip), or the same live IMS Profile API + User Management API calls CAIS Onboarding's own `check-auth` action uses (slower, but proven and revocation-aware). Whichever is chosen, if valid the endpoint mints a **short-lived (60–120s), single-use, bucket-scoped token**, signed with our own secret (HMAC/JWT) — unrelated to AEM identity, IMS, or any AEM/JCR session.
4. **CM redirects the browser** to `https://<org>.gensearch-demo.adobe.com/?token=...`.
5. **The destination page's own Sling servlet** (ordinary application code, not an AEM identity/session integration) verifies the token's signature and expiry, establishes its own lightweight application-level session (a cookie unrelated to AEM login), and discards the token. The redemption response is served with `Cache-Control: private, no-store`, with a corresponding dispatcher cache-deny rule for that path, and the client strips the token from the URL bar immediately after redemption.
6. **The demo page itself** — same gensearch component family as WKND — calls Content AI's search/gensearch endpoints using whichever of Content AI's two documented options matches that org's content source, decided at redirect time rather than assumed:
   - **Public source → anonymous `x-api-key`** (Option B), exactly like the existing WKND setup: a per-org API key configured at onboarding time. (Configuring this the same way `ContentAIClientImpl` already does — via OSGi config, `defaultContentSource`/`baseUrlOverride` — describes the shared-AEM-environment-with-a-page-per-org hosting variant specifically; a separate-environment-per-org variant would configure this per-environment instead of per-page. Hosting model is still an open question, §9.)
   - **Private source → forwarded user Bearer** (Option A): since Cloud Manager already knows each content source's public/private status (it lists them on the same Content AI Sources tab the redirect originates from), the minting endpoint includes that status as a claim in the handoff token. For a private source, the destination page can't rely on a one-time click-time check — every subsequent search/gensearch call needs a still-valid, FI-code-entitled IMS user token attached. Rather than inventing a session-refresh mechanism, the minting endpoint forwards the original IMS user token itself (or a narrowly-scoped exchange token representing the same user, valid only for Content AI calls) alongside the handoff token, and the demo page attaches it directly to its own Content AI calls for exactly as long as it remains valid - no refresh, no extension. This matches the "demo" nature of the flow: once the original token expires, private-source search stops working until the user clicks through from Cloud Manager again for a fresh one. This forwarded credential gets the same handling as the handoff token itself (§7): never logged, never cached, HTTPS only, discarded rather than persisted server-side.
     **Confirmed real code change required**: `ContentAIClientImpl` (`bundles/core/.../internal/services/contentai/ContentAIClientImpl.java`) today only ever sends `X-Api-Key` - both its request-building methods hardcode that header, and one carries an explicit comment "Anonymous, public-index access uses X-Api-Key ... never a bearer token." There is no Bearer-token code path in this component at all currently. Supporting private sources means adding one (a new method or branch that sets `Authorization: Bearer <token>` instead of `X-Api-Key`, using whichever credential step 6 forwarded) - this is `aem-core-wcm-components` work, not an onboarding/config difference, and is separate from (additional to) the hosting-model code change discussed in §11.

   This is deliberately decoupled from the viewing-gate in step 3–5: "who can reach the page" and "how the page talks to Content AI" are two independent concerns. The public-source path reuses a mechanism already proven stable all session and sidesteps gensearch's unfinished S2S story entirely; the private-source path is new surface area this design didn't need before v1's public-only assumption - it means the destination page's backend must forward a live IMS-class credential, not just validate an opaque one-time token, which needs its own review before implementation.

### 5.3 Why this design, over the alternatives considered

- **Vs. a full IMS SSO redirect**: fewer hops, no need to register the destination as its own IMS OAuth client per org, and reuses a token CM's frontend already holds rather than forcing a fresh login.
- **Vs. fully extending the CAIS Onboarding App as-is**: smaller net-new build for *this* feature specifically, without inheriting the trial-scoped assumptions and undocumented isolation/rate-limiting posture of that system (decided against, §4).
- **Vs. attempting a custom AEM identity/session integration**: avoided entirely — this design never asks AEM to treat the visitor as an authenticated JCR user, which is the specific thing Adobe's own guidance discourages.

## 6. Content AI API usage (recap)

Two independent auth concerns. The second now branches on the content source's public/private status (§5.2 step 6), using whichever of Content AI's two documented options applies:

| Concern | Mechanism | Reuses |
|---|---|---|
| Who can reach the destination demo page | Our own short-lived signed token (§5.2 steps 3–5) | Nothing Content-AI-specific — pure application logic |
| How the demo page's backend calls Content AI (public source) | Anonymous `x-api-key` (Content AI's documented Option B) | The exact mechanism WKND's demo has used successfully all session |
| How the demo page's backend calls Content AI (private source) | Forwarded user Bearer token (Content AI's documented Option A) | The FI-code-gated path Content AI already requires for non-public sources - not previously used by this feature, new surface area |

## 7. Security considerations

- Token in the redirect URL is short-lived, single-use, and bucket-scoped; the redemption response is never CDN-cacheable (`private, no-store` + dispatcher deny rule) and the token is stripped from the URL immediately after use.
- The destination servlet's own session cookie is self-contained/signature-verified (not a server-side session store), which also sidesteps any multi-pod/sticky-session concern on AEM Cloud Service's Publish tier — no documented guidance was found on that specifically, so avoiding the question entirely is the safer choice.
- No new AEM/JCR identity mechanism is introduced anywhere in this design — the one thing Adobe's own documentation explicitly discourages for customer/solution code.
- **Private-source case is materially more sensitive than public-source**: it requires forwarding a live, user-scoped IMS credential to the destination page for the duration of its validity, not just a one-time verification. It gets the exact same handling as the handoff token (never logged, never cached, HTTPS only, discarded not persisted) but is a bigger blast radius if mishandled - this path needs its own explicit security review before implementation, separate from the already-reasoned-through public-source path.

## 8. Components to build

1. **CM frontend change**: one additional call from the existing Content AI Sources tab, on click, to the new minting endpoint; redirect on response. (Confirmed: CM's frontend architecture already calls external APIs directly from the browser this way — no CM backend change needed.)
2. **Minting endpoint** (new service): validates the incoming Bearer token, decodes `projectedProductContext`/`roles`, checks the target content source's public/private status, mints the short-lived signed handoff token (plus, for private sources, forwards a scoped user credential alongside it - §5.2 step 6, §7).
3. **Destination demo page + servlet** (per org, hosting model TBD — see §9): redemption/session logic, plus the existing gensearch component family (reused from `aem-core-wcm-components`, WKND-style), configured per org and able to call Content AI via either auth path depending on that org's content source status.
4. **`ContentAIClientImpl` Bearer-token support** (`aem-core-wcm-components` code change, confirmed necessary in §5.2 step 6): today this component only ever sends `X-Api-Key`; add a code path to call Content AI with `Authorization: Bearer <token>` instead, for the private-source case. Independent of, and in addition to, whatever code change the hosting-model decision (§11) requires.
5. **Onboarding step** (manual for v1): create the org's demo page, configure its content source (and API key, for public sources), set up the subdomain + branding.

## 9. Open questions / dependencies

- **Hosting model**: one shared AEM environment hosting many org pages (matches the CAIS Onboarding pattern, cheaper) vs. a separate AEM environment per org (heavier, more isolated). Still unresolved - see the dedicated discussion below, which surfaced a concrete new constraint bearing on this decision.
- **Two Slack threads referenced during research** (`cq-dev.slack.com/archives/C09F4B1FMMF/...` and `.../C09N5KS9JR0/...`) were never retrieved — that workspace isn't indexed by our internal search tooling and direct fetch hit Slack's login wall. If they contain material context, they still need to be pulled manually and folded in.
- **Private-source auth path** (§5.2 step 6, §6, §7) is new design surface not yet implementation-planned in detail - needs its own review pass once the hosting model is settled, since the forwarding mechanism's exact shape (raw IMS token vs. a narrower exchange token) may depend on it.

### Resolved since first draft

- ~~CAIS Onboarding team dependency~~ — resolved: not extending it, using it only as an architectural reference (§4).
- ~~Public content source requirement~~ — resolved: both public and private sources are supported, branching per-source at redirect time (§5.2 step 6).

## 11. Hosting model — decision in progress

**New constraint found while working through this**: `ContentAIClientImpl` (the core component's Content AI client) is a **singleton OSGi component** (`@Component(service = ContentAIClient.class)`, `@Designate(ocd = ContentAIConfig.class)`, no factory configuration) — its `apiKey`/`baseUrlOverride`/`defaultContentSource` are configured **once per AEM environment**, not per page or per resource. This is a real constraint on the hosting decision, not just a cost/ops trade-off:

- **Shared AEM environment, page per org** (matches CAIS Onboarding's own pattern, cheaper): every org's demo page would live on the *same* environment, but `ContentAIClientImpl`'s API key is instance-wide - as it stands today, every page sharing that instance would use the *same* Content AI credential/bucket, which is wrong for multi-org hosting. Making this option viable requires either:
  - Changing how the api key/base URL/content source are resolved - from a singleton OSGi config to something resource/context-aware (e.g. Sling Context-Aware Configuration scoped per page, or reading it as an authored `@ValueMapValue` property the way `contentSources` already works on `ContentAISupportedSearchImpl`, rather than through `ContentAIClientImpl`'s OSGi config) - a real, scoped code change in `aem-core-wcm-components`, not just an ops/onboarding step.
- **Separate AEM environment per org** (heavier, more isolated): sidesteps this constraint entirely - `ContentAIClientImpl`'s existing singleton-per-environment model already works as-is, since each org's environment only ever has one org's credential in it. No core-component code change needed, at the cost of a full AEM Cloud Service environment per org.

This changes the trade-off from "cost vs. isolation" to "cost vs. isolation vs. a real (if bounded) code change" - worth deciding with that fully in view rather than the cost/isolation framing alone.

## 10. References

- Content AI "Authorization" wiki (`wiki.corp.adobe.com/spaces/ContentAI/pages/3812165945`) — FI codes, Bearer vs anonymous access options.
- "Authorization in the Content AI project" security review notes (`wiki.corp.adobe.com/spaces/WEM/pages/3475525949`) — both-tokens recommendation, `aem.content-ai.` scope, hashed-token caching.
- CAIS Onboarding App handover notes (`wiki.corp.adobe.com/spaces/ContentAI/pages/3676956006`).
- "Site Advisory Agent - Architecture" (`wiki.corp.adobe.com/spaces/WEM/pages/3647582989`) — S2S-as-interim-workaround statement for gensearch.
- "20260605 - GenSearch on Content Sources" (`wiki.corp.adobe.com/spaces/ContentAI/pages/3914826774`).
- "20251110 - Provision Content AI for all AEM CS customers in CloudManager" (`wiki.corp.adobe.com/spaces/ContentAI/pages/3666506396`) — per-environment credential provisioning, 20-technical-account cap.
- Content AI prod UI/API rollout issues, `#team-content-ai` Slack thread (April 2026) — CORS fix confirming direct browser-to-API calls from `experience.adobe.com`.
- "[cloud] Communication with Content Provider" (`wiki.corp.adobe.com/spaces/screens/pages/2274893427`) — named architecture pattern for direct Cloud-UI-to-Content-Provider calls.
- "Caching Behavior" (`wiki.corp.adobe.com/spaces/WEM/pages/2032223864`) and Experience League "Caching in AEM as a Cloud Service" — CDN/Dispatcher caching behavior and mitigation.
- IM-1694 "IMSS for Super Users" and AEM Launchpad Security wiki — "Super User" resolution.
