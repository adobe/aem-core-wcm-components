# Content AI: In-Console Cloud Manager Sidebar Experience

**Date:** 2026-07-24
**Status:** Draft — pending user review
**Relationship to prior work:** Independent alternative to the redirect-to-subdomain design (`docs/superpowers/specs/2026-07-20-gensearch-multitenant-demo-design.md`). That spec is unmodified by this document. This is a candidate replacement, not a merge — both approaches solve the same underlying ask ("let someone browse a content source's live search experience") with different trade-offs, and are documented separately so each can be evaluated on its own.

## 1. Problem

Cloud Manager's "Content AI Sources" list currently lives buried inside a specific environment's detail page, as a tab (`General | Configuration | Restore Content | Advanced network configuration | Content AI Sources`). Reaching it requires navigating into a specific environment first, and today its only per-source action is a search icon that opens an in-app modal — not a real search/gensearch experience.

The idea: promote "Content AI" to a first-class item in Cloud Manager's own left nav, and let a user expand a selected content source in place to see the same gensearch experience (search input, AI-generated answer with citations, results list) already built for the WKND demo — without ever leaving the authenticated Cloud Manager session.

## 2. Goals

- "Content AI" becomes a top-level Cloud Manager nav item (under the existing "Services" grouping), scoped to whichever environment is currently selected.
- Selecting a content source expands the WKND-style search experience in place, with the URL updating to reflect the selection (deep-linkable, back/forward-safe, refresh-safe) — without a full page reload.
- The panel calls Content AI directly using the session's own existing Bearer token — no new auth bridge, no minting endpoint, no subdomain.
- Per-org visual branding (colors, font, logo) is possible without importing a customer's raw CSS/HTML.

## 3. Non-goals

- No live CSS/HTML scraping or import at request time — confirmed as a real security risk (Section 6), not just a theoretical concern. Only a pre-extracted, sanitized token set is used.
- No changes to the existing redirect/subdomain spec (2026-07-20) — independent, not a merge or supersession at the document level.
- No change to the credential model established in that other spec's research — this reuses the session's own Bearer token, uniformly, for every content source; `x-api-key` is not reintroduced here either.
- No aggregation across environments — this stays per-environment scoped, matching today's model (Section 5.1).

## 4. Prior art / research this design is built on

- **Cloud Manager's frontend already calls Content AI directly from the browser** with the session's own Bearer token (confirmed in the 2026-07-20 spec's research) — this design extends that same established pattern to a new panel, rather than introducing a new one.
- **Cloud Manager's left nav is a closed, CM-team-owned surface.** Sourced from the "Cloud Manager UI Next" design doc and the "Extensibility" wiki space (`wiki.corp.adobe.com/spaces/WEM`, `/spaces/engage`): CM's nav is built on the "Unified Shell Side Navigation" primitive (`sidenav.ts`/`ShellSideNav.js` in `git.corp.adobe.com/exc/unified-shell`, enabled via a one-time PR — e.g. `unified-shell/pull/7685`), and once enabled, "menu items are in control of the application" — i.e., fully owned and rendered by Cloud Manager's own team from their own app code. No extension point exists for another team to inject an item into it. **This claim hasn't been directly confirmed with a Cloud Manager engineer** — it's inferred from documentation about the underlying nav primitive, not a firsthand statement from that team. Since the entire rest of this design (Sections 5–8) is contingent on this being true, that confirmation should happen before this spec is used to justify choosing this design over the 2026-07-20 redirect alternative.
- **The AEM Unified Shell's own top-level nav (Home/Sites/Assets/etc.) is a separate, similarly closed surface** — adding a new top-level item there requires a PR into the Unified Shell team's own repo (`git.corp.adobe.com/exc/unified-shell`) and their sign-off; it's explicitly not self-service. (Not the surface this design targets, per Section 8's decision, but ruled out as an easier alternative during research.)
- **Adobe Developer Console / App Builder "Extensions" (UIX SDK) exist but don't solve this.** They register iframe'd panels into specific pre-defined extension points (e.g. inside Extension Manager, CF Admin, Universal Editor) — not a genuine new top-level shell or CM nav entry. Confirmed even the existing "Extension Manager" item itself required custom engineering plus a Unified Shell team PR (`unified-shell/pull/9026`), not self-service registration.
- **No Adobe-internal "brand-kit-from-a-live-URL" service exists today.** Adobe Express's own Brand Kit feature extracts from an uploaded file, not a live URL. The closest internal precedent — Adobe Express's own HTML Design Surface threat-model documentation — explicitly flags that its HTML importer "does not fully sanitize all markup," a live, named internal risk of importing external HTML/CSS, not a hypothetical one.

## 5. Architecture

