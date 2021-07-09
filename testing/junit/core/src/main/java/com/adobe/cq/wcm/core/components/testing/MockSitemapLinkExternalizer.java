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
package com.adobe.cq.wcm.core.components.testing;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.sitemap.spi.common.SitemapLinkExternalizer;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;

@Component(
    service = SitemapLinkExternalizer.class
)
public class MockSitemapLinkExternalizer implements SitemapLinkExternalizer {

    private final String mockHost;

    public MockSitemapLinkExternalizer() {
        this("http://foo.bar");
    }

    public MockSitemapLinkExternalizer(String mockHost) {
        this.mockHost = mockHost;
    }

    @Override public @Nullable String externalize(SlingHttpServletRequest request, String path) {
        return mockHost + path;
    }

    @Override public @Nullable String externalize(Resource resource) {
        return mockHost + resource.getPath();
    }
}
