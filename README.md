# Core Components Examples

Home to example websites that showcase the latest features of the AEM Sites Core Components, hosted on GitHub pages. Currently including one example site, the [Component Library](http://opensource.adobe.com/aem-core-wcm-components/library.html).

The source files for the examples can be found in the [Core Components Examples content package](https://github.com/adobe/aem-core-wcm-components/tree/master/examples).

## Component Library

* **Home** [http://opensource.adobe.com/aem-core-wcm-components/library.html](http://opensource.adobe.com/aem-core-wcm-components/library.html)
* **Description** A site featuring a selection of Core Components and their features. Each component page has example configurations with sample code output, including component properties, markup and JSON.

## Updating

The sites content is currently updated manually, as follows:

1. Install the [examples content package](https://github.com/adobe/aem-core-wcm-components/tree/master/examples) to AEM.
1. Fetch the examples content from a publish instance of AEM on the default port `4503`:
    ```
    wget --mirror --convert-links --adjust-extension --page-requisites "http://localhost:4503/content/core-components-examples/library.html"
    ```
1. Rewrite the URLs in the fetched content to remove the `/content/core-components-examples/` path segment.
1. Branch off the `gh-pages` branch of the repository.
1. Add the updated example content to the newly created branch. 
1. Make a pull request with the updated content.
