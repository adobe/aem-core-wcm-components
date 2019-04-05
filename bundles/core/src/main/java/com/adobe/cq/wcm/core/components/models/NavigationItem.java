/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

package com.adobe.cq.wcm.core.components.models;

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

import com.day.cq.wcm.api.Page;

/**
 * Interface for a single navigation item, used by the {@link Breadcrumb} and {@link Navigation} models.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface NavigationItem extends ListItem {
    
    String PN_CUSTOM_GROUP_TEMPLATE_PATH = "navigationCustomGroupTemplatePath";
    
    String PN_CUSTOM_ITEM_TEMPLATE_PATH = "navigationCustomItemTemplatePath";
    
    String PN_CUSTOM_ITEM_CONTENT_TEMPLATE_PATH = "navigationCustomItemContentTemplatePath";
    
    
    /**
     * Default template path that will render the item content of navigation items.
     */
    String DEFAULT_ITEM_CONTENT_TEMPLATE_PATH = "itemContent.html";
    
    /**
     * Default template path that will render the item's of navigation items.
     */
    String DEFAULT_ITEM_TEMPLATE_PATH = "item.html";
    
    /**
     * Default template path that will render the group's of navigation items.
     */
    String DEFAULT_GROUP_TEMPLATE_PATH = "group.html";
    
    /**
     * Default template path that will render the group's of navigation items.
     * This is applicable for the secondary / alternative rendition of the navigation, useful for flyouts / complex menu structures.
     */
    String PN_CUSTOM_SECONDARY_GROUP_TEMPLATE_PATH = "secondary/group.html";
    
    /**
     * Default template path that will render the item's of navigation items.
     * This is applicable for the secondary / alternative rendition of the navigation, useful for flyouts / complex menu structures.
     */
    String PN_CUSTOM_SECONDARY_ITEM_TEMPLATE_PATH = "secondary/item.html";
    
    /**
     * Default template path that will render the item content of navigation items.
     * This is applicable for the secondary / alternative rendition of the navigation, useful for flyouts / complex menu structures.
     */
    String  PN_CUSTOM_SECONDARY_ITEM_CONTENT_TEMPLATE_PATH = "secondary/itemContent.html";
    
    /**
     * Default template path that will render the item content of navigation items.
     */
    String DEFAULT_SECONDARY_ITEM_CONTENT_TEMPLATE_PATH = "secondary/itemContent.html";
    
    /**
     * Default template path that will render the item's of navigation items.
     */
    String DEFAULT_SECONDARY_ITEM_TEMPLATE_PATH = "secondary/item.html";
    
    /**
     * Default template path that will render the group's of navigation items.
     */
    String DEFAULT_SECONDARY_GROUP_TEMPLATE_PATH = "secondary/group.html";
    
    
    /**
     * Returns the {@link Page} contained by this navigation item.
     *
     * @return The {@link Page} contained in this navigation item.
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     * @deprecated since 12.1.0 as {@link NavigationItem} relies on {@link ListItem}
     */
    @Deprecated
    default Page getPage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if the page contained by this navigation item is active.
     *
     * @return {@code true} if it is the current page, otherwise {@code false}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean isActive() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the children of this {@code NavigationItem}, if any.
     *
     * @return the children of this {@code NavigationItem}; if this {@code NavigationItem} doesn't have any children, the returned
     * {@link java.util.List} will be empty
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default List<NavigationItem> getChildren() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the depth level of this {@code NavigationItem}.
     *
     * @return the depth level
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getLevel() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns whether or not a custom template to render this navigation items' group (children) should be used
     * @return
     */
    default boolean isCustomGroupTemplateActive() { return false; }
    
    /**
     * Returns whether or not a custom template to render this navigation item should be used
     * @return
     */
    default boolean isCustomItemTemplateActive() { return false; }
    
    /**
     * Returns whether or not a custom template to render this navigation item content should be used
     * @return
     */
    default boolean isCustomItemContentTemplateActive() { return false; }
    
    /**
     * Returns whether or not a custom template to render this navigation items' group (children) should be used
     * @return
     */
    default String getGroupTemplatePath() { return DEFAULT_GROUP_TEMPLATE_PATH; }
    
    /**
     * Returns whether or not a custom template to render this navigation item should be used
     * @return
     */
    default String getItemTemplatePath() { return DEFAULT_ITEM_TEMPLATE_PATH; }
    
    /**
     * Returns whether or not a custom template to render this navigation item content should be used
     * @return
     */
    default String getItemContentTemplatePath() { return DEFAULT_ITEM_CONTENT_TEMPLATE_PATH; }
    
    /**
     * Returns whether or not a custom template to render this navigation items' group (children) should be used
     * @return
     */
    default String getSecondaryGroupTemplatePath() { return DEFAULT_SECONDARY_GROUP_TEMPLATE_PATH; }
    
    /**
     * Returns whether or not a custom template to render this navigation item should be used
     * @return
     */
    default String getSecondaryItemTemplatePath() { return DEFAULT_SECONDARY_ITEM_TEMPLATE_PATH; }
    
    /**
     * Returns whether or not a custom template to render this navigation item content should be used
     * @return
     */
    default String getSecondaryItemContentTemplatePath() { return DEFAULT_SECONDARY_ITEM_CONTENT_TEMPLATE_PATH; }
}
