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
package com.adobe.cq.wcm.core.components.models;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

import org.junit.jupiter.api.Test;

/**
 * Test default method implementations of {@link Image}.
 */
class ImageTest {

    private static final String URL = "/url.html";

    @Test
    void testValidLink() {
        Image underTest = new ImageImpl(URL);
        assertValidLink(underTest.getImageLink(), URL);
    }

    @Test
    void testInvalidLink() {
        Image underTest = new ImageImpl(null);
        assertInvalidLink(underTest.getImageLink());
    }

    private static class ImageImpl implements Image {
        
        private final String url;
        
        public ImageImpl(String url) {
            this.url = url;
        }
        
        @Override
        public String getLink() {
            return url;
        }

    }

}
