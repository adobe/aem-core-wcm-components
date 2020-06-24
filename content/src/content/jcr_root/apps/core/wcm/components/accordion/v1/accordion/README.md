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
Accordion (v1)
====
Accordion component written in HTL.

## Features

* Allows addition of accordion items of varying resource type.
* Allowed components can be configured through policy configuration.
* Toggle accordion panels from accordion header controls.
* Ability to force a single panel to be displayed.
* Items expanded by default are configurable.
* Item header HTML element is configurable (`h2` - `h6`, `h1` is omitted for SEO reasons).
* Editing features for accordion items (adding, removing, editing, re-ordering).
* Allows deep linking into a specific panel by passing the panel id as the URL fragment

### Use Object
The Accordion component uses the `com.adobe.cq.wcm.core.components.models.Accordion` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./allowedHeadingElements` - the heading elements (`h2` - `h6`, `h1` is omitted for SEO reasons) that are allowed to be selected in the edit dialog.
2. `./headingElement` - the default heading element (`h2` - `h6`, `h1` is omitted for SEO reasons) to use for the accordion headers.

It is also possible to define the allowed components for the Accordion.

### Edit Dialog Properties
The following properties are written to JCR for this Accordion component and are expected to be available as `Resource` properties:

1. `./singleExpansion` - `true` if one panel should be forced to be expanded at a time, `false` otherwise.
2. `./expandedItems` - defines the names of the items that are expanded by default.
3. `./headingElement` - defines the heading element to use for the accordion headers (`h2` - `h6`).
4. `./id` - defines the component HTML ID attribute.

The edit dialog also allows editing of Accordion items (adding, removing, naming, re-ordering).

## Client Libraries
The component provides a `core.wcm.components.accordion.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

It also provides a `core.wcm.components.accordion.v1.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit and policy dialogs.

## BEM Description
```
BLOCK cmp-accordion
    ELEMENT cmp-accordion__item
    ELEMENT cmp-accordion__header
    ELEMENT cmp-accordion__button
        MOD cmp-accordion__button--expanded
    ELEMENT cmp-accordion__title
    ELEMENT cmp-accordion__icon
    ELEMENT cmp-accordion__panel
        MOD cmp-accordion__panel--expanded
        MOD cmp-accordion__panel--hidden
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="accordion"` attribute to the wrapper block to enable initialization of the JavaScript component.

The following attributes can be added to the same element to provide options:

1. `data-cmp-single-expansion` - if the attribute is present, forces a single panel to be expanded at a time.

The following attributes can be added to the accordion item (`data-cmp-hook-accordion="item"`):

1. `data-cmp-expanded` - if the attribute is present, indicates that the item should be initially expanded.

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-accordion="item"
data-cmp-hook-accordion="button"
data-cmp-hook-accordion="panel"
```

### Enabling Accordion Editing Functionality
The following properties and child nodes are required in the proxy component to enable full editing functionality for the Accordion:

1. `./cq:isContainer` - set to `{Boolean}true`, marks the Accordion as a container component
2. `./cq:editConfig` - `afterchilddelete`, `afterchildinsert` and `afterchildmove` listeners should be provided via
the edit configuration of the proxy. `_cq_editConfig.xml` contains the recommended actions and can be copied to the proxy component.

The default Accordion site Client Library provides a handler for message requests between the editor and the Accordion.
If the built-in Client Library is not used, a message request handler should be registered:
```
new Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
    if (message.data && message.data.type === "cmp-accordion" && message.data.id === myAccordionHTMLElement.dataset["cmpPanelcontainerId"]) {
        if (message.data.operation === "navigate") {
            // handle navigation
        }
    }
});
```

The handler should subscribe to a `cmp.panelcontainer` message that allows routing of a `navigate` operation to ensure
that the UI component is updated when the active item is switched in the editor layer.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_accordion\_v1](https://www.adobe.com/go/aem_cmp_accordion_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_accordion](https://www.adobe.com/go/aem_cmp_library_accordion)
* **Author**: [Brandon M. Maynard](https://github.com/brandonmaynard)
* **Co-authors**: [Richard Hand](https://github.com/richardhand)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._
