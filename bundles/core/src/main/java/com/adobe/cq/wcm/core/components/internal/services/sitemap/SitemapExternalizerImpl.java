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
package com.adobe.cq.wcm.core.components.internal.services.sitemap;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.sitemap.common.Externalizer;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * An {@link Externalizer} to be used for sitemaps.
 */
@Component(property = Constants.SERVICE_RANKING + "=I\"100\"")
public class SitemapExternalizerImpl implements Externalizer {

    @Reference
    private com.day.cq.commons.Externalizer externalizer;

    @Override
    @Nullable
    public String externalize(SlingHttpServletRequest request, String path) {
        return externalizer.absoluteLink(request, request.getScheme(), path);
    }

    @Override
    @Nullable
    public String externalize(Resource resource) {
        return externalizer.publishLink(resource.getResourceResolver(), resource.getPath());
    }
}
