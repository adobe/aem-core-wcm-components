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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
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
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


@ExtendWith(AemContextExtension.class)
class CarouselImplTest {

    private static final String TEST_BASE = "/carousel";
    private static final String CONTENT_ROOT = "/content";
    private static final String TEST_ROOT_PAGE = "/content/carousel";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CAROUSEL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/carousel-1";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
    }

    @Test
    void testEmptyCarousel() {
        Carousel carousel = new CarouselImpl();
        List<ListItem> items = carousel.getItems();
        assertEquals("", 0, items.size());
    }

    @Test
    void testCarouselWithItems() {
        Carousel carousel = getCarouselUnderTest();
        Object[][] expectedItems = {
                {"item_1", "Teaser 1", "resourceListItem", "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_1"},
                {"item_2", "Teaser 2", "resourceListItem", "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_2"},
                {"item_3", "Carousel Panel 3", "resourceListItem", "/content/carousel/jcr:content/root/responsivegrid/carousel-1/item_3"},
        };
        verifyCarouselItems(expectedItems, carousel.getItems());
        Utils.testJSONExport(carousel, Utils.getTestExporterJSONPath(TEST_BASE, "carousel1"));
    }

    @Test
    void testCarouselProperties() {
        Carousel carousel = getCarouselUnderTest();
        assertTrue(carousel.getAutoplay());
        assertEquals(Long.valueOf(7000), carousel.getDelay());
        assertTrue(carousel.getAutopauseDisabled());
    }

    private Carousel getCarouselUnderTest() {
        context.currentResource(CarouselImplTest.CAROUSEL_1);
        return context.request().adaptTo(Carousel.class);
    }

    private void verifyCarouselItems(Object[][] expectedItems, List<ListItem> items) {
        assertEquals("The carousel contains a different number of items than expected.", expectedItems.length, items.size());
        int index = 0;
        for (ListItem item : items) {
            assertEquals("The carousel item's name is not what was expected.",
                    expectedItems[index][0], item.getName());
            assertEquals("The carousel item's title is not what was expected: " + item.getTitle(),
                    expectedItems[index][1], item.getTitle());
            assertEquals("The carousel item's path is not what was expected: " + item.getPath(),
                expectedItems[index][3], item.getPath());



            assertNotEquals("The carousel item's data layer string is empty", item.getDataLayerString(), "{}");

            assertEquals("The carousel item's data layer name is not what was expected: " + item.getDataLayerName(),
                    expectedItems[index][0], item.getDataLayerName());
            assertEquals("The carousel item's data layer title is not what was expected: " + item.getDataLayerTitle(),
                expectedItems[index][1], item.getDataLayerTitle());
            assertEquals("The carousel item's data layer type is not what was expected: " + item.getDataLayerType(),
                expectedItems[index][2], item.getDataLayerType());
            assertEquals("The carousel item's data layer id is not what was expected: " + item.getDataLayerId(),
                expectedItems[index][2] + "-" + StringUtils.substring(DigestUtils.sha1Hex((String)expectedItems[index][3]), 0, 10), item.getDataLayerId());

            index++;
        }
    }
}
