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

import com.adobe.cq.wcm.core.components.commons.link.LinkBuilder;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;

class MixedPageListItemImpl extends PageListItemImpl {
    private final String linkText;

    MixedPageListItemImpl(@NotNull LinkManager linkManager, @NotNull Page page, Resource itemResource, String parentId, Component component, boolean showDescription, boolean linkItems, Resource resource) {
        super(prepareLinkBuilder(linkManager, page, itemResource), page, parentId, component, showDescription, linkItems, resource);
        ValueMap properties = itemResource.getValueMap();
        this.linkText = properties.get(List.PN_LINK_TEXT, String.class);
    }

    @Override
    public String getTitle() {
        return StringUtils.isNotBlank(linkText) ? linkText : super.getTitle();
    }

    @NotNull
    private static LinkBuilder prepareLinkBuilder(@NotNull LinkManager linkManager, @NotNull Page page, Resource resource) {
        ValueMap properties = resource.getValueMap();
        String linkTarget = properties.get(PN_LINK_TARGET, String.class);
        LinkBuilder linkBuilder = linkManager.get(page);
        if (StringUtils.isNotBlank(linkTarget)) {
            linkBuilder.withLinkTarget(linkTarget);
        }
        return linkBuilder;
    }
}
