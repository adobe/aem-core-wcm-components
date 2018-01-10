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
Title (v2)
====
Title component written in HTL, allowing to define a section heading.

## Features

* In-place editing
* HTML element configuration (`h1` - `h6`)
* Styles

### Use Object
The Title component uses the `com.adobe.cq.wcm.core.components.models.Title` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./type` - defines the default HTML heading element type (`h1` - `h6`) this component will use for its rendering

### Edit Dialog Properties
The following properties are written to JCR for this Title component and are expected to be available as `Resource` properties:

1. `./jcr:title` - will store the text of the title to be rendered
2. `./type` - will store the HTML heading element type which will be used for rendering; if no value is defined, the component will fallback
to the value defined by the component's policy

## Client Libraries
The component provides a `core.wcm.components.title.v2.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit and design dialogs.

## BEM Description
```
BLOCK cmp-title
    ELEMENT cmp-title__text
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_title\_v2](https://www.adobe.com/go/aem_cmp_title_v2)
