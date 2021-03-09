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

import java.util.Collection;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Breadcrumb} Sling Model used for the {@code /apps/core/wcm/components/breadcrumb} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface Breadcrumb extends Component {

    /**
     * Name of the resource property that will indicate if pages that are hidden for navigation will still be displayed.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_SHOW_HIDDEN = "showHidden";

    /**
     * Name of the resource property that will indicate if the current page should not be present in the collection returned by
     * {@link #getItems()}.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_HIDE_CURRENT = "hideCurrent";

    /**
     * Name of the resource property that will indicate from which level starting from the current page the items from the collection
     * returned by {@link #getItems()} will be accumulated.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_START_LEVEL = "startLevel";

    /**
     * Creates collection of pages(from site hierarchy of current page) for the breadcrumb component
     *
     * @return {@link Collection} of breadcrumb items
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default Collection<NavigationItem> getItems() {
        return null;
    }

}
