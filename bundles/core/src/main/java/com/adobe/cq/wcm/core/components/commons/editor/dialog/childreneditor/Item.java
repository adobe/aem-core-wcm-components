/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.SlingHttpServletRequest;

import com.day.cq.i18n.I18n;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.day.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Defines an {@code Item} class, used by the children editor {@code Editor} Sling Model.
 *
 * @since com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor 1.0.0
 */
public class Item {
    public static final Logger LOG = LoggerFactory.getLogger(Item.class);

    protected String name;
    protected String value;
    protected String title;
    protected String iconName;
    protected String iconPath;
    protected String iconAbbreviation;
    protected boolean isLiveCopy;

    /**
     * Name of the resource property that defines a panel title
     */
    String PN_PANEL_TITLE = "cq:panelTitle";

    /**
     * Name of the resource property that defines a component icon
     */
    String PN_ICON = "cq:icon";

    /**
     * Name of the resource property that defines a two letter component abbreviation
     */
    String PN_ABBREVIATION = "abbreviation";

    /**
     * Name of the resource property that defines a translation context for the component abbreviation
     */
    String PN_ABBREVIATION_I18N = "abbreviation_commentI18n";

    /**
     * Name of a component child node that defines an icon in PNG format
     */
    String NN_ICON_PNG = "cq:icon.png";

    /**
     * Name of a component child node that defines an icon in SVG format
     */
    String NN_ICON_SVG = "cq:icon.svg";

    public Item(SlingHttpServletRequest request, Resource resource) {
        String translationContext = null;
        String titleI18n = null;
        I18n i18n = new I18n(request);
        if (resource != null) {
            name = resource.getName();
            ValueMap vm = resource.getValueMap();
            value = Optional.ofNullable(vm.get(PN_PANEL_TITLE, String.class))
                .orElseGet(() -> vm.get(JcrConstants.JCR_TITLE, String.class));
            LiveRelationshipManager mgr = request.getResourceResolver().adaptTo(LiveRelationshipManager.class);
            if (mgr != null) {
                try {
                    isLiveCopy = mgr.hasLiveRelationship(resource) && !mgr.getLiveRelationship(resource, true).getStatus().isCancelled();
                } catch (WCMException e) {
                    LOG.error("Something went wrong while checking live copy status for resource {}", resource.getPath(), e);
                }
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
                    iconName = valueMap.get(PN_ICON, String.class);
                    if (iconName != null) {
                        break;
                    }
                    iconAbbreviation = valueMap.get(PN_ABBREVIATION, String.class);
                    if (iconAbbreviation != null) {
                        translationContext = valueMap.get(PN_ABBREVIATION_I18N, String.class);
                        break;
                    }
                    Resource png = res.getChild(NN_ICON_PNG);
                    if (png != null) {
                        iconPath = png.getPath();
                        break;
                    } else {
                        Resource svg = res.getChild(NN_ICON_SVG);
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

    /**
     * Retrieves the node name of this children editor item.
     *
     * @return the {@code Item} name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the value ({@code jcr:title}) of this children editor item.
     *
     * @return the {@code Item} value
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the title (component name) of this children editor item.
     *
     * @return the {@code Item} title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Retrieves the icon name of this children editor item.
     *
     * @return the {@code Item} icon name
     */
    public String getIconName() {
        return iconName;
    }

    /**
     * Retrieves the icon path of this children editor item.
     *
     * @return the {@code Item} icon path
     */
    public String getIconPath() {
        return iconPath;
    }

    /**
     * Retrieves the icon abbreviation of this children editor item.
     *
     * @return the {@code Item} icon abbreviation
     */
    public String getIconAbbreviation() {
        return iconAbbreviation;
    }

    /**
     * Checks if the panel is the target of a livecopy.
     *
     * @return {@code true} if the panel is target of a livecopy
     */
    public boolean isLiveCopy() {
        return isLiveCopy;
    }
}
