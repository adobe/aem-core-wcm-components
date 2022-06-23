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
Image (v1)
====
Image component written in HTL that renders a smart adaptive image.

## Features
* Smart loading of optimal rendition
* In-place editing, cropping, rotating, and resizing
* Image title, description, accessibility text and link
* SVG support
* Styles

### Use Object
The Image component uses the `com.adobe.cq.wcm.core.components.models.Image` Sling Model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./allowedWidths` - defines the allowed renditions (as an integer array) that will be generated for the images rendered by this
component; the actual size will be requested by the client device;
2. `./jpegQuality` - defines the image quality for JPEGs (0 lowest quality / size to 100 highest quality / size). Default value is 82.
3. `./disableLazyLoading` - allows to disable lazy loading for images (loading the image only when the image is visible on the client
device)
4. `./enableAssetDelivery` - If `true`, assets will be delivered through the Asset Delivery system (based on Dynamic Media for AEMaaCS). This will also enable optimizations based on
   [content negotiation](https://developer.mozilla.org/en-US/docs/Web/HTTP/Content_negotiation). Currently, this optimization is available only for webp.

### Edit Dialog Properties
The following properties are written to JCR for this Image component and are expected to be available as `Resource` properties:

1. `./fileReference` property or `file` child node - will store either a reference to the image file, or the image file
2. `./isDecorative` - if set to `true`, then the image will be ignored by assistive technology
3. `./alt` - defines the value of the HTML `alt` attribute (not needed if `./isDecorative` is set to `true`)
4. `./linkURL` - allows defining a URL to which the image will link to
5. `./jcr:title` - defines the value of the HTML `title` attribute or the value of the caption, depending on the value of
`./displayPopupTitle`
6. `./displayPopupTitle` - if set to `true` it will render the value of the `./jcr:title` property through the HTML `title` attribute,
otherwise a caption will be rendered
7. `./id` - defines the component HTML ID attribute.

## Rendering Logic

The HTML markup for the image component looks like this

```
<div class="cmp cmp-image">
  <div class="cq-dd-image"><!-- this surrounding "div" is only rendered for wcmmode!=disabled -->
    <a href="..." class="cmp-image--link"><!-- this surrounding "a" is only rendered for images with a link -->
      <noscript data-cmp-image="...">
        <img src="..." alt="..." title="..."/><!-- used as fallback in case no javascript is enabled or in case no image widths are configured in the component's policy -->
      </noscript>
    </a>
  </div>
</div>
```

There is some javascript logic bound to this markup which extracts the image urls from the `data-cmp-image` JSON attribute. Then the most appropriate url is being picked and ends up in a dynamically added `img` element. The most appropriate url is the image url specifying the server rendition with the closest width which is at least as wide as the image's container! The new `img` element is placed in the DOM as first child below the container with class `cmp-image`.

### data-cmp-image Attribute Format
The following JSON format is expected in the attribute `data-cmp-image` of the `noscript` element.

```
{
  smartImages: [<image urls per width given in smartSizes>],
  smartSizes: [<image widths from component's policy configuration>],
  lazyEnabled: <false in case disableLazyLoading from the component's policy configuration is set to true, otherwise true>
}
```

### Necessary Attributes for the JavaScript Logic
1. `cmp-image` class attribute is necessary to select the right container below which to create the new `img` element. Make sure to manually place this container in the markup of composed components (`data-sly-resource` with a `resourceType` override).
2. `data-cmp-image` attribute must contain all necessary image URLs in the format described above.

## Extending from This Component

1. In case you overwrite the image's HTL script, make sure the necessary attributes for the JavaScript loading script are contained in the markup at the right position (see section above).
2. In case your own component does not only render an image but does also render something else, use the following approach
  1. `resourceSuperType` should be set to `core/wcm/components/image/v1/image` (to make sure the image rendering servlet is being used)
  2. Your HTL script should include the image markup via `<div class="cmp-image" data-sly-include="image.html"></div>`
  3. You derived component should reset `cq:htmlTags`
  4. You component's dialog should overwrite the dialog fully from the image component via `sling:hideResource="true"` on the node `cq:dialog/content/items/image`

## URL Formats
The images are loaded through the `com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet`, therefore their URLs have the following patterns:

```
Author:
/content/<project_path>/<page_path>/<component_path>/<component_name>.img.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>

Publish:
/content/<project_path>/<page_path>/<component_path>/<component_name>.img.<quality>.<width>.<extension>/<timestamp>/<filename>.<extension>
```

## Client Libraries
The component provides a `core.wcm.components.image.v1` client library category that contains a recommended base
CSS styling. It should be added to a relevant site client library using the `embed` property.

It also provides a `core.wcm.components.image.v1.editor` editor client library category that includes
JavaScript handling for dialog interaction. It is already included by its edit dialog.

## SVG
SVG MIME-types are supported, but have some specific handling. Alternative smart image widths defined at the component policy dialog are ignored for SVG images, with `Image#getWidths` returning an empty array.
In addition, SVG image types have a more limited set of editing options available in the AEM inline image editor. The lazy loading feature is still supported for SVG images.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_image\_v1](https://www.adobe.com/go/aem_cmp_image_v1)

