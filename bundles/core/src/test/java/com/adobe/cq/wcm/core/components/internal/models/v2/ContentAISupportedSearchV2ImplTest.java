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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.osgi.framework.Version;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearchV2;
import com.adobe.cq.wcm.core.components.testing.MockProductInfoProvider;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ContentAISupportedSearchV2ImplTest {

    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/contentaisearch-v2";

    private final AemContext context = CoreComponentTestContext.newAemContext();
    private static final MockProductInfoProvider mockProductInfoProvider = new MockProductInfoProvider();

    @BeforeEach
    void setUp() {
        mockProductInfoProvider.setVersion(new Version("6.6.0")); // cloud, per AemCloudPlatformDetector.MIN_CLOUD_CLASSIC_VERSION
        context.registerInjectActivateService(mockProductInfoProvider);
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any())).thenReturn(new RootResourceBundle());
        Mockito.when(resourceBundleProvider.getResourceBundle(Mockito.any(), Mockito.any())).thenReturn(new RootResourceBundle());
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
    }

    private void createResource(Map<String, Object> extraProps) {
        Map<String, Object> props = new HashMap<>(extraProps);
        props.put("sling:resourceType", ContentAISupportedSearchV2Impl.RESOURCE_TYPE);
        context.create().resource(COMPONENT_PATH, props);
    }

    @Test
    void resolvesContentSourcesAndPrimarySource() {
        Map<String, Object> props = new HashMap<>();
        props.put("contentSources", new String[] {"wknd", "wknd-blog"});
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals(Arrays.asList("wknd", "wknd-blog"), model.getContentSources());
        assertEquals("wknd", model.getPrimaryContentSource());
    }

    @Test
    void aiSearchModeEnabledDefaultsToTrueOnCloud() {
        createResource(new HashMap<>());
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertTrue(model.isAiSearchModeEnabled());
    }

    @Test
    void aiSearchModeEnabledFalseOnNonCloud() {
        mockProductInfoProvider.setVersion(new Version("6.5.25")); // below MIN_CLOUD_CLASSIC_VERSION
        createResource(new HashMap<>());
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertFalse(model.isAiSearchModeEnabled());
    }

    @Test
    void aiSearchModeEnabledFalseWhenAuthoredFalse() {
        Map<String, Object> props = new HashMap<>();
        props.put("aiSearchModeEnabled", false);
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertFalse(model.isAiSearchModeEnabled());
    }

    @Test
    void genSearchErrorRetryVisibleDefaultsToTrue() {
        createResource(new HashMap<>());
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertTrue(model.isGenSearchErrorRetryVisible());
    }

    @Test
    void genSearchErrorRetryVisibleFalseWhenAuthoredFalse() {
        Map<String, Object> props = new HashMap<>();
        props.put("genSearchErrorRetryVisible", false);
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertFalse(model.isGenSearchErrorRetryVisible());
    }

    @Test
    void resultsLayoutDefaultsToCard() {
        createResource(new HashMap<>());
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals(ContentAISupportedSearchV2.RESULTS_LAYOUT_CARD, model.getResultsLayout());
    }

    @Test
    void resultsLayoutListWhenAuthored() {
        Map<String, Object> props = new HashMap<>();
        props.put("resultsLayout", "list");
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals(ContentAISupportedSearchV2.RESULTS_LAYOUT_LIST, model.getResultsLayout());
    }

    @Test
    void exportedTypeIsV2ResourceType() {
        createResource(new HashMap<>());
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals(ContentAISupportedSearchV2Impl.RESOURCE_TYPE, model.getExportedType());
    }

    @Test
    void resolvesLegacySingleContentSourceProperty() {
        Map<String, Object> props = new HashMap<>();
        props.put("contentSource", "wknd");
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals(Arrays.asList("wknd"), model.getContentSources());
        assertEquals("wknd", model.getPrimaryContentSource());
    }

    @Test
    void primaryContentSourceOverridesFirstOfContentSources() {
        Map<String, Object> props = new HashMap<>();
        props.put("contentSources", new String[] {"wknd", "wknd-blog"});
        props.put("primaryContentSource", "wknd-blog");
        createResource(props);
        context.currentResource(COMPONENT_PATH);

        ContentAISupportedSearchV2 model = context.request().adaptTo(ContentAISupportedSearchV2.class);

        assertEquals("wknd-blog", model.getPrimaryContentSource());
    }
}
