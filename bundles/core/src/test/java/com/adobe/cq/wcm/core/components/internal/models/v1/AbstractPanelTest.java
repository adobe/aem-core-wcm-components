/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.PanelContainer;
import com.adobe.cq.wcm.core.components.models.PanelContainerItem;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Shared tests for all panel containers.
 */
public class AbstractPanelTest {

    /**
     * Verify the data layer for the specified container and it's children.
     *
     * @param container The container.
     * @param testBase The test base.
     * @param containerName The container name.
     */
    protected void verifyPanelDataLayer(PanelContainer container, String testBase, String containerName) {
        Utils.testJSONDataLayer(container.getData(), Utils.getTestDataModelJSONPath(testBase, containerName));
        for (PanelContainerItem item : container.getChildren()) {
            Utils.testJSONDataLayer(item.getData(), Utils.getTestDataModelJSONPath(testBase, containerName + "-" + item.getResource().getName()));
        }
    }

    /**
     * Verify the panel container items for the specified container.
     *
     * @param expectedItems The expected items.
     * @param items The actual items.
     */
    protected void verifyContainerItems(Object[][] expectedItems, List<? extends PanelContainerItem> items) {
        assertEquals(expectedItems.length, items.size(), "The tabs contains a different number of items than expected.");
        int index = 0;
        for (PanelContainerItem item : items) {
            assertEquals(expectedItems[index][0], item.getName(), "The panel item's name is not what was expected.");
            assertEquals(expectedItems[index][1], item.getTitle(), "The panel item's title is not what was expected: " + item.getTitle());
            assertEquals(expectedItems[index][2], item.getId(), "The panel item's id is not what was expected: " + item.getId());
            assertEquals(expectedItems[index][3], item.getResource().getPath(), "The panel item's path is not what was expected: " + item.getResource().getPath());
            index++;
        }
    }

    /**
     * Verify the list items for the specified container.
     *
     * @param expectedItems The expected items.
     * @param items The actual items.
     */
    protected void verifyContainerListItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals(expectedItems.length, items.size(), "The container contains a different number of items than expected.");
        int index = 0;
        for (ListItem item : items) {
            assertEquals(expectedItems[index][0], item.getName(), "The panel item's name is not what was expected.");
            assertEquals(expectedItems[index][1], item.getTitle(), "The panel item's title is not what was expected: " + item.getTitle());
            assertEquals(expectedItems[index][2], item.getId(), "The panel item's id is not what was expected: " + item.getId());
            assertEquals(expectedItems[index][3], item.getPath(), "The panel item's path is not what was expected: " + item.getPath());
            index++;
        }
    }
}
