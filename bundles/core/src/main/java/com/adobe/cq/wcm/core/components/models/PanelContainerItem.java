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
package com.adobe.cq.wcm.core.components.models;

import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface for a container item that is a panel.
 * In addition to having a resource, a panel container item also includes a title, ID, and data layer object.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.27.0
 */
@ConsumerType
public interface PanelContainerItem extends ContainerItem {

    /**
     * Name of the property that contains the panel item's title.
     */
    String PN_PANEL_TITLE = "cq:panelTitle";

    /**
     * Gets the panel item's title.
     *
     * @return The title of the panel item.
     */
    @Nullable
    default String getTitle() {
        return null;
    }

    /**
     * Gets the ID for this panel.
     * Note: this is not the ID of the contained item - this is the ID of the panel its self.
     *
     * @return The ID for this panel.
     */
    @Nullable
    default String getId() {
        return null;
    }

    /**
     * Gets the data layer object for this panel.
     * Note: this is not the data layer object for the contained item - this is the data layer object for the panel its
     * self.
     *
     * @return The data layer object for this panel.
     */
    @Nullable
    default ComponentData getData() {
        return null;
    }
}
