<!--
Copyright 2019 Adobe

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

Content Fragment (v1)
====
Content Fragment component written in HTL that displays the elements of a Content Fragment or a selection thereof.

## Features
* Displays the elements of a Content Fragment as an HTML description list
* By default renders all elements of a Content Fragment
* Can be configured to render a subset of the elements in a specific order
* Alternative Content Fragment variations are configurable

### Use Object
The Content Fragment component uses the `com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment` Sling model as its Use-object.

### Edit dialog properties
The following JCR properties are used:

1. `./fragmentPath` - defines the path to the Content Fragment to be rendered
2. `./variationName` - defines the variation to use to render the elements (optional: if not present, the master variation is used)
3. `./elementNames` - multi-valued property defining the elements to be rendered and in which order (optional: if not present, all elements are rendered)
4. `./paragraphScope` - defines if all or a range of paragraphs are to be rendered (only used in paragraph mode)
5. `./paragraphRange` - defines the range(s) of paragraphs to be rendered (only used in paragraph mode and if paragraphs are restricted to ranges)
6. `./paragraphHeadings` - defines if headings should count as paragraphs (only used in paragraph mode and if paragraphs are restricted to ranges)
7. `./id` - defines the component HTML ID attribute.

## BEM description
```
BLOCK cmp-contentfragment
  MOD cmp-contentfragment--<name>
    ELEMENT cmp-contentfragment__title
    ELEMENT cmp-contentfragment__description
    ELEMENT cmp-contentfragment__elements
    ELEMENT cmp-contentfragment__element
        MOD cmp-contentfragment__element--<name>
    ELEMENT cmp-contentfragment__element-title
    ELEMENT cmp-contentfragment__element-value
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_contentfragment\_v1](https://www.adobe.com/go/aem_cmp_contentfragment_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_cf](https://www.adobe.com/go/aem_cmp_library_cf)
