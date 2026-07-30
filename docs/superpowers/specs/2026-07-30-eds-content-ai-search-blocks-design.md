# EDS Semantic Search & Content AI Search Blocks — Design

_Jira: [GRANITE-71249](https://jira.corp.adobe.com/browse/GRANITE-71249) • Status: draft, backend approach pending teammate discussion_

## 1. Goal

Replicate the AEM **ContentAI Supported Search** and **Semantic Search** core
components (`GRANITE-70028`, `GRANITE-69682`) as native **Edge Delivery
Services (EDS)** blocks, so the same search experience can be dropped into
EDS sites — which have no AEM page resource behind them for a Sling model to
render against.

## 2. Background

- **AEM side (this repo)**: a shipped `ContentAISupportedSearch` core
  component (v1, status "work-in-progress") backed by Content AI's
  `content-sources/search` + `content-sources/gensearch` APIs via
  `X-Api-Key`. Dialog-configurable content sources, card/list layout, and a
  generative-summary toggle. "Semantic Search" is a separate existing toggle
  (`GRANITE-69682`), not a standalone component.
- **EDS reference**: [posabogdanpetre/frescopa](https://github.com/posabogdanpetre/frescopa)
  (live: `https://main--frescopa--posabogdanpetre.aem.live/stories`) already
  has a working search experience in EDS:
  - `blocks/search-input` — the interactive piece: a lexical / semantic /
    generative mode toggle, example-query chips (persisted per-tab in
    `sessionStorage`), cursor-paginated result cards, and a generative-answer
    panel with source links. It calls custom Sling servlets directly from
    client JS: `/bin/caid/{lexicalsearch,semanticsearch,gensearch}` on the
    **publish** host (`config.json` → `aem.publish`).
  - `blocks/contentai-hero` — a pure presentational banner (kicker/title/
    description), authored via `component-models.json` fields, no backend
    calls.
  - Both blocks are registered as authorable components via
    `component-definition.json` / `component-models.json` (the DA.live /
    Universal Editor block-authoring pattern).

## 3. Decided scope

- **Two distinct EDS blocks**, not a single unified block:
  1. A **Semantic Search** block (lexical + semantic keyword-style search,
     result cards, pagination) — modeled on frescopa's `search-input` minus
     its generative mode.
  2. A **Content AI Search** block (generative/AI-answer experience: query →
     synthesized answer + source links) — modeled on frescopa's generative
     mode panel (`renderGenAnswer`) as its own block.
- frescopa is the UI/UX reference for both: card layout, loading/empty/error
  states, generative-answer panel with sources, and the block-authoring
  pattern (`component-definition.json` / `component-models.json`) for making
  the blocks configurable in DA.live / Universal Editor.

## 4. Open questions — pending teammate discussion

Everything about the **backend** is left open. To be confirmed before
implementation planning begins:

1. **Backend architecture** — how do the EDS blocks reach Content AI?
   Options discussed but not decided:
   - New standalone (resource-independent) Sling servlets in this repo,
     reusing the existing `ContentAIClient` service layer, exposed at fixed
     paths (mirroring frescopa's `/bin/caid/*` pattern) — keeps the
     `X-Api-Key` server-side.
   - Same, but **config-driven per-instance**: each block's authoring model
     lets a page author pick content source(s)/layout, validated by the
     servlet against an OSGi allow-list — closer parity with the AEM
     component's per-instance dialog configurability.
   - Direct browser calls to Content AI's public API with a client-exposed
     key — simplest, but exposes the key client-side and bypasses the
     validation/auth logic already built into `ContentAIClient`.
2. **Deliverable location** — where the block code (JS/CSS/authoring models)
   should live. EDS blocks conventionally live in a standalone EDS site repo,
   not in this Java Maven multi-module repo (confirmed via internal
   Slack/docs on EDS block-development conventions). Not yet decided:
   - A new minimal reference EDS repo (e.g. via `aem-boilerplate` +
     `adobe/skills` EDS block-development skill).
   - A fork/extension of `frescopa` itself.
   - An existing internal or customer EDS repo.

## 5. Next steps

Once the backend architecture and deliverable location are confirmed:
resume design (data flow, per-block authoring fields, error/empty/loading
states, auth model) in sections, get sign-off, then hand off to
`writing-plans` for an implementation plan.
