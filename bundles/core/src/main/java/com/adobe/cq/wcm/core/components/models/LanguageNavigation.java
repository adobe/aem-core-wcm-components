/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.List;

/**
 * Defines the {@code LanguageNavigation} Sling Model used for the {@code /apps/core/wcm/components/languagenavigation} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
public interface LanguageNavigation extends Component {

    /**
     * Name of the resource / configuration policy property that defines the navigation root from which to build the global
     * language structure navigation. The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_NAVIGATION_ROOT = "navigationRoot";

    /**
     * Name of the resource / configuration policy property that defines the depth of the global language structure in the content tree
     * relative to the site root. The property should provide a Long value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_STRUCTURE_DEPTH = "structureDepth";

    /**
     * Returns the list of language navigation items related to the current page.
     *
     * @return a list of language navigation items related to the current page
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default List<NavigationItem> getItems() {
        return null;
    }

    /**
     * Retrieves an accessibility label that describes the language navigation.
     *
     * @return an accessibility label for the language navigation
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    default String getAccessibilityLabel() {
        return null;
    }

}
