/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.components.Component;

import java.util.Optional;

/**
 * Abstract helper class for ListItem implementations.
 * Generates an ID for the item, using the ID of its parent as a prefix
 */
public abstract class AbstractListItemImpl extends AbstractComponentImpl implements ListItem {

    /**
     * Prefix prepended to the item ID.
     */
    private static final String ITEM_ID_PREFIX = "item";

    /**
     * The ID of the component that contains this list item.
     */
    protected String parentId;

    /**
     * The path of this list item.
     */
    protected String path;

    /**
     * Data layer type.
     */
    protected String dataLayerType;

    /**
     * Construct a list item.
     *
     * @param parentId The ID of the containing component.
     * @param resource The resource of the list item.
     * @param component The component that contains this list item.
     */
    protected AbstractListItemImpl(String parentId, Resource resource, Component component) {
        this.parentId = parentId;
        if (resource != null) {
            this.path = resource.getPath();
        }
        if (component != null) {
            this.dataLayerType = component.getResourceType() + "/" + ITEM_ID_PREFIX;
        }
        this.resource = resource;
    }

    @NotNull
    @Override
    public String getId() {
        return ComponentUtils.generateId(StringUtils.join(parentId, ComponentUtils.ID_SEPARATOR, ITEM_ID_PREFIX), path);
    }

    @NotNull
    @Override
    protected ComponentData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData())
            .asComponent()
            .withType(() -> Optional.ofNullable(this.dataLayerType).orElseGet(() -> super.getComponentData().getType()))
            .withTitle(this::getTitle)
            .withLinkUrl(() -> Optional.ofNullable(this.getLink()).map(Link::getMappedURL).orElse(null))
            .build();
    }

}
