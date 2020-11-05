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
package com.adobe.cq.wcm.core.components.models.datalayer.builder;

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ImageDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Data builder for image components.
 */
public final class ImageComponentDataBuilder extends GenericComponentDataBuilder<ImageComponentDataBuilder, ImageData> {

    /**
     * Construct a data layer builder for an image component.
     *
     * @param supplier The data layer supplier.
     */
    ImageComponentDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Set the supplier that supplies the component's asset data.
     *
     * @param supplier The asset data value supplier.
     * @return A new {@link ImageComponentDataBuilder}.
     */
    @NotNull
    public ImageComponentDataBuilder withAssetData(@NotNull final Supplier<AssetData> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setAssetData(supplier));
    }

    @Override
    @NotNull
    ImageComponentDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new ImageComponentDataBuilder(supplier);
    }

    @NotNull
    @Override
    public ImageData build() {
        return new ImageDataImpl(this.getDataLayerSupplier());
    }
}
