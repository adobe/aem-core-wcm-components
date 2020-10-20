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
package com.adobe.cq.wcm.core.components.internal.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.reference.Reference;
import com.day.cq.wcm.api.reference.ReferenceProvider;

/**
 * Provides Context Aware Configuration references for a given resource.
 */
@Component(
        service = ReferenceProvider.class
)
public class CaConfigReferenceProvider implements ReferenceProvider {

    private static final String NN_SLING_CONFIGS = "sling:configs";
    private static final String CA_CONFIG_REFERENCE_TYPE = "caconfig";

    /**
     * The @{@link ConfigurationResourceResolver} service.
     */
    @org.osgi.service.component.annotations.Reference
    private ConfigurationResourceResolver configurationResourceResolver;

    @Override
    public List<Reference> findReferences(Resource resource) {
        List<Reference> references = new ArrayList<>();
        Resource configResource = configurationResourceResolver.getResource(resource, NN_SLING_CONFIGS, HtmlPageItemsConfig.class.getName());
        if (configResource != null) {
            ValueMap properties = configResource.getValueMap();
            Calendar lastModified = properties.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
            references.add(new Reference(CA_CONFIG_REFERENCE_TYPE, HtmlPageItemsConfig.class.getName(), configResource, (lastModified != null) ? lastModified.getTimeInMillis() : -1));
        }
        return references;
    }
}
