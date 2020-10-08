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

public interface PageItem {

    String PROP_ELEMENT = "element";
    String PROP_LOCATION = "location";
    String PROP_HREF = "href";
    String PROP_SRC = "src";

    default Element getElement() {
        return null;
    }

    default Location getLocation() {
        return null;
    }

    default Map<String, String> getAttributes() {
        return null;
    }

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
                    return new String[] {"crossorigin", PROP_HREF, "hreflang", "media", "referrerpolicy", "rel", "sizes", "title", "type"};
                case SCRIPT:
                    return new String[] {"async", "charset", "defer", PROP_SRC, "type"};
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
