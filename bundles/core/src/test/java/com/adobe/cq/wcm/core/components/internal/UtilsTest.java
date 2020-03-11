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
package com.adobe.cq.wcm.core.components.internal;

import org.apache.sling.api.SlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class UtilsTest {

    @Mock
    private Page pageMock;

    @Mock
    private SlingHttpServletRequest slingHttpServletRequestMock;

    @Mock
    private PageManager pageManagerMock;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void getURLExtended_pageNull() {
        String pathSample = "fake/path/for/testing";

        when(this.pageManagerMock.getPage(pathSample))
         .thenReturn(null);

        assertEquals(pathSample, Utils.getURL(this.slingHttpServletRequestMock, this.pageManagerMock, pathSample));
    }

    @Test
    void getURLExtended_vanityUrlEmpty() {
        when(this.pageMock.getVanityUrl())
         .thenReturn("");
        when(this.pageMock.getPath())
         .thenReturn("testPage");
        when(this.slingHttpServletRequestMock.getContextPath())
         .thenReturn("/fake/path/for/testing/");

        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
    }

    @Test
    void getURLExtended_vanityUrlCorrect() {
        when(this.pageMock.getVanityUrl())
         .thenReturn("testPage.html");
        when(this.slingHttpServletRequestMock.getContextPath())
         .thenReturn("/fake/path/for/testing/");

        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
    }
}
