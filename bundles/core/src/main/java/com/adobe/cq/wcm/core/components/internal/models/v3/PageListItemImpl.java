/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

class PageListItemImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.PageListItemImpl {
    private String linkText;

    PageListItemImpl(@NotNull Link link, Page page, Resource itemResource, String parentId, Component component, boolean showDescription, boolean linkItems, Resource resource) {
        super(link, page, parentId, component, showDescription, linkItems, resource);
        ValueMap properties = itemResource.getValueMap();
        this.linkText = properties.get(List.PN_LINK_TEXT, String.class);
    }

    public PageListItemImpl(Link build, Page page, String parentId, Component component, boolean showDescription, boolean showDescription1, Resource resource) {
        super(build, page, parentId, component, showDescription, showDescription1, resource);
    }

    @Override
    public String getTitle() {
        return StringUtils.isNotBlank(linkText) ? linkText : super.getTitle();
    }
}
