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
Search Result (v1)
====
Search Result component written in HTL.

## Features

### Use Object
The Search component uses the `com.adobe.cq.wcm.core.components.models.Search` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for the Search component and are expected to be available as `Resource` properties:

1. `./searchRoot` - the root page from which to search. Can be a blueprint master, language master or regular page.
2. `./resultsSize` - the maximal number of results displayed initially.
3. `./showResultCount` - total number of records.
4. `./guessTotal` - field for query builder for estimation.
5. `./resultCountFormat` - text to be displayed showing number of records.
6. `./defaultSort` - parameter on which default sorting is performed.
7. `./defaultSortDirection` - default sorting direction.
8. `./loadMoreText` - text displayed on load more button.
9. `./noResultText` - text displayed in case of no results.
10. `./enableFacet` - enables or diables facet section.
11. `./facetTitle` - title for facet section.
12. `./tagProperty` -  name of the field which is submitted with the form dataular page.
13. `./cq:tags` - tags to populate the filter options.
14. `./enableSort` - enables or disables sort option.
15. `./sortTitle` - sort title to be displayed.
16. `./sortItems` - options around which results can be rearranged.
17. `./text` - sort property text.
18. `./value` - sort property value.



## Client Libraries
The component provides a `core.wcm.components.searchresult.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-search
    ELEMENT search__field--results
    ELEMENT search__field--view
    ELEMENT cmp-search__total-records
    ELEMENT cmp-search-list__item-group
    ELEMENT search__results--footer
    ELEMENT cmp-sort__heading
    ELEMENT cmp-facet__heading
    ELEMENT cmp-facet__filter
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_searchresult\_v1](https://www.adobe.com/go/aem_cmp_searchresult_v1)
