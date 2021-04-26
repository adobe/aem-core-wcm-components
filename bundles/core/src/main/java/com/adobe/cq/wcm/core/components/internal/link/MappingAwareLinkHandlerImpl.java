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
package com.adobe.cq.wcm.core.components.internal.link;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.LinkHandler;

/**
 * Mapping aware implementation for resolving and validating links from model's resources.
 */
@Model(adaptables= SlingHttpServletRequest.class, adapters = LinkHandler.class, resourceType = MappingAwareLinkHandlerImpl.RESOURCE_TYPE)
public class MappingAwareLinkHandlerImpl extends LinkHandlerImpl implements LinkHandler {

    public static final String RESOURCE_TYPE = "core/wcm/components/linkHandler/mappingAware";

    @Override
    protected @NotNull String map(@NotNull String path) {
        try {
            return StringUtils.defaultString(request.getResourceResolver().map(request, path), path);
        } catch (Exception e) {
            return path;
        }
    }
}
