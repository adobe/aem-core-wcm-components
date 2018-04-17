# AEM Core WCM Components
[![Build Status](https://travis-ci.org/Adobe-Marketing-Cloud/aem-core-wcm-components.png?branch=master)](https://travis-ci.org/Adobe-Marketing-Cloud/aem-core-wcm-components)
[![Code Coverage](https://codecov.io/gh/Adobe-Marketing-Cloud/aem-core-wcm-components/branch/master/graph/badge.svg)](https://codecov.io/gh/Adobe-Marketing-Cloud/aem-core-wcm-components)

A set of standardized components that can be used to speed up development of websites.

## Documentation

* [Tutorial to build a new site using the Core Components](https://helpx.adobe.com/experience-manager/kt/sites/using/getting-started-wknd-tutorial-develop.html)
* AEM product documentation of Core Components
  * [Author documentation](https://docs.adobe.com/docs/en/aem/6-3/author/page-authoring/default-components/core-components.html)
  * [Developer documentation](https://docs.adobe.com/docs/en/aem/6-3/develop/components/core-components.html)
* [adaptTo() 2017 presentation](https://adapt.to/2017/en/schedule/extensible-components-with-sling-models-and-htl.html)

## Development
If you're curious about how the next generation of components looks like, a tech preview is made available in the
[`development`](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/tree/development) branch.

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Available Components

* Page authoring components:
  * [Breadcrumb](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
  * [Content Fragment](extension/contentfragment/content/src/content/jcr_root/apps/core/wcm/extension/components/contentfragment/v1/contentfragment)\*
  * [Image](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
  * [List](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
  * [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
  * [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
  * [Page](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
  * [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)
  * [Sharing](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
  * [Text](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)
  * [Title](content/src/content/jcr_root/apps/core/wcm/components/title/v2/title)

* Form components:
  * [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v2/button)
  * [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container)
  * [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v2/hidden)
  * [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v2/options)
  * [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v2/text)

The components' versioning scheme is documented on the [AEM Core WCM Components' versioning policies](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/wiki/Versioning-policies) wiki page.

\* The Content Fragment Component is an extension to the Core Components and must be separately downloaded and explicitly enabled.

## System Requirements

Core Components | AEM                       | Java
----------------|---------------------------|-----
[1.0.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.0), [1.0.2](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.all-1.0.2)    | AEM 6.3                   | 1.7
[1.0.4](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.4), [1.0.6](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.6)    | AEM 6.3                   | 1.8
[1.1.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.1.0)           | AEM 6.3 + FP19614 or SP 1 | 1.8
[2.0.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.0)           | AEM 6.3 + SP1 + FP20593   | 1.8
[2.0.4](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.4)           | AEM 6.3 + SP1 + CFP2 + FP20593 + FP20696 | 1.8
[2.0.6](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.6)           | AEM 6.3 + SP2 | 1.8

## Installation

To install everything the [released all package as released aggregate package](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases) can be installed via the AEM Package Manager.

For more information about the Package Manager please have a look at [How to Work With Packages](https://docs.adobe.com/docs/en/aem/6-2/administer/content/package-manager.html) documentation page.

## Build

The project has the following minimal requirements:
* Java SE Development Kit 8 or newer
* Apache Maven 3.3.1 or newer

For ease of build and installation the following profiles are provided:

 * ``autoInstallSinglePackage`` - install everything to an existing AEM author instance, as specified by ``http://${aem.host}:${aem.port}``
 * ``autoInstallSinglePackagePublish`` - install everything to an existing AEM publish instance, as specified by ``http://${aem.publish.host}:${aem.publish.port}``
 * ``autoInstallPackage`` - installs the package/bundle to an existing AEM author instance, as specified by ``http://${aem.host}:${aem.port}``
 * ``autoInstallPackagePublish`` - installs the package/bundle to an existing AEM publish instance, as specified by ``http://${aem.publish.host}:${aem.publish.port}``

### UberJar

This project relies on the unobfuscated AEM 6.3 cq-quickstart. This is publicly available on https://repo.adobe.com

For more details about the UberJar please head over to the
[How to Build AEM Projects using Apache Maven](https://docs.adobe.com/docs/en/aem/6-2/develop/dev-tools/ht-projects-maven.html#What%20is%20the%20UberJar?)
documentation page.

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
     <version>2.0.6</version>
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
