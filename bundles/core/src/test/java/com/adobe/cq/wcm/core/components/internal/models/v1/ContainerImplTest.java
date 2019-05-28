/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String CONTAINER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-2";
    private static final String CONTAINER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-3";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @Rule
    public final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Before
    public void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        AEM_CONTEXT.registerService(SlingModelFilter.class, new MockSlingModelFilter());
    }

    @Test
    public void testContainerWithBackgroundAndItems() {
        Container container = getContainerUnderTest(CONTAINER_1);
        assertEquals("Style mismatch",
                "background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;",
                container.getStyle());
        Object[][] expectedItems = {
                {"item_1", "Teaser 1"},
                {"item_2", "Teaser 2"}
        };
        verifyContainerItems(expectedItems, container.getItems());
        assertEquals("Exported type mismatch",
                "core/wcm/components/container/v1/container",
                container.getExportedType());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container1"));
    }
    
    @Test
    public void testContainerNoProperties() {
        Container container = getContainerUnderTest(CONTAINER_2);
        assertNull("Style", container.getStyle());
    }
    
    @Test
    public void testContainerWithPropertiesAndDisabledBackgroundFromContentPolicies() {
        Container container = getContainerUnderTest(CONTAINER_3);
        // TODO: mock content policy to disable backgrounds
        //assertNull("Style", container.getStyle());
    }
    
    private void verifyContainerItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("Item number mismatch", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("Item title mismatch", expectedItems[index][1], item.getTitle());
            index++;
        }
    }

    private Container getContainerUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }        
        AEM_CONTEXT.currentResource(resource);
        AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
        return AEM_CONTEXT.request().adaptTo(Container.class);
    }
}
