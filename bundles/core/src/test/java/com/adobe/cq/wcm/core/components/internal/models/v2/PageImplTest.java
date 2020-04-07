/*******************************************************************************
 * Copyright 2017 Adobe
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
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.Utils.getTestExporterJSONPath;
import static com.adobe.cq.wcm.core.components.Utils.testJSONExport;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImplTest {

    private static final String TEST_BASE = "/page/v2";
    private static final String REDIRECT_PAGE = CONTENT_ROOT + "/redirect-page";
    private static final String PN_CLIENT_LIBS = "clientlibs";

    private static MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @BeforeEach
    protected void setUp() {
        internalSetup(TEST_BASE);
        ClientLibrary mockClientLibrary = Mockito.mock(ClientLibrary.class);
        when(mockClientLibrary.getPath()).thenReturn("/apps/wcm/core/page/clientlibs/favicon");
        when(mockClientLibrary.allowProxy()).thenReturn(true);
        context.registerInjectActivateService(new MockHtmlLibraryManager(mockClientLibrary));
        context.registerInjectActivateService(mockProductInfoProvider);
    }

    @Test
    void testPage() throws ParseException {
        Page page = getPageUnderTest(PAGE, DESIGN_PATH_KEY, DESIGN_PATH, PageImpl.PN_CLIENTLIBS_JS_HEAD,
                new String[]{"coretest.product-page-js-head"}, PN_CLIENT_LIBS,
                new String[]{"coretest.product-page","coretest.product-page-js-head"}, Page.PN_APP_RESOURCES_CLIENTLIB,
                "coretest.product-page.appResources",
                CSS_CLASS_NAMES_KEY, new String[]{"class1", "class2"});
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
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
        testJSONExport(page, getTestExporterJSONPath(TEST_BASE, PAGE));
    }

    @Test
    void testFavicons() {
        Page page = getPageUnderTest(PAGE);
            assertThrows(UnsupportedOperationException.class, () -> { page.getFavicons();
        });
    }

    @Test
    void testGetFaviconClientLibPath() throws Exception {
        Page page = getPageUnderTest(PAGE, Page.PN_APP_RESOURCES_CLIENTLIB,
                "coretest.product-page.appResources");
        String faviconClientLibPath = page.getAppResourcesPath();
        assertEquals(CONTEXT_PATH + "/etc.clientlibs/wcm/core/page/clientlibs/favicon/resources", faviconClientLibPath);
    }

    @Test
    void testRedirectTarget() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
    }

    @Test
    void testGetCssClasses() throws Exception {
        Page page = getPageUnderTest(PAGE, CSS_CLASS_NAMES_KEY, new String[]{"class1", "class2"});
        String cssClasses = page.getCssClassNames();
        assertEquals("The CSS classes of the page are not expected: " + PAGE, "class1 class2", cssClasses);
    }

    @Test
    void testHasCloudconfigSupport() {
        Page page = new PageImpl();
        assertFalse("Expected no cloudconfig support if product info provider missing", page.hasCloudconfigSupport());

        mockProductInfoProvider.setVersion(new Version("6.3.1"));
        page = getPageUnderTest(PAGE);
        assertFalse("Expected no cloudconfig support if product version < 6.4.0", page.hasCloudconfigSupport());

        // reset cached value
        Utils.setInternalState(page, "hasCloudconfigSupport", (Boolean)null);
        mockProductInfoProvider.setVersion(new Version("6.4.0"));
        assertTrue("Expected cloudconfig support if product version >= 6.4.0", page.hasCloudconfigSupport());
    }

//    private Page getPageUnderTest(String pagePath) {
//        return super.getPageUnderTest(Page.class, pagePath);
//    }
}
