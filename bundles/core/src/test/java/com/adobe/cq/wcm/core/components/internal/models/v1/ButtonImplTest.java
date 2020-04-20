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

package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Button;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
class ButtonImplTest {

    private static final String TEST_BASE = "/button";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String TEST_ROOT_PAGE_GRID = "/button/jcr:content/root/responsivegrid";
    private static final String BUTTON_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/button-1";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);
    }

    @Test
    void testExportedType() {
        Button button = getButtonUnderTest();
        assertEquals(ButtonImpl.RESOURCE_TYPE, button.getExportedType());
    }

    @Test
    void testGetText() {
        Button button = getButtonUnderTest();
        assertEquals("Adobe", button.getText());
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, "button1"));
    }

    @Test
    void testGetLink() {
        Button button = getButtonUnderTest();
        assertEquals("https://www.adobe.com", button.getLink());
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, "button1"));
    }

    @Test
    void testGetIcon() {
        Button button = getButtonUnderTest();
        assertEquals("adobe", button.getIcon());
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, "button1"));
    }

    private Button getButtonUnderTest(Object... properties) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.currentResource(ButtonImplTest.BUTTON_1);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        return context.request().adaptTo(Button.class);
    }
}
