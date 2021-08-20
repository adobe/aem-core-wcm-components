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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.RangeIterator;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import io.wcm.testing.mock.aem.MockLanguageManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@ExtendWith(AemContextExtension.class)
public class NavigationImplTest {

    private static final String TEST_BASE = "/navigation";

    protected final AemContext context = CoreComponentTestContext.newAemContext();

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
    // tests for new structureStart option
    private static final String NAV_COMPONENT_12 = TEST_ROOT + "/jcr:content/root/navigation-component-12";
    private static final String NAV_COMPONENT_13 = TEST_ROOT + "/jcr:content/root/navigation-component-13";
    private static final String NAV_COMPONENT_14 = TEST_ROOT + "/jcr:content/root/navigation-component-14";
    private static final String NAV_COMPONENT_15 = "/content/navigation-livecopy/jcr:content/root/navigation-component-15";
    private static final String NAV_COMPONENT_16 = TEST_ROOT + "/jcr:content/root/navigation-component-16";
    private static final String NAV_COMPONENT_17 = TEST_ROOT + "/jcr:content/root/navigation-component-17";
    private static final String NAV_COMPONENT_18 = "/content/navigation-redirect-chain/jcr:content/root/navigation-component-18";
    private static final String NAV_COMPONENT_19 = TEST_ROOT + "/jcr:content/root/navigation-component-19";


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
                        final Iterator<LiveRelationship> iterator = relationships.iterator();
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
        assertEquals(0, navigation.getItems().size(), "Didn't expect any navigation items.");
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
    void testNavigationWithLiveCopyTreeCurrentPageAtRoot() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_15);
        Object[][] expectedPages = {
            {"/content/navigation-livecopy", 0, true, "/content/navigation-livecopy.html"},
            {"/content/navigation-livecopy/1", 1, false, "/content/navigation-livecopy/1.html"},
            {"/content/navigation-livecopy/1/1-1", 2, false, "/content/navigation-livecopy/1/1-1.html"},
            {"/content/navigation-livecopy/1/1-3", 2, false, "/content/navigation-livecopy/1/1-3.html"},
            {"/content/navigation-livecopy/2", 1, false, "/content/navigation-livecopy/2.html"},
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
                {"/content/navigation-redirect/navigation-2", 1, false, "/content/navigation-redirect/navigation-2.html"},
                {"/content/navigation-redirect/navigation-3", 1, false, "https://www.adobe.com"},
                {"/content/navigation-redirect/navigation-4", 1, false, "https://www.adobe.com"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation9"));
    }

    /**
     * Tests that a chain of redirects that eventually point to the current page are all marked active.
     */
    @Test
    void activeRedirectChainTest() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_18);
        Object[][] expectedPages = {
            {"/content/navigation-redirect-chain", 0, true, "/content/navigation-redirect-chain.html"},
            {"/content/navigation-redirect-chain", 1, true, "/content/navigation-redirect-chain.html"},
            {"/content/navigation-redirect-chain", 2, true, "/content/navigation-redirect-chain.html"},
            {"/content/navigation-redirect-chain", 1, true, "/content/navigation-redirect-chain.html"},
            {"/content/navigation-redirect-chain", 1, true, "/content/navigation-redirect-chain.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation18"));
    }
    /**
     * Test to verify #945: if shadowing is disabled Redirecting pages should be displayed instead of redirect targets
     */
    @Test
    void testRedirectWithDisabledShadowing() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_16);
        Object[][] expectedPages = {
            {"/content/navigation-redirect", 0, true, "/content/navigation-redirect.html"},
            {"/content/navigation-redirect/navigation-1", 1, false, "/navigation-1-vanity"},
            {"/content/navigation-redirect/navigation-1/navigation-1-1", 2, false,
                "/content/navigation-redirect/navigation-1/navigation-1-1.html"},
            {"/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-1", 3, false,
                "/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-1.html"},
            {"/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2", 3, false,
                "/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2.html"},
            {"/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-1", 4, false,
                "/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-1.html"},
            {"/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3", 4, false,
                "/content/navigation-redirect/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"},
            {"/content/navigation-redirect/navigation-2", 1, false, "/content/navigation-redirect/navigation-2.html"},
            {"/content/navigation-redirect/navigation-3", 1, false, "/content/navigation-redirect/navigation-3.html"},
            {"/content/navigation-redirect/navigation-4", 1, false, "/content/navigation-redirect/navigation-4.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation16"));
    }

    /**
     * Test to verify #945: if shadowing is enabled Redirect target pages should be displayed instead of original pages
     */
    @Test
    void testRedirectWithEnabledShadowing() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_17);
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
            {"/content/navigation-redirect/navigation-2", 1, false, "/content/navigation-redirect/navigation-2.html"},
            {"/content/navigation-redirect/navigation-3", 1, false, "https://www.adobe.com"},
            {"/content/navigation-redirect/navigation-4", 1, false, "https://www.adobe.com"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation17"));
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

    @Test
    protected void testStructureStartZero() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_12);
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation12"));
    }

    @Test
    protected void testStructureStartOne() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_13);
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation13"));
    }

    @Test
    protected void testStructureStartTwo() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_14);
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation14"));
    }

    @Test
    protected void testVanityPaths() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_19);
        Object[][] expectedPages = {
                {"/content/navigation-vanity-paths", 0, false, "/nav0.html"},
                {"/content/navigation-vanity-paths/navigation-1", 1, false, "/nav1.html"},
                {"/content/navigation-vanity-paths/navigation-2", 1, false, "/nav2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(testBase, "navigation19"));
    }

    protected Navigation getNavigationUnderTest(String resourcePath) {
        Utils.enableDataLayer(context, true);
        context.currentResource(resourcePath);
        MockSlingHttpServletRequest request = context.request();
        request.setContextPath("/core");
        Component component = mock(Component.class);
        when(component.getResourceType()).thenReturn(resourceType);
        SlingBindings slingBindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
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
        assertEquals(expectedPages.length, items.size(), "The navigation tree contains a different number of pages than expected.");
        int index = 0;
        for (NavigationItem item : items) {
            assertEquals(expectedPages[index][0], item.getPath(), "The navigation tree doesn't seem to have the correct order.");
            assertEquals(expectedPages[index][1], item.getLevel(), "The navigation item's level is not what was expected: " + item.getPath());
            assertEquals(expectedPages[index][2], item.isActive(), "The navigation item's active state is not what was expected: " + item.getPath());
            String expectedURL = expectedPages[index][3].toString();
            if (!expectedURL.startsWith("http")) {
                expectedURL = CONTEXT_PATH + expectedURL;
            }
            assertEquals(expectedURL, item.getURL(), "The navigation item's URL is not what was expected: " + item.getPath());
            verifyNavigationItem(expectedPages[index], item);
            index++;
        }
    }

    @SuppressWarnings("deprecation")
    protected void verifyNavigationItem(Object[] expectedPage, NavigationItem item) {
        assertEquals(expectedPage[0], item.getPath(), "The navigation tree doesn't seem to have the correct order.");
        assertEquals(expectedPage[1], item.getLevel(), "The navigation item's level is not what was expected: " + item.getPath());
        assertEquals(expectedPage[2], item.isActive(), "The navigation item's active state is not what was expected: " + item.getPath());
        String expectedURL = expectedPage[3].toString();
        if (!expectedURL.startsWith("http")) {
            expectedURL = CONTEXT_PATH + expectedURL;
        }
        assertEquals(expectedURL, item.getURL(), "The navigation item's URL is not what was expected: " + item.getPath());
    }

}
