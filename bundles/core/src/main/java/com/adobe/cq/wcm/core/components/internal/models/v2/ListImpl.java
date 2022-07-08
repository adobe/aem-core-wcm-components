/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ListImpl implements List {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v2/list";

    @Self
    private LinkManager linkManager;

    /**
     * Result list.
     */
    private Collection<ListItem> listItems;

    @Override
    @NotNull
    @JsonProperty("items")
    public Collection<ListItem> getListItems() {
        if (this.listItems == null) {
            this.listItems = super.getItems().stream()
                .filter(Objects::nonNull)
                .map(page -> newPageListItem(linkManager, page, getId(), component))
                .collect(Collectors.toList());
        }
        return this.listItems;
    }

    protected ListItem newPageListItem(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component) {
        return new PageListItemImpl(linkManager, page, parentId, component);
    }

}
