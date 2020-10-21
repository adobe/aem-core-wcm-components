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

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Temporary data builder that requires an ID supplier to be set.
 * This is typically used as the first level of a data builder to ensure that the caller specifies the ID field value
 * as required by the {@link com.adobe.cq.wcm.core.components.models.datalayer.ComponentData} interface.
 *
 * @param <T> The data builder type.
 * @param <K> The data type.
 */
public final class IdRequiredDataBuilder<T extends GenericDataBuilder<T, K>, K> {

    /**
     * The data builder.
     */
    @NotNull
    private final T builder;

    /**
     * Create an ID requiring data builder.
     *
     * @param builder The data builder.
     */
    IdRequiredDataBuilder(@NotNull final T builder) {
        this.builder = builder;
    }

    /**
     * Set the supplier that supplies the component's ID.
     *
     * @param supplier The ID value supplier.
     * @return A new data builder.
     * @see com.adobe.cq.wcm.core.components.models.datalayer.ComponentData#getId()
     * @see com.adobe.cq.wcm.core.components.models.datalayer.AssetData#getId()
     */
    @NotNull
    public T withId(@NotNull final Supplier<@NotNull String> supplier) {
        return this.builder.withId(supplier);
    }
}
