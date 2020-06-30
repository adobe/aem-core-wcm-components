/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1.custom;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.adobe.aem.formsndocuments.util.FMConstants;
import com.adobe.cq.wcm.core.components.models.custom.ClientLibraryFilter;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = ClientLibraryFilter.class
)
public class ClientLibraryFilterImpl implements ClientLibraryFilter {

    @Inject
    @Named(OPTION_RESOURCE_TYPES)
    Collection<String> resourceTypes;

    @Inject
    @Named(OPTION_FILTER_REGEX)
    @Optional
    String filterRegex;

    @Inject
    @Named(OPTION_INHERITED)
    @Default(booleanValues = OPTION_INHERITED_DEFAULT)
    boolean inherited;

    @ScriptVariable
    //TODO: will this resolver work on publish?
    private ResourceResolver resolver;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    private Pattern pattern;
    private Set<String> categories;
    private Map<String, ClientLibrary> allLibraries;

    @PostConstruct
    public void init() {
        if (StringUtils.isNotEmpty(filterRegex)) {
            pattern = Pattern.compile(filterRegex);
        }
    }

    @Override
    public Set<String> getCategories() {
        if (categories == null) {
            categories = new HashSet<>();

            allLibraries = htmlLibraryManager.getLibraries();
            Collection<ClientLibrary> libraries = new LinkedList<>();

            for (String resourceType : resourceTypes) {
                Resource componentRes = getResource(resourceType);
                addClientLibraries(componentRes, libraries);

                if (inherited && componentRes != null) {
                    addClientLibraries(getResource(componentRes.getResourceSuperType()), libraries);
                }
            }
            for (ClientLibrary library : libraries) {
                for (String category : library.getCategories()) {
                    if (pattern != null) {
                        if (pattern.matcher(category).matches()) {
                            categories.add(category);
                        }
                    } else {
                        categories.add(category);
                    }
                }
            }
        }
        return categories;
    }

    private Resource getResource(String path) {
        if (path == null) {
            return null;
        }
        return resolver.getResource(path);

    }

    private void addClientLibraries(Resource componentRes, Collection<ClientLibrary> libraries) {
        if (componentRes == null) {
            return;
        }
        String componentType = componentRes.getResourceType();
        if (StringUtils.equals(componentType, FMConstants.CQ_CLIENTLIBRARY_FOLDER)) {
            ClientLibrary library = allLibraries.get(componentRes.getPath());
            if (library != null) {
                libraries.add(library);
            }
        }
        Iterable<Resource> childComponents = componentRes.getChildren();
        for (Resource child : childComponents) {
            addClientLibraries(child, libraries);
        }
    }
}
