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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.osgi.service.component.annotations.Component;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;

@Component(
        service = AdapterFactory.class,
        property = {
                AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
                AdapterFactory.ADAPTER_CLASSES + "=com.day.cq.wcm.api.policies.ContentPolicyMapping"
        }
)
public class MockAdapterFactory implements AdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType getAdapter(Object o, Class<AdapterType> aClass) {
        if (aClass == ContentPolicyMapping.class && o instanceof Resource) {
            Resource resource = (Resource) o;
            ValueMap valueMap = resource.getValueMap();
            String policyPath = valueMap.get("cq:policy", StringUtils.EMPTY);
            Resource policyMappingResource = null;
            if (StringUtils.isNotEmpty(policyPath)) {
                policyMappingResource = resource;
            } else {
                PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
                if (pageManager != null) {
                    Page page = pageManager.getContainingPage(resource);
                    if (page != null) {
                        Template template = page.getTemplate();
                        if (template != null && page.getPath().startsWith(template.getPath() + "/")) {
                            // in template; resolve relative to policies node
                            policyPath = template.getPath() + "/policies/" + resource.getPath().replace(template.getPath
                                    () + "/structure/", "");
                            policyMappingResource = resource.getResourceResolver().getResource(policyPath);
                        }
                    }
                }
            }
            if (policyMappingResource != null) {
                return (AdapterType) new MockContentPolicyMapping(policyMappingResource);
            }
        }
        return null;
    }
}
