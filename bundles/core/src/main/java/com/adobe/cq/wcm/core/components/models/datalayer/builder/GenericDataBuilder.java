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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Generic data builder that specifies fields available on all data builders.
 * Every data builder should implement this interface.
 *
 * @param <T> The data builder type.
 * @param <K> The data type.
 */
public abstract class GenericDataBuilder<T extends GenericDataBuilder<T, K>, K> {

    /**
     * The current data layer supplier.
     */
    private final DataLayerSupplier dataLayerSupplier;

    /**
     * Construct an Abstract Data Builder.
     *
     * @param supplier The data layer supplier.
     */
    GenericDataBuilder(@NotNull final DataLayerSupplier supplier) {
        this.dataLayerSupplier = supplier;
    }

    /**
     * Get the current {@link DataLayerSupplier}.
     *
     * @return The current data layer supplier.
     */
    @NotNull
    final DataLayerSupplier getDataLayerSupplier() {
        return this.dataLayerSupplier;
    }

    /**
     * Set the supplier that supplies the component's ID.
     *
     * @param supplier The ID value supplier.
     * @return A new builder.
     */
    @NotNull
    public final T withId(@NotNull final Supplier<String> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setId(supplier));
    }

    /**
     * Create a new instance of the the current wrapper using the specified supplier.
     *
     * @param supplier The data layer supplier to wrap.
     * @return The wrapped data layer supplier.
     */
    @NotNull
    abstract T createInstance(@NotNull final DataLayerSupplier supplier);

    /**
     * Build the data.
     *
     * @return The data object.
     */
    @NotNull
    public abstract K build();
}
