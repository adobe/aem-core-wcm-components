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
package com.adobe.cq.wcm.core.components.internal.services.contentai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ContentSourceLabelFormatterTest {

    @Test
    void nullDescription_returnsNameOnly() {
        assertEquals("aem-live", ContentSourceLabelFormatter.formatLabel("aem-live", null));
    }

    @Test
    void blankDescription_returnsNameOnly() {
        assertEquals("aem-live", ContentSourceLabelFormatter.formatLabel("aem-live", "   "));
    }

    @Test
    void shortDescription_appendsFull() {
        assertEquals("hotels-demo — Demo index",
            ContentSourceLabelFormatter.formatLabel("hotels-demo", "Demo index"));
    }

    @Test
    void longDescription_truncatesWithEllipsis() {
        String desc = "A".repeat(90);
        String label = ContentSourceLabelFormatter.formatLabel("x", desc);
        assertTrue(label.endsWith("..."));
        assertEquals("x — " + "A".repeat(80) + "...", label);
    }

    @Test
    void resolveIndexName_prefersNameOverId() {
        assertEquals("n", ContentSourceLabelFormatter.resolveIndexName("n", "id"));
        assertEquals("id", ContentSourceLabelFormatter.resolveIndexName("", "id"));
    }
}
