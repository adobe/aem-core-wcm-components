/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.ArrayList;
import java.util.List;

import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.Assert.assertEquals;

@ExtendWith(AemContextExtension.class)
class LanguageNavigationImplTest {

    private static final String TEST_BASE = "/languagenavigation";
    private static final String CONTEXT_PATH = "/core";
    private static final String NAVIGATION_ROOT = "/content/languagenavigation";
    private static final Object[][] EXPECTED_PAGES_DEPTH_1 = {
            {"/content/languagenavigation/LOCALE-1/LOCALE-5/about", "LOCALE 1", true, 0, "US", "en-US",
                    "/content/languagenavigation/LOCALE-1/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-2/LOCALE-5/about", "LOCALE 2", false, 0, "CA", "en-CA",
                    "/content/languagenavigation/LOCALE-2/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-3", "LOCALE 3", false, 0, "CH", "de-CH", "/LOCALE-3-vanity"},
            {"/content/languagenavigation/LOCALE-4", "LOCALE 4", false, 0, "DE", "de-DE", "/content/languagenavigation/LOCALE-4.html"},
    };
    private static final Object[][] EXPECTED_PAGES_DEPTH_2 = {
            {"/content/languagenavigation/LOCALE-1/LOCALE-5/about", "LOCALE 1", true, 0, "US", "en-US",
                    "/content/languagenavigation/LOCALE-1/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-1/LOCALE-5/about", "LOCALE 5", true, 1, "US", "en-US",
                    "/content/languagenavigation/LOCALE-1/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-1/LOCALE-6", "LOCALE 6", false, 1, "US", "es-US",
                    "/content/languagenavigation/LOCALE-1/LOCALE-6.html"},
            {"/content/languagenavigation/LOCALE-2/LOCALE-5/about", "LOCALE 2", false, 0, "CA", "en-CA",
                    "/content/languagenavigation/LOCALE-2/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-2/LOCALE-5/about", "LOCALE 5", false, 1, "CA", "en-CA",
                    "/content/languagenavigation/LOCALE-2/LOCALE-5/about.html"},
            {"/content/languagenavigation/LOCALE-2/LOCALE-7", "LOCALE 7", false, 1, "CA", "fr-CA",
                    "/content/languagenavigation/LOCALE-2/LOCALE-7.html"},
            {"/content/languagenavigation/LOCALE-3", "LOCALE 3", false, 0, "CH", "de-CH", "/LOCALE-3-vanity"},
            {"/content/languagenavigation/LOCALE-3/LOCALE-8", "LOCALE 8", false, 1, "CH", "de-CH",
                    "/content/languagenavigation/LOCALE-3/LOCALE-8.html"},
            {"/content/languagenavigation/LOCALE-4", "LOCALE 4", false, 0, "DE", "de-DE", "/content/languagenavigation/LOCALE-4.html"},
            {"/content/languagenavigation/LOCALE-4/LOCALE-9", "LOCALE 9", false, 1, "DE", "de-DE",
                    "/content/languagenavigation/LOCALE-4/LOCALE-9.html"},
    };

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        context.load().json("/languagenavigation/test-conf.json", "/conf");
    }

    @Test
    void testLanguageNavigationItems() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-1");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_1, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(TEST_BASE, "languagenavigation1"));

    }

    @Test
    void testLanguageNavigationItemsStructureDepth() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-2");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_2, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(TEST_BASE, "languagenavigation2"));
    }

    @Test
    void testLanguageNavigationItemsStructureDepthContentPolicy() {
        context.contentPolicyMapping(LanguageNavigationImpl.RESOURCE_TYPE,
                "siteRoot", "/content/languagenavigation",
                "structureDepth", 2);
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-3");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_2, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(TEST_BASE, "languagenavigation3"));

    }

    @Test
    void testLanguageNavigationItemsNoRoot() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-4");
        assertEquals("Didn't expect any language navigation items.", 0, languageNavigation.getItems().size());
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(TEST_BASE, "languagenavigation4"));

    }

    @Test
    void testLanguageNavigationItemsOnTemplate() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                "/conf/coretest/settings/wcm/templates/template-1/structure/jcr:content/root/languagenavigation");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        Object[][] expectedPages = {
                {"/content/languagenavigation/LOCALE-1", "LOCALE 1", false, 0, "US", "en-US", "/content/languagenavigation/LOCALE-1.html"},
                {"/content/languagenavigation/LOCALE-1/LOCALE-5", "LOCALE 5", false, 1, "US", "en-US",
                        "/content/languagenavigation/LOCALE-1/LOCALE-5.html"},
                {"/content/languagenavigation/LOCALE-1/LOCALE-6", "LOCALE 6", false, 1, "US", "es-US",
                        "/content/languagenavigation/LOCALE-1/LOCALE-6.html"},
                {"/content/languagenavigation/LOCALE-2", "LOCALE 2", false, 0, "CA", "en-CA", "/content/languagenavigation/LOCALE-2.html"},
                {"/content/languagenavigation/LOCALE-2/LOCALE-5", "LOCALE 5", false, 1, "CA", "en-CA",
                        "/content/languagenavigation/LOCALE-2/LOCALE-5.html"},
                {"/content/languagenavigation/LOCALE-2/LOCALE-7", "LOCALE 7", false, 1, "CA", "fr-CA",
                        "/content/languagenavigation/LOCALE-2/LOCALE-7.html"},
                {"/content/languagenavigation/LOCALE-3", "LOCALE 3", false, 0, "CH", "de-CH", "/LOCALE-3-vanity"},
                {"/content/languagenavigation/LOCALE-3/LOCALE-8", "LOCALE 8", false, 1, "CH", "de-CH",
                        "/content/languagenavigation/LOCALE-3/LOCALE-8.html"},
                {"/content/languagenavigation/LOCALE-4", "LOCALE 4", false, 0, "DE", "de-DE", "/content/languagenavigation/LOCALE-4.html"},
                {"/content/languagenavigation/LOCALE-4/LOCALE-9", "LOCALE 9", false, 1, "DE", "de-DE",
                        "/content/languagenavigation/LOCALE-4/LOCALE-9.html"},
        };
        verifyLanguageNavigationItems(expectedPages, items);

    }

    private LanguageNavigation getLanguageNavigationUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(LanguageNavigation.class);
    }

    private List<NavigationItem> getLanguageNavigationItems(LanguageNavigation languageNavigation) {
        List<NavigationItem> items = new ArrayList<>();
        for (NavigationItem item : languageNavigation.getItems()) {
            collect(items, item);
        }
        return items;
    }

    private void collect(List<NavigationItem> items, NavigationItem navigationItem) {
        items.add(navigationItem);
        for (NavigationItem item : navigationItem.getChildren()) {
            collect(items, item);
        }
    }

    private void verifyLanguageNavigationItems(Object[][] expectedPages, List<NavigationItem> items) {
        assertEquals("The language navigation items contain a different number of pages than expected.", expectedPages.length,
                items.size());
        int index = 0;
        while (items.size() > index) {
            LanguageNavigationItem item = (LanguageNavigationItem) items.get(index);
            assertEquals("The language navigation items don't seem to have the correct order.", expectedPages[index][0], item.getPath());
            assertEquals("The language navigation item's title is not what was expected: " + item.getPath(), expectedPages[index][1],
                    item.getTitle());
            assertEquals("The language navigation item's active state is not what was expected: " + item.getPath(), expectedPages[index][2],
                    item.isActive());
            assertEquals("The language navigation item's level is not what was expected: " + item.getPath(), expectedPages[index][3],
                    item.getLevel());
            assertEquals("The language navigation item's country is not what was expected: " + item.getPath(), expectedPages[index][4],
                    item.getCountry());
            assertEquals("The language navigation item's language is not what was expected: " + item.getPath(), expectedPages[index][5],
                    item.getLanguage());
            assertEquals("The language navigation item's locale is not what was expected: " + item.getPath(), expectedPages[index][5],
                    item.getLocale().toString().replace('_', '-'));
            assertEquals("The language navigation item's URL is not what was expected: " + item.getPath(),
                    CONTEXT_PATH + expectedPages[index][6], item.getURL());
            index++;
        }
    }
}
