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
Page (v1)
====
Extensible page component written in HTL.

## Features
* Editable templates
* SEO meta title, tagging and language definition
* Page title, subtitle, description and thumbnail
* Navigation title, or hide from navigation
* Vanity URL, page alias and redirection
* Page tagging and define content language
* On/Off time and launches
* Blueprints and live copy
* Closed user groups and permissions
* Cloud services

## Loading of CSS/JS
The page component automatically loads certain client libraries in the head section and at the end of the body section

Client Library Category or Path | Condition | Type | Head or Body
--- | --- | --- | --- 
`cq.pagetypes.html5page`,`cq.authoring.page`,`cq.wcm.foundation-main`,`cq.shared` | only for `WCMMode`!=`Disabled` | JS and CSS | Head
libsScript from all referenced [cloud service configurations](https://docs.adobe.com/docs/en/aem/6-3/develop/extending/cloud-service-configurations.html), by default `headlibs.jsp` | only in case there is a lib script found for the referenced cloud service config | anything | Head
`<clientlibs>` being set in component policy | only if `<clientlibs>` is set | CSS | Head
`<designpath>.css` | only if design is set for current page | CSS | Head
`<clientlibs>` being set in component policy | only if `<clientlibs>` is set | JS | Body

### Use Object
The Page component uses the following use objects:
* `com.adobe.cq.wcm.core.components.models.Page`
* `com.adobe.cq.wcm.core.components.models.SocialMediaHelper`
* `com.day.cq.wcm.foundation.TemplatedContainer`

### Component Policy Configuration Properties
The following configuration properties are used:

1. `./clientlibs` - allows to define a list of client libraries to be loaded by the pages associated to this configuraiton policy

### Edit Dialog Properties
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
10. `./offTime` - defines when the page should not be available any more.
11. `./sling:vanityPath` - defines a vanity URL at which the page could be accessed.
12. `./sling:redirect` - if `true`, the page will redirect to its defined vanity URL.
13. `./jcr:language` - defines the language set for this page, to help with `i18n`.
14. `./cq:designPath` - defines where the design for the page is stored.
15. `./sling:alias` - defines a Sling alias for the page, so that it can be accessed at a different resource path.
16. `./cq:allowedTemplates` - defines a template or a list of templates that the page should use.
17. `./cq:exportTemplate` - defines the template used for exporting the page for content synchronisation.
18. `./socialMedia` - defines the enabled social media configurations.
19. `./variantPath` - allows defining the social media variation experience fragment to be used for generating page meta data for social.
media
20. `./cq:contextHubPath` - defines the Context Path configuration used by this page.
21. `./cq:contextHubSegmentsPath` - defines the Context Path Segments Path.
22. `./id` - defines the component HTML ID attribute.

## Client Libraries
The component provides a `core.wcm.components.page.v1.sharing` client library category that contains the JavaScript
required to enable social sharing. It should be added to a relevant site client library using the `embed` property.

It also provides a `core.wcm.components.page.v1.editor` editor client library category that includes
JavaScript handling for dialog interaction. It is already included by its edit dialog.

## Information
* **Vendor**: Adobe
* **Version**: v1
* **Compatibility**: AEM 6.3
* **Status**: production-ready
* **Documentation**: [https://www.adobe.com/go/aem\_cmp\_page\_v1](https://www.adobe.com/go/aem_cmp_page_v1)

