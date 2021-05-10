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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.Utils;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Search;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
class SearchImplTest {

    private static final String TEST_BASE = "/search";
    private static final String CONTENT_ROOT = "/content";
    private static final String SEARCH_PAGE = "/content/en/search/page";
    private static final String SEARCH_PAGE_DE = "/content/de/search/page";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        LiveRelationshipManager relationshipManager = mock(LiveRelationshipManager.class);
        context.registerService(LiveRelationshipManager.class, relationshipManager);
    }

    @Test
    void testSearchProperties() {
        Search search = getSearchUnderTest(SEARCH_PAGE + "/jcr:content/search");
        assertEquals(10, search.getResultsSize());
        assertEquals(3, search.getSearchTermMinimumLength());
        assertEquals("/jcr:content/search", search.getRelativePath());
        assertEquals("core/wcm/components/search/v1/search", search.getExportedType());
        assertEquals("/content/en/search", search.getSearchRootPagePath());
        assertSame(search.getSearchRootPagePath(), search.getSearchRootPagePath());
        Utils.testJSONExport(search, Utils.getTestExporterJSONPath(TEST_BASE, "search"));
    }

    @Test
    void testSearchProperties_noPath() {
        Search search = getSearchUnderTest(SEARCH_PAGE_DE + "/jcr:content/search");
        assertEquals(10, search.getResultsSize());
        assertEquals(3, search.getSearchTermMinimumLength());
        assertEquals("/jcr:content/search", search.getRelativePath());
        assertEquals("core/wcm/components/search/v1/search", search.getExportedType());
        assertEquals(SEARCH_PAGE_DE, search.getSearchRootPagePath());
        assertSame(search.getSearchRootPagePath(), search.getSearchRootPagePath());
        Utils.testJSONExport(search, Utils.getTestExporterJSONPath(TEST_BASE, "search2"));
    }

    private Search getSearchUnderTest(String contentPath) {
        context.currentResource(contentPath);
        return context.request().adaptTo(Search.class);
    }

}
