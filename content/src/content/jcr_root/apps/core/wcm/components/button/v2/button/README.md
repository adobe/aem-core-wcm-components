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
Button (v2)
====
Button component written in HTL.

## Features
* Linkable to content pages, external URLs or page anchors.
* Allows an icon identifier to be configured for rendering an icon.
* Style System support.

### Use Object
The Button component uses the `com.adobe.cq.wcm.core.components.models.Button` Sling model as its Use-object.

### Edit Dialog Properties
The following properties are written to JCR for the Button component and are expected to be available as `Resource` properties:

1. `./jcr:title` - defines the button text
2. `./link` - defines the button link
3. `./linkTarget` - defines if the link should be opened in a new browser tab
4. `./icon` - defines an icon identifier for rendering an icon
5. `./accessibilityLabel` - defines an accessibility label for the button
6. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-button
    ELEMENT cmp-button__text
    ELEMENT cmp-button__icon
        MOD cmp-button__icon--<icon>
```

### Icon styling

Icon styling must be done by users of the Core Components. Here's an [example from the Core Components Library](https://github.com/adobe/aem-core-wcm-components/blob/72e2be7b9599aec7526be1adf3e4b3eaf3cf6f02/examples/ui.apps/src/content/jcr_root/apps/core-components-examples/clientlibs/clientlib-themes/core-components-clean/styles/components/carousel/base.less#L145).

## Information
* **Vendor**: Adobe
* **Version**: v2
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_button\_v2](https://www.adobe.com/go/aem_cmp_button_v2)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_button](https://www.adobe.com/go/aem_cmp_library_button)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._
