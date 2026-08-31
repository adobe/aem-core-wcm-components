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

## Configuration - Content AI API Access
The component calls the Content AI `content-sources/search` and `content-sources/gensearch` APIs through the `ContentAIClient` OSGi service, which is configured via the `Core Components Content AI Client` OSGi configuration (PID `com.adobe.cq.wcm.core.components.internal.services.contentai.ContentAIClientImpl`):

| Property | Required | Description |
|---|---|---|
| `apiKey` | Yes | Adobe Developer Console client ID (X-Api-Key) used for anonymous, public-index Content AI access. |
| `defaultContentSource` | No | Default public content source name used when a component instance does not specify one. |
| `connectionTimeout` | No | Connection timeout (ms) to Content AI. Defaults to `2000`. |
| `socketTimeout` | No | Socket read timeout (ms). Defaults to `10000`. |
| `baseUrlOverride` | No | Full Content AI base URL override. Required on AEM 6.5 LTS / Adobe Managed Services (AMS) and for local development. |

Without a valid `apiKey`, the component renders its results section but search/gensearch requests fail gracefully (see `ContentAIClientException`); no key is bundled with, or defaulted by, this component.

### Setting `apiKey` on AEM as a Cloud Service
1. Obtain an X-Api-Key by requesting Content AI API access for your AEM CS Program/Environment (via the Content AI onboarding process) and creating a Server-to-Server credential in Adobe Developer Console. Copy its "API Key (Client ID)".
2. In your own Cloud Manager Git repository, add an OSGi configuration targeting the PID above, for example at:
   `ui.config/src/main/content/jcr_root/apps/<your-app>/osgiconfig/config.author/com.adobe.cq.wcm.core.components.internal.services.contentai.ContentAIClientImpl.cfg.json`
   ```json
   {
     "apiKey": "$[secret:CONTENT_AI_API_KEY]"
   }
   ```
3. Set `CONTENT_AI_API_KEY` as a secret environment variable for your environment in Cloud Manager, using the key copied in step 1.
4. On the next deploy (or OSGi config reload), Cloud Manager's environment variable interpolation resolves the placeholder and the component starts sending `X-Api-Key` automatically. No AEM code changes are required.

### Setting `apiKey` and `baseUrlOverride` on AEM 6.5 LTS / Adobe Managed Services
AEM 6.5 LTS and AMS do not expose the `AEM_PROGRAM_ID`/`AEM_ENV_ID`/`AEM_SERVICE` environment variables that AEM as a Cloud Service uses to derive its own Content AI host, so both properties must be set explicitly:
1. Obtain an X-Api-Key as in step 1 above, and the Content AI base URL for your provisioned bucket (of the form `https://{tier}-p{PID}-e{EID}.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI`).
2. Add an OSGi configuration targeting the PID above with both `apiKey` and `baseUrlOverride` set, via the standard AMS OSGi configuration delivery process (e.g. a `.cfg.json` file in your content package, or the Web Console on environments where that's permitted).

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
* **Compatibility**: AEM as a Cloud Service, AEM 6.5 LTS up to service pack 19 (including Adobe Managed Services). The visitor-facing generative-summary toggle is unsupported and hidden on AEM 6.5 service pack 20 and later.
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_ai\_search\_v1](https://www.adobe.com/go/aem_cmp_ai_search_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_ai\_search](https://www.adobe.com/go/aem_cmp_library_ai_search)
