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

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static org.apache.sling.api.SlingConstants.PROPERTY_PATH;

@JsonIgnoreProperties(value = {"page", "children", "level", "description", "lastModified",PROPERTY_PATH})
public class BreadcrumbItemImpl extends NavigationItemImpl implements NavigationItem {

    public BreadcrumbItemImpl(Page page, boolean active, @NotNull LinkManager linkManager, int level,
                              List<NavigationItem> children, String parentId, Component component) {
        super(page, active, active, linkManager, level, children, parentId, component);
    }

}
