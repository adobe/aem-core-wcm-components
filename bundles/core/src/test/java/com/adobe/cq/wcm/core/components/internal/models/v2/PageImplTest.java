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
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Version;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImplTest {

    private static final String TEST_BASE = "/page/v2";
    private static final String REDIRECT_PAGE = ROOT + "/redirect-page";

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
        assertEquals(page.getLastModifiedDate(), calendar);
        assertEquals("en-GB", page.getLanguage());
        assertEquals("Templated Page", page.getTitle());
        assertEquals(DESIGN_PATH, page.getDesignPath());
        assertNull(page.getStaticDesignPath());
        String[] keywordsArray = page.getKeywords();
        assertEquals(3, keywordsArray.length);
        Set<String> keywords = new HashSet<>(keywordsArray.length);
        keywords.addAll(Arrays.asList(keywordsArray));
        assertTrue(keywords.contains("one") && keywords.contains("two") && keywords.contains("three"));
        assertEquals("coretest.product-page", page.getClientLibCategories()[0]);
        assertEquals("product-page", page.getTemplateName());
        Utils.testJSONExport(page, Utils.getTestExporterJSONPath(TEST_BASE, PAGE));
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
