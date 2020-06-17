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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.Template;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ClientLibraries.class}
)
public class ClientLibrariesImpl implements ClientLibraries {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibrariesImpl.class);

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
    private String categories;

    @Inject
    private String categoryFilter;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    private Map<String, ClientLibrary> allLibraries;
    private Collection<ClientLibrary> libraries;
    private String[] categoriesArray;

    @PostConstruct
    protected void initModel() {
        Set<String> categoriesSet = new HashSet<>();

        if (StringUtils.isNotBlank(categories)) {
            if (categories.contains(",")) {
                Collections.addAll(categoriesSet, categories.split(","));
            } else {
                categoriesSet.add(categories);
            }
        } else {
            // retrieve all the clientlibs defined for the resource, its descendants and its super types
            populateClientLibraries();

            // add categories defined by the clientlibs
            for (ClientLibrary library : libraries) {
                String[] libraryCategories = library.getCategories();
                categoriesSet.addAll(Arrays.asList(libraryCategories));
            }

            // add categories defined in the page policy and page design
            addPageClientLibCategories(categoriesSet);

            // add categories injected by the HTL template
            if (StringUtils.isNotBlank(additionnalCategories)) {
                if (additionnalCategories.contains(",")) {
                    Collections.addAll(categoriesSet, additionnalCategories.split(","));
                } else {
                    categoriesSet.add(additionnalCategories);
                }
            }

            // filter the categories based on category name regex
            if (StringUtils.isNotBlank(categoryFilter)) {
                Pattern p = Pattern.compile(categoryFilter);
                categoriesSet.removeIf(category -> {
                    Matcher m = p.matcher(category);
                    return !m.find();
                });
            }
        }

        categoriesArray = categoriesSet.toArray(new String[0]);
    }

    public String getJsTags() {
        return getLibTags("js");
    }

    public String getCssTags() {
        return getLibTags("css");
    }

    public String getLibTags() {
        return getLibTags("");
    }

    private String getLibTags(String type) {
        StringWriter sw = new StringWriter();
        try {
            if (categoriesArray == null || categoriesArray.length == 0)  {
                LOG.error("'categories' option might be missing from the invocation of the /libs/granite/sightly/templates/clientlib.html" +
                    "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if ("js".equalsIgnoreCase(type)) {
                    htmlLibraryManager.writeJsInclude(request, out, categoriesArray);
                } else if ("css".equalsIgnoreCase(type)) {
                    htmlLibraryManager.writeCssInclude(request, out, categoriesArray);
                } else {
                    htmlLibraryManager.writeIncludes(request, out, categoriesArray);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to include client libraries {}", categoriesArray);
        }
        return sw.toString();
    }

    @NotNull
    @Override
    public Set<String> getJsPaths() {
        return getLibsPaths(LibraryType.JS);
    }

    @NotNull
    @Override
    public Set<String> getCssPaths() {
        return getLibsPaths(LibraryType.CSS);
    }

    @NotNull
    @Override
    public String getInlineJS() {
        return getInline(LibraryType.JS);
    }

    @NotNull
    @Override
    public String getInlineCSS() {
        return getInline(LibraryType.CSS);
    }

    private Set<String> getLibsPaths(LibraryType libraryType) {
        Set<String> paths = new HashSet<>();
        Collection<ClientLibrary> clientlibs = htmlLibraryManager.getLibraries(categoriesArray, libraryType, true, false);
        // Iterate through the clientlibs and aggregate their content.
        for (ClientLibrary clientlib : clientlibs) {
            HtmlLibrary htmlLibrary = htmlLibraryManager.getLibrary(libraryType, clientlib.getPath());
            if (htmlLibrary != null) {
                paths.add(htmlLibrary.getPath(htmlLibraryManager.isMinifyEnabled()));
            }
        }
        return paths;
    }

    private String getInline(LibraryType libraryType) {
        Collection<ClientLibrary> clientlibs = htmlLibraryManager.getLibraries(categoriesArray, libraryType, true, false);
        // Iterate through the clientlibs and aggregate their content.
        StringBuilder output = new StringBuilder();
        for (ClientLibrary clientlib : clientlibs) {
            HtmlLibrary htmlLibrary = htmlLibraryManager.getLibrary(libraryType, clientlib.getPath());
            if (htmlLibrary != null) {
                try {
                    output.append(IOUtils.toString(htmlLibrary.getInputStream(htmlLibraryManager.isMinifyEnabled()),
                        StandardCharsets.UTF_8));
                } catch (IOException e) {
                    LOG.error("Error getting input stream from clientlib with path '{}'.", clientlib.getPath());
                }
            }
        }
        return output.toString();
    }

    @NotNull
    @Override
    public String[] getCategories() {
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
