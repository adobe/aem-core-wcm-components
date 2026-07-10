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

package com.adobe.cq.wcm.core.components.internal.models.v3;

import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Search;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class SearchImplTest extends com.adobe.cq.wcm.core.components.internal.models.v2.SearchImplTest {

    private static final String TEST_BASE = "/search/v3";
    private static final String SEARCH_PAGE = "/content/en/search/page";

    private static final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @BeforeEach
    @Override
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = SearchImpl.RESOURCE_TYPE;
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        LiveRelationshipManager relationshipManager = mock(LiveRelationshipManager.class);
        context.registerService(LiveRelationshipManager.class, relationshipManager);
        ResourceBundleProvider resourceBundleProvider = mock(ResourceBundleProvider.class);
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any())).thenReturn(new RootResourceBundle());
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any(), Mockito.any())).thenReturn(new RootResourceBundle());
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.registerInjectActivateService(mockProductInfoProvider);
    }

    @Test
    void testHideAiSearchToggle_defaultHiddenOnAem65() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.currentResource(SEARCH_PAGE + "/jcr:content/search");
        Search search = context.request().adaptTo(Search.class);
        assertTrue(search.hideAiSearchToggle());
    }

    @Test
    void testHideAiSearchToggle_defaultVisibleOnCloudPublish() {
        mockProductInfoProvider.setVersion(new Version("6.6.0"));
        context.currentResource(SEARCH_PAGE + "/jcr:content/search");
        Search search = context.request().adaptTo(Search.class);
        assertFalse(search.hideAiSearchToggle());
    }

    @Test
    void testHideAiSearchToggle_defaultVisibleOnCloudAuthor() {
        mockProductInfoProvider.setVersion(new Version("2026.2.24288"));
        context.currentResource(SEARCH_PAGE + "/jcr:content/search");
        Search search = context.request().adaptTo(Search.class);
        assertFalse(search.hideAiSearchToggle());
    }

    @Test
    void testHideAiSearchToggle_policyEnabled() {
        context.currentResource(SEARCH_PAGE + "/jcr:content/search");
        context.contentPolicyMapping(resourceType, Search.PN_HIDE_AI_SEARCH_TOGGLE, true);
        Search search = context.request().adaptTo(Search.class);
        assertTrue(search.hideAiSearchToggle());
    }

    @Test
    void testHideAiSearchToggle_policyExplicitlyDisabled() {
        mockProductInfoProvider.setVersion(new Version("6.5.25"));
        context.currentResource(SEARCH_PAGE + "/jcr:content/search");
        context.contentPolicyMapping(resourceType, Search.PN_HIDE_AI_SEARCH_TOGGLE, false);
        Search search = context.request().adaptTo(Search.class);
        assertFalse(search.hideAiSearchToggle());
    }
}
