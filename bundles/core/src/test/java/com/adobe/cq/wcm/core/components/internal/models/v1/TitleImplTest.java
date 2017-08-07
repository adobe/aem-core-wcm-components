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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.adobe.cq.wcm.core.components.models.Title;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class TitleImplTest {

    private static final String TEST_CONTENT_ROOT = "/content/title";
    private static final String TITLE_RESOURCE_JCR_TITLE = TEST_CONTENT_ROOT + "/jcr:content/par/title-jcr-title";
    private static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TEST_CONTENT_ROOT + "/jcr:content/par/title-jcr-title-type";
    private static final String TITLE_NOPROPS = TEST_CONTENT_ROOT + "/jcr:content/par/title-noprops";
    private static final String TITLE_WRONGTYPE = TEST_CONTENT_ROOT + "/jcr:content/par/title-wrongtype";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/title", TEST_CONTENT_ROOT);

    private Title underTest;
    private SlingBindings slingBindings;

    @Before
    public void setUp() throws Exception {
        Page page = context.currentPage(TEST_CONTENT_ROOT);
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
    }

    @Test
    public void testGetTitleFromResource() {
        Resource resource = context.currentResource(TITLE_RESOURCE_JCR_TITLE);
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Title.class);
        assertEquals("Hello World", underTest.getText());
        assertNull(underTest.getType());
    }

    @Test
    public void testGetTitleFromResourceWithElementInfo() {
        Resource resource = context.currentResource(TITLE_RESOURCE_JCR_TITLE_TYPE);
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Title.class);
        assertEquals("Hello World", underTest.getText());
        assertEquals("h2", underTest.getType());
    }

    @Test
    public void testGetTitleResourcePageStyleType() {
        context.currentResource(TITLE_NOPROPS);
        Style style = Mockito.mock(Style.class);
        when(style.get(Title.PN_DESIGN_DEFAULT_TYPE, String.class)).thenReturn("h2");
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        underTest = context.request().adaptTo(Title.class);
        assertEquals("h2", underTest.getType());
    }

    @Test
    public void testGetTitleFromCurrentPageWithWrongElementInfo() {
        Resource resource = context.currentResource(TITLE_WRONGTYPE);
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Title.class);
        assertNull(underTest.getType());
    }
}
