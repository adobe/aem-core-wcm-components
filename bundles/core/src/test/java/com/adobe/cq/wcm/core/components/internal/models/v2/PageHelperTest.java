/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class PageHelperTest {

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext();

    @Test
    public void testGetModelUrl() {
        assertNull(PageHelpers.getModelUrl(""));
        assertEquals("some/path.model.json", PageHelpers.getModelUrl("some/path"));
        assertEquals("path/with/some.model.json", PageHelpers.getModelUrl("path/with/some.more.selectors.html"));
    }

    @Test
    public void testGetHierarchyServletRequest() {
        MockSlingHttpServletRequest mockedRequest = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        Page mockedPage = mock(Page.class);

        SlingHttpServletRequest hierarchyServletRequest = PageHelpers.getHierarchyServletRequest(mockedRequest, mockedPage);

        assertEquals(mockedPage, hierarchyServletRequest.getAttribute("currentPage"));
        assertNotNull(hierarchyServletRequest.getAttribute("com.day.cq.wcm.componentcontext"));
    }

    @Test
    public void testGetPageTreeTraversalDepth() {
        // no style
        assertEquals(0, PageHelpers.getPageTreeTraversalDepth(null));

        // structureDepth set to 42
        Style mockedStyle = mock(Style.class);
        int answer = 42;
        when(mockedStyle.get(eq("structureDepth"), any())).thenReturn(answer);

        assertEquals(answer, PageHelpers.getPageTreeTraversalDepth(mockedStyle));
    }

    @Test
    public void testGetStructurePatterns() {
        final String PN_STRUCTURE_PATTERNS = "structurePatterns";

        SlingHttpServletRequest mockedRequest = mock(SlingHttpServletRequest.class);
        RequestParameter mockedRequestParameter = mock(RequestParameter.class);
        when(mockedRequest.getRequestParameter(eq(PN_STRUCTURE_PATTERNS.toLowerCase()))).thenReturn(mockedRequestParameter);

        // expect two elements
        when(mockedRequestParameter.getString()).thenReturn("first,second");

        List<Pattern> patterns = PageHelpers.getStructurePatterns(mockedRequest, null);
        assertEquals("first", patterns.get(0).pattern());
        assertEquals("second", patterns.get(1).pattern());

        // expect empty
        when(mockedRequestParameter.getString()).thenReturn("");

        patterns = PageHelpers.getStructurePatterns(mockedRequest, null);
        assertTrue(patterns.isEmpty());
    }
}
