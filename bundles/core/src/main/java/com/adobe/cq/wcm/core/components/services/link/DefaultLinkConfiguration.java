/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.services.link;

import java.util.Map;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.wcm.core.components.models.Page;

public class DefaultLinkConfiguration implements LinkConfiguration {

    Resource resource;
    String linkURLPropertyName;
    Page page;
    String URL;
    Map<String, String> attributes;

    public Resource getResource() {
        return this.resource;
    }

    public LinkConfiguration setResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public String getResourcePropertyName() {
        return this.linkURLPropertyName;
    }

    public LinkConfiguration setResourcePropertyName(String linkURLPropertyName) {
        this.linkURLPropertyName = linkURLPropertyName;
        return this;
    }

    public Page getTarget() {
        return this.page;
    }

    public LinkConfiguration setTarget(Page page) {
        this.page = page;
        return this;
    }

    public String getURL() {
        return this.URL;
    }

    public LinkConfiguration setURL(String URL){
        this.URL = URL;
        return this;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public LinkConfiguration setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
        return this;
    }

}
