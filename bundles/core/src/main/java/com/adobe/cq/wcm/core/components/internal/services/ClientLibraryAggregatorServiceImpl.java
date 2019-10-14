/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.adobe.cq.wcm.core.components.services.ClientLibraryAggregatorService;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since com.adobe.cq.wcm.core.components.models 12.11.0
 */
@Component(service = ClientLibraryAggregatorService.class)
public class ClientLibraryAggregatorServiceImpl implements ClientLibraryAggregatorService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, LibraryType> libraryTypeMap = generateTypeMap();

    @Reference
    private HtmlLibraryManager htmlLibraryManager;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Override
    public String getClientLibOutput(String baseCategory, String categoryString, String type) {
        List<String> categoriesToSearch = new ArrayList<>();
        if (StringUtils.isNotBlank(baseCategory)) {
            categoriesToSearch.add(baseCategory);
        }
        if (categoryString != null && categoryString.contains(",")) {
            categoriesToSearch.addAll(Arrays.asList(categoryString.split(",")));
        } else if (categoryString != null) {
            categoriesToSearch.add(categoryString);
        }
        LibraryType libraryType = libraryTypeMap.get(type);
        Collection<ClientLibrary> libraries = htmlLibraryManager
            .getLibraries(categoriesToSearch.toArray(new String[]{}), libraryType, false, true);
        StringBuilder sb = new StringBuilder();
        for (ClientLibrary clientlib : libraries) {
            HtmlLibrary library = htmlLibraryManager.getLibrary(libraryType, clientlib.getPath());
            if (library != null) {
                try {
                    sb.append(IOUtils.toString(library.getInputStream(true)));
                } catch (IOException e) {
                    log.error("Error getting input stream from clientlib with path ", clientlib.getPath());
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String getClientLibOutput(String baseCategory, Set<String> resourceTypes, String type) {
        List<String> categories = new ArrayList<>();
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            if (resourceResolver == null) {
                log.error("Service user doesn't exist.");
                return null;
            }
            for (String resType : resourceTypes) {
                Resource resource = getResource(resourceResolver, resType);
                if (resource != null && resource.getChild("clientlibs") != null) {
                    Resource ampClientlib = resource.getChild("clientlibs/amp-clientlib");
                    if (ampClientlib == null) {
                        continue;
                    }
                    String[] componentCategories = ampClientlib.getValueMap().get("categories", String[].class);
                    if (componentCategories != null) {
                        categories.addAll(Arrays.asList(componentCategories));
                    }
                }
            }
        }
        return getClientLibOutput(baseCategory, StringUtils.join(categories, ","), type);
    }

    private Resource getResource(ResourceResolver resourceResolver, String resourceType) {
        String[] searchPaths = resourceResolver.getSearchPath();
        for (String path : searchPaths) {
            Resource resource = resourceResolver.getResource(path + resourceType);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    private Map<String, LibraryType> generateTypeMap() {
        Map<String, LibraryType> map = new HashMap<>();
        map.put("css", LibraryType.CSS);
        map.put("js", LibraryType.JS);
        return map;
    }

    /**
     * Obtains a service resource resolver.
     *
     * @return {@link ResourceResolver} instance
     * @throws LoginException exception if it cannot get the resource resolver object.
     */
    private ResourceResolver getServiceResourceResolver() {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "component-clientlib-service");

        try {
            return resolverFactory.getServiceResourceResolver(param);
        } catch (LoginException e) {
            log.error("Unable to get the service resource resolver.");
        }
        return null;
    }
}

