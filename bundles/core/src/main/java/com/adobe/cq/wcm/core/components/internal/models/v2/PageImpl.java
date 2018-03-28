/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.osgi.framework.Version;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.models.v1.RedirectItemImpl;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.granite.license.ProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.components.ComponentContext;
import com.google.common.collect.Lists;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v2/page";
    public static final String PN_REDIRECT_TARGET = "cq:redirectTarget";

    private Boolean hasCloudconfigSupport;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    @OSGiService
    private ProductInfoProvider productInfoProvider;

    @Self
    protected SlingHttpServletRequest request;

    @ScriptVariable
    private ComponentContext componentContext;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL,
                   name = PN_REDIRECT_TARGET)
    private String redirectTargetValue;

    private String appResourcesPath;
    private NavigationItem redirectTarget;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        String resourcesClientLibrary = currentStyle.get(PN_APP_RESOURCES_CLIENTLIB, String.class);
        if (resourcesClientLibrary != null) {
            Collection<ClientLibrary> clientLibraries =
                    htmlLibraryManager.getLibraries(new String[]{resourcesClientLibrary}, LibraryType.CSS, true, true);
            ArrayList<ClientLibrary> clientLibraryList = Lists.newArrayList(clientLibraries.iterator());
            if (!clientLibraryList.isEmpty()) {
                appResourcesPath = getProxyPath(clientLibraryList.get(0));
            }
        }
        setRedirect();
    }

    private void setRedirect() {
        if (StringUtils.isNotEmpty(redirectTargetValue)) {
            redirectTarget = new RedirectItemImpl(redirectTargetValue, request);
        }
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
    public Map<String, String> getFavicons() {
        throw new UnsupportedOperationException();
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

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        return super.getExportedItemsOrder();
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return super.getExportedItems();
    }

    @Nullable
    @Override
    public NavigationItem getRedirectTarget() {
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
}
