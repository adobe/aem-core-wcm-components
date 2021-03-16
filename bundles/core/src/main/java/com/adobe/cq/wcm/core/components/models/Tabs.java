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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Tabs} Sling Model used for the {@code /apps/core/wcm/components/tabs} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.5.0
 */
@ConsumerType
public interface Tabs extends Container {

    /**
     * Returns the default active item
     *
     * @return The default active item
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    default String getActiveItem() {
        return null;
    }

    /**
     * Returns an accessibility label that describes the tabs.
     *
     * @return an accessibility label for tabs
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    default String getAccessibilityLabel() {
        return null;
    }
}
