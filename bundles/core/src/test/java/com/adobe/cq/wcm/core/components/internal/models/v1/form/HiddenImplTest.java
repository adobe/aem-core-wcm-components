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

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.cq.wcm.core.components.models.form.Field;
import com.day.cq.wcm.foundation.forms.FormStructureHelperFactory;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class HiddenImplTest {

    private static final String TEST_BASE = "/form/hidden";
    private static final String CONTAINING_PAGE = "/content/we-retail/demo-page";
    private static final String HIDDEN_INPUT1_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_1";
    private static final String HIDDEN_INPUT2_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_2";
    private static final String HIDDEN_INPUT3_PATH = CONTAINING_PAGE + "/jcr:content/root/responsivegrid/container/hidden_3";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTAINING_PAGE);
        FormsHelperStubber.createStub();
        context.registerService(FormStructureHelperFactory.class, resource -> null);
    }

    @Test
    void testDefaultInput() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDEN_INPUT1_PATH);
        assertEquals("hidden", hiddenField.getName());
        assertEquals("", hiddenField.getValue());
        assertNull(hiddenField.getHelpMessage());
        assertEquals(HiddenImpl.ID_PREFIX, ((HiddenImpl) hiddenField).getIDPrefix());
        assertEquals(HiddenImpl.PROP_NAME_DEFAULT, ((HiddenImpl) hiddenField).getDefaultName());
        assertEquals(HiddenImpl.PROP_VALUE_DEFAULT, ((HiddenImpl) hiddenField).getDefaultValue());
        assertNull(((HiddenImpl) hiddenField).getDefaultTitle());
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDEN_INPUT1_PATH));
    }

    @Test
    void testInputWithCustomData() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDEN_INPUT2_PATH);
        assertEquals("Custom_Name", hiddenField.getName());
        assertEquals("Custom value", hiddenField.getValue());
        assertEquals("hidden-field-id", hiddenField.getId());
        assertNull(hiddenField.getHelpMessage());
        assertEquals(HiddenImpl.ID_PREFIX, ((HiddenImpl) hiddenField).getIDPrefix());
        assertEquals(HiddenImpl.PROP_NAME_DEFAULT, ((HiddenImpl) hiddenField).getDefaultName());
        assertEquals(HiddenImpl.PROP_VALUE_DEFAULT, ((HiddenImpl) hiddenField).getDefaultValue());
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDEN_INPUT2_PATH));
    }

    @Test
    void testExportedType() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDEN_INPUT1_PATH);
        assertEquals(FormConstants.RT_CORE_FORM_HIDDEN_V1, (hiddenField).getExportedType());
    }

    @Test
    void testV2JOSNExport() {
        Field hiddenField = prepareHiddenFieldForTest(HIDDEN_INPUT3_PATH);
        Utils.testJSONExport(hiddenField, Utils.getTestExporterJSONPath(TEST_BASE, HIDDEN_INPUT3_PATH));
    }

    private Field prepareHiddenFieldForTest(String resourcePath) {
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Field.class);
    }

}
