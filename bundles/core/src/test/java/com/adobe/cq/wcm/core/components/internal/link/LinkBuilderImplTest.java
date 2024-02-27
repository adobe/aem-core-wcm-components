/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class})
class LinkBuilderImplTest {

    public final AemContext context = new AemContext();
    private PathProcessor pathProcessor = mock(PathProcessor.class);

    @BeforeEach
    void setup() {
        when(pathProcessor.accepts(any(), any())).thenReturn(Boolean.TRUE);
        when(pathProcessor.map(any(), any())).then(returnsFirstArg());
        when(pathProcessor.sanitize(any(), any())).then(returnsFirstArg());
        when(pathProcessor.externalize(any(), any())).then(returnsFirstArg());
    }

    @Test
    void testEmptyLink() {
        Link link = new LinkBuilderImpl("" ,context.request(), Collections.singletonList(pathProcessor), false)
            .build();

        assertNotNull(link);
        assertNull(link.getExternalizedURL());
        assertNull(link.getURL());
        assertNull(link.getMappedURL());
        assertFalse(link.isValid());

        verify(pathProcessor, never()).map(any(), any());
        verify(pathProcessor, never()).externalize(any(), any());
        verify(pathProcessor, never()).sanitize(any(), any());
    }

    @ParameterizedTest
    @CsvSource({
        "/content/path/to/page, /content/path/to/page.html, /content/path/to/page.html",
        "/content/path/to/page.html, /content/path/to/page.html,",
        // with Adobe Campaign expressions
        "/content/path/to/page.html?recipient=<%= recipient.id %>, /content/path/to/page.html?recipient=<%= recipient.id %>,",
        // with Adobe Campaign expressions and other encoded character sequences
        "https://foo.bar/%E9%A1%B5.html?recipient=<%= recipient.id %>, https://foo.bar/页.html?recipient=<%= recipient.id %>, https://foo.bar/页.html?recipient=<%= recipient.id %>",
        // malformed url
        "/content/path/to/page.html?campaign=%%PLACEHOLDER%%, /content/path/to/page.html?campaign=%%PLACEHOLDER%%,",
    })
    void testLinkToPage(String given, String expected, String passedDownToPathProcessor) {
        context.create().page("/content/path/to/page");

        Link link = new LinkBuilderImpl(given, context.request(), Collections.singletonList(pathProcessor), false)
            .build();

        assertNotNull(link);
        assertEquals(expected, link.getURL());
        assertEquals(expected, link.getMappedURL());
        assertEquals(expected, link.getExternalizedURL());

        passedDownToPathProcessor = StringUtils.defaultIfEmpty(passedDownToPathProcessor, given);

        // external links are not mapped
        if (!LinkManagerImpl.isExternalLink(passedDownToPathProcessor)) {
            verify(pathProcessor).map(eq(passedDownToPathProcessor), any());
        }
        verify(pathProcessor).externalize(eq(passedDownToPathProcessor), any());
        verify(pathProcessor).sanitize(eq(passedDownToPathProcessor), any());
    }
}
