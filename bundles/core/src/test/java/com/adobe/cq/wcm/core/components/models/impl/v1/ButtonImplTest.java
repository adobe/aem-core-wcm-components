/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.models.impl.v1;

import io.wcm.testing.mock.aem.junit.AemContext;

import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.form.Button;

import static org.junit.Assert.assertEquals;

public class ButtonImplTest {

    private static String ROOT_PATH = "/content/buttons";

    private static String EMPTY_BUTTON_PATH = ROOT_PATH + "/button";

    private static String BUTTON1_PATH = ROOT_PATH + "/button1";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/form/button", "/content/buttons");

    private RootResourceBundle rootResourceBundle = new RootResourceBundle();

    @Before
    public void setUp() {
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        Mockito.when(resourceBundleProvider.getResourceBundle(null)).thenReturn(rootResourceBundle);
        Mockito.when(resourceBundleProvider.getResourceBundle(null, null)).thenReturn(rootResourceBundle);
    }

    /**
     * Tests an empty button.
     * <p>
     * Note: the test button is created by the {@link SlingContext}.
     * </p>
     */
    @Test
    public void testEmptyButton() throws Exception {
        context.currentResource(EMPTY_BUTTON_PATH);
        Button button = context.request().adaptTo(Button.class);
        assertEquals(Button.Type.SUBMIT, button.getType());
        assertEquals("Submit", button.getTitle());
        assertEquals("", button.getName());
        assertEquals("", button.getValue());
    }

    /**
     * Tests a fully configured button.
     * <p>
     * Note: the test button is loaded from a JSON file by the {@link SlingContext}.
     * </p>
     */
    @Test
    public void testJsonButton() throws Exception {
        context.currentResource(BUTTON1_PATH);
        Button button = context.request().adaptTo(Button.class);
        assertEquals(Button.Type.BUTTON, button.getType());
        assertEquals("button title", button.getTitle());
        assertEquals("name1", button.getName());
        assertEquals("value1", button.getValue());
    }

}
