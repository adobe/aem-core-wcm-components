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

import com.adobe.cq.wcm.core.components.config.AttributeConfig;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemConfig;
import com.adobe.cq.wcm.core.components.models.HtmlPageItem;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

public class HtmlPageItemImpl implements HtmlPageItem {

    String prefixPath;
    HtmlPageItemConfig config;
    Element element;
    Location location;
    Map<String, String> attributes;

    public HtmlPageItemImpl(@NotNull String prefixPath, @NotNull HtmlPageItemConfig config) {
        this.prefixPath = prefixPath;
        this.config = config;
    }

    @Override
    public Element getElement() {
        if (element == null) {
            element = Element.fromString(config.element());
        }
        return element;
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            location = Location.fromString(config.location());
        }
        return location;
    }

    @Override
    public Map<String, String> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
            for (AttributeConfig attributeConfig : config.attributes()) {
                String attrName = attributeConfig.name();
                String attrValue = attributeConfig.value();
                if (isNotEmpty(attrName)) {
                    if ((getElement() == Element.LINK && HtmlPageItem.PN_HREF.equals(attrName)) ||
                        (getElement() == Element.SCRIPT && HtmlPageItem.PN_SRC.equals(attrName))) {
                        attrValue = prefixPath + attrValue;
                    }
                    attributes.put(attrName, attrValue);
                }
            }
        }
        return attributes;
    }
}
