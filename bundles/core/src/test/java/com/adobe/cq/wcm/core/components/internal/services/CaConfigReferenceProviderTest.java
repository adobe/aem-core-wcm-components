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
package com.adobe.cq.wcm.core.components.internal.services;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.DataLayerConfig;
import com.day.cq.wcm.api.reference.Reference;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AemContextExtension.class)
class CaConfigReferenceProviderTest {

    private static final String TEST_BASE = "/com/adobe/cq/wcm/core/components/internal/services/CaConfigReferenceProvider";
    private static final String TEST_PAGE = "/content/mysite/page";
    private static final String TEST_CA_COMPONENT = TEST_PAGE + "/ca-page/jcr:content/par/title";
    private static final String TEST_NO_CA_COMPONENT = TEST_PAGE + "/no-ca-page/jcr:content/par/title";
    private static final String SLING_CONFIGS_ROOT = "/conf/mysite/sling:configs";

    private CaConfigReferenceProvider caConfigReferenceProvider;

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
        context.load().json(TEST_BASE + "/test-sling-configs.json", SLING_CONFIGS_ROOT);
        MockContextAwareConfig.registerAnnotationClasses(context, HtmlPageItemsConfig.class, DataLayerConfig.class);
        caConfigReferenceProvider = context.registerInjectActivateService(new CaConfigReferenceProvider());
    }

    @Test
    void testFindReferences() {
        Resource resource = context.resourceResolver().getResource(TEST_CA_COMPONENT);
        assertNotNull(resource);
        List<Reference> references = caConfigReferenceProvider.findReferences(resource);
        assertEquals(2, references.size());
        Reference reference = getReference(references, HtmlPageItemsConfig.class);
        assertNotNull(reference);
        Resource expectedReferenceRes = context.resourceResolver().getResource(SLING_CONFIGS_ROOT + "/" + HtmlPageItemsConfig.class.getName());
        assertEquals("caconfig", reference.getType());
        if (expectedReferenceRes != null) {
            assertEquals(expectedReferenceRes.getPath(), reference.getResource().getPath());
        }
        assertEquals(1602683813696L, reference.getLastModified());
        reference = getReference(references, DataLayerConfig.class);
        assertNotNull(reference);
        assertEquals(1623074213696L, reference.getLastModified());
    }

    private Reference getReference(List<Reference> references, Class<?> clazz) {
        for (Reference refItem: references) {
            if (refItem.getName().equals(clazz.getName())) {
                return refItem;
            }
        }
        return null;
    }

    @Test
    void testNoReferences() {
        Resource resource = context.resourceResolver().getResource(TEST_NO_CA_COMPONENT);
        assertNotNull(resource);
        List<Reference> references = caConfigReferenceProvider.findReferences(resource);
        assertEquals(0, references.size());
    }

    @Test
    void testDisabled() {
        Resource resource = context.resourceResolver().getResource(TEST_CA_COMPONENT);
        CaConfigReferenceProvider referenceProvider = context.registerInjectActivateService(new CaConfigReferenceProvider(), "enabled", false);
        List<Reference> references = referenceProvider.findReferences(resource);
        assertTrue(references.isEmpty());

    }
}
