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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Tabs;

/**
 * Tabs model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Tabs.class, ComponentExporter.class, ContainerExporter.class},
       resourceType = TabsImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TabsImpl extends AbstractPanelContainerImpl implements Tabs {

    /**
     * The resource type.
     */
    public final static String RESOURCE_TYPE = "core/wcm/components/tabs/v1/tabs";

    /**
     * The accessibility label.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String accessibilityLabel;

    @Override
    @Nullable
    public String getAccessibilityLabel() {
        return this.accessibilityLabel;
    }

    @Override
    public String[] getDataLayerShownItems() {
        String activeItemName = getActiveItem();
        return this.getChildren().stream()
                .filter(e -> StringUtils.equals(e.getResource().getName(), activeItemName))
                .findFirst()
                .map(PanelContainerItemImpl::getId)
                .map(id -> new String[]{id})
                .orElseGet(() -> new String[0]);
    }
}
