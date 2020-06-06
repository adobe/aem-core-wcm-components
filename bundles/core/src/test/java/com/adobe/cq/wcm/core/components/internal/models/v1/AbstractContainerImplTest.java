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

import org.jetbrains.annotations.NotNull;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

public class AbstractContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    // private static final String CONTEXT_PATH = "/core";
    // private static final String TEST_ROOT_PAGE = "/content/container";
    // private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    // private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
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
        assertEquals(0, items.size());
    }


    private static class ContainerImpl extends AbstractContainerImpl {
        @Override
        @NotNull
        protected List<ListItem> readItems() {
            return new ArrayList<>();
        }
    }
}
