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
Progress Bar (v1)
=========
Progress bar component written in HTL.

## Features

* Allows displaying a visual indication of progress.
* Markup inspired from [W3](https://www.w3schools.com/w3css/w3css_progressbar.asp), which makes it compatible with older browsers too.

### Use Object
The Progress Bar component uses the `com.adobe.cq.wcm.core.components.models.ProgressBar` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for this Progress Bar component and are expected to be available as `Resource` properties:

1. `./completed` - will store the completion percentage
2. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-progressbar
    ELEMENT cmp-progressbar__label--completed
    ELEMENT cmp-progressbar__label--remaining
    ELEMENT cmp-progressbar__bar
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_progressbar\_v1](https://www.adobe.com/go/aem_cmp_progress_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_progress](https://www.adobe.com/go/aem_cmp_library_progress)
