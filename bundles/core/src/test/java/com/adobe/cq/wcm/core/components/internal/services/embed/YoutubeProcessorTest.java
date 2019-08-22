/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import org.junit.Test;

import com.adobe.cq.wcm.core.components.models.Embed;

import static org.junit.Assert.*;

public class YoutubeProcessorTest {

    @Test
    public void test() {
        YoutubeProcessor processor = new YoutubeProcessor();
        Embed.Processor.Result result = processor.process("https://www.youtube.com/watch?v=vpdcMZnYCko");
        assertNotNull(result);
        assertEquals(YoutubeProcessor.NAME, result.getProcessor());
        assertEquals("vpdcMZnYCko", result.getOptions().get(YoutubeProcessor.VIDEO_ID));

        assertNull(processor.process("blah-blah"));
    }
}
