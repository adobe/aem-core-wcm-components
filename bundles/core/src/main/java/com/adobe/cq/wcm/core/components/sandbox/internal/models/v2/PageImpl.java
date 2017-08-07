/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.components.ComponentContext;
import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.sandbox.models.Page;

@Model(adaptables = SlingHttpServletRequest.class, adapters = Page.class, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class PageImpl  extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/sandbox/components/page/v2/page";

    @OSGiService
    @JsonIgnore
    protected HtmlLibraryManager htmlLibraryManager;

    @Self
    @JsonIgnore
    protected SlingHttpServletRequest request;

    @ScriptVariable
    @JsonIgnore
    protected ComponentContext componentContext;

    protected String faviconClientLibCategory;
    protected String faviconClientLibPath;

    protected static final String DEFAULT_FAVICON_CLIENT_LIB = "core.wcm.components.page.v2.favicon";

    @PostConstruct
    protected void initModel() {
        super.initModel();
        faviconClientLibCategory = currentStyle.get(PN_FAVICON_CLIENT_LIB, DEFAULT_FAVICON_CLIENT_LIB);
        populateFaviconPath();
    }

    protected void populateFaviconPath() {
        Collection<ClientLibrary> clientLibraries =
                htmlLibraryManager.getLibraries(new String[]{faviconClientLibCategory}, LibraryType.CSS, true, true);
        ArrayList<ClientLibrary> clientLibraryList = Lists.newArrayList(clientLibraries.iterator());
        if(!clientLibraryList.isEmpty()) {
            faviconClientLibPath = getProxyPath(clientLibraryList.get(0));
        }
    }

    protected String getProxyPath(ClientLibrary lib) {
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
