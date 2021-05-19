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

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.day.cq.wcm.api.Page;
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
                new HashMap<String, String>() {{ put(ATTR_TARGET, "_blank"); }});
        assertValidLink(link, URL, "_blank");
        assertNull(link.getReference());
    }

    @Test
    void testValidLinkWithoutTarget() {
        Link link = new LinkImpl(URL, URL, MockExternalizerFactory.ROOT + URL,null, null);

        assertValidLink(link, URL, (String)null);
        assertNull(link.getReference());
    }

    @Test
    void testValidLinkWithTargetAndTargetPage() {
        Page page = mock(Page.class);
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page,
                new HashMap<String, String>() {{ put(ATTR_TARGET,
                "_blank"); }});
        assertValidLink(link, URL, "_blank");
        assertSame(page, link.getReference());
    }

    @Test
    void testValidLinkWithTargetTargetPageAccessibilityLabelAndTitleAttribute() {
        Page page = mock(Page.class);
        Link<Page> link = new LinkImpl<>(URL, URL, MockExternalizerFactory.ROOT + URL, page, new HashMap<String, String>() {{
            put(ATTR_TARGET, "_blank");
            put(ATTR_ARIA_LABEL,  "Url Label");
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
}
