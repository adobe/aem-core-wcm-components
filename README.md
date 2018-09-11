# AEM Sites Core Components Sandbox
[![Build Status](https://travis-ci.org/Adobe-Marketing-Cloud/aem-core-wcm-components.png?branch=development)](https://travis-ci.org/Adobe-Marketing-Cloud/aem-core-wcm-components)
[![Code Coverage](https://codecov.io/gh/Adobe-Marketing-Cloud/aem-core-wcm-components/branch/development/graph/badge.svg)](https://codecov.io/gh/Adobe-Marketing-Cloud/aem-core-wcm-components)

**The Sandbox represents a space where work-in-progress versions of the Core Components are developed. They provide _beta features_ and *should not be used in production*. When the components become stable, they will be promoted to new production-ready releases and moved out of sandbox.**

## Documentation

* [Tutorial to build a new site using the Core Components](https://helpx.adobe.com/experience-manager/kt/sites/using/getting-started-wknd-tutorial-develop.html)
* AEM product documentation of Core Components
  * [Author documentation](https://docs.adobe.com/docs/en/aem/6-3/author/page-authoring/default-components/core-components.html)
  * [Developer documentation](https://docs.adobe.com/docs/en/aem/6-3/develop/components/core-components.html)
* [adaptTo() 2017 presentation](https://adapt.to/2017/en/schedule/extensible-components-with-sling-models-and-htl.html)

## Contributing

Contributions are welcome! Read the [Contributing Guide](CONTRIBUTING.md) for more information.

## Available Components

* Page authoring components:
  * [Breadcrumb](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
  * [Carousel](content/src/content/jcr_root/apps/core/wcm/sandbox/components/carousel/v1/carousel)
  * [Content Fragment](extension/contentfragment/content/src/content/jcr_root/apps/core/wcm/extension/components/contentfragment/v1/contentfragment)\*
  * [Image](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
  * [List](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
  * [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
  * [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
  * [Page](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
  * [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)
  * [Sharing](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
  * [Tabs](content/src/content/jcr_root/apps/core/wcm/sandbox/components/tabs/v1/tabs)
  * [Teaser](content/src/content/jcr_root/apps/core/wcm/components/teaser/v1/teaser)
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

Core Components | Extension | AEM                      | Java
----------------|-----------|--------------------------|-----
[1.0.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.0), [1.0.2](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.all-1.0.2)    | - | AEM 6.3                   | 1.7
[1.0.4](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.4), [1.0.6](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.0.6)    | - | AEM 6.3                   | 1.8
[1.1.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-1.1.0)           | sandbox/preview | AEM 6.3 + FP19614 or SP 1 | 1.8
[2.0.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.0)           | sandbox/preview | AEM 6.3 + SP1 + FP20593   | 1.8
[2.0.4](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.4)           | 1.0.0 | AEM 6.3 + SP1 + CFP2 + FP20593 + FP20696 | 1.8
[2.0.6](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.6), [2.0.8](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.0.8)           | 1.0.2, 1.0.4 | AEM 6.3 + SP2 | 1.8
[2.1.0](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases/tag/core.wcm.components.reactor-2.1.0)           | 1.0.6 | AEM 6.3.2.2 + FP24268, AEM 6.4.1.0 + FP24267 | 1.8

## Installation

The Sandbox components depend on [`AEM 6.3 SP2`](https://www.adobeaemcloud.com/content/marketplace/marketplaceProxy.html?packagePath=/content/companies/public/adobe/packages/cq630/servicepack/AEM-6.3.2.0) or the latest AEM 6.4 release.

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

 ### Running the UI tests

 The Sandbox components might use features not yet available on AEM 6.4. In order to correctly test the functionality that's still 
 supported for AEM 6.4, the Hobbes UI tests
 should be executed using the following request parameters:

```
http://localhost:4502/libs/granite/testing/hobbes.html?runId=1&autoRun=true&optin=disabled&filter=aem.core-components.testsuite.sandbox&run.options={"withMetadata":{"ignoreOn63":{"value":true,"type":"exclude"}}}
```

This will make sure that tests which would normally fail on AEM 6.3 due to platform changes are not executed.
