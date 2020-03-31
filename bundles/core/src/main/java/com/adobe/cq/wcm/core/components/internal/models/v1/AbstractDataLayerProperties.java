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

import com.adobe.cq.wcm.core.components.internal.DataLayerPropertiesFactory;
import com.adobe.cq.wcm.core.components.models.DataLayerProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Defines a minimal set of methods to be used by models enabling the DataLayer.
 *
 */
public abstract class AbstractDataLayerProperties implements DataLayerProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDataLayerProperties.class);

    @Override
    public boolean isDataLayerEnabled() {
        return true;
    }

    /**
     * Returns a Map with all dataLayer properties.
     *
     * @return a Map with dataLayer properties
     */
    @Override
    public Map<String, Map<String, Object>> getDataLayerJson() {
        if (isDataLayerEnabled()) {
            return DataLayerPropertiesFactory.build(this);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Returns a JSON string with all dataLayer properties to be used in HTL
     *
     * @return a JSON string with dataLayer properties
     */
    @Override
    @JsonIgnore
    public String getDataLayerString() {
        String json = null;

        try {
            json = new ObjectMapper().writeValueAsString(getDataLayerJson());
        } catch (JsonProcessingException e) {
            LOGGER.error("Unable to generate dataLayer JSON string", e);
        }

        return json;
    }
}
