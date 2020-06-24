<!--
Copyright 2017 Adobe

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
Text (v2)
====
Text component written in HTL that provides a section of rich text.

## Features

* In-place editing
* Rich text editor
* Styles

### Use Object
The Title component uses the `com.adobe.cq.wcm.core.components.models.Text` Sling model as its Use-object. The current implementation reads
the following resource properties:

1. `./text` - the actual text to be rendered
2. `./textIsRich` - flag determining if the rendered text is rich or not, useful for applying the correct HTL display context
3. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-text
    ELEMENT cmp-text__paragraph
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_text\_v2](https://www.adobe.com/go/aem_cmp_text_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_text](https://www.adobe.com/go/aem_cmp_library_text)
