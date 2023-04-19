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

package com.adobe.cq.wcm.core.components.internal.models.v2;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.Button;

import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class ButtonImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.ButtonImplTest {

    private static final String TEST_BASE = "/button/v2";
    private static final String BUTTON_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/button-2";
    private static final String BUTTON_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/button-3";

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = ButtonImpl.RESOURCE_TYPE;
        internalSetup();
    }

    @Test
    @SuppressWarnings("deprecation")
    @Override
    protected void testGetLink() {
        Button button = getButtonUnderTest(BUTTON_1);
        assertEquals("https://www.adobe.com", button.getLink());
        assertValidLink(button.getButtonLink(), "https://www.adobe.com", "_blank");
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(testBase, "button1"));
    }

    @Test
    protected void testGetLink_withOldLinkProp() {
        Button button = getButtonUnderTest(BUTTON_2);
        assertEquals("https://www.adobe.com", button.getLink());
        assertValidLink(button.getButtonLink(), "https://www.adobe.com", "_blank");
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(testBase, "button2"));
    }

    @Test
    void testLinkWhichIsAlreadyEncoded() {
        Button button = getButtonUnderTest(BUTTON_3);
        assertEquals("https://www.adobe.com/content/dam/test/docs/pdfs/test/360/360%20Test%20Test%20Test%20123.pdf.coredownload.inline.pdf", button.getButtonLink().getHtmlAttributes().get("href"));
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(testBase, "button3"));
    }

}
