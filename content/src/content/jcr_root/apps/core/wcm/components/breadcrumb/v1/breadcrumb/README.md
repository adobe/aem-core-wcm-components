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
Breadcrumb (v1)
====
Breadcrumb component written in HTL.

## Features
* Start level
* Option to show hidden navigation items
* Exclude the current page from the breadcrumb

### Use Object
The Breadcrumb component uses the `com.adobe.cq.wcm.core.components.models.Breadcrumb` Sling model as its Use-object.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./startLevel` - defines from which level relative to the current page the breadcrumbs will be rendered
2. `./showHidden` - if set to `true`, the breadcrumb components associated with the policy will also render hidden navigation items
3. `./hideCurrent` - if set to `true`, the current page will be skipped by the breadcrumb components associated with the policy

### Edit Dialog Properties
The following properties are written to JCR for this Breadcrumb component and are expected to be available as `Resource` properties:

1. `./startLevel` - defines from which level relative to the current page this breadcrumb will render its items
2. `./showHidden` - if set to `true`, this breadcrumb component will also render hidden navigation items
3. `./hideCurrent` - if set to `true`, the current page will be skipped by this breadcrumb component
4. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component provides a `core.wcm.components.breadcrumb.v1` client library category that contains a recommended base
CSS styling. It should be added to a relevant site client library using the `embed` property.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_breadcrumb\_v1](https://www.adobe.com/go/aem_cmp_breadcrumb_v1)

