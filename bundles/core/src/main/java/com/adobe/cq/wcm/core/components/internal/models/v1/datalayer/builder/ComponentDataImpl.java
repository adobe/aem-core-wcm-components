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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder;

import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
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
 *
 * This implementation supports:
 * <ul>
 *     <li>{@link ComponentData}</li>
 *     <li>{@link ImageData}</li>
 *     <li>{@link ContainerData}</li>
 *     <li>{@link PageData}</li>
 * </ul>
 */
public final class ComponentDataImpl implements ComponentData, ImageData, ContainerData, PageData {

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
     * Construct the data layer model.
     *
     * @param supplier The data layer supplier.
     */
    public ComponentDataImpl(@NotNull final DataLayerSupplier supplier) {
        this.dataLayerSupplier = supplier;
    }

    @Override
    @NotNull
    public String getId() {
        return this.getDataLayerSupplier().getId().get();
    }

    @Override
    @Nullable
    public String getType() {
        return this.getDataLayerSupplier()
            .getType()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public Date getLastModifiedDate() {
        return this.getDataLayerSupplier()
            .getLastModifiedDate()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getParentId() {
        return this.getDataLayerSupplier()
            .getParentId()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getTitle() {
        return this.getDataLayerSupplier()
            .getTitle()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getDescription() {
        return this.getDataLayerSupplier()
            .getDescription()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getText() {
        return this.getDataLayerSupplier()
            .getText()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getLinkUrl() {
        return this.getDataLayerSupplier()
            .getLinkUrl()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String[] getShownItems() {
        return this.getDataLayerSupplier()
            .getShownItems()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public AssetData getAssetData() {
        return this.getDataLayerSupplier()
            .getAssetData()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getTemplatePath() {
        return this.getDataLayerSupplier()
            .getTemplatePath()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getLanguage() {
        return this.getDataLayerSupplier()
            .getLanguage()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String[] getTags() {
        return this.getDataLayerSupplier()
            .getTags()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getUrl() {
        return this.getDataLayerSupplier()
            .getUrl()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    @Nullable
    public String getJson() {
        return ComponentDataImpl.getJson(this);
    }

    /**
     * Get the JSOn for component data.
     *
     * @param data The component data.
     * @return The JSON.
     */
    @Nullable
    static String getJson(@NotNull final ComponentData data) {
        try {
            return String.format("{\"%s\":%s}",
                data.getId(),
                new ObjectMapper().writeValueAsString(data));
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
    protected DataLayerSupplier getDataLayerSupplier() {
        return this.dataLayerSupplier;
    }
}
