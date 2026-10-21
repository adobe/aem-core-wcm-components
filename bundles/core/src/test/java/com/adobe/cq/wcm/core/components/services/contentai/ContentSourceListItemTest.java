/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.services.contentai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentSourceListItemTest {

    @Test
    void resolvableDescriptionPrefersTopLevelDescription() {
        ContentSourceListItem item = new ContentSourceListItem();
        item.setDescription("Top level");
        ContentSourceListItem.ContentSourceConfig config = new ContentSourceListItem.ContentSourceConfig();
        config.setDescription("Config level");
        item.setConfig(config);

        assertEquals("Top level", item.getResolvableDescription());
    }

    @Test
    void resolvableDescriptionFallsBackToConfigDescription() {
        ContentSourceListItem item = new ContentSourceListItem();
        ContentSourceListItem.ContentSourceConfig config = new ContentSourceListItem.ContentSourceConfig();
        config.setDescription("Config level");
        item.setConfig(config);

        assertEquals("Config level", item.getResolvableDescription());
    }

    @Test
    void resolvableDescriptionReturnsNullWhenMissing() {
        assertNull(new ContentSourceListItem().getResolvableDescription());
    }

    @Test
    void publicAccessReflectsConfigAccess() {
        ContentSourceListItem item = new ContentSourceListItem();
        ContentSourceListItem.ContentSourceConfig config = new ContentSourceListItem.ContentSourceConfig();
        ContentSourceListItem.ContentSourceAccess access = new ContentSourceListItem.ContentSourceAccess();
        access.setPublic(true);
        config.setAccess(access);
        item.setConfig(config);

        assertTrue(item.isPublicAccess());
        access.setPublic(false);
        assertFalse(item.isPublicAccess());
    }

    @Test
    void gettersAndSetters() {
        ContentSourceListItem item = new ContentSourceListItem();
        item.setName("source-a");
        item.setId("id-a");
        item.setType("ACQUISITION");

        assertEquals("source-a", item.getName());
        assertEquals("id-a", item.getId());
        assertEquals("ACQUISITION", item.getType());
    }
}
