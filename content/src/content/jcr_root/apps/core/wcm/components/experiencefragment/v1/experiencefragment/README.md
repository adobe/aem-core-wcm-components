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

Experience Fragment (v1)
====
Experience fragment component written in HTL that renders an experience fragment variation.

## Features
* Can be used on both templates and pages
* Defines a configurable experience fragment variation to be displayed
* Supports references for localized content

### Use Object
The Experience fragment component uses the `com.adobe.cq.wcm.core.components.models.ExperienceFragment` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./localizationRoot` - the root page of the global localization structure.
2. `./localizationDepth` - the depth of the global localization structure relative to the localization root.

### Edit Dialog Properties
The following properties are written to JCR for the Experience Fragment component and are expected to be available as `Resource` properties:

1. `./fragmentPath` - defines the path to the Experience Fragment to be rendered
2. `./localizationRoot` - the root page of the global localization structure.
3. `./localizationDepth` - the depth of the global localization structure relative to the localization root.

## BEM Description
```
BLOCK cmp-experiencefragment
```

Note: the rendered HTML markup of the Experience Component contains CSS classes that start with `xf-` (e.g. `xf-content-height` or `xf-master-building-block`):
those classes are private and should not be used in custom code (e.g. to style the markup).

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.4
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_experiencefragment\_v1](https://www.adobe.com/go/aem_cmp_experiencefragment_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_xf](https://www.adobe.com/go/aem_cmp_library_xf)

