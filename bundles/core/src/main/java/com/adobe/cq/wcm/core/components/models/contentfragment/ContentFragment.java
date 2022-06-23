/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.models.contentfragment;

import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.models.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Defines the Sling model for the {@code /apps/core/wcm/components/contentfragment} component. The model
 * provides information about the referenced content fragment and access to representations of its elements.
 *
 * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
 */
public interface ContentFragment extends DAMContentFragment, ContainerExporter, Component {

    /**
     * Name of the mandatory resource property that stores the path to a content fragment.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_PATH = "fragmentPath";

    /**
     * Name of the optional resource property that stores the names of the elements to be used.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_ELEMENT_NAMES = "elementNames";

    /**
     * Name of the optional resource property that stores the name of the variation to be used.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_VARIATION_NAME = "variationName";

    /**
     * Name of the required property that stores whether a single text element (<code>singleText</code>) or multiple
     * elements (<code>multi</code>) are displayed.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_DISPLAY_MODE = "displayMode";

    /**
     * Returns resource type that is used for the internal responsive grid.
     *
     * @return resource type
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @JsonIgnore
    default String getGridResourceType() {
        return "";
    }

    /**
     * Returns the map of all exported child items (resource names from Sling Model classes).
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        return Collections.emptyMap();
    }

    /**
     * Returns the order of items in the map.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        return new String[]{};
    }

    /**
     * Returns the type of the resource for which the export is performed.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }

    /**
     * Returns the paragraphs of a multiline text element.
     *
     * @return an array containing HTML paragraphs or {@code null} for non-multiline-text elements
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @Nullable
    default String[] getParagraphs() {
        return null;
    }
}
