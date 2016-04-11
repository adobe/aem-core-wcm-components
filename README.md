# AEM Core WCM Components

## Installation

For ease of installation the following profiles are provided:

 * ``autoInstallPackage`` - installs the package/bundle to an existing AEM author instance, as specified by ``http://${aem.host}:${aem.port}``
 * ``autoInstallPackagePublish`` - installs the package/bundle to an existing AEM publish instance, as specified by ``http://${aem.publish.host}:${aem.publish.port}``

### All Components

You can install all components to your running AEM instance by issueing the following command in the top level folder of the project:

    mvn clean install -PautoInstallPackage
    
### Single Components

You can install a single component (or a list of components) by issuing the following command in the top level folder of the project:

    mvn clean install -PautoInstallPackage -pl <component_name(s)> -am

Please note that

 * ``-pl/-projects`` option specifies the list of projects that you want to install
 * ``-am/-also-make`` options specifies that dependencies should also be built
