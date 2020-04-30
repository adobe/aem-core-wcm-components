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

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.models.DataLayer;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implements the DataLayer functionality.
 *
 */
public class DataLayerImpl implements DataLayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataLayerImpl.class);

    private final AbstractComponentImpl component;

    private final Resource resource;

    private Boolean dataLayerEnabled;

    public DataLayerImpl(@NotNull AbstractComponentImpl component, @NotNull Resource resource) {
        this.component = component;
        this.resource = resource;
    }

    @Override
    public boolean isEnabled() {
        if (dataLayerEnabled == null) {
            dataLayerEnabled = false;
            if (resource != null) {
                ConfigurationBuilder builder = resource.adaptTo(ConfigurationBuilder.class);
                if (builder != null) {
                    DataLayerConfig dataLayerConfig = builder.as(DataLayerConfig.class);
                    dataLayerEnabled = dataLayerConfig.enabled();
                }
            }
        }
        return dataLayerEnabled;
    }

    @Override
    public Resource getAssetResource() {
        return component.getDataLayerAssetResource();
    }

    @Override
    public String getId() {
        return component.getId();
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
    public String getLastModifiedDate() {
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        Calendar lastModified = null;

        if (valueMap != null) {
            lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);

            if (lastModified == null) {
                lastModified = valueMap.get(JcrConstants.JCR_CREATED, Calendar.class);
            }
        }

        if (lastModified != null) {
            return lastModified.toInstant().toString();
        }

        return null;
    }

    @Override
    public String getText() {
        return component.getDataLayerText();
    }

    @Override
    public String[] getTags() {
        return component.getDataLayerTags();
    }

    @Override
    public String getUrl() {
        return component.getDataLayerUrl();
    }

    @Override
    public String getLinkUrl() {
        return component.getDataLayerLinkUrl();
    }

    @Override
    public String getTemplatePath() {
        return component.getDataLayerTemplatePath();
    }

    @Override
    public String getLanguage() {
        return component.getDataLayerLanguage();
    }

    @Override
    public String[] getShownItems() {
        return component.getDataLayerShownItems();
    }

    @Override
    public String getString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to generate dataLayer JSON string", e);
        }
        return null;
    }
}
