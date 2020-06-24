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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;

public class ResourceListItemImpl extends AbstractListItemImpl implements ListItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceListItemImpl.class);

    protected String url;
    protected String title;
    protected String description;
    protected Calendar lastModified;
    protected String name;

    public ResourceListItemImpl(@NotNull SlingHttpServletRequest request, @NotNull Resource resource, String parentId) {
        super(parentId, resource);
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            title = valueMap.get(JcrConstants.JCR_TITLE, String.class);
            description = valueMap.get(JcrConstants.JCR_DESCRIPTION, String.class);
            lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        }
        path = resource.getPath();
        name = resource.getName();
        url = null;
    }

    @Override
    public String getURL() {
        return url;
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

    /*
     * DataLayerProvider implementation of field getters
     */

    @Override
    public String getDataLayerTitle() {
        return getTitle();
    }

    @Override
    public String getDataLayerLinkUrl() {
        return getURL();
    }
}
