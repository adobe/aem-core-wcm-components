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

Navigation (v1 - sandbox)
====
Navigation component written in HTL that renders a website navigation tree.

## Features
* Can be used on both templates and pages
* Defines a configurable site root, start level and max depth for allowing flexibility in building the navigation tree
* Automatically filters out pages that should be hidden from navigation
* Automatically handles redirect targets defined on pages


### Use Object
The Navigation component uses the `com.adobe.cq.wcm.core.components.sandbox.models.Navigation` Sling model as its Use-object.

### Component policy configuration properties
The following configuration properties are used:

1. `./siteRoot` - defines the root of the website for which to build the navigation tree
2. `./startLevel` - defines the start level, relative to the site root; the site root is level 0
3. `./currentPageTreeOnly` - boolean value that enables collecting pages only from the current page's tree
4. `./maxDepth` - defines the maximum depth level in the content tree for searching pages, relative to the site root

### Edit dialog properties
The following properties are written to JCR for the Navigation component and are expected to be available as `Resource` properties:

1. `./siteRoot` - defines the root of the website for which to build the navigation tree
2. `./startLevel` - defines the start level, relative to the site root; the site root is level 0
3. `./currentPageTreeOnly` - boolean value that enables collecting pages only from the current page's tree
4. `./maxDepth` - defines the maximum depth level in the content tree for searching pages, relative to the site root

## BEM description
```
BLOCK cmp-navigation
    ELEMENT cmp-navigation__group
    ELEMENT cmp-navigation__item
        MOD cmp-navigation__item--active
        MOD cmp-navigation__item--level-*
    ELEMENT cmp-navigation__item-link
```

## Information
* **Vendor**: Adobe
* **Version**: v1 - sandbox
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_navigation\_v1](https://www.adobe.com/go/aem_cmp_navigation_v1)

