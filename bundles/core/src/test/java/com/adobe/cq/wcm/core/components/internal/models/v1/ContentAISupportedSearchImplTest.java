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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ContentAISupportedSearchImplTest {

    private static final String TEST_BASE = "/contentaisupportedsearch";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content-dam.json", CONTENT_ROOT);
    }

    @Test
    void testProperties() {
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
    }
}
