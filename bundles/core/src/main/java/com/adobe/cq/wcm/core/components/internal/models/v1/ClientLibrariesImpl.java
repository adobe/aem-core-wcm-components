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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ClientLibraries;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = ClientLibraries.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ClientLibrariesImpl implements ClientLibraries {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibrariesImpl.class);

    /**
     * Name of the subservice used to authenticate as in order to be able to read details about components and
     * client libraries.
     */
    public static final String COMPONENTS_SERVICE = "components-service";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    @Named(OPTION_RESOURCE_TYPES)
    Object resourceTypes;

    @Inject
    @Named(OPTION_FILTER_REGEX)
    String filterRegex;

    @Inject
    @Named(OPTION_INHERITED)
    @Default(booleanValues = OPTION_INHERITED_DEFAULT)
    boolean inherited;

    @Inject
    @Named(OPTION_CATEGORIES)
    private Object categories;

    @Inject
    @Named(OPTION_ASYNC)
    @Nullable
    private boolean async;

    @Inject
    @Named(OPTION_DEFER)
    @Nullable
    private boolean defer;

    @Inject
    @Named(OPTION_CROSSORIGIN)
    @Nullable
    private String crossorigin;

    @Inject
    @Named(OPTION_ONLOAD)
    @Nullable
    private String onload;

    @Inject
    @Named(OPTION_MEDIA)
    @Nullable
    private String media;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    @OSGiService
    ResourceResolverFactory resolverFactory;

    private Set<String> resourceTypeSet;
    private Pattern pattern;
    private String[] categoriesArray;

    @PostConstruct
    protected void initModel() {
        resourceTypeSet = Utils.getStrings(resourceTypes);
        if (StringUtils.isNotEmpty(filterRegex)) {
            pattern = Pattern.compile(filterRegex);
        }

        Set<String> categoriesSet = Utils.getStrings(categories);
        if (categoriesSet.isEmpty()) {
            categoriesSet = getCategoriesFromComponents();
        }

        categoriesArray = categoriesSet.toArray(new String[0]);
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

    /**
     * Returns the markup for including the client libraries into an HTML page
     *
     * @param type - the type of the client libraries
     *
     * @return Markup to include the client libraries
     */
    private String getLibIncludes(LibraryType type) {
        StringWriter sw = new StringWriter();
        try {
            if (categoriesArray == null || categoriesArray.length == 0)  {
                LOG.error("No categories detected. Please either specify the categories as a CSV string or a set of resource types for looking them up!");
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
            LOG.error("Failed to include client libraries {}", Arrays.toString(categoriesArray));
        }

        String html = sw.toString();
        // inject attributes from HTL into the JS and CSS HTML tags
        return getHtmlWithInjectedAttributes(html);
    }

    /**
     * Returns the HTML markup with the injected JS/CSS attributes
     *
     * @param html - the input html
     *
     * @return HTML with injected JS/CSS attributes
     */
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

    /**
     * Returns the HTML fragment for an attribute, based on its name and a flag to include or not
     *
     * @param name - the name of the attribute
     * @param include - {@code true} to include, {@code false} otherwise
     *
     * @return Fragment for the attribute
     */
    private String getHtmlAttr(String name, boolean include) {
        if (include) {
            return name + " ";
        }
        return "";
    }

    /**
     * Returns the HTML fragment for an attribute, based on its name and value
     *
     * @param name - the name of the attribute
     * @param value - the value of the attribute
     *
     * @return Fragment for the attribute
     */
    private String getHtmlAttr(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            return name + "=\"" + value + "\" ";
        }
        return "";
    }

    /**
     * Returns a concatenated string of the content of all the client libraries, given a library type.
     *
     * @param libraryType - the type of the library
     *
     * @return The concatenated string of the content of all the client libraries
     */
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

    /**
     * Returns the clientlib categories from the list of component resource types, filtered by the given filter.
     *
     * @return {@link Set<String>} of clientlib categories
     */
    @NotNull
    protected Set<String> getCategoriesFromComponents() {
        try (ResourceResolver resourceResolver = resolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, COMPONENTS_SERVICE))) {
            Set<String> categories = new HashSet<>();
            for (ClientLibrary library : this.getAllClientLibraries(resourceResolver)) {
                for (String category : library.getCategories()) {
                    if (pattern == null || pattern.matcher(category).matches()) {
                        categories.add(category);
                    }
                }
            }
            return categories;
        } catch (LoginException e) {
            LOG.error("Cannot login as a service user", e);
            return Collections.emptySet();
        }
    }

    /**
     * Gets all of the client libraries.
     *
     * @param resourceResolver The resource resolver.
     * @return Set of all client libraries.
     */
    @NotNull
    private Set<ClientLibrary> getAllClientLibraries(@NotNull final ResourceResolver resourceResolver) {
        Map<String, ClientLibrary> allLibraries = htmlLibraryManager.getLibraries();
        Set<ClientLibrary> clientLibraries = new LinkedHashSet<>();
        for (String resourceType : getAllResourceTypes(resourceResolver)) {
            Resource resource = resourceResolver.getResource(resourceType);
            if (resource != null) {
                clientLibraries.addAll(getClientLibraries(resource, allLibraries));
            }
        }
        return clientLibraries;
    }

    /**
     * Gets all resource types.
     *
     * @param resourceResolver The resource resolver.
     * @return Set of all resource types under which to search for client libraries.
     */
    @NotNull
    private Set<String> getAllResourceTypes(@NotNull final ResourceResolver resourceResolver) {
        Set<String> allResourceTypes = new LinkedHashSet<>(resourceTypeSet);
        if (inherited) {
            for (String resourceType : resourceTypeSet) {
                allResourceTypes.addAll(Utils.getSuperTypes(resourceType, resourceResolver));
            }
        }
        return allResourceTypes;
    }

    /**
     * Gets a list of client libraries, starting from the given resource
     * and diving into its descendants.
     *
     * @param resource - the given resource, which will be checked to see if it's a client library
     * @param allLibraries - Map of all client libraries.
     * @return List of client libraries for the given resource.
     */
    @NotNull
    private static List<ClientLibrary> getClientLibraries(@org.jetbrains.annotations.Nullable final Resource resource,
                                                          @NotNull final Map<String, ClientLibrary> allLibraries) {
        return Optional.ofNullable(resource)
            .map(Resource::getPath)
            .map(path -> allLibraries.entrySet().stream()
                .filter(entry -> entry.getKey().equals(path) || entry.getKey().startsWith(path + "/"))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList()))
            .orElseGet(Collections::emptyList);
    }

}
