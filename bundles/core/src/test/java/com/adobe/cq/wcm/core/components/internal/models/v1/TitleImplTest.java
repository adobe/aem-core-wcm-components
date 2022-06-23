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

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Title;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class TitleImplTest {

    private static final String TEST_BASE = "/title";
    protected static final String TEST_PAGE = "/content/title";
    protected static final String TITLE_RESOURCE_JCR_TITLE = TEST_PAGE + "/jcr:content/par/title-jcr-title";
    protected static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TEST_PAGE + "/jcr:content/par/title-jcr-title-type";
    protected static final String TITLE_NOPROPS = TEST_PAGE + "/jcr:content/par/title-noprops";
    protected static final String TITLE_WRONGTYPE = TEST_PAGE + "/jcr:content/par/title-wrongtype";
    protected static final String TITLE_RESOURCE_JCR_TITLE_V2 = TEST_PAGE + "/jcr:content/par/title-jcr-title-v2";
    protected static final String TITLE_RESOURCE_JCR_TITLE_LINK_V2 = TEST_PAGE + "/jcr:content/par/title-jcr-title-link-v2";
    protected static final String TITLE_RESOURCE_TITLE_V2_REDIRECT_CHAIN = TEST_PAGE + "/jcr:content/par/title-v2-redirect-chain";
    protected static final String TITLE_RESOURCE_TITLE_V2_REDIRECT_EXTERNAL_URL = TEST_PAGE + "/jcr:content/par/title-v2-redirect-external-url";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    protected String testBase;
    protected String resourceType;

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = TitleImpl.RESOURCE_TYPE_V1;
        internalSetup();
    }

    protected void internalSetup() {
        context.load().json(testBase + "/test-content.json", TEST_PAGE);
    }

    @Test
    protected void testExportedType() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertEquals(resourceType, title.getExportedType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE));
    }

    @Test
    protected void testGetTitleFromResource() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertNull(title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE));
    }

    @Test
    protected void testGetTitleFromResourceWithElementInfo() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_TYPE);
        assertEquals("Hello World", title.getText());
        assertEquals("h2", title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE_TYPE));
    }

    @Test
    protected void testGetTitleResourcePageStyleType() {
        Title title = getTitleUnderTest(TITLE_NOPROPS,
                Title.PN_DESIGN_DEFAULT_TYPE, "h2");
        assertEquals("h2", title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_NOPROPS));
    }

    @Test
    protected void testGetTitleFromCurrentPageWithWrongElementInfo() {
        Title title = getTitleUnderTest(TITLE_WRONGTYPE);
        assertNull(title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_WRONGTYPE));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testV2JSONExport() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_V2);
        assertNull(title.getLinkURL());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE_V2));
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testGetLinkUrl() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2);
        assertEquals("https://www.adobe.com", title.getLinkURL());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE_LINK_V2));
    }

    @Test
    protected void testGetLink() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2);
        assertValidLink(title.getLink(), "https://www.adobe.com", "World", "World title");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE_LINK_V2));
    }

    @Test
    protected void testGetLinkFromRedirectChain() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_TITLE_V2_REDIRECT_CHAIN);
        assertValidLink(title.getLink(), "/content/title/redirect/redirect-page-3.html", "Should Redirect to Page 3 (accessibility label)", "Should Redirect to Page 3 (title)");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_TITLE_V2_REDIRECT_CHAIN));
    }

    @Test
    protected void testGetLinkFromRedirectToExternalURL() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_TITLE_V2_REDIRECT_EXTERNAL_URL);
        assertValidLink(title.getLink(), "https://www.adobe.com", "Adobe", "Adobe title");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_TITLE_V2_REDIRECT_EXTERNAL_URL));
    }

    @Test
    protected void testTitleWithLinksDisabled() {
        Utils.enableDataLayer(context, true);
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2,
                Title.PN_TITLE_LINK_DISABLED, true);
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, "title-linkdisabled"));
    }

    protected Title getTitleUnderTest(String resourcePath, Object ... properties) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        return context.request().adaptTo(Title.class);
    }
}
