/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.Container;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AbstractComponentImpl implements Component {

    @SlingObject
    protected Resource resource;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;

    private boolean backgroundColorEnabled;
    private boolean backgroundImageEnabled;
    private String id;
    private String backgroundImageReference;
    private String backgroundColor;
    private StringBuilder styleBuilder;


    private void populateStyleProperties() {
        backgroundColorEnabled = currentStyle.get(Component.PN_BACKGROUND_COLOR_ENABLED, false);
        backgroundImageEnabled = currentStyle.get(Component.PN_BACKGROUND_IMAGE_ENABLED, false);
        if (resource != null) {
            ValueMap properties = resource.getValueMap();
            backgroundColor = properties.get(Component.PN_BACKGROUND_COLOR, String.class);
            backgroundImageReference = properties.get(Container.PN_BACKGROUND_IMAGE_REFERENCE, String.class);
        }
    }

    private void setBackgroundStyleString()
    {
        styleBuilder = new StringBuilder();
        if (backgroundImageEnabled && !StringUtils.isEmpty(backgroundImageReference)) {
            styleBuilder.append("background-image:url(" + backgroundImageReference + ");background-size:cover;background-repeat:no-repeat;");
        }
        if (backgroundColorEnabled && !StringUtils.isEmpty(backgroundColor)) {
            styleBuilder.append("background-color:" + backgroundColor + ";");
        }
    }

    @Nullable
    @Override
    public String getId() {
        if (id == null) {
            if (resource != null) {
                ValueMap properties = resource.getValueMap();
                id = properties.get(Component.PN_ID, String.class);
            }
        }
        return id;
    }

    @Nullable
    @Override
    public String getStyle() {
        if (styleBuilder == null) {
            populateStyleProperties();
            setBackgroundStyleString();
        }
        String style = styleBuilder.toString();
        if (StringUtils.isEmpty(style)) {
            return null;
        }
        return style;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
