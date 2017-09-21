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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.sandbox.models.Navigation;
import com.adobe.cq.wcm.core.components.sandbox.models.NavigationItem;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NavigationImplTest {

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext("/navigation", "/content");

    private static final ContentPolicyManager POLICY_MANAGER = mock(ContentPolicyManager.class);
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT = "/content/navigation";
    private static final String NAV_COMPONENT_1 = TEST_ROOT + "/jcr:content/root/navigation-component-1";
    private static final String NAV_COMPONENT_2 = TEST_ROOT + "/navigation-1/navigation-1-1/jcr:content/root/navigation-component-2";
    private static final String NAV_COMPONENT_3 = TEST_ROOT +
            "/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3/jcr:content/root/navigation-component-3";
    private static final String NAV_COMPONENT_4 = TEST_ROOT + "/navigation-2/jcr:content/root/navigation-component-4";
    private static final String NAV_COMPONENT_5 = TEST_ROOT + "/navigation-2/jcr:content/root/navigation-component-5";
    private static final String NAV_COMPONENT_6 = TEST_ROOT + "/navigation-2/jcr:content/root/navigation-component-6";
    private static final String NAV_COMPONENT_7 = TEST_ROOT + "/navigation-1/navigation-1-1/jcr:content/root/navigation-component-7";
    private static final String NAV_COMPONENT_8 =
            TEST_ROOT + "/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3/jcr:content/root/navigation-component-8";
    private static final String NAV_COMPONENT_IN_TEMPLATE =
            "/conf/coretest/settings/wcm/templates/template-1/structure/jcr:content/root/navigation";

    private static final ContentPolicyManager contentPolicyManager = mock(ContentPolicyManager.class);

    @BeforeClass
    public static void init() {
        AEM_CONTEXT.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
                (Function<ResourceResolver, ContentPolicyManager>) resourceResolver -> contentPolicyManager
        );
        AEM_CONTEXT.load().json("/navigation/test-conf.json", "/conf");
    }

    @Test
    public void testFullNavigationTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_1);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
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
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testPartialNavigationTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_2);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
        };
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testPartialNavigationTreeContentPolicy() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_3);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1/navigation-1-1", 0, true, "/content/navigation/navigation-1/navigation-1-1.html"},
        };
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testNavigationNoRoot() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_4);
        assertEquals("Didn't expect any navigation items.", 0, navigation.getItems().size());
    }

    @Test
    public void testNavigationWithRootInDifferentTree() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_5);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
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
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testNavigationStartGreaterThanMax() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_6);
        assertNull("Didn't expect a model for an invalid configured component.", navigation);
    }

    @Test
    public void testPartialNavigationTreeNotOnlyCurrentPage() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_7);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testPartialNavigationTreeContentPolicyNotOnlyCurrentPage() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_8);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
        Object[][] expectedPages = {
                {"/content/navigation/navigation-1", 0, true, "/navigation-1-vanity"},
                {"/content/navigation/navigation-1/navigation-1-1", 1, true, "/content/navigation/navigation-1/navigation-1-1.html"},
                {"/content/navigation/navigation-2", 0, false, "/content/navigation/navigation-2.html"}
        };
        verifyNavigationItems(expectedPages, items);
    }

    @Test
    public void testCollectionOnTemplate() {
        Navigation navigation = getNavigationUnderTest(NAV_COMPONENT_IN_TEMPLATE);
        Map<String, NavigationItem> items = getNavigationItems(navigation);
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
                        "/content/navigation/navigation-1/navigation-1-1/navigation-1-1-2/navigation-1-1-2-3.html"}
        };
        verifyNavigationItems(expectedPages, items);
    }

    private Navigation getNavigationUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
        if (mapping == null) {
            throw new IllegalStateException("Adapter not registered for the ContentPolicyManager.");
        }
        ContentPolicy contentPolicy = mapping.getPolicy();
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

    private Map<String, NavigationItem> getNavigationItems(Navigation navigation) {
        Map<String, NavigationItem> items = new LinkedHashMap<>();
        for (NavigationItem item : navigation.getItems()) {
            collect(items, item);
        }
        return items;
    }

    private void collect(Map<String, NavigationItem> items, NavigationItem navigationItem) {
        if (items.put(navigationItem.getPage().getPath(), navigationItem) != null) {
            fail("NavigationItem " + navigationItem.getURL() + " seems to have already been included; invalid recursion collection in the" +
                    " implementation?!");
        }
        for (NavigationItem item : navigationItem.getChildren()) {
            collect(items, item);
        }
    }

    private void verifyNavigationItems(Object[][] expectedPages, Map<String, NavigationItem> items) {
        assertEquals("The navigation tree contains a different number of pages than expected.", expectedPages.length, items.size());
        int index = 0;
        for (String key : items.keySet()) {
            NavigationItem item = items.get(key);
            assertTrue("The navigation tree doesn't seem to have the correct order.", expectedPages[index][0].equals(item.getPage()
                    .getPath()));
            assertEquals("The navigation item's level is not what was expected: " + item.getPage()
                    .getPath(), expectedPages[index][1], item
                    .getLevel());
            assertEquals("The navigation item's active state is not what was expected: " + item.getPage()
                    .getPath(), expectedPages[index][2], item.isActive());
            assertEquals("The navigation item's URL is not what was expected: " + item.getPage()
                    .getPath(), CONTEXT_PATH + expectedPages[index][3], item.getURL());
            index++;
        }
    }
}
