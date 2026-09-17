/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v2.NavigationItemImpl;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static org.apache.sling.api.SlingConstants.PROPERTY_PATH;

/**
 * V3 Breadcrumb Item Implementation.
 */
@JsonIgnoreProperties(value = {"page", "children", "level", "description", "lastModified", PROPERTY_PATH})
public class BreadcrumbItemImpl extends NavigationItemImpl implements NavigationItem {

    /**
     * Construct a Breadcrumb Item.
     *
     * @param page        The page for which to create a breadcrumb item.
     * @param active      Flag indicating if the breadcrumb item is active.
     * @param linkManager Link manager service.
     * @param level       Depth level of the navigation item.
     * @param parentId    ID of the parent navigation component.
     * @param component   The parent navigation {@link Component}.
     */
    public BreadcrumbItemImpl(@NotNull final Page page,
                              final boolean active,
                              @NotNull final LinkManager linkManager,
                              final int level,
                              final String parentId,
                              final Component component) {
        super(page, active, active, linkManager, level, Collections::emptyList, parentId, component);
    }

    @Override
    @JsonIgnore(false)
    @Nullable
    public Link getLink() {
        return super.getLink();
    }
}
