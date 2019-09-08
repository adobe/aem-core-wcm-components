/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.adobe.cq.wcm.core.components.models;

import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertInvalidLink;
import static com.adobe.cq.wcm.core.components.internal.link.LinkTestUtils.assertValidLink;

import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.models.mixin.LinkMixin;

/**
 * Test default method implementations of {@link ListItem}.
 */
class ListItemTest {

    private static final String URL = "/url.html";

    @Test
    void testValidLink() {
        LinkMixin underTest = new ListItemImpl(URL);
        assertValidLink(underTest, URL);
    }

    @Test
    void testInvalidLink() {
        LinkMixin underTest = new ListItemImpl(null);
        assertInvalidLink(underTest);
    }

    private static class ListItemImpl implements ListItem {
        
        private final String url;
        
        public ListItemImpl(String url) {
            this.url = url;
        }
        
        @Override
        public String getURL() {
            return url;
        }

    }

}
