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
Teaser (v1 - sandbox)
====
Teaser component written in HTL.

## Features

### Use Object
The Teaser component uses the `com.adobe.cq.wcm.core.components.sandbox.models.Teaser` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are inherited from the image component:

1. `./allowedWidths` - defines the allowed renditions (as an integer array) that will be generated for the images rendered by this
component; the actual size will be requested by the client device;
2. `./disableLazyLoading` - allows to disable lazy loading for images (loading the image only when the image is visible on the client
device)

### Edit Dialog Properties
The following properties are written to JCR for this Teaser component and are expected to be available as `Resource` properties:

1. `./fileReference` property or `file` child node - will store either a reference to the image file, or the image file
2. `./linkURL` - required URL to which the teaser will link to
3. `./linkText` - Call to Action link text
4. `./jcr:title` - defines the value of the teaser title and HTML `title` attribute of the teaser image
5. `./jcr:description` - defines the value of the teaser description 

## BEM Description
```
BLOCK cmp-teaser
    ELEMENT cmp-teaser__image
    ELEMENT cmp-teaser__title
    ELEMENT cmp-teaser__title-link
    ELEMENT cmp-teaser__description
    ELEMENT cmp-teaser__description-link    
```

## Information
* **Vendor**: Adobe
* **Version**: v1 - sandbox
* **Compatibility**: AEM 6.3
* **Status**: preview
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_teaser\_v1](https://www.adobe.com/go/aem_cmp_teaser_v1)

