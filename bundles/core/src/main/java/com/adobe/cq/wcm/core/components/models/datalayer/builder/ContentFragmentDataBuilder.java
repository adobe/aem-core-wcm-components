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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ContentFragmentDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import org.jetbrains.annotations.NotNull;
import java.util.function.Supplier;

/**
 * Data builder for content fragment components.
 */
public final class ContentFragmentDataBuilder extends GenericComponentDataBuilder<ContentFragmentDataBuilder, ContentFragmentData> {

    /**
     * Construct a data layer builder for a content fragment component.
     *
     * @param supplier The data layer supplier.
     */
    ContentFragmentDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Set the supplier that supplies the content fragment data.
     *
     * @param supplier The content fragment data value supplier.
     * @return A new {@link ContentFragmentDataBuilder}.
     */
    @NotNull
    public ContentFragmentDataBuilder withElementsData(@NotNull final Supplier<ContentFragmentData.ElementData[]> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setContentFragmentElements(supplier));
    }

    @Override
    @NotNull
    ContentFragmentDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new ContentFragmentDataBuilder(supplier);
    }

    @NotNull
    @Override
    public ContentFragmentData build() {
        return new ContentFragmentDataImpl(this.getDataLayerSupplier());
    }
}
