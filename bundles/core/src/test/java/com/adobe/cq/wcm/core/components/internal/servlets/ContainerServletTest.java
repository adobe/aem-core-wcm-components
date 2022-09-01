/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.LiveStatus;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class ContainerServletTest {
    // root folder in resources
    private static final String TEST_BASE = "/carousel";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH = "/content/carousel/jcr:content/root/responsivegrid/carousel-1";
    // path to live copy resource
    private static final String LIVE_COPY_PATH = "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_4";

    // request parameter for ordering children
    private static final String PARAM_ORDERED_CHILDREN = "order";
    // servlet under test
    private final ContainerServlet servlet = new ContainerServlet();

    public final AemContext context = new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_OAK).build();

    @Mock
    RequestDispatcher requestDispatcher;

    @BeforeEach
    public void setUp() throws WCMException, NoSuchFieldException {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        // make the carousel component the current resource
        context.currentResource(CAROUSEL_PATH);
    }

    /**
     * Reorder children.
     */
    @Test
    public void testOrderChildren() throws IOException {
        context.request().setMethod("POST");
        // define the new order
        String[] reorderedChildren = new String[]{"item_3","item_2","item_1","item_4"};
        // set the param
        context.request().setParameterMap(ImmutableMap.of(PARAM_ORDERED_CHILDREN, reorderedChildren));
        // make the request
        servlet.doPost(context.request(), context.response());
        // get iterators
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
            .stream(childrenIterator.spliterator(), false)
            .collect(Collectors.toList());
        Iterator<String> expectedIterator = Arrays.asList(reorderedChildren).iterator();
        assertEquals(4, childList.size());
        // compare new order with wishlist :)
        for (Resource resource : childList) {
            assertEquals(resource.getName(), expectedIterator.next(), "Reordering children failed");
        }
    }

    @Test
    public void testGetMultiField() throws ServletException, IOException {
        context.request().setMethod(HttpConstants.METHOD_GET);
        context.request().setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return requestDispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions requestDispatcherOptions) {
                return requestDispatcher;
            }
        });

        servlet.doGet(context.request(), context.response());
        verify(requestDispatcher, times(1)).include(context.request(), context.response());
    }
}
