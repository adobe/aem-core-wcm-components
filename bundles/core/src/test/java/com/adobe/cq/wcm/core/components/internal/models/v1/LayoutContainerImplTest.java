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

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class LayoutContainerImplTest {

    private static final String TEST_BASE = "/container";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = "/content/container";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CONTAINER_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-1";
    private static final String CONTAINER_2 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-2";
    private static final String CONTAINER_3 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-3";
    private static final String CONTAINER_4 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/container-4";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void init() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    public void testContainerWithPropertiesAndPolicy() {
        context.contentPolicyMapping(LayoutContainerImpl.RESOURCE_TYPE_V1, new HashMap<String, Object>() {{
            put(Container.PN_BACKGROUND_IMAGE_ENABLED, true);
            put(Container.PN_BACKGROUND_COLOR_ENABLED, true);
            put(LayoutContainer.PN_LAYOUT, "simple");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_1);

        assertEquals(
            "background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain-range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;",
            container.getBackgroundStyle(),
            "Style mismatch");
        // layout set in component properties
        assertEquals(LayoutContainer.LayoutType.RESPONSIVE_GRID, container.getLayout(), "Layout type mismatch");
        assertEquals("test", container.getId(), "ID mismatch");
        Object[][] expectedItems = {
                {"item_1", "Teaser 1"},
                {"item_2", "Teaser 2"}
        };
        verifyContainerItems(expectedItems, container.getItems());
        assertEquals("core/wcm/components/container/v1/container", container.getExportedType(), "Exported type mismatch");
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container1"));
    }

    @Test
    public void testContainerWithPropertiesAndLayoutInPolicy() {
        context.contentPolicyMapping(LayoutContainerImpl.RESOURCE_TYPE_V1, new HashMap<String, Object>() {{
            put(LayoutContainer.PN_LAYOUT, "responsiveGrid");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);

        // layout set in content policy
        assertEquals(LayoutContainer.LayoutType.RESPONSIVE_GRID, container.getLayout(), "Layout type mismatch");
        assertEquals("core/wcm/components/container/v1/container", container.getExportedType(), "Exported type mismatch");
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container2"));
    }

    @Test
    public void testContainerNoProperties() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);
        assertEquals("container-2611f8dc62", container.getId(), "ID mismatch");
        assertNull(container.getBackgroundStyle(), "Style");
        assertEquals(LayoutContainer.LayoutType.SIMPLE, container.getLayout(), "Layout type mismatch");
    }

    @Test
    public void testContainerWithPropertiesAndNoPolicy() {
        LayoutContainer container = getContainerUnderTest(CONTAINER_3);
        assertEquals("container-d7eba9c61f", container.getId(), "ID mismatch");
        assertNull(container.getBackgroundStyle(), "Style");
    }

    @Test
    public void testSpaceEscapingInBackgroundImage() {
        context.contentPolicyMapping(LayoutContainerImpl.RESOURCE_TYPE_V1, new HashMap<String, Object>() {{
            put(Container.PN_BACKGROUND_IMAGE_ENABLED, true);
            put(Container.PN_BACKGROUND_COLOR_ENABLED, true);
            put(LayoutContainer.PN_LAYOUT, "simple");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_4);
        assertEquals("container-ece469fc8b", container.getId(), "ID mismatch");
        assertEquals("background-image:url(/content/dam/core-components-examples/library/sample-assets/mountain%20range.jpg);background-size:cover;background-repeat:no-repeat;background-color:#000000;", container.getBackgroundStyle(), "Style mismatch");
    }

    private void verifyContainerItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals(expectedItems.length, items.size(), "Item number mismatch");
        int index = 0;
        for (ListItem item : items) {
            assertEquals(expectedItems[index][1], item.getTitle(), "Item title mismatch");
            index++;
        }
    }

    private LayoutContainer getContainerUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        Resource resource = context.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }

        context.currentResource(resource);
        return context.request().adaptTo(LayoutContainer.class);
    }

    @Test
    protected void testGetAccessibilityLabel() {
        context.contentPolicyMapping(LayoutContainerImpl.RESOURCE_TYPE_V1, new HashMap<String, Object>() {{
            put(LayoutContainer.PN_LAYOUT, "responsiveGrid");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);
        assertEquals("container", container.getAccessibilityLabel());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container2"));
    }

    @Test
    protected void testGetRoleAttribute() {
        context.contentPolicyMapping(LayoutContainerImpl.RESOURCE_TYPE_V1, new HashMap<String, Object>() {{
            put(LayoutContainer.PN_LAYOUT, "responsiveGrid");
        }});
        LayoutContainer container = getContainerUnderTest(CONTAINER_2);
        assertEquals("main", container.getRoleAttribute());
        Utils.testJSONExport(container, Utils.getTestExporterJSONPath(TEST_BASE, "container2"));
    }
}
