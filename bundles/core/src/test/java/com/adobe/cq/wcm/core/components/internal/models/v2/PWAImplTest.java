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

package com.adobe.cq.wcm.core.components.internal.models.v2;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.config.PWACaConfig;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.PWA;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(AemContextExtension.class)
class PWAImplTest {

    private static final String SITES_PROJECT_PATH = "/content/page/templated-page";
    private static final String CONTENT_ROOT = "/content/page";
    private static final String TEST_BASE = "/page/v2";
    private PWA pwa;

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        MockContextAwareConfig.registerAnnotationClasses(context, PWACaConfig.class);
        Resource contextResource = context.currentResource(CONTENT_ROOT + "/templated-page/jcr:content");
        if (contextResource != null) {
            MockContextAwareConfig.writeConfiguration(context, contextResource.getPath(), PWACaConfig.class, "projectSiteRootLevel", 2);
        }
    }

    @Test
    void testPWAProperties() {
        pwa = context.request().adaptTo(PWA.class);
        assertEquals(SITES_PROJECT_PATH + "/manifest.webmanifest", pwa.getManifestPath());
        assertEquals("/content/dam/foo.png", pwa.getIconPath());
        assertEquals("/templated-pagesw.js", pwa.getServiceWorkerPath());
        assertEquals("templated-page", pwa.getProjectName());
        assertEquals("#ffa000", pwa.getThemeColor());
    }


    @Test
    void testPWAReturnsFalseIfPWAOptionIsNotEnabled() {
        context.currentResource(CONTENT_ROOT + "/redirect-page/jcr:content");
        pwa = context.request().adaptTo(PWA.class);
        assertFalse(pwa.isEnabled());
    }
}
