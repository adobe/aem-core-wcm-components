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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Text;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;

public class TextImplTest {

    protected static final String ROOT = "/content/text";
    private static final String TEST_BASE = "/text";
    protected static final String TEXT_1 = ROOT + "/rich-text";
    protected static final String TEXT_2 = ROOT + "/plain-text";
    protected static final String TEXT_3 = ROOT + "/empty-text";
    protected static final String TEXT_4 = ROOT + "/rich-text-v2";

    protected static String getTestBase() {
        return TEST_BASE;
    }

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(getTestBase(), ROOT);

    @Test
    public void testRichText() {
        Text text = getTextUnderTest(Text.class, TEXT_1);
        assertEquals("<p>rich</p>", text.getText());
        assertTrue(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(getTestBase(), TEXT_1));
    }

    @Test
    public void testPlainText() {
        Text text = getTextUnderTest(Text.class, TEXT_2);
        assertEquals("plain", text.getText());
        assertFalse(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(getTestBase(), TEXT_2));
    }

    @Test
    public void testEmptyText() {
        Text text = getTextUnderTest(Text.class, TEXT_3);
        assertNull(text.getText());
        assertFalse(text.isRichText());
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(getTestBase(), TEXT_3));
    }

    @Test
    public void testExportedType() {
        Text text = getTextUnderTest(Text.class, TEXT_1);
        assertEquals("core/wcm/components/text/v1/text", ((TextImpl) text).getExportedType());
    }

    @Test
    public void testV2JSONExport() {
        Text text = getTextUnderTest(Text.class, TEXT_4);
        Utils.testJSONExport(text, Utils.getTestExporterJSONPath(getTestBase(), TEXT_4));
    }

    protected <T> T getTextUnderTest(Class<T> model, String resourcePath) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        request.setResource(resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(model);
    }
}
