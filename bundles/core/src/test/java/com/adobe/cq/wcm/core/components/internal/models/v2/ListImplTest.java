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

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class ListImplTest {

    private static final String TEST_BASE = "/list/v2";
    private static final String CONTENT_ROOT = "/content";
    private static final String CURRENT_PAGE = "/content/list";
    private static final String CONTEXT_PATH = "/context";
    private static final String LIST_1 = CURRENT_PAGE + "/jcr:content/root/staticListType";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json("/list" + CoreComponentTestContext.TEST_TAGS_JSON, CONTENT_ROOT + "/cq:tags/list");
    }

    @Test
    protected void testProperties() {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        assertTrue(list.linkItems());
        assertEquals(2, list.getListItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_1));
    }

    private List getListUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to defines test resource " + resourcePath + "?");
        }
        context.request().setContextPath(CONTEXT_PATH);
        context.currentResource(resource);
        return context.request().adaptTo(List.class);
    }

}
