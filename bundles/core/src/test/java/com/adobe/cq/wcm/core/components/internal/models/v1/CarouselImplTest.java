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

import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Carousel;
import com.adobe.cq.wcm.core.components.models.ListItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.*;


@ExtendWith(AemContextExtension.class)
class CarouselImplTest {

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
                { "item_1", "Teaser 1", "core/wcm/components/teaser/v1/teaser",
                        "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_1" },
                { "item_2", "Teaser 2", "core/wcm/components/teaser/v1/teaser",
                        "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_2" },
                { "item_3", "Carousel Panel 3", "core/wcm/components/teaser/v1/teaser",
                        "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_3" }, };
        verifyCarouselItems(expectedItems, carousel.getItems(), carousel.getId());
        Utils.testJSONExport(carousel, Utils.getTestExporterJSONPath(TEST_BASE, "carousel1"));
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
        return context.request().adaptTo(Carousel.class);
    }

    private void verifyCarouselItems(Object[][] expectedItems, List<ListItem> items, String carouselId) {
        assertEquals("The carousel contains a different number of items than expected.", expectedItems.length,
                items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("The carousel item's name is not what was expected.", expectedItems[index][0], item.getName());
            assertEquals("The carousel item's title is not what was expected: " + item.getTitle(),
                    expectedItems[index][1], item.getTitle());
            assertEquals("The carousel item's path is not what was expected: " + item.getPath(),
                    expectedItems[index][3], item.getPath());

            if (item.getData() != null) {
                assertNotEquals("The carousel item's data layer string is empty", item.getData().getJson(), "{}");

                assertEquals("The carousel item's data layer title is not what was expected: " + item.getData().getTitle(),
                        expectedItems[index][1], item.getData().getTitle());
                assertEquals("The carousel item's data layer type is not what was expected: " + item.getData().getType(),
                        expectedItems[index][2], item.getData().getType());
                assertEquals("The carousel item's data layer id is not what was expected: " + item.getData().getId(),
                        com.adobe.cq.wcm.core.components.internal.Utils.generateId(carouselId + "-item", (String) expectedItems[index][3]),
                        item.getData().getId());
            }
            index++;
        }
    }
}
