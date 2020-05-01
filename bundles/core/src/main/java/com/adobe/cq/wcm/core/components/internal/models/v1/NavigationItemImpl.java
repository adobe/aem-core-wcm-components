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

import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class NavigationItemImpl extends PageListItemImpl implements NavigationItem {

    protected List<NavigationItem> children = Collections.emptyList();
    protected int level;
    protected boolean active;

    public NavigationItemImpl(Page page, boolean active, SlingHttpServletRequest request, int level, List<NavigationItem> children, String parentId) {
        this(page, active, request, level, children, parentId, PROP_DISABLE_SHADOWING_DEFAULT);
    }

    public NavigationItemImpl(Page page, boolean active, SlingHttpServletRequest request, int level, List<NavigationItem> children, String parentId, boolean isShadowingDisabled) {
        super(request, page, parentId, isShadowingDisabled);
        this.active = active;
        this.level = level;
        this.children = children;
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
    public List<NavigationItem> getChildren() {
        return children;
    }

    @Override
    public int getLevel() {
        return level;
    }
}
