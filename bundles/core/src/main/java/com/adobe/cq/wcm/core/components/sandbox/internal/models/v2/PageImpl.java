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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.sandbox.models.Page;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.components.ComponentContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Page.class, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class PageImpl  extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/sandbox/components/page/v2/page";
    private static final String DEFAULT_FAVICON_CLIENT_LIB = "core.wcm.components.page.v2.favicon";

    @OSGiService
    @JsonIgnore
    private HtmlLibraryManager htmlLibraryManager;

    @Self
    @JsonIgnore
    protected SlingHttpServletRequest request;

    @ScriptVariable
    @JsonIgnore
    private ComponentContext componentContext;

    @Inject
    private ModelFactory modelFactory;

    private String faviconClientLibCategory;
    private String faviconClientLibPath;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        faviconClientLibCategory = currentStyle.get(PN_FAVICON_CLIENT_LIB, DEFAULT_FAVICON_CLIENT_LIB);
        populateFaviconPath();
    }

    private void populateFaviconPath() {
        Collection<ClientLibrary> clientLibraries =
                htmlLibraryManager.getLibraries(new String[]{faviconClientLibCategory}, LibraryType.CSS, true, true);
        ArrayList<ClientLibrary> clientLibraryList = Lists.newArrayList(clientLibraries.iterator());
        if(!clientLibraryList.isEmpty()) {
            faviconClientLibPath = getProxyPath(clientLibraryList.get(0));
        }
    }

    private String getProxyPath(ClientLibrary lib) {
        String path = lib.getPath();
        if (lib.allowProxy() && (path.startsWith("/libs/") || path.startsWith("/apps/"))) {
            path = "/etc.clientlibs" + path.substring(5);
        } else {
            if (request.getResourceResolver().getResource(lib.getPath()) == null) {
                path = null;
            }
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
    public String getFaviconClientLibPath() {
        return faviconClientLibPath;
    }

    @Override
    public String getCssClassNames() {
        Set<String> cssClassesSet = componentContext.getCssClassNames();
        return StringUtils.join(cssClassesSet, " ");
    }

}
