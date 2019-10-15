/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ ClientLibraryCategoriesDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ClientLibraryCategoriesDataSourceServlet extends SlingSafeMethodsServlet {

    public final static String RESOURCE_TYPE = "core/wcm/components/commons/datasources/clientlibrarycategories/v1";
    public final static String PN_LIBRARY_TYPE = "type";

    @Reference
    private transient HtmlLibraryManager htmlLibraryManager;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        LibraryType libraryType = null;
        Resource dataSourceResource = request.getResource().getChild(Config.DATASOURCE);
        if (dataSourceResource != null) {
            ValueMap dataSourceValueMap = ResourceUtil.getValueMap(dataSourceResource);
            if (dataSourceValueMap != null) {
                String type = dataSourceValueMap.get(PN_LIBRARY_TYPE, String.class);
                if (type != null) {
                    type = type.toUpperCase();
                    libraryType = LibraryType.valueOf(type);
                }
            }
        }
        SimpleDataSource clientLibraryCategoriesDataSource = new SimpleDataSource(getCategoryResourceList(request, libraryType).iterator());
        request.setAttribute(DataSource.class.getName(), clientLibraryCategoriesDataSource);
    }

    private List<Resource> getCategoryResourceList(@NotNull SlingHttpServletRequest request, LibraryType libraryType) {
        List<Resource> categoryResourceList = new ArrayList<>();
        HashSet<String> clientLibraryCategories = new HashSet<String>();
        for (ClientLibrary library: htmlLibraryManager.getLibraries().values()) {
            for (String category: library.getCategories()) {
                clientLibraryCategories.add(category);
            }
        }
        if (libraryType != null) {
            Collection<ClientLibrary> clientLibraries = htmlLibraryManager
                .getLibraries(clientLibraryCategories.toArray(new String[clientLibraryCategories.size()]),
                libraryType, true, true);
            clientLibraryCategories.clear();
            for (ClientLibrary library: clientLibraries) {
                for (String category: library.getCategories()) {
                    clientLibraryCategories.add(category);
                }
            }
        }
        for (String category: clientLibraryCategories) {
            categoryResourceList.add(new CategoryResource(category, request.getResourceResolver()));
        }
        return categoryResourceList;
    }

    private static class CategoryResource extends TextValueDataResourceSource {

        private final String category;

        CategoryResource(String category, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.category = category;
        }

        @Override
        public String getText() {
            return category;
        }

        @Override
        public String getValue() {
            return category;
        }
    }
}
