/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.models;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;

/**
 * A base interface to be extended by containers such as the {@link Carousel} and {@link Tabs} models.
 *
 * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
 */
@ConsumerType
public interface Container extends ContainerExporter {

    /**
     * Returns a list of component items
     *
     * @return List of component items
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default List<ListItem> getItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItems()
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    @Nonnull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    @Nonnull
    @Override
    default String[] getExportedItemsOrder() {
        throw new UnsupportedOperationException();
    }
}
