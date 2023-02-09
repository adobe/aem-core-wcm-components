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
package com.adobe.cq.wcm.core.components.util;

import static com.adobe.cq.wcm.core.components.models.Page.NN_PAGE_FEATURED_IMAGE;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.models.Component;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility helper functions for components.
 */
public final class ComponentUtils {

    /**
     * Name of the node holding the context aware configurations below /conf/{site-name};
     */
    public static final String NN_SLING_CONFIGS = "sling:configs";

    /**
     * Name of the separator character used between prefix and hash when generating an ID, e.g. image-5c7e0ef90d
     */
    public static final String ID_SEPARATOR = "-";

    /**
     * Length of the ID hash.
     */
    private static final int ID_HASH_LENGTH = 10;


    private static final String DATALAYER_ATTRIBUTE_NAME = "isDataLayerEnabled";

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private ComponentUtils() {
        // NOOP
    }

    /**
     * Check if data layer is enabled.
     *
     * @param request  The request in which context this happens
     * @param resource The resource to check.
     * @return True if data layer is enabled for this resource, false otherwise.
     */
    public static boolean isDataLayerEnabled(@Nullable SlingHttpServletRequest request,
                                             @NotNull final Resource resource) {

        if (request == null) {
            return isDataLayerEnabled (resource);
        }
        Object storedValue = request.getAttribute(DATALAYER_ATTRIBUTE_NAME);
        if (storedValue != null) {
            return (Boolean) storedValue;
        } else {
            boolean isEnabled = isDataLayerEnabled (resource);
            request.setAttribute(DATALAYER_ATTRIBUTE_NAME, Boolean.valueOf(isEnabled));
            return isEnabled;
        }
    }

    /**
     * Check if data layer is enabled.
     *
     * @param resource The resource to check.
     * @return True if data layer is enabled for this resource, false otherwise.
     */
    public static boolean isDataLayerEnabled(@NotNull final Resource resource) {
        return Optional.ofNullable(resource.adaptTo(ConfigurationBuilder.class))
            .map(builder -> builder.as(DataLayerConfig.class))
            .map(DataLayerConfig::enabled)
            .orElse(false);
    }

    /**
     * See {@link #getId(Resource, Page, String, ComponentContext)}.
     *
     * @param resource The resource for which to get or generate an ID.
     * @param currentPage The current request page.
     * @param componentContext The current component context.
     * @return The ID property value for the specified resource, or a generated ID if not set.
     */
    @NotNull
    public static String getId(@NotNull final Resource resource,
                               @Nullable final Page currentPage,
                               @Nullable final ComponentContext componentContext) {
        return getId(resource, currentPage, null, componentContext);
    }

    /**
     * Get the ID property value if set (using {@link #getPropertyId(Resource)},
     * otherwise generate a new ID (using {@link #generateId(Resource, Page, String, ComponentContext)}.
     *
     * @param resource The resource for which to get or generate an ID.
     * @param currentPage The current request page.
     * @param resourceCallerPath The path of the page or template resource that references this component.
     * @param componentContext The current component context.
     * @return The ID property value for the specified resource, or a generated ID if not set.
     */
    @NotNull
    public static String getId(@NotNull final Resource resource,
                                                  @Nullable final Page currentPage,
                                                  @Nullable final String resourceCallerPath,
                                                  @Nullable final ComponentContext componentContext) {
        return ComponentUtils.getPropertyId(resource)
            .orElseGet(() -> ComponentUtils.generateId(resource, currentPage, resourceCallerPath, componentContext));
    }

    /**
     * Get the ID value from the &quot;{@value Component#PN_ID}&quot; property.
     *
     * @param resource The resource for which to get the ID property value.
     * @return The ID property value if set, empty optional if not.
     */
    private static Optional<String> getPropertyId(@NotNull final Resource resource) {
        return Optional.ofNullable(resource.getValueMap().get(Component.PN_ID, String.class))
            .filter(StringUtils::isNotEmpty)
            .map(StringUtils::trim)
            .map(StringUtils::normalizeSpace)
            .map(val -> StringUtils.replace(val, " ", ID_SEPARATOR));
    }

    /**
     * Returns an auto generated component ID.
     *
     * The ID is the first {@value ComponentUtils#ID_HASH_LENGTH} characters of an SHA-1 hexadecimal hash of the component path,
     * prefixed with the component name. Example: title-810f3af321
     *
     * If the component is referenced, the path is taken to be a concatenation of the component path,
     * with the path of the first parent context resource that exists on the page or in the template.
     * This ensures the ID is unique if the same component is referenced multiple times on the same page or template.
     *
     * Collision
     * ---------
     * c = expected collisions
     * c ~= (i^2)/(2m) - where i is the number of items and m is the number of possibilities for each item.
     * m = 16^n - for a hexadecimal string, where n is the number of characters.
     *
     * For i = 1000 components with the same name, and n = 10 characters:
     *
     * c ~= (1000^2)/(2*(16^10))
     * c ~= 0.00000045
     *
     * @return the auto generated component ID
     */
    @NotNull
    private static String generateId(@NotNull final Resource resource,
                                    @Nullable final Page currentPage,
                                    @Nullable final String resourceCallerPath,
                                    @Nullable final ComponentContext componentContext) {
        String resourceType = resource.getResourceType();
        String prefix = StringUtils.substringAfterLast(resourceType, "/");
        String path = resource.getPath();
        if (currentPage != null && componentContext != null) {
            PageManager pageManager = currentPage.getPageManager();
            Page resourcePage = pageManager.getContainingPage(resource);
            Template template = currentPage.getTemplate();
            boolean inCurrentPage = (resourcePage != null && StringUtils.equals(resourcePage.getPath(), currentPage.getPath()));
            boolean inTemplate = (template != null && path.startsWith(template.getPath()));
            if (resourceCallerPath != null) {
                path = resourceCallerPath.concat(resource.getPath());
            } else if (!inCurrentPage && !inTemplate) {
                ComponentContext parentContext = componentContext.getParent();
                while (parentContext != null) {
                    Resource parentContextResource = parentContext.getResource();
                    if (parentContextResource != null) {
                        Page parentContextPage = pageManager.getContainingPage(parentContextResource);
                        inCurrentPage = (parentContextPage != null && StringUtils.equals(parentContextPage.getPath(), currentPage.getPath()));
                        inTemplate = (template != null && parentContextResource.getPath().startsWith(template.getPath()));
                        if (inCurrentPage || inTemplate) {
                            path = parentContextResource.getPath().concat(resource.getPath());
                            break;
                        }
                    }
                    parentContext = parentContext.getParent();
                }
            }

        }

        return ComponentUtils.generateId(prefix, path);
    }

