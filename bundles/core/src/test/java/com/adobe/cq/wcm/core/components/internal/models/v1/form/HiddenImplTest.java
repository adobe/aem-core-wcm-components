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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.models.form.Field;
import io.wcm.testing.mock.aem.junit.AemContext;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HiddenImplTest {

    private static final String TEST_BASE = "/form/hidden";
    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";
    private static final String HIDDENINPUT1_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_1";
    private static final String HIDDENINPUT2_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_2";
    private static final String HIDDENINPUT3_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_3";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content/we-retail/demo-page");

    @BeforeClass
    public static void setUp() {
        FormsHelperStubber.createStub();
        CONTEXT.registerService(FormStructureHelperFactory.class, resource -> null);
    }

    @Test
    public void testDefaultInput() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDENINPUT1_PATH);
        assertEquals("hidden", hiddenField.getName());
        assertEquals("", hiddenField.getValue());
        assertNull(hiddenField.getHelpMessage());
        assertEquals(HiddenImpl.ID_PREFIX, ((HiddenImpl) hiddenField).getIDPrefix());
        assertEquals(HiddenImpl.PROP_NAME_DEFAULT, ((HiddenImpl) hiddenField).getDefaultName());
        assertEquals(HiddenImpl.PROP_VALUE_DEFAULT, ((HiddenImpl) hiddenField).getDefaultValue());
        assertNull(((HiddenImpl) hiddenField).getDefaultTitle());
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDENINPUT1_PATH));
    }

    @Test
    public void testInputWithCustomData() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDENINPUT2_PATH);
        assertEquals("Custom_Name", hiddenField.getName());
        assertEquals("Custom value", hiddenField.getValue());
        assertEquals("hidden-field-id", hiddenField.getId());
        assertNull(hiddenField.getHelpMessage());
        assertEquals(HiddenImpl.ID_PREFIX, ((HiddenImpl) hiddenField).getIDPrefix());
        assertEquals(HiddenImpl.PROP_NAME_DEFAULT, ((HiddenImpl) hiddenField).getDefaultName());
        assertEquals(HiddenImpl.PROP_VALUE_DEFAULT, ((HiddenImpl) hiddenField).getDefaultValue());
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDENINPUT2_PATH));
    }

    @Test
    public void testExportedType() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDENINPUT1_PATH);
        assertEquals(FormConstants.RT_CORE_FORM_HIDDEN_V1, ((HiddenImpl) hiddenField).getExportedType());
    }

    @Test
    public void testV2JOSNExport() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDENINPUT3_PATH);
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDENINPUT3_PATH));
    }

    private Field prepareHiddenFieldForTest(String resourcePath) {
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        request.setResource(resource);
        Page currentPage = CONTEXT.pageManager().getPage(CONTAINING_PAGE);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Field.class);
    }

}
