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

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ComponentDataImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.jetbrains.annotations.NotNull;

/**
 * Data builder for components that are not a specific type of component.
 * I.e. for components that <b>are not</b> pages, containers, or image components.
 *
 * This builder will produce a valid {@link ComponentData} object.
 *
 * For more specific data builders please see:
 * <ul>
 *     <li>{@link ImageComponentDataBuilder}</li>
 *     <li>{@link ContainerDataBuilder}</li>
 *     <li>{@link PageDataBuilder}</li>
 * </ul>
 */
public final class ComponentDataBuilder extends GenericComponentDataBuilder<ComponentDataBuilder, ComponentData> {

    /**
     * Construct a component data builder.
     *
     * @param supplier The data layer supplier.
     */
    ComponentDataBuilder(@NotNull final DataLayerSupplier supplier) {
        super(supplier);
    }

    @Override
    @NotNull
    ComponentDataBuilder createInstance(@NotNull final DataLayerSupplier supplier) {
        return new ComponentDataBuilder(supplier);
    }

    @NotNull
    @Override
    public ComponentData build() {
        return new ComponentDataImpl(this.getDataLayerSupplier());
    }
}
