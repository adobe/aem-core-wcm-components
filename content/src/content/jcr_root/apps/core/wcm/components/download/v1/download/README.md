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

Download (v1)
====
Download component written in HTL that displays a downloadable asset on a page.

## Features
* Displays an asset on a page for download with
    * preview, title and description information 
    * custom CTA link
* DownloadServlet with support of conditional header information and Content-Disposition

### Use Object
The Download component uses the `com.adobe.cq.wcm.core.components.models.Download` Sling model as its Use-object.

### Edit dialog properties
The following JCR properties are used:

1. `./fileReference` - defines the path to the asset from DAM
2. `./inline` - defines if the download item should be displayed inline in the browser vs. attachment
3. `./jcr:title` - title of the download item
4. `./titleFromAsset` - defines if the the title should be reused from the asset dam title
5. `./jcr:description` - description of the download item
6. `./descriptionFromAsset` - defines if the description should be reused from the asset dam description
7. `./actionText` - defines the CTA link text

## BEM description
```
BLOCK cmp-download
    ELEMENT cmp-download__image
    ELEMENT cmp-download__title
    ELEMENT cmp-download__description
    ELEMENT cmp-download__action
        ELEMENT cmp-download__action-icon
            MOD cmp-download__action-icon--<extension>
        ELEMENT cmp-download__action-text
    ELEMENT cmp-download__filename
    ELEMENT cmp-download__size
    ELEMENT cmp-download__format
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_download\_v1](https://www.adobe.com/go/aem_cmp_download_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_download](https://www.adobe.com/go/aem_cmp_library_download)
