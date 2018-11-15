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

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.SlingHttpServletRequest;

import com.adobe.cq.wcm.core.components.models.PanelContainerItem;
import com.day.cq.commons.jcr.JcrConstants;

public class PanelContainerItemImpl extends ResourceListItemImpl implements PanelContainerItem {

    public PanelContainerItemImpl(@Nonnull SlingHttpServletRequest request, @Nonnull Resource resource) {
        super(request, resource);
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            String jcrTitle = valueMap.get(JcrConstants.JCR_TITLE, String.class);
            title = valueMap.get(PanelContainerItem.PN_PANEL_TITLE, jcrTitle);
        }
    }
}
