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

import org.apache.sling.api.resource.Resource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

public class TabsImplTest {

    private static final String TEST_BASE = "/tabs";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/tabs";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TABS_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-1";
    private static final String TABS_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/tabs-2";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @Rule
    public final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Before
    public void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        AEM_CONTEXT.registerService(SlingModelFilter.class, new MockSlingModelFilter());
    }

    @Test
    public void testEmptyTabs() {
        Tabs tabs = new TabsImpl();
        List<ListItem> items = tabs.getItems();
        Assert.assertTrue("", items == null || items.size() == 0);
    }

    @Test
    public void testTabsWithItems() {
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
    public void testTabsWithNestedTabs() {
        Tabs tabs = getTabsUnderTest(TABS_2);
        Utils.testJSONExport(tabs, Utils.getTestExporterJSONPath(TEST_BASE, "tabs2"));
    }

    private Tabs getTabsUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        AEM_CONTEXT.currentResource(resource);
        AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
        return AEM_CONTEXT.request().adaptTo(Tabs.class);
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
