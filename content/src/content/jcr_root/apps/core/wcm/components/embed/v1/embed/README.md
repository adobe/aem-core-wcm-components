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
Embed (v1)
====
Embed component written in HTL that allows third-party widgets (e.g. chatbot, lead generation form, social pixels, videos) to be added to a page.

## Features
* The following input types are supported:
    * url - an author is able to paste a URL of a widget to embed. URLs are checked against registered providers for a match.
    * embeddable - an author is able to select from pre-configured trusted embeddables. Embeddables can be parameterized and may include unsafe tags.
    * html - an author is able to enter free-form HTML. HTML is restricted to safe tags only.
* Each type can be disabled by a template editor.
* For the embeddable type, the embeddables that are allowed to be selected in the edit dialog can be configured by a template author.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./urlDisabled` - defines whether or not URL input is disabled in the edit dialog.
2. `./embeddablesDisabled` - defines whether or not embeddables are disabled in the edit dialog.
1. `./htmlDisabled` - defines whether or not free-form html input disabled in the edit dialog.
3. `./allowedEmbeddables` - defines the embeddables that are allowed to be selected by an author when embeddables are not disabled.

### Edit Dialog Properties
The following JCR properties are used:

1. `./type` - defines the input type to use. Types include URL, embeddable and html.
2. `./url` - defines the URL of the widget to embed.
3. `./embeddableResourceType` - defines the resource type of an embeddable.
4. `./html` - defines a HTML string to embed.

## BEM Description
```
BLOCK cmp-embed
    ELEMENT cmp-embed__embeddable
        MOD cmp-embed__embeddable--<name>
```

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_embed\_v1](https://www.adobe.com/go/aem_cmp_embed_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_embed](https://www.adobe.com/go/aem_cmp_library_embed)
* **Author**: Vivekanand Mishra

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components)._
