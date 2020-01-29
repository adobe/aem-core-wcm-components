<!--
Copyright 2019 Adobe Systems Incorporated

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
ImageList (v1)
====
ImageList component written in HTL that renders a configurable collection of items or content.

## Features
* List images
* Styles

### Use Object
The List component uses the `com.adobe.cq.wcm.core.components.models.ImageList` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for this Image List component and are expected to be available as `Resource` properties:

1. `./linkURL` - defines the link url for the image
2. `./fileReference` - defines the image path from dam
3. `./linkText` - defines the link text for the image
4. `./sling:resourceType` - allows this node to be used as synthetic image component

### Extending the Imagelist Component
When extending the Imagelist component by using `sling:resourceSuperType`, developers need to define the `imageDelegate` property for
the proxy component and point it to the designated Image component.

For example:
```
imageDelegate="core/wcm/components/image/v2/image"
```

## BEM Description
```
BLOCK cmp-image-list
    ELEMENT cmp-image-list__item
    ELEMENT cmp-imagelist__item
    ELEMENT cmp-imagelist__image
    ELEMENT cmp-imagelist__item-title
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_imagelist\_v1](https://www.adobe.com/go/aem_cmp_imagelist_v1)
* **Component Library**: [http://opensource.adobe.com/aem-core-wcm-components/library/imagelist.html](http://opensource.adobe.com/aem-core-wcm-components/library/imagelist.html)
