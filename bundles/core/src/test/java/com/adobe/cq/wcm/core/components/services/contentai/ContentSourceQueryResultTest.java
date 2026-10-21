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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class ContentSourceQueryResultTest {

    @Test
    void hitGettersAndSetters() {
        ContentSourceQueryResult.Hit hit = new ContentSourceQueryResult.Hit();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("title", "Example");

        hit.setId("hit-1");
        hit.setMetadata(metadata);

        assertEquals("hit-1", hit.getId());
        assertSame(metadata, hit.getMetadata());
    }

    @Test
    void queryResultGettersAndSetters() {
        ContentSourceQueryResult result = new ContentSourceQueryResult();
        ContentSourceQueryResult.Hit hit = new ContentSourceQueryResult.Hit();
        hit.setId("hit-1");

        result.setQuery("cars");
        result.setResult("answer");
        result.setHits(Collections.singletonList(hit));

        assertEquals("cars", result.getQuery());
        assertEquals("answer", result.getResult());
        assertEquals(1, result.getHits().size());
        assertEquals("hit-1", result.getHits().get(0).getId());
    }
}
