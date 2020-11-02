/*******************************************************************************
 * Copyright 2017 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.internal.models;

import java.util.Calendar;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.wcm.core.components.models.Page;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PageTest {

    private final Page underTest = new MockPage();

    @Test
    public void testGetFavicons() {
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::getFavicons);
    }

    @Test
    public void testGetFaviconClientLibPath() {
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::getAppResourcesPath);
    }

    @Test
    public void testGetRedirectTarget() {
        Assertions.assertThrows(UnsupportedOperationException.class, underTest::getRedirectTarget);
    }

    private static class MockPage implements Page, ContainerExporter {
        @Override
        public String getLanguage() {
            return null;
        }

        @Override
        public Calendar getLastModifiedDate() {
            return null;
        }

        @Override
        public String[] getKeywords() {
            return new String[0];
        }

        @Override
        public String getDesignPath() {
            return null;
        }

        @Override
        public String getStaticDesignPath() {
            return null;
        }

        @Override
        public String getTitle() {
            return null;
        }

        @Override
        public String[] getClientLibCategories() {
            return new String[0];
        }

        @Override
        public String getTemplateName() {
            return null;
        }

        @NotNull
        @Override
        public Map<String, ? extends ComponentExporter> getExportedItems() {
            return null;
        }

        @NotNull
        @Override
        public String[] getExportedItemsOrder() {
            return new String[0];
        }

        @NotNull
        @Override
        public String getExportedType() {
            return null;
        }
    }
}
