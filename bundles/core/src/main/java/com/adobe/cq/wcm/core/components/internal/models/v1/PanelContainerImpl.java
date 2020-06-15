/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class PanelContainerImpl extends AbstractContainerImpl implements Container {

    @Override
    @NotNull
    protected List<PanelContainerItemImpl> readItems() {
        return getChildren().stream()
            .map(res -> new PanelContainerItemImpl(request, res, getId()))
            .collect(Collectors.toList());
    }

    @Override
    protected Map<String, ComponentExporter> getItemModels(@NotNull SlingHttpServletRequest request,
                                                           @NotNull Class<ComponentExporter> modelClass) {
        Map<String, ComponentExporter> models = super.getItemModels(request, modelClass);
        models.entrySet().forEach(entry ->
            getItems().stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.isNotEmpty(item.getName()) && StringUtils.equals(item.getName(), entry.getKey()))
                .findFirst()
                .ifPresent(match -> entry.setValue(new JsonWrapper(entry.getValue(), match)))
        );
        return models;
    }

    /**
     * Wrapper class used to add specific properties of the container items to the JSON serialization of the underlying container item model
     *
     */
    static class JsonWrapper implements ComponentExporter {
        private ComponentExporter inner;
        private String panelTitle;

        JsonWrapper(@NotNull ComponentExporter inner, ListItem item) {
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
        @JsonInclude(JsonInclude.Include.ALWAYS)
        public String getPanelTitle() {
            return panelTitle;
        }

        @NotNull
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
