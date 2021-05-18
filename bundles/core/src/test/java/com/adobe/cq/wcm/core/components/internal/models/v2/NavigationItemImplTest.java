/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.util.Collections;

import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.internal.link.LinkHandler;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigationItemImplTest {

    @Test
    protected void test() {
        Page page = mock(Page.class);
        when(page.getProperties()).thenReturn(ValueMap.EMPTY);
        LinkHandler linkHandler = mock(LinkHandler.class);
        Component component = mock(Component.class);
        NavigationItemImpl navigationItem = new NavigationItemImpl(page, true, true, linkHandler, 0, Collections.emptyList(), "id", false, component);
        assertEquals(page, navigationItem.getPage());
        assertTrue(navigationItem.isActive());
        assertEquals(Collections.emptyList(), navigationItem.getChildren());
        assertEquals(0, navigationItem.getLevel());
    }
}
