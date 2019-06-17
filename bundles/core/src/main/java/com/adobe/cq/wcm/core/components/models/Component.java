/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * A base interface to be extended by components that need to provide access to common properties.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
public interface Component extends ComponentExporter {

    String PN_ID = "id";
    String PN_BACKGROUND_IMAGE_ENABLED = "backgroundImageEnabled";
    String PN_BACKGROUND_COLOR_ENABLED = "backgroundColorEnabled";
    String PN_BACKGROUND_COLOR_SWATCHES_ONLY = "backgroundColorSwatchesOnly";
    String PN_BACKGROUND_IMAGE_REFERENCE = "backgroundImageReference";
    String PN_BACKGROUND_COLOR = "backgroundColor";

    /**
     * Returns the HTML id of the the component's root element
     *
     * @return HTML id of the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default String getId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a list of classes to be applied to the component's root element
     *
     * @return List of classes for the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default String[] getClasses() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the CSS style to be applied to the component's root element
     *
     * @return CSS style string for the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default String getStyle() {
        throw new UnsupportedOperationException();
    }

    /**
     *  Returns a map of HTML attributes to be added to the component's root element
     *
     * @return {@link Map<String, String>} of HTML attributes for the component's root element
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nullable
    default Map<String, String> getAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
