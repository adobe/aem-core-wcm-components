/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import javax.annotation.Nonnull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.sandbox.models.Container;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;


/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl implements Container {

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @Inject
    private ModelFactory modelFactory;

    @Inject
    private SlingModelFilter slingModelFilter;

    private List<ListItem> items;
    private Map<String, ? extends ComponentExporter> childrenModels;
    private String[] exportedItemsOrder;

    private List<ListItem> readItems() {
        List<ListItem> items = new ArrayList<>();
        if (resource != null) {
            ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);
            if (componentManager != null) {
                for (Resource res : resource.getChildren()) {
                    Component component = componentManager.getComponentOfResource(res);
                    if (component != null) {
                        items.add(new ResourceListItemImpl(request, res));
                    }
                }
            }
        }
        return items;
    }

    @Override
    public List<ListItem> getItems() {
        if (items == null) {
            items = readItems();
        }
        return items;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (childrenModels == null) {
            childrenModels = Utils.getChildModels(slingModelFilter, modelFactory, request, ComponentExporter.class);
        }
        return childrenModels;
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        if (exportedItemsOrder == null) {
            Map<String, ? extends ComponentExporter> models = getExportedItems();
            if (!models.isEmpty()) {
                exportedItemsOrder = models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
            } else {
                exportedItemsOrder = ArrayUtils.EMPTY_STRING_ARRAY;
            }
        }
        return Arrays.copyOf(exportedItemsOrder,exportedItemsOrder.length);
    }
}
