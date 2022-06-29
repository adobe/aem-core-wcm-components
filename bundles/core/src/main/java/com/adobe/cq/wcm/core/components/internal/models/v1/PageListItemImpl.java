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

import java.util.Calendar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * List item implementation for a page-backed list item.
 */
public class PageListItemImpl extends AbstractListItemImpl implements ListItem {

    /**
     * The page for this list item.
     */
    protected Page page;

    /**
     * The link for this list item.
     */
    protected Link link;

    /**
     * Construct a list item for a given page.
     *
     * @param linkManager The link manager.
     * @param page The current page.
     * @param parentId The ID of the list containing this item.
     * @param component The component containing this list item.
     */
    public PageListItemImpl(@NotNull final LinkManager linkManager,
                            @NotNull final Page page,
                            final String parentId,
                            final Component component) {
        super(parentId, page.getContentResource(), component);
        this.parentId = parentId;
        this.link = linkManager.get(page).build();
        if (this.link.isValid()) {
            this.page = (Page) link.getReference();
        } else {
            this.page = page;
        }
    }

    @Override
    @JsonIgnore
    @Nullable
    public Link<Page> getLink() {
        return link;
    }

    @Override
    public String getURL() {
        return link.getURL();
    }

    @Override
    public String getTitle() {
        return PageListItemImpl.getTitle(this.page);
    }

    /**
     * Gets the title of a page list item from a given page.
     * The list item title is derived from the page by selecting the first non-null value from the
     * following:
     * <ul>
     *     <li>{@link Page#getNavigationTitle()}</li>
     *     <li>{@link Page#getPageTitle()}</li>
     *     <li>{@link Page#getTitle()}</li>
     *     <li>{@link Page#getName()}</li>
     * </ul>
     *
     * @param page The page for which to get the title.
     * @return The list item title.
     */
    public static String getTitle(@NotNull final Page page) {
        String title = page.getNavigationTitle();
        if (title == null) {
            title = page.getPageTitle();
        }
        if (title == null) {
            title = page.getTitle();
        }
        if (title == null) {
            title = page.getName();
        }
        return title;
    }

    @Override
    public String getDescription() {
        return page.getDescription();
    }

    @Override
    public Calendar getLastModified() {
        return page.getLastModified();
    }

    @Override
    public String getPath() {
        return page.getPath();
    }

    @Override
    @JsonIgnore
    public String getName() {
        return page.getName();
    }

    @Override
    @NotNull
    protected PageData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asPage()
            .withTitle(this::getTitle)
            .withLinkUrl(() -> link.getMappedURL())
            .build();
    }
}
