/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.util.Map;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.EmbeddableDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.EmbeddableData;

/**
 * Data builder for an Embeddable.
 * This builder produces a valid {@link EmbeddableData} object.
 *
 * @since com.adobe.cq.wcm.core.components.models.datalayer.builder 1.2.0
 */
public class EmbeddableDataBuilder extends GenericDataBuilder<EmbeddableDataBuilder, EmbeddableData> {

    public EmbeddableDataBuilder(
            @NotNull DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Sets the supplier that supplies the content fragment data.
     *
     * @param supplier The content fragment data value supplier.
     * @return A new {@link ContentFragmentDataBuilder}.
     */
    @NotNull
    public EmbeddableDataBuilder withEmbeddableDetails(@NotNull final Supplier<Map<String, Object>> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setEmbeddableDetails(supplier));
    }

    @Override
    @NotNull EmbeddableDataBuilder createInstance(@NotNull DataLayerSupplier supplier) {
        return new EmbeddableDataBuilder(supplier);
    }

    @Override
    public @NotNull EmbeddableData build() {
        return new EmbeddableDataImpl(this.getDataLayerSupplier());
    }
}
