/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.services.embed;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.services.embed.OEmbedClient;
import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class OEmbedUrlProcessorTest {

    @Test
    void test() {
        OEmbedUrlProcessor processor = new OEmbedUrlProcessor();

        OEmbedClient mockClient = Mockito.mock(OEmbedClient.class);
        Mockito.when(mockClient.getProvider("something")).thenReturn("Test");
        Mockito.when(mockClient.getResponse("something")).thenReturn(new OEmbedJSONResponseImpl());
        processor.oEmbedClient = mockClient;

        UrlProcessor.Result result = processor.process("something");
        assertNotNull(result);
        assertEquals(OEmbedUrlProcessor.NAME, result.getProcessor());
        assertEquals("Test", result.getOptions().get("provider"));
        assertNotNull(result.getOptions().get("response"));

        assertNull(processor.process("anything"));
    }
}
