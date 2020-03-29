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
package com.adobe.cq.wcm.core.extensions.amp.internal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.adobe.cq.wcm.core.extensions.amp.services.ClientLibraryAggregatorService;
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
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since com.adobe.cq.wcm.core.components.models 12.11.0
 */
@Component(service = ClientLibraryAggregatorService.class,
    configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(
    ocd = ClientLibraryAggregatorServiceImpl.Cfg.class
)
public class ClientLibraryAggregatorServiceImpl implements ClientLibraryAggregatorService {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibraryAggregatorServiceImpl.class);

    private static final String CATEGORIES = "categories";

    private static Map<String, LibraryType> libraryTypeMap;

    static {
        libraryTypeMap = new HashMap<>();
        libraryTypeMap.put("css", LibraryType.CSS);
        libraryTypeMap.put("js", LibraryType.JS);
    }

    @Reference
    private HtmlLibraryManager htmlLibraryManager;

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String resourceTypeRegex;
    private String headlibName;

    /**
     * Reads the service's configuration when the service is started.
     * @param cfg The service's configuration.
     */
    @Activate
    @Modified
    protected final void activate(ClientLibraryAggregatorServiceImpl.Cfg cfg) {
        this.resourceTypeRegex = cfg.resource_type_regex();
        this.headlibName = cfg.headlib_name();
    }

    protected String[] getClientLibArrayCategories(String categoryCsv) {
        String[] categories;
        if (categoryCsv.contains(",")) {
            categories = categoryCsv.split(",");
        } else {
            categories = new String[] {categoryCsv};
        }
        return categories;
    }

    protected LibraryType getClientLibType(String type) {
        return libraryTypeMap.get(type);
    }

    /**
     * Returns the aggregated content of the specified clientlib type from the given comma delimited list of categories.
     * @param categoryCsv Comma delimited list of clientlib categories to read the content from.
     * @param type The type of clientlib content to retrieve. Typically 'css' or 'js'.
     * @return String of all aggregated clientlib content.
     */
    @Override
    public String getClientLibOutput(String categoryCsv, String type) {

        // Validate the parameters.
        if (StringUtils.isBlank(categoryCsv)) {
            return "";
        }
        if (!libraryTypeMap.containsKey(type)) {
            LOG.error("No client libraries of type '{}'.", type);
            return "";
        }

        // Parse the array of categories from the comma delimited category string.
        String[] categories = getClientLibArrayCategories(categoryCsv);

        // Retrieve the clientlibs of the categories of the specified type.
        LibraryType libraryType = getClientLibType(type);
        Collection<ClientLibrary> libraries = htmlLibraryManager.getLibraries(categories, libraryType, false, true);

        // Iterate through the clientlibs and aggregate their content.
        StringBuilder output = new StringBuilder();
        for (ClientLibrary clientlib : libraries) {
            HtmlLibrary library = htmlLibraryManager.getLibrary(libraryType, clientlib.getPath());
            if (library != null) {
                try {
                    output.append(IOUtils.toString(library.getInputStream(htmlLibraryManager.isMinifyEnabled()),
                        StandardCharsets.UTF_8));
                } catch (IOException e) {
                    LOG.error("Error getting input stream from clientlib with path '{}'.", clientlib.getPath());
                }
            }
        }

        return output.toString();
    }

    /**
     * Returns the aggregated content of the specified clientlib type from the given categories and all categories of
     * the given resource types.
     * @param categoryCsv Comma separated list of clientlib categories to include.
     * @param type The type of clientlib content to retrieve. Typically 'css' or 'js'.
     * @param resourceTypes The set of resource types to retrieve clientlib content for.
     * @param primaryPath The relative path of the target clientlibs from the given resource types.
     * @param fallbackPath The relative path of the target clientlibs from the given resource types if none is found
     *                     using the primary path.
     * @return String of all aggregated clientlib content.
     */
    @Override
    public String getClientLibOutput(String categoryCsv, String type, Set<String> resourceTypes, String primaryPath,
        String fallbackPath) {

        // Validate the given path values.
        boolean primaryPathBlank = StringUtils.isBlank(primaryPath);
        boolean fallbackPathBlank = StringUtils.isBlank(fallbackPath);
        if (primaryPathBlank && fallbackPathBlank) {
            LOG.debug("Resource type clientlib aggregator must have a path value.");
            return "";
        }

        // Initialize the category list with the comma separated category values.
        List<String> categories = new ArrayList<>();
        if (StringUtils.isNotBlank(categoryCsv)) {
            if (categoryCsv.contains(",")) {
                Collections.addAll(categories, categoryCsv.split(","));
            } else {
                categories.add(categoryCsv);
            }
        }

        try (ResourceResolver resolver = getClientlibResourceResolver()) {

            // Iterate through each resource type and retrieve its clientlib categories.
            for (String resourceType : resourceTypes) {

                // Resolve the resource type's clientlib.
                Resource clientlib = null;
                if (!primaryPathBlank) {
                    clientlib = AmpUtil.resolveResource(resolver, resourceType + "/" + primaryPath);
                }
                if (clientlib == null) {
                    if (!fallbackPathBlank) {
                        clientlib = AmpUtil.resolveResource(resolver, resourceType + "/" + fallbackPath);
                        if (clientlib == null) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }

                // Retrieve the resource type's clientlib categories.
                String[] resourceCategories = clientlib.getValueMap().get(CATEGORIES, String[].class);
                if (resourceCategories != null) {
                    categories.addAll(Arrays.asList(resourceCategories));
                }
            }
        } catch (LoginException e) {
            LOG.error("Unable to get the service resource resolver.");
        }

        return getClientLibOutput(StringUtils.join(categories, ","), type);
    }

    @Override
    public String getResourceTypeRegex() {
        return this.resourceTypeRegex;
    }

    @Override
    public String getHeadlibName() {
        return this.headlibName;
    }

    @Override
    public ResourceResolver getClientlibResourceResolver() throws LoginException {
        return resolverFactory.getServiceResourceResolver(
            Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, AmpUtil.CLIENTLIB_SUBSERVICE));
    }

    @ObjectClassDefinition(name = "Client Library Aggregator Service")
    public @interface Cfg {

        /**
         * The name used for AMP js head library files.
         */
        @AttributeDefinition(
            name = "Headlib Name",
            description = "The name used for AMP js head library files.")
        String headlib_name() default "customheadlibs.amp.html";

        /**
         * Regex defining valid resource type paths while aggregating client libraries.
         */
        @AttributeDefinition(
            name = "Resource Type Regex",
            description = "Regex defining valid resource type paths while aggregating client libraries.")
        String resource_type_regex() default StringUtils.EMPTY;
    }
}
