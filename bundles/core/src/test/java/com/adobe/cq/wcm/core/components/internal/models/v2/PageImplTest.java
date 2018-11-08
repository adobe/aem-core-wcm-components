/*******************************************************************************
 * Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.factory.ModelFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.export.json.hierarchy.HierarchyNodeExporter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImplTest {

    private static final String TEST_BASE = "/page/v2";
    private static final String REDIRECT_PAGE = ROOT + "/redirect-page";

    private static final String PAGE_CHILD = PAGE + "/child";

    private static ClientLibrary mockClientLibrary;

    private static MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @BeforeClass
    public static void setUp() {

        internalSetUp(CONTEXT, TEST_BASE, ROOT);
        mockClientLibrary = Mockito.mock(ClientLibrary.class);

        when(mockClientLibrary.getPath()).thenReturn("/apps/wcm/core/page/clientlibs/favicon");
        when(mockClientLibrary.allowProxy()).thenReturn(true);
        CONTEXT.registerInjectActivateService(new MockHtmlLibraryManager(mockClientLibrary));
        CONTEXT.registerInjectActivateService(mockProductInfoProvider);
    }

    @Test
    public void testPage() throws ParseException {
        Page page = getPageUnderTest(PAGE);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        calendar.setTime(sdf.parse("2016-01-20T10:33:36.000+0100"));
        assertEquals(page.getLastModifiedDate().getTime(), calendar.getTime());
        assertEquals("en-GB", page.getLanguage());
        assertEquals("Templated Page", page.getTitle());
        assertEquals(DESIGN_PATH, page.getDesignPath());
        assertNull(page.getStaticDesignPath());
        String[] keywordsArray = page.getKeywords();
        assertEquals(3, keywordsArray.length);
        Set<String> keywords = new HashSet<>(keywordsArray.length);
        keywords.addAll(Arrays.asList(keywordsArray));
        assertTrue(keywords.contains("one") && keywords.contains("two") && keywords.contains("three"));
        assertArrayEquals(new String[] {"coretest.product-page", "coretest.product-page-js-head"}, page.getClientLibCategories());
        assertArrayEquals(new String[] {"coretest.product-page-js-head"}, page.getClientLibCategoriesJsHead());
        assertArrayEquals(new String[] {"coretest.product-page"}, page.getClientLibCategoriesJsBody());
        assertEquals("product-page", page.getTemplateName());
        Utils.testJSONExport(page, Utils.getTestExporterJSONPath(TEST_BASE, PAGE));
    }

    @Test
    public void testChildren() {
        Page page = getPageUnderTest(PAGE);
        ModelFactory modelFactory = Mockito.mock(ModelFactory.class);
        Whitebox.setInternalState(page, "modelFactory", modelFactory);

        Page childPage = getPageUnderTest(PAGE_CHILD);
        when(modelFactory.getModelFromWrappedRequest(any(SlingHttpServletRequest.class), any(Resource.class), eq(Page.class))).thenReturn(childPage);

        Map<String, ? extends HierarchyNodeExporter> children = page.getExportedChildren();
        Assert.assertEquals(2, children.size());
        Assert.assertEquals(childPage, children.get(PAGE_CHILD));

    }
    @Test
    public void testGetRootUrl() {
        // no parent
        Page rootPage = getPageUnderTest(PAGE);
        Assert.assertEquals(CONTEXT_PATH + PAGE + ".model.json", rootPage.getRootUrl());

        // with parent
        Page childPage = getPageUnderTest(PAGE_CHILD);
        Assert.assertEquals(CONTEXT_PATH + PAGE + ".model.json", childPage.getRootUrl());
    }

    @Test
    public void testGetRootModel() {
        // no parent
        Page page = getPageUnderTest(PAGE);
        Assert.assertEquals(page, page.getRootModel());
    }

    @Test(expected = UnsupportedOperationException.class)
    @Override
    public void testFavicons() {
        Page page = getPageUnderTest(PAGE);
        page.getFavicons();
    }

    @Test
    public void testGetFaviconClientLibPath() throws Exception {
        Page page = getPageUnderTest(PAGE);
        String faviconClientLibPath = page.getAppResourcesPath();
        assertEquals(CONTEXT_PATH + "/etc.clientlibs/wcm/core/page/clientlibs/favicon/resources", faviconClientLibPath);
    }

    @Test
    public void testRedirectTarget() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
    }

    @Test
    public void testGetCssClasses() throws Exception {
        Page page = getPageUnderTest(PAGE);
        String cssClasses = page.getCssClassNames();
        assertEquals("The CSS classes of the page are not expected: " + PAGE, "class1 class2", cssClasses);
    }

    @Test
    public void testHasCloudconfigSupport() {
        Page page = new PageImpl();
        assertFalse("Expected no cloudconfig support if product info provider missing", page.hasCloudconfigSupport());

        mockProductInfoProvider.setVersion(new Version("6.3.1"));
        page = getPageUnderTest(PAGE);
        assertFalse("Expected no cloudconfig support if product version < 6.4.0", page.hasCloudconfigSupport());

        // reset cached value
        Whitebox.setInternalState(page, "hasCloudconfigSupport", (Boolean)null);
        mockProductInfoProvider.setVersion(new Version("6.4.0"));
        assertTrue("Expected cloudconfig support if product version >= 6.4.0", page.hasCloudconfigSupport());
    }

    private Page getPageUnderTest(String pagePath) {
        return super.getPageUnderTest(Page.class, pagePath);
    }
}
