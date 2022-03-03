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

Navigation (v2)
====
Navigation component written in HTL that renders a website navigation tree.

## Features
* Can be used on both templates and pages
* Defines a configurable navigation root, navigation root depth and structure depth to allow flexibility in building the navigation tree
* Automatically filters out pages that should be hidden from navigation
* Automatically handles redirect targets defined on pages

### Use Object
The Navigation component uses the `com.adobe.cq.wcm.core.components.models.Navigation` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./navigationRoot` - the root page from which to build the navigation. It can be a blueprint master, language master or regular page.
2. `./structureStart` - the start level of the navigation structure relative to the navigation root. 
3. ~~./skipNavigationRoot~~ - **deprecated**; if `true`, excludes the navigation root in the resulting tree, including its descendants only.
4. `./collectAllPages` - if `true`, collects all pages that are descendants of the `./navigationRoot`. Overrides `./structureDepth`.
5. `./structureDepth` - the depth of the navigation structure, relative to the navigation root.
6. `./disableShadowing` - for redirecting pages PageA -> PageB. If `true` - PageA(original page) is shown. If `false` or not configured - PageB(target page).

### Edit Dialog Properties
The following properties are written to JCR for the Navigation component and are expected to be available as `Resource` properties:

1. `./navigationRoot` - the root page from which to build the navigation. It can be a blueprint master, language master or regular page.
2. `./structureStart` - the start level of the navigation structure relative to the navigation root.
3. ~~./skipNavigationRoot~~ - **deprecated**; if `true`, excludes the navigation root in the resulting tree, including its descendants only.
4. `./collectAllPages` - if `true`, collects all pages that are descendants of the `./navigationRoot`. Overrides `./structureDepth`.
5. `./structureDepth` - the depth of the navigation structure, relative to the navigation root.
6. `./accessibilityLabel` - defines an accessibility label for the navigation.
7. `./disableShadowing` - for redirecting pages PageA -> PageB. If `true` - PageA(original page) is shown. If `false` or not configured - PageB(target page).
8. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component reuses the `core.wcm.components.navigation.v1.editor` editor client library category that includes
JavaScript handling for dialog interaction. It is already included by its edit and design dialogs.

## BEM Description
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
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_navigation\_v2](https://www.adobe.com/go/aem_cmp_navigation_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_navigation](https://www.adobe.com/go/aem_cmp_library_navigation)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann)
