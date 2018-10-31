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
package com.adobe.cq.wcm.core.components.internal.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Carousel;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CarouselImplTest {

    private static final String TEST_BASE = "/carousel";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/carousel";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CAROUSEL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/carousel-1";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void init() {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    public void testEmptyCarousel() {
        Carousel carousel = new CarouselImpl();
        List<ListItem> items = carousel.getItems();
        assertTrue("", items == null || items.size() == 0);
    }

    @Test
    public void testCarouselWithItems() {
        Carousel carousel = getCarouselUnderTest(CAROUSEL_1);
        Object[][] expectedItems = {
            {"/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_1", "Teaser 1", "Teaser 1 description"},
            {"/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_2", "Teaser 2", "Teaser 2 description"},
        };
        verifyCarouselItems(expectedItems, carousel.getItems());
        //Utils.testJSONExport(carousel, Utils.getTestExporterJSONPath(TEST_BASE, "carousel1"));
    }

    @Test
    public void testCarouselProperties() {
        Carousel carousel = getCarouselUnderTest(CAROUSEL_1);
        assertTrue(carousel.getAutoplay());
        assertEquals(new Long(7000), carousel.getDelay());
        assertTrue(carousel.getAutopauseDisabled());
    }

    private Carousel getCarouselUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        Style style = mock(Style.class);
        when(style.get(any(), any(Object.class))).thenAnswer(
            invocation -> invocation.getArguments()[1]
        );
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Carousel.class);
    }

    private void verifyCarouselItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("The carousel contains a different number of items than expected.", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("The carousel item's path is not what was expected.",
                expectedItems[index][0], item.getPath());
            assertEquals("The carousel item's title is not what was expected: " + item.getTitle(),
                expectedItems[index][1], item.getTitle());
            assertEquals("The carousel item's description is not what was expected: " + item.getDescription(),
                expectedItems[index][2], item.getDescription());
            index++;
        }
    }
}
