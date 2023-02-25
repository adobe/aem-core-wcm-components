/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v4;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.link.LinkImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractListItemImpl;
import com.adobe.cq.wcm.core.components.internal.resource.CoreResourceWrapper;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.adobe.cq.wcm.core.components.models.List.PN_TEASER_DELEGATE;

class ExternalLinkListItemImpl extends AbstractListItemImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalLinkListItemImpl.class);
    private final Link link;
    private String linkText;

    private Resource teaserResource;

    ExternalLinkListItemImpl(@NotNull Link link, Resource resource, String parentId, Component component) {
        super(parentId, resource, component);
        this.component = component;
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

    @Override
    @JsonIgnore
    public Resource getTeaserResource() {
        if (teaserResource == null && component != null) {
            String delegateResourceType = component.getProperties().get(PN_TEASER_DELEGATE, String.class);
            if (StringUtils.isEmpty(delegateResourceType)) {
                LOGGER.error("In order for list rendering delegation to work correctly you need to set up the teaserDelegate property on" +
                    " the {} component; its value has to point to the resource type of a teaser component.", component.getPath());
            } else {
                Map<String, Object> overriddenProperties = new HashMap<>();
                String target = (String) link.getHtmlAttributes().get(LinkImpl.ATTR_TARGET);
                if (StringUtils.isNotBlank(target)) {
                    overriddenProperties.put(Link.PN_LINK_TARGET, target);
                }
                overriddenProperties.put(JcrConstants.JCR_TITLE, linkText);
                overriddenProperties.put(Teaser.PN_TITLE_FROM_PAGE, false);
                teaserResource = new CoreResourceWrapper(resource, delegateResourceType, null, overriddenProperties);
            }
        }
        return teaserResource;
    }
}
