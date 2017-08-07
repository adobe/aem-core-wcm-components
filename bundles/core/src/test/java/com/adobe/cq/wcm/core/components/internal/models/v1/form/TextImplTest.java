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

import com.adobe.cq.wcm.core.components.models.form.Text;
import com.day.cq.wcm.foundation.forms.FormStructureHelper;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit.AemContext;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TextImplTest {

    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";

    private static final String TEXTINPUT1_PATH = CONTAINING_PAGE+"/jcr:content/root/responsivegrid/container/text";

    private static final String TEXTINPUT2_PATH = CONTAINING_PAGE+"/jcr:content/root/responsivegrid/container/text_185087333";

    private static final String RESOURCE_PROPERTY = "resource";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/form/text", "/content/we-retail/demo-page");

    private SlingBindings slingBindings;

    @Before
    public void setUp() {
        Page page = context.currentPage(CONTAINING_PAGE);
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
        context.registerService(FormStructureHelperFactory.class, new FormStructureHelperFactory() {
            @Override
            public FormStructureHelper getFormStructureHelper(Resource formElement) {
                return null;
            }
        });
        FormsHelperStubber.createStub();
    }

    @Test
    public void testDefaultInput(){
        Resource resource = context.currentResource(TEXTINPUT1_PATH);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, resource);
        Text text = context.request().adaptTo(Text.class);
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
        Resource resource = context.currentResource(TEXTINPUT2_PATH);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(RESOURCE_PROPERTY, resource);
        Text text = context.request().adaptTo(Text.class);
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
    }
}
