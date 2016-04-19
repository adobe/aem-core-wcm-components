# AEM Core WCM Components

Tech preview of standardized components.

## Available Components

### Page

Extensible page component that features:
* Editable templates
* Page title, subtitle, description and thumbnail
* Navigation title, or hide from navigation
* Vanity URL, page alias and redirection
* Page tagging and define content language
* On/Off time and launches
* Blueprints and live copy
* Closed user groups and permissions
* Cloud services

### Title

Core title component that features:
* In-place editing
* Available levels & default level
* Styles

### Text

Core text component that features:
* In-place editing
* Rich text editor
* Styles

### Image

Core image component that features:
* Smart loading of optimal rendition
* In-place editing, cropping, rotating, and resizing
* Image title, description, accessibility text and link
* Styles

### List

Core list component that features:
* Multiple sources:
  * List page children
  * List tagged items
  * List query result
  * List static items
* Ordering, pagination and limit
* Styles

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
