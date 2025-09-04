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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.List;
import java.util.function.Supplier;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

/**
 * V2 Navigation model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
        adapters = {Navigation.class, ComponentExporter.class},
        resourceType = {NavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.NavigationImpl {

    /**
     * V2 Navigation resource type.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/navigation/v2/navigation";

    @Override
    protected NavigationItem newNavigationItem(@NotNull final Page page,
                                               final boolean active,
                                               final boolean current,
                                               @NotNull final LinkManager linkManager,
                                               final int level,
                                               @NotNull final Supplier<@NotNull List<NavigationItem>> children,
                                               final String parentId,
                                               final Component component) {
        return new NavigationItemImpl(page, active, current, linkManager, level, children, parentId, component);
    }

}
