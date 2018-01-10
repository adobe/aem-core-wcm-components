/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.Text;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

public class TextImplTest {

    private static final String TEST_BASE = "/form/text";
    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";
    private static final String TEXTINPUT1_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text";
    private static final String TEXTINPUT2_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text_185087333";
    private static final String TEXTINPUT3_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/text-v2";


    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTAINING_PAGE);

    @BeforeClass
    public static void setUp() {
        CONTEXT.registerService(FormStructureHelperFactory.class, resource -> null);
        FormsHelperStubber.createStub();
    }

    @Test
    public void testDefaultInput() {
        Text text = getTextUnderTest(TEXTINPUT1_PATH);
        assertEquals("text", text.getName());
        assertEquals("Text input field", text.getTitle());
        assertEquals(false, text.isRequired());
        assertEquals("", text.getRequiredMessage());
        assertEquals(false, text.isReadOnly());
        assertEquals("text", text.getType());
        assertEquals("", text.getConstraintMessage());
        assertEquals("", text.getValue());
        assertEquals(2, text.getRows());
        assertEquals("", text.getHelpMessage());
        assertEquals(false, text.hideTitle());
    }

    @Test
    public void testInputWithCustomDataAndAttributes() {
        Text text = getTextUnderTest(TEXTINPUT2_PATH);
        assertEquals("Custom Name", text.getName());
        assertEquals("Custom title", text.getTitle());
        assertEquals(true, text.isRequired());
        assertEquals("please fill the field", text.getRequiredMessage());
        assertEquals(true, text.isReadOnly());
        assertEquals("email", text.getType());
        assertEquals("The value should be a valid email address", text.getConstraintMessage());
        assertEquals("Prefilled Sample Input", text.getValue());
        assertEquals(3, text.getRows());
        assertEquals("Custom help/placeholder message", text.getHelpMessage());
        assertEquals(true, text.hideTitle());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXTINPUT2_PATH));
    }

    @Test
    public void testV2JSONExport() {
        Text text = getTextUnderTest(TEXTINPUT3_PATH);
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(TEST_BASE, TEXTINPUT3_PATH));
    }

    private Text getTextUnderTest(String resourcePath) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        request.setResource(resource);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Text.class);
    }
}
