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

import org.mockito.Mockito;
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
    // resolveResource method
    ResourceResolver resourceResolverMock;
    Resource resourceMock;
    String pathMock;
    String[] searchPathMock;

    // getTemplateResourceTypes method
    Page pageMock;
    Template templateMock;
    String resourceTypeRegexMock;
    Set<String> resourceTypesMock;

    // getURL method
    SlingHttpServletRequest slingHttpServletRequestMock;
    PageManager pageManagerMock;

    @BeforeEach
    void setUp() {
        // resolveResource method
        this.resourceResolverMock = Mockito.mock(ResourceResolver.class);
        this.resourceMock = Mockito.mock(Resource.class);

        // getTemplateResourceTypes method
        this.pageMock = Mockito.mock(Page.class);
        this.templateMock = Mockito.mock(Template.class);

        // getURL method
        this.slingHttpServletRequestMock = Mockito.mock(SlingHttpServletRequest.class);
        this.pageManagerMock = Mockito.mock(PageManager.class);
    }

    @Test
    public void resolveResource_pathStartsWithSlash() {
        this.pathMock = "/fake/path/for/testing";

        when(this.resourceResolverMock.getResource(this.pathMock))
         .thenReturn(this.resourceMock);

        assertEquals(this.resourceMock, Utils.resolveResource(this.resourceResolverMock, this.pathMock));
    }

    @Test
    public void resolveResource_pathStartsWithoutSlashResourceNull() {
        this.pathMock = "fake/path/for/testing";
        this.searchPathMock = new String[] { "/base/fake/path" };

        when(this.resourceResolverMock.getSearchPath())
         .thenReturn(this.searchPathMock);
        when(this.resourceResolverMock.getResource(this.searchPathMock[0] + this.pathMock))
         .thenReturn(null);

        assertEquals(null, Utils.resolveResource(this.resourceResolverMock, this.pathMock));
    }

    @Test
    public void resolveResource_pathStartsWithoutSlashResourceNotNull() {
        this.pathMock = "fake/path/for/testing";
        this.searchPathMock = new String[] { "/base/fake/path" };

        when(this.resourceResolverMock.getSearchPath())
         .thenReturn(this.searchPathMock);
        when(this.resourceResolverMock.getResource(this.searchPathMock[0] + this.pathMock))
         .thenReturn(this.resourceMock);

        assertEquals(this.resourceMock, Utils.resolveResource(this.resourceResolverMock, this.pathMock));
    }

    @Test
    public void getTemplateResourceTypes_pageNull() {
        this.resourceTypesMock = new HashSet<String>();

        this.resourceTypesMock.add("dummyType");
        this.resourceTypesMock.add("fakeType");

        when(this.pageMock.getTemplate())
         .thenReturn(null);

        assertEquals(this.resourceTypesMock, Utils.getTemplateResourceTypes(this.pageMock, this.resourceTypeRegexMock, this.resourceResolverMock, this.resourceTypesMock));
    }

    @Test
    public void getTemplateResourceTypes_resourceNull() {
        this.pathMock = "/fake/path/for/testing";
        this.resourceTypesMock = new HashSet<String>();

        this.resourceTypesMock.add("dummyType");
        this.resourceTypesMock.add("fakeType");

        when(this.pageMock.getTemplate())
         .thenReturn(this.templateMock);
        when(this.templateMock.getPath())
         .thenReturn(this.pathMock);
        when(this.resourceResolverMock.getResource(this.pathMock + AllowedComponentList.STRUCTURE_JCR_CONTENT))
         .thenReturn(null);

        assertEquals(this.resourceTypesMock, Utils.getTemplateResourceTypes(this.pageMock, this.resourceTypeRegexMock, this.resourceResolverMock, this.resourceTypesMock));
    }

    @Test
    public void getResourceTypes_resourceNull() {
        assertEquals(null, Utils.getResourceTypes(null, this.resourceTypeRegexMock, this.resourceTypesMock));
    }

    @Test
    public void getResourceTypes_resourceNotNull() {
        this.resourceTypesMock = new HashSet<String>();

        this.resourceTypesMock.add("dummyType");

        when(this.resourceMock.getResourceType())
         .thenReturn("fakeType");

        Set<String> resourceTypesResult = new HashSet<String>();

        resourceTypesResult.add("dummyType");
        resourceTypesResult.add("fakeType");

        assertEquals(resourceTypesResult, Utils.getResourceTypes(this.resourceMock, this.resourceTypeRegexMock, this.resourceTypesMock));
    }

    @Test
    public void getURLExtended_pageNull() {
        this.pathMock = "fake/path/for/testing";

        when(this.pageManagerMock.getPage(this.pathMock))
         .thenReturn(null);

        assertEquals(this.pathMock, Utils.getURL(this.slingHttpServletRequestMock, this.pageManagerMock, this.pathMock));
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
    public void getURLExtended_vanityUrlNotEmpty() {
        when(this.pageMock.getVanityUrl())
         .thenReturn("testPage.html");
        when(this.slingHttpServletRequestMock.getContextPath())
         .thenReturn("/fake/path/for/testing/");

        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
    }
}
