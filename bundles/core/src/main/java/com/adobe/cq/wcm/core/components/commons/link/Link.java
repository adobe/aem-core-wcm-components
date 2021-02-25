/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.commons.link;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.day.cq.wcm.api.Page;

/**
 * Describes a link target.
 */
@ConsumerType
public interface Link {

    /**
     * Check if the link defined for the component is valid.
     * @return true if component has a valid link defined
     */
    boolean isValid();

    /**
     * Externalized link URL.
     * @return Link URL or null if link is invalid.
     */
    @Nullable
    String getURL();

    /**
     * Map with Attributes for HTML Anchor tag for this links.
     * This usually also contains the Link URL as <code>href</code> attribute,
     * but may contain additional attributes like <code>target</code> and others.
     * @return Map with HTML-specific anchor attributes, or an empty map if link is invalid
     */
    @NotNull
    Map<String, String> getHtmlAttributes();

    /**
     * Returns the references target page if the links points to an internal page.
     * @return Target page or null
     */
    @Nullable
    Page getTargetPage();

}
