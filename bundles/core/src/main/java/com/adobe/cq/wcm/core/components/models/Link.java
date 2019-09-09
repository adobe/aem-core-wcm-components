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

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Describes a link target.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface Link {

    /**
     * Check if the link defined for the component is valid.
     * @return true if component has a valid link defined
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    @JsonIgnore
    default boolean isValid() {
        throw new UnsupportedOperationException();
    }

    /**
     * Externalized link URL.
     * @return Link URL or null if link is invalid.
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    @Nullable
    default String getURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Map with Attributes for HTML Anchor tag for this links.
     * This usually also contains the Link URL as <code>href</code> attribute,
     * but may contain additional attributes like <code>target</code> and others.
     * @return Map with HTML-specific anchor attributes, or null if link is invalid
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    @JsonIgnore
    @Nullable
    default Map<String, String> getHtmlAttributes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the references target page if the links points to an internal page.
     * @return Target page or null
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    @JsonIgnore
    @Nullable
    default com.day.cq.wcm.api.Page getTargetPage() {
        throw new UnsupportedOperationException();
    }

}
