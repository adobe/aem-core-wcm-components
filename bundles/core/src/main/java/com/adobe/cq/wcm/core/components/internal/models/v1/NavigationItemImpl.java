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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.List;

import com.day.cq.wcm.api.designer.Style;
import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class NavigationItemImpl extends PageListItemImpl implements NavigationItem {
    
    
    protected final List<NavigationItem> children;
    protected final int level;
    protected final boolean active;
    protected final Style style;
    
    public NavigationItemImpl(Page page, boolean active, SlingHttpServletRequest request, int level, List<NavigationItem> children, Style style) {
        super(request, page);
        this.active = active;
        this.level = level;
        this.children = children;
        this.style = style;
    }

    @Override
    @JsonIgnore
    public Page getPage() {
        return page;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public List<NavigationItem> getChildren() {
        return children;
    }

    @Override
    public int getLevel() {
        return level;
    }
    
    @Override
    public String getGroupTemplatePath() {
        return style.get(PN_CUSTOM_GROUP_TEMPLATE_PATH, DEFAULT_GROUP_TEMPLATE_PATH);
    }
    
    @Override
    public String getItemTemplatePath() {
        return style.get(PN_CUSTOM_ITEM_TEMPLATE_PATH, DEFAULT_ITEM_TEMPLATE_PATH);
    }
    
    @Override
    public String getItemContentTemplatePath() {
        return style.get(PN_CUSTOM_ITEM_CONTENT_TEMPLATE_PATH, DEFAULT_ITEM_CONTENT_TEMPLATE_PATH);
    }
}
