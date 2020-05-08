<!--
Copyright 2020 Adobe

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
Document Cloud Viewer (v1)
====
Document Cloud Viewer component written in HTL.

## Features

* Allows embedding PDF files using the View SDK
* Configurable features for controlling appearance and functionality. 

### Use Object
The Document Cloud Viewer component uses the `com.adobe.cq.wcm.core.components.models.DocumentCloudViewer` Sling model as its Use-object.

### Context Aware Config
The Document Cloud Viewer component uses the context aware config `com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer.DocumentCloudViewerCaConfig` with the following properties:

1. `clientId` - **Required** - the client key obtained by registering for the view sdk api - [https://www.adobe.com/go/dcsdks_credentials](https://www.adobe.com/go/dcsdks_credentials)
2. `reportSuiteId` - Adobe Analytics ID

### Edit Dialog Properties
The following properties are written to JCR for this Document Cloud Viewer component and are expected to be available as `Resource` properties:

1. `./documentPath` - defines the path of the pdf to display
2. `./type` - defines the display type
3. `./defaultViewMode` - defines the display view
4. `./viewerHeight` - defines the height of the viewer when not inline or in full screen mode
5. `./borderless` - `true` to enable full screen borderless
6. `./showAnnotationTools` - `true` to enable annotation tools
7. `./showFullScreen` - `true` to show full screen button
8. `./showLeftHandPanel` - `true` to display left side panel
9. `./showDownloadPdf` - `true` to show download button
10. `./showPrintPdf` - `true` to show print button
11. `./showPageControls` - `true` to show page controls
12. `./dockPageControls` - `true` to dock controls to bottom

## Client Libraries
The component provides a `core.wcm.components.documentcloudviewer.v1` client library category that contains base
CSS styling and JavaScript component. It should be added to a relevant site client library using the `embed` property.

## BEM Description
```
BLOCK cmp-documentcloudviewer
    ELEMENT cmp-documentcloudviewer__content
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **ViewSDK Documentation**: [https://www.adobe.io/apis/documentcloud/dcsdk/docs.html](https://www.adobe.io/apis/documentcloud/dcsdk/docs.html)
* **Author**: [Ensemble Systems Inc. - Leslie Chan](https://ensemble.com)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._
