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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class LanguageNavigationImplTest {

    private static final String TEST_BASE = "/languagenavigation";
    protected static final String CONTEXT_PATH = "/core";
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

    protected final AemContext context = CoreComponentTestContext.newAemContext();

    protected String testBase;
    protected String resourceType;

    @BeforeEach
    protected void setUp() {
        testBase = TEST_BASE;
        resourceType = LanguageNavigationImpl.RESOURCE_TYPE;
        internalSetup();
    }

    protected void internalSetup() {
        context.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        context.load().json(testBase + "/test-conf.json", "/conf");
    }

    @Test
    protected void testLanguageNavigationItems() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-1");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_1, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(testBase, "languagenavigation1"));

    }

    @Test
    protected void testLanguageNavigationItemsStructureDepth() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-2");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_2, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(testBase, "languagenavigation2"));
    }

    @Test
    protected void testLanguageNavigationItemsStructureDepthContentPolicy() {
        context.contentPolicyMapping(resourceType,
                "siteRoot", "/content/languagenavigation",
                "structureDepth", 2);
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-3");
        List<NavigationItem> items = getLanguageNavigationItems(languageNavigation);
        verifyLanguageNavigationItems(EXPECTED_PAGES_DEPTH_2, items);
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(testBase, "languagenavigation3"));
    }

    @Test
    protected void testLanguageNavigationItemsNoRoot() {
        LanguageNavigation languageNavigation = getLanguageNavigationUnderTest(
                NAVIGATION_ROOT + "/LOCALE-1/LOCALE-5/about/jcr:content/root/languagenavigation-component-4");
        assertEquals(0, languageNavigation.getItems().size(), "Didn't expect any language navigation items.");
        Utils.testJSONExport(languageNavigation, Utils.getTestExporterJSONPath(testBase, "languagenavigation4"));

    }

    @Test
    protected void testLanguageNavigationItemsOnTemplate() {
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

    protected  LanguageNavigation getLanguageNavigationUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath(CONTEXT_PATH);
        return request.adaptTo(LanguageNavigation.class);
    }

    protected  List<NavigationItem> getLanguageNavigationItems(LanguageNavigation languageNavigation) {
        List<NavigationItem> items = new ArrayList<>();
        for (NavigationItem item : languageNavigation.getItems()) {
            collect(items, item);
        }
        return items;
    }

    protected  void collect(List<NavigationItem> items, NavigationItem navigationItem) {
        items.add(navigationItem);
        for (NavigationItem item : navigationItem.getChildren()) {
            collect(items, item);
        }
    }

    protected void verifyLanguageNavigationItems(Object[][] expectedPages, List<NavigationItem> items) {
        assertEquals(expectedPages.length, items.size(),
            "The language navigation items contain a different number of pages than expected.");
        int index = 0;
        while (items.size() > index) {
            LanguageNavigationItem item = (LanguageNavigationItem) items.get(index);
            assertEquals(expectedPages[index][0], item.getPath(), "The language navigation items don't seem to have the correct order.");
            assertEquals(expectedPages[index][1], item.getTitle(), "The language navigation item's title is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][2], item.isActive(), "The language navigation item's active state is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][3], item.getLevel(), "The language navigation item's level is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][4], item.getCountry(), "The language navigation item's country is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][5], item.getLanguage(), "The language navigation item's language is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][5], item.getLocale().toString().replace('_', '-'), "The language navigation item's locale is not what was expected: " + item.getPath());
            assertEquals(CONTEXT_PATH + expectedPages[index][6], item.getURL(), "The language navigation item's URL is not what was expected: " + item.getPath());
            verifyLanguageNavigationItem(expectedPages[index], item);
            index++;
        }
    }

    @SuppressWarnings("deprecation")
    protected void verifyLanguageNavigationItem(Object[] expectedPage, LanguageNavigationItem item) {
        assertEquals(expectedPage[0], item.getPath(), "The language navigation items don't seem to have the correct order.");
        assertEquals(expectedPage[1], item.getTitle(), "The language navigation item's title is not what was expected: " + item.getPath());
        assertEquals(expectedPage[2], item.isActive(), "The language navigation item's active state is not what was expected: " + item.getPath());
        assertEquals(expectedPage[3], item.getLevel(), "The language navigation item's level is not what was expected: " + item.getPath());
        assertEquals(expectedPage[4], item.getCountry(), "The language navigation item's country is not what was expected: " + item.getPath());
        assertEquals(expectedPage[5], item.getLanguage(), "The language navigation item's language is not what was expected: " + item.getPath());
        assertEquals(expectedPage[5], item.getLocale().toString().replace('_', '-'), "The language navigation item's locale is not what was expected: " + item.getPath());
        assertEquals(CONTEXT_PATH + expectedPage[6], item.getURL(), "The language navigation item's URL is not what was expected: " + item.getPath());
    }
}
