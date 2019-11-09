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

import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.day.cq.wcm.foundation.AllowedComponentList;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import java.util.Set;
import java.util.HashSet;

public class UtilsTest {
    @Mock
    ResourceResolver resourceResolverMock;
    @Mock
    Resource resourceMock;
    @Mock
    Page pageMock;
    @Mock
    Template templateMock;
    @Mock
    SlingHttpServletRequest slingHttpServletRequestMock;
    @Mock
    PageManager pageManagerMock;

    String pathSample;
    String[] searchPathSample;
    String resourceTypeRegexSample;
    Set<String> resourceTypesSample;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    public void resolveResource_pathStartsWithSlash() {
        this.pathSample = "/fake/path/for/testing";

        when(this.resourceResolverMock.getResource(this.pathSample))
         .thenReturn(this.resourceMock);

        assertEquals(this.resourceMock, Utils.resolveResource(this.resourceResolverMock, this.pathSample));
    }

    @Test
    public void resolveResource_pathStartsWithoutSlashResourceNull() {
        this.pathSample = "fake/path/for/testing";
        this.searchPathSample = new String[] { "/base/fake/path" };

        when(this.resourceResolverMock.getSearchPath())
         .thenReturn(this.searchPathSample);
        when(this.resourceResolverMock.getResource(this.searchPathSample[0] + this.pathSample))
         .thenReturn(null);

        assertEquals(null, Utils.resolveResource(this.resourceResolverMock, this.pathSample));
    }

    @Test
    public void resolveResource_pathStartsWithoutSlashResourceNotNull() {
        this.pathSample = "fake/path/for/testing";
        this.searchPathSample = new String[] { "/base/fake/path" };

        when(this.resourceResolverMock.getSearchPath())
         .thenReturn(this.searchPathSample);
        when(this.resourceResolverMock.getResource(this.searchPathSample[0] + this.pathSample))
         .thenReturn(this.resourceMock);

        assertEquals(this.resourceMock, Utils.resolveResource(this.resourceResolverMock, this.pathSample));
    }

    @Test
    public void getTemplateResourceTypes_pageNull() {
        this.resourceTypesSample = new HashSet<String>();

        this.resourceTypesSample.add("dummyType");
        this.resourceTypesSample.add("fakeType");

        when(this.pageMock.getTemplate())
         .thenReturn(null);

        assertEquals(this.resourceTypesSample, Utils.getTemplateResourceTypes(this.pageMock, this.resourceTypeRegexSample, this.resourceResolverMock, this.resourceTypesSample));
    }

    @Test
    public void getTemplateResourceTypes_resourceNull() {
        this.pathSample = "/fake/path/for/testing";
        this.resourceTypesSample = new HashSet<String>();

        this.resourceTypesSample.add("dummyType");
        this.resourceTypesSample.add("fakeType");

        when(this.pageMock.getTemplate())
         .thenReturn(this.templateMock);
        when(this.templateMock.getPath())
         .thenReturn(this.pathSample);
        when(this.resourceResolverMock.getResource(this.pathSample + AllowedComponentList.STRUCTURE_JCR_CONTENT))
         .thenReturn(null);

        assertEquals(this.resourceTypesSample, Utils.getTemplateResourceTypes(this.pageMock, this.resourceTypeRegexSample, this.resourceResolverMock, this.resourceTypesSample));
    }

    @Test
    public void getResourceTypes_resourceNull() {
        assertEquals(null, Utils.getResourceTypes(null, this.resourceTypeRegexSample, this.resourceTypesSample));
    }

    @Test
    public void getResourceTypes_resourceCorrect() {
        this.resourceTypesSample = new HashSet<String>();

        this.resourceTypesSample.add("dummyType");

        when(this.resourceMock.getResourceType())
         .thenReturn("fakeType");

        Set<String> resourceTypesResult = new HashSet<String>();

        resourceTypesResult.add("dummyType");
        resourceTypesResult.add("fakeType");

        assertEquals(resourceTypesResult, Utils.getResourceTypes(this.resourceMock, this.resourceTypeRegexSample, this.resourceTypesSample));
    }

    @Test
    public void getURLExtended_pageNull() {
        this.pathSample = "fake/path/for/testing";

        when(this.pageManagerMock.getPage(this.pathSample))
         .thenReturn(null);

        assertEquals(this.pathSample, Utils.getURL(this.slingHttpServletRequestMock, this.pageManagerMock, this.pathSample));
    }

    @Test
    public void getURLExtended_vanityUrlEmpty() {
        when(this.pageMock.getVanityUrl())
         .thenReturn("");
        when(this.pageMock.getPath())
         .thenReturn("testPage");
        when(this.slingHttpServletRequestMock.getContextPath())
         .thenReturn("/fake/path/for/testing/");

        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
    }

    @Test
    public void getURLExtended_vanityUrlCorrect() {
        when(this.pageMock.getVanityUrl())
         .thenReturn("testPage.html");
        when(this.slingHttpServletRequestMock.getContextPath())
         .thenReturn("/fake/path/for/testing/");

        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
    }
}
