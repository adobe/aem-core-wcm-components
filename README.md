# AEM Core WCM Components

A set of standardized components that can be used to speed up development of web sites.

## Available Components

* Page authoring components:
  * [Page component](content/src/content/jcr_root/apps/core/wcm/components/page/v1/page)
  * [Breadcrumb component](content/src/content/jcr_root/apps/core/wcm/components/breadcrumb/v1/breadcrumb)
  * [Title component](content/src/content/jcr_root/apps/core/wcm/components/title/v1/title)
  * [Text component](content/src/content/jcr_root/apps/core/wcm/components/text/v1/text)
  * [Image component](content/src/content/jcr_root/apps/core/wcm/components/image/v1/image)
  * [List component](content/src/content/jcr_root/apps/core/wcm/components/list/v1/list)
  * [Sharing component](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing)
* Form components:
  * [Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v1/container)
  * [Form text field](content/src/content/jcr_root/apps/core/wcm/components/form/text/v1/text)
  * [Form options field](content/src/content/jcr_root/apps/core/wcm/components/form/options/v1/options)
  * [Form hidden field](content/src/content/jcr_root/apps/core/wcm/components/form/hidden/v1/hidden)
  * [Form button](content/src/content/jcr_root/apps/core/wcm/components/form/button/v1/button)

## System Requirements

The core components are build on top of the Sling Models API 1.3, which is part of `AEM 6.3`

## Installation

To install everything the [released all package as released aggregate package](https://github.com/Adobe-Marketing-Cloud/aem-core-wcm-components/releases) can be installed via the AEM Package Manager.

For more information about the Package Manager please have a look at [How to Work With Packages](https://docs.adobe
.com/docs/en/aem/6-2/administer/content/package-manager.html) documentation page.

## Build

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

The released version of the core components are available on the public maven repository at http://repo.adobe.com. To include the 
core components package into your own project maven build you can add the dependency
 ```
 <dependency>
     <groupId>com.adobe.cq</groupId>
     <artifactId>core.wcm.components.all</artifactId>
     <type>zip</type>
     <version>1.0.0</version>
     <scope>provided</scope>
 </dependency>
 ```
 
 and sub package section
 ```
 <subPackage>
     <groupId>${project.groupId}</groupId>
     <artifactId>core.wcm.components.config</artifactId>
     <filter>true</filter>
 </subPackage>
 ```
 
 to the `content-package-maven-plugin`.
 
 For more information how to setup the Adobe Maven Repository (repo.adobe.com) for your maven build, please have a look at the 
 related [Knowledge Base article](https://helpx.adobe.com/experience-manager/kb/SetUpTheAdobeMavenRepository.html)