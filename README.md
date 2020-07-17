# AEM WCM Core Components

[![CircleCI](https://circleci.com/gh/adobe/aem-core-wcm-components/tree/development.svg?style=svg)](https://circleci.com/gh/adobe/aem-core-wcm-components/tree/development)
[![Code Coverage](https://codecov.io/gh/adobe/aem-core-wcm-components/branch/development/graph/badge.svg)](https://codecov.io/gh/adobe/aem-core-wcm-components)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all)

Set of standardized Web Content Management (WCM) components for [Adobe Experience Manager (AEM)](https://www.adobe.com/marketing/experience-manager.html) to speed up development time and reduce maintenance cost of your websites.

## Welcome

* **Contributions** are welcome, read our [contributing guide](CONTRIBUTING.md) for more information.
* **Ideas and questions** are discussed on our [public mailing list](https://groups.google.com/forum/#!forum/aem-core-components-dev); you can also [subscribe via email](mailto:aem-core-components-dev+subscribe@googlegroups.com).

### Usability Study 2020

We're conducting a usability study by using the [System Usability Scale](https://measuringu.com/sus/), a reliable tool to measure the perceived usability.  
Please help us making the Core Components better by responding to our **[short usability questionnaire](https://s2.userzoom.com/m/MSBDNTc1MlMxMDk1)**. Thank you!

## Documentation

* **[Component Library](https://www.adobe.com/go/aem_cmp_library):** A collection of examples to view the components in their various configurations.
* **[Component Documentation](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/introduction.html):** For developers and authors, with details about each component.
* Get Started:
  - **[WKND Tutorial](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/overview.html):** A two-day tutorial for building a new site.
  - **[Summit Tutorial](https://expleague.azureedge.net/labs/L767/index.html):** A two-hour tutorial for building a new site (from a Lab at US Summit 2019).
  - **[Gems Webinar](https://helpx.adobe.com/experience-manager/kt/eseminars/gems/AEM-Core-Components.html):** A guided tour of the Core Components (recorded on Dec 2018).

## Features

* **Production-Ready:** 28 robust components that are well tested, widely used, and that perform well.
* **Cloud-Ready:** Whether on [AEM as a Cloud Service](https://docs.adobe.com/content/help/en/experience-manager-cloud-service/landing/home.html), on [Adobe Managed Services](https://github.com/adobe/aem-project-archetype/tree/master/src/main/archetype/dispatcher.ams), or on-premise, they just work.
* **Versatile:** The components represent generic concepts with which the authors can assemble nearly any layout.
* **Configurable:** Template-level [content policies](https://docs.adobe.com/content/help/en/experience-manager-65/developing/platform/templates/page-templates-editable.html#content-policies) define which features the page authors are allowed to use or not.
* **Trackable:** The [Adobe Client Data Layer](https://github.com/adobe/adobe-client-data-layer/) integration allows to track all aspects of the visitor experience.
* **Accessible:** They comply [WCAG 2.1 standard](https://www.w3.org/TR/WCAG21/), provide ARIA labels, and support keyboard navigation ([known issues](https://github.com/adobe/aem-core-wcm-components/issues?utf8=%E2%9C%93&q=is%3Aissue+is%3Aopen+accessibility+in%3Atitle)).
* **SEO-Friendly:** The HTML output is semantic and provides schema.org microdata annotations.
* **WebApp-Ready:** The [streamlined JSON output](https://docs.adobe.com/content/help/en/experience-manager-learn/foundation/development/develop-sling-model-exporter.html) allows client-side rendering, still with a possibility of [in-context editing](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/spa-editor/spa-editor-framework-feature-video-use.html).
* **Design Kit:** A [UI kit for Adobe XD](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/assets/overview/AEM_UI-kit_Wireframe.xd) allows designers to create wireframes that they can then [style as needed](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/assets/overview/AEM_UI-kit_WKND.xd).
* **Themeable:** The components implement the [Style System](https://docs.adobe.com/content/help/en/experience-manager-65/developing/components/style-system.html), and the markup follows [BEM CSS conventions](http://getbem.com/).
* **Customizable:** Several patterns allow [easy customization](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/developing/customizing.html), from adjusting the HTML to advanced functionality reuse.
* **Versionned:** The [versioning policy](https://github.com/adobe/aem-core-wcm-components/wiki/Versioning-policies) ensures we won't break your site when improving things that might impact you.
* **Open Sourced:** If something is not as it should, [contribute](CONTRIBUTING.md) your improvements!

### Template Components

1. [Page](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
2. [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
3. [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
4. [Breadcrumb](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
5. [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)

### Page Authoring Components

6. [Title](content/src/content/jcr_root/apps/core/wcm/components/title/v2/title)
7. [Text](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)
8. [Image](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
9. [Button](content/src/content/jcr_root/apps/core/wcm/components/button/v1/button)
10. [Teaser](content/src/content/jcr_root/apps/core/wcm/components/teaser/v1/teaser)
11. [Download](content/src/content/jcr_root/apps/core/wcm/components/download/v1/download)
12. [List](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
13. [Experience Fragment](content/src/content/jcr_root/apps/core/wcm/components/experiencefragment/v1/experiencefragment)
14. [Content Fragment](content/src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment)
15. [Content Fragment List](content/src/content/jcr_root/apps/core/wcm/components/contentfragmentlist/v1/contentfragmentlist)
16. [Embed](content/src/content/jcr_root/apps/core/wcm/components/embed/v1/embed)
17. [Sharing](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
18. [Separator](content/src/content/jcr_root/apps/core/wcm/components/separator/v1/separator)
19. [Progress Bar](content/src/content/jcr_root/apps/core/wcm/components/progressbar/v1/progressbar)
20. [PDF Viewer](content/src/content/jcr_root/apps/core/wcm/components/pdfviewer/v1/pdfviewer)

### Container Components

21. [Container](content/src/content/jcr_root/apps/core/wcm/components/container/v1/container)
22. [Carousel](content/src/content/jcr_root/apps/core/wcm/components/carousel/v1/carousel)
23. [Tabs](content/src/content/jcr_root/apps/core/wcm/components/tabs/v1/tabs)
24. [Accordion](content/src/content/jcr_root/apps/core/wcm/components/accordion/v1/accordion)

### Form Components

25. [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container)
26. [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v2/text)
27. [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v2/options)
28. [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v2/hidden)
29. [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v2/button)

### Roadmap

To learn about the main upcoming components and features, visit the [roadmap wiki page](https://github.com/adobe/aem-core-wcm-components/wiki).

## Usage

To include the Core Components in a new project, we strongly advise to use the [AEM Project Archetype](https://github.com/adobe/aem-project-archetype); this guarantees a starting point that complies to all recommended practices from Adobe.

For existing projects, take example from the [AEM Project Archetype](https://github.com/adobe/aem-project-archetype) by looking at the `core.wcm.components` references in the main [`pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/pom.xml), in [`all/pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/all/pom.xml), and in [`ui.apps/pom.xml`](https://github.com/adobe/aem-project-archetype/blob/master/src/main/archetype/ui.apps/pom.xml). For the rest, make sure to create Proxy Components, to load the client libraries and to allow the components on the template, as instructed in [Using Core Components](https://docs.adobe.com/content/help/en/experience-manager-core-components/using/get-started/using.html).

### System Requirements

Core Components | AEM as a Cloud Service | AEM 6.5 | AEM 6.4 | Java SE | Maven
----------------|------------------------|---------|---------|---------|---------
[2.10.0](https://github.com/adobe/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.10.0) | Continual | 6.5.5.0+ | 6.4.8.1+ | 8, 11 | 3.3.9+

For the requirements from previous Core Component releases, see [Historical System Requirements](VERSIONS.md).

The Core Components require the use of [editable templates](https://docs.adobe.com/content/help/en/experience-manager-learn/sites/page-authoring/template-editor-feature-video-use.html) and do not support Classic UI nor static templates. If needed, check out the [AEM Modernization Tools](https://opensource.adobe.com/aem-modernize-tools/pages/tools.html).

Setup your local development environment for [AEM as a Cloud Service SDK](https://docs.adobe.com/content/help/en/experience-manager-learn/cloud-service/local-development-environment-set-up/overview.html) or for [older versions of AEM](https://docs.adobe.com/content/help/en/experience-manager-learn/foundation/development/set-up-a-local-aem-development-environment.html).

### Building

To compile your own version of the Core Components, you can build and install everything on your running AEM instance by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallSinglePackage

You can also install individual packages/bundles by issuing the following command in the top-level folder of the project:

    mvn clean install -PautoInstallPackage -pl <project_name(s)> -am

Note that:
* `-pl/-projects` option specifies the list of projects that you want to install
* `-am/-also-make` options specifies that dependencies should also be built

For convenience, the following deployment profiles are provided when running the Maven install goal with `mvn install`:
* `autoInstallSinglePackage`: Install everything to the AEM author instance.
* `autoInstallSinglePackagePublish`: Install everything to the AEM publish instance.
* `autoInstallPackage`: Install the `ui.content` and `ui.apps` content packages to the AEM author instance.
* `autoInstallPackagePublish`: Install the `ui.content` and `ui.apps` content packages to the  AEM publish instance.

The hostname and port of the instance can be changed with the following user defined properties:
* `aem.host` and `aem.port` for the author instance.
* `aem.publish.host` and `aem.publish.port` for the publish instance.
