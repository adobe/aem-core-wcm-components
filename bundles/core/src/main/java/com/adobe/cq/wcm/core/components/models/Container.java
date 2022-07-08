/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.models;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;

/**
 * A base interface to be extended by containers such as the {@link Carousel}, {@link Tabs} and {@link Accordion} models.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.5.0
 */
@ConsumerType
public interface Container extends Component, ContainerExporter {

    /**
     * Name of the configuration policy property that indicates if background images are enabled
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_IMAGE_ENABLED = "backgroundImageEnabled";

    /**
     * Name of the configuration policy property that indicates if background colors are enabled
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_COLOR_ENABLED = "backgroundColorEnabled";

    /**
     * Name of the configuration policy property that indicates if background colors are to be restricted to predefined values
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_COLOR_SWATCHES_ONLY = "backgroundColorSwatchesOnly";

    /**
     * Name of the resource property that indicates that path to the background image
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_IMAGE_REFERENCE = "backgroundImageReference";

    /**
     * Name of the resource property that indicates the background color
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_BACKGROUND_COLOR = "backgroundColor";

    /**
     * Returns a list of container items
     *
     * @return List of container items
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    default List<ListItem> getItems() {
        return Collections.emptyList();
    }

    /**
     * Returns the background CSS style to be applied to the component's root element
     *
     * @return CSS style string for the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default String getBackgroundStyle() {
        return null;
    }

    /**
     * @see ContainerExporter#getExportedItems()
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        return Collections.emptyMap();
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        return new String[]{};
    }
}
