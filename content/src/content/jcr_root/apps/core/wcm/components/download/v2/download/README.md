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

Download (v2)
====
Download component written in HTL that displays a downloadable asset on the page.

## Features
* Displays an asset on the page for download with the following elements:
    * Title
    * Description
    * File Size, File Format, Filename
    * Action
* Optionally allows assets to be displayed inline in the browser rather than directly downloaded.
* Direct upload from a local file system is configurable.
* Download Servlet with support for conditional header information and Content-Disposition.
* Style System support.

### Use Object
The Download component uses the `com.adobe.cq.wcm.core.components.models.Download` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

2. `./allowUpload` - defines whether direct upload from a local file system is allowed.
3. `./titleType` - defines the HTML element to use for the download title.
4. `./displaySize` - defines whether the file size should be displayed.
5. `./displayFormat` - defines whether the file format should be displayed.
6. `./displayFilename` - defines whether the filename should be displayed.
7. `./id` - defines the component HTML ID attribute.

### Edit Dialog Properties
The following JCR properties are used:

1. `./fileReference` - defines the path to the asset from DAM.
2. `./inline` - defines whether the download item should be displayed inline in the browser vs. attachment.
3. `./jcr:title` - defines the download title.
4. `./titleFromAsset` - defines whether the title should be taken from the DAM asset title.
5. `./jcr:description` - defines the download description.
6. `./descriptionFromAsset` - defines whether the description should be taken from the DAM asset description.
7. `./actionText` - defines the action text.

## BEM Description
```
BLOCK cmp-download
    ELEMENT cmp-download__title
    ELEMENT cmp-download__title-link
    ELEMENT cmp-download__description
    ELEMENT cmp-download__properties
    ELEMENT cmp-download__property
        MOD cmp-download__property--<property>
    ELEMENT cmp-download__property-label
    ELEMENT cmp-download__property-content
    ELEMENT cmp-download__action
    ELEMENT cmp-download__action-text
```

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_download\_v2](https://www.adobe.com/go/aem_cmp_download_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_download](https://www.adobe.com/go/aem_cmp_library_download)
* **Author**: [Burkhard Pauli](https://github.com/bpauli)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._
