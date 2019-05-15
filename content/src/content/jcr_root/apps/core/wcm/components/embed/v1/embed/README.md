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
Embed component written in HTL that allow easy to use third party widgets (ex. chat bot, lead gen form, social pixels, videos, etc.) to be consumed on a web page.

## Features

* Free form HTML widgets
* Selection based set of pre-configured trusted embeddable snippets. These snippets are parameterized, and can include unsafe tags such as <script>
* Developers can create custom embeddable snippets by creating a hidden component with a certain sling:resourceSuperType i.e. core/wcm/components/embed/embeddable

### Use Cases
Component supports 2 input modes:
1. HTML: allows the input of free-form html, but is restricted only to safe tags by using the HTL output context of "html"
2. Selection: the author can select from a set of pre-configured trusted embeddable snippets. These snippets can be parameterized, and can include unsafe tags such as <script>.

The template editor can make one, or both, modes of input available to the page author. If the template editor makes selection mode available, they must also select which embeddable snippets to allow.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.5
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_embed\_v1](https://www.adobe.com/go/aem_cmp_embed_v1)

