/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

package com.adobe.cq.wcm.core.components.sandbox.ui;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.jackrabbit.oak.plugins.document.NodeDocument;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class ChildrenEditorTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChildrenEditorTest.class);
    // root folder in resources
    private static final String TEST_BASE = "/carousel";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH =
        "/content/carousel/jcr:content/root/responsivegrid/carousel-1";


    // content used for testing
    @Rule
    public AemContext AEM_CONTEXT  = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    /**
     * Test getChildren() method.
     */
    @Test
    public void testGetChildren() {
        ChildrenEditor childEdit = getChildrenEditor(CAROUSEL_PATH);
        List<Resource> children = childEdit.getChildren();
        assertEquals("Number of children is not the same",3,children.size());
        Iterator<String> it= Arrays.asList("item_1","item_2","item_3").iterator();
        for (Iterator<Resource> it1 = children.iterator(); it1.hasNext(); ) {
            Resource child = it1.next();
            assertEquals("Child not found!", child.getName(),it.next());
        }
    }

    /**
     * Test getContainer() method.
     */
    @Test
    public void testGetContainer() {
        ChildrenEditor childEdit = getChildrenEditor(CAROUSEL_PATH);
        Resource r = childEdit.getContainer();
        Iterator<String> it= Arrays.asList("item_1","item_2","item_3").iterator();
        for(Resource child: r.getChildren()){
            assertEquals("Child not found!", child.getName(),it.next());
        }
    }

    /**
     * Test with an empty suffix
     */
    @Test
    public void testEmptySuffix() {
        ChildrenEditor childEdit = getChildrenEditor("");
        Resource r = childEdit.getContainer();
        assertNull("Container should be null for empty suffix!",r);
    }

    /**
     * Test with an invalid suffix
     */
    @Test
    public void testInvalidSuffix() {
        ChildrenEditor childEdit = getChildrenEditor("/asdf/adf/asdf");
        Resource r = childEdit.getContainer();
        assertNull("Container should be null for invalid suffix!",r);
    }

    private ChildrenEditor getChildrenEditor(String suffix){
        // get the carousel component node resource
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(CAROUSEL_PATH);

        // prepare request object
        MockSlingHttpServletRequest req  = AEM_CONTEXT.request();
        // set the suffix
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) req.getRequestPathInfo();
        requestPathInfo.setSuffix(suffix);
        // define the bindings
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        req.setAttribute(SlingBindings.class.getName(), slingBindings);
        // adapt to the class to test
        return req.adaptTo(ChildrenEditor.class);

    }
}
