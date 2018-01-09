# AEM Core WCM Components Sandbox
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
  * [Page component](content/src/content/jcr_root/apps/core/wcm/components/page/v2/page)
  * [Breadcrumb component](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v2/breadcrumb)
  * [Title component](content/src/content/jcr_root/apps/core/wcm/components/title/v2/title)
  * [Text component](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text)
  * [Image component](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image)
  * [List component](content/src/content/jcr_root/apps/core/wcm/components/list/v2/list)
  * [Sharing component](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
  * [Navigation](content/src/content/jcr_root/apps/core/wcm/components/navigation/v1/navigation)
  * [Language Navigation](content/src/content/jcr_root/apps/core/wcm/components/languagenavigation/v1/languagenavigation)
  * [Quick Search](content/src/content/jcr_root/apps/core/wcm/components/search/v1/search)
  * [Teaser](content/src/content/jcr_root/apps/core/wcm/sandbox/components/teaser/v1/teaser)
* Form components:
  * [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container)
  * [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v2/text)
  * [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v2/options)
  * [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v2/hidden)
  * [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v2/button)

The components' versioning scheme is documented on the [AEM Core WCM Components' versioning policies](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/wiki/Versioning-policies) wiki page

## System Requirements

Core Components | AEM                       | Java
----------------|---------------------------|-----
1.0.0, 1.0.2    | AEM 6.3                   | 1.7
1.0.4, 1.0.6    | AEM 6.3                   | 1.8
1.1.0           | AEM 6.3 + FP19614 or SP 1 | 1.8

The Sandbox components depend on the [`cq-6.3.0-featurepack-18678`](https://www.adobeaemcloud.com/content/marketplace/marketplaceProxy.html?packagePath=/content/companies/public/adobe/packages/cq630/featurepack/cq-6.3.0-featurepack-18678) Feature Pack. This Feature Pack is a tech preview, meaning that changes might happen to it that could break compatibility. It is therefore recommended to not install it on production environments and instead wait until these capabilities are consolidated in a Service Pack.

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

 ### Running the UI tests

 The Sandbox components might use features not yet available on AEM 6.3. In order to correctly test the functionality that's still supported for AEM 6.3, the Hobbes UI tests
 should be executed using the following request parameters:

```
http://localhost:4502/libs/granite/testing/hobbes.html?runId=1&autoRun=true&optin=disabled&filter=aem.core-components.testsuite.sandbox&run.options={"withMetadata":{"ignoreOn63":{"value":true,"type":"exclude"}}}
```

This will make sure that tests which would normally fail on AEM 6.3 due to platform changes are not executed.
