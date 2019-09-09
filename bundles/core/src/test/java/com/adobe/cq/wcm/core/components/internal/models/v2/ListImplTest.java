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

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit.AemContext;

public class ListImplTest {

    protected static final String TEST_BASE = "/list/v2";
    private static final String CONTEXT_PATH = "/context";
    private static final String LIST_1 = "/content/list/listTypes/staticListType";

    @Rule
    public final AemContext context = CoreComponentTestContext.createContext(TEST_BASE, "/content/list");

    @Before
    public void setUp() throws Exception {
        context.load().json("/list/test-etc.json", "/etc/tags/list");
        context.request().setContextPath(CONTEXT_PATH);
    }

    @Test
    public void testProperties() throws Exception {
        List list = getListUnderTest(LIST_1);
        assertTrue(list.showDescription());
        assertTrue(list.showModificationDate());
        assertTrue(list.linkItems());
        assertEquals(2, list.getListItems().size());
        Utils.testJSONExport(list, Utils.getTestExporterJSONPath(TEST_BASE, LIST_1));
        
        checkListConsistencyByPaths(list, "/content/list/pages/page_1", "/content/list/pages/page_2");
    }

    private List getListUnderTest(String resourcePath) {
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to defines test resource " + resourcePath + "?");
        }
        context.currentResource(resource);
        return context.request().adaptTo(List.class);
    }

    private void checkListConsistencyByPaths(List list, String... expectedPagePaths) {
        assertTrue("Expected that the returned list will contain " + expectedPagePaths.length + " items",
                list.getListItems().size() == expectedPagePaths.length);
        int index = 0;
        for (Page item : list.getItems()) {
            assertEquals(expectedPagePaths[index++], item.getPath());
        }
        index = 0;
        for (ListItem item : list.getListItems()) {
            assertValidLink(item.getLink(), CONTEXT_PATH + expectedPagePaths[index++] + ".html");
        }
    }

}
