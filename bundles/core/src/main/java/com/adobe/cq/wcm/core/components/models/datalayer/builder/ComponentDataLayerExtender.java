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
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.jetbrains.annotations.NotNull;

/**
 * Component data layer extender.
 *
 * Used to extend existing component data layer models.
 */
public final class ComponentDataLayerExtender {

    /**
     * The component data to be extended.
     */
    final ComponentData componentData;

    /**
     * Construct a component data layer extender.
     *
     * @param componentData The existing component data to be extended.
     */
    ComponentDataLayerExtender(@NotNull final ComponentData componentData) {
        this.componentData = componentData;
    }

    /**
     * Get a ComponentDataBuilder that extends existing component data.
     *
     * @return A new ComponentDataBuilder pre-initialized with the existing component data.
     */
    @NotNull
    public ComponentDataBuilder asComponent() {
        return new ComponentDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }

    /**
     * Get a ContainerDataBuilder that extends existing component data.
     *
     * @return A new ContainerDataBuilder pre-initialized with the existing container data.
     */
    @NotNull
    public ContainerDataBuilder asContainer() {
        return new ContainerDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }
    /**
     * Get a PageDataBuilder that extends existing component data.
     *
     * @return A new PageDataBuilder pre-initialized with the existing page data.
     */
    @NotNull
    public PageDataBuilder asPage() {
        return new PageDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }

    /**
     * Get a ImageComponentDataBuilder that extends existing component data.
     *
     * @return A new ImageComponentDataBuilder pre-initialized with the existing image component data.
     */
    @NotNull
    public ImageComponentDataBuilder asImageComponent() {
        return new ImageComponentDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }

    /**
     * Get a ContentFragmentDataBuilder that extends existing component data.
     *
     * @return A new ContentFragmentDataBuilder pre-initialized with the existing component data.
     */
    @NotNull
    public ContentFragmentDataBuilder asContentFragment() {
        return new ContentFragmentDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }

    /**
     * Gets a EmbeddableDataBuilder that extends existing component data.
     *
     * @return A new EmbeddableDataBuilder pre-initialized with the existing component data.
     */
    @NotNull
    public EmbeddableDataBuilder asEmbeddable() {
        return new EmbeddableDataBuilder(DataLayerSupplierImpl.extend(this.componentData));
    }

}
