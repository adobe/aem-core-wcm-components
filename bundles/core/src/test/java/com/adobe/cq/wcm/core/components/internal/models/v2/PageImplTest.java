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

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.caconfig.ConfigurationResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.config.AttributeConfig;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemConfig;
import com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.cq.wcm.core.components.testing.MockConfigurationResourceResolver;
import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.Utils.getTestExporterJSONPath;
import static com.adobe.cq.wcm.core.components.Utils.testJSONExport;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class PageImplTest extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImplTest {

    private static final String TEST_BASE = "/page/v2";
    private static final String REDIRECT_PAGE = CONTENT_ROOT + "/redirect-page";
    private static final String PN_CLIENT_LIBS = "clientlibs";
    private static final String SLING_CONFIGS_ROOT = "/conf/sling:configs";

    private static final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();
    private static final ConfigurationResolver mockConfigurationResolver = Mockito.mock(ConfigurationResolver.class);

    @BeforeEach
    protected void setUp() {
        internalSetup(TEST_BASE);
        ClientLibrary mockClientLibrary = Mockito.mock(ClientLibrary.class);
        when(mockClientLibrary.getPath()).thenReturn("/apps/wcm/core/page/clientlibs/favicon");
        when(mockClientLibrary.allowProxy()).thenReturn(true);
        context.registerInjectActivateService(new MockHtmlLibraryManager(mockClientLibrary));
        context.registerInjectActivateService(mockProductInfoProvider);
        MockConfigurationResourceResolver mockConfigurationResourceResolver = new MockConfigurationResourceResolver(context.resourceResolver(), SLING_CONFIGS_ROOT);
        context.registerInjectActivateService(mockConfigurationResourceResolver);
        context.registerService(ConfigurationResolver.class, mockConfigurationResolver);
    }

    private void loadHtmlPageItemsConfig(boolean useNewFormat) {
        if (useNewFormat) {
            context.load().json(TEST_BASE + "/test-sling-configs.json", SLING_CONFIGS_ROOT);
        } else {
            context.load().json(TEST_BASE + "/test-sling-configs-deprecated-caconfig.json", SLING_CONFIGS_ROOT);
        }
    }

    @Test
    void testPage() throws Exception {
        testPage(true);
    }

    @Test
    void testPageWithDeprecatedCaconfig() throws Exception {
        testPage(false);
    }

    private void testPage(boolean useNewCaconfig) throws Exception {
        Page page = getPageUnderTest(PAGE, DESIGN_PATH_KEY, DESIGN_PATH, PageImpl.PN_CLIENTLIBS_JS_HEAD,
                new String[]{"coretest.product-page-js-head"}, PN_CLIENT_LIBS,
                new String[]{"coretest.product-page","coretest.product-page-js-head"}, Page.PN_APP_RESOURCES_CLIENTLIB,
                "coretest.product-page.appResources",
                CSS_CLASS_NAMES_KEY, new String[]{"class1", "class2"});
        loadHtmlPageItemsConfig(useNewCaconfig);
        mockConfigurationResolver(page);
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
    void testFavicons() throws Exception {
        Page page = getPageUnderTest(PAGE);
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
        assertThrows(UnsupportedOperationException.class, page::getFavicons);
    }

    @Test
    void testGetFaviconClientLibPath() throws Exception {
        Page page = getPageUnderTest(PAGE, Page.PN_APP_RESOURCES_CLIENTLIB,
                "coretest.product-page.appResources");
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
        String faviconClientLibPath = page.getAppResourcesPath();
        assertEquals(CONTEXT_PATH + "/etc.clientlibs/wcm/core/page/clientlibs/favicon/resources", faviconClientLibPath);
    }

    @Test
    void testRedirectTarget() throws Exception {
        Page page = getPageUnderTest(REDIRECT_PAGE);
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
        NavigationItem redirectTarget = page.getRedirectTarget();
        assertNotNull(redirectTarget);
        assertEquals("Templated Page", redirectTarget.getPage().getTitle());
        assertEquals("/core/content/page/templated-page.html", redirectTarget.getURL());
    }

    @Test
    void testGetCssClasses() throws Exception {
        Page page = getPageUnderTest(PAGE, CSS_CLASS_NAMES_KEY, new String[]{"class1", "class2"});
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
        String cssClasses = page.getCssClassNames();
        assertEquals("class1 class2", cssClasses, "The CSS classes of the page are not expected: " + PAGE);
    }

    @Test
    void testHasCloudconfigSupport() throws Exception {
        Page page = new PageImpl();
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
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
    void testNoHtmlPageItemsConfig() throws Exception {
        Page page = getPageUnderTest(PAGE);
        mockConfigurationResolver(page);
        assertEquals(0, page.getHtmlPageItems().size(), "Expected no HTML page items");
    }

    @Test
    void testHtmlPageItemsConfigWithDeprecatedCaconfig() throws Exception {
        Page page = getPageUnderTest(PAGE);
        loadHtmlPageItemsConfig(false);
        mockConfigurationResolver(page);
        assertNotNull(page.getHtmlPageItems());
        assertEquals(3, page.getHtmlPageItems().size(), "Unexpected number of HTML page items");
    }

    @Test
    void testHtmlPageItemsConfig() throws Exception {
        Page page = getPageUnderTest(PAGE);
        loadHtmlPageItemsConfig(true);
        mockConfigurationResolver(page);
        assertNotNull(page.getHtmlPageItems());
        assertEquals(3, page.getHtmlPageItems().size(), "Unexpected number of HTML page items");
    }

    private void mockConfigurationResolver(Page underTest) throws Exception {
        Resource htmlPageItemsConfigRes = context.resourceResolver().getResource(SLING_CONFIGS_ROOT + "/" + HtmlPageItemsConfig.class.getName());
        HtmlPageItemsConfig mockConfig = Mockito.mock(HtmlPageItemsConfig.class);
        if (htmlPageItemsConfigRes != null) {
            Resource itemsRes = htmlPageItemsConfigRes.getChild("items");
            ValueMap configProps = htmlPageItemsConfigRes.getValueMap();
            String prefixPath = configProps.get("prefixPath", String.class);
            when(mockConfig.prefixPath()).thenReturn(prefixPath);

            List<HtmlPageItemConfig> mockItemsList = new LinkedList<>();
            // the new caconfig has an item resource
            if (itemsRes != null) {
                for (Resource child : itemsRes.getChildren()) {
                    ValueMap properties = child.getValueMap();
                    String element = properties.get("element", String.class);
                    String location = properties.get("location", String.class);
                    HtmlPageItemConfig mockItem = Mockito.mock(HtmlPageItemConfig.class);
                    when(mockItem.element()).thenReturn(element);
                    when(mockItem.location()).thenReturn(location);
                    Resource attributesRes = child.getChild("attributes");
                    if (attributesRes != null) {
                        ValueMap attributesProps = attributesRes.getValueMap();
                        List<AttributeConfig> attributeConfigList = new LinkedList<>();
                        Set<String> keys = attributesProps.keySet();
                        for (String key : keys) {
                            if (StringUtils.equals(key, "jcr:primaryType")) {
                                continue;
                            }
                            String value = (String) attributesProps.get(key);
                            AttributeConfig mockAttributeConfig = Mockito.mock(AttributeConfig.class);
                            when(mockAttributeConfig.name()).thenReturn(key);
                            when(mockAttributeConfig.value()).thenReturn(value);
                            attributeConfigList.add(mockAttributeConfig);
                        }
                        when(mockItem.attributes()).thenReturn(attributeConfigList.toArray(new AttributeConfig[0]));
                    } else {
                        when(mockItem.attributes()).thenReturn(new AttributeConfig[0]);
                    }
                    mockItemsList.add(mockItem);
                }
            }
            HtmlPageItemConfig[] mockItems = mockItemsList.toArray(new HtmlPageItemConfig[0]);
            when(mockConfig.items()).thenReturn(mockItems);

        } else {
            when(mockConfig.items()).thenReturn(ArrayUtils.toArray());
        }
        ConfigurationBuilder mockConfigurationBuilder = Mockito.mock(ConfigurationBuilder.class);
        when(mockConfigurationBuilder.as(HtmlPageItemsConfig.class)).thenReturn(mockConfig);
        when(mockConfigurationResolver.get(any())).thenReturn(mockConfigurationBuilder);
        Field configurationResourceResolverField = underTest.getClass().getDeclaredField("configurationResolver");
        configurationResourceResolverField.setAccessible(true);
        configurationResourceResolverField.set(underTest, mockConfigurationResolver);
    }

}
