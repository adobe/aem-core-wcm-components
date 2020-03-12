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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.HashMap;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.resourceresolver.MockValueMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.testing.MockSlingModelFilter;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class LayoutContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_PAGE = "/content/container";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String CONTAINER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-2";
    private static final String CONTAINER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-3";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @Rule
    public final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Before
    public void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        AEM_CONTEXT.registerService(SlingModelFilter.class, new MockSlingModelFilter());
    }

    @Test
    public void testContainerWithPropertiesAndPolicy() {
        Resource mockResource = mock(Resource.class);
        LayoutContainer container = getContainerUnderTest(CONTAINER_1, new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap() {{
            put(Container.PN_BACKGROUND_IMAGE_ENABLED, true);
            put(Container.PN_BACKGROUND_COLOR_ENABLED, true);
        }})));
        assertEquals("Style mismatch",
                "background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;",
                container.getBackgroundStyle());
        assertEquals("Layout type mismatch",
                LayoutContainer.LayoutType.RESPONSIVE_GRID,
                container.getLayout());
        assertEquals("ID mismatch",
                "test",
                container.getId());
        Object[][] expectedItems = {
                {"item_1", "Teaser 1"},
                {"item_2", "Teaser 2"}
        };
        verifyContainerItems(expectedItems, container.getItems());
        assertEquals("Exported type mismatch",
                "core/wcm/components/container/v1/container",
                container.getExportedType());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container1"));
    }

    @Test
    public void testContainerNoProperties() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_2, null);
        assertEquals("ID mismatch", "container-2611f8dc62", container.getId());
        assertNull("Style", container.getBackgroundStyle());
        assertEquals("Layout type mismatch",
                LayoutContainer.LayoutType.SIMPLE,
                container.getLayout());
    }

    @Test
    public void testContainerWithPropertiesAndNoPolicy() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_3, null);
        assertEquals("ID mismatch", "container-d7eba9c61f", container.getId());
        assertNull("Style", container.getBackgroundStyle());
    }

    private void verifyContainerItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("Item number mismatch", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("Item title mismatch", expectedItems[index][1], item.getTitle());
            index++;
        }
    }

    private LayoutContainer getContainerUnderTest(String resourcePath, Style style) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }

        SlingBindings bindings = new SlingBindings();
        ComponentContext componentContext = mock(ComponentContext.class);
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(SlingBindings.REQUEST, AEM_CONTEXT.request());
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        bindings.put(WCMBindings.CURRENT_PAGE, AEM_CONTEXT.pageManager().getPage(TEST_PAGE));
        bindings.put(WCMBindings.COMPONENT_CONTEXT, componentContext);
        bindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        if (style == null) {
            Resource mockResource = mock(Resource.class);
            style = new MockStyle(mockResource, new MockValueMap(mockResource, new HashMap()));
        }
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        AEM_CONTEXT.request().setAttribute(SlingBindings.class.getName(), bindings);

        AEM_CONTEXT.currentResource(resource);
        AEM_CONTEXT.request().setContextPath(CONTEXT_PATH);
        return AEM_CONTEXT.request().adaptTo(LayoutContainer.class);
    }
}
