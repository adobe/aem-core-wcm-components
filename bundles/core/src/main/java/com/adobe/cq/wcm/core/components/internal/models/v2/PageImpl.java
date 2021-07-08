/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.ConfigurationResolver;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Version;

import com.adobe.aem.wcm.seo.SeoTags;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemConfig;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig;
import com.adobe.cq.wcm.core.components.internal.link.LinkHandler;
import com.adobe.cq.wcm.core.components.internal.models.v1.RedirectItemImpl;
import com.adobe.cq.wcm.core.components.models.HtmlPageItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.adobe.granite.license.ProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.components.ComponentContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * V2 Page model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl implements Page {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v2/page";

    /**
     * Head JS client library style property name.
     */
    protected static final String PN_CLIENTLIBS_JS_HEAD = "clientlibsJsHead";

    /**
     * Redirect target property name.
     */
    public static final String PN_REDIRECT_TARGET = "cq:redirectTarget";

    /**
     * Main content selector style property name.
     */
    public static final String PN_MAIN_CONTENT_SELECTOR_PROP = "mainContentSelector";

    /**
     * Flag indicating if cloud configuration support is enabled.
     */
    private Boolean hasCloudconfigSupport;

    /**
     * The HtmlLibraryManager (client library) service.
     */
    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    /**
     * The ProductInfoProvider service.
     */
    @OSGiService
    private ProductInfoProvider productInfoProvider;

    /**
     * The @{@link ConfigurationResourceResolver} service.
     */
    @OSGiService
    private ConfigurationResourceResolver configurationResourceResolver;

    /**
     * The @{@link ConfigurationResolver} service.
     */
    @OSGiService
    private ConfigurationResolver configurationResolver;

    /**
     * The @{@link PathProcessor} service.
     */
    @OSGiService
    private PathProcessor pathProcessor;

    /**
     * The current component context.
     */
    @ScriptVariable
    private ComponentContext componentContext;

    /**
     * The redirect target if set, null if not.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL, name = PN_REDIRECT_TARGET)
    @Nullable
    private String redirectTargetValue;

    @Self
    private LinkHandler linkHandler;

    /**
     * The proxy path of the first client library listed in the style under the
     * &quot;{@value Page#PN_APP_RESOURCES_CLIENTLIB}&quot; property.
     */
    private String appResourcesPath;

    /**
     * The redirect target as a NavigationItem.
     */
    private NavigationItem redirectTarget;

    /**
     * Body JS client library categories.
     */
    private String[] clientLibCategoriesJsBody;

    /**
     * Head JS client library categories.
     */
    private String[] clientLibCategoriesJsHead;

    private List<HtmlPageItem> htmlPageItems;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        this.appResourcesPath = Optional.ofNullable(currentStyle)
                .map(style -> style.get(PN_APP_RESOURCES_CLIENTLIB, String.class))
                .map(resourcesClientLibrary -> htmlLibraryManager.getLibraries(new String[]{resourcesClientLibrary}, LibraryType.CSS, true, false))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .findFirst()
                .map(this::getProxyPath)
                .orElse(null);
    }

    protected NavigationItem newRedirectItem(@NotNull String redirectTarget, @NotNull SlingHttpServletRequest request, @NotNull LinkHandler linkHandler) {
        return new RedirectItemImpl(redirectTarget, request, linkHandler);
    }

    private String getProxyPath(ClientLibrary lib) {
        String path = lib.getPath();
        if (lib.allowProxy()) {
            for (String searchPath : request.getResourceResolver().getSearchPath()) {
                if (path.startsWith(searchPath)) {
                    path = request.getContextPath() + "/etc.clientlibs/" + path.replaceFirst(searchPath, "");
                }
            }
        } else {
            if (request.getResourceResolver().getResource(lib.getPath()) == null) {
                path = null;
            }
        }
        if (path != null) {
            path = path + "/resources";
        }
        return path;
    }

    @Override
    protected void loadFavicons(String designPath) {
    }

    @Override
    @JsonIgnore
    @Deprecated
    public Map<String, String> getFavicons() {
        throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsBody() {
        if (clientLibCategoriesJsBody == null) {
            List<String> headLibs = Arrays.asList(getClientLibCategoriesJsHead());
            clientLibCategoriesJsBody = Arrays.stream(clientLibCategories)
                .distinct()
                .filter(item -> !headLibs.contains(item))
                .toArray(String[]::new);
        }
        return Arrays.copyOf(clientLibCategoriesJsBody, clientLibCategoriesJsBody.length);
    }

    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsHead() {
        if (clientLibCategoriesJsHead == null) {
            clientLibCategoriesJsHead = Optional.ofNullable(currentStyle)
                .map(style -> style.get(PN_CLIENTLIBS_JS_HEAD, String[].class))
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .distinct()
                .toArray(String[]::new);
        }
        return Arrays.copyOf(clientLibCategoriesJsHead, clientLibCategoriesJsHead.length);
    }

    @Override
    public String getAppResourcesPath() {
        return appResourcesPath;
    }

    @Override
    public String getCssClassNames() {
        Set<String> cssClassesSet = componentContext.getCssClassNames();
        return StringUtils.join(cssClassesSet, " ");
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return super.getExportedItemsOrder();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return super.getExportedItems();
    }

    @Nullable
    @Override
    public NavigationItem getRedirectTarget() {
        if (redirectTarget == null && StringUtils.isNotEmpty(redirectTargetValue)) {
            redirectTarget = newRedirectItem(redirectTargetValue, request, linkHandler);
        }
        return redirectTarget;
    }

    @Override
    public boolean hasCloudconfigSupport() {
        if (hasCloudconfigSupport == null) {
            if (productInfoProvider == null || productInfoProvider.getProductInfo() == null ||
                    productInfoProvider.getProductInfo().getVersion() == null) {
                hasCloudconfigSupport = false;
            } else {
                hasCloudconfigSupport = productInfoProvider.getProductInfo().getVersion().compareTo(new Version("6.4.0")) >= 0;
            }
        }
        return hasCloudconfigSupport;
    }

    @Override
    public String getMainContentSelector() {
        if (currentStyle != null) {
            return currentStyle.get(PN_MAIN_CONTENT_SELECTOR_PROP, String.class);
        }
        return null;
    }

    @Override
    public @NotNull List<HtmlPageItem> getHtmlPageItems() {
        if (htmlPageItems == null) {
            htmlPageItems = new LinkedList<>();
            ConfigurationBuilder configurationBuilder = configurationResolver.get(resource);
            HtmlPageItemsConfig config = configurationBuilder.as(HtmlPageItemsConfig.class);
            for (HtmlPageItemConfig itemConfig : config.items()) {
                HtmlPageItem item = new HtmlPageItemImpl(StringUtils.defaultString(config.prefixPath()), itemConfig);
                if (item.getElement() != null) {
                    htmlPageItems.add(item);
                }
            }
            // Support the former node structure: see com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
            if (htmlPageItems.isEmpty()) {
                Resource configResource = configurationResourceResolver.getResource(resource, "sling:configs", HtmlPageItemsConfig.class.getName());
                if (configResource != null) {
                    ValueMap properties = configResource.getValueMap();
                    for (Resource child : configResource.getChildren()) {
                        HtmlPageItem item = new HtmlPageItemImpl(properties.get(HtmlPageItemsConfig.PN_PREFIX_PATH, StringUtils.EMPTY), child);
                        if (item.getElement() != null) {
                            htmlPageItems.add(item);
                        }
                    }
                }
            }
        }
        return htmlPageItems;
    }

    @Override
    public @Nullable String getCanonicalLink() {
        try {
            SeoTags seoTags = resource.adaptTo(SeoTags.class);
            return seoTags != null ? seoTags.getCanonicalUrl() : null;
        } catch (NoClassDefFoundError ex) {
            return pathProcessor.externalize(currentPage.getPath(), request);
        }
    }

    @Override
    public @NotNull Map<Locale, String> getAlternateLanguageLinks() {
        try {
            SeoTags seoTags = resource.adaptTo(SeoTags.class);
            return seoTags != null && seoTags.getAlternateLanguages().size() > 0
                ? Collections.unmodifiableMap(seoTags.getAlternateLanguages())
                : Collections.emptyMap();
        } catch (NoClassDefFoundError ex) {
            return Collections.emptyMap();
        }
    }

    @Override
    public @NotNull List<String> getRobotsTags() {
        try {
            SeoTags seoTags = resource.adaptTo(SeoTags.class);
            return seoTags != null && seoTags.getRobotsTags().size() > 0
                ? Collections.unmodifiableList(seoTags.getRobotsTags())
                : Collections.emptyList();
        } catch (NoClassDefFoundError ex) {
            return Collections.emptyList();
        }
    }
}
