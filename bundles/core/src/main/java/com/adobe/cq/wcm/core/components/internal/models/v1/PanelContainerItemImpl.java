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

import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

/**
 * Panel container item implementation.
 */
public class PanelContainerItemImpl extends ResourceListItemImpl implements ListItem {

    /**
     * Name of the property that contains the panel item's title.
     */
    public static final String PN_PANEL_TITLE = "cq:panelTitle";

    /**
     * Construct a panel item.
     *
     * @param resource The resource.
     * @param parentId The ID of the containing component.
     */
    public PanelContainerItemImpl(@NotNull final LinkManager linkManager, @NotNull final Resource resource, final String parentId, Component component,
                                  Page currentPage) {
        super(linkManager, resource, parentId, component);
        setCurrentPage(currentPage);
        title = Optional.ofNullable(resource.getValueMap().get(PN_PANEL_TITLE, String.class))
            .orElseGet(() -> resource.getValueMap().get(JcrConstants.JCR_TITLE, String.class));
    }
}
