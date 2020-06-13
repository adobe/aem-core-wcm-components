<!--
Copyright 2017 Adobe

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
Quick Search (v1)
====
Search component written in HTL.

## Features

### Use Object
The Search component uses the `com.adobe.cq.wcm.core.components.models.Search` Sling model as its Use-object.

### Behavior
When the user is scrolling down the results, if the hidden results below are less than the visible results, more results
are fetched.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./searchRoot` - the root page from which to search. It can be a blueprint master, language master or regular page.
2. `./resultsSize` - the maximal number of results fetched by a search request
3. `./searchTermMinimumLength` - the minimum required length of the search term before results are fetched

### Edit Dialog Properties
The following properties are written to JCR for the Search component and are expected to be available as `Resource` properties:

1. `./searchRoot` - the root page from which to search. It can be a blueprint master, language master or regular page.
2. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component provides a `core.wcm.components.search.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-search
    ELEMENT cmp-search__form
    ELEMENT cmp-search__field
    ELEMENT cmp-search__icon
    ELEMENT cmp-search__input
    ELEMENT cmp-search__loading-indicator
    ELEMENT cmp-search__clear
    ELEMENT cmp-search__clear-icon
    ELEMENT cmp-search__results
    ELEMENT cmp-search__item
    ELEMENT cmp-search__item-mark
    ELEMENT cmp-search__item-title
        MOD cmp-search__item--is-focused
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="search"` attribute to the wrapper block to enable initialization of the JavaScript component.

The following attributes can be added to the same element to provide options:

1. `data-cmp-min-length` - populated with `searchTermMinimumLength` from the component's policy configuration
2. `data-cmp-results-size` - populated with `resultsSize` from the component's policy configuration

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-search="form"
data-cmp-hook-search="icon"
data-cmp-hook-search="loadingIndicator"
data-cmp-hook-search="input"
data-cmp-hook-search="clear"
data-cmp-hook-search="results"
data-cmp-hook-search="itemTemplate"
data-cmp-hook-search="item"
data-cmp-hook-search="itemTitle"
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_search\_v1](https://www.adobe.com/go/aem_cmp_search_v1)
