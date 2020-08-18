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
package com.adobe.cq.wcm.core.components.internal.models.v1.form;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.Text;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class TextImplTest {

    private static final String TEST_BASE = "/form/text";
    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";
    private static final String TEXTINPUT1_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text";
    private static final String TEXTINPUT2_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text_185087333";
    private static final String TEXTINPUT3_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text-v2";


    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTAINING_PAGE);
        context.registerService(FormStructureHelperFactory.class, resource -> null);
        FormsHelperStubber.createStub();
    }

    @Test
    public void testDefaultInput() {
        Text text = getTextUnderTest(TEXTINPUT1_PATH);
        assertEquals("text", text.getName());
        assertEquals("Text input field", text.getTitle());
        assertFalse(text.isRequired());
        assertEquals("", text.getRequiredMessage());
        assertFalse(text.isReadOnly());
        assertEquals("text", text.getType());
        assertEquals("", text.getConstraintMessage());
        assertEquals("", text.getValue());
        assertEquals(2, text.getRows());
        assertEquals("", text.getHelpMessage());
        assertFalse(text.hideTitle());
    }

    @Test
    public void testInputWithCustomDataAndAttributes() {
        Text text = getTextUnderTest(TEXTINPUT2_PATH);
        assertEquals("Custom Name", text.getName());
        assertEquals("Custom title", text.getTitle());
        assertTrue(text.isRequired());
        assertEquals("please fill the field", text.getRequiredMessage());
        assertTrue(text.isReadOnly());
        assertEquals("email", text.getType());
        assertEquals("The value should be a valid email address", text.getConstraintMessage());
        assertEquals("Prefilled Sample Input", text.getValue());
        assertEquals(3, text.getRows());
        assertEquals("Custom help/placeholder message", text.getHelpMessage());
        assertTrue(text.hideTitle());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXTINPUT2_PATH));
    }

    @Test
    public void testV2JSONExport() {
        Text text = getTextUnderTest(TEXTINPUT3_PATH);
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXTINPUT3_PATH));
    }

    private Text getTextUnderTest(String resourcePath) {
        context.currentResource(Objects.requireNonNull(context.resourceResolver().getResource(resourcePath)));
        return context.request().adaptTo(Text.class);
    }
}
