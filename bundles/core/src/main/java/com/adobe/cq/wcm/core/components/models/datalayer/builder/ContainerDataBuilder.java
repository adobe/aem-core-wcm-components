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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ContainerDataImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.builder.DataLayerSupplierImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Data builder for container components.
 * This builder will produce a valid {@link ContainerData} object.
 */
public final class ContainerDataBuilder extends GenericComponentDataBuilder<ContainerDataBuilder, ContainerData> {

    /**
     * Construct a data builder for a container component.
     *
     * @param supplier The data layer supplier.
     */
    ContainerDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    /**
     * Set the supplier that supplies the array of shown items.
     *
     * @param supplier The shown items value supplier.
     * @return A new {@link ContainerDataBuilder}.
     * @see ContainerData#getShownItems()
     */
    @NotNull
    public ContainerDataBuilder withShownItems(@NotNull final Supplier<String[]> supplier) {
        return this.createInstance(new DataLayerSupplierImpl(this.getDataLayerSupplier()).setShownItems(supplier));
    }

    @Override
    @NotNull
    ContainerDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new ContainerDataBuilder(supplier);
    }

    @NotNull
    @Override
    public ContainerData build() {
        return new ContainerDataImpl(this.getDataLayerSupplier());
    }
}
