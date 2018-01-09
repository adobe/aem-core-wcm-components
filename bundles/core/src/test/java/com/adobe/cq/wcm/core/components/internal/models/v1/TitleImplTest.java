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
import com.adobe.cq.wcm.core.components.models.Title;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TitleImplTest {

    private static final String TEST_BASE = "/title";
    private static final String TEST_PAGE = "/content/title";
    private static final String TITLE_RESOURCE_JCR_TITLE = TEST_PAGE + "/jcr:content/par/title-jcr-title";
    private static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TEST_PAGE + "/jcr:content/par/title-jcr-title-type";
    private static final String TITLE_NOPROPS = TEST_PAGE + "/jcr:content/par/title-noprops";
    private static final String TITLE_WRONGTYPE = TEST_PAGE + "/jcr:content/par/title-wrongtype";
    private static final String TITLE_RESOURCE_JCR_TITLE_V2 = TEST_PAGE + "/jcr:content/par/title-jcr-title-v2";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, TEST_PAGE);

    @Test
    public void testExportedType() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertEquals(TitleImpl.RESOURCE_TYPE_V1, ((TitleImpl) title).getExportedType());
    }

    @Test
    public void testGetTitleFromResource() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertNull(title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE));
    }

    @Test
    public void testGetTitleFromResourceWithElementInfo() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_TYPE);
        assertEquals("Hello World", title.getText());
        assertEquals("h2", title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE_TYPE));
    }

    @Test
    public void testGetTitleResourcePageStyleType() {
        Style style = mock(Style.class);
        when(style.get(Title.PN_DESIGN_DEFAULT_TYPE, String.class)).thenReturn("h2");
        Title title = getTitleUnderTest(TITLE_NOPROPS, style);
        assertEquals("h2", title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_NOPROPS));
    }

    @Test
    public void testGetTitleFromCurrentPageWithWrongElementInfo() {
        Title title = getTitleUnderTest(TITLE_WRONGTYPE);
        assertNull(title.getType());
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_WRONGTYPE));
    }

    @Test
    public void testV2JSONExport() {
        Title title = getTitleUnderTest(TITLE_RESOURCE_JCR_TITLE_V2);
        Utils.testJSONExport(title, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE_V2));
    }

    private Title getTitleUnderTest(String resourcePath) {
        return getTitleUnderTest(resourcePath, null);
    }

    private Title getTitleUnderTest(String resourcePath, Style style) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, request);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        bindings.put(WCMBindings.CURRENT_PAGE, CONTEXT.pageManager().getPage(TEST_PAGE));
        if (style == null) {
            style = mock(Style.class);
        }
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        request.setResource(resource);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Title.class);
    }
}
