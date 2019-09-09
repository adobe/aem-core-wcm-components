/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

package com.adobe.cq.wcm.core.components.internal.models.v1;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Title;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class TitleImplTest {

    private static final String TEST_BASE = "/title";
    private static final String TEST_PAGE = "/content/title";
    private static final String TITLE_RESOURCE_JCR_TITLE = TEST_PAGE + "/jcr:content/par/title-jcr-title";
    private static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TEST_PAGE + "/jcr:content/par/title-jcr-title-type";
    private static final String TITLE_NOPROPS = TEST_PAGE + "/jcr:content/par/title-noprops";
    private static final String TITLE_WRONGTYPE = TEST_PAGE + "/jcr:content/par/title-wrongtype";
    private static final String TITLE_RESOURCE_JCR_TITLE_V2 = TEST_PAGE + "/jcr:content/par/title-jcr-title-v2";
    private static final String TITLE_RESOURCE_JCR_TITLE_LINK_V2 = TEST_PAGE + "/jcr:content/par/title-jcr-title-link-v2";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
    }

    @Test
    void testExportedType() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertEquals(TitleImpl.RESOURCE_TYPE_V1, title.getExportedType());
    }

    @Test
    void testGetTitleFromResource() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertNull(title.getType());
        assertInvalidLink(title.getLink());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE));
    }

    @Test
    void testGetTitleFromResourceWithElementInfo() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_TYPE);
        assertEquals("Hello World", title.getText());
        assertEquals("h2", title.getType());
        assertInvalidLink(title.getLink());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE_TYPE));
    }

    @Test
    void testGetTitleResourcePageStyleType() {
        Title title = getTitleUnderTest(TITLE_NOPROPS,
                Title.PN_DESIGN_DEFAULT_TYPE, "h2");
        assertEquals("h2", title.getType());
        assertInvalidLink(title.getLink());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_NOPROPS));
    }

    @Test
    void testGetTitleFromCurrentPageWithWrongElementInfo() {
        Title title = getTitleUnderTest(TITLE_WRONGTYPE);
        assertNull(title.getType());
        assertInvalidLink(title.getLink());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_WRONGTYPE));
    }

    @Test
    void testV2JSONExport() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_V2);
        assertNull(title.getLinkURL());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE_V2));
    }

    @Test
    void testGetLink() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2);
        assertValidLink(title.getLink(), "https://www.adobe.com");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE_LINK_V2));
    }

    @Test
    void testTitleWithLinksDisabled() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2,
                Title.PN_TITLE_LINK_DISABLED, true);
        assertValidLink(title.getLink(), "https://www.adobe.com");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, "title-linkdisabled"));
    }

    private Title getTitleUnderTest(String resourcePath, Object ... properties) {
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Title.class);
    }
}