    /**
     * Returns an ID based on the prefix, the ID_SEPARATOR and a hash of the path, e.g. image-5c7e0ef90d
     *
     * @param prefix the prefix for the ID
     * @param path   the resource path
     * @return the generated ID
     */
    @NotNull
    public static String generateId(@NotNull final String prefix, @NotNull final String path) {
        return StringUtils.join(prefix, ID_SEPARATOR, StringUtils.substring(DigestUtils.sha256Hex(path), 0, ID_HASH_LENGTH));
    }

    /**
     * Returns the resource holding the properties of the featured image of the page.
     *
     * @param page the page
     * @return the featured image resource
     */
    @Nullable
    public static Resource getFeaturedImage(@NotNull Page page) {
        return page.getContentResource(NN_PAGE_FEATURED_IMAGE);
    }

    /**
     * Gets the effective {@link TemplatedResource} for the specified resource.
     *
     * @param resource The resource for which to get the TemplateResource.
     * @param request The current request. This is needed if the resource is potentially part of the template structure.
     * @return The TemplatedResource, or the current resource if it cannot be adapted to a TemplatedResource.
     */
    @NotNull
    public static Resource getEffectiveResource(@NotNull final Resource resource, @Nullable final SlingHttpServletRequest request) {
        if (resource instanceof TemplatedResource) {
            return resource;
        }

        Resource res = resource;
        while (res instanceof ResourceWrapper) {
            res = ((ResourceWrapper) res).getResource();
            if (res instanceof TemplatedResource) {
                return resource;
            }
        }

        return Optional.ofNullable((Resource) resource.adaptTo(TemplatedResource.class))
            .orElseGet(() -> Optional.ofNullable(request)
                .map(r -> (Resource) r.adaptTo(TemplatedResource.class))
                .orElse(resource));
    }

    /**
     * Gets a list of all child resources that are components.
     *
     * @param resource The resource for which to get the children.
     * @param request The current request. This is needed if the resource is potentially part of the template structure.
     * @return The list of child resources that are components.
     */
    @NotNull
    public static List<Resource> getChildComponents(@NotNull final Resource resource, @Nullable final SlingHttpServletRequest request) {
        return Optional.ofNullable(resource.getResourceResolver().adaptTo(ComponentManager.class))
            .map(componentManager ->
                StreamSupport.stream(ComponentUtils.getEffectiveResource(resource, request).getChildren().spliterator(), false)
                    .filter(res -> Objects.nonNull(componentManager.getComponentOfResource(res))))
            .orElseGet(Stream::empty)
            .collect(Collectors.toList());
    }

    /**
     * Gets the models for the child resources that are components, filtered by the Sling Model Filter.
     * This should only be used for JSON export, for other usages refer to {@link com.adobe.cq.wcm.core.components.models.Container#getChildren}.
     *
     * @param request The current request.
     * @param modelClass The child model class.
     * @return Map of models wherein the key is the child name, and the value is it's model.
     */
    public static LinkedHashMap<String, ComponentExporter> getComponentModels(@NotNull final SlingModelFilter slingModelFilter,
                                                                    @NotNull final ModelFactory modelFactory,
                                                                    @NotNull final SlingHttpServletRequest request,
                                                                    @NotNull final Class<ComponentExporter> modelClass) {
        return ComponentUtils.getComponentModels(
            slingModelFilter,
            modelFactory,
            ComponentUtils.getChildComponents(request.getResource(), request),
            request,
            modelClass);
    }

    /**
     * Gets the models for the child resources that are components, filtered by the Sling Model Filter.
     * This should only be used for JSON export, for other usages refer to {@link com.adobe.cq.wcm.core.components.models.Container#getChildren}
     *
     * @param request The current request.
     * @param modelClass The child model class.
     * @return Map of models wherein the key is the child name, and the value is it's model.
     */
    public static LinkedHashMap<String, ComponentExporter> getComponentModels(@NotNull final SlingModelFilter slingModelFilter,
                                                                    @NotNull final ModelFactory modelFactory,
                                                                    @NotNull final List<Resource> childComponents,
                                                                    @NotNull final SlingHttpServletRequest request,
                                                                    @NotNull final Class<ComponentExporter> modelClass) {
        LinkedHashMap<String, ComponentExporter> models = new LinkedHashMap<>();
        slingModelFilter.filterChildResources(childComponents).forEach(child -> {
            ComponentExporter model = modelFactory.getModelFromWrappedRequest(request, child, modelClass);
            if (model != null) {
                models.put(child.getName(), model);
            }
        });
        return models;
    }
}
