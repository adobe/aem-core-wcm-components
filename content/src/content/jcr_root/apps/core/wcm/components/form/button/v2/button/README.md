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
Form Button (v2)
====
Button component written in HTL.

## Features
* Provides support for regular and submit buttons

### Use Object
The Form Button component uses the `com.adobe.cq.wcm.core.components.models.form.Button` Sling Model for its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for this Form Button component and are expected to be available as `Resource` properties:

1. `./jcr:title` - defines the text displayed on the button; if none is provided, the text will default to the button type
2. `./name` - defines the name of the button, which will be submitted with the form data
3. `./value` - defines the value of the button, which will be submitted with the form data
4. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component provides a `core.wcm.components.form.button.v2.editor` editor client library category that includes
JavaScript handling for dialog interaction. It is already included by its edit and design dialogs.

## BEM Description
```
BLOCK cmp-form-button
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_form\_button\_v2](https://www.adobe.com/go/aem_cmp_form_button_v2)

