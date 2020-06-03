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
package com.adobe.cq.wcm.core.extensions.amp.internal.models.v1;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import com.adobe.cq.wcm.core.extensions.amp.internal.AmpUtil;
import com.adobe.cq.wcm.core.extensions.amp.models.ClientLibrary;
import com.adobe.cq.wcm.core.extensions.amp.services.ClientLibraryAggregatorService;
import com.day.cq.wcm.api.Page;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since com.adobe.cq.wcm.core.components.models 12.11.0
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    adapters = {ClientLibrary.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ClientLibraryImpl implements ClientLibrary {

    private static final Logger LOG = LoggerFactory.getLogger(ClientLibraryImpl.class);

    @OSGiService
    @Nullable
    private ClientLibraryAggregatorService aggregatorService;

    @Inject
    @Nullable
    private String categories;

    @ScriptVariable
    @Nullable
    private Page currentPage;

    @Inject
    @Nullable
    private String fallbackPath;

    @Inject
    @Nullable
    private String primaryPath;

    @Inject
    @Nullable
    private String type;


    /**
     * Returns the aggregated content of the specified clientlib type from the given comma delimited list of categories.
     * @return The aggregated clientlib output.
     */
    @Override
    public String getInline() {
        return Optional.ofNullable(this.aggregatorService)
            .map(aggregator -> aggregator.getClientLibOutput(categories, type))
            .orElse(null);
    }

    /**
     * Returns the aggregated content of the specified clientlib type from the given categories and all categories of
     * the current page's child resources.
     * @return The aggregated clientlib output.
     */
    @Override
    public String getInlineLimited() {
        if (this.currentPage != null && this.aggregatorService != null) {
            Set<String> resourceTypes = AmpUtil.getResourceTypes(currentPage.getContentResource(),
                aggregatorService.getResourceTypeRegex(), new HashSet<>());

            try (ResourceResolver resolver = aggregatorService.getClientlibResourceResolver()) {
                AmpUtil.getTemplateResourceTypes(currentPage, aggregatorService.getResourceTypeRegex(), resolver,
                    resourceTypes);
            } catch (LoginException e) {
                LOG.error("Unable to get the service resource resolver.", e);
            }

            return aggregatorService.getClientLibOutput(categories, type, resourceTypes, primaryPath, fallbackPath);
        }
        return null;
    }
}
