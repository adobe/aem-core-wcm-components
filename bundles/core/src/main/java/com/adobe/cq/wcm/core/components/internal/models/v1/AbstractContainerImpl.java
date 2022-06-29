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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl extends AbstractComponentImpl implements Container {

    @Self
    protected LinkManager linkManager;

    /**
     * The current style for this component.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Nullable
    protected Style currentStyle;

    /**
     * The sling model factory service.
     */
    @OSGiService
    protected SlingModelFilter slingModelFilter;

    /**
     * The model factory.
     */
    @OSGiService
    protected ModelFactory modelFactory;

    /**
     * The list of child items.
     */
    protected List<ListItem> items;

    /**
     * The list of child resources that are components.
     */
    protected List<Resource> childComponents;

    /**
     * The child resources to be exported.
     */
    protected List<Resource> filteredChildComponents;

    /**
     * Map of the child items to be exported wherein the key is the child name, and the value is the child model.
     */
    protected Map<String, ? extends ComponentExporter> itemModels;

    /**
     * The name of the child resources in the order they are to be exported.
     */
    private String[] exportedItemsOrder;

    /**
     * The background style string for this container component.
     */
    private String backgroundStyle;

    /**
     * Return (and cache) the list of children resources that are components
     *
     * @return List of all children resources that are components.
     */
    @NotNull
    protected List<Resource> getChildren() {
        if (childComponents == null) {
            Resource effectiveResource = this.getEffectiveResource();
            childComponents = Optional.ofNullable(request.getResourceResolver().adaptTo(ComponentManager.class))
                .map(componentManager ->
                    StreamSupport.stream(effectiveResource.getChildren().spliterator(), false)
                        .filter(res -> Objects.nonNull(componentManager.getComponentOfResource(res))))
                .orElseGet(Stream::empty)
                .collect(Collectors.toList());
        }
        return childComponents;
    }

    /**
     * Return (and cache) the list of children resources that are components, filtered by the Sling Model Filter. This
     * should only be used for JSON export, for other usages refer to {@link AbstractContainerImpl#getChildren}.
     *
     * @return The list of children resources available for JSON export.
     */
    @NotNull
    protected List<Resource> getFilteredChildren() {
        if (filteredChildComponents == null) {
            filteredChildComponents = new LinkedList<>();
            slingModelFilter.filterChildResources(getChildren())
                .forEach(filteredChildComponents::add);
        }
        return filteredChildComponents;
    }

    /**
     * Get the list of items in the container.
     *
     * @return The list of items in the container.
     */
    @NotNull
    protected abstract List<? extends ListItem> readItems();

    /**
     * Get the background colour.
     *
     * @return The background colour if set, empty if background colour is not enabled or not defined.
     */
    private Optional<String> getBackgroundColor() {
        return Optional.ofNullable(this.currentStyle)
            .filter(style -> style.get(PN_BACKGROUND_COLOR_ENABLED, Boolean.FALSE))
            .flatMap(style -> Optional.ofNullable(this.resource.getValueMap().get(PN_BACKGROUND_COLOR, String.class)))
            .filter(StringUtils::isNotEmpty);
    }

    /**
     * Get the background image.
     *
     * @return The background image if set, empty if background image is not enabled or not defined.
     */
    private Optional<String> getBackgroundImage() {
        return Optional.ofNullable(this.currentStyle)
            .filter(style -> style.get(PN_BACKGROUND_IMAGE_ENABLED, Boolean.FALSE))
            .flatMap(style -> Utils.getOptionalLink(linkManager.get(resource).withLinkUrlPropertyName(PN_BACKGROUND_IMAGE_REFERENCE).build()).map(Link::getURL))
            .filter(StringUtils::isNotEmpty);
    }

    @Override
    @JsonIgnore
    @NotNull
    public List<ListItem> getItems() {
        if (items == null) {
            items = readItems().stream().map(i -> (ListItem) i).collect(Collectors.toList());
        }
        return items;
    }

    @Nullable
    @Override
    public String getBackgroundStyle() {
        if (this.backgroundStyle == null) {
            StringBuilder styleBuilder = new StringBuilder();
            getBackgroundImage().ifPresent(image -> styleBuilder.append("background-image:url(").append(image).append(");background-size:cover;background-repeat:no-repeat;"));
            getBackgroundColor().ifPresent(color -> styleBuilder.append("background-color:").append(color).append(";"));
            this.backgroundStyle = styleBuilder.toString();
        }

        if (StringUtils.isEmpty(this.backgroundStyle)) {
            return null;
        }
        return this.backgroundStyle;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (itemModels == null) {
            itemModels = getItemModels(request, ComponentExporter.class);
        }
        return itemModels;
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        if (exportedItemsOrder == null) {
            Map<String, ? extends ComponentExporter> models = getExportedItems();
            if (!models.isEmpty()) {
                exportedItemsOrder = models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
            } else {
                exportedItemsOrder = ArrayUtils.EMPTY_STRING_ARRAY;
            }
        }
        return Arrays.copyOf(exportedItemsOrder, exportedItemsOrder.length);
    }

    /**
     * Get the models for the child resources as provided by {@link AbstractContainerImpl#getFilteredChildren()}.
     *
     * @param request The current request.
     * @param modelClass The child model class.
     * @return Map of models wherein the key is the child name, and the value is it's model.
     */
    protected Map<String, ComponentExporter> getItemModels(@NotNull final SlingHttpServletRequest request,
                                                           @NotNull final Class<ComponentExporter> modelClass) {
        Map<String, ComponentExporter> models = new LinkedHashMap<>();
        getFilteredChildren().forEach(child -> {
            ComponentExporter model = modelFactory.getModelFromWrappedRequest(request, child, modelClass);
            if (model != null) {
                models.put(child.getName(), model);
            }
        });
        return models;
    }

    /**
     * Get the effective {@link TemplatedResource} for the current resource.
     *
     * @return The TemplatedResource, or the current resource if it cannot be adapted to a TemplatedResource.
     */
    @NotNull
    protected Resource getEffectiveResource() {
        if (this.resource instanceof TemplatedResource) {
            return this.resource;
        }
        return Optional.ofNullable((Resource)this.resource.adaptTo(TemplatedResource.class))
                .orElse(Optional.ofNullable((Resource)this.request.adaptTo(TemplatedResource.class))
                .orElse(this.resource));
    }

    /*
     * DataLayer specific methods
     */

    @Override
    @NotNull
    protected ContainerData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asContainer()
            .withShownItems(this::getDataLayerShownItems)
            .build();
    }

    @JsonIgnore
    public abstract String[] getDataLayerShownItems();

}
