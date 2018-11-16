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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PanelContainerImpl extends AbstractContainerImpl implements Container {

    @Override
    protected List<ListItem> readItems() {
        List<ListItem> items = new LinkedList<>();
        for (Resource res : getChildren()) {
            items.add(new PanelContainerItemImpl(request, res));
        }
        return items;
    }

    @Override
    protected Map<String, ComponentExporter> getItemModels(@Nonnull SlingHttpServletRequest request,
                                                           @Nonnull Class<ComponentExporter> modelClass) {
        Map<String, ComponentExporter> models = super.getItemModels(request, modelClass);
        for (Map.Entry<String, ComponentExporter> entry : models.entrySet()) {
            for (ListItem item : getItems()) {
                if (item != null && StringUtils.isNotEmpty(item.getName()) && StringUtils.equals(item.getName(), entry.getKey())) {
                    entry.setValue(new JsonWrapper(entry.getValue(), item));
                }
            }
        }

        return models;
    }

    /**
     * Wrapper class used to add specific properties of the container items to the JSON serialization of the underlying container item model
     *
     */
    @JsonInclude(JsonInclude.Include.ALWAYS)
    static class JsonWrapper implements ComponentExporter {
        private ComponentExporter inner;
        private String panelTitle;

        JsonWrapper(ComponentExporter inner, ListItem item) {
            this.inner = inner;
            this.panelTitle = item.getTitle();
        }

        /**
         * @return the underlying container item model
         */
        @JsonUnwrapped
        public ComponentExporter getInner() {
            return inner;
        }

        /**
         * @return the container item title
         */
        @JsonProperty(PanelContainerItemImpl.PN_PANEL_TITLE)
        public String getPanelTitle() {
            return panelTitle;
        }

        @Nonnull
        @Override
        public String getExportedType() {
            if (inner != null) {
                return inner.getExportedType();
            } else {
                return "";
            }
        }
    }
}
