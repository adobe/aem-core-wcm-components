/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.adobe.cq.wcm.core.components.models.ClientLibrary;
import com.adobe.cq.wcm.core.components.services.ClientLibraryAggregatorService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

/**
 * @since com.adobe.cq.wcm.core.components.models 12.11.0
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = {ClientLibrary.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClientLibraryImpl implements ClientLibrary {

    @Inject
    private String categories;

    @Inject
    private String type;

    @OSGiService
    private ClientLibraryAggregatorService clientLibraryAggregatorService;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private Style currentStyle;

    @Override
    public String getInline() {
        String baseClientLibrary = currentStyle.get("appResourcesClientlib", String.class);
        return clientLibraryAggregatorService.getClientLibOutput(baseClientLibrary, categories, type);
    }

    @Override
    public String getInlineLimited() {
        String baseClientLibrary = currentStyle.get("appResourcesClientlib", String.class);
        Set<String> resourceTypes = getResourceTypes(currentPage.getContentResource(), new HashSet<>());
        return clientLibraryAggregatorService.getClientLibOutput(baseClientLibrary, resourceTypes, type);
    }

    private Set<String> getResourceTypes(Resource resource, Set<String> resourceTypes) {
        resourceTypes.add(resource.getResourceType());
        if (resource.hasChildren()) {
            for (Resource child : resource.getChildren()) {
                resourceTypes.add(child.getResourceType());
                if (child.hasChildren()) {
                    getResourceTypes(child, resourceTypes);
                }
            }
        }
        return resourceTypes;
    }
}
