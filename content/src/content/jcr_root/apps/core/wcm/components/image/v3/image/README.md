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
Image (v3) Work in progress, do not use in production
====
Image component written in HTL that renders an adaptive image.

## Features
* Smart loading of optimal rendition
* In-place editing, cropping, rotating, resizing and image map definition
* Responsive image map resizing
* Image title, description, accessibility text and link
* SVG support
* Styles
* Dynamic Media images support, including Image Presets and Smart Crop

### Use Object
The Image component uses the `com.adobe.cq.wcm.core.components.models.Image` Sling Model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./allowedRenditionWidths` - defines the allowed renditions (as an integer array) that will be generated for the images rendered by this
component; the actual size will be requested by the client device;
2. `./jpegQuality` - defines the image quality for JPEGs (0 lowest quality / size to 100 highest quality / size). Default value is 82.
3. `./disableLazyLoading` - if `true`, the lazy loading of images (loading only when the image is visible on the client
device) is disabled.
4. `./lazyThreshold` - defines the number of pixel an image is getting loaded before it gets visible and lazy loading is enabled. 
Default is set to 0, meaning that the native threshold of the browser will be used if the browser supports native lazy loading functionality.
For the browsers without native lazy loading support, the default threshold provided by the [vanilla-lazyload](https://github.com/verlok/vanilla-lazyload) library will be used, which is 300.
If any other value than the default one is configured, that value will be used to define the number of pixel an image is getting loaded before it gets visible.
5.  `./enableDmFeatures` - if `true`, Dynamic Media features are enabled.

### Edit Dialog Properties
The following properties are written to JCR for this Image component and are expected to be available as `Resource` properties:

1. `./fileReference` property or `file` child node - will store either a reference to the image file, or the image file
1. `./isDecorative` - if set to `true`, then the image will be ignored by assistive technology
1. `./alt` - defines the value of the HTML `alt` attribute (not needed if `./isDecorative` is set to `true`)
1. `./altValueFromDAM` - if `true`, the HTML `alt` attribute is inherited from the DAM asset.
1. `./linkURL` - allows defining a URL to which the image will link to.  
1. `./width` - allows defining a HTML `width` attribute, useful for browser to calculate the aspect ratio of the image, preventing the layout shifts
1.  `./height` - allows defining a HTML `height` attribute, useful for browser to calculate the aspect ratio of the image, preventing the layout shifts
1. `./jcr:title` - defines the value of the HTML `title` attribute or the value of the caption, depending on the value of
`./displayPopupTitle`
1. `./displayPopupTitle` - if set to `true` it will render the value of the `./jcr:title` property through the HTML `title` attribute,
otherwise a caption will be rendered
1. `./id` - defines the component HTML ID attribute.
1. `./dmPresetType` - defines the type of Dynamic Media image rendering, possible values are `imagePreset`, `smartCrop`.
1. `./imagePreset` - defines the name for the Dynamic Media Image Preset to apply to the Dynamic Media image URL.
1. `./smartCropRendition` - defines how Dynamic Media Smart Crop image renders. `SmartCrop:Auto` means that the component will automatically select Smart Crop rendition which fits the container size better; the name of specific Smart Crop rendition will force the component to render that image rendition only.
1. `./imageModifiers` - defines additional Dynamic Media Image Serving commands separated by '&amp;'. Field gives complete flexibility to change Dynamic Media image rendering.
1. `./imageFromPageImage` - if `true`, the image is inherited from the featured image of either the linked page if `./linkURL` is set or the current page.
1. `./altValueFromPageImage` - if `true` and if `./imageFromPageImage` is `true`, the HTML `alt` attribute is inherited from the featured image of either the linked page if `./linkURL` is set or the current page.

## Extending from This Component
1. In case you overwrite the image's HTL script, make sure the necessary attributes for the JavaScript loading script are contained in the markup at the right position (see section below).
2. In case your own component does not only render an image but does also renders something else, use the following approach:
  1. `resourceSuperType` should be set to `core/wcm/components/image/v3/image` (to make sure the image rendering servlet is being used)
  2. Your HTL script should include the image markup via `<div class="cmp-image" data-sly-include="image.html"></div>`
  3. You derived component should reset `cq:htmlTags`
  4. You component's dialog should overwrite the dialog fully from the image component via `sling:hideResource="true"` on the node `cq:dialog/content/items/image`

## URL Formats
In case Dynamic Media features are not used the images are loaded through the `com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet`, therefore their URLs have the following patterns:

```
Author:
/content/<project_path>/<page_path>/<component_path>/<component_name>.coreimg.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>

