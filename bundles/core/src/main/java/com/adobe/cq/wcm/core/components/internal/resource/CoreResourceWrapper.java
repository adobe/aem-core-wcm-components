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

import java.util.Collections;
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

/**
 * Resource wrapper allowing for the change of resource type, properties, and children.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CoreResourceWrapper extends ResourceWrapper {

    /**
     * The properties for this resource wrapper.
     */
    private final ValueMap valueMap;

    /**
     * The resource type for this resource.
     */
    private final String overriddenResourceType;

    /**
     * Additional child resources to add to this resource.
     */
    private final Map<String, Resource> overriddenChildren;

    /**
     * Construct a resource wrapper.
     *
     * @param resource The resource to be wrapped.
     * @param overriddenResourceType The new resource type.
     */
    public CoreResourceWrapper(@NotNull final Resource resource, @NotNull final String overriddenResourceType) {
        this(resource, overriddenResourceType, null, null, null);
    }

    /**
     * Construct a resource wrapper.
     *
     * @param resource The resource to be wrapped.
     * @param overriddenResourceType The new resource type.
     * @param hiddenProperties Properties to hide.
     * @param overriddenProperties Properties to add/override.
     */
    public CoreResourceWrapper(@NotNull final Resource resource,
                               @NotNull final String overriddenResourceType,
                               @Nullable final List<String> hiddenProperties,
                               @Nullable final Map<String, Object> overriddenProperties) {
        this(resource, overriddenResourceType, hiddenProperties, overriddenProperties, null);
    }

    /**
     * Construct a resource wrapper.
     *
     * @param resource The resource to be wrapped.
     * @param overriddenResourceType The new resource type.
     * @param hiddenProperties Properties to hide.
     * @param overriddenProperties Properties to add/override.
     * @param overriddenChildren Child resources to add/override.
     */
    public CoreResourceWrapper(@NotNull final Resource resource,
                               @NotNull final String overriddenResourceType,
                               @Nullable final List<String> hiddenProperties,
                               @Nullable final Map<String, Object> overriddenProperties,
                               @Nullable final Map<String, Resource> overriddenChildren) {
        super(resource);
        if (StringUtils.isEmpty(overriddenResourceType)) {
            throw new IllegalArgumentException("The " + CoreResourceWrapper.class.getName() + " needs to override the resource type of " +
                    "the wrapped resource, but the resourceType argument was null or empty.");
        }
        this.overriddenResourceType = overriddenResourceType;
        this.overriddenChildren = overriddenChildren;
        HashMap<String, Object> properties = new HashMap<>(resource.getValueMap());
        properties.put(ResourceResolver.PROPERTY_RESOURCE_TYPE, overriddenResourceType);
        if (overriddenProperties != null) {
            properties.putAll(overriddenProperties);
        }
        if (hiddenProperties != null) {
            for (String property : hiddenProperties) {
                properties.remove(property);
            }
        }
        // wrapped to prevent external modification of the underlying map
        this.valueMap = new ValueMapDecorator(Collections.unmodifiableMap(properties));
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
