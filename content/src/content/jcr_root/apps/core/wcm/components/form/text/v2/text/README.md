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
Form Text (v2)
====
Text form field component written in HTL.

## Features

* Provides the following type of input:
  * text
  * textarea
  * email
  * telephone
  * date
  * number
  * password
* Custom constraint messages for the above types

### Use Object
The Form Text component uses the `com.adobe.cq.wcm.core.components.models.form.Text` Sling Model for its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for this Form Text component and are expected to be available as `Resource` properties:

1. `./type` - defines the type of text this field provides; possible values: `text`, `textarea`, `email`, `tel`, `date`, `number`,
`password`
2. `./rows` - defines the number of text lines available in this input field
3. `./jcr:title` - defines the label to use for this field
3. `./hideTitle` - if set to `true`, the label of this field will be hidden
4. `./name` - defines the name of the field, which will be submitted with the form data
5. `./value` - defines the default value of the field
6. `./helpMessage` - defines a help message that can be rendered in the field as a hint for the user
7. `./usePlaceholder` - if set to `true`, the help message will be displayed inside the form input if the field is empty and not focused
8. `./constraintMessage` - defines the message displayed as tooltip when submitting the form if the value does not validate the chosen type
9. `./required` - if set to `true`, this field will be marked as required, not allowing the form to be submitted until the field has a value
10. `./requiredMessage` - defines the message displayed as tooltip when submitting the form if the value is left empty
11. `./readOnly` - if set to `true`, the filed will be read only
12. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component provides a `core.wcm.components.form.text.v2` client library category that contains a JavaScript component.
It should be added to a relevant site client library using the `embed` property.

It also provides a `core.wcm.components.form.text.v2.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit dialog.

## BEM Description
```
BLOCK cmp-form-text
    ELEMENT cmp-form-text__help-block
    ELEMENT cmp-form-text__textarea
    ELEMENT cmp-form-text__text
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="formText"` attribute to the wrapper block to enable initialization of the JavaScript component.

The following attributes can be added to the same element to provide options:

1. `data-cmp-constraint-message` - populated with `constraintMessage` from the component's edit dialog
2. `data-cmp-required-message` - populated with  `requiredMessage` from the component's edit dialog

A `data-cmp-hook-form-text="input"` attribute should be added to the input field or textarea so that the JavaScript is able to target it.

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_form\_text\_v2](https://www.adobe.com/go/aem_cmp_form_text_v2)

