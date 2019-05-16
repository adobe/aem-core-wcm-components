<!--
Copyright 2018 Adobe Systems Incorporated

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

* Allows addition of accordion item components of varying resource type.
* Allowed components can be configured through policy configuration.
* Navigate the accordion items by expanding/collapsing the items.
* Editing features for accordion items (adding, removing, editing, re-ordering).

### Use Object
The Accordion component uses the `com.adobe.cq.wcm.core.components.models.Accordion` Sling model as its Use-object.

### Component Policy Configuration Properties
The component policy dialog allows definition of allowed components for the Accordion.

### Edit Dialog Properties
The following properties are written to JCR for this Accordion component and are expected to be available as `Resource` properties:

1. `./expandedItem` - defines the name of the item that is expanded by default.

The edit dialog also allows editing of Accordion items (adding, removing, naming, re-ordering).

## Client Libraries
The component provides a `core.wcm.components.accordion.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-accordion
    ELEMENT cmp-accordion__tablist
    ELEMENT cmp-accordion__item
        MOD cmp-accordion__item--expanded
    ELEMENT accordion__item--title
    ELEMENT accordion__item--title--icon
    ELEMENT cmp-accordion__itempanel
        MOD cmp-accordion__itempanel--expanded
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="accordion"` attribute to the wrapper block to enable initialization of the JavaScript component.

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-accordion="item"
data-cmp-hook-accordion="itempanel"
```

### Enabling Accordion Editing Functionality
The following properties and child nodes are required in the proxy component to enable full editing functionality for the Accordion:

1. `./cq:isContainer` - set to `true`, marks the Accordion as a container component
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
