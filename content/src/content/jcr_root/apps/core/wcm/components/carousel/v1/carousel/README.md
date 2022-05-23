<!--
Copyright 2018 Adobe

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

* Allows addition of Carousel item components of varying resource type.
* Allowed components can be configured through policy configuration.
* Carousel navigation via next/previous and position indicators.
* Carousel autoplay with: 
  * Configurable delay.
  * Ability to disable automatic pause on hover.
  * Pause/play buttons.
  * Automatic pausing when the document is hidden, making use of the [Page Visibility API](https://developer.mozilla.org/en-US/docs/Web/API/Page_Visibility_API).
* Editing features for items (adding, removing, editing, re-ordering).
* Allows deep linking into a specific panel by passing the panel id as the URL fragment

### Use Object
The Carousel component uses the `com.adobe.cq.wcm.core.components.models.Carousel` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./autoplay` - defines whether or not the carousel should automatically transition between slides.
2. `./delay` - defines the delay (in milliseconds) when automatically transitioning between slides.
3. `./autopauseDisabled` - defines whether or not automatic pause when hovering the carousel is disabled.
4. `./controlsPrepended` - defines whether the carousel controls should be arranged before the carousel items or not.

It is also possible to define the allowed components for the Carousel.

### Edit Dialog Properties
The following properties are written to JCR for this Carousel component and are expected to be available as `Resource` properties:

1. `./autoplay` - defines whether or not the carousel should automatically transition between slides.
2. `./delay` - defines the delay (in milliseconds) when automatically transitioning between slides.
3. `./autopauseDisabled` - defines whether or not automatic pause when hovering the carousel is disabled.
4. `./id` - defines the component HTML ID attribute.
5. `./accessibilityLabel` - defines an accessibility label for the carousel.

The edit dialog also allows editing of Carousel items (adding, removing, naming, re-ordering).

Note: on author instances automatic transitioning only works with the `wcmmode=disabled` URL parameter.

## Client Libraries
The component provides a `core.wcm.components.carousel.v1` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-carousel
    ELEMENT cmp-carousel__content
    ELEMENT cmp-carousel__item
    ELEMENT cmp-carousel__actions
    ELEMENT cmp-carousel__action
        MOD cmp-carousel__action--disabled
        MOD cmp-carousel__action--previous
        MOD cmp-carousel__action--next
        MOD cmp-carousel__action--pause
        MOD cmp-carousel__action--play
    ELEMENT cmp-carousel__action-icon
    ELEMENT cmp-carousel__action-text
    ELEMENT cmp-carousel__indicators
    ELEMENT cmp-carousel__indicator
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="carousel"` attribute to the wrapper block to enable initialization of the JavaScript component.

The following attributes can be added to the same element to provide options:

1. `data-cmp-autoplay` - if the attribute is present, indicates that the carousel should automatically transition between slides.
2. `data-cmp-delay` - the delay (in milliseconds) when automatically transitioning between slides.
3. `data-cmp-autopause-disabled` - if the attribute is present, indicates that automatically pausing the carousel on hover, is disabled. 

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
data-cmp-hook-carousel="item"
data-cmp-hook-carousel="previous"
data-cmp-hook-carousel="next"
data-cmp-hook-carousel="pause"
data-cmp-hook-carousel="play"
data-cmp-hook-carousel="indicators"
data-cmp-hook-carousel="indicator"
```

### Enabling Carousel Editing Functionality
The following properties and child nodes are required in the proxy component to enable full editing functionality for the Carousel:

1. `./cq:isContainer` - set to `{Boolean}true`, marks the Carousel as a container component
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
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_carousel\_v1](https://www.adobe.com/go/aem_cmp_carousel_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_carousel](https://www.adobe.com/go/aem_cmp_library_carousel)
