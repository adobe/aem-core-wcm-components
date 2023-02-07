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

import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.PanelContainerItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * An individual panel found within a {@link com.adobe.cq.wcm.core.components.models.PanelContainer}.
 */
public final class PanelContainerItemImpl implements PanelContainerItem {

    /**
     * Prefix prepended to the item ID.
     */
    private static final String PANEL_ITEM_ID_PREFIX = "item";

    /**
     * The resource for this container item.
     */
    @NotNull
    private final Resource resource;

    /**
     * The ID of this panel.
     */
    @NotNull
    private final String id;

    /**
     * The data type of this panel.
     */
    @NotNull
    private final String dataType;

    /**
     * Construct a panel.
     *
     * @param resource The container item for this panel.
     * @param container The container component that contains this panel.
     */
    public PanelContainerItemImpl(@NotNull final Resource resource, @NotNull final Component container) {
        this.resource = resource;
        this.id = ComponentUtils.generateId(
                StringUtils.join(container.getId(), ComponentUtils.ID_SEPARATOR, PANEL_ITEM_ID_PREFIX),
                this.resource.getPath());
        this.dataType =  String.join("/",
                Optional.ofNullable(container.getData()).map(ComponentData::getType).orElseGet(container::getExportedType),
                PANEL_ITEM_ID_PREFIX);
    }


    @Override
    @NotNull
    public Resource getResource() {
        return this.resource;
    }

    @Override
    @NotNull
    public String getName() {
        return resource.getName();
    }

    @Override
    @Nullable
    public String getTitle() {
        return Optional.ofNullable(this.resource.getValueMap().get(PN_PANEL_TITLE, String.class))
            .orElseGet(() -> this.resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class));
    }

    @Override
    @NotNull
    public String getId() {
        return this.id;
    }

    /**
     * Gets the last modified date of the container item resource, or the creation date if no modification date is set.
     *
     * @return The date that the container item resource was modified, or created, or null if neither property exists.
     */
    private Date getLastModifiedDate() {
        return // Note: this can be simplified in JDK 11
            Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_LASTMODIFIED, Calendar.class))
                .map(Calendar::getTime)
                .orElseGet(() ->
                    Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_CREATED, Calendar.class))
                        .map(Calendar::getTime)
                        .orElse(null));
    }

    @Override
    @NotNull
    public ComponentData getData() {
        return DataLayerBuilder.forComponent()
            .withId(this::getId)
            .withLastModifiedDate(this::getLastModifiedDate)
            .withType(() -> this.dataType)
            .withTitle(this::getTitle)
            .build();
    }

    /**
     * Gets the component exporter for this container item.
     *
     * @param slingModelFilter The sling model filter service.
     * @param modelFactory The model factory service.
     * @param request The current request.
     * @return A single map entry with the resource name as the key and the component exporter as the value if the resource
     * can be adapted to a component exporter via the model factory, empty if not.
     */
    public Optional<java.util.Map.Entry<String, ComponentExporter>> getComponentExporter(@NotNull final SlingModelFilter slingModelFilter,
                                                                                         @NotNull final ModelFactory modelFactory,
                                                                                         @NotNull SlingHttpServletRequest request) {
        return ComponentUtils.getComponentModels(slingModelFilter, modelFactory, Collections.singletonList(this.getResource()), request, ComponentExporter.class)
            .entrySet().stream()
            .findFirst()
            .map(i -> new AbstractMap.SimpleEntry<>(i.getKey(), new JsonWrapper(i.getValue())));

    }

    /**
     * Wrapper class used to add specific properties of the container items to the JSON serialization of the underlying container item model
     */
    private final class JsonWrapper implements ComponentExporter {

        /**
         * The wrapped ComponentExporter.
         */
        @NotNull
        private final ComponentExporter inner;


        /**
         * Construct the wrapper.
         *
         * @param inner The ComponentExporter to be wrapped.
         */
        JsonWrapper(@NotNull final ComponentExporter inner) {
            this.inner = inner;
        }

        /**
         * Get the underlying ComponentExporter that is wrapped by this wrapper.
         *
         * @return the underlying container item model
         */
        @JsonUnwrapped
        @NotNull
        public ComponentExporter getInner() {
            return this.inner;
        }

        /**
         * Get the panel title.
         *
         * @return the container item title
         */
        @JsonProperty(PanelContainerItemImpl.PN_PANEL_TITLE)
        @JsonInclude()
        public String getPanelTitle() {
            return getTitle();
        }

        @NotNull
        @Override
        @JsonIgnore
        public String getExportedType() {
            return this.inner.getExportedType();
        }
    }
}
