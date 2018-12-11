/*******************************************************************************
 * Copyright 2018 Adobe Systems Incorporated
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.export.json.hierarchy.HierarchyNodeExporter;
import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.wcm.api.designer.Style;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImplTest {

    private static final String TEST_BASE = "/page/v3";

    private static final String PAGE_CHILD = PAGE + "/child";

    @BeforeClass
    public static void setUp() {
        internalSetUpV2(TEST_BASE);
    }

    @Test
    public void testPage() throws ParseException {
        Page page = getPageWithChildren();
        assertPage(page);
    }

    @Test
    public void testChildren() {
        Page childPage = getChildPage();
        Page page = getPageWithThisChild(childPage);

        Map<String, ? extends HierarchyNodeExporter> children = page.getExportedChildren();
        Assert.assertEquals(2, children.size());
        Assert.assertEquals(childPage, children.get(PAGE_CHILD));
    }

    @Test
    public void testGetRootUrl() {
        // no parent
        Page rootPage = getPageWithChildren();
        Assert.assertEquals(CONTEXT_PATH + PAGE + ".model.json", rootPage.getRootUrl());

        // with parent
        Page childPage = getChildPage();
        Assert.assertEquals(CONTEXT_PATH + PAGE + ".model.json", childPage.getRootUrl());
    }

    @Test
    public void testGetRootModel() {
        // no parent
        Page page = getPageWithChildren();
        Assert.assertEquals(page, page.getRootModel());
    }

    @Test
    public void testHelperGetModelUrl() {
        assertNull(PageImpl.getModelUrl(""));
        assertEquals("some/path.model.json", PageImpl.getModelUrl("some/path"));
        assertEquals("path/with/some.model.json", PageImpl.getModelUrl("path/with/some.more.selectors.html"));
    }

    @Test
    public void testHelperGetHierarchyServletRequest() {
        MockSlingHttpServletRequest mockedRequest = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        com.day.cq.wcm.api.Page mockedPage = mock(com.day.cq.wcm.api.Page.class);

        SlingHttpServletRequest hierarchyServletRequest = PageImpl.getHierarchyServletRequest(mockedRequest, mockedPage, mockedPage);

        assertEquals(mockedPage, hierarchyServletRequest.getAttribute("currentPage"));
        assertNotNull(hierarchyServletRequest.getAttribute("com.day.cq.wcm.componentcontext"));
        assertEquals(mockedPage, hierarchyServletRequest.getAttribute("com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.entryPointPage"));
    }

    @Test
    public void testGetHelperPageTreeTraversalDepth() {
        // no style
        assertEquals(0, PageImpl.getPageTreeTraversalDepth(null));

        // structureDepth set to 42
        Style mockedStyle = mock(Style.class);
        int answer = 42;
        when(mockedStyle.get(eq("structureDepth"), any())).thenReturn(answer);

        assertEquals(answer, PageImpl.getPageTreeTraversalDepth(mockedStyle));
    }

    @Test
    public void testHelperGetStructurePatterns() {
        final String PN_STRUCTURE_PATTERNS = "structurePatterns";

        SlingHttpServletRequest mockedRequest = mock(SlingHttpServletRequest.class);
        RequestParameter mockedRequestParameter = mock(RequestParameter.class);
        when(mockedRequest.getRequestParameter(eq(PN_STRUCTURE_PATTERNS.toLowerCase()))).thenReturn(mockedRequestParameter);

        // expect two elements
        when(mockedRequestParameter.getString()).thenReturn("first,second");

        List<Pattern> patterns = PageImpl.getStructurePatterns(mockedRequest, null);
        assertEquals("first", patterns.get(0).pattern());
        assertEquals("second", patterns.get(1).pattern());

        // expect empty
        when(mockedRequestParameter.getString()).thenReturn("");

        patterns = PageImpl.getStructurePatterns(mockedRequest, null);
        assertTrue(patterns.isEmpty());
    }

    private Page getPageWithChildren() {
        return getPageWithThisChild(getChildPage());
    }

    private Page getPageWithThisChild(Page child) {
        Page page = super.getPageUnderTest(Page.class, PAGE);

        ModelFactory modelFactory = Mockito.spy(CONTEXT.getService(ModelFactory.class));
        Whitebox.setInternalState(page, "modelFactory", modelFactory);

        Mockito.doReturn(child)
            .when(modelFactory)
            .getModelFromWrappedRequest(any(), any(), eq(Page.class));

        return page;
    }

    private Page getChildPage() {
        return super.getPageUnderTest(Page.class, PAGE_CHILD);
    }

    @Override
    protected String getTestBase() {
        return TEST_BASE;
    }
}
