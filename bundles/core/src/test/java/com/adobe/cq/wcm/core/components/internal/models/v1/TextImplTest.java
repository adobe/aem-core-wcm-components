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
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Text;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;

public class TextImplTest {

    private static final String ROOT = "/content/text";

    @Rule
    public final AemContext context = CoreComponentTestContext.createContext("/text", ROOT);

    @Test
    public void testRichText() {
        Text text = getTestedText("rich-text");
        assertEquals("<p>rich</p>", text.getText());
        assertTrue(text.isRichText());
    }

    @Test
    public void testPlainText() {
        Text text = getTestedText("plain-text");
        assertEquals("plain", text.getText());
        assertFalse(text.isRichText());
    }

    @Test
    public void testEmptyText() {
        Text text = getTestedText("empty-text");
        assertNull(text.getText());
        assertFalse(text.isRichText());
    }

    private Text getTestedText(String path) {
        Resource resource = context.currentResource(ROOT + "/" + path);
        MockSlingHttpServletRequest request = context.request();
        SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        bindings.put(SlingBindings.RESOURCE, resource);
        return request.adaptTo(Text.class);
    }
}
