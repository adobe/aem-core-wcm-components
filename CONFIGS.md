# Configurations for AEM WCM Core Components

## OSGi Configurations

AEM uses [OSGi configurations](https://experienceleague.adobe.com/docs/experience-manager-65/deploying/configuring/configuring-osgi.html) to drive how its internals work and WCM Core Components leverage the following:

### General Configurations:

| Configuration | Description |
|---|---|
| [ComponentConfigImpl](config/src/content/jcr_root/apps/core/wcm/config/com.adobe.cq.dam.cfm.impl.component.ComponentConfigImpl-core-comp-v1.config) | Enables Content Fragment management for [Content Fragment component](content/src/content/jcr_root/apps/core/wcm/components/contentfragment/v1/contentfragment). Reference documentation: https://experienceleague.adobe.com/docs/experience-manager-65/developing/extending-aem/content-fragments-config-components-rendering.html |
| [RTEFilterServletFactory](config/src/content/jcr_root/apps/core/wcm/config/com.adobe.cq.ui.wcm.commons.internal.servlets.rte.RTEFilterServletFactory.amended-core-components.config) | Enables usage of RTE (Rich Text Editor) with [Text component](content/src/content/jcr_root/apps/core/wcm/components/text/v2/text). Reference documentation: https://experienceleague.adobe.com/docs/experience-manager-65/administering/operations/rich-text-editor.html |
| [OEmbedClientImplConfigurationFactory](config/src/content/jcr_root/apps/core/wcm/config/com.adobe.cq.wcm.core.components.internal.services.embed.OEmbedClientImplConfigurationFactory-*.config) | Enables out-of-the-box OEmbed configurations for several services, with [Embed component](content/src/content/jcr_root/apps/core/wcm/components/embed/v1/embed) |
| [AdaptiveImageServletMappingConfigurationFactory](config/src/content/jcr_root/apps/core/wcm/config/com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServletMappingConfigurationFactory-*.config) | Enables adaptive image support for [Image component](content/src/content/jcr_root/apps/core/wcm/components/image/v2/image) | 
| [MailServlet](config/src/content/jcr_root/apps/core/wcm/config/com.day.cq.wcm.foundation.forms.impl.MailServlet-core-components.config) | Enables email support for user-submitted information in [Core Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container) |
| [ServiceUserMapperImpl](config/src/content/jcr_root/apps/core/wcm/config/org.apache.sling.serviceusermapping.impl.ServiceUserMapperImpl.amended-componentsservice.config) | Enables access rights for [ClientLibraries](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ClientLibraries.java) and [ComponentFiles](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ComponentFiles.java) helpers |

### `author` Configurations
| Configuration | Description |
|---|---|
| [FormParagraphPostProcessor](config/src/content/jcr_root/apps/core/wcm/config.author/com.day.cq.wcm.foundation.forms.impl.FormParagraphPostProcessor-core-components.config) | Enables functionality of [Core Form container](content/src/content/jcr_root/apps/core/wcm/components/form/container/v2/container) |
| [MobileEmulatorProvider](config/src/content/jcr_root/apps/core/wcm/config.author/com.day.cq.wcm.mobile.core.impl.MobileEmulatorProvider-core-components.config) | Enables mobile devices emulation in the editor for [Page component](content/src/content/jcr_root/apps/core/wcm/components/page/v3/page) |

## Context-Aware configurations

The following [context-aware configurations](https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html) are defined by the AEM WCM Core Components:

| Configuration | Description |
|---|---|
| [HtmlPageItemsConfig](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/config/HtmlPageItemsConfig.java) | Holds information on items to be included by [Page component](content/src/content/jcr_root/apps/core/wcm/components/page/v3/page): scripts, links, meta elements |
| [PdfViewerCaConfig](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/CaConfigReferenceProvider.java) | Holds configuration for [PDF Viewer component](content/src/content/jcr_root/apps/core/wcm/components/pdfviewer/v1/pdfviewer) | 
| [DataLayerConfig](bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/DataLayerConfig.java) | Toggles the [Adobe Client Data Layer](https://github.com/adobe/adobe-client-data-layer) integration |

At the moment, there is no editing UI for context-aware configuration available out-of-the-box. Developers can either:

* Deploy predefined context-ware configurations via content packages.
* Change context-aware configurations using `SlingPostServlet` or `CRXDE Lite`.
* Use the [wcm.io context-aware configuration editor](https://wcm.io/caconfig/editor/). An example setup is included with the Core Components Library example content (see https://github.com/adobe/aem-core-wcm-components/pull/1410) 
