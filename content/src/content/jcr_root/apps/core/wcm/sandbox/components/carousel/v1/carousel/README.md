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
Carousel (v1)
====
Carousel component written in HTL.

## Features

* Allows addition of slide components of varying resource type.
* Allowed components can be configured through policy configuration.
* Carousel navigation via next/previous and position indicators.
* Editing features for slides (adding, removing, editing, re-ordering).

### Use Object
The Carousel component uses the `com.adobe.cq.wcm.core.components.models.Carousel` Sling model as its Use-object.

### Component Policy Configuration Properties
The component policy dialog allows definition of allowed components for the Carousel.

### Edit Dialog Properties
The edit dialog allows editing of Carousel slides (adding, removing, naming, re-ordering).

## Client Libraries
The component provides a `core.wcm.components.carousel.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-carousel
    ELEMENT cmp-carousel__content
    ELEMENT cmp-carousel__item
    ELEMENT cmp-carousel__action
        MOD cmp-carousel__action--prev
        MOD cmp-carousel__action--next
    ELEMENT cmp-carousel__action-icon
    ELEMENT cmp-carousel__action-text
    ELEMENT cmp-carousel__indicators
    ELEMENT cmp-carousel__indicator
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="carousel"` attribute to the wrapper block to enable initialization of the JavaScript component.

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-carousel="item"
data-cmp-hook-carousel="prev"
data-cmp-hook-carousel="next"
data-cmp-hook-carousel="indicator"
```

### Enabling Carousel Editing Functionality
The following properties and child nodes are required in the proxy component to enable full editing functionality for the Carousel:

1. `./cq:isContainer` - set to `true`, marks the Carousel as a container component
2. `./cq:editConfig` - `afterchilddelete`, `afterchildinsert` and `afterchildmove` listeners should be provided via
the edit configuration of the proxy. `_cq_editConfig.xml` contains the recommended actions and can be copied to the proxy component.

The default Carousel site Client Library provides a handler for message requests between the editor and the Carousel.
If the built-in Client Library is not used, a message request handler should be registered:
```
new Granite.author.MessageChannel("cqauthor", window).subscribeRequestMessage("cmp.panelcontainer", function(message) {
    if (message.data && message.data.type === "cmp-carousel" && message.data.id === myCarouselHTMLElement.dataset["cmpPanelcontainerId"]) {
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
* **Status**: preview

