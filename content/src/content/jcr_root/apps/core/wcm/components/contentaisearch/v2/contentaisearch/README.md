<!--
Copyright 2026 Adobe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-->
ContentAI Supported Search (v2)
====
Search component backed by the Content AI `content-sources/search` and `content-sources/gensearch` APIs, presenting the generative answer and the results list as two mutually-exclusive tabs — Search Results and AI Mode — instead of v1's stacked checkbox-toggle layout.

## Features

### Use Object
The component uses the `com.adobe.cq.wcm.core.components.models.ContentAISupportedSearchV2` Sling model as its Use-object (extends v1's `ContentAISupportedSearch`).

### Behavior
On submit, the component always fetches `.search.json`. When AI Search Mode is enabled (see below), it also fetches `.gensearch.json` in parallel — regardless of which tab is currently active, so switching tabs is instant rather than triggering a new fetch. When AI Search Mode is disabled, `.gensearch.json` is never called at all.

The active tab persists across page loads via a single, site-wide cookie (`cmp-contentaisearch-tab`, values `search-results`/`ai-mode`, set with `path=/; max-age=<1 year>; SameSite=Lax; Secure`), applied by a small synchronous script inline in the component's own markup — not a server-side cookie read — so pages using this component remain fully cacheable.

### Edit Dialog Properties
The following properties are written to JCR for the component:

1. `./contentSourceType` - the Content AI content source type (default `ACQUISITION`)
2. `./contentSources` - the selected Content AI content source names
3. `./primaryContentSource` - optional override for generative search source (resource property; not exposed as a dialog field — set via the underlying resource, not authored through the dialog UI)
4. `./resultsSize` - default number of results to fetch
5. `./resultsLayout` - `card` or `list`
6. `./placeholder` - placeholder text for the search input
7. `./aiSearchModeEnabled` - whether the AI Mode tab is shown to visitors at all (unchecked = plain Search Results view, no tab bar, generative search never called)
8. `./genSearchErrorRetryVisible` - whether a retry button is shown when the AI Mode tab's answer fails to generate
9. `./disclaimerText` - optional disclaimer below the generative summary
10. `./id` - defines the component HTML ID attribute

Compared to v1: `genSearchEnabledByDefault` is removed (the default tab is always Search Results); `genSearchToggleVisible` is renamed to `aiSearchModeEnabled`; `genSearchErrorFallback`'s three states are replaced by the two-state `genSearchErrorRetryVisible`.

## Configuration - Content AI API Access
Shares the same `ContentAIClient` OSGi service and configuration as v1 — see the [v1 README](../../v1/contentaisearch/README.md#configuration---content-ai-api-access) for the full `apiKey`/Cloud Manager secret setup. No v2-specific configuration exists; both versions call the same backend.

## Client Libraries
The component provides a `core.wcm.components.contentaisearch.v2` client library category (base CSS + JS) and a `core.wcm.components.contentaisearch.v2.editor` category (dialog-time JS). Add the site category to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-contentaisearch
    MOD cmp-contentaisearch--card
    MOD cmp-contentaisearch--list
    ELEMENT cmp-contentaisearch__form
    ELEMENT cmp-contentaisearch__field
    ELEMENT cmp-contentaisearch__input
    ELEMENT cmp-contentaisearch__input-label
    ELEMENT cmp-contentaisearch__icon
    ELEMENT cmp-contentaisearch__clear
    ELEMENT cmp-contentaisearch__clear-icon
    ELEMENT cmp-contentaisearch__tabs
    ELEMENT cmp-contentaisearch__tab
    ELEMENT cmp-contentaisearch__panel
    ELEMENT cmp-contentaisearch__summary
    ELEMENT cmp-contentaisearch__summary-card
    ELEMENT cmp-contentaisearch__summary-header
    ELEMENT cmp-contentaisearch__summary-icon
    ELEMENT cmp-contentaisearch__summary-heading
    ELEMENT cmp-contentaisearch__summary-title
    ELEMENT cmp-contentaisearch__summary-attribution
    ELEMENT cmp-contentaisearch__summary-text
    ELEMENT cmp-contentaisearch__summary-loading
    ELEMENT cmp-contentaisearch__summary-loading-content
    ELEMENT cmp-contentaisearch__summary-loading-indicator
    ELEMENT cmp-contentaisearch__summary-loading-text
    ELEMENT cmp-contentaisearch__sources-section
    ELEMENT cmp-contentaisearch__sources-label
    ELEMENT cmp-contentaisearch__sources
    ELEMENT cmp-contentaisearch__source-chip
    ELEMENT cmp-contentaisearch__disclaimer
    ELEMENT cmp-contentaisearch__error
    ELEMENT cmp-contentaisearch__results-section
    ELEMENT cmp-contentaisearch__results-toolbar
    ELEMENT cmp-contentaisearch__layout-toggle
    ELEMENT cmp-contentaisearch__layout-btn
    ELEMENT cmp-contentaisearch__results
    ELEMENT cmp-contentaisearch__load-more
    ELEMENT cmp-contentaisearch__item
    ELEMENT cmp-contentaisearch__card
    ELEMENT cmp-contentaisearch__card-image
    ELEMENT cmp-contentaisearch__card-body
    ELEMENT cmp-contentaisearch__card-title
    ELEMENT cmp-contentaisearch__card-description
    ELEMENT cmp-contentaisearch__row
    ELEMENT cmp-contentaisearch__row-image
    ELEMENT cmp-contentaisearch__row-body
    ELEMENT cmp-contentaisearch__row-title
    ELEMENT cmp-contentaisearch__row-description
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="contentaisearch"` attribute to the wrapper block to enable initialization of the JavaScript component.

1. `data-cmp-content-source` - populated with `primaryContentSource`, specifies the Content AI source for generative search
2. `data-cmp-ai-search-mode-enabled` - populated with `aiSearchModeEnabled`; a Sightly boolean-typed attribute, so it's rendered bare (present) when true and omitted entirely when false - the client JS reads it with `hasAttribute`, not a string comparison
3. `data-cmp-gensearch-error-retry-visible` - populated with `genSearchErrorRetryVisible`, present on the root element for reference/parity only — the client JS never reads it; retry-button visibility is enforced server-side by the HTL (`data-sly-test="${search.genSearchErrorRetryVisible}"` on the retry button itself), which simply omits the button from the DOM when false
4. `data-cmp-results-layout` - populated with `resultsLayout` (`card` or `list`)
5. `data-cmp-resource-path` - the component resource path used to build `.search.json` and `.gensearch.json` URLs

```
data-cmp-hook-contentaisearch="form"
data-cmp-hook-contentaisearch="input"
data-cmp-hook-contentaisearch="icon"
data-cmp-hook-contentaisearch="clear"
data-cmp-hook-contentaisearch="tabs"
data-cmp-hook-contentaisearch="tabSearchResults"
data-cmp-hook-contentaisearch="tabAiMode"
data-cmp-hook-contentaisearch="panelSearchResults"
data-cmp-hook-contentaisearch="panelAiMode"
data-cmp-hook-contentaisearch="summaryLoading"
data-cmp-hook-contentaisearch="summary"
data-cmp-hook-contentaisearch="summaryText"
data-cmp-hook-contentaisearch="sources"
data-cmp-hook-contentaisearch="disclaimer"
data-cmp-hook-contentaisearch="error"
data-cmp-hook-contentaisearch="errorMessage"
data-cmp-hook-contentaisearch="retry"
data-cmp-hook-contentaisearch="resultsSection"
data-cmp-hook-contentaisearch="results"
data-cmp-hook-contentaisearch="loadMore"
data-cmp-hook-contentaisearch="layoutCard"
data-cmp-hook-contentaisearch="layoutList"
data-cmp-hook-contentaisearch="itemTemplateCard"
data-cmp-hook-contentaisearch="itemTemplateList"
data-cmp-hook-contentaisearch="item"
data-cmp-hook-contentaisearch="itemTitle"
data-cmp-hook-contentaisearch="itemDescription"
data-cmp-hook-contentaisearch="itemImage"
data-cmp-hook-contentaisearch="itemImagePlaceholder"
data-cmp-hook-contentaisearch="sourceTemplate"
data-cmp-hook-contentaisearch="sourceLink"
data-cmp-hook-contentaisearch="sourceText"
data-cmp-hook-contentaisearch="prepaint"
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM as a Cloud Service
* **Status**: work-in-progress
