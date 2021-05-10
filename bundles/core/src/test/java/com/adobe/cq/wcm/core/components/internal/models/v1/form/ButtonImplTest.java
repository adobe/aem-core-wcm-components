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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.Button;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class ButtonImplTest {

    private static final String TEST_BASE = "/form/button";
    private static final String CONTENT_ROOT = "/content/buttons";
    private static final String EMPTY_BUTTON_PATH = CONTENT_ROOT + "/button";
    private static final String BUTTON1_PATH = CONTENT_ROOT + "/button1";
    private static final String BUTTON2_PATH = CONTENT_ROOT + "/button2";
    private static final String ID_PREFIX = "form-button";
    private static final RootResourceBundle RESOURCE_BUNDLE = new RootResourceBundle();

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);

        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        Mockito.when(resourceBundleProvider.getResourceBundle(null)).thenReturn(RESOURCE_BUNDLE);
        Mockito.when(resourceBundleProvider.getResourceBundle(null, null)).thenReturn(RESOURCE_BUNDLE);
    }

    @Test
    public void testEmptyButton() {
        Button button = getButtonUnderTest(EMPTY_BUTTON_PATH);
        assertEquals(Button.Type.SUBMIT, button.getType());
        assertEquals("Submit", button.getTitle());
        assertEquals("", button.getName());
        assertEquals("", button.getValue());
        assertNull(button.getHelpMessage());
        String id = ID_PREFIX + "-" + Math.abs(EMPTY_BUTTON_PATH.hashCode() - 1);
        assertEquals(id, button.getId());
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, EMPTY_BUTTON_PATH));
    }

    @Test
    public void testButton() {
        Button button = getButtonUnderTest(BUTTON1_PATH);
        assertEquals(Button.Type.BUTTON, button.getType());
        assertEquals("button title", button.getTitle());
        assertEquals("name1", button.getName());
        assertEquals("value1", button.getValue());
        assertEquals("button-id", button.getId());
        assertNull(button.getHelpMessage());
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, BUTTON1_PATH));
    }

    @Test
    public void testV2JSONExport() {
        Button button = getButtonUnderTest(BUTTON2_PATH);
        Utils.testJSONExport(button, Utils.getTestExporterJSONPath(TEST_BASE, BUTTON2_PATH));
    }


    private Button getButtonUnderTest(String resourcePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext());
        request.setResource(resource);
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Button.class);
    }

}
