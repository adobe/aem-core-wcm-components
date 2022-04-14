<!--
Copyright 2021 Adobe

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
Content Fragment List (v2)
====
Content Fragment List component written in HTL that renders a list of Content Fragments. Useful for authoring headless content that can be easily consumed by applications. 

## Features
* Displays a list of a Content Fragment assets based on a Content Fragment model
* The parent path for asset lookup is configurable
* The list can be filtered by tag
* The list can be ordered by an element or property - ascending or descending
* A subset of elements in the data model can be displayed

### Use Object
The Content Fragment List component uses the `com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragmentList` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for the Content Fragment List component and are expected to be available as `Resource` properties:

1. `./modelPath` - path to the Content Fragment Model on which the list is based.
2. `./parentPath` - parent path from which the list should be built.
3. `./tagNames` - tag names for filtering the list.
4. `./orderBy` - an element or property to order the list by.
5. `./sortOrder` - sort order ascending or descending.
6. `./maxItems` - defines the maximum number of items rendered by the list. If not defined, all fragments matching the query criteria are returned.
7. `./elementNames` - element names for limiting the model data displayed in the result.
8. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component reuses the `core.wcm.components.contentfragmentlist.v1.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit dialog.

## BEM Description
```
BLOCK cmp-contentfragmentlist
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_cflist\_v2](https://www.adobe.com/go/aem_cmp_cflist_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_cflist](https://www.adobe.com/go/aem_cmp_library_cflist)
* **Authors**: [Burkhard Pauli](https://github.com/bpauli)
