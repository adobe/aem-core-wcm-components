/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models;

import java.util.Calendar;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;

public class ResourceListItemImpl implements ListItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceListItemImpl.class);

    protected String url;
    protected String title;
    protected String description;
    protected Calendar lastModified;
    protected String path;

    public ResourceListItemImpl(@Nonnull SlingHttpServletRequest request, @Nonnull Resource resource) {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            title = valueMap.get(JcrConstants.JCR_TITLE, String.class);
            description = valueMap.get(JcrConstants.JCR_DESCRIPTION, String.class);
            lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
        }
        path = resource.getPath();
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

}
