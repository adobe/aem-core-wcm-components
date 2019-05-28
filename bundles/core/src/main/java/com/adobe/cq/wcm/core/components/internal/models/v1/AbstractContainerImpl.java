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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl extends AbstractComponentImpl implements Container {

    @Self
    protected SlingHttpServletRequest request;

    @OSGiService
    protected SlingModelFilter slingModelFilter;

    @OSGiService
    protected ModelFactory modelFactory;

    protected List<ListItem> items;
    protected List<Resource> childComponents;
    protected List<Resource> filteredChildComponents;

    protected Map<String, ? extends ComponentExporter> itemModels;
    private String[] exportedItemsOrder;

    /**
     * Read the list of children resources that are components
     *
     * @return
     */
    @NotNull
    private List<Resource> readChildren() {
        List<Resource> children = new LinkedList<>();
        if (resource != null) {
            ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);
            if (componentManager != null) {
                resource.getChildren().forEach(res -> {
                    Component component = componentManager.getComponentOfResource(res);
                    if (component != null) {
                        children.add(res);
                    }
                });
            }
        }
        return children;
    }

    /**
     * Return (and cache) the list of children resources that are components
     *
     * @return
     */
    @NotNull
    protected List<Resource> getChildren() {
        if (childComponents == null) {
            childComponents = readChildren();
        }
        return childComponents;
    }

    /**
     * Return (and cache) the list of children resources that are components, filtered by the Sling Model Filter. This
     * should only be used for JSON export, for other usages refer to {@link AbstractContainerImpl#getChildren}.
     *
     * @return
     */
    @NotNull
    protected List<Resource> getFilteredChildren() {
        if (filteredChildComponents == null) {
            filteredChildComponents = new LinkedList<>();
            slingModelFilter.filterChildResources(getChildren())
                .forEach(filteredChildComponents::add);
        }
        return filteredChildComponents;
    }

    /**
     * Read the list of items in the container
     *
     * @return
     */
    @NotNull
    protected List<ListItem> readItems() {
        List<ListItem> items = new LinkedList<>();
        getChildren().forEach(res -> {
            items.add(new ResourceListItemImpl(request, res));
        });
        return items;
    }

    @Override
    @JsonIgnore
    public List<ListItem> getItems() {
        if (items == null) {
            items = readItems();
        }
        return items;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (itemModels == null) {
            itemModels = getItemModels(request, ComponentExporter.class);
        }
        return itemModels;
    }

    @NotNull
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
        return Arrays.copyOf(exportedItemsOrder, exportedItemsOrder.length);
    }

    protected Map<String, ComponentExporter> getItemModels(@NotNull SlingHttpServletRequest request,
                                                           @NotNull Class<ComponentExporter> modelClass) {
        Map<String, ComponentExporter> models = new LinkedHashMap<>();
        getFilteredChildren().forEach(child -> {
            ComponentExporter model = modelFactory.getModelFromWrappedRequest(request, child, modelClass);
            if (model != null) {
                models.put(child.getName(), model);
            }
        });
        return models;
    }
}
