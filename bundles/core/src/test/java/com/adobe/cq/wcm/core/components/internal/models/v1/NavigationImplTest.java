/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.adobe.cq.wcm.core.components.testing.MockLanguageManager;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.cq.wcm.msm.api.RolloutManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigationImplTest {

    private static final String TEST_BASE = "/navigation";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content");

    private static final ContentPolicyManager POLICY_MANAGER = mock(ContentPolicyManager.class);
    private static final String CONTEXT_PATH = "/core";
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

    private static final ContentPolicyManager contentPolicyManager = mock(ContentPolicyManager.class);

    @BeforeClass
    public static void init() throws WCMException {
        AEM_CONTEXT.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
                (Function<ResourceResolver, ContentPolicyManager>) resourceResolver -> contentPolicyManager
        );
        AEM_CONTEXT.load().json("/navigation/test-conf.json", "/conf");
        AEM_CONTEXT.registerService(LanguageManager.class, new MockLanguageManager());
        LiveRelationshipManager relationshipManager = mock(LiveRelationshipManager.class);
        when(relationshipManager.getLiveRelationships(any(Resource.class), any(String.class), any(RolloutManager.Trigger.class))).then(
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
        AEM_CONTEXT.registerService(LiveRelationshipManager.class, relationshipManager);
    }

    @Test
    public void testFullNavigationTree() {
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation1"));
    }

    @Test
    public void testNavigationNoRoot() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_2);
        assertEquals("Didn't expect any navigation items.", 0, navigation.getItems().size());
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation4"));
    }

    @Test
    public void testNavigationWithRootInDifferentTree() {
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation5"));
    }

    @Test
    public void testPartialNavigationTreeNotOnlyCurrentPage() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_4);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation7"));
    }

    @Test
    public void testPartialNavigationTreeContentPolicyNotOnlyCurrentPage() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_5);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation8"));
    }

    @Test
    public void testCollectionOnTemplate() {
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
    public void testNavigationWithLanguageMaster() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_6);
        Object[][] expectedPages = {
                {"/content/navigation-3-region/us/en", 0, true, "/content/navigation-3-region/us/en.html"},
                {"/content/navigation-3-region/us/en/1", 1, false, "/content/navigation-3-region/us/en/1.html"},
                {"/content/navigation-3-region/us/en/2", 1, true, "/content/navigation-3-region/us/en/2.html"},
        };
        verifyNavigationItems(expectedPages, getNavigationItems(navigation));
    }

    @Test
    public void testNavigationWithLanguageMasterLeafsMissing() {
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
    public void testNavigationWithLiveCopyTree() {
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
    public void activeRedirectTest() throws Exception {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_9);
        Object[][] expectedPages = {
                {"/content/navigation", 0, true, "/content/navigation.html"},
                {"/content/navigation-redirect/navigation-1", 1, false, "/navigation-1-vanity"},
                {"/content/navigation-redirect/navigation-1/navigation-1-1", 2, false, "/content/navigation-redirect/navigation-1/navigation-1-1.html"},
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
        Utils.testJSONExport(navigation, Utils.getTestExporterJSONPath(TEST_BASE, "navigation9"));
    }

    /**
     * Test to verify #189 : Null Pointer Exception in NavigationImpl when Redirect Target is not found
     * @throws Exception
     */
    @Test
    public void invalidRedirectTest() throws Exception {
        // get the navigation component
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_10);
        // get the elements, an NPE will cause the test to fail
        getNavigationItems(navigation);
    }

    private Navigation getNavigationUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
        ContentPolicy contentPolicy = null;
        if (mapping != null) {
            contentPolicy = mapping.getPolicy();
        }
        final MockSlingHttpServletRequest request =
                new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(), AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        Page currentPage = AEM_CONTEXT.pageManager().getContainingPage(resource);
        SlingBindings slingBindings = new SlingBindings();
        Style currentStyle;
        if (contentPolicy != null) {
            when(POLICY_MANAGER.getPolicy(resource)).thenReturn(contentPolicy);
            currentStyle = new MockContentPolicyStyle(contentPolicy);
        } else {
            currentStyle = mock(Style.class);
            when(currentStyle.get(anyString(), (Object) Matchers.anyObject())).thenAnswer(
                    invocation -> invocation.getArguments()[1]
            );
        }
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.CURRENT_PAGE, currentPage);
        slingBindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        slingBindings.put(WCMBindings.CURRENT_STYLE, currentStyle);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Navigation.class);
    }

    private List<NavigationItem> getNavigationItems(Navigation navigation) {
        List<NavigationItem> items = new LinkedList<>();
        for (NavigationItem item : navigation.getItems()) {
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

    private void verifyNavigationItems(Object[][] expectedPages, List<NavigationItem> items) {
        assertEquals("The navigation tree contains a different number of pages than expected.", expectedPages.length, items.size());
        int index = 0;
        for (NavigationItem item : items) {
            assertTrue("The navigation tree doesn't seem to have the correct order.",
                    expectedPages[index][0].equals(item.getPath()));
            assertEquals("The navigation item's level is not what was expected: " + item.getPath(),
                    expectedPages[index][1], item.getLevel());
            assertEquals("The navigation item's active state is not what was expected: " + item.getPath(),
                    expectedPages[index][2], item.isActive());
            assertEquals("The navigation item's URL is not what was expected: " + item.getPath(),
                    CONTEXT_PATH + expectedPages[index][3], item.getURL());
            index++;
        }
    }
}
