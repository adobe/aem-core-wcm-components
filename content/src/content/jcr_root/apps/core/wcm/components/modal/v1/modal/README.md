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
Modal (v1)
====
Modal component written in HTL that renders a configurable modal view for the content.

## Features
* Displays Content Fragment & Experience Fragment in Modal View.
* Supports Modal View on page load & on click of any link.

### Use Object
The Modal component uses the `com.adobe.cq.wcm.core.components.models.Modal` Sling model as its Use-object.


### Edit Dialog Properties
The following properties are written to JCR for this Modal component and are expected to be available as `Resource` properties:

1. `./modalId` - represents the hash generated for the component path. Field is non-editable.
2. `./pagePath` - allows to select page path which is to be shown in modal view.
3. `./showModalByDefault` - allows you to enable/disable viewing of current modal on page load without #modalId.

## Client Libraries
The component provides a `core.wcm.components.modal.v1` client library category that contains a recommended base
CSS styling. It should be added to a relevant site client library using the `embed` property.


## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.4
* **Status**: production-ready

