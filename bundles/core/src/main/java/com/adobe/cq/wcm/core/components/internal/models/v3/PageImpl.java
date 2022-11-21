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

import java.util.Optional;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.adobe.cq.wcm.core.components.internal.models.v2.RedirectItemImpl;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v3/page";

    /**
     * Style property name to load custom Javascript libraries asynchronously.
     */
    protected static final String PN_CLIENTLIBS_ASYNC = "clientlibsAsync";

    @Override
    @JsonIgnore
    public boolean isClientlibsAsync() {
        if (currentStyle != null) {
            return currentStyle.get(PN_CLIENTLIBS_ASYNC, false);
        }
        return false;
    }

    @Override
    @JsonIgnore
    public boolean isDataLayerClientlibIncluded() {
        return Optional.ofNullable(resource.adaptTo(ConfigurationBuilder.class))
                .map(builder -> builder.as(DataLayerConfig.class))
                .map(config -> !config.skipClientlibInclude())
                .orElse(true);
    }

    protected NavigationItem newRedirectItem(@NotNull String redirectTarget, @NotNull SlingHttpServletRequest request, @NotNull LinkManager linkManager) {
        return new RedirectItemImpl(redirectTarget, request, linkManager);
    }

}
