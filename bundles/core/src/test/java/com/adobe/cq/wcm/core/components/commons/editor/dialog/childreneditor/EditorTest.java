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
package com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.i18n.impl.RootResourceBundle;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@ExtendWith(AemContextExtension.class)
class EditorTest {
    // root folder in resources
    private static final String TEST_BASE = "/commons/editor/dialog/childreneditor";
    // apps root folder
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";
    // root of content
    private static final String CONTENT_ROOT = "/content";
    // path to container node
    private static final String CAROUSEL_PATH =
        "/content/childreneditor/jcr:content/root/responsivegrid/carousel-1";

    private static final RootResourceBundle RESOURCE_BUNDLE = new RootResourceBundle();

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        ResourceBundleProvider resourceBundleProvider = Mockito.mock(ResourceBundleProvider.class);
        context.registerService(ResourceBundleProvider.class, resourceBundleProvider);
        Mockito.when(resourceBundleProvider.getResourceBundle(null)).thenReturn(RESOURCE_BUNDLE);
        Mockito.when(resourceBundleProvider.getResourceBundle(null, null)).thenReturn(RESOURCE_BUNDLE);
    }

    /**
     * Test getItems() method.
     */
    @Test
    void testGetItems() {
        Editor childrenEditor = getItemsEditor(CAROUSEL_PATH);
        List<Item> items = childrenEditor.getItems();
        assertEquals("Number of items is not the same.", 5, items.size());
        Object[][] expectedItems = {
            {"item_1", "Teaser 1", "Teaser (v1)", "image", null, null},
            {"item_2", "Teaser 2", "Teaser Icon Abbreviation", null, null, "Aa"},
            {"item_3", "Teaser 3", "Teaser Icon Auto Abbreviation", null, null, "Te"},
            {"item_4", "Teaser 4", "Teaser Icon SVG", null, "/apps/core/wcm/components/teaserIconSVG/cq:icon.svg", null},
            {"item_5", "Teaser 5", "Teaser Icon PNG", null, "/apps/core/wcm/components/teaserIconPNG/cq:icon.png", null}
        };
        int index = 0;
        for (Item item : items) {
            assertEquals("Item name does not match the expected.", expectedItems[index][0], item.getName());
            assertEquals("Item value does not match the expected.", expectedItems[index][1], item.getValue());
            assertEquals("Item title does not match the expected.", expectedItems[index][2], item.getTitle());
            assertEquals("Item icon name does not match the expected.", expectedItems[index][3], item.getIconName());
            assertEquals("Item icon path does not match the expected.", expectedItems[index][4], item.getIconPath());
            assertEquals("Item icon abbreviation does not match the expected.", expectedItems[index][5], item.getIconAbbreviation());
            index++;
        }
    }

    /**
     * Test getContainer() method.
     */
    @Test
    void testGetContainer() {
        Editor childrenEditor = getItemsEditor(CAROUSEL_PATH);
        Resource r = childrenEditor.getContainer();
        Iterator<String> it = Arrays.asList("item_1", "item_2", "item_3", "item_4", "item_5", "item_6").iterator();
        for (Resource child : r.getChildren()) {
            assertEquals("Child not found.", child.getName(), it.next());
        }
    }

    /**
     * Test with an empty suffix
     */
    @Test
    void testEmptySuffix() {
        Editor childrenEditor = getItemsEditor("");
        Resource resource = childrenEditor.getContainer();
        assertNull("For an empty suffix, expected container resource to be null.", resource);
    }

    /**
     * Test with an invalid suffix
     */
    @Test
    void testInvalidSuffix() {
        Editor childrenEditor = getItemsEditor("/asdf/adf/asdf");
        Resource resource = childrenEditor.getContainer();
        assertNull("For an invalid suffix, expected container resource to be null.", resource);
    }

    private Editor getItemsEditor(String suffix) {
        // get the carousel component node resource
        Resource resource = context.resourceResolver().getResource(CAROUSEL_PATH);
        // prepare the request object
        final MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(context.resourceResolver(), context.bundleContext());
        // set the suffix
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(suffix);
        // define the bindings
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        if (resource != null) {
            slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        }
        slingBindings.put(WCMBindings.PAGE_MANAGER, context.pageManager());
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        // adapt to the class to test
        return request.adaptTo(Editor.class);
    }
}
