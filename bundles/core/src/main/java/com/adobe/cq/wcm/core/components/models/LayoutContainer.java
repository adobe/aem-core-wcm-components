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

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code LayoutContainer} Sling Model used for the {@code /apps/core/wcm/components/container} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
@ConsumerType
public interface LayoutContainer extends Container {

    /**
     * Name of the resource property that indicates the layout that should be used by the container component.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_LAYOUT = "layout";

    /**
     * Enumeration of supported layout types for the container component
     */
    enum LayoutType {
        SIMPLE("simple"),
        RESPONSIVE_GRID("responsiveGrid")
        ;

        private String layout;

        LayoutType(String layout) {
            this.layout = layout;
        }

        public String getLayout() {
            return layout;
        }

        public static LayoutType getLayoutType(String layout) {
            for (LayoutType layoutType : values()) {
                if (layoutType.layout.equals(layout)) {
                    return layoutType;
                }
            }
            return null;
        }
    }

    /**
     * Returns the {@link LayoutType} to be used by the container component
     *
     * @return {@link LayoutType} for the container component
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @NotNull
    default LayoutType getLayout() {
        return LayoutType.SIMPLE;
    }

    /**
     * Returns an accessibility label that describes the container.
     *
     * @return an accessibility label for the container
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    default String getAccessibilityLabel() {
        return null;
    }

    /**
     * Returns a role attribute to be used on the container.
     *
     * @return a role attribute for the container
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    default String getRoleAttribute() {
        return null;
    }
}
