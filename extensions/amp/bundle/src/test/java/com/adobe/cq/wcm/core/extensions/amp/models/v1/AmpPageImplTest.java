/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.extensions.amp.models.v1;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.testing.MockHtmlLibraryManager;
import com.adobe.cq.wcm.core.extensions.amp.AmpTestContext;
import com.adobe.cq.wcm.core.extensions.amp.models.AmpPage;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
class AmpPageImplTest {
    private static final String TEST_BASE = "/amp-page";
    private static final String TEST_ROOT_PAGE = "/content";
    private static final String PAIRED_AMP = TEST_ROOT_PAGE + "/paired-amp";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_NO_COMPONENT = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-no-component";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_NULL_SUPERTYPE = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-null-supertype";
    private static final String AMP_SELECTOR_WITH_AMP_MODE_NO_SUPERTYPE = TEST_ROOT_PAGE + "/amp-selector-with-amp-mode-no-supertype";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private final AemContext context = AmpTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + AmpTestContext.TEST_CONTENT_JSON, TEST_ROOT_PAGE);
        context.load().json(TEST_BASE + AmpTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        context.registerInjectActivateService(new MockHtmlLibraryManager(mock(com.adobe.granite.ui.clientlibs.ClientLibrary.class)));
    }

    @Test
    void pairedAmpWithAmpSelector() {
        context.currentPage(PAIRED_AMP);
        context.requestPathInfo().setSelectorString("amp");
        context.requestPathInfo().setExtension("html");
        AmpPage ampPage = context.request().adaptTo(AmpPage.class);
        assertNotNull(ampPage);
        Map<String, String> expectedAttrs = new HashMap<>();
        expectedAttrs.put("rel", "canonical");
        expectedAttrs.put("href", "/content/paired-amp.html");
        assertEquals(expectedAttrs, ampPage.getPageLinkAttrs());

        assertTrue(ampPage.isAmpEnabled());
    }

    @Test
    void pairedAmpWithoutAmpSelector() {
        context.currentPage(PAIRED_AMP);
        context.requestPathInfo().setExtension("html");
        AmpPage ampPage = context.request().adaptTo(AmpPage.class);
        assertNotNull(ampPage);
        Map<String, String> expectedAttrs = new HashMap<>();
        expectedAttrs.put("rel", "amphtml");
        expectedAttrs.put("href", "/content/paired-amp.amp.html");
        assertEquals(expectedAttrs, ampPage.getPageLinkAttrs());

        assertTrue(ampPage.isAmpEnabled());
    }

}
