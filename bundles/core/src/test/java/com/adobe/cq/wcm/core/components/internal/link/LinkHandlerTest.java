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

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class LinkHandlerTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();
    
    private Page page;
    private LinkHandler underTest;

    @BeforeEach
    void setUp() {
        page = context.create().page("/content/site1/en");
        context.currentPage(page);
        underTest = context.request().adaptTo(LinkHandler.class);
    }

    @Test
    void testResourceEmpty() {
        Resource linkResource = context.create().resource(page, "link");
        Link link = underTest.getLink(linkResource);
        
        assertInvalidLink(link);
        assertNull(link.getReference());
    }

    @Test
    void testResourceExternalLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost");
        Link link = underTest.getLink(linkResource);
        
        assertValidLink(link, "http://myhost");
        assertNull(link.getReference());
    }

    @ParameterizedTest
    @ValueSource(strings = {"_blank", "_parent", "_top"})
    void testResourceExternalLinkWithAllowedTargets(String target) {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, target);
        Link link = underTest.getLink(linkResource);

        assertValidLink(link, "http://myhost", target);
        assertNull(link.getReference());
    }

    @ParameterizedTest
    @ValueSource(strings = {"_self","_invalid"})
    void testResourceExternalLinkWithInvalidTargets(String target) {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, target);
        Link link = underTest.getLink(linkResource);

        // invalid target or _self target should be stripped away 
        assertValidLink(link, "http://myhost");
        assertNull(link.getReference());
    }

    @Test
    void testResourcePageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, page.getPath());
        Link link = underTest.getLink(linkResource);
        assertValidLink(link, page.getPath() + ".html");
        assertEquals(page, link.getReference());
    }

    @Test
    void testResourceInvalidPageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "/content/non-existing");
        Link link = underTest.getLink(linkResource);

        // TODO: this link should be handled as invalid. but we keep this behavior for now to keep backwards compatibility
        assertValidLink(link, "/content/non-existing");
        assertNull(link.getReference());
    }

    @Test
    void testPageLink() {
        Link<Page> link = underTest.getLink(page);
        
        assertValidLink(link, page.getPath() + ".html");
        assertEquals(page, link.getReference());
    }

    @Test
    void testPageLink_Null() {
        Link<Page> link = underTest.getLink((Page)null);
        
        assertInvalidLink(link);
        assertNull(link.getReference());
    }

    @Test
    void testLinkURLPageLinkWithTarget() {
        Link link = underTest.getLink(page.getPath(), "_blank");
        
        assertValidLink(link, page.getPath() + ".html", "_blank");
        assertEquals(page, link.getReference());
    }

    @Test
    void testInvalidLink() {
        Link link = underTest.getInvalid();
        
        assertInvalidLink(link);
        assertNull(link.getReference());
    }

}
