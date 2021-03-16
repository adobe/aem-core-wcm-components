/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.internal.link;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class LinkTestUtils {

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL) {
        assertTrue(link.isValid(), "linkValid");
        assertEquals(linkURL, link.getURL(), "linkURL");
        assertEquals(ImmutableMap.of("href", linkURL), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertValidLink(@NotNull Link link, @NotNull String linkURL, @Nullable String linkTarget) {
        if (linkTarget == null) {
            assertValidLink(link,  linkURL);
            return;
        }
        assertTrue(link.isValid(), "linkValid");
        assertEquals(linkURL, link.getURL(), "linkURL");
        assertEquals(ImmutableMap.of("href", linkURL, "target", linkTarget), link.getHtmlAttributes(), "linkHtmlAttributes");
    }

    public static void assertInvalidLink(@NotNull Link link) {
        assertFalse(link.isValid(), "linkValid");
        assertNull(link.getURL(), "linkURL");
        assertNotNull(link.getHtmlAttributes(), "linkHtmlAttributes not null");
        assertTrue(link.getHtmlAttributes().isEmpty(), "linkHtmlAttributes empty");
    }

}
