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

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.adapter.SlingAdaptable;
import org.apache.sling.api.resource.Resource;

import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;

public class MockContentPolicyMapping extends SlingAdaptable implements ContentPolicyMapping {

    Resource contentPolicyMappingResource;
    private ContentPolicy contentPolicy;
    private String contentPolicyPath;

    public MockContentPolicyMapping(Resource contentPolicyMappingResource) {
        this.contentPolicyMappingResource = contentPolicyMappingResource;
        contentPolicyPath = contentPolicyMappingResource.getValueMap().get("cq:policy", String.class);
        if (StringUtils.isEmpty(contentPolicyPath)) {
            throw new IllegalArgumentException("Resource " + contentPolicyMappingResource.getPath() + " does not contain a valid " +
                    "cq:policy property");
        }
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public Calendar getLastModified() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public ContentPolicy getPolicy() {
        if (contentPolicy == null) {
            contentPolicy = new MockContentPolicy(contentPolicyMappingResource.getResourceResolver().getResource
                    ("/conf/coretest/settings/wcm/policies/" + contentPolicyPath));
        }
        return contentPolicy;
    }

    @Override
    public Template getTemplate() {
        return null;
    }
}
