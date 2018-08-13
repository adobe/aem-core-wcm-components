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
package com.adobe.cq.wcm.core.components.sandbox.ui;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.text.Text;

public class ChildrenEditorItem {

    protected String name;
    protected String value;
    protected String title;
    protected String iconName;
    protected String iconPath;
    protected String iconAbbreviation;

    public ChildrenEditorItem(SlingHttpServletRequest request, Resource resource) {
        String translationContext = null;
        String titleI18n = null;
        I18n i18n = new I18n(request);
        if (resource != null) {
            name = resource.getName();
            ValueMap vm = resource.adaptTo(ValueMap.class);
            if (vm != null) {
                value = vm.get("jcr:title", String.class);
            }
        }
        ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);
        if (componentManager != null) {
            Component component = componentManager.getComponentOfResource(resource);
            if (component != null) {
                title = component.getTitle();
                titleI18n = i18n.getVar(title);
                if (title == null) {
                    title = Text.getName(component.getPath());
                }
            }
            while (component != null) {
                Resource res = component.adaptTo(Resource.class);
                if (res != null) {
                    ValueMap valueMap = res.getValueMap();
                    iconName = valueMap.get("cq:icon", String.class);
                    if (iconName != null) {
                        break;
                    }
                    iconAbbreviation = valueMap.get("abbreviation", String.class);
                    if (iconAbbreviation != null) {
                        translationContext = valueMap.get("abbreviation_commentI18n", String.class);
                        break;
                    }
                    Resource png = res.getChild("cq:icon.png");
                    if (png != null) {
                        iconPath = png.getPath();
                        break;
                    } else {
                        Resource svg = res.getChild("cq:icon.svg");
                        if (svg != null) {
                            iconPath = svg.getPath();
                            break;
                        }
                    }
                }
                component = component.getSuperComponent();
            }
        }

        if (iconAbbreviation != null && !"".equals(iconAbbreviation) && translationContext != null) {
            iconAbbreviation = i18n.getVar(iconAbbreviation, translationContext);
        } else if ((iconName == null && iconAbbreviation == null && iconPath == null) || "".equals(iconAbbreviation)) {
            // build internationalized abbreviation from title: Image >> Im
            iconAbbreviation = titleI18n == null ? title : titleI18n;

            if (iconAbbreviation.length() >= 2) {
                iconAbbreviation = iconAbbreviation.substring(0, 2);
            } else if (iconAbbreviation.length() == 1) {
                iconAbbreviation = String.valueOf(iconAbbreviation.charAt(0));
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public String getIconName() {
        return iconName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public String getIconAbbreviation() {
        return iconAbbreviation;
    }

}
