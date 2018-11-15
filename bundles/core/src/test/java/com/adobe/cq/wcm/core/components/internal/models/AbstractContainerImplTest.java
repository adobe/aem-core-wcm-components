/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models;

import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbstractContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CAROUSEL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    public void testEmptyContainer() {
        Container container = new ContainerImpl();
        List<ListItem> items = container.getItems();
        assertTrue("", items == null || items.size() == 0);
    }

    @Test
    public void testContainerWithItems() {
        Container container = getContainerUnderTest(CAROUSEL_1);
        Object[][] expectedItems = {
            {"Teaser 1 description", "item_1", "/content/container/jcr:content/root/responsivegrid/container-1/item_1", "Teaser 1"},
            {"Teaser 2 description", "item_2", "/content/container/jcr:content/root/responsivegrid/container-1/item_2", "Teaser 2"},
        };
        verifyContainerItems(expectedItems, container.getItems());
    }

    private Container getContainerUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        AEM_CONTEXT.currentResource(resource);
        AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
        Container container = new ContainerImpl();
        Whitebox.setInternalState(container, "resource", resource);
        Whitebox.setInternalState(container, "request", AEM_CONTEXT.request());
        return container;
    }

    private void verifyContainerItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("The container has a different number of items than expected.", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("The container item's description is not what was expected: " + item.getDescription(),
                expectedItems[index][0], item.getDescription());
            assertEquals("The container item's name is not what was expected: " + item.getName(),
                expectedItems[index][1], item.getName());
            assertEquals("The container item's path is not what was expected: " + item.getPath(),
                expectedItems[index][2], item.getPath());
            assertEquals("The container item's title is not what was expected: " + item.getTitle(),
                expectedItems[index][3], item.getTitle());
            index++;
        }
    }

    private class ContainerImpl extends AbstractContainerImpl {
    }
}
