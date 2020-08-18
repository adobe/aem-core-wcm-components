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

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class ContainerServletTest {
    // root folder in resources
    private static final String TEST_BASE = "/carousel";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH = "/content/carousel/jcr:content/root/responsivegrid/carousel-1";

    // request parameter for deleting one or multiple children
    private static final String PARAM_DELETED_CHILDREN = "delete";
    // request parameter for ordering children
    private static final String PARAM_ORDERED_CHILDREN = "order";
    // servlet under test
    private final ContainerServlet servlet = new ContainerServlet();

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        // make the carousel component the current resource
        context.currentResource(CAROUSEL_PATH);
        // set http method
        context.request().setMethod("POST");
    }

    /**
     * Delete one child.
     */
    @Test
    public void testDeleteOneChild() throws ServletException, IOException {
        // set param to delete one item
        context.request().getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_1"});
        servlet.doPost(context.request(), context.response());
        assertNull(context.currentResource().getChild("item_1"), "Deleted child 'item_1' still exists");
    }

    /**
     * Delete multiple children.
     */
    @Test
    public void testDeleteMultipleChildren() throws ServletException, IOException {
        // set param to delete 2 items
        context.request().getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_1","item_3"});
        servlet.doPost(context.request(), context.response());
        assertNull(context.currentResource().getChild("item_1"), "Deleted child 'item_1' still exists");
        assertNotNull(context.currentResource().getChild("item_2"), "Child 'item_2' was deleted but should still exist");
        assertNull(context.currentResource().getChild("item_3"), "Deleted child 'item_3' still exists");
    }

    /**
     * Edge Case : Delete a non-existing child.
     */
    @Test
    public void testDeleteUnknownChild() throws ServletException, IOException {
        // set param to non-existing child name
        context.request().getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_XXXX"});
        servlet.doPost(context.request(), context.response());
    }

    /**
     * Edge Case : Send an empty list of deleted children.
     */
    @Test
    public void testDeleteEmptyParam() throws ServletException, IOException {
        // send an empty list
        context.request().getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{});
        servlet.doPost(context.request(), context.response());
    }

    /**
     * Reorder children.
     */
    @Test
    public void testOrderChildren() throws ServletException, IOException {
        // define the new order
        String[] reorderedChildren = new String[]{"item_3","item_2","item_1"};
        // set the param
        context.request().getParameterMap().put(PARAM_ORDERED_CHILDREN, reorderedChildren);
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
