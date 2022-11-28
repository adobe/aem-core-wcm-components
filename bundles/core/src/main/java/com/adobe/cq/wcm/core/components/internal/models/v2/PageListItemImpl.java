/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.adobe.cq.wcm.core.components.commons.link.LinkBuilder;
import com.adobe.cq.wcm.core.components.internal.link.LinkImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.resource.CoreResourceWrapper;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.datalayer.PageData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.adobe.cq.wcm.core.components.models.List.PN_TEASER_DELEGATE;

public class PageListItemImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl {

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PageListItemImpl.class);

    private final Component component;
    private Resource listResource;
    private Resource teaserResource;
    private boolean showDescription;
    private boolean linkItems;

    /**
     * List of properties that should be inherited when delegating to the featured image of the page.
     */
    private final Map<String, Object> overriddenProperties = new HashMap<>();
    private final List<String> hiddenProperties = new ArrayList<>();

    public PageListItemImpl(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component) {
        super(linkManager, page, parentId, component);
        this.component = component;
    }

    public PageListItemImpl(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component,
                            boolean showDescription, boolean linkItems, Resource resource) {
        super(linkManager, page, parentId, component);
        this.component = component;
        this.showDescription = showDescription;
        this.linkItems = linkItems;
        this.listResource = resource;
    }

    protected PageListItemImpl(@NotNull LinkBuilder linkBuilder, @NotNull Page page, String parentId, Component component,
                            boolean showDescription, boolean linkItems, Resource resource) {
        super(linkBuilder, page, parentId, component);
        this.component = component;
        this.showDescription = showDescription;
        this.linkItems = linkItems;
        this.listResource = resource;
    }

    @Override
    @JsonIgnore(false)
    @Nullable
    public Link<Page> getLink() {
        return super.getLink();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getURL() {
        return super.getURL();
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
                Resource resourceToBeWrapped = ComponentUtils.getFeaturedImage(page);
                if (resourceToBeWrapped != null) {
                    ValueMap valueMap = resourceToBeWrapped.getValueMap();
                    String inheritedFileReference = valueMap.get(DownloadResource.PN_REFERENCE, String.class);
                    overriddenProperties.put(DownloadResource.PN_REFERENCE, inheritedFileReference);
                    overriddenProperties.put(Image.PN_EXTERNAL_IMAGE_RESOURCE_PATH, resourceToBeWrapped.getPath());
                    // use the page featured image and inherit properties from the page item
                    overriddenProperties.put(JcrConstants.JCR_TITLE, this.getTitle());
                    if (showDescription) {
                        overriddenProperties.put(JcrConstants.JCR_DESCRIPTION, this.getDescription());
                    }
                    if (linkItems) {
                        overriddenProperties.put(ImageResource.PN_LINK_URL, this.getPath());
                        Link<Page> itemLink = this.getLink();
                        if (itemLink != null) {
                            String target = itemLink.getHtmlAttributes().get(LinkImpl.ATTR_TARGET);
                            if (StringUtils.isNotBlank(target)) {
                                overriddenProperties.put(Link.PN_LINK_TARGET, target);
                            }
                        }
                    }
                } else {
                    // use the page content node and inherit properties from the page item
                    resourceToBeWrapped = page.getContentResource();
                    if (resourceToBeWrapped == null) {
                        return null;
                    }
                    if (!showDescription) {
                        hiddenProperties.add(JcrConstants.JCR_DESCRIPTION);
                    }
                    if (linkItems) {
                        overriddenProperties.put(ImageResource.PN_LINK_URL, this.getPath());
                        Link<Page> itemLink = this.getLink();
                        if (itemLink != null) {
                            String target = itemLink.getHtmlAttributes().get(LinkImpl.ATTR_TARGET);
                            if (StringUtils.isNotBlank(target)) {
                                overriddenProperties.put(Link.PN_LINK_TARGET, target);
                            }
                        }
                    }
                }
                teaserResource = new CoreResourceWrapper(listResource, delegateResourceType, hiddenProperties, overriddenProperties);
            }
        }
        return teaserResource;
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
