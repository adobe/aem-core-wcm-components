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
Container (v1)
====
Container component written in HTL.

## Features

* Configurable background image and color
* Custom-defined color swatches for background color.
* Background images and colors can be disabled through content policies.
* Background color can be restricted to predefined swatches through content policies.
* Allowed components can be configured through policy configuration.

### Use Object
The Container component uses the `com.adobe.cq.wcm.core.components.models.Container` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./backgroundImageEnabled` - defines whether or not display background image option.
2. `./backgroundColorEnabled` - defines whether or not to display background color picker option.
3. `./backgroundSwatchesOnly` -  defines whether or not to display color picker properties tab.
4. `./cq:swatches` - defines list of custom swatches list.

It is also possible to define the allowed components for the Container.

### Edit Dialog Properties
The following properties are written to JCR for this Container component and are expected to be available as `Resource` properties:

1. `./backgroundImageReference` - defines background image of container component.
2. `./backgroundColor` - defines background color of container component.

## BEM Description
```
BLOCK cmp-container
```


## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_container\_v1](https://www.adobe.com/go/aem_cmp_container_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_container](https://www.adobe.com/go/aem_cmp_library_container)
