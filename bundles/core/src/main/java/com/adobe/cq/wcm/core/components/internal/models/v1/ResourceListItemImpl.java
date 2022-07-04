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

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Resource-backed list item implementation.
 */
public class ResourceListItemImpl extends AbstractListItemImpl implements ListItem {

    protected Link link;
    /**
     * The title.
     */
    protected String title;

    /**
     * The description.
     */
    protected String description;

    /**
     * The last modified date.
     */
    protected Calendar lastModified;

    /**
     * The name.
     */
    protected String name;

    /**
     * Construct a resource-backed list item.
     *
     * @param resource The resource.
     * @param parentId The ID of the containing component.
     */
    public ResourceListItemImpl(@NotNull LinkManager linkManager, @NotNull Resource resource,
                                String parentId, Component component) {
        super(parentId, resource, component);
        ValueMap valueMap = resource.getValueMap();
        title = valueMap.get(JcrConstants.JCR_TITLE, String.class);
        description = valueMap.get(JcrConstants.JCR_DESCRIPTION, String.class);
        lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        path = resource.getPath();
        name = resource.getName();
        link = linkManager.get(resource).build();
    }


    @Override
    @NotNull
    @JsonIgnore
    public Link getLink() {
        return link;
    }

    @Override
    @JsonIgnore
    public String getURL() {
        return link.getURL();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Calendar getLastModified() {
        return lastModified;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getName() {
        return name;
    }
}
