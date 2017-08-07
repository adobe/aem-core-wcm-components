/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface for a single navigation item, used by the {@link Breadcrumb} and {@link Navigation} models.
 */
@ConsumerType
public interface NavigationItem extends com.adobe.cq.wcm.core.components.models.NavigationItem {

    /**
     * Returns the children of this {@code NavigationItem}, if any.
     *
     * @return the children of this {@code NavigationItem}; if this {@code NavigationItem} doesn't have any children, the returned
     * {@link List} will be empty
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default List<NavigationItem> getChildren() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the URL of this {@code NavigationItem}.
     *
     * @return the URL of this navigation item
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default String getURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the depth level of this {@code NavigationItem}.
     *
     * @return the depth level
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default int getLevel() {
        throw new UnsupportedOperationException();
    }

}
