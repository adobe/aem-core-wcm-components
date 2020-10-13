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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.HtmlPageItem;

public class HtmlPageItemImpl implements HtmlPageItem {

    String prefixPath;
    Resource resource;
    ValueMap properties;
    Element element;
    Location location;
    Map<String, String> attributes;

    public HtmlPageItemImpl(@NotNull String prefixPath, @NotNull Resource resource) {
        this.prefixPath = prefixPath;
        this.resource = resource;
        this.properties = resource.getValueMap();
    }

    @Override
    public Element getElement() {
        if (element == null) {
            element = Element.fromString(properties.get(HtmlPageItem.PN_ELEMENT, String.class));
        }
        return element;
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            location = Location.fromString(properties.get(HtmlPageItem.PN_LOCATION, String.class));
        }
        return location;
    }

    @Override
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
            Resource attributesNode = resource.getChild(HtmlPageItem.NN_ATTRIBUTES);
            if (attributesNode != null) {
                ValueMap attributesProperties = attributesNode.getValueMap();
                for (String attrName : getElement().getAttributeNames()) {
                    String attrValue = attributesProperties.get(attrName, String.class);
                    if (attrValue != null) {
                        if ((getElement() == Element.LINK && HtmlPageItem.PN_HREF.equals(attrName)) ||
                                (getElement() == Element.SCRIPT && HtmlPageItem.PN_SRC.equals(attrName))) {
                            attrValue = prefixPath + attrValue;
                        }
                        attributes.put(attrName, attrValue);
                    }
                }
            }
        }
        return attributes;
    }
}
