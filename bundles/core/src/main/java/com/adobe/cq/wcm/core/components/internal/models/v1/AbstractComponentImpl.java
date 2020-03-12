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
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.components.ComponentContext;

import com.adobe.cq.wcm.core.components.models.Component;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
public abstract class AbstractComponentImpl implements Component {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractComponentImpl.class);

    @SlingObject
    protected Resource resource;

    @ScriptVariable
    protected ComponentContext componentContext;

    @ScriptVariable
    private Page currentPage;

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
                id = generateId();
            } else {
                id = StringUtils.replace(StringUtils.normalizeSpace(StringUtils.trim(id)), " ", "-");
            }
        }
        return id;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    /**
     * Returns an auto generated component ID.
     *
     * The ID is the first 10 characters of an SHA-1 hexadecimal hash of the component path,
     * prefixed with the component name. Example: title-810f3af321
     *
     * If the component is referenced, the path is taken to be a concatenation of the component path,
     * with the path of the first parent context resource that exists on the page or in the template.
     * This ensures the ID is unique if the same component is referenced multiple times on the same page or template.
     *
     * Collision
     * ---------
     * c = expected collisions
     * c ~= (i^2)/(2m) - where i is the number of items and m is the number of possibilities for each item.
     * m = 16^n - for a hexadecimal string, where n is the number of characters.
     *
     * For i = 1000 components with the same name, and n = 10 characters:
     *
     * c ~= (1000^2)/(2*(16^10))
     * c ~= 0.00000045
     *
     * @return the auto generated component ID
     */
    private String generateId() {
        String resourceType = resource.getResourceType();
        String prefix = StringUtils.substringAfterLast(resourceType, "/");
        String path = resource.getPath();
        PageManager pageManager = currentPage.getPageManager();
        Page containingPage = pageManager.getContainingPage(resource);
        Template template = currentPage.getTemplate();
        Boolean inCurrentPage = (containingPage != null && StringUtils.equals(containingPage.getPath(), currentPage.getPath()));
        Boolean inTemplate = (template != null && path.startsWith(template.getPath()));
        if (!inCurrentPage && !inTemplate) {
            ComponentContext parentContext = componentContext.getParent();
            while (parentContext != null) {
                Resource parentContextResource = parentContext.getResource();
                if (parentContextResource != null) {
                    Page parentContextPage = pageManager.getContainingPage(parentContextResource);
                    inCurrentPage = (parentContextPage != null && StringUtils.equals(parentContextPage.getPath(), currentPage.getPath()));
                    inTemplate = (template != null && parentContextResource.getPath().startsWith(template.getPath()));
                    if (inCurrentPage || inTemplate) {
                        path = parentContextResource.getPath().concat(resource.getPath());
                        break;
                    }
                }
                parentContext = parentContext.getParent();
            }
        }
        return prefix + "-" + StringUtils.substring(DigestUtils.sha256Hex(path), 0, 10);
    }
}
