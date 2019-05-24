/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import io.wcm.testing.mock.aem.junit.AemContext;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.EmbedConstants;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.day.cq.wcm.api.designer.Style;

public class EmbedImplTest {

    private static final String BASE = "/embed/v1";
    private static final String ROOT = "/content/page/component";
    private static final String EMBED_TYPE_1 = ROOT + "/embeddable";
    private static final String EMBED_TYPE_2 = ROOT + "/html";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(BASE, ROOT);

    Style style;

    @Test
    public void testHTML() {
	style = mock(Style.class);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_HTML_MODE, false)).thenReturn(true);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_SELECTION_MODE, false)).thenReturn(false);
	Embed embed = getEmbedUnderTest(Embed.class, EMBED_TYPE_2);
	assertEquals("html", embed.getEmbedMode());
	assertNull(embed.getEmbedType());
	assertEquals("<h2>hello html</h2>", embed.getMarkup());
	Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_TYPE_2));
    }
    
    @Test
    public void testHTMLWhenDualMode() {
	style = mock(Style.class);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_HTML_MODE, false)).thenReturn(true);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_SELECTION_MODE, false)).thenReturn(true);
	Embed embed = getEmbedUnderTest(Embed.class, EMBED_TYPE_2);
	assertEquals("html", embed.getEmbedMode());
	assertNull(embed.getEmbedType());
	assertEquals("<h2>hello html</h2>", embed.getMarkup());
	Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_TYPE_2));
    }

    @Test
    public void testEmbeddable() {
	style = mock(Style.class);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_SELECTION_MODE, false)).thenReturn(true);
	Mockito.when(style.get(EmbedConstants.PN_ENABLE_HTML_MODE, false)).thenReturn(false);
	Embed embed = getEmbedUnderTest(Embed.class, EMBED_TYPE_1);
	assertEquals("embeddable", embed.getEmbedMode());
	assertEquals("core/wcm/components/embed/embeddables/youtube", embed.getEmbedType());
	assertNull(embed.getMarkup());
	Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_TYPE_1));
    }

    protected <T> T getEmbedUnderTest(Class<T> model, String resourcePath) {
	Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
	if (resource == null) {
	    throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
	}
	MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(),
		CONTEXT.bundleContext());
	SlingBindings bindings = new SlingBindings();
	bindings.put(SlingBindings.RESOURCE, resource);
	bindings.put(SlingBindings.REQUEST, request);
	bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
	bindings.put(WCMBindings.CURRENT_STYLE, style);
	request.setResource(resource);
	request.setAttribute(SlingBindings.class.getName(), bindings);
	return request.adaptTo(model);
    }

}
