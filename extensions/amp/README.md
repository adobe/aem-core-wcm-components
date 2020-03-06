# AMP
Learn more about how AMP mode can be controlled [here](../../README.md)

## Major Technical aspects

### Parallel delivery

The promise of using AMP in AEM is to not rewrite content but to be  able to serve the usual HTML pages along with their AMP versions if desired. To support this, AEM core components are relying on the `amp` sling selctor to render a `page.html` and `page.amp.html` if requested. 

A [Transformer](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/amp/AmpTransformer.java) is used to also inject the proper `<link>` in the head of the document to inform an HTML page has an AMP "sibling" and vice-versa. This allows for AMP discoverability of AEM pages.

### Inline CSS

AMP requires all the CSS used for a page to be inline in the `<head>` element of the document. This also has to be **optimized** so that the CSS code does not represent all possible component combinations but only the ones needed for the page currently being rendered. 

To achieve this, a developer will need to override the [ampheadlibs.html](examples/src/content/jcr_root/apps/core-components-examples/components/page/ampheadlibs.html) file and provide a spcific clientlib inclusion like so:

```
<sly data-sly-use.clientlib="${'com.adobe.cq.wcm.core.components.models.ClientLibrary' @ type='css', primaryPath='clientlibs/amp', fallbackPath='clientlibs/site', categories='cmp-examples.base.amp'}"/>
<style amp-custom data-sly-test="${clientlib.inlineLimited}">
	${clientlib.inlineLimited @ context = 'unsafe'}
</style>
```
Where: 

1. `primaryPath='clientlibs/amp'` is the pattern of the path where the component specific clientlib folder will be, relative to the component. See [this folder](examples/src/content/jcr_root/apps/core-components-examples/components/carousel/clientlibs/amp) as an example.
2. `fallbackPath ='clientlibs/site'` is the pattern of the path to fallback to, relative to the component. If the aggregator cannot find an AMP clientlib from the `primaryPath`, it will fallback to the value of the `fallbackPath`.
3. `categories='cmp-examples.base.amp'` is the clientlib categories specific to AMP that would contain all the non component styles, i.e base styles (see [Component Library AMP base clientlib](examples/src/content/jcr_root/apps/core-components-examples/clientlibs/clientlib-base-amp)) as an example.

Finally, the [ClientLibraryAggregatorService](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/ClientLibraryAggregatorServiceImpl.java) will ensure it gathers all the defined clientlibs, concatenate them with the base AMP clientlib and render them inline in the `<head>` element. It is intended to be used in cinjuction with the **HTML Library Manager minification turned on**.

### AMP component library asynchronous loading

Certain AEM Core Components require to be mapped to AMP components in order to render valid AMP code and functionality. When it is the case, developers can use / overlay the [customheadlibs.amp.html](content/src/content/jcr_root/apps/core/wcm/components/sharing/v1/sharing/customheadlibs.amp.html) file in order to inject a `<script>` tag in the `<head>`.
Here again, the [AmpTransformer](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/amp/AmpTransformer.java) is used to gather all the necessary libraries to load on the page based on the present components and add their content to the `<head>` element of the rendered page.