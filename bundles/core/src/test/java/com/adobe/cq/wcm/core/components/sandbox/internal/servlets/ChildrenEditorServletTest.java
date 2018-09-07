/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.internal.servlets;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import javax.servlet.ServletException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ChildrenEditorServletTest {
    // root folder in resources
    private static final String TEST_BASE = "/carousel";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH =
        "/content/carousel/jcr:content/root/responsivegrid/carousel-1";

    // request parameter for deleting one or multiple children
    private static final String PARAM_DELETED_CHILDREN = "deletedChildren";
    // request parameter for ordering children
    private static final String PARAM_ORDERED_CHILDREN = "orderedChildren";
    // servlet under test
    private ChildrenEditorServlet servlet = new ChildrenEditorServlet();

    // mock request object
    private MockSlingHttpServletRequest request;
    // mock request response
    private MockSlingHttpServletResponse response;

    @Rule
    public AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Before
    public void setUp() {
        // make the carousel component the current resource
        AEM_CONTEXT.currentResource(CAROUSEL_PATH);
        // prepare request and response
        request = AEM_CONTEXT.request();
        response = AEM_CONTEXT.response();
        // set http method
        request.setMethod("POST");
    }

    /**
     * Delete one child.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDeleteOneChild() throws ServletException, IOException {
        // set param to delete one item
        request.getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_1"});
        servlet.doPost(request, response);
        assertNull("Deleted child 'item_1' still exists", AEM_CONTEXT.currentResource().getChild("item_1"));
    }

    /**
     * Delete multiple children.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testDeleteMultipleChildren() throws ServletException, IOException {
        // set param to delete 2 items
        request.getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_1","item_3"});
        servlet.doPost(request, response);
        assertNull("Deleted child 'item_1' still exists", AEM_CONTEXT.currentResource().getChild("item_1"));
        assertNotNull("Child 'item_2' was deleted but should still exist", AEM_CONTEXT.currentResource().getChild("item_2"));
        assertNull("Deleted child 'item_3' still exists", AEM_CONTEXT.currentResource().getChild("item_3"));
    }

    /**
     * Edge Case : Delete a non-existing child.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test(expected = Test.None.class /* no exception expected */)
    public void testDeleteUnkownChild() throws ServletException, IOException {
        // set param to non-existing child name
        request.getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{"item_XXXX"});
        servlet.doPost(request, response);
    }

    /**
     * Edge Case : Send an empty list of deleted children.
     *
     * @throws ServletException
     * @throws IOException
     */
    @Test(expected = Test.None.class /* no exception expected */)
    public void testDeleteEmptyParam() throws ServletException, IOException {
        // send an empty list
        request.getParameterMap().put(PARAM_DELETED_CHILDREN, new String[]{});
        servlet.doPost(request, response);
    }

    /**
     * Reorder children.
     * @throws ServletException
     * @throws IOException
     */
    @Test
    public void testReorderChildren() throws ServletException, IOException {
        // define the new order
        String[] reorderedChildren = new String[]{"item_3","item_2","item_1"};
        // set the param
        request.getParameterMap().put(PARAM_ORDERED_CHILDREN, reorderedChildren);
        // make the request
        servlet.doPost(request, response);
        // get iterators
        Iterable<Resource> childrenIterator = AEM_CONTEXT.currentResource().getChildren();
        Iterator<String> expectedIterator = Arrays.asList(reorderedChildren).iterator();
        // compare new order with wishlist :)
        for (Resource resource : childrenIterator) {
            assertEquals("Reordering children failed", resource.getName(), expectedIterator.next());
        }
    }
}
