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
import java.util.Iterator;

import javax.servlet.ServletException;

import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.LiveStatus;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.reflection.FieldSetter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class ContainerServletTest {
    // root folder in resources
    private static final String TEST_BASE = "/carousel";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH = "/content/carousel/jcr:content/root/responsivegrid/carousel-1";
    // path to live copy resource
    private static final String LIVE_COPY_PATH = "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_4";
    // ghost resource type
    private static final String RT_GHOST = "wcm/msm/components/ghost";

    // request parameter for deleting one or multiple children
    private static final String PARAM_DELETED_CHILDREN = "delete";
    // request parameter for ordering children
    private static final String PARAM_ORDERED_CHILDREN = "order";
    // servlet under test
    private final ContainerServlet servlet = new ContainerServlet();

    public final AemContext context = new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_OAK).build();

    @BeforeEach
    public void setUp() throws WCMException, NoSuchFieldException {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        // make the carousel component the current resource
        context.currentResource(CAROUSEL_PATH);
        // set http method
        context.request().setMethod("POST");
        // live relationship manager
        LiveRelationshipManager liveRelationshipManager = mock(LiveRelationshipManager.class);
        when(liveRelationshipManager.getLiveRelationship(any(Resource.class), anyBoolean())).then(
            invocation -> {
                Object[] arguments = invocation.getArguments();
                Resource resource = (Resource) arguments[0];
                if (LIVE_COPY_PATH.equals(resource.getPath())) {
                    LiveRelationship liveRelationship = mock(LiveRelationship.class);
                    LiveStatus liveStatus = mock(LiveStatus.class);
                    when(liveStatus.isSourceExisting()).thenReturn(true);
                    when(liveRelationship.getStatus()).thenReturn(liveStatus);
                    return liveRelationship;
                }
                return null;
            }
        );
        FieldSetter.setField(servlet, servlet.getClass().getDeclaredField("liveRelationshipManager"), liveRelationshipManager);
    }

    /**
     * Delete one child.
     */
    @Test
    public void testDeleteOneChild() throws ServletException, IOException {
        // set param to delete one item
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, new String[]{"item_1"}));
        servlet.doPost(context.request(), context.response());
        assertNull(context.currentResource().getChild("item_1"), "Deleted child 'item_1' still exists");
    }

    /**
     * Delete multiple children.
     */
    @Test
    public void testDeleteMultipleChildren() throws ServletException, IOException {
        // set param to delete 2 items
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, new String[]{"item_1","item_3"}));
        servlet.doPost(context.request(), context.response());
        assertNull(context.currentResource().getChild("item_1"), "Deleted child 'item_1' still exists");
        assertNotNull(context.currentResource().getChild("item_2"), "Child 'item_2' was deleted but should still exist");
        assertNull(context.currentResource().getChild("item_3"), "Deleted child 'item_3' still exists");
    }

    /**
     * Delete a child that is a live copy.
     */
    @Test
    public void testDeleteLiveCopyChild() throws ServletException, IOException {
        // set param to delete a child that is a live copy.
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, new String[]{"item_4"}));
        servlet.doPost(context.request(), context.response());
        Resource resource = context.currentResource().getChild("item_4");
        assertNotNull(resource, "Child 'item_4' was deleted but should still exist");
        assertEquals(resource.getResourceType(), RT_GHOST, "Child 'item_4' does not have a ghost resource type");
    }

    /**
     * Edge Case : Delete a non-existing child.
     */
    @Test
    public void testDeleteUnknownChild() throws ServletException, IOException {
        // set param to non-existing child name
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, new String[]{"item_XXXX"}));
        servlet.doPost(context.request(), context.response());
    }

    /**
     * Edge Case : Send an empty list of deleted children.
     */
    @Test
    public void testDeleteEmptyParam() throws ServletException, IOException {
        // send an empty list
        context.request().setParameterMap(ImmutableMap.of(PARAM_DELETED_CHILDREN, new String[]{}));
        servlet.doPost(context.request(), context.response());
    }

    /**
     * Reorder children.
     */
    @Test
    public void testOrderChildren() throws ServletException, IOException {
        // define the new order
        String[] reorderedChildren = new String[]{"item_3","item_2","item_1","item_4"};
        // set the param
        context.request().setParameterMap(ImmutableMap.of(PARAM_ORDERED_CHILDREN, reorderedChildren));
        // make the request
        servlet.doPost(context.request(), context.response());
        // get iterators
        Iterable<Resource> childrenIterator = context.currentResource().getChildren();
        Iterator<String> expectedIterator = Arrays.asList(reorderedChildren).iterator();
        // compare new order with wishlist :)
        for (Resource resource : childrenIterator) {
            assertEquals(resource.getName(), expectedIterator.next(), "Reordering children failed");
        }
    }
}
