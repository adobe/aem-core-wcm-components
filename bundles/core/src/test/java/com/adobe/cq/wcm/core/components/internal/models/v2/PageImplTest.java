/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import com.adobe.aem.wcm.seo.impl.sitemap.SitemapLinkExternalizerImpl;
import com.adobe.aem.wcm.seo.sitemap.PageTreeSitemapGenerator;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.components.testing.MockPersistenceStrategy;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.adobe.cq.wcm.core.components.testing.MockSitemapLinkExternalizer;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.day.cq.wcm.api.PageManagerFactory;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.Utils.getTestExporterJSONPath;
import static com.adobe.cq.wcm.core.components.Utils.testJSONExport;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImplTest {

    private static final String TEST_BASE = "/page/v2";
    protected static final String REDIRECT_PAGE = CONTENT_ROOT + "/redirect-page";
    private static final String PN_CLIENT_LIBS = "clientlibs";
    private static final String SLING_CONFIGS_ROOT = "/conf/page/sling:configs";

    private static final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    private PageTreeSitemapGenerator pageTreeSitemapGenerator;

    @BeforeEach
    @Override
    protected void setUp() {
        MockContextAwareConfig.registerAnnotationClasses(context, HtmlPageItemsConfig.class);
        testBase = TEST_BASE;
        internalSetup();
    }

    @Override
    protected void internalSetup() {
        super.internalSetup();
        ClientLibrary mockClientLibrary = Mockito.mock(ClientLibrary.class);
        when(mockClientLibrary.getPath()).thenReturn("/apps/wcm/core/page/clientlibs/favicon");
        when(mockClientLibrary.allowProxy()).thenReturn(true);
        context.registerInjectActivateService(new MockHtmlLibraryManager(mockClientLibrary));
        context.registerInjectActivateService(mockProductInfoProvider);
        context.registerInjectActivateService(new MockPersistenceStrategy(), ImmutableMap.of(Constants.SERVICE_RANKING, Integer.MAX_VALUE));

        pageTreeSitemapGenerator = Mockito.mock(PageTreeSitemapGenerator.class);
        context.registerService(PageTreeSitemapGenerator.class, pageTreeSitemapGenerator);
        context.registerInjectActivateService(new MockSitemapLinkExternalizer());
    }

    private void loadHtmlPageItemsConfig(boolean useNewFormat) {
        if (useNewFormat) {
            context.load().json(TEST_BASE + "/test-sling-configs.json", SLING_CONFIGS_ROOT);
        } else {
            context.load().json(TEST_BASE + "/test-sling-configs-deprecated-caconfig.json", SLING_CONFIGS_ROOT);
        }
    }

    @Test
    @Override
    protected void testPage() throws ParseException {
        testPage(true);
    }

    @Test
    void testPageWithDeprecatedCaconfig() throws Exception {
        testPage(false);
    }

    protected void testPage(boolean useNewCaconfig) throws ParseException {
        loadHtmlPageItemsConfig(useNewCaconfig);
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
        assertArrayEquals(new String[] {"" +
                "coretest.product-page-js-head"}, page.getClientLibCategoriesJsHead());
        assertArrayEquals(new String[] {"coretest.product-page"}, page.getClientLibCategoriesJsBody());
        assertEquals("product-page", page.getTemplateName());
        testJSONExport(page, getTestExporterJSONPath(testBase, PAGE));
    }

    @Test
    protected void testFavicons() {
        Page page = getPageUnderTest(PAGE);
        loadHtmlPageItemsConfig(true);
        assertThrows(UnsupportedOperationException.class, page::getFavicons);
    }

    @Test
    protected void testGetFaviconClientLibPath() {
        Page page = getPageUnderTest(PAGE, Page.PN_APP_RESOURCES_CLIENTLIB,
                "coretest.product-page.appResources");
        String faviconClientLibPath = page.getAppResourcesPath();
        assertEquals(CONTEXT_PATH + "/etc.clientlibs/wcm/core/page/clientlibs/favicon/resources", faviconClientLibPath);
    }

    @Test
    @SuppressWarnings("deprecation")
    protected void testRedirectTarget() {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        loadHtmlPageItemsConfig(true);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
    }

    @Test
    protected void testGetCssClasses() {
        Page page = getPageUnderTest(PAGE, CSS_CLASS_NAMES_KEY, new String[]{"class1", "class2"});
        loadHtmlPageItemsConfig(true);
        String cssClasses = page.getCssClassNames();
        assertEquals("class1 class2", cssClasses, "The CSS classes of the page are not expected: " + PAGE);
    }

    @Test
    protected void testHasCloudconfigSupport() {
        Page page = new PageImpl();
        loadHtmlPageItemsConfig(true);
        assertFalse(page.hasCloudconfigSupport(), "Expected no cloudconfig support if product info provider missing");

        mockProductInfoProvider.setVersion(new Version("6.3.1"));
        page = getPageUnderTest(PAGE);
        assertFalse(page.hasCloudconfigSupport(), "Expected no cloudconfig support if product version < 6.4.0");

        // reset cached value
        Utils.setInternalState(page, "hasCloudconfigSupport", (Boolean)null);
        mockProductInfoProvider.setVersion(new Version("6.4.0"));
        assertTrue(page.hasCloudconfigSupport(), "Expected cloudconfig support if product version >= 6.4.0");
    }

    @Test
    void testNoHtmlPageItemsConfig() {
        Page page = getPageUnderTest(PAGE);
        assertEquals(0, page.getHtmlPageItems().size(), "Expected no HTML page items");
    }

    @Test
    void testHtmlPageItemsConfigWithDeprecatedCaconfig() throws Exception {
        Page page = getPageUnderTest(PAGE);
        loadHtmlPageItemsConfig(false);
        assertNotNull(page.getHtmlPageItems());
        assertEquals(3, page.getHtmlPageItems().size(), "Unexpected number of HTML page items");
    }

    @Test
    void testHtmlPageItemsConfig() {
        loadHtmlPageItemsConfig(true);
        Page page = getPageUnderTest(PAGE);
        assertNotNull(page.getHtmlPageItems());
        assertEquals(3, page.getHtmlPageItems().size(), "Unexpected number of HTML page items");
    }

    @Test
    public void testRobotsTags() {
        Page page = getPageUnderTest(PAGE);
        assertEquals(2,page.getRobotsTags().size());
        assertThat(page.getRobotsTags(), hasItems("index", "nofollow"));
    }

    @Test
    public void testCanonicalLink() {
        Page page = getPageUnderTest(PAGE);
        assertEquals("http://foo.bar/content/page/templated-page",page.getCanonicalLink());
    }

    @Test
    public void testAlternateLanguageLinks() {
        Page page = getPageUnderTest(PAGE);
        Map<Locale, String> expectedAlternates = ImmutableMap.of(
            Locale.ENGLISH, "http://foo.bar/content/en/templated-page",
            Locale.GERMAN, "http://foo.bar/content/de/templated-page"
        );
        when(pageTreeSitemapGenerator.getLanguageAlternatives(any())).thenReturn(expectedAlternates);
        assertEquals(2,page.getAlternateLanguageLinks().size());
        assertEquals("http://foo.bar/content/en/templated-page",page.getAlternateLanguageLinks().get(Locale.ENGLISH));
        assertEquals("http://foo.bar/content/de/templated-page",page.getAlternateLanguageLinks().get(Locale.GERMAN));
    }
}
