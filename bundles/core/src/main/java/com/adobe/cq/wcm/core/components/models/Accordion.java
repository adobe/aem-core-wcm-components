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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Accordion} Sling Model used for the {@code /apps/core/wcm/components/accordion} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
@ConsumerType
public interface Accordion extends Container {

    /**
     * Name of the configuration policy property that stores the default value for the accordion heading's HTML element.
     *
     * @see #getHeadingElement()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_DESIGN_HEADING_ELEMENT = "headingElement";

    /**
     * Indicates whether the accordion forces a single item to be expanded at a time or not.
     *
     * @return {@code true} if the accordion forces a single item to be expanded at a time; {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean isSingleExpansion() {
        return false;
    }

    /**
     * Returns the items that are expanded by default.
     *
     * @return the expanded items
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String[] getExpandedItems() {
        return null;
    }

    /**
     * Returns the HTML element to use for accordion headers.
     *
     * @return the HTML element to use for accordion headers
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getHeadingElement() {
        return null;
    }
}
