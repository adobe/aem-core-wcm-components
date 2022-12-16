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
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.wcm.api.components.Component;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ExternalLinkListItemImpl extends AbstractListItemImpl {
    private final Link link;
    private String linkText;

    ExternalLinkListItemImpl(@NotNull Link link, Resource resource, String parentId, Component component) {
        super(parentId, resource, component);
        ValueMap properties = resource.getValueMap();
        this.linkText = properties.get(List.PN_LINK_TEXT, String.class);
        this.link = link;
        if (StringUtils.isBlank(this.linkText)) {
            this.linkText = this.link.getURL();
        }
    }

    @Override
    public @Nullable Link getLink() {
        return link;
    }

    @Override
    public @Nullable String getURL() {
        return link.getURL();
    }

    @Override
    public @Nullable String getTitle() {
        return linkText;
    }
}
