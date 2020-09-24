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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.wcm.api.components.Component;

import static com.adobe.cq.wcm.core.components.internal.Utils.ID_SEPARATOR;

/**
 * Abstract helper class for ListItem implementations.
 * Generates an ID for the item, using the ID of its parent as a prefix
 *
 */
public abstract class AbstractListItemImpl extends AbstractComponentImpl {

    protected String parentId;
    protected String path;
    protected String dataLayerType;

    private static final String ITEM_ID_PREFIX = "item";

    protected AbstractListItemImpl(String parentId, Resource resource, Component component) {
        this.parentId = parentId;
        if (resource != null) {
            this.path = resource.getPath();
        }
        if (component != null) {
            this.dataLayerType = component.getResourceType() + "/" + ITEM_ID_PREFIX;
        }
        this.resource = resource;
    }

    @Nullable
    @Override
    public String getId() {
        String prefix = StringUtils.join(parentId, ID_SEPARATOR, ITEM_ID_PREFIX);
        return Utils.generateId(prefix, path);
    }

    @Override
    public String getDataLayerType() {
        return StringUtils.defaultString(dataLayerType, super.getDataLayerType());
    }

}
