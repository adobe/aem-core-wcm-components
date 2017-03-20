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
Image (v1)
====
Image component written in HTL that renders a smart adaptive image.

## Features
* Smart loading of optimal rendition
* In-place editing, cropping, rotating, and resizing
* Image title, description, accessibility text and link
* Styles

### Use Object
The Image component uses the `com.adobe.cq.wcm.core.components.models.Image` Sling Model as its Use-object.

### Component policy configuration properties
The following configuration properties are used:

1. `./allowedWidths` - defines the allowed renditions (as an integer array) that will be generated for the images rendered by this
component; the actual size will be requested by the client device;
2. `./disableLazyLoading` - allows to disable lazy loading for images (loading the image only when the image is visible on the client
device)

### Edit dialog properties
The following properties are written to JCR for this Image component and are expected to be available as `Resource` properties:

1. `./fileReference` property or `file` child node - will store either a reference to the image file, or the image file
2. `./isDecorative` - if set to `true`, then the image will be ignored by assistive technology
3. `./alt` - defines the value of the HTML `alt` attribute (not needed if `./isDecorative` is set to `true`)
4. `./linkURL` - allows defining a URL to which the image will link to
5. `./jcr:title` - defines the value of the HTML `title` attribute or the value of the caption, depending on the value of
`./displayPopupTitle`
6. `./displayPopupTitle` - if set to `true` it will render the value of the `./jcr:title` property through the HTML `title` attribute,
otherwise a caption will be rendered

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_image\_v1](https://www.adobe.com/go/aem_cmp_image_v1)

