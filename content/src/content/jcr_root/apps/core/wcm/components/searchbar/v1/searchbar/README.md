<!--
Copyright 2019 Adobe Systems Incorporated

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
Search Bar (v1)
====
Search Bar written in HTL.

## Features

### Use Object
The Search Bar component uses the `com.adobe.cq.wcm.core.components.models.Searchbar` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for the Search Bar component and are expected to be available as `Resource` properties:

1. `./hideButton` - shows or hides "Search" button.
2. `./resultpage` - result page path used for redirection

## Client Libraries
The component provides a `core.wcm.components.search.searchbar.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-search-bar
    ELEMENT cmp-search-bar__field
    ELEMENT cmp-search-bar__field--text
    ELEMENT cmp-search-bar__field--btn
    ELEMENT cmp-search-bar__action-icon
    ELEMENT cmp-search-bar__action-text
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_searchbar\_v1](https://www.adobe.com/go/aem_cmp_searchbar_v1)
