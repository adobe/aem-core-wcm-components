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

import java.util.Collections;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

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
     * Property name for storing link accessibility label.
     * @deprecated
     * This property was used with Title v2, but is not used with Title v3 nor with any other components.
     */
    @Deprecated
    String PN_LINK_ACCESSIBILITY_LABEL = "linkAccessibilityLabel";

    /**
     * Property name for storing link title attribute.
     * @deprecated
     * This property was used with Title v2, but is not used with Title v3 nor with any other components.
     */
    @Deprecated
    String PN_LINK_TITLE_ATTRIBUTE = "linkTitleAttribute";

    /**
     * Checks if the link defined for the component is valid.
     *
     * @return {@code true} if component has a valid link defined
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    default boolean isValid() {
        return false;
    }

    /**
     * The link URL, supports context path and escaping.
     *
     * @return Link URL or {@code null} if link is invalid
     */
    @Nullable
    default String getURL() {
        return null;
    }

    /**
     * The mapped URL, which supports mapping and vanity path.
     * This usually is resource resolver mapping.
     *
     * @return Mapped link URL or {@code null} if link is invalid or no processing can be done
     */
    @Nullable
    default String getMappedURL() {
        return null;
    }

    /**
     * The externalized URL which also contains the scheme and host information.
     * This is usually created with a {@link com.day.cq.commons.Externalizer} service
     * @return Full link URL or {@code null} if link is invalid or can't be externalized.
     */
    @Nullable
    default String getExternalizedURL() {
        return null;
    }

    /**
     * Returns a Map with attributes for HTML anchor tag for this link.
     * This usually also contains the Link URL as <code>href</code> attribute,
     * but may contain additional attributes like <code>target</code> and others.
     *
     * @return {@link Map} with HTML-specific anchor attributes, or an empty map if link is invalid
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    @NotNull
    default Map<String, String> getHtmlAttributes() {
        return Collections.emptyMap();
    }

    /**
     * Returns the referenced WCM/DAM object.
     *
     * @return Target page or {@code null}
     * @since com.adobe.cq.wcm.core.components.commons.link 1.0.0
     */
    @Nullable
    default T getReference() {
        return null;
    }
}
