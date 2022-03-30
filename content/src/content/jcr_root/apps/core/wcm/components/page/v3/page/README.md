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
Page (v3)
====
Extensible page component written in HTL.

## Features
* Editable templates
* SEO meta title, tagging and language definition
* Canonical link, alternate language links and robots tags
* Page title, subtitle, description and thumbnail
* Navigation title, or hide from navigation
* Vanity URL, page alias and redirection
* On/Off time and launches
* Blueprints and live copy
* Closed user groups and permissions
* Cloud services
* [PWA support](https://experienceleague.adobe.com/docs/experience-manager-cloud-service/sites/authoring/features/enable-pwa.html)

## Loading of CSS/JS
The page component automatically loads certain client libraries in the head section and at the end of the body section

Client Library Category or Path | Condition | Type | Head or Body
--- | --- | --- | ---
`cq.pagetypes.html5page`,`cq.authoring.page`,`cq.wcm.foundation-main`,`cq.shared` | only for `WCMMode`!=`Disabled` | JS and CSS | Head
libsScript from all referenced [cloud service configurations](https://docs.adobe.com/docs/en/aem/6-3/develop/extending/cloud-service-configurations.html), by default `headlibs.jsp` | only in case there is a lib script found for the referenced cloud service config | anything | Head
`<clientlibsJsHead>` being set in component policy | only if `<clientlibsJsHead>` is set | JS | Head
`<clientlibs>` being set in component policy | only if `<clientlibs>` is set | CSS | Head
`<designpath>.css` | only if design is set for current page | CSS | Head
`<clientlibs>` being set in component policy | only if `<clientlibs>` is set. Categories duplicated in `<clientlibsJsHead>` are only loaded in the page head. | JS | Body

### Loading of Context-Aware CSS/JS
The page component also supports loading developer-defined context-aware CSS, Javascript or `meta` tags. This is done by creating a [context-aware resource](https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html#context-aware-resources) for `com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig` with the following structure:

```
com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
    - prefixPath="/some/path"
    + item01
        - element=["link"|"script"|"meta"]
        - location=["header"|"footer"]
        + attributes
            - attributeName01="attributeValue01"
            - attributeName02="attributeValue02"
            ...
    + item02
        ...
    ...
```
These items will be loaded in the header or footer of the page, depending on the configured `location`. The attribute names should match supported HTML attributes for [link](https://www.w3schools.com/tags/tag_link.asp), [script](https://www.w3schools.com/tags/tag_script.asp) and [meta](https://www.w3schools.com/tags/tag_meta.asp).

## Use Object
The Page component uses the following use objects:
* `com.adobe.cq.wcm.core.components.models.Page`
* `com.day.cq.wcm.foundation.TemplatedContainer`

## Component Policy Configuration Properties
The following configuration properties are used:

1. `./clientlibs` - allows definition of a list of client libraries to be loaded by the pages associated with this policy configuration
2. `./clientlibsJsHead` - allows definition of client libraries for which JavaScript is specifically intended to be loaded
in the document head (JavaScript only) of pages associated with this policy configuration
3. `./clientlibsAsync` - allows custom Javascript libraries to be loaded asynchronously
4. `./appResourcesClientlib` - allows definition of the client library that is used to serve web resources such as favicons
5. `./renderAlternateLanguageLinks` - toggles on/off the rendering of links to alternate language versions of the page in its head

## Edit Dialog Properties
The following properties are written to JCR for this Page component and are expected to be available as `Resource` properties:

1. `./jcr:title` - defines the page title, used for the page SEO meta title and on-page title (unless an overriding `./pageTitle` is defined).
2. `pageName` - defines the page name.
3. `./cq:tags` - defines the page SEO meta tags.
4. `./hideInNav` - if `true`, the page will be hidden in a navigation context (breadcrumb, navigation etc.).
5. `./pageTitle` - defines an alternative page title. Used for overriding the `./jcr:title` in an on-page context.
6. `./navTitle` - defines the page navigation title. Overrides other page titles, when the page is displayed in a navigation context (breadcrumb, navigation etc.).
7. `./subtitle` - defines the page subtitle.
8. `./jcr:description` - defines the page description.
9.  `./onTime` - defines when the page should become available.
10. `./offTime` - defines when the page should no longer be available.
11. `./sling:vanityPath` - defines a vanity URL at which the page could be accessed.
12. `./sling:redirect` - if `true`, the page will redirect to its defined vanity URL.
13. `./jcr:language` - defines the language set for this page, to help with `i18n`.
14. `./cq:designPath` - defines where the design for the page is stored.
15. `./sling:alias` - defines a Sling alias for the page, so that it can be accessed at a different resource path.
16. `./cq:allowedTemplates` - defines a template or a list of templates that the page should use.
17. `./cq:exportTemplate` - defines the template used for exporting the page for content synchronisation.
18. `./cq:contextHubPath` - defines the Context Path configuration used by this page.
19. `./cq:contextHubSegmentsPath` - defines the Context Path Segments Path.
20. `./mainContentSelector` - defines the ID of the main content element of the page (used by the "skip to main content" accessibility feature).
21. `./id` - defines the component HTML ID attribute.
22. `./cq:featuredimage/fileReference` property or `./cq:featuredimage/file` child node - will store either a reference to the image file, or the image file of the featured image of the page.
23. `./cq:featuredimage/alt` - defines the value of the HTML `alt` attribute of the featured image of the page.
24. `./cq:featuredimage/altValueFromDAM` - if `true`, the HTML `alt` attribute of the featured image of the page is inherited from the DAM asset.

## Web Resources Client Library
A web resources client library can be defined at the template level (see `./appResourcesClientlib` component policy configuration).
This client library has to provide the following structure:

```json
{
  "<client library folder>": {
    "jcr:primaryType": "cq:ClientLibraryFolder",
    "allowProxy": true,
    "categories": [
      "<category name>"
    ],
    "css.txt": {"jcr:primaryType": "nt:file"},
    "resources": {
      "jcr:primaryType": "nt:folder"
      }
  }
}
```

The following files are expected in the `resources` folder, for maximum compatibility:

Filename|Browser|Size
--------|-------|----
apple-touch-icon-180x180.png|Safari on iPhone| 180px x 180px
apple-touch-icon-167x167.png|Safari on iPad Pro| 167px x 167px
apple-touch-icon-152x152.png|Safari on iPad, iPad Mini| 152px x 152px
apple-touch-icon-120x120.png|Safari on iPhone| 120px x 120px
icon-192x192.png|Chrome, Opera|192px x 192px
icon-310x310.png|Internet Explorer, Edge and Windows Phone|310px x 310px
icon-310x150.png|Internet Explorer, Edge and Windows Phone|310px x 150px
icon-150x150.png|Internet Explorer, Edge and Windows Phone|150px x 150px
icon-70x70.png|Internet Explorer, Edge and Windows Phone|70px x 70px

## Client Libraries
The component reuses the `core.wcm.components.image.v3.editor` client library category in the edit dialog to support defining
the featured image of the page.

## Information
* **Vendor**: Adobe
* **Version**: v3
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_page\_v3](https://www.adobe.com/go/aem_cmp_page_v3)
* **Authors**: [Stefan Seifert](https://github.com/stefanseifert), [Vlad Bailescu](https://github.com/vladbailescu), [Jean-Christophe Kautzmann](https://github.com/jckautzmann)
