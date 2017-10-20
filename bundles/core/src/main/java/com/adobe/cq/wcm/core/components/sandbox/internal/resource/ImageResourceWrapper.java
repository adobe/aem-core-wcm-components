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
package com.adobe.cq.wcm.core.components.sandbox.internal.resource;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Exporter(name = Constants.EXPORTER_NAME, extensions = Constants.EXPORTER_EXTENSION)
public class ImageResourceWrapper extends ResourceWrapper {

    private ValueMap valueMap;
    private String resourceType;

    public ImageResourceWrapper(@Nonnull Resource resource, @Nonnull String resourceType) {
        super(resource);
        if (StringUtils.isEmpty(resourceType)) {
            throw new IllegalArgumentException("The " + ImageResourceWrapper.class.getName() + " needs to override the resource type of " +
                    "the wrapped resource, but the resourceType argument was null or empty.");
        }
        this.resourceType = resourceType;
        valueMap = new ValueMapDecorator(new HashMap<>(resource.getValueMap()));
        valueMap.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, resourceType);
    }

    public ImageResourceWrapper(@Nonnull Resource resource, @Nonnull String resourceType, List<String> hiddenProperties) {
        this(resource, resourceType);
        for (String property : hiddenProperties) {
            valueMap.remove(property);
        }
    }

    @Override
    public <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
        if (type == ValueMap.class) {
            return (AdapterType) valueMap;
        }
        return super.adaptTo(type);
    }

    @Override
    @Nonnull
    public ValueMap getValueMap() {
        return valueMap;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public boolean isResourceType(String resourceType) {
        return this.getResourceResolver().isResourceType(this, resourceType);
    }
}
