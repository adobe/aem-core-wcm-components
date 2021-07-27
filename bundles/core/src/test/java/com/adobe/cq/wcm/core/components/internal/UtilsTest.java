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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
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

//    @Test
//    void getURLExtended_pageNull() {
//        String pathSample = "fake/path/for/testing";
//
//        when(this.pageManagerMock.getPage(pathSample))
//         .thenReturn(null);
//
//        assertEquals(pathSample, Utils.getURL(this.slingHttpServletRequestMock, this.pageManagerMock, pathSample));
//    }
//
//    @Test
//    void getURLExtended_vanityUrlEmpty() {
//        when(this.pageMock.getVanityUrl())
//         .thenReturn("");
//        when(this.pageMock.getPath())
//         .thenReturn("testPage");
//        when(this.slingHttpServletRequestMock.getContextPath())
//         .thenReturn("/fake/path/for/testing/");
//
//        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
//    }
//
//    @Test
//    void getURLExtended_vanityUrlCorrect() {
//        when(this.pageMock.getVanityUrl())
//         .thenReturn("testPage.html");
//        when(this.slingHttpServletRequestMock.getContextPath())
//         .thenReturn("/fake/path/for/testing/");
//
//        assertEquals("/fake/path/for/testing/testPage.html", Utils.getURL(this.slingHttpServletRequestMock, this.pageMock));
//    }

    @Test
    void testGetStrings() {
        Set<String> reference = new LinkedHashSet<String>() {{
            add("test1");
            add("test2");
        }};

        // Test collection
        List<String> list = new LinkedList<String>() {{
            add("test1");
            add("test2");
            add("test1");
        }};
        assertEquals(reference, Utils.getStrings(list));


        List<Object> objectList = new LinkedList<Object>() {{
            add(new Object() {
                @Override
                public String toString() {
                    return "test1";
                }
            });
            add(new Object() {
                @Override
                public String toString() {
                    return "test2";
                }
            });
        }};
        assertEquals(reference, Utils.getStrings(objectList));

        // Test array
        String[] array = new String[] {"test2", "test2", "test1"};
        assertEquals(reference, Utils.getStrings(array));

        // Test CSV
        String csv = "test1, test2,test1 ,test2";
        assertEquals(reference, Utils.getStrings(csv));

        // Test single string
        reference = new HashSet<String>() {{
            add("test");
        }};
        assertEquals(reference, Utils.getStrings("test"));

        // Test null
        assertEquals(new LinkedHashSet<String>(), Utils.getStrings(null));
    }

    @Test
    public void testGetPropertyOrStyleReturnsProperty() {
        Resource resource = mock(Resource.class);
        ValueMap properties = mock(ValueMap.class);

        when(resource.getValueMap()).thenReturn(properties);
        when(properties.get("foo", String.class)).thenReturn("bar");

        assertEquals("bar", Utils.getPropertyOrStyle(resource, "foo", String.class));
    }

    @Test
    public void testGetPropertyOrStyleReturnsStyle() {
        Resource resource = mock(Resource.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        Designer designer = mock(Designer.class);
        Style style = mock(Style.class);

        when(resource.getValueMap()).thenReturn(ValueMap.EMPTY);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
        when(designer.getStyle(resource)).thenReturn(style);
        when(style.get("foo", String.class)).thenReturn("bar");

        assertEquals("bar", Utils.getPropertyOrStyle(resource, "foo", String.class));
    }

    @Test
    public void testGetPropertyOrStyleReturnsNothingWhenStyleMissing() {
        Resource resource = mock(Resource.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        Designer designer = mock(Designer.class);
        Style style = mock(Style.class);

        when(resource.getValueMap()).thenReturn(ValueMap.EMPTY);
        when(resource.getResourceResolver()).thenReturn(resourceResolver);

        // Designer missing
        assertNull(Utils.getPropertyOrStyle(resource, "foo", String.class));

        // Style missing
        when(resourceResolver.adaptTo(Designer.class)).thenReturn(designer);
        assertNull(Utils.getPropertyOrStyle(resource, "foo", String.class));

        // Value missing
        when(designer.getStyle(resource)).thenReturn(style);
        assertNull(Utils.getPropertyOrStyle(resource, "foo", String.class));
    }
}
