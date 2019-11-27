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

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.RangeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.testing.MockLanguageManager;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class NavigationImplTest {

    private static final String TEST_BASE = "/navigation";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    protected static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT = "/content/navigation";
    private static final String NAV_COMPONENT_1 = TEST_ROOT + "/jcr:content/root/navigation-component-1";
    private static final String NAV_COMPONENT_2 = TEST_ROOT + "/navigation-2/jcr:content/root/navigation-component-2";
    private static final String NAV_COMPONENT_3 = TEST_ROOT + "/navigation-2/jcr:content/root/navigation-component-3";
    private static final String NAV_COMPONENT_4 = TEST_ROOT + "/navigation-1/navigation-1-1/jcr:content/root/navigation-component-4";
    private static final String NAV_COMPONENT_5 =
            TEST_ROOT + "/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3/jcr:content/root/navigation-component-5";
    private static final String NAV_COMPONENT_IN_TEMPLATE =
            "/conf/coretest/settings/wcm/templates/template-1/structure/jcr:content/root/navigation";
    private static final String NAV_COMPONENT_6 = "/content/navigation-3-region/us/en/2/jcr:content/root/navigation-component-6";
    private static final String NAV_COMPONENT_7 = "/content/navigation-3-region/us/en/2/jcr:content/root/navigation-component-7";
    private static final String NAV_COMPONENT_8 = "/content/navigation-livecopy/2/jcr:content/root/navigation-component-8";
    private static final String NAV_COMPONENT_9 = TEST_ROOT + "/jcr:content/root/navigation-component-9";
    // points to the nav component used for invalidRedirectTest()
    private static final String NAV_COMPONENT_10 = TEST_ROOT + "/jcr:content/root/navigation-component-10";
    // points to the nav component used for when the nav root has no jcr:content child
    private static final String NAV_COMPONENT_11 = TEST_ROOT + "/jcr:content/root/navigation-component-11";

    protected String testBase;
    protected String resourceType;

    @BeforeEach
    protected void setUp() throws WCMException {
        testBase = TEST_BASE;
        resourceType = NavigationImpl.RESOURCE_TYPE;
        internalSetup();
    }

    protected void internalSetup() throws WCMException {
        context.load().json(testBase + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        context.load().json(testBase + "/test-conf.json", "/conf");
        context.registerService(LanguageManager.class, new MockLanguageManager());
        LiveRelationshipManager relationshipManager = mock(LiveRelationshipManager.class);
        when(relationshipManager.getLiveRelationships(any(Resource.class), isNull(), isNull())).then(
                invocation -> {
                    Object[] arguments = invocation.getArguments();
                    Resource resource = (Resource) arguments[0];
                    if ("/content/navigation-blueprint".equals(resource.getPath())) {
                        LiveRelationship liveRelationship = mock(LiveRelationship.class);
                        when(liveRelationship.getTargetPath()).thenReturn("/content/navigation-livecopy");
                        final ArrayList<LiveRelationship> relationships = new ArrayList<>();
                        relationships.add(liveRelationship);
                        final Iterator iterator = relationships.iterator();
                        return new RangeIterator() {

                            int index = 0;

                            @Override
                            public void skip(long skipNum) {

                            }

                            @Override
                            public long getSize() {
                                return relationships.size();
                            }

                            @Override
                            public long getPosition() {
                                return index;
                            }

                            @Override
                            public boolean hasNext() {
                                return iterator.hasNext();
                            }

                            @Override
                            public Object next() {
                                index++;
                                return iterator.next();
                            }
                        };
                    }
                    return null;
                }
        );
        context.registerService(LiveRelationshipManager.class, relationshipManager);
    }

    @Test
    protected void testFullNavigationTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_1);
        Object[][] expectedPages = {
                {"/content/navigation", 0, true, "/content/navigation.html"},
                {"/content/navigation/navigation-1", 1, false, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 2, false, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 4, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 4, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
                {"/content/navigation/navigation-2", 1, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation1"));
    }

    @Test
    protected void testNavigationNoRoot() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_2);
        assertEquals("Didn't expect any navigation items.", 0, navigation.getItems().size());
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation4"));
    }

    /**
     * Demonstrates the ability to construct a {@link NavigationImpl} where the navigation root page does not have a
     * jcr:content node, but does have legitimate sub-pages.
     */
    @Test
    protected void testNavigationRootMissingJCRContent() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_11);
        Object[][] expectedPages = {
            {"/content/navigation-missing-jcr-content/navigation-1", 0, false, "/content/navigation-missing-jcr-content/navigation-1.html"},
            {"/content/navigation-missing-jcr-content/navigation-2", 0, false, "/content/navigation-missing-jcr-content/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, navigation.getItems());
    }

    @Test
    protected void testNavigationWithRootInDifferentTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_3);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1/navigation-1-1", 0, false, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 1, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 1, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 2, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 2, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation5"));
    }

    @Test
    protected void testPartialNavigationTreeNotOnlyCurrentPage() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_4);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation7"));
    }

    @Test
    protected void testPartialNavigationTreeContentPolicyNotOnlyCurrentPage() {
        context.contentPolicyMapping(resourceType,
                "navigationRoot", "/content/navigation",
                "collectAllPages", false,
                "structureDepth", 2);
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_5);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation8"));
    }

    @Test
    protected void testCollectionOnTemplate() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_IN_TEMPLATE);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, false, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, false, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 2, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 2, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
    }

    @Test
    protected void testNavigationWithLanguageMaster() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_6);
        Object[][] expectedPages = {
                {"/content/navigation-3-region/us/en", 0, true, "/content/navigation-3-region/us/en.html"},
                {"/content/navigation-3-region/us/en/1", 1, false, "/content/navigation-3-region/us/en/1.html"},
                {"/content/navigation-3-region/us/en/2", 1, true, "/content/navigation-3-region/us/en/2.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
    }

    @Test
    protected void testNavigationWithLanguageMasterLeafsMissing() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_7);
        Object[][] expectedPages = {
                {"/content/navigation-3-region/us/en/1", 0, false, "/content/navigation-3-region/us/en/1.html"},
                {"/content/navigation-3-region/us/en/1/1-1", 1, false, "/content/navigation-3-region/us/en/1/1-1.html"},
                {"/content/navigation-3-region/us/en/1/1-3", 1, false, "/content/navigation-3-region/us/en/1/1-3.html"},
                {"/content/navigation-3-region/us/en/1/1-4", 1, false, "/content/navigation-3-region/us/en/1/1-4.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
    }

    @Test
    protected void testNavigationWithLiveCopyTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_8);
        Object[][] expectedPages = {
                {"/content/navigation-livecopy", 0, true, "/content/navigation-livecopy.html"},
                {"/content/navigation-livecopy/1", 1, false, "/content/navigation-livecopy/1.html"},
                {"/content/navigation-livecopy/1/1-1", 2, false, "/content/navigation-livecopy/1/1-1.html"},
                {"/content/navigation-livecopy/1/1-3", 2, false, "/content/navigation-livecopy/1/1-3.html"},
                {"/content/navigation-livecopy/2", 1, true, "/content/navigation-livecopy/2.html"},
                {"/content/navigation-livecopy/3", 1, false, "/content/navigation-livecopy/3.html"},

        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
    }

    @Test
    protected void activeRedirectTest() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_9);
        Object[][] expectedPages = {
                {"/content/navigation", 0, true, "/content/navigation.html"},
                {"/content/navigation-redirect/navigation-1", 1, false, "/navigation-1-vanity"},
                {"/content/navigation-redirect/navigation-1/navigation-1-1", 2, false,
                        "/content/navigation-redirect/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1", 3, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-1.html"},
                {"/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1", 4, false,
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-2/navigation-1-1-2-2-1.html"},
                {"/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 4, false,
                        "/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
                {"/content/navigation-redirect/navigation-2", 1, false, "/content/navigation-redirect/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation9"));
    }

    /**
     * Test to verify #189 : Null Pointer Exception in NavigationImpl when Redirect Target is not found
     */
    @Test
    protected void invalidRedirectTest() {
        // get the navigation component
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_10);
        // get the elements, an NPE will cause the test to fail
        getNavigationItems(navigation);
    }

    protected Navigation getNavigationUnderTest(String resourcePath) {
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath("/core");
        return request.adaptTo(Navigation.class);
    }

    protected List<NavigationItem> getNavigationItems(Navigation navigation) {
        List<NavigationItem> items = new LinkedList<>();
        for (NavigationItem item : navigation.getItems()) {
            collect(items, item);
        }
        return items;
    }

    protected void collect(List<NavigationItem> items, NavigationItem navigationItem) {
        items.add(navigationItem);
        for (NavigationItem item : navigationItem.getChildren()) {
            collect(items, item);
        }
    }

    protected void verifyNavigationItems(Object[][] expectedPages, List<NavigationItem> items) {
        assertEquals("The navigation tree contains a different number of pages than expected.", expectedPages.length, items.size());
        int index = 0;
        for (NavigationItem item : items) {
            verifyNavigationItem(expectedPages[index], item);
            index++;
        }
    }

    @SuppressWarnings("deprecation")
    protected void verifyNavigationItem(Object[] expectedPage, NavigationItem item) {
        assertEquals("The navigation tree doesn't seem to have the correct order.", expectedPage[0], item.getPath());
        assertEquals("The navigation item's level is not what was expected: " + item.getPath(),
                expectedPage[1], item.getLevel());
        assertEquals("The navigation item's active state is not what was expected: " + item.getPath(),
                expectedPage[2], item.isActive());
        assertEquals("The navigation item's URL is not what was expected: " + item.getPath(),
                CONTEXT_PATH + expectedPage[3], item.getURL());
    }

}
