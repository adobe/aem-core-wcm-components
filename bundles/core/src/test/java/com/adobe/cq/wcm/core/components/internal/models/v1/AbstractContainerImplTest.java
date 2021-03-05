/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
~ Copyright 2018 Adobe
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

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class AbstractContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    public void testEmptyContainer() {
        Container container = new ContainerImpl();
        List<ListItem> items = container.getItems();
        assertEquals(0, items.size());
    }

    @Test
    public void testContainerWithItems() {
        Container container = getContainerUnderTest(CONTAINER_1);
        Object[][] expectedItems = {
            {"Teaser 1 description", "item_1", "/content/container/jcr:content/root/responsivegrid/container-1/item_1", "Teaser 1"},
            {"Teaser 2 description", "item_2", "/content/container/jcr:content/root/responsivegrid/container-1/item_2", "Teaser 2"},
        };
        verifyContainerItems(expectedItems, container.getItems());
    }

    private Container getContainerUnderTest(String resourcePath) {
        Resource resource = context.currentResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(LayoutContainer.class);
    }

    private void verifyContainerItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals(expectedItems.length, items.size(), "The container has a different number of items than expected.");
        int index = 0;
        for (ListItem item : items) {
            assertEquals(expectedItems[index][0], item.getDescription(), "The container item's description is not what was expected: " + item.getDescription()                );
            assertEquals(expectedItems[index][1], item.getName(), "The container item's name is not what was expected: " + item.getName());
            assertEquals(expectedItems[index][2], item.getPath(), "The container item's path is not what was expected: " + item.getPath());
            assertEquals(expectedItems[index][3], item.getTitle(), "The container item's title is not what was expected: " + item.getTitle());
            index++;
        }
    }

    private static class ContainerImpl extends AbstractContainerImpl {
        @Override
        @NotNull
        protected List<ListItem> readItems() {
            return new ArrayList<>();
        }

        @Override
        public String[] getDataLayerShownItems() {
            return null;
        }
    }
}
