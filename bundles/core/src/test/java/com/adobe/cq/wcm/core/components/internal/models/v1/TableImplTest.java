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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Table;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
class TableImplTest {

    private static final String TEST_BASE = "/table";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = CONTENT_ROOT + "/table";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTEXT_PATH = "/core";
    private static final String TABLE_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/table-1";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
    }

    @Test
    void testEmptyTable() {
        Table table = new TableImpl();
        List<List<String>> items = table.getItems();
        Assert.assertTrue("", CollectionUtils.isEmpty(items));

    }

    @Test
    void testTableWithItems() {
        Table table = getTableUnderTest();
        Object[][] expectedItems = {
            {"item-1", "Active-1"},
            {"item-2", "Active-2"},
            {"item-3", "Active-3"}
        };
        verifyTableItems(expectedItems, table.getItems());
        Utils.testJSONExport(table, Utils.getTestExporterJSONPath(TEST_BASE, "table-1"));
    }


    private void verifyTableItems(Object[][] expectedItems, List<List<String>> items) {
        assertEquals("The table contains a different number of items than expected.", expectedItems.length, items.size());
        int index = 0;
        for (List<String> item : items) {
            assertEquals("The table item's is not what was expected.",
                expectedItems[index][0], item.get(index));
            assertEquals("The table item's  is not what was expected: " + item.get(index),
                expectedItems[index][1], item.get(index));
            assertEquals("The table item's  is not what was expected: " + item.get(index),
                expectedItems[index][2], item.get(index));
            index++;
        }
    }
    private Table getTableUnderTest() {
        Utils.enableDataLayer(context, true);
        context.currentResource(TABLE_1);
        context.request().setContextPath(CONTEXT_PATH);
        return context.request().adaptTo(Table.class);
    }

}