Publish:
/content/<project_path>/<page_path>/<component_path>/<component_name>.coreimg.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>
```
When an image is a Dynamic Media asset and Dynamic Media features are enabled in component's policy the images are loaded from Dynamic Media Image Serving, the URL format differs depending on image rendering type chosen. In the case of 'Smart Crop':
```
Author:
/is/image/<company>/<assetId><:smart crop rendition>?ts=<timestamp>&<image modifiers>

Publish:
<DM publish server>/is/image/<company>/<assetId><:smart crop rendition>?ts=<timestamp>&<image modifiers>
```
In the case of 'Image preset':
```
Author:
/is/image/<company>/<assetId>?qlt=<quality>&wid=<width>&ts=<timestamp>&$<image_preset>$&<image_modifiers>

Publish:
<dm_publish_server>/is/image/<company>/<assetId>?qlt=<quality>&wid=<width>&ts=<timestamp>&$<image_preset>$&<image_modifiers>
```

## Client Libraries
The component reuses the `core.wcm.components.image.v2` client library category that contains a recommended base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

It also reuses the `core.wcm.components.image.v2.editor` editor client library category that includes JavaScript
handling for dialog interaction. It is already included by its edit dialog.

## BEM Description
```
BLOCK cmp-image
    ELEMENT cmp-image__link
    ELEMENT cmp-image__image
    ELEMENT cmp-image__title
```

## JavaScript Data Attribute Bindings
Apply a `data-cmp-is="image"` attribute to the wrapper block to enable initialization of the JavaScript component.

The following attributes can be added to the same element to provide options:

1. `data-cmp-lazy` - if not `false`, indicates that the image should be rendered lazily.
2. `data-cmp-src` - the image source. Can be a simple image source, or a URI template representation that can be variable expanded -
useful for building an image configuration with an alternative width. Should contain a `{.width}` variable.
e.g. '/path/to/image.coreimg{.width}.jpeg'
3. `data-cmp-dmimage` - if not `false`, indicates that the image is DM image.

A hook attribute from the following should be added to the corresponding element so that the JavaScript is able to target it:

```
 data-cmp-hook-image="image"
 data-cmp-hook-image="link"
 data-cmp-hook-image="map"
 data-cmp-hook-image="area"
```

To allow lazy loading it is expected that the `data-cmp-lazy` option is supplied.
Hybrid lazy loading is supported, by using this library [vanilla-lazyload](https://github.com/verlok/vanilla-lazyload).
Hybrid lazy loading is a technique which provides native lazy loading on browsers that support it, otherwise the custom lazy loading implementation provided by the library is used.
If any other value than the default one is configured for `./lazyThreshold`, the custom lazy loading will be used by all browsers, because the native threshold of the browsers cannot be controlled.

If there are alternative widths (`./allowedRenditionWidths`) defined in the Component Policy Configuration, the `srcset` attribute will be constructed and set to the `<img>`.
In this way the the browser will figure out based on its native adaptive capabilities which image to load from the `srcset` attribute in relation with the viewport width.

The `data-cmp-widths` option must be provided with more than one width, as well as the `data-cmp-src` option,
with a URI template representation of the source.

To allow responsive recalculation of image map areas, a `data-cmp-relcoords` attribute should be added to each map `area`. The coordinates
are represented as comma-separated decimal percentages:

```
    <area shape="rect" coords="0,0,10,10" data-cmp-relcoords="0,0,0.5,0.5" href="http://www.adobe.com">
```

## SVG
SVG MIME-types are supported, but have some specific handling. Alternative smart image widths defined at the component policy dialog are ignored for SVG images, with `Image#getWidths` returning an empty array.
In addition, SVG image types have a more limited set of editing options available in the AEM inline image editor. The lazy loading feature is still supported for SVG images.

## Information
* **Vendor**: Adobe
* **Version**: v3
* **Compatibility**: AEM 6.5
* **Status**: work-in-progress
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_image\_v3](https://www.adobe.com/go/aem_cmp_image_v3)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_image](https://www.adobe.com/go/aem_cmp_library_image)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann), [Vlad Coman](https://github.com/comanV)
