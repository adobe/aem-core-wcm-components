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
import static org.junit.jupiter.api.Assertions.assertSame;

class ContentAIClientExceptionTest {

    @Test
    void statusCodeConstructor() {
        ContentAIClientException exception = new ContentAIClientException("failed", 502);
        assertEquals("failed", exception.getMessage());
        assertEquals(502, exception.getStatusCode());
    }

    @Test
    void causeConstructor() {
        RuntimeException cause = new RuntimeException("timeout");
        ContentAIClientException exception = new ContentAIClientException("transport error", cause);
        assertEquals("transport error", exception.getMessage());
        assertSame(cause, exception.getCause());
        assertEquals(0, exception.getStatusCode());
    }
}
