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
Language Navigation (v2)
====
Language Navigation component written in HTL that renders a global language structure navigation.

## Features

### Use Object
The Language Navigation component uses the `com.adobe.cq.wcm.core.components.models.LanguageNavigation` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./navigationRoot` - the root page of the global language structure.
2. `./structureDepth` - the depth of the global language structure relative to the navigation root.

### Edit Dialog Properties
The following properties are written to JCR for the Language Navigation component and are expected to be available as `Resource` properties:

1. `./navigationRoot` - the root path of the global language structure.
2. `./structureDepth` - the depth of the global language structure relative to the navigation root.
3. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-languagenavigation
    ELEMENT cmp-languagenavigation__group
    ELEMENT cmp-languagenavigation__item
        MOD cmp-languagenavigation__item--active
        MOD cmp-languagenavigation__item--countrycode-*
        MOD cmp-languagenavigation__item--langcode-*
        MOD cmp-languagenavigation__item--level-*
    ELEMENT cmp-languagenavigation__item-link
    ELEMENT cmp-languagenavigation__item-title
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_languagenavigation\_v2(https://www.adobe.com/go/aem_cmp_languagenavigation_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_langnav](https://www.adobe.com/go/aem_cmp_library_langnav)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann)
