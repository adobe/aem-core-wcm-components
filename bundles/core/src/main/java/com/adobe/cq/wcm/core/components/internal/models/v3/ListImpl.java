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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ListImpl implements List {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v3/list";

    /**
     * Default flag indicating if list items should be displayed as teasers.
     */
    private static final boolean DISPLAY_ITEM_AS_TEASER_DEFAULT = false;

    /**
     * Flag indicating if items should be displayed as teasers.
     */
    private boolean displayItemAsTeaser;

    protected ListItem newPageListItem(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component) {
        return new PageListItemImpl(linkManager, page, parentId, component, showDescription, linkItems || displayItemAsTeaser);
    }

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        displayItemAsTeaser = properties.get(PN_DISPLAY_ITEM_AS_TEASER, currentStyle.get(PN_DISPLAY_ITEM_AS_TEASER, DISPLAY_ITEM_AS_TEASER_DEFAULT));
    }

    @Override
    @JsonProperty("displayItemAsTeaser")
    public boolean displayItemAsTeaser() {
        return displayItemAsTeaser;
    }

}
