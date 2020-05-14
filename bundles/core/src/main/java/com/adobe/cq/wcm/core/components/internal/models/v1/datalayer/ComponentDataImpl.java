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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import java.util.Calendar;
import java.util.Date;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements the DataLayer functionality.
 *
 */
public class ComponentDataImpl implements ComponentData {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentDataImpl.class);

    protected final AbstractComponentImpl component;

    protected final Resource resource;

    public ComponentDataImpl(@NotNull AbstractComponentImpl component, @NotNull Resource resource) {
        this.component = component;
        this.resource = resource;
    }

    @Override
    public String getId() {
        return component.getId();
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public String getType() {
        return resource.getResourceType();
    }

    @Override
    public String getTitle() {
        return component.getDataLayerTitle();
    }

    @Override
    public String getDescription() {
        return component.getDataLayerDescription();
    }

    @Override
    public Date getLastModifiedDate() {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        Calendar lastModified = null;

        if (valueMap != null) {
            lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);

            if (lastModified == null) {
                lastModified = valueMap.get(JcrConstants.JCR_CREATED, Calendar.class);
            }
        }

        if (lastModified != null) {
            return lastModified.getTime();
        }

        return null;
    }

    @Override
    public String getText() {
        return component.getDataLayerText();
    }

    @Override
    public String getLinkUrl() {
        return component.getDataLayerLinkUrl();
    }

    @Override
    public String getJson() {
        try {
            return String.format("{\"%s\":%s}",
                    getId(),
                    new ObjectMapper().writeValueAsString(this));
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to generate dataLayer JSON string", e);
        }
        return null;
    }
}
