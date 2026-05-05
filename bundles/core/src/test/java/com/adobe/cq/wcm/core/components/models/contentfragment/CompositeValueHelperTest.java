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
package com.adobe.cq.wcm.core.components.models.contentfragment;

import java.util.Collections;
import java.util.LinkedHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompositeValueHelperTest {

    @Test
    void detectsList() {
        CompositeValueHelper h = new CompositeValueHelper(Collections.singletonList("a"));
        Assertions.assertTrue(h.isList());
        Assertions.assertFalse(h.isMap());
        Assertions.assertEquals(Collections.singletonList("a"), h.getValue());
    }

    @Test
    void detectsMap() {
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        m.put("k", "v");
        CompositeValueHelper h = new CompositeValueHelper(m);
        Assertions.assertFalse(h.isList());
        Assertions.assertTrue(h.isMap());
        Assertions.assertEquals(m, h.getValue());
    }

    @Test
    void scalarNotListOrMap() {
        CompositeValueHelper h = new CompositeValueHelper("x");
        Assertions.assertFalse(h.isList());
        Assertions.assertFalse(h.isMap());
        Assertions.assertEquals("x", h.getValue());
    }
}
