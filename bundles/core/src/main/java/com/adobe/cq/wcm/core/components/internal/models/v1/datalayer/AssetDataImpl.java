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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplier;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.function.Supplier;

/**
 * {@link DataLayerSupplier} backed asset data model implementation.
 *
 * @see AssetData
 */
public final class AssetDataImpl implements AssetData {

    /**
     * The current data layer supplier.
     */
    @NotNull
    private final DataLayerSupplier dataLayerSupplier;

    /**
     * Construct an AssetData model.
     *
     * @param supplier The data layer supplier.
     */
    public AssetDataImpl(@NotNull final DataLayerSupplier supplier) {
        this.dataLayerSupplier = supplier;
    }

    @Override
    @NotNull
    public String getId() {
        return this.dataLayerSupplier.getId().get();
    }

    @Override
    public Date getLastModifiedDate() {
        return this.dataLayerSupplier
            .getLastModifiedDate()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    public String getFormat() {
        return this.dataLayerSupplier
            .getFormat()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    public String getUrl() {
        return this.dataLayerSupplier
            .getUrl()
            .map(Supplier::get)
            .orElse(null);
    }

    @Override
    public String[] getTags() {
        return this.dataLayerSupplier
            .getTags()
            .map(Supplier::get)
            .orElse(null);
    }
}
