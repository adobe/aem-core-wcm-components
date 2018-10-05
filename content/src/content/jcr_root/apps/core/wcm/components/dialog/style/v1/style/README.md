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
Dialog Style Selector (v1)
====
Component for TouchUI authoring dialogs written in HTL that adds the style system options from a component's policy to the component dialog.

## Features
* Consolidates authoring options
* Checkboxes for multiple-selection styles
* Radio buttons for single-selection styles

### Use Object
The Image component uses the `com.adobe.cq.wcm.core.components.models.Image` Sling Model as its Use-object.

## Client Libraries
The component provides a `core.wcm.components.image.v1` client library category that contains a recommended base
CSS styling. It should be added to a relevant site client library using the `embed` property.

It also provides a `core.wcm.components.image.v1.editor` editor client library category that includes
JavaScript handling for dialog interaction. It is already included by its edit dialog.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_image\_v1](https://www.adobe.com/go/aem_cmp_image_v1)

