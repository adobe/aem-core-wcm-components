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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Objects;

import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Carousel;
import com.day.cq.wcm.api.components.Component;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(AemContextExtension.class)
class CarouselImplTest extends AbstractPanelTest {

    private static final String TEST_BASE = "/carousel";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = "/content/carousel";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CAROUSEL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/carousel-1";
    private static final String CAROUSEL_EMPTY = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/carousel-empty";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    void testEmptyCarousel() {
        Assertions.assertEquals(0, getCarouselUnderTest(CAROUSEL_EMPTY).getItems().size());
    }

    @Test
    void testCarouselWithItems() {
        Carousel carousel = getCarouselUnderTest(CAROUSEL_1);
        Object[][] expectedItems = {
                { "item_1", "Teaser 1", "carousel-c39ba25916-item-6204f971ea", CAROUSEL_1 + "/item_1" },
                { "item_2", "Teaser 2", "carousel-c39ba25916-item-7d7da28085", CAROUSEL_1 + "/item_2" },
                { "item_3", "Carousel Panel 3", "carousel-c39ba25916-item-aeaad4f462", CAROUSEL_1 + "/item_3" },
        };
        verifyContainerListItems(expectedItems, carousel.getItems());
        verifyContainerItems(expectedItems, carousel.getChildren());
        Utils.testJSONExport(carousel, Utils.getTestExporterJSONPath(TEST_BASE, "carousel1"));
        verifyPanelDataLayer(carousel, TEST_BASE, "carousel1");
    }

    @Test
    void testCarouselProperties() {
        Carousel carousel = getCarouselUnderTest(CAROUSEL_1);
        assertTrue(carousel.getAutoplay());
        assertEquals(Long.valueOf(7000), carousel.getDelay());
        assertTrue(carousel.getAutopauseDisabled());
    }

    private Carousel getCarouselUnderTest(@NotNull final String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(Objects.requireNonNull(context.resourceResolver().getResource(resourcePath)));
        Component component = mock(Component.class);
        when(component.getResourceType()).thenReturn(CarouselImpl.RESOURCE_TYPE);
        MockSlingHttpServletRequest request = context.request();
        SlingBindings slingBindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return context.request().adaptTo(Carousel.class);
    }
}
