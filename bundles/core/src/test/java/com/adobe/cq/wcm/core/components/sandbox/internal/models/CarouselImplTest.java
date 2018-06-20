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
package com.adobe.cq.wcm.core.components.sandbox.internal.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.sandbox.models.Carousel;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;

public class CarouselImplTest {

    private static final String TEST_BASE = "/carousel";
    private static final String CONTENT_ROOT = "/content";
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/carousel";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String CAROUSEL_1 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/carousel-1";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @Test
    public void testEmptyCarousel() {
        Carousel carousel = new CarouselImpl();
        List<Resource> items = carousel.getItems();
        Assert.assertTrue("", items == null || items.size() == 0);
    }

    @Test
    public void testCarouselWithItems() {
        Carousel carousel = getCarouselUnderTest(CAROUSEL_1);
        assertEquals(2, carousel.getItems().size());
        //Utils.testJSONExport(carousel, Utils.getTestExporterJSONPath(TEST_BASE, "carousel1"));
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
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Carousel.class);
    }
}
