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
package com.adobe.cq.wcm.core.components.internal.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.jcr.RepositoryException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.Modification;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.internal.models.v1.PanelContainerImpl;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContainerPostProcessorTest {

    ContainerPostProcessor containerPostProcessor = new ContainerPostProcessor();

    private final AemContext context = new AemContext(ResourceResolverType.JCR_MOCK);
    private static final String PARAM_ORDERED_CHILDREN = "itemOrder";
    private static final String PARAM_DELETED_CHILDREN = "deletedItems";

    @Test
    public void testOrderChildren() throws IOException, RepositoryException {
        context.create().resource("/test/dummy/container");
        context.create().resource("/test/dummy/container/child1");
        context.create().resource("/test/dummy/container/child2");
        String[] reorderedChildren = new String[]{"child2","child1","nonExistingChild"};
        context.request().setParameterMap(ImmutableMap.of(PARAM_ORDERED_CHILDREN,
                String.join(",", reorderedChildren)));
        containerPostProcessor.handleOrder(context.currentResource("/test/dummy/container"), context.request());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        Iterator<String> expectedIterator = Arrays.asList(reorderedChildren).iterator();
        assertEquals(2, childList.size());
        // compare new order with wishlist :)
        for (Resource resource : childList) {
            assertEquals(resource.getName(), expectedIterator.next(), "Reordering children failed");
        }
    }

    @Test
    public void testDeleteChildren() throws IOException, RepositoryException {
        Resource container = context.create().resource("/test/dummy/container");
        context.create().resource("/test/dummy/container/child1");
        context.create().resource("/test/dummy/container/child2");
        context.currentResource(container);
        String deletedChildren = "child2";
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, deletedChildren));
        containerPostProcessor.handleDelete(container, context.request(), context.resourceResolver(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(1, childList.size());
    }

    @Test
    public void testThrowRepositoryException() throws Exception {
        ContainerPostProcessor exceptionPostProcessor = new ContainerPostProcessor(){
            @Override
            protected void handleDelete(Resource container, SlingHttpServletRequest request, ResourceResolver resolver,
                                        List<Modification> addedModifications) throws RepositoryException {
                throw new RepositoryException("Dummy exception to meet code coverage");
            }
        };
        Resource container = context.create().resource("/test/dummy/container", ImmutableMap.of(
                "sling:resourceType", PanelContainerImpl.RESOURCE_TYPE
        ));
        context.create().resource("/test/dummy/container/child1");
        String deletedChildren = "child1";
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, deletedChildren));
        context.currentResource(container);
        exceptionPostProcessor.process(context.request(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(1, childList.size());
    }

    @Test
    public void testProcess() throws Exception {
        Resource container = context.create().resource("/test/dummy/container", ImmutableMap.of(
                "sling:resourceType", PanelContainerImpl.RESOURCE_TYPE
        ));
        context.create().resource("/test/dummy/container/child1");
        String deletedChildren = "child1";
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, deletedChildren));
        context.currentResource(container);
        containerPostProcessor.process(context.request(), new ArrayList<>());
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        List<Resource> childList     = StreamSupport
                .stream(childrenIterator.spliterator(), false)
                .collect(Collectors.toList());
        assertEquals(0, childList.size());
    }

}