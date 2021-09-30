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

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableMap;

import static com.adobe.cq.wcm.core.components.internal.link.LinkImpl.*;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class LinkImplTest {

    private static final String URL = "/url.html";

    @Test
    void testValidLink() {
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, null, null);
        assertValidLink(link, URL);
        assertNull(link.getReference());
        assertEquals(URL, link.getMappedURL());
    }

    @Test
    void testValidLinkWithTarget() {
        Link<Page> link = new LinkImpl(URL, URL, MockExternalizerFactory.ROOT + URL, null,
            new HashMap<String, String>() {{
                put(ATTR_TARGET, "_blank");
            }});
        assertValidLink(link, URL, "_blank");
        assertNull(link.getReference());
    }

    @Test
    void testValidLinkWithoutTarget() {
        Link link = new LinkImpl(URL, URL, MockExternalizerFactory.ROOT + URL, null, null);

        assertValidLink(link, URL, (String) null);
        assertNull(link.getReference());
    }

    @Test
    void testValidLinkWithTargetAndTargetPage() {
        Page page = mock(Page.class);
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page,
            new HashMap<String, String>() {{
                put(ATTR_TARGET,
                    "_blank");
            }});
        assertValidLink(link, URL, "_blank");
        assertSame(page, link.getReference());
    }

    @Test
    void testValidLinkWithTargetTargetPageAccessibilityLabelAndTitleAttribute() {
        Page page = mock(Page.class);
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page, new HashMap<String, String>() {{
            put(ATTR_TARGET, "_blank");
            put(ATTR_ARIA_LABEL, "Url Label");
            put(ATTR_TITLE, "Url Title");
        }});

        assertValidLink(link, URL, "Url Label", "Url Title", "_blank");
        assertSame(page, link.getReference());
    }

    @Test
    void testValidLinkWithTargetPageAccessibilityLabelTitleAttributeAndWithoutTarget() {
        Page page = mock(Page.class);
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page, new HashMap<String, String>() {{
            put(ATTR_ARIA_LABEL, "Url Label");
            put(ATTR_TITLE, "Url Title");
        }});

        assertValidLink(link, URL, "Url Label", "Url Title", null);
        assertSame(page, link.getReference());
    }

    @Test
    void testInvalidLink() {
        Link<Page> link = new LinkImpl<>(null, null, null, null, null);
        assertInvalidLink(link);
        assertNull(link.getReference());
        assertNull(link.getMappedURL());
    }

    @Test
    void testValidLikWithFilteredHtmlAttributes() {
        Page page = mock(Page.class);
        String invalidAttribute = "invalidAttribute";
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page, ImmutableMap.of(invalidAttribute,
            "invalidValue"));
        assertValidLink(link, URL);
        assertNull(link.getHtmlAttributes().get(invalidAttribute));
    }

    /**
     * This test validates that annotations on the implementation overrule the ones on the interface. A typical use case would
     * be for an implementation to decide to export the externalized link instead of the mapped one per default as "url" field.
     */
    @Test
    public void testJacksonAnnotationInheritance() {
        // given
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, null, null);
        CustomLinkImpl<Page> customLink = new CustomLinkImpl<>(link);
        ObjectMapper objectMapper = new ObjectMapper();

        // when
        Map<String, Object> linkData = objectMapper.convertValue(link, Map.class);
        Map<String, Object> customLinkData = objectMapper.convertValue(customLink, Map.class);

        // then
        assertEquals(link.getMappedURL(), linkData.get("url"));
        assertEquals(customLink.getExternalizedURL(), customLinkData.get("url"));
    }

    private static class CustomLinkImpl<T> implements Link<T> {
        private final Link<T> delegate;

        CustomLinkImpl(Link<T> delegate) {
            this.delegate = delegate;
        }

        @Override public boolean isValid() {
            return delegate.isValid();
        }

        @Override @Nullable public String getURL() {
            return delegate.getURL();
        }

        @Override @JsonIgnore @Nullable public String getMappedURL() {
            return delegate.getMappedURL();
        }

        @Override
        @JsonIgnore(value = false)
        @JsonProperty("url")
        @Nullable
        public String getExternalizedURL() {
            return delegate.getExternalizedURL();
        }

        @Override @NotNull public Map<String, String> getHtmlAttributes() {
            return delegate.getHtmlAttributes();
        }

        @Override @Nullable public T getReference() {
            return delegate.getReference();
        }
    }
}
