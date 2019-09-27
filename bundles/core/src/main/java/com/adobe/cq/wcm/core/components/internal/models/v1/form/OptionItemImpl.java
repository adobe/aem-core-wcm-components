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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.wcm.core.components.models.form.OptionItem;
import com.day.cq.wcm.foundation.forms.FormsHelper;

public class OptionItemImpl implements OptionItem {

    private static final String PN_TEXT = "text";
    private static final String PN_SELECTED = "selected";
    private static final String PN_DISABLED = "disabled";
    private static final String PN_VALUE = "value";

    private SlingHttpServletRequest request;
    private Resource options;
    private ValueMap properties;

    public OptionItemImpl(SlingHttpServletRequest request, Resource options, Resource option) {
        this.request = request;
        this.options = options;
        this.properties = option.getValueMap();
    }

    @Override
    public String getText() {
        return properties.get(PN_TEXT, String.class);
    }

    @Override
    public boolean isSelected() {
        String[] prefillValues = FormsHelper.getValues(request, options);
        if (prefillValues != null) {
            for (String prefillValue : prefillValues) {
                if (prefillValue.equals(this.getValue())) {
                    return true;
                }
            }
            return false;
        }
        return properties.get(PN_SELECTED, false);
    }

    @Override
    public boolean isDisabled() {
        return properties.get(PN_DISABLED, false);
    }

    @Override
    public String getValue() {
        return properties.get(PN_VALUE, String.class);
    }
}
