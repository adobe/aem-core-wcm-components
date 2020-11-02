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

import com.adobe.cq.wcm.core.components.services.embed.UrlProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class PinterestUrlProcessorTest {

    @Test
    void test() {
        PinterestUrlProcessor processor = new PinterestUrlProcessor();
        UrlProcessor.Result result = processor.process("https://www.pinterest.com/pin/99360735500167749/");
        assertNotNull(result);
        assertEquals(PinterestUrlProcessor.NAME, result.getProcessor());
        assertEquals("99360735500167749", result.getOptions().get(PinterestUrlProcessor.PIN_ID));

        assertNull(processor.process("blah-blah"));
    }
}
