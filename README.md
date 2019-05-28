# AEM Sites Core Components Sandbox
[![CircleCI](https://circleci.com/gh/adobe/aem-core-wcm-components/tree/development.svg?style=svg)](https://circleci.com/gh/adobe/aem-core-wcm-components/tree/development)
[![Code Coverage](https://codecov.io/gh/adobe/aem-core-wcm-components/branch/development/graph/badge.svg)](https://codecov.io/gh/adobe/aem-core-wcm-components)

**The Sandbox represents a space where work-in-progress versions of the Core Components are developed. They provide _beta features_ and *should not be used in production*. When the components become stable, they will be promoted to new production-ready releases and moved out of sandbox.**

## Documentation

* [Component Library](https://www.adobe.com/go/aem_cmp_library)
* [Using Core Components](https://docs.adobe.com/docs/en/aem/6-3/author/page-authoring/default-components/core-components.html)
* [Tutorial for building a new site using the Core Components (takes about 2 days)](https://helpx.adobe.com/experience-manager/kt/sites/using/getting-started-wknd-tutorial-develop.html)
* [Tutorial for building a new site, used at Adobe Summit 2019 (takes about 2 hours)](https://expleague.azureedge.net/labs/L767/index.html)
* [Recording of the AEM GEMS Webinar, Dec 2018](https://helpx.adobe.com/experience-manager/kt/eseminars/gems/AEM-Core-Components.html)

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Mailing List

For discussions and Q&A you can use our public mailing list hosted on [googlegroups.com](https://groups.google.com/forum/#!forum/aem-core-components-dev). 
You can also subscribe via Email [aem-core-components-dev+subscribe@googlegroups.com](mailto:aem-core-components-dev+subscribe@googlegroups.com).

## Available Components

* Page authoring components:
  * [Breadcrumb](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
  * [Button](content/src/content/jcr_root/apps/core/wcm/components/button/v1/button)
  * [Carousel](content/src/content/jcr_root/apps/core/wcm/components/carousel/v1/carousel)
  * [Content Fragment](content/src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment)
  * [Content Fragment List](content/src/content/jcr_root/apps/core/wcm/components/contentfragmentlist/v1/contentfragmentlist)
  * [Download](content/jcr_root/apps/core/wcm/components/download/v1/download)
  * [Image](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
  * [List](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
  * [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
  * [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
  * [Page](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
  * [Separator](content/src/content/jcr_root/apps/core/wcm/components/separator/v1/separator)
  * [Sharing](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
  * [Tabs](content/src/content/jcr_root/apps/core/wcm/components/tabs/v1/tabs)
  * [Teaser](content/src/content/jcr_root/apps/core/wcm/components/teaser/v1/teaser)
  * [Text](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)
  * [Title](content/src/content/jcr_root/apps/core/wcm/components/title/v2/title)
  * [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)

* Form components:
  * [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v2/button)
  * [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container)
  * [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v2/hidden)
  * [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v2/options)
  * [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v2/text)

Visit the [roadmap wiki page](https://github.com/adobe/aem-core-wcm-components/wiki#roadmap) for upcoming changes.

## Component Versioning

The components' versioning scheme is documented on the [AEM Core WCM Components' versioning policies](https://github.com/adobe/aem-core-wcm-components/wiki/Versioning-policies) wiki page.

## System Requirements

The latest version of the Core Components, require the below system requirements:

Core Components | AEM 6.5 | AEM 6.4 | AEM 6.3 | Java
----------------|---------|---------|---------|------
[2.4.0](https://github.com/adobe/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.4.0) | 6.5.0.0+ | 6.4.2.0+ | 6.3.3.0+ | 8, 11

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

## Include core components as subpackage into your own project maven build

The released version of the core components are available on the public maven repository at https://repo.adobe.com. To include the
core components package into your own project maven build you can add the dependency
 ```
 <dependency>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <type>zip</type>
     <version>2.2.0</version>
 </dependency>
 ```

 and sub package section
```
 <subPackage>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <filter>true</filter>
 </subPackage>
```

 to the `content-package-maven-plugin`.

 For more information how to setup the Adobe Maven Repository (`repo.adobe.com`) for your maven build, please have a look at the
 related [Knowledge Base article](https://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html)

 ### Running the UI tests

 The Sandbox components might use features not yet available on AEM 6.4. In order to correctly test the functionality that's still
 supported for AEM 6.4, the Hobbes UI tests
 should be executed using the following request parameters:

```
http://localhost:4502/libs/granite/testing/hobbes.html?runId=1&autoRun=true&optin=disabled&filter=aem.core-components.testsuite.sandbox&run.options={"withMetadata":{"ignoreOn63":{"value":true,"type":"exclude"}}}
```

This will make sure that tests which would normally fail on AEM 6.3 due to platform changes are not executed.
