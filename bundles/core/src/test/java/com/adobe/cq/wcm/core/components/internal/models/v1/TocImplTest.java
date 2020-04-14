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

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Toc;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
class TocImplTest {

    private static final String TEST_BASE = "/toc";
    private static final String TEST_PAGE = "/content/toc";
    private static final String TITLE_RESOURCE_JCR_TITLE = TEST_PAGE + "/jcr:content/par/title-jcr-title";
    private static final String TABLE_OF_CONTENT_DEFAULT_TITLE = "Table of Content";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", TEST_PAGE);
    }

    @Test
    void testExportedType() {
        Toc toc = getTableOfContentUnderTest(TITLE_RESOURCE_JCR_TITLE);
        assertEquals(TitleImpl.RESOURCE_TYPE_V1, toc.getExportedType());
    }

   @Test
    void testGetTitle() {
        Toc toc = new TocImpl();
        assertEquals(TABLE_OF_CONTENT_DEFAULT_TITLE, toc.getTitle());
       // Utils.testJSONExport(toc, Utils.getTestExporterJSONPath(TEST_BASE, TITLE_RESOURCE_JCR_TITLE));
    }

   private Toc getTableOfContentUnderTest(String resourcePath, Object ... properties) {
        Resource resource = context.currentResource(resourcePath);
        if (resource != null && properties != null) {
            context.contentPolicyMapping(resource.getResourceType(), properties);
        }
        MockSlingHttpServletRequest request = context.request();
        return request.adaptTo(Toc.class);
    }

}
