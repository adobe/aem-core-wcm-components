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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LinkTestUtils {

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL) {
        assertValidLink(link, linkURL, (SlingHttpServletRequest)null);
    }

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL,
                                       @Nullable SlingHttpServletRequest request) {
        assertTrue(link.isValid(), "linkValid");
        assertEquals(MockExternalizerFactory.ROOT + linkURL, link.getExternalizedURL(), "linkExternalizedUrl");
        if (request != null && StringUtils.isNotEmpty(request.getContextPath()) && !linkURL.startsWith("http")) {
            linkURL = request.getContextPath().concat(linkURL);
        }
        assertEquals(linkURL, link.getURL(), "linkUrl");
        assertEquals(linkURL.replaceAll("^\\/content\\/links\\/site1\\/(.+)","/content/site1/$1"), link.getMappedURL(), "linkMappedUrl");
        assertEquals(ImmutableMap.of("href", linkURL), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL, @Nullable String linkTarget) {
        if (linkTarget == null) {
            assertValidLink(link,  linkURL);
            return;
        }
        assertTrue(link.isValid(), "linkValid");
        assertEquals(linkURL, link.getURL(), "linkUrl");
        assertEquals(linkURL.replaceAll("^\\/content\\/links\\/site1\\/(.+)","/content/site1/$1"), link.getMappedURL(), "linkMappedUrl");
        assertEquals(MockExternalizerFactory.ROOT + linkURL, link.getExternalizedURL(), "linkExternalizedUrl");
        assertEquals(ImmutableMap.of("href", linkURL, "target", linkTarget), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL, @NotNull String linkAccessibilityLabel, @NotNull String linkTitleAttribute) {
        assertTrue(link.isValid(), "linkValid");
        assertEquals(linkURL, link.getURL(), "linkUrl");
        assertEquals(linkURL, link.getMappedURL(), "linkMappedUrl");
        assertEquals(MockExternalizerFactory.ROOT + linkURL, link.getExternalizedURL(), "linkExternalizedUrl");
        assertEquals(ImmutableMap.of("href", linkURL, "aria-label", linkAccessibilityLabel, "title", linkTitleAttribute), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL, @NotNull String linkAccessibilityLabel, @NotNull String linkTitleAttribute, @Nullable String linkTarget) {
        if (linkTarget == null) {
            assertValidLink(link,  linkURL, linkAccessibilityLabel, linkTitleAttribute);
            return;
        }
        assertTrue(link.isValid(), "linkValid");
        assertEquals(linkURL, link.getURL(), "linkUrl");
        assertEquals(linkURL, link.getMappedURL(), "linkMappedUrl");
        assertEquals(MockExternalizerFactory.ROOT + linkURL, link.getExternalizedURL(), "linkExternalizedUrl");
        assertEquals(ImmutableMap.of("href", linkURL, "aria-label", linkAccessibilityLabel, "title", linkTitleAttribute, "target", linkTarget), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertInvalidLink(@NotNull Link link) {
        assertFalse(link.isValid(), "linkValid");
        assertNull(link.getURL(), "linkURL");
        assertNotNull(link.getHtmlAttributes(), "linkHtmlAttributes not null");
        assertTrue(link.getHtmlAttributes().isEmpty(), "linkHtmlAttributes empty");
    }

}
