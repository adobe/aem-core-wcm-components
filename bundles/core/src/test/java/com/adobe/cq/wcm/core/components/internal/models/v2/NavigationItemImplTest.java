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

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkBuilder;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
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
        LinkManager linkManager = mock(LinkManager.class);
        LinkBuilder linkBuilder = mock(LinkBuilder.class);
        Link link = mock(Link.class);
        when(link.isValid()).thenReturn(false);
        when(linkBuilder.build()).thenReturn(link);
        when(linkManager.get(page)).thenReturn(linkBuilder);
        Component component = mock(Component.class);
        NavigationItemImpl navigationItem = new NavigationItemImpl(page, true, true, linkManager, 0, Collections.emptyList(), "id", component);
        assertEquals(page, navigationItem.getPage());
        assertTrue(navigationItem.isActive());
        assertEquals(Collections.emptyList(), navigationItem.getChildren());
        assertEquals(0, navigationItem.getLevel());
    }
}
