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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {ClientLibraries.class}
)
public class ClientLibrariesImpl implements ClientLibraries {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibrariesImpl.class);

    private static final String NN_CQ_CLIENT_LIBRARY_FOLDER = "cq:ClientLibraryFolder";

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
    @Optional
    @Nullable
    @Named("categories")
    private String categoriesCsv;

    @Inject
    @Optional
    @Nullable
    @Named("resourceTypes")
    private String resourceTypesCsv;

    @Inject
    @Optional
    @Nullable
    private String filter;

    @Inject
    @Optional
    private boolean async;

    @Inject
    @Optional
    private boolean defer;

    @Inject
    @Optional
    @Nullable
    private String crossorigin;

    @Inject
    @Optional
    @Nullable
    private String onload;

    @Inject
    @Optional
    @Nullable
    private String media;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    private Set<String> resourceTypes;
    private Map<String, ClientLibrary> allLibraries;
    private Collection<ClientLibrary> libraries;
    private String[] categoriesArray;

    @PostConstruct
    protected void initModel() {
        Set<String> categoriesSet = new HashSet<>();

        if (StringUtils.isNotBlank(categoriesCsv)) {
            if (categoriesCsv.contains(",")) {
                Collections.addAll(categoriesSet, categoriesCsv.split(","));
            } else {
                categoriesSet.add(categoriesCsv);
            }
        } else {
            populateResourceTypes();
            // retrieve all the clientlibs defined for the resource, its descendants and its super types
            populateClientLibraries();

            // add categories defined by the clientlibs
            for (ClientLibrary library : libraries) {
                String[] libraryCategories = library.getCategories();
                categoriesSet.addAll(Arrays.asList(libraryCategories));
            }

            // add categories defined in the page policy and page design
            addPageClientLibCategories(categoriesSet);

            // filter the categories based on category name regex
            if (StringUtils.isNotBlank(filter)) {
                Pattern p = Pattern.compile(filter);
                categoriesSet.removeIf(category -> {
                    Matcher m = p.matcher(category);
                    return !m.find();
                });
            }
        }

        categoriesArray = categoriesSet.toArray(new String[0]);
    }

    @NotNull
    @Override
    public String[] getCategories() {
        return Arrays.copyOf(categoriesArray, categoriesArray.length);
    }

    @NotNull
    @Override
    public String getJsInline() {
        return getInline(LibraryType.JS);
    }

    @NotNull
    @Override
    public String getCssInline() {
        return getInline(LibraryType.CSS);
    }

    @Override
    public String getJsIncludes() {
        return getLibIncludes(LibraryType.JS);
    }

    @Override
    public String getCssIncludes() {
        return getLibIncludes(LibraryType.CSS);
    }

    @Override
    public String getJsAndCssIncludes() {
        return getLibIncludes(null);
    }

    private String getLibIncludes(LibraryType type) {
        StringWriter sw = new StringWriter();
        try {
            if (categoriesArray == null || categoriesArray.length == 0)  {
                LOG.error("'categories' option might be missing from the invocation of the /libs/granite/sightly/templates/clientlib.html" +
                    "client libraries template library. Please provide a CSV list or an array of categories to include.");
            } else {
                PrintWriter out = new PrintWriter(sw);
                if (type == LibraryType.JS) {
                    htmlLibraryManager.writeJsInclude(request, out, categoriesArray);
                } else if (type == LibraryType.CSS) {
                    htmlLibraryManager.writeCssInclude(request, out, categoriesArray);
                } else {
                    htmlLibraryManager.writeIncludes(request, out, categoriesArray);
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to include client libraries {}", categoriesArray);
        }

        String html = sw.toString();
        // inject attributes from HTL into the JS and CSS HTML tags
        return getHtmlWithInjectedAttributes(html);
    }

    private String getHtmlWithInjectedAttributes(String html) {
        StringBuilder jsAttributes = new StringBuilder();
        jsAttributes.append(getHtmlAttr(OPTION_ASYNC, async));
        jsAttributes.append(getHtmlAttr(OPTION_DEFER, defer));
        jsAttributes.append(getHtmlAttr(OPTION_CROSSORIGIN, crossorigin));
        jsAttributes.append(getHtmlAttr(OPTION_ONLOAD, onload));
        StringBuilder cssAttributes = new StringBuilder();
        cssAttributes.append(getHtmlAttr(OPTION_MEDIA, media));
        String updatedHtml = StringUtils.replace(html,"<script ", "<script " + jsAttributes.toString());
        return StringUtils.replace(updatedHtml,"<link ", "<link " + cssAttributes.toString());
    }

    private String getHtmlAttr(String name, boolean include) {
        if (include) {
            return name + " ";
        }
        return "";
    }

    private String getHtmlAttr(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            return name + "=\"" + value + "\" ";
        }
        return "";
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

    private void populateResourceTypes() {
        resourceTypes = new HashSet<>();
        if (StringUtils.isNotBlank(resourceTypesCsv)) {
            if (resourceTypesCsv.contains(",")) {
                Collections.addAll(resourceTypes, resourceTypesCsv.split(","));
            } else {
                resourceTypes.add(resourceTypesCsv);
            }
        } else {
            resourceTypes = Utils.getAllResourceTypes(resolver, modelFactory, currentPage.getPageManager(), request, resource);
        }
    }

    private void populateClientLibraries() {
        allLibraries = htmlLibraryManager.getLibraries();
        libraries = new ArrayList<>();

        // get the clientlibs defined by the resource types
        for (String resourceType : resourceTypes) {
            Resource componentRes = resolver.getResource(resourceType);
            addClientLibraries(componentRes, libraries);
        }
    }

    private void addPageClientLibCategories(Set<String> categories) {
        // if the resource is based on a page model
        Page pageModel = request.adaptTo(Page.class);
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

}
