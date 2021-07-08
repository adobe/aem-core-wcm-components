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
