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

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.spi.ImplementationPicker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.link.LinkProcessor;

@Component(property= Constants.SERVICE_RANKING+":Integer="+Integer.MAX_VALUE, service= LinkProcessor.class)
public class MappingLinkProcessor implements LinkProcessor {

    public static final String LINK_PROCESSOR = "link-processor";

    private static final Logger LOG = LoggerFactory.getLogger(MappingLinkProcessor.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public @Nullable String process(@Nullable String linkUrl) {
        if (StringUtils.isNotEmpty(linkUrl)) {
            try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(
                    Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, LINK_PROCESSOR))) {
                return resourceResolver.map(linkUrl);
            } catch (LoginException e) {
                LOG.error(e.getMessage());
            }
        }
        return null;
    }
}
