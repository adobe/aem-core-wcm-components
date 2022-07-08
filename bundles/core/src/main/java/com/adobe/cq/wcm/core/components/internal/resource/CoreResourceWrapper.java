/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceWrapper;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ExporterConstants;

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CoreResourceWrapper extends ResourceWrapper {

    private ValueMap valueMap;
    private String overriddenResourceType;
    private Map<String, Resource> overriddenChildren;

    public CoreResourceWrapper(@NotNull Resource resource, @NotNull String overriddenResourceType) {
        this(resource, overriddenResourceType, null, null, null);
    }

    public CoreResourceWrapper(@NotNull Resource resource, @NotNull String overriddenResourceType,
                               @Nullable List<String> hiddenProperties, @Nullable Map<String, String> overriddenProperties) {
        this(resource, overriddenResourceType, hiddenProperties, overriddenProperties, null);
    }

    public CoreResourceWrapper(@NotNull Resource resource, @NotNull String overriddenResourceType,
                               @Nullable List<String> hiddenProperties, @Nullable Map<String, String> overriddenProperties,
                               @Nullable Map<String, Resource> overriddenChildren) {
        super(resource);
        if (StringUtils.isEmpty(overriddenResourceType)) {
            throw new IllegalArgumentException("The " + CoreResourceWrapper.class.getName() + " needs to override the resource type of " +
                    "the wrapped resource, but the resourceType argument was null or empty.");
        }
        this.overriddenResourceType = overriddenResourceType;
        this.overriddenChildren = overriddenChildren;
        valueMap = new ValueMapDecorator(new HashMap<>(resource.getValueMap()));
        valueMap.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, overriddenResourceType);
        if (overriddenProperties != null) {
            for (Map.Entry<String, String> entry : overriddenProperties.entrySet()) {
                valueMap.put(entry.getKey(), entry.getValue());
            }
        }
        if (hiddenProperties != null) {
            for (String property : hiddenProperties) {
                valueMap.remove(property);
            }
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
    @NotNull
    public ValueMap getValueMap() {
        return valueMap;
    }

    @Override
    @Nullable
    public Resource getChild(String relPath) {
        if (overriddenChildren != null) {
            if (overriddenChildren.containsKey(relPath)) {
                return overriddenChildren.get(relPath);
            }
        }
        return super.getChild(relPath);
    }

    @Override
    @NotNull
    public String getResourceType() {
        return overriddenResourceType;
    }

    @Override
    public boolean isResourceType(String resourceType) {
        return this.getResourceResolver().isResourceType(this, resourceType);
    }
}
