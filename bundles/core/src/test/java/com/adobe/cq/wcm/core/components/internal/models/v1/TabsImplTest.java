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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.osgi.MapUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Tabs;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.apache.sling.api.resource.ResourceResolver.PROPERTY_RESOURCE_TYPE;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        assertEquals(0, tabs.getItems().size());
        Utils.testJSONExport(tabs, Utils.getTestExporterJSONPath(TEST_BASE, "tabs0"));
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

    @Test
    void testDataLayerShownItems_ThreeItems_NoActiveItem() {
        Resource tabsResource = createTabsResource();
        addTabsItemResources(tabsResource, "item1", "item2", "item3");

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertEquals("item1", tabs.getActiveItem());
        assertArrayEquals(new String[] {"tabs-3d7c531ec1-item-58f3fa999a"}, ((TabsImpl)tabs).getDataLayerShownItems());
    }

    @Test
    void testDataLayerShownItems_ThreeItems_ActiveItem() {
        Resource tabsResource = createTabsResource("activeItem", "item2");
        addTabsItemResources(tabsResource, "item1", "item2", "item3");

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertEquals("item2", tabs.getActiveItem());
        assertArrayEquals(new String[] {"tabs-3d7c531ec1-item-e2c847d465"}, ((TabsImpl)tabs).getDataLayerShownItems());
    }

    @Test
    void testDataLayerShownItems_ThreeItems_InvalidActiveItem() {
        Resource tabsResource = createTabsResource("activeItem", "not-existing-item");
        addTabsItemResources(tabsResource, "item1", "item2", "item3");

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertEquals("item1", tabs.getActiveItem());
        assertArrayEquals(new String[] {"tabs-3d7c531ec1-item-58f3fa999a"}, ((TabsImpl)tabs).getDataLayerShownItems());
    }

    @Test
    void testDataLayerShownItems_NoItems_NoActiveItem() {
        Resource tabsResource = createTabsResource();

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertNull(tabs.getActiveItem());
        assertArrayEquals(new String[0], ((TabsImpl)tabs).getDataLayerShownItems());
    }

    @Test
    void testDataLayerShownItems_NoItems_ActiveItem() {
        Resource tabsResource = createTabsResource("activeItem", "item2");

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertNull(tabs.getActiveItem());
        assertArrayEquals(new String[0], ((TabsImpl)tabs).getDataLayerShownItems());
    }

    @Test
    void testDataLayerShownItems_NoItems_InvalidActiveItem() {
        Resource tabsResource = createTabsResource("activeItem", "not-existing-item");

        Tabs tabs = getTabsUnderTest(tabsResource.getPath());
        assertNull(tabs.getActiveItem());
        assertArrayEquals(new String[0], ((TabsImpl)tabs).getDataLayerShownItems());
    }

    private Tabs getTabsUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(Tabs.class);
    }

    private void verifyTabItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals(expectedItems.length, items.size(), "The tabs contains a different number of items than expected.");
        int index = 0;
        for (ListItem item : items) {
            assertEquals(expectedItems[index][0], item.getName(), "The tabs item's name is not what was expected.");
            assertEquals(expectedItems[index][1], item.getTitle(), "The tabs item's title is not what was expected: " + item.getTitle());
            index++;
        }
    }

    private Resource createTabsResource(Object... properties) {
        Map<String,Object> props = new HashMap<>();
        props.put(PROPERTY_RESOURCE_TYPE, TabsImpl.RESOURCE_TYPE);
        props.putAll(MapUtil.toMap(properties));

        Page testPage = context.pageManager().getPage(TEST_ROOT_PAGE);
        return context.create().resource(testPage, "tabs-test", props);
    }

    private void addTabsItemResources(Resource tabsResource, String... itemNames) {
        for (String itemName : itemNames) {
            context.create().resource(tabsResource, itemName,
                    PROPERTY_RESOURCE_TYPE, TeaserImpl.RESOURCE_TYPE);
        }
    }

}
