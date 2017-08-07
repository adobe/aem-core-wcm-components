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

package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

import com.day.cq.wcm.api.Page;

/**
 * Interface for a single item of the {@link Breadcrumb} model
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface NavigationItem {

    /**
     * Returns the {@link Page} contained by this navigation item.
     *
     * @return The {@link Page} contained in this navigation item.
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default Page getPage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if the page contained by this navigation item is active.
     *
     * @return true if it is the current page, otherwise false
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean isActive() {
        throw new UnsupportedOperationException();
    }
}
