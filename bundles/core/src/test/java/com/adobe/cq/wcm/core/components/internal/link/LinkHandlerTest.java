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
package com.adobe.cq.wcm.core.components.internal.link;

import static com.adobe.cq.wcm.core.components.internal.link.LinkNameConstants.PN_LINK_TARGET;
import static com.adobe.cq.wcm.core.components.internal.link.LinkNameConstants.PN_LINK_URL;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Link;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

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
        assertNull(link.getTargetPage());
    }

    @Test
    void testResourceExternalLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost");
        Link link = underTest.getLink(linkResource);
        
        assertValidLink(link, "http://myhost");
        assertNull(link.getTargetPage());
    }

    @Test
    void testResourceExternalLinkWithTarget() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, "_blank");
        Link link = underTest.getLink(linkResource);
        
        assertValidLink(link, "http://myhost", "_blank");
        assertNull(link.getTargetPage());
}

    @Test
    void testResourceExternalLinkWithInvalidTarget() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "http://myhost",
                PN_LINK_TARGET, "_invalid");
        Link link = underTest.getLink(linkResource);
        
        assertValidLink(link, "http://myhost");
        assertNull(link.getTargetPage());
    }

    @Test
    void testResourcePageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, page.getPath());
        Link link = underTest.getLink(linkResource);
        assertValidLink(link, page.getPath() + ".html");
        assertEquals(page, link.getTargetPage());
    }

    @Test
    void testResourceInvalidPageLink() {
        Resource linkResource = context.create().resource(page, "link",
                PN_LINK_URL, "/content/non-existing");
        Link link = underTest.getLink(linkResource);

        // TODO: this link should be handled as invalid. but we keep this behavior for now to keep backwards compatibility
        assertValidLink(link, "/content/non-existing");
        assertNull(link.getTargetPage());
    }

    @Test
    void testPageLink() {
        Link link = underTest.getLink(page);
        
        assertValidLink(link, page.getPath() + ".html");
        assertEquals(page, link.getTargetPage());
    }

    @Test
    void testPageLink_Null() {
        Link link = underTest.getLink((Page)null);
        
        assertInvalidLink(link);
        assertNull(link.getTargetPage());
    }

    @Test
    void testLinkURLPageLinkWithTarget() {
        Link link = underTest.getLink(page.getPath(), "_blank");
        
        assertValidLink(link, page.getPath() + ".html", "_blank");
        assertEquals(page, link.getTargetPage());
    }

    @Test
    void testInvalidLink() {
        Link link = underTest.getInvalid();
        
        assertInvalidLink(link);
        assertNull(link.getTargetPage());
    }

}
