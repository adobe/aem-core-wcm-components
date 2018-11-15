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
package com.adobe.cq.wcm.core.components.internal.models;

import java.util.*;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl implements Container {

    @SlingObject
    protected Resource resource;

    @Self
    protected SlingHttpServletRequest request;

    @OSGiService
    private SlingModelFilter slingModelFilter;

    @OSGiService
    private ModelFactory modelFactory;

    protected List<ListItem> items;
    private Map<String, ? extends ComponentExporter> itemModels;
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
    @JsonIgnore
    public List<ListItem> getItems() {
        if (items == null) {
            items = readItems();
        }
        return items;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        if (itemModels == null) {
            itemModels = getItemModels(request, ComponentExporter.class);
        }
        return itemModels;
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
        return Arrays.copyOf(exportedItemsOrder, exportedItemsOrder.length);
    }

    private <T> Map<String, T> getItemModels(@Nonnull SlingHttpServletRequest request,
                                              @Nonnull Class<T> modelClass) {
        Map<String, T> models = new LinkedHashMap<>();
        List<ListItem> items = getItems();
        List<Resource> itemResources = new ArrayList<>();
        for (ListItem item : items) {
            if (item != null && StringUtils.isNotEmpty(item.getName())) {
                Resource itemResource = request.getResourceResolver().getResource(resource, item.getName());
                if (itemResource != null) {
                    itemResources.add(itemResource);
                }
            }
        }
        for (Resource child : slingModelFilter.filterChildResources(itemResources)) {
            T model = modelFactory.getModelFromWrappedRequest(request, child, modelClass);
            if (model != null) {
                for (ListItem item : items) {
                    if (item != null && StringUtils.isNotEmpty(item.getName()) && StringUtils.equals(item.getName(), child.getName())) {
                        JsonWrapper<T> wrappedModel = new JsonWrapper<T>(model, item);
                        models.put(child.getName(), (T) wrappedModel);
                    }
                }
            }
        }
        return models;
    }

    /**
     * Wrapper class used to add specific properties of the container items to the JSON serialization of the underlying container item model
     *
     * @param <T> the model class of the underlying container item model
     */
    static class JsonWrapper<T> {
        private T inner;
        private String panelTitle;

        JsonWrapper(T inner, ListItem item) {
            this.inner = inner;
            this.panelTitle = item.getTitle();
        }

        /**
         * @return the underlying container item model
         */
        @JsonUnwrapped
        public T getInner() {
            return inner;
        }

        /**
         * @return the container item title
         */
        @JsonProperty(ListItem.PN_PANEL_TITLE)
        public String getPanelTitle() {
            return panelTitle;
        }
    }

}
