# AMP
This extension enables support form [AMP - Accelerated Mobile Pages](https://developers.google.com/amp).

## Major Technical aspects

### Parallel delivery

The promise of using AMP in AEM is to not rewrite content but to be  able to serve the usual HTML pages along with their AMP versions if desired. To support this, AEM core components are relying on the `amp` sling selector to render a `page.html` and `page.amp.html` if requested. 

An [AmpPage](bundle/src/main/java/com/adobe/cq/wcm/core/extensions/amp/models/AmpPage.java) helper is used to also inject the proper `<link>` and attributes in the head of the document to inform an HTML page has an AMP "sibling" and vice-versa. This allows for AMP discoverability of AEM pages.

### Inline CSS

AMP requires all the CSS used for a page to be inlined in the `<head>` element of the document. This also has to be **optimized** so that the CSS code does not represent all possible component combinations but only the ones needed for the page currently being rendered. 

To achieve this, a customized page component is used (`core/wcm/extension/amp/components/page/v1/page`) which loads just the AMP-specific CSS for components present on the page via this [ampheadlibs.html](content/src/content/jcr_root/apps/core/wcm/extensions/amp/components/page/v1/page/ampheadlibs.html) snippet:

```
<style amp-custom
       data-sly-use.clientlibs="${'com.adobe.cq.wcm.core.components.models.ClientLibraries' @ resourceTypes = page.componentsResourceTypes, filter='.*\\.amp'}">
    ${clientlibs.cssInline @ context="unsafe"}
</style>
```
Where: 

1. `resourceTypes = page.componentsResourceTypes` is the set of resource types for components present on the page (along with those included via the template or experience fragments).
2. `filter='.*\\.amp'` is the regular expression used to filter the AMP-specific client libraries, by category.

As an example, check out this [accordion AMP-specific clientlib folder](../../examples/ui.apps/src/content/jcr_root/apps/core-components-examples/components/accordion/clientlibs/amp/)

### AMP component library asynchronous loading

Some AEM Core Components require to be mapped to AMP components in order to render valid AMP code and functionality. When it is the case, developers can use / overlay the [customheadlibs.amp.html](content/src/content/jcr_root/apps/core/wcm/extensions/amp/components/accordion/v1/accordion/customheadlibs.amp.html) file in order to inject a `<script>` tag in the `<head>`.

These files are picked up by this snippet in [ampheadlibs.html](content/src/content/jcr_root/apps/core/wcm/extensions/amp/components/page/v1/page/ampheadlibs.html):

```
<sly data-sly-use.componentFiles="${'com.adobe.cq.wcm.core.components.models.ComponentFiles' @ resourceTypes = page.componentsResourceTypes, filter='customheadlibs\\.amp\\.html'}"
     data-sly-repeat.toInclude="${componentFiles.paths}"
     data-sly-include="${toInclude}"></sly>
```

### Including custom CSS built with a front-end module

In case developers need to include custom CSS that was built with a front-end module, they can wrap it as a clientlib (example: [cmp-examples.base.amp](../../examples/ui.apps/src/content/jcr_root/apps/core-components-examples/clientlibs/clientlib-base-amp)) and include with similar to this snippet from [customheadlibs.amp.html](../../examples/ui.apps/src/content/jcr_root/apps/core-components-examples/components/page/customheadlibs.amp.html)

```
<style amp-custom
       data-sly-use.clientlibs="${'com.adobe.cq.wcm.core.components.models.ClientLibraries' @ categories='cmp-examples.base.amp'}">
    ${clientlibs.cssInline @ context="unsafe"}
</style>
```

### Including custom JavaScript

In case developers need to include custom JavaScript, they can wrap it as a clientlib and use [com.adobe.cq.wcm.core.components.models.ClientLibraries](../../bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ClientLibraries.java) to include it with proper `async` attribute:

```
<sly data-sly-use.clientlibs="${'com.adobe.cq.wcm.core.components.models.ClientLibraries' @ categories='cmp-examples.base.amp', async=true}">${clientlibs.jsInclude @ context="unsafe"}</sly>
```
