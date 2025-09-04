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
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

/**
 * V2 Language Navigation model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {LanguageNavigation.class, ComponentExporter.class},
    resourceType = {LanguageNavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class LanguageNavigationImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImpl implements LanguageNavigation {

    /**
     * V2 Language Navigation resource type.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/languagenavigation/v2/languagenavigation";

    @Override
    protected LanguageNavigationItem newLanguageNavigationItem(@NotNull final Page page,
                                                               final boolean active,
                                                               final boolean current,
                                                               @NotNull final LinkManager linkManager,
                                                               final int level,
                                                               @NotNull final Supplier<List<NavigationItem>> children,
                                                               final String title,
                                                               final String parentId,
                                                               final Component component) {
        return new LanguageNavigationItemImpl(page, active, current, linkManager, level, children, title, parentId, component);
    }

}
