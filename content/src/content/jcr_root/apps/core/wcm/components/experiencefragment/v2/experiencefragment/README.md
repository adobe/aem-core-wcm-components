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

Experience Fragment (v2)
====
Experience fragment component written in HTL that renders an experience fragment variation.

## Features
* Can be used on both templates and pages.
* Defines a configurable experience fragment variation to be displayed.
* Supports references for localized content: if the component is defined in a template and if the fragment is part of a localized structure below `/content/experience-fragments` that follows the same patterns as the site below `/content`, the fragment with the same localization (language, blueprint or live copy) as the current page will be rendered.

### Use Object
The experience fragment component uses the `com.adobe.cq.wcm.core.components.models.ExperienceFragment` Sling model as its Use-object.

### Edit Dialog Properties
The following property is written to JCR for the experience fragment component and is expected to be available as a `Resource` property:

1. `./fragmentVariationPath` - defines the path to the experience fragment variation to be rendered.
2. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-experiencefragment
  MOD cmp-experiencefragment--<name>
```

Note: the rendered HTML markup of the experience fragment component may contain CSS classes that start with `xf-` (e.g. `xf-content-height` or `xf-master-building-block`) -
those classes are private and should not be used in custom code (e.g. to style the markup).

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_xf\_v2](https://www.adobe.com/go/aem_cmp_xf_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_xf](https://www.adobe.com/go/aem_cmp_library_xf)
* **Authors**: [Burkhard Pauli](https://github.com/bpauli)
* **Co-authors**: [Jean-Christophe Kautzmann](https://github.com/jckautzmann)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._
