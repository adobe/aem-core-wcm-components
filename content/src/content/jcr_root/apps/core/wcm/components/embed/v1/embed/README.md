<!--
Copyright 2019 Adobe

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
Embed component written in HTL that allows third-party widgets (e.g. chatbots, lead generation forms, social media posts, social pixels, videos etc.) to be added to a page.

## Features
* The following input types are supported:
    * **URL** - an author is able to paste a URL of a widget to embed. URLs are checked against registered processors for a match. The following URL processors are provided:
        * **oEmbed** - generic oEmbed URL processor with out-of-the-box configurations for Facebook Post, Facebook Video, Flickr, Instagram, SoundCloud, Twitter and YouTube.
        * **Pinterest** - processes Pinterest URLs. 
    * **Embeddable** - an author is able to select from pre-configured trusted embeddables. Embeddables can be parameterized and may include unsafe tags. A YouTube embeddable is included out-of-the-box.
    * **HTML** - an author is able to enter free-form HTML. HTML is restricted to safe tags only.
* Each input type can be disabled by a template author.
* For the embeddable type, the embeddables that are allowed to be selected in the edit dialog can be configured by a template author.

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./urlDisabled` - defines whether or not URL input is disabled in the edit dialog.
2. `./embeddablesDisabled` - defines whether or not embeddables are disabled in the edit dialog.
1. `./htmlDisabled` - defines whether or not free-form HTML input is disabled in the edit dialog.
3. `./allowedEmbeddables` - defines the embeddables that are allowed to be selected by an author when embeddables are not disabled.

In addition once the YouTube embeddable is allowed a tab from [YouTube component](embeddable/youtube)'s design dialog is included.


### Edit Dialog Properties
The following JCR properties are used:

1. `./type` - defines the input type to use. Types include URL, embeddable and HTML.
2. `./url` - defines the URL of the widget to embed.
3. `./embeddableResourceType` - defines the resource type of an embeddable.
4. `./html` - defines a HTML string to embed.
5. `./id` - defines the component HTML ID attribute.

## BEM Description
```
BLOCK cmp-embed
```

## Extending the Embed Component

Please read the [Security Recommendations](#security-recommendations) before defining an extension.

Extension of the Embed component is possible by providing any of the following:

### Custom URL Processor

By implementing the [UrlProcessor](../../../../../../../../../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/embed/UrlProcessor.java) interface you can create your own URL processor.

You will also need to create an HTL template file, with the same name as the `processor` field returned in the `Result`.

Example:

* [Pinterest processor](../../../../../../../../../../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/embed/PinterestUrlProcessor.java)
* [Pinterest HTL template](processors/pinterest.html)

### Custom oEmbed Configuration

By adding an OSGi configuration you can embed an URL from an oEmbed provider.

Example:

* [YouTube configuration](../../../../../../../../../../../config/src/content/jcr_root/apps/core/wcm/config/com.adobe.cq.wcm.core.components.internal.services.embed.OEmbedClientImplConfigurationFactory-youtube.config)

See also:

* [oEmbed specification](https://oembed.com)
* [oEmbed providers](https://oembed.com/providers.json)

### Custom embeddable fragment

1. Create a hidden component with a supertype of `core/wcm/components/embed/v1/embed/embeddable`.
2. Create a rendering HTL script suitable for what your want to render.
3. Create a cq:dialog node with only the configuration options needed for your embeddable.
4. Make sure to have the following properties added to a `granite:data` node under the `cq:dialog` node:
   
   ```
   cmp-embed-dialog-edit-embeddableoptions="true"
   cmp-embed-dialog-edit-showhidetargetvalue="<embeddableResourceType>"
   ```
   where `<embeddableResourceType>` is the resource type of your custom embeddable. See [YouTube embeddable options](./embeddable/youtube/_cq_dialog/.content.xml#L122-123) for an example!
5. The JCR properties for the edit configuration options of an embeddable _must_ be namespaced to prevent clashes.
6. (Optional) Create a cq:design_dialog node if the custom embeddable should also extend the content policy configuration for the 
   embeddable. Make sure to also add the properties which are required for the edit dialog. See step 4 and  [YouTube embeddable options](./embeddable/youtube/_cq_design_dialog/.content.xml#L23) for an example!

Example:

* [YouTube embeddable](embeddable/youtube)

### Security Recommendations

* Only implement URL processors and embeddables for trusted sources, as the payload returned by an endpoint may contain JavaScript.
* Always fetch resources over HTTPS, without `sslRelax` set to `true`.
* Do not use the `unsafe` [HTL display context](https://docs.adobe.com/content/help/en/experience-manager-htl/using/htl/expression-language.html#display-context) unless the payload is trusted.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_embed\_v1](https://www.adobe.com/go/aem_cmp_embed_v1)
* **Component Library**: [https://www.adobe.com/go/aem\_cmp\_library\_embed](https://www.adobe.com/go/aem_cmp_library_embed)
* **Author**: [Vivekanand Mishra](https://github.com/vivekanand-mishra)
* **Co-authors**: [Jean-Christophe Kautzmann](https://github.com/jckautzmann), [Richard Hand](https://github.com/richardhand), [Vlad Bailescu](https://github.com/vladbailescu)

_If you were involved in the authoring of this component and are not credited above, please reach out to us on [GitHub](https://github.com/adobe/aem-core-wcm-components)._