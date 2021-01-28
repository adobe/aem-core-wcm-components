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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.AssetDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Data builder for a Dam Assets.
 * This builder will produce a valid {@link AssetData} object.
 */
public final class AssetDataBuilder extends GenericDataBuilder<AssetDataBuilder, AssetData> {

    /**
     * Name of the property holding the name of smart tag;
     */
    public final static String SMARTTAG_NAME_PROP = "name";

    /**
     * Name of the property holding the confidence of smart tag;
     */
    public final static String SMARTTAG_CONFIDENCE_PROP = "confidence";

    /**
     * Construct an Asset Data Builder.
     *
     * @param supplier The data layer supplier.
     */
    AssetDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Sets the supplier that supplies the URL.
     *
     * @param supplier The URL value supplier.
     * @return A new {@link AssetDataBuilder}.
     * @see AssetData#getUrl()
     */
    @NotNull
    public AssetDataBuilder withUrl(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setUrl(supplier));
    }


    /**
     * Sets the supplier that supplies the Asset's format.
     *
     * @param supplier The format value supplier.
     * @return A new {@link AssetDataBuilder}.
     * @see AssetData#getFormat()
     */
    @NotNull
    public AssetDataBuilder withFormat(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setFormat(supplier));
    }

    /**
     * Sets the supplier that supplies the Asset's tags.
     *
     * @param supplier The tags value supplier.
     * @return A new {@link AssetDataBuilder}.
     * @see AssetData#getTags()
     */
    @NotNull
    public AssetDataBuilder withTags(@NotNull final Supplier<String[]> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setTags(supplier));
    }

    /**
     * Sets the supplier that supplies the Asset's smart tags.
     *
     * @param supplier The smart tags value supplier.
     * @return A new {@link AssetDataBuilder}.
     * @see AssetData#getSmartTags()
     */
    @NotNull
    public AssetDataBuilder withSmartTags(@NotNull final Supplier<Map<String, Object>> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setSmartTags(supplier));
    }

    /**
     * Set the supplier that supplies the component's last modified date.
     *
     * @param supplier The last modified date value supplier.
     * @return A new {@link AssetDataBuilder}.
     * @see AssetData#getLastModifiedDate()
     */
    @NotNull
    public AssetDataBuilder withLastModifiedDate(@NotNull final Supplier<Date> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setLastModifiedDate(supplier));
    }

    @Override
    @NotNull
    AssetDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new AssetDataBuilder(supplier);
    }

    @Override
    @NotNull
    public AssetData build() {
        return new AssetDataImpl(this.getDataLayerSupplier());
    }
}
