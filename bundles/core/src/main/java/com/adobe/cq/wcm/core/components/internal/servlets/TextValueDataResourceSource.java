/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.HashMap;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

public abstract class TextValueDataResourceSource extends SyntheticResource {

    public static final String PN_VALUE = "value";
    public static final String PN_TEXT = "text";
    protected static final String PN_SELECTED = "selected";

    private ValueMap valueMap;

    public TextValueDataResourceSource(ResourceResolver resourceResolver, String path, String resourceType) {
        super(resourceResolver, path, resourceType);

    }

    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type == ValueMap.class) {
            if (valueMap == null) {
                initValueMap();
            }
            return (AdapterType) valueMap;
        } else {
            return super.adaptTo(type);
        }
    }

    private void initValueMap() {
        valueMap = new ValueMapDecorator(new HashMap<String, Object>());
        valueMap.put(PN_VALUE, getValue());
        valueMap.put(PN_TEXT, getText());
        valueMap.put(PN_SELECTED, getSelected());
    }

    public abstract String getText();

    public abstract String getValue();

    protected boolean getSelected() {
        return false;
    }
}
