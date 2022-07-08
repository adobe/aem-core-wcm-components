<!--
Copyright 2021 Adobe

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
Teaser (v2)
====
Teaser component written in HTL, allowing definition of an image, title, rich text description and actions/links.
Teaser variations can include some or all of these elements.

## Features
* Combines image, title, rich text description and actions/links.
* Allows disabling of teaser elements through policy configuration.
* Allows control over whether title and description should be inherited from a linked page.

### Use Object
The Teaser component uses the `com.adobe.cq.wcm.core.components.models.Teaser` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./actionsDisabled` - defines whether or not Call-to-Actions are disabled
2. `./pretitleHidden` - defines whether or not the pretitle is hidden
3. `./titleHidden` - defines whether or not the title is hidden
4. `./descriptionHidden` - defines whether or not the description is hidden
5. `./titleType` - stores the value for this title's HTML element type
6. `./showTitleType` - defines whether or not the title tab dropdown menu is shown

The following configuration properties are inherited from the image component:

1. `./allowedRenditionWidths` - defines the allowed renditions (as an integer array) that will be generated for the images rendered by this
component; the actual size will be requested by the client device
2. `./disableLazyLoading` - if `true`, the lazy loading of images (loading only when the image is visible on the client
device) is disabled

### Edit Dialog Properties
The following properties are written to JCR for this Teaser component and are expected to be available as `Resource` properties:

1. `./actionsEnabled` - property that defines whether or not the teaser has Call-to-Action elements
1. `./actions` - child node where the Call-to-Action elements are stored as a list of `item` nodes with the following properties
    1. `link` - property that stores the Call-to-Action link
    1. `text` - property that stores the Call-to-Action text
1. `./fileReference` - property or `file` child node - will store either a reference to the image file, or the image file
1. `./linkURL` - link applied to teaser elements. URL or path to a content page
1. `./pretitle` - defines the value of the teaser pretitle
1. `./jcr:title` - defines the value of the teaser title and HTML `title` attribute of the teaser image
1. `./titleFromPage` - defines whether or not the title value is taken from the linked page
1. `./jcr:description` - defines the value of the teaser description
1. `./descriptionFromPage` - defines whether or not the description value is taken from the linked page
1. `./id` - defines the component HTML ID attribute.
1. `./titleType` - stores the value for this title's HTML element type
1. `./isDecorative` - if set to `true`, then the image will be ignored by assistive technology
1. `./alt` - defines the value of the HTML `alt` attribute (not needed if `./isDecorative` is set to `true`)
1. `./altValueFromDAM` - if `true`, the HTML `alt` attribute is inherited from the DAM asset.
1. `./imageFromPageImage` - if `true`, the image is inherited from the featured image of either the linked page if `./linkURL` is set or the current page.
1. `./altValueFromPageImage` - if `true` and if `./imageFromPageImage` is `true`, the HTML `alt` attribute is inherited from the featured image of either the linked page if `./linkURL` is set or the current page.

### Extending the Teaser Component
When extending the Teaser component by using `sling:resourceSuperType`, developers need to define the `imageDelegate` property for
the proxy component and point it to the designated Image component.

For example:
```
imageDelegate="core/wcm/components/image/v3/image"
```

## Client Libraries
The component reuses the following editor client library categories that include JavaScript
handling for dialog interaction. They are already included by its edit dialog:
* `core.wcm.components.teaser.v1.design`
* `core.wcm.components.teaser.v2.editor`
* `core.wcm.components.image.v3.editor`

## BEM Description
```
BLOCK cmp-teaser
    ELEMENt cmp-teaser__link
    ELEMENT cmp-teaser__image
    ELEMENT cmp-teaser__content
    ELEMENT cmp-teaser__pretitle
    ELEMENT cmp-teaser__title
    ELEMENT cmp-teaser__title-link
    ELEMENT cmp-teaser__description
    ELEMENT cmp-teaser__action-container
    ELEMENT cmp-teaser__action-link
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_teaser\_v2](https://www.adobe.com/go/aem_cmp_teaser_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_teaser](https://www.adobe.com/go/aem_cmp_library_teaser)
