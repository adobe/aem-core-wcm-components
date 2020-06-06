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

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
class TabsImplTest {

    private static final String TEST_BASE = "/tabs";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/tabs";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TABS_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-1";
    private static final String TABS_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-2";
    private static final String TABS_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-3";
    private static final String TABS_EMPTY = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-empty";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    void testEmptyTabs() {
        Tabs tabs = getTabsUnderTest(TABS_EMPTY);
        Assert.assertEquals(0, tabs.getItems().size());
    }

    @Test
    void testTabsWithItems() {
        Tabs tabs = getTabsUnderTest(TABS_1);
        Object[][] expectedItems = {
            {"item_1", "Tab 1"},
            {"item_2", "Tab Panel 2"},
        };
        verifyTabItems(expectedItems, tabs.getItems());
        assertEquals("item_2", tabs.getActiveItem());
        Utils.testJSONExport(tabs, Utils.getTestExporterJSONPath(TEST_BASE, "tabs1"));
    }

    @Test
    void testTabsWithNestedTabs() {
        Tabs tabs = getTabsUnderTest(TABS_2);
        Utils.testJSONExport(tabs, Utils.getTestExporterJSONPath(TEST_BASE, "tabs2"));
    }

    @Test
    void testTabsDefaultActiveItem() {
        Tabs tabs = getTabsUnderTest(TABS_3);
        Object[][] expectedItems = {
            {"item_1", "Tab 1"},
            {"item_2", "Tab Panel 2"},
        };
        verifyTabItems(expectedItems, tabs.getItems());
        assertEquals("item_1", tabs.getActiveItem());
        Utils.testJSONExport(tabs, Utils.getTestExporterJSONPath(TEST_BASE, "tabs3"));
    }

    private Tabs getTabsUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(Tabs.class);
    }

    private void verifyTabItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("The tabs contains a different number of items than expected.", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("The tabs item's name is not what was expected.",
                expectedItems[index][0], item.getName());
            assertEquals("The tabs item's title is not what was expected: " + item.getTitle(),
                expectedItems[index][1], item.getTitle());
            index++;
        }
    }
}
