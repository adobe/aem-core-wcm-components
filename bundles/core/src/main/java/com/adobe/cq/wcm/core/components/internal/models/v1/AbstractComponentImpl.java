/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.Component;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
public abstract class AbstractComponentImpl implements Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentImpl.class);

    @SlingObject
    protected Resource resource;

    private String id;

    @Nullable
    @Override
    public String getId() {
        if (id == null) {
            if (resource != null) {
                ValueMap properties = resource.getValueMap();
                id = properties.get(Component.PN_ID, String.class);
            }
            if (StringUtils.isEmpty(id)) {
                id = getGeneratedId();
            }
        }
        return id;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    private String getGeneratedId() {
        String resourceType = resource.getResourceType();
        String prefix = StringUtils.substringAfterLast(resourceType, "/");
        return prefix + "-" + StringUtils.substring(DigestUtils.shaHex(resource.getPath()), 0, 10);
    }
}
