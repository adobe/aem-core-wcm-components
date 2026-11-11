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

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * V1 Navigation Item Implementation.
 */
public class NavigationItemImpl extends PageListItemImpl implements NavigationItem {

    /**
     * List of children.
     * Note - this will be null until populated from {@link #childrenSupplier} and so should remain private.
     */
    private List<NavigationItem> children;

    /**
     * Navigation level.
     */
    private final int level;

    /**
     * Flag indicating if this navigation item is active.
     */
    private final boolean active;

    /**
     * Flag indicating if this navigation item is for the current page.
     */
    private final boolean current;

    /**
     * Supplier for child navigation items.
     */
    @NotNull
    private final Supplier<@NotNull List<NavigationItem>> childrenSupplier;

    /**
     * Construct a Navigation Item.
     *
     * @param page             The page for which to create a navigation item.
     * @param active           Flag indicating if the navigation item is active.
     * @param current          Flag indicating if the navigation item is current page.
     * @param linkManager      Link manager service.
     * @param level            Depth level of the navigation item.
     * @param childrenSupplier The child navigation items supplier.
     * @param parentId         ID of the parent navigation component.
     * @param component        The parent navigation {@link Component}.
     */
    public NavigationItemImpl(@NotNull final Page page,
                              final boolean active,
                              final boolean current,
                              @NotNull final LinkManager linkManager,
                              final int level,
                              @NotNull final Supplier<List<NavigationItem>> childrenSupplier,
                              final String parentId,
                              final Component component) {
        super(linkManager, page, parentId, component);
        this.active = active;
        this.current = current;
        this.level = level;
        this.childrenSupplier = childrenSupplier;
    }

    @Override
    @JsonIgnore
    @Deprecated
    public Page getPage() {
        return page;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public boolean isCurrent() {
        return current;
    }

    @Override
    public List<NavigationItem> getChildren() {
        if (this.children == null) {
            this.children = Collections.unmodifiableList(this.childrenSupplier.get());
        }
        return children;
    }

    @Override
    public int getLevel() {
        return level;
    }
}
