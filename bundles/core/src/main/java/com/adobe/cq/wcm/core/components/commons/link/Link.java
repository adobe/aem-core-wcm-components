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
 *
 * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
 */
@ConsumerType
public interface Link<T> {

    /**
     * Default property name for storing link URL.
     * All new model implementation should use this name, some of the existing models use other names to store the link URL.
     */
    String PN_LINK_URL = "linkURL";

    /**
     * Property name for storing link target.
     */
    String PN_LINK_TARGET = "linkTarget";

    /**
     * Check if the link defined for the component is valid.
     *
     * @return {@code true} if component has a valid link defined
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    boolean isValid();

    /**
     * The link URL, supports context path and vanity paths.
     *
     * @return Link URL or {@code null} if link is invalid
     */
    @Nullable
    String getURL();

    /**
     * Map with Attributes for HTML Anchor tag for this link.
     * This usually also contains the Link URL as <code>href</code> attribute,
     * but may contain additional attributes like <code>target</code> and others.
     *
     * @return {@link Map} with HTML-specific anchor attributes, or an empty map if link is invalid
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    @NotNull
    Map<String, String> getHtmlAttributes();

    /**
     * Returns the referenced WCM/DAM object.
     *
     * @return Target page or {@code null}
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    @Nullable
    T getReference();

}