### 5.1 Where it lives

Cloud Manager's own React/Redux SPA. A new top-level "Content AI" item is added under the left nav's existing "Services" grouping, scoped to whichever environment is currently selected — the same environment-context threading CM already does for "Environments," "Domain Settings," etc.

### 5.2 Migration, not duplication

The existing "Content AI Sources" tab on the environment detail page is removed once the new nav item ships. One source of truth, no parallel UI to maintain.

### 5.3 Route structure and in-place expansion

- List view: a route like `.../environment/<envId>/content-ai`.
- Selecting a source updates the route client-side (e.g. `.../content-ai/wknd`) via the SPA's existing router — no full page reload.
- The search panel expands in place (list stays mounted, panel renders below/beside it) rather than navigating to a visually distinct page or opening a modal.
- This gets both properties at once: the smooth, non-jarring feel of a pure in-place expansion, *and* a real URL underneath it — deep-linkable, browser back/forward-safe (back collapses the panel, forward re-expands it), and refresh-safe (reloading re-opens the same source's panel instead of dropping to the bare list).

### 5.4 What "reuse the WKND-style experience" means here

The panel reuses the *design and behavior* of the existing gensearch component family (search input → AI-generated answer with source citations → always-visible results list, same toggle behavior) — not the literal `aem-core-wcm-components` HTL/clientlib code. Cloud Manager's frontend is a separate React/Redux application, a completely different rendering stack from AEM's own Sling/HTL components. This is a **new React implementation in Cloud Manager's own codebase** that mirrors the WKND page's UX as a reference, not a code-level reuse.

### 5.5 Auth

The panel calls Content AI's `/content-sources/search` and `/content-sources/gensearch` endpoints directly from the browser, using the session's own existing Bearer token — the same token CM's frontend already holds and already uses for the sources list itself today. No handoff token, no minting endpoint, no subdomain: nothing here ever crosses an origin boundary, so none of the bridging machinery the redirect design (2026-07-20) needed applies. This is the core structural difference between the two designs, not an oversight relative to that one.

**Token-scope trade-off, stated explicitly**: this reuse puts the full-scope session Bearer token within reach of a brand-new, less battle-tested React surface that also renders externally-influenced data (the branding tokens from Section 6). If the CSS/logo sanitization in Section 6 has a gap, the practical blast radius is session-token theft, not just a cosmetic bug — something a narrower, panel-scoped token (e.g., a read-only Content-AI-only scope, mirroring the exchange-token approach the 2026-07-20 spec uses for a different reason) would have prevented even if sanitization failed elsewhere. Reusing the full token is still the recommended default here, since it's what makes this design simpler than the redirect alternative in the first place — but that simplicity has this specific cost, and it should be weighed with Section 6's sanitization treated as a hard requirement, not a nice-to-have, given what it's now protecting.

## 6. Branding

- **No live-URL CSS/HTML scraping or import.** Confirmed as a real, named risk (Section 4) — not import-and-hope.
- **Safe pattern**: a server-side process extracts a **sanitized brand token set** only — primary/accent hex color values, a font-family name (checked against a known font allowlist, falling back to Cloud Manager's own default font on no match), and one validated logo image URL (confirmed to actually be an image, not arbitrary content). Doing this server-side also sidesteps the CORS restrictions a client-side fetch of an arbitrary customer domain would hit.
- These tokens are applied through Cloud Manager's **own** CSS custom properties/theming system — the panel still renders with CM's own components, layout, and markup; only color/font/logo values change per org. No customer's raw markup or stylesheet is ever embedded.
- **Hardening requirements, not deferred details** — each closes a specific gap the pattern above doesn't fully cover on its own, so these are hard requirements for whoever builds the pipeline (Section 9), not optional polish:
  - *Color values*: validate against a strict, anchored format (`^#[0-9a-fA-F]{6}$`, nothing else permitted) before a value is ever written into a CSS custom property. An unvalidated "color" string is a CSS-injection vector (e.g. a value that closes the property early and injects `url()`/`expression()`/additional rules), not just a cosmetic risk.
  - *Font-family name*: matched only against the known allowlist (already stated above) — this alone prevents injection through this field, since no other value is ever set from it.
  - *Logo image*: validate by actually decoding the fetched bytes as an image, not by trusting the response's `Content-Type` header (servers can lie about it). **Reject or fully sanitize SVG** specifically — SVG is a valid image format that can carry `<script>`/`onload`/`foreignObject` payloads, so accepting it as-is risks stored XSS if it's ever rendered as inline markup rather than a plain `<img src>`. Render the logo only via `<img>`, never `dangerouslySetInnerHTML`/`<object>`/`<embed>`.
  - *Logo hosting*: download and re-host the validated logo in CM-controlled storage rather than linking live to the source URL — otherwise there's a time-of-check/time-of-use gap (the source domain can change after validation) and every panel view leaks CM usage/referrer/IP to the org's own infrastructure.
  - *Extraction fetch (SSRF)*: whatever server-side process fetches the org-supplied source URL to extract these tokens from must block private/link-local IP ranges (including cloud metadata endpoints like `169.254.169.254`), restrict redirects to the originally-validated host, apply DNS-rebinding protection, and enforce timeouts/size caps. An org-supplied URL fetched by CM's own backend is a standard SSRF shape if these aren't in place — this needs to be true from the pipeline's first version, not added later.
- Where these tokens come from (what source per org triggers extraction) and how often they're refreshed is still an open implementation question (Section 9) — that's the one part of the pipeline still undetermined; the hardening requirements above apply regardless of how that's resolved.

## 7. Non-functional considerations

- **Access control**: inherently solved by construction at the environment level — the panel only exists inside an already-authenticated Cloud Manager session with legitimate access to that specific environment. There's no new gating logic to design there (unlike the redirect spec, which needed one specifically because it left the authenticated session). One thing this doesn't automatically cover: since the route (Section 5.3) encodes the content source name directly (`.../content-ai/wknd`) and is user-editable, the server side must still enforce that the requesting user's session is actually authorized for *that specific content source* within the environment, not just for the environment in general — a client-side-only check would let someone edit the URL to reach a source they shouldn't see.
- **Testing**: ownership sits entirely with the Cloud Manager team (Section 8) — testing (frontend component tests, integration tests against a Content AI sandbox) is their responsibility, not something planned against `aem-core-wcm-components`.

## 8. Ownership & scope

This entire feature — the nav item, the panel UI, the branding-token extraction/storage — is **Cloud Manager-team-owned work, in their own repository**, confirmed directly by the research in Section 4 (CM's nav is a closed surface with no extension point for other teams). **Nothing in this design lands in `aem-core-wcm-components`.** This repo's existing gensearch component family serves only as a UX/behavior reference for Section 5.4, not as code that gets reused directly.

This is stated plainly, not glossed over: a plan generated from this spec would have essentially no actionable implementation steps inside `aem-core-wcm-components`. If this design moves forward, the natural next step is handing it to the Cloud Manager team as a proposal, not generating an in-repo implementation plan the way the 2026-07-20 spec's §8 item 4 could be.

**The asymmetry between the two specs is bigger than "different architecture," and worth naming directly**: the 2026-07-20 spec has a concrete, scoped, already-empirically-verified code change in this repo (`ContentAIClientImpl` Bearer-token support, its §8 item 4) that a plan can act on today. This spec has no comparable unit — every piece of it sits in a codebase this document's author has no PR rights in and no self-service entry point into (Section 4). Put plainly: the redirect design is actionable *now*; this one is actionable only *after* a separate negotiation with the Cloud Manager team that this document doesn't own or control. Anyone deciding between the two specs should weigh that difference directly, not assume both are equally "ready to hand off for planning."

## 9. Open questions / dependencies

- **Branding token pipeline**: Section 6 establishes the safe extraction *pattern*, but not where tokens are extracted from (would still need a URL or other input per org), how often they're refreshed, or where they're stored. Needs its own design pass, likely owned by whichever team builds this.
- **Per-environment vs. per-org identity**: Content AI Sources are named per environment today (e.g. `wknd`); this design doesn't introduce a new identity concept beyond that, but the branding-token lookup (Section 6) needs *some* key to associate tokens with — presumably the content source name, consistent with how the rest of this design scopes by source, not a new per-org concept.
- **Formal handoff to the Cloud Manager team — currently underspecified, not just "not yet done."** This document has no named recipient: no specific team alias, Slack channel, JIRA project, or PM/engineering contact on the CM side. A spec living in this repo's `docs/superpowers/specs/` folder has no natural discovery path to the CM team — nobody there greps this repo. Before this design can be considered ready to move forward, someone needs to identify a concrete destination (a CM team channel, a specific contact, or a ticket filed in their project) and actually send it — this is a real, unstarted action item, not a formality.
- **Cloud Manager's willingness/prioritization to take this on is unconfirmed and is itself a primary risk — separate from the technical feasibility already covered above.** This design assumes CM will agree to make "Content AI" a permanent top-level nav item, remove the existing tab, and build a branding-token pipeline (Sections 5–7), but none of that has CM buy-in yet. It competes for their own roadmap and headcount, and they may prefer a different shape (or decline entirely) once they see it. This is arguably more likely to determine whether this design ships at all than any point raised elsewhere in this document.
