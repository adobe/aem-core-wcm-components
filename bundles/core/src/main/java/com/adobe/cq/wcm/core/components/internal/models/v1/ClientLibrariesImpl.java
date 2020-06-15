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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.day.cq.wcm.api.Template;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ClientLibraries.class}
)
public class ClientLibrariesImpl implements ClientLibraries {

    private static final String NN_CQ_CLIENT_LIBRARY_FOLDER = "cq:ClientLibraryFolder";
    private static final String TEMPLATE_STRUCTURE_CONTENT_PATH = "/structure/jcr:content";

    @ScriptVariable
    protected com.day.cq.wcm.api.Page currentPage;

    @ScriptVariable
    @JsonIgnore
    protected ResourceResolver resolver;

    @Inject
    private ModelFactory modelFactory;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private Resource resource;

    @Inject
    private String additionnalCategories;

    @Inject
    private String categoryFilter;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    private Map<String, ClientLibrary> allLibraries;
    private Collection<ClientLibrary> libraries;
    private String[] categoriesArray;

    @NotNull
    @Override
    public String[] getCategories() {
        if (categoriesArray == null) {
            Set<String> categories = new HashSet<>();

            // retrieve all the clientlibs defined for the resource, its descendants and its super types
            populateClientLibraries();

            // add categories defined by the clientlibs
            for (ClientLibrary library : libraries) {
                String[] libraryCategories = library.getCategories();
                categories.addAll(Arrays.asList(libraryCategories));
            }

            // add categories defined in the page policy and page design
            addPageClientLibCategories(categories);

            // add categories injected by the HTL template
            if (StringUtils.isNotBlank(additionnalCategories)) {
                if (additionnalCategories.contains(",")) {
                    Collections.addAll(categories, additionnalCategories.split(","));
                } else {
                    categories.add(additionnalCategories);
                }
            }

            // filter the categories based on category name regex
            if (StringUtils.isNotBlank(categoryFilter)) {
                Pattern p = Pattern.compile(categoryFilter);
                categories.removeIf(category -> {
                    Matcher m = p.matcher(category);
                    return !m.find();
                });
            }

            categoriesArray = categories.toArray(new String[0]);
        }

        return Arrays.copyOf(categoriesArray, categoriesArray.length);
    }

    private void populateClientLibraries() {
        allLibraries = htmlLibraryManager.getLibraries();
        libraries = new ArrayList<>();
        Set<String> resourceTypes = new HashSet<>();
        // get the resource types of this resource, its descendants and its super types
        addResourceTypesFromResource(resourceTypes);
        // if the resource is a page: get the resource types of the resources used in the template
        addResourceTypesFromTemplate(resourceTypes);

        // get the clientlibs defined by the resource types
        for (String resourceType : resourceTypes) {
            Resource componentRes = resolveResource(resolver, resourceType);
            addClientLibraries(componentRes, libraries);
        }
    }

    private void addPageClientLibCategories(Set<String> categories) {
        // if the resource is based on a page model
        com.adobe.cq.wcm.core.components.models.Page  pageModel = request.adaptTo(com.adobe.cq.wcm.core.components.models.Page.class);
        if (pageModel != null) {
            String[] pageClientLibCategories = pageModel.getClientLibCategories();
            if (pageClientLibCategories != null) {
                categories.addAll(new HashSet<>(Arrays.asList(pageClientLibCategories)));
            }
        }
    }

    private void addClientLibraries(Resource componentRes, Collection<ClientLibrary> libraries) {
        if (componentRes == null) {
            return;
        }
        String componentType = componentRes.getResourceType();
        if (StringUtils.equals(componentType, NN_CQ_CLIENT_LIBRARY_FOLDER)) {
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

    private void addResourceTypesFromResource(Set<String> resourceTypes) {
        addResourceTypesFromTree(resource, resourceTypes);
    }

    private void addResourceTypesFromTemplate(Set<String> resourceTypes) {
        // if the resource is a page
        if (StringUtils.equals(resource.getPath(), currentPage.getContentResource().getPath())) {
            Template template = currentPage.getTemplate();
            if (template == null) {
                return;
            }
            String templatePath = template.getPath() + TEMPLATE_STRUCTURE_CONTENT_PATH;
            Resource templateResource = resolver.getResource(templatePath);
            if (templateResource != null) {
                addResourceTypesFromTree(templateResource, resourceTypes);
            }
        }
    }

    private void addResourceTypesFromTree(Resource resource, Set<String> resourceTypes) {
        if (resource == null) {
            return;
        }
        // add the resource type of the resource
        String resourceType = resource.getResourceType();
        resourceTypes.add(resourceType);
        // add all the super types of the resource
        addResourceSuperTypes(resourceType, resourceTypes);
        // in case the resource is an experience fragment: add the resource types of the original fragment
        addResourceTypesFromXF(resource, resourceTypes);
        // add the resource types of the children and their descendents
        Iterable<Resource> childComponents = resource.getChildren();
        for (Resource child : childComponents) {
            addResourceTypesFromTree(child, resourceTypes);
        }
    }

    private void addResourceSuperTypes(String resourceType, Set<String> resourceTypes) {
        Resource resource = resolveResource(resolver, resourceType);
        if (resource == null) {
            return;
        }
        // Get resource superType path from the resource type.
        String superType = resource.getResourceSuperType();
        if (!StringUtils.isEmpty(superType)) {
            resourceTypes.add(superType);
        }
        addResourceSuperTypes(superType, resourceTypes);
    }

    private void addResourceTypesFromXF(Resource resource, Set<String> resourceTypes) {
        ExperienceFragment experienceFragment = modelFactory.getModelFromWrappedRequest(request, resource, ExperienceFragment.class);
        if (experienceFragment != null) {
            String fragmentPath = experienceFragment.getLocalizedFragmentVariationPath();
            if (StringUtils.isNotEmpty(fragmentPath)) {
                Resource fragmentResource = resolver.getResource(fragmentPath);
                if (fragmentResource != null) {
                    addResourceTypesFromTree(fragmentResource, resourceTypes);
                }
            }
        }
    }

    private static Resource resolveResource(ResourceResolver resolver, String path) {
        if (StringUtils.isEmpty(path)) {
            return null;
        }
        // Resolve absolute resource path.
        if (StringUtils.startsWith(path, "/")) {
            return resolver.getResource(path);
        }
        // Resolve relative resource path.
        for (String searchPath : resolver.getSearchPath()) {
            Resource resource = resolver.getResource(searchPath + path);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

}
