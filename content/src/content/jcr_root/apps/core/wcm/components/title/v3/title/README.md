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
Title (v3)
====
Title component written in HTL, allowing to define a section heading.

## Features

* In-place editing
* Automatic reading of the page title from the current page, if no title text is defined
* HTML element configuration (`h1` - `h6`)
* Linkable to content pages, external URLs or page anchors
* Styles

### Use Object
The Title component uses the `com.adobe.cq.wcm.core.components.models.Title` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./type` - defines the default HTML heading element type (`h1` - `h6`) this component will use for its rendering
2. `./linkDisabled` - defines whether or not the title link is disabled

### Edit Dialog Properties
The following properties are written to JCR for this Title component and are expected to be available as `Resource` properties:

1. `./jcr:title` - will store the text of the title to be rendered
2. `./type` - will store the HTML heading element type which will be used for rendering; if no value is defined, the component will fallback
to the value defined by the component's policy
3. `./linkURL` - will allow definition of a content page path, external URL or page anchor for linking the title.
4. `./id` - defines the component HTML ID attribute.
5. `./linkAccessibilityLabel` - defines an accessibility label for the the title's link.
6. `./linkTitleAttribute` - defines a title attribute for the the title's link.

## Client Libraries
The component leverages `core.wcm.components.title.v2.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit and design dialogs.

## BEM Description
```
BLOCK cmp-title
    ELEMENT cmp-title__text
    ELEMENT cmp-title__link
```

## Information
* **Vendor**: Adobe
* **Version**: v3
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_title\_v3](https://www.adobe.com/go/aem_cmp_title_v3)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_title](https://www.adobe.com/go/aem_cmp_library_title)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann)
