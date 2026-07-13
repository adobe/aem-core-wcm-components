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
ContentAI Supported Search (v1)
====
Search component backed by the Content AI `content-sources/search` and `content-sources/gensearch` APIs.

## Features

### Use Object
The ContentAI Supported Search component uses the `com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch` Sling model as its Use-object.

### Behavior
On input, the component fetches merged search results from `.search.json`. When the generative summary toggle is enabled, it fetches `.gensearch.json` in parallel. Results are rendered from HTL `x-template` markup; the JavaScript clones those templates and populates title, description, image, and link fields from each API result.

### Edit Dialog Properties
The following properties are written to JCR for the ContentAI Supported Search component:

1. `./contentSourceType` - the Content AI content source type (default `ACQUISITION`)
2. `./contentSources` - the selected Content AI content source names
3. `./primaryContentSource` - optional override for generative search source
4. `./resultsSize` - default number of results to fetch
5. `./resultsLayout` - `card` or `list`
6. `./genSearchEnabledByDefault` - whether the generative summary toggle defaults to on
7. `./genSearchToggleVisible` - whether the visitor-facing generative summary toggle is rendered
8. `./genSearchErrorFallback` - visitor-facing fallback when generative search fails
9. `./placeholder` - search input placeholder text
10. `./disclaimerText` - optional disclaimer below the generative summary
11. `./id` - defines the component HTML ID attribute

## Client Libraries
The component provides a `core.wcm.components.contentaisearch.v1` client library category that contains recommended base CSS styling and JavaScript. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-contentaisearch
    MOD cmp-contentaisearch--card
    MOD cmp-contentaisearch--list
    ELEMENT cmp-contentaisearch__form
    ELEMENT cmp-contentaisearch__field
    ELEMENT cmp-contentaisearch__input
    ELEMENT cmp-contentaisearch__loading-indicator
    ELEMENT cmp-contentaisearch__toggle
    ELEMENT cmp-contentaisearch__summary
    ELEMENT cmp-contentaisearch__summary-card
    ELEMENT cmp-contentaisearch__summary-header
    ELEMENT cmp-contentaisearch__summary-icon
    ELEMENT cmp-contentaisearch__summary-title
    ELEMENT cmp-contentaisearch__summary-attribution
    ELEMENT cmp-contentaisearch__summary-text
    ELEMENT cmp-contentaisearch__sources-section
    ELEMENT cmp-contentaisearch__sources-label
    ELEMENT cmp-contentaisearch__sources
    ELEMENT cmp-contentaisearch__source-chip
    ELEMENT cmp-contentaisearch__disclaimer
    ELEMENT cmp-contentaisearch__error
    ELEMENT cmp-contentaisearch__results-section
    ELEMENT cmp-contentaisearch__results-toolbar
    ELEMENT cmp-contentaisearch__results-status
    ELEMENT cmp-contentaisearch__results-limit
    ELEMENT cmp-contentaisearch__results
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

The following attributes can be added to the same element to provide options:

1. `data-cmp-results-size` - populated with `resultsSize` from the component configuration
2. `data-cmp-results-layout` - populated with `resultsLayout` from the component configuration (`card` or `list`)
3. `data-cmp-gensearch-enabled-default` - populated with `genSearchEnabledByDefault`
4. `data-cmp-gensearch-toggle-visible` - populated with `genSearchToggleVisible`
5. `data-cmp-gensearch-error-fallback` - populated with `genSearchErrorFallback`
6. `data-cmp-resource-path` - the component resource path used to build `.search.json` and `.gensearch.json` URLs
7. `data-i18n-messages` - localized strings for client-side rendering

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-contentaisearch="form"
data-cmp-hook-contentaisearch="input"
data-cmp-hook-contentaisearch="loadingIndicator"
data-cmp-hook-contentaisearch="toggle"
data-cmp-hook-contentaisearch="summary"
data-cmp-hook-contentaisearch="summaryText"
data-cmp-hook-contentaisearch="sources"
data-cmp-hook-contentaisearch="disclaimer"
data-cmp-hook-contentaisearch="error"
data-cmp-hook-contentaisearch="errorMessage"
data-cmp-hook-contentaisearch="retry"
data-cmp-hook-contentaisearch="resultsSection"
data-cmp-hook-contentaisearch="resultsStatus"
data-cmp-hook-contentaisearch="resultsLimit"
data-cmp-hook-contentaisearch="results"
data-cmp-hook-contentaisearch="itemTemplate"
data-cmp-hook-contentaisearch="item"
data-cmp-hook-contentaisearch="itemTitle"
data-cmp-hook-contentaisearch="itemDescription"
data-cmp-hook-contentaisearch="itemImage"
data-cmp-hook-contentaisearch="itemImagePlaceholder"
data-cmp-hook-contentaisearch="sourceTemplate"
data-cmp-hook-contentaisearch="sourceLink"
data-cmp-hook-contentaisearch="sourceText"
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM as a Cloud Service
* **Status**: work-in-progress
