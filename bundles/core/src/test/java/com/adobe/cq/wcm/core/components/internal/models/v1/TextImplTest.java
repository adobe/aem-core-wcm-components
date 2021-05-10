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

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Text;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class TextImplTest {

    private static final String ROOT = "/content/text";
    private static final String TEST_BASE = "/text";
    private static final String TEXT_1 = ROOT + "/rich-text";
    private static final String TEXT_2 = ROOT + "/plain-text";
    private static final String TEXT_3 = ROOT + "/empty-text";
    private static final String TEXT_4 = ROOT + "/rich-text-v2";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", ROOT);
    }

    @Test
    void testRichText() {
        Text text = getTextUnderTest(TEXT_1);
        assertEquals("<p>rich</p>", text.getText());
        assertTrue(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXT_1));
    }

    @Test
    void testPlainText() {
        Text text = getTextUnderTest(TEXT_2);
        assertEquals("plain", text.getText());
        assertFalse(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXT_2));
    }

    @Test
    void testEmptyText() {
        Text text = getTextUnderTest(TEXT_3);
        assertNull(text.getText());
        assertFalse(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXT_3));
    }

    @Test
    void testExportedType() {
        Text text = getTextUnderTest(TEXT_1);
        assertEquals("core/wcm/components/text/v1/text", text.getExportedType());
    }

    @Test
    void testV2JSONExport() {
        Text text = getTextUnderTest(TEXT_4);
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXT_4));
    }

    private Text getTextUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Text.class);
    }
}
