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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.config.AttributeConfig;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemConfig;
import com.adobe.cq.wcm.core.components.models.HtmlPageItem;

public class HtmlPageItemImpl implements HtmlPageItem {

    String prefixPath;
    Resource resource;
    ValueMap properties;
    HtmlPageItemConfig config;
    Element element;
    Location location;
    Map<String, Object> attributes;

    // Support the former node structure: see com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
    public HtmlPageItemImpl(@NotNull String prefixPath, @NotNull Resource resource) {
        this.prefixPath = prefixPath;
        this.resource = resource;
        this.properties = resource.getValueMap();
    }

    public HtmlPageItemImpl(@NotNull String prefixPath, @NotNull HtmlPageItemConfig config) {
        this.prefixPath = prefixPath;
        this.config = config;
    }

    @Override
    public Element getElement() {
        if (element == null) {
            if (config != null) {
                element = Element.fromString(config.element());
            } else {
                // Support the former node structure: see com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
                element = Element.fromString(properties.get(HtmlPageItem.PN_ELEMENT, String.class));
            }
        }
        return element;
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            if (config != null) {
                location = Location.fromString(config.location());
            } else {
                // Support the former node structure: see com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
                location = Location.fromString(properties.get(HtmlPageItem.PN_LOCATION, String.class));
            }
        }
        return location;
    }

    @Override
    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
            if (config != null) {
                for (AttributeConfig attributeConfig : config.attributes()) {
                    String attrName = attributeConfig.name();
                    String attrValue = attributeConfig.value();
                    if (StringUtils.isNotEmpty(attrName)) {
                        addAttributes(attrName, attrValue);
                    }
                }
            } else {
                // Support the former node structure: see com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
                Resource attributesNode = resource.getChild(HtmlPageItem.NN_ATTRIBUTES);
                if (attributesNode != null) {
                    ValueMap attributesProperties = attributesNode.getValueMap();
                    for (String attrName : getElement().getAttributeNames()) {
                        String attrValue = attributesProperties.get(attrName, String.class);
                        if (attrValue != null) {
                            addAttributes(attrName, attrValue);
                        }
                    }
                }
            }
        }
        return attributes;
    }

    private void addAttributes(String name, String value) {
        if ((getElement() == Element.LINK && HtmlPageItem.PN_HREF.equals(name)) ||
                (getElement() == Element.SCRIPT && HtmlPageItem.PN_SRC.equals(name))) {
            value = prefixPath + value;
        }
        if (StringUtils.equals(value, "true")) {
            attributes.put(name, true);
        } else if (StringUtils.equals(value, "false")) {
            attributes.put(name, false);
        } else {
            attributes.put(name, value);
        }
    }
}
