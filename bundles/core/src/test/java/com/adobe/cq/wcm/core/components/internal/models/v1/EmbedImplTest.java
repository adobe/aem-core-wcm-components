/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Embed;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
class EmbedImplTest {

    private static final String BASE = "/embed";
    private static final String CONTENT_ROOT = "/content";
    private static final String ROOT_PAGE = "/content/embed";
    private static final String GRID = ROOT_PAGE + "/jcr:content/root/responsivegrid";
    private static final String EMBED_1 = "/embed1";
    private static final String EMBED_2 = "/embed2";
    private static final String EMBED_3 = "/embed3";
    private static final String PATH_EMBED_1 = GRID + EMBED_1;
    private static final String PATH_EMBED_2 = GRID + EMBED_2;
    private static final String PATH_EMBED_3 = GRID + EMBED_3;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private Style style;

    @BeforeEach
    void setUp() {
        context.load().json(BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
    }

    @Test
    void testUrl() {
        style = mock(Style.class);
        Mockito.when(style.get(Embed.PN_DESIGN_URL_DISABLED, false)).thenReturn(false);
        Mockito.when(style.get(Embed.PN_DESIGN_HTML_DISABLED, false)).thenReturn(true);
        Mockito.when(style.get(Embed.PN_DESIGN_EMBEDDABLES_DISABLED, false)).thenReturn(true);
        Embed embed = getEmbedUnderTest(PATH_EMBED_1);
        assertEquals(Embed.Type.URL, embed.getType());
        assertNull(embed.getHtml());
        assertNull(embed.getEmbeddableResourceType());
        assertEquals("https://www.youtube.com/embed/vpdcMZnYCko", embed.getUrl());
        Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_1));
    }

    @Test
    void testEmbeddable() {
        style = mock(Style.class);
        Mockito.when(style.get(Embed.PN_DESIGN_URL_DISABLED, false)).thenReturn(true);
        Mockito.when(style.get(Embed.PN_DESIGN_HTML_DISABLED, false)).thenReturn(true);
        Mockito.when(style.get(Embed.PN_DESIGN_EMBEDDABLES_DISABLED, false)).thenReturn(false);
        Embed embed = getEmbedUnderTest(PATH_EMBED_2);
        assertEquals(Embed.Type.EMBEDDABLE, embed.getType());
        assertNull(embed.getUrl());
        assertNull(embed.getHtml());
        assertEquals("core/wcm/components/embed/v1/embed/embeddable/youtube", embed.getEmbeddableResourceType());
        Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_2));
    }

    @Test
    void testHtml() {
        style = mock(Style.class);
        Mockito.when(style.get(Embed.PN_DESIGN_URL_DISABLED, false)).thenReturn(true);
        Mockito.when(style.get(Embed.PN_DESIGN_HTML_DISABLED, false)).thenReturn(false);
        Mockito.when(style.get(Embed.PN_DESIGN_EMBEDDABLES_DISABLED, false)).thenReturn(true);
        Embed embed = getEmbedUnderTest(PATH_EMBED_3);
        assertEquals(Embed.Type.HTML, embed.getType());
        assertNull(embed.getUrl());
        assertNull(embed.getEmbeddableResourceType());
        assertEquals("<div>html</div>", embed.getHtml());
        Utils.testJSONExport(embed, Utils.getTestExporterJSONPath(BASE, EMBED_3));
    }

    private Embed getEmbedUnderTest(String resourcePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(),
            context.bundleContext());
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        request.setResource(resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Embed.class);
    }
}
