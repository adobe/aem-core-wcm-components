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

import java.util.Collections;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.internal.DataLayerPropertiesFactory;
import com.adobe.cq.wcm.core.components.models.DataLayerProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Defines a minimal set of methods to be used by models enabling the DataLayer.
 *
 */
public abstract class AbstractDataLayerProperties implements DataLayerProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataLayerProperties.class);

    @SlingObject
    protected Resource resource;

    @Override
    public boolean isDataLayerEnabled() {
        if (resource != null) {
            ConfigurationBuilder builder = resource.adaptTo(ConfigurationBuilder.class);
            if (builder != null) {
                DataLayerConfig dataLayerConfig = builder.as(DataLayerConfig.class);
                return dataLayerConfig.enabled();
            }
        }
        return false;
    }

    @Override
    public Map<String, Map<String, Object>> getDataLayerJson() {
        if (isDataLayerEnabled()) {
            return DataLayerPropertiesFactory.build(this);
        }
        return Collections.emptyMap();
    }

    @Override
    @JsonIgnore
    public String getDataLayerString() {
        Map<String, Map<String, Object>> json = getDataLayerJson();
        try {
            if (!json.isEmpty()) {
                return new ObjectMapper().writeValueAsString(json);
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to generate dataLayer JSON string", e);
        }
        return null;
    }
}
