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

package com.adobe.cq.wcm.core.components.internal.models.v3;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.Title;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class TitleImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.TitleImplTest {

    private static final String TEST_BASE = "/title/v3";

    protected static final String TITLE_RESOURCE_TITLE_REDIRECT_CHAIN = TEST_PAGE + "/jcr:content/par/title-redirect-chain";
    protected static final String TITLE_RESOURCE_TITLE_REDIRECT_EXTERNAL_URL = TEST_PAGE + "/jcr:content/par/title-redirect-external-url";

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = TitleImpl.RESOURCE_TYPE;
        internalSetup();
    }

    @Test
    @Override
    protected void testGetLink() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_LINK_V2);
        assertValidLink(title.getLink(), "https://www.adobe.com", "World", "World title" );
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_JCR_TITLE_LINK_V2));
    }

    @Test
    protected void testGetLinkFromRedirectChain() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_TITLE_REDIRECT_CHAIN);
        assertValidLink(title.getLink(), "/content/title/redirect/redirect-page-3.html", "Should Redirect to Page 3 (accessibility label)", "Should Redirect to Page 3 (title)");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_TITLE_REDIRECT_CHAIN));
    }

    @Test
    protected void testGetLinkFromRedirectToExternalURL() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_TITLE_REDIRECT_EXTERNAL_URL);
        assertValidLink(title.getLink(), "https://www.adobe.com", "Adobe", "Adobe title");
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(testBase, TITLE_RESOURCE_TITLE_REDIRECT_EXTERNAL_URL));
    }

}
