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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {Breadcrumb.class, ComponentExporter.class},
    resourceType = {BreadcrumbImpl.RESOURCE_TYPE_V1, BreadcrumbImpl.RESOURCE_TYPE_V2})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class BreadcrumbImpl extends AbstractComponentImpl implements Breadcrumb {

    protected static final String RESOURCE_TYPE_V1 = "core/wcm/components/breadcrumb/v1/breadcrumb";
    protected static final String RESOURCE_TYPE_V2 = "core/wcm/components/breadcrumb/v2/breadcrumb";

    protected static final boolean PROP_SHOW_HIDDEN_DEFAULT = false;
    protected static final boolean PROP_HIDE_CURRENT_DEFAULT = false;
    protected static final int PROP_START_LEVEL_DEFAULT = 2;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @ScriptVariable
    private Page currentPage;

    @Self
    protected LinkManager linkManager;

    private boolean showHidden;
    private boolean hideCurrent;
    private int startLevel;
    private List<NavigationItem> items;

    @PostConstruct
    private void initModel() {
        startLevel = properties.get(PN_START_LEVEL, currentStyle.get(PN_START_LEVEL, PROP_START_LEVEL_DEFAULT));
        showHidden = properties.get(PN_SHOW_HIDDEN, currentStyle.get(PN_SHOW_HIDDEN, PROP_SHOW_HIDDEN_DEFAULT));
        hideCurrent = properties.get(PN_HIDE_CURRENT, currentStyle.get(PN_HIDE_CURRENT, PROP_HIDE_CURRENT_DEFAULT));
    }

    @Override
    public Collection<NavigationItem> getItems() {
        if (items == null) {
            items = createItems();
        }
        return Collections.unmodifiableList(items);
    }

    private List<NavigationItem> createItems() {
        List<NavigationItem> items = new ArrayList<>();
        int currentLevel = currentPage.getDepth();
        while (startLevel < currentLevel) {
            Page page = currentPage.getAbsoluteParent(startLevel);
            if (page != null && page.getContentResource() != null) {
                boolean isActivePage = page.equals(currentPage);
                if (isActivePage && hideCurrent) {
                    break;
                }
                if (checkIfNotHidden(page)) {
                    NavigationItem navigationItem = newBreadcrumbItem(page, isActivePage, linkManager, currentLevel, getId(), component);
                    items.add(navigationItem);
                }
            }
            startLevel++;
        }
        return items;
    }

    /**
     * Create a Breadcrumb Item.
     *
     * @param page        The page for which to create a breadcrumb item.
     * @param active      Flag indicating if the breadcrumb item is active.
     * @param linkManager Link manager service.
     * @param level       Depth level of the navigation item.
     * @param parentId    ID of the parent navigation component.
     * @param component   The parent navigation {@link Component}.
     */
    protected NavigationItem newBreadcrumbItem(@NotNull final Page page,
                                               final boolean active,
                                               @NotNull final LinkManager linkManager,
                                               final int level,
                                               final String parentId,
                                               final Component component) {
        return new BreadcrumbItemImpl(page, active, linkManager, level, parentId, component);
    }

    /**
     * Check if a page should be shown in the breadcrumb
     * A page should be shown if either
     * <ul>
     *     <li>`showHidden` is set to true for this component; or,</li>
     *     <li>The page is not configured to be hidden in navigation</li>
     * </ul>
     *
     * @param page The page to check.
     * @return True if the page should be shown in the breadcrumb, false if not.
     */
    private boolean checkIfNotHidden(@NotNull final Page page) {
        return this.showHidden || !page.isHideInNav();
    }
}
