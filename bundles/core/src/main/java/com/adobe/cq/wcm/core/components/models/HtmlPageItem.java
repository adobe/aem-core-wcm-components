/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This interface defines items that can be included on a page.
 * @since com.adobe.cq.wcm.core.components.models 12.16.0
 */
public interface HtmlPageItem {

    /**
     * Property name that defines the type of the HTML element rendered by the page item
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     * @deprecated since 12.18.0
     */
    @Deprecated
    String PN_ELEMENT = "element";

    /**
     * Property that defines the location (header or footer) where the page item should be inserted
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     * @deprecated since 12.18.0
     */
    @Deprecated
    String PN_LOCATION = "location";

    /**
     * Sub-node that holds the page item's attributes
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     * @deprecated since 12.18.0
     */
    @Deprecated
    String NN_ATTRIBUTES = "attributes";

    /**
     * HREF attribute for {@link Element#LINK} page items
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    String PN_HREF = "href";

    /**
     * SRC attribute for {@link Element#SCRIPT} page items
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    String PN_SRC = "src";

    /**
     * Returns the {@link Element} type for the page item.
     *
     * @return {@link Element} type
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    default Element getElement() {
        return null;
    }

    /**
     * Returns the {@link Location} where the page item should be inserted.
     *
     * @return {@link Location} where item should be inserted
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    default Location getLocation() {
        return null;
    }

    /**
     * Returns the HTML attributes and values for the page item element.
     *
     * @return HTML attributes and values
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    default Map<String, Object> getAttributes() {
        return null;
    }

    /**
     * {@code enum} that defines possible insert positions for a page item.
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    enum Location {
        HEADER("header"),
        FOOTER("footer");

        private String name;

        Location(String name) {
            this.name = name;
        }

        @NotNull
        public static Location fromString(String name) {
            for (Location location : Location.values()) {
                if (StringUtils.equals(location.getName(), name)) {
                    return location;
                }
            }
            return HEADER;
        }

        public String getName() {
            return name;
        }

        @Override
        @JsonValue
        public String toString() {
            return name;
        }
    }

    /**
     * {@code enum} that defines the possible HTML elements for a page item
     */
    enum Element {
        SCRIPT("script"),
        LINK("link"),
        META("meta");

        private String name;

        Element(String name) {
            this.name = name;
        }

        @Nullable
        public static Element fromString(String name) {
            for (Element element : Element.values()) {
                if (StringUtils.equals(element.getName(), name)) {
                    return element;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public String[] getAttributeNames() {
            switch(this) {
                case LINK:
                    return new String[] {"as", "crossorigin", PN_HREF, "hreflang", "media", "referrerpolicy", "rel", "sizes", "title", "type"};
                case SCRIPT:
                    return new String[] {"async", "charset", "defer", PN_SRC, "type"};
                case META:
                    return new String[] {"charset", "content", "http-equiv", "name"};
            }
            return new String[] {};
        }

        @Override
        @JsonValue
        public String toString() {
            return name;
        }
    }
}
