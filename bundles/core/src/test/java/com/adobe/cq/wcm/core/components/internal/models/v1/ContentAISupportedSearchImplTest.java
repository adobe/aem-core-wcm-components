/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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

import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ContentAISupportedSearchImplTest {

    private static final String TEST_BASE = "/contentaisupportedsearch";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/contentaisearch";
    private static final String COMPONENT_DEFAULTS_PATH = CONTENT_ROOT + "/contentaisearch-defaults";
    private static final String COMPONENT_LIST_PATH = CONTENT_ROOT + "/contentaisearch-list";

    private final AemContext context = CoreComponentTestContext.newAemContext();
    private static final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content-dam.json", CONTENT_ROOT);
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.registerInjectActivateService(mockProductInfoProvider);
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any())).thenReturn(new RootResourceBundle());
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any(), Mockito.any())).thenReturn(new RootResourceBundle());
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
    }

    @Test
    void testProperties() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(COMPONENT_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertEquals("my-content-source", search.getContentSource());
        assertEquals("ACQUISITION", search.getContentSourceType());
        assertEquals(1, search.getContentSources().size());
        assertEquals("my-content-source", search.getPrimaryContentSource());
        assertEquals(5, search.getResultsSize());
        assertTrue(search.isGenSearchEnabledByDefault());
        assertTrue(search.isGenSearchToggleVisible());
        assertEquals("RESULTS_ONLY", search.getGenSearchErrorFallback());
        assertEquals(ContentAISupportedSearchImpl.RESOURCE_TYPE, search.getExportedType());
        assertEquals("card", search.getResultsLayout());
        assertNull(search.getPlaceholder());
        assertNull(search.getDisclaimerText());
    }

    @Test
    void testListLayoutAndTextProperties() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(COMPONENT_LIST_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertEquals("list", search.getResultsLayout());
        assertEquals("Search content", search.getPlaceholder());
        assertEquals("AI-generated summary", search.getDisclaimerText());
        assertEquals("SHOW_ERROR", search.getGenSearchErrorFallback());
        assertEquals(2, search.getContentSources().size());
        assertEquals("primary-source", search.getPrimaryContentSource());
        assertFalse(search.isGenSearchEnabledByDefault());
        assertFalse(search.isGenSearchToggleVisible());
    }

    @Test
    void getI18nMessagesReturnsJson() throws Exception {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(COMPONENT_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        JsonNode node = new ObjectMapper().readTree(search.getI18nMessages());
        assertNotNull(node.get("Search"));
        assertNotNull(node.get("Load more results"));
    }

    @Test
    void resultsSize_defaultsToTwelve() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.create().resource(CONTENT_ROOT + "/default-results-size",
            "sling:resourceType", ContentAISupportedSearchImpl.RESOURCE_TYPE,
            "contentSources", new String[] {"my-content-source"});
        context.currentResource(CONTENT_ROOT + "/default-results-size");
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertEquals(12, search.getResultsSize());
    }

    @Test
    void resolvesContentSourceFromLegacyProperty() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.create().resource(CONTENT_ROOT + "/legacy-source",
            "sling:resourceType", ContentAISupportedSearchImpl.RESOURCE_TYPE,
            "contentSource", "legacy-source");
        context.currentResource(CONTENT_ROOT + "/legacy-source");
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertEquals("legacy-source", search.getContentSource());
        assertEquals(1, search.getContentSources().size());
    }

    @Test
    void genSearchErrorFallback_defaultsToResultsOnlyWhenBlank() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        String path = CONTENT_ROOT + "/blank-fallback";
        context.create().resource(path,
            "sling:resourceType", ContentAISupportedSearchImpl.RESOURCE_TYPE,
            "contentSource", "my-content-source",
            "contentSources", new String[] {"my-content-source"},
            "genSearchErrorFallback", " ");
        context.currentResource(path);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertEquals("RESULTS_ONLY", search.getGenSearchErrorFallback());
    }

    @Test
    void genSearchToggleVisible_defaultHiddenOnAem65() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.currentResource(COMPONENT_DEFAULTS_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertFalse(search.isGenSearchToggleVisible());
    }

    @Test
    void genSearchToggleVisible_defaultVisibleOnCloudPublish() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(COMPONENT_DEFAULTS_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertTrue(search.isGenSearchToggleVisible());
    }

    @Test
    void genSearchToggleVisible_defaultVisibleOnCloudAuthorReleaseTrain() {
        mockProductInfoProvider.setVersion(new Version("2026.2.24288"));
        context.currentResource(COMPONENT_DEFAULTS_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertTrue(search.isGenSearchToggleVisible());
    }

    @Test
    void genSearchToggleVisible_alwaysHiddenOnAem65EvenWhenAuthorEnables() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.currentResource(COMPONENT_PATH);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertFalse(search.isGenSearchToggleVisible());
    }

    @Test
    void genSearchToggleVisible_authorCanDisableOnCloud() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(COMPONENT_DEFAULTS_PATH);
        context.create().resource(COMPONENT_DEFAULTS_PATH,
            "sling:resourceType", ContentAISupportedSearchImpl.RESOURCE_TYPE,
            "genSearchToggleVisible", false);
        ContentAISupportedSearch search = context.request().adaptTo(ContentAISupportedSearch.class);
        assertFalse(search.isGenSearchToggleVisible());
    }
}
