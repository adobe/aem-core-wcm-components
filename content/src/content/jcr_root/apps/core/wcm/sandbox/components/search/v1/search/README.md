<!--
Copyright 2017 Adobe Systems Incorporated

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
Quick Search (v1 - sandbox)
====
Search component written in HTL.

## Features

### Use Object
The Search component uses the `com.adobe.cq.wcm.core.components.sandbox.models.Search` Sling model as its Use-object.

### Component policy configuration properties
The following configuration properties are used:

1. `./startLevel` - defines the level from which to search for results (eg: 1 for /content/site)

### Edit dialog properties
The following properties are written to JCR for the Search component and are expected to be available as `Resource` properties:

1. `./startLevel` - defines the level from which to search for results (eg: 1 for /content/site)

## BEM description
```
BLOCK cmp-search
    ELEMENT cmp-search__form
    ELEMENT cmp-search__field
    ELEMENT cmp-search__icon
    ELEMENT cmp-search__input
    ELEMENT cmp-search__clear
    ELEMENT cmp-search__clear-icon
    ELEMENT cmp-search__results
    ELEMENT cmp-search__item
    ELEMENT cmp-search__item-mark
    ELEMENT cmp-search__item-title
        MOD cmp-search__item--focused
```

## Information
* **Vendor**: Adobe
* **Version**: v1 - sandbox
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_search\_v1](https://www.adobe.com/go/aem_cmp_search_v1)

