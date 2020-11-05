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
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * {@link DataLayerSupplier} backed image component data implementation.
 */
public final class ImageDataImpl extends ComponentDataImpl implements ImageData {

    /**
     * The asset field value.
     */
    private AssetData assetData;

    /**
     * Construct the data layer model.
     *
     * @param supplier The data layer supplier.
     */
    public ImageDataImpl(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    @Override
    @Nullable
    public AssetData getAssetData() {
        if (this.assetData == null) {
            this.assetData = this.getDataLayerSupplier()
                .getAssetData()
                .map(Supplier::get)
                .orElse(null);
        }
        return this.assetData;
    }
}
