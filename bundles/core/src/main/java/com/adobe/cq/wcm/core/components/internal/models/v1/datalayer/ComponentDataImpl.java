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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerSupplier;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.function.Supplier;

/**
 * {@link DataLayerSupplier} backed component data implementation.
 */
public class ComponentDataImpl implements ComponentData {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentDataImpl.class);

    /**
     * The current data layer supplier.
     */
    @NotNull
    private final DataLayerSupplier dataLayerSupplier;

    /**
     * The ID field value.
     */
    private String id;

    /**
     * The type field value.
     */
    private String type;

    /**
     * The last modified date field value.
     */
    private Date lastModifiedDate;

    /**
     * The parent ID field value.
     */
    private String parentId;

    /**
     * The title field value.
     */
    private String title;

    /**
     * The description field value.
     */
    private String description;

    /**
     * The text field value.
     */
    private String text;

    /**
     * The link URL field value.
     */
    private String linkUrl;

    /**
     * Construct the data layer model.
     *
     * @param supplier The data layer supplier.
     */
    public ComponentDataImpl(@NotNull final DataLayerSupplier supplier) {
        this.dataLayerSupplier = supplier;
    }

    @Override
    @NotNull
    public final String getId() {
        if (this.id == null) {
            this.id = this.getDataLayerSupplier().getId().get();
        }
        return this.id;
    }

    @Override
    @Nullable
    public final String getType() {
        if (this.type == null) {
            this.type = this.getDataLayerSupplier()
                .getType()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.type;
    }

    @Override
    @Nullable
    public final Date getLastModifiedDate() {
        if (this.lastModifiedDate == null) {
            this.lastModifiedDate = this.getDataLayerSupplier()
                .getLastModifiedDate()
                .map(Supplier::get)
                .orElse(null);
        }
        if (this.lastModifiedDate != null) {
            return new Date(this.lastModifiedDate.getTime());
        }
        return null;
    }

    @Override
    @Nullable
    public final String getParentId() {
        if (this.parentId == null) {
            this.parentId = this.getDataLayerSupplier()
                .getParentId()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.parentId;
    }

    @Override
    @Nullable
    public final String getTitle() {
        if (this.title == null) {
            this.title = this.getDataLayerSupplier()
                .getTitle()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.title;
    }

    @Override
    @Nullable
    public final String getDescription() {
        if (this.description == null) {
            this.description = this.getDataLayerSupplier()
                .getDescription()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.description;
    }

    @Override
    @Nullable
    public final String getText() {
        if (this.text == null) {
            this.text = this.getDataLayerSupplier()
                .getText()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.text;
    }

    @Override
    @Nullable
    public final String getLinkUrl() {
        if (this.linkUrl == null) {
            this.linkUrl = this.getDataLayerSupplier()
                .getLinkUrl()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.linkUrl;
    }

    @Override
    @Nullable
    public final String getJson() {
        try {
            return String.format("{\"%s\":%s}",
                this.getId(),
                new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to generate dataLayer JSON string", e);
        }
        return null;
    }

    /**
     * Get the data layer supplier.
     *
     * @return The data layer supplier.
     */
    @NotNull
    protected final DataLayerSupplier getDataLayerSupplier() {
        return this.dataLayerSupplier;
    }
}
