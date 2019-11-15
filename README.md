# AEM Sites Core Components
[![CircleCI](https://circleci.com/gh/adobe/aem-core-wcm-components.svg?style=svg)](https://circleci.com/gh/adobe/aem-core-wcm-components)
[![Code Coverage](https://codecov.io/gh/adobe/aem-core-wcm-components/branch/master/graph/badge.svg)](https://codecov.io/gh/adobe/aem-core-wcm-components)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.adobe.cq/core.wcm.components.all)

A set of standardized components for AEM 6.3+ that can be used to speed up development of websites.

## Documentation

* [Component Library](https://www.adobe.com/go/aem_cmp_library)
* [Using Core Components](https://docs.adobe.com/docs/en/aem/6-3/author/page-authoring/default-components/core-components.html)
* [Tutorial for building a new site using the Core Components (takes about 2 days)](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/overview.html)
* [Tutorial for building a new site, used at Adobe Summit 2019 (takes about 2 hours)](https://expleague.azureedge.net/labs/L767/index.html)
* [Recording of the AEM GEMS Webinar, Dec 2018](https://helpx.adobe.com/experience-manager/kt/eseminars/gems/AEM-Core-Components.html)

## Development
If you're curious about how the next generation of components looks like, a tech preview is made available in the
[`development`](https://github.com/adobe/aem-core-wcm-components/tree/development) branch.

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Mailing List

For discussions and Q&A, you can use our public mailing list hosted on [googlegroups.com](https://groups.google.com/forum/#!forum/aem-core-components-dev). 
You can also subscribe via Email [aem-core-components-dev+subscribe@googlegroups.com](mailto:aem-core-components-dev+subscribe@googlegroups.com).

## Available Components

### Template components

1. [Page](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
2. [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
3. [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
4. [Breadcrumb](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
5. [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)

### Page authoring components

6. [Text](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)
7. [Title](content/src/content/jcr_root/apps/core/wcm/components/title/v2/title)
8. [Image](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
9. [Download](content/src/content/jcr_root/apps/core/wcm/components/download/v1/download)
10. [Embed](content/src/content/jcr_root/apps/core/wcm/components/embed/v1/embed)
11. [Experience Fragment](content/src/content/jcr_root/apps/core/wcm/components/experiencefragment/v1/experiencefragment)
12. [Teaser](content/src/content/jcr_root/apps/core/wcm/components/teaser/v1/teaser)
13. [Button](content/src/content/jcr_root/apps/core/wcm/components/button/v1/button)
14. [List](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
15. [Content Fragment](content/src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment)
16. [Content Fragment List](content/src/content/jcr_root/apps/core/wcm/components/contentfragmentlist/v1/contentfragmentlist)
17. [Separator](content/src/content/jcr_root/apps/core/wcm/components/separator/v1/separator)
18. [Sharing](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)

### Container components

19. [Container](content/src/content/jcr_root/apps/core/wcm/components/container/v1/container)
20. [Carousel](content/src/content/jcr_root/apps/core/wcm/components/carousel/v1/carousel)
21. [Accordion](content/src/content/jcr_root/apps/core/wcm/components/accordion/v1/accordion)
22. [Tabs](content/src/content/jcr_root/apps/core/wcm/components/tabs/v1/tabs)

### Form components

23. [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container)
24. [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v2/text)
25. [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v2/options)
26. [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v2/hidden)
27. [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v2/button)

Visit the [roadmap wiki page](https://github.com/adobe/aem-core-wcm-components/wiki) for the main upcoming components and features.

## Component Versioning

The components' versioning scheme is documented on the [AEM Core WCM Components' versioning policies](https://github.com/adobe/aem-core-wcm-components/wiki/Versioning-policies) wiki page.

## System Requirements

The latest version of the Core Components, require the below system requirements:

Core Components | AEM 6.5 | AEM 6.4 | AEM 6.3 | Java
----------------|---------|---------|---------|------
[2.7.0](https://github.com/adobe/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.7.0) | 6.5.0.0+ | 6.4.4.0+ | 6.3.3.4+ | 8, 11

For a list of requirements for previous versions, see [Historical System Requirements](VERSIONS.md).

## Installation

To install everything, excluding examples, the [released aggregate package `core.wcm.components.all`](https://github.com/adobe/aem-core-wcm-components/releases) can be installed via the AEM Package Manager.

For more information about the Package Manager please have a look at [How to Work With Packages](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/package-manager.html) documentation page.

## Build

The project has the following requirements:
* Java SE Development Kit 8 or Java SE Development Kit 11
* Apache Maven 3.3.1 or newer

For ease of build and installation the following profiles are provided:

 * ``autoInstallSinglePackage`` - install everything to an existing AEM author instance, as specified by ``http://${aem.host}:${aem.port}``
 * ``autoInstallSinglePackagePublish`` - install everything to an existing AEM publish instance, as specified by ``http://${aem.publish.host}:${aem.publish.port}``
 * ``autoInstallPackage`` - installs the package/bundle to an existing AEM author instance, as specified by ``http://${aem.host}:${aem.port}``
 * ``autoInstallPackagePublish`` - installs the package/bundle to an existing AEM publish instance, as specified by ``http://${aem.publish.host}:${aem.publish.port}``

### UberJar

This project relies on the unobfuscated AEM 6.3 cq-quickstart. This is publicly available on https://repo.adobe.com

For more details about the UberJar please head over to the
[How to Build AEM Projects using Apache Maven](https://helpx.adobe.com/experience-manager/6-4/sites/developing/using/ht-projects-maven.html) documentation page.

### Install everything

You can install everything needed to use the components on your running AEM instance by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallSinglePackage

### Individual packages/bundles

You can install individual packages/bundles by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallPackage -pl <project_name(s)> -am

Please note that

 * ``-pl/-projects`` option specifies the list of projects that you want to install
 * ``-am/-also-make`` options specifies that dependencies should also be built

## Include core components into your own project maven build

To add core components to your project, you will need to add it to your maven build.
The released version of the core components are available on the public maven repository at https://repo1.maven.org/maven2/com/adobe/cq/core.wcm.components.all/ 

### For Projects using Maven Archetype 17 and below

To include the core components package into your own project's maven build using AEM's maven archetype 17 and below, you can add the dependency to your pom.xml like this
 ```
 <dependency>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <type>zip</type>
     <version>2.7.0</version>
 </dependency>
 ```

 and then add this subpackage to your sub package section
```
 <subPackage>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <filter>true</filter>
 </subPackage>
```

 inside the configuration of the `content-package-maven-plugin`.

 Also, make sure that if you have a submodule like ui.apps to add the core components as a dependency to ui.apps/pom.xml as well.

 ### For Projects Using Maven Archetype 18 and Above

To include the core components package into your own project using AEM Archetype 18+, add it as a dependency to your build like so:
 ```
 <dependency>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <type>zip</type>
     <version>2.7.0</version>
 </dependency>
 ```

Then add it as a subpackage
```
 <subPackage>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <filter>true</filter>
 </subPackage>
```

inside the configuration of the `filevault-package-maven-plugin`.

 For more information on how to setup the Adobe Maven Repository (`repo.adobe.com`) for your maven build, please have a look at the
 related [Knowledge Base article](https://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html)
