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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BreadcrumbImplTest {

    private static final String TEST_BASE = "/breadcrumb";
    private static final String CURRENT_PAGE = "/content/breadcrumb/women/shirts/devi-sleeveless-shirt";
    private static final String BREADCRUMB_1 = CURRENT_PAGE + "/jcr:content/header/breadcrumb";
    private static final String BREADCRUMB_2 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-show-hidden";
    private static final String BREADCRUMB_3 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-hide-current";
    private static final String BREADCRUMB_4 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-start-level";
    private static final String BREADCRUMB_5 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-style-based";
    private static final String BREADCRUMB_6 = CURRENT_PAGE + "/jcr:content/header/breadcrumb-v2";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, "/content/breadcrumb/women");

    @Test
    public void testBreadcrumbItems() throws Exception {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_1);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_1));
    }

    @Test
    public void testGetShowHidden() throws Exception {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_2);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women", "Shirts", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_2));
    }

    @Test
    public void testGetHideCurrent() throws Exception {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_3);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Women"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_3));
    }

    @Test
    public void testStartLevel() throws Exception {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_4);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Shirts", "Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_4));
    }

    @Test
    public void testStyleBasedBreadcrumb() throws Exception {
        Style style = mock(Style.class);
        when(style.get(BreadcrumbImpl.PN_START_LEVEL, BreadcrumbImpl.PROP_START_LEVEL_DEFAULT)).thenReturn(3);
        when(style.get(BreadcrumbImpl.PN_HIDE_CURRENT, BreadcrumbImpl.PROP_SHOW_HIDDEN_DEFAULT)).thenReturn(false);
        when(style.get(BreadcrumbImpl.PN_SHOW_HIDDEN, BreadcrumbImpl.PROP_SHOW_HIDDEN_DEFAULT)).thenReturn(false);
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_5, style);
        checkBreadcrumbConsistency(breadcrumb, new String[]{"Devi Sleeveless Shirt"});
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_5));
    }

    @Test
    public void testV2JSONExporter() throws Exception {
        Breadcrumb breadcrumb = getBreadcrumbUnderTest(BREADCRUMB_6);
        Utils.testJSONExport(breadcrumb, Utils.getTestExporterJSONPath(TEST_BASE, BREADCRUMB_6));
    }

    private void checkBreadcrumbConsistency(Breadcrumb breadcrumb, String[] expectedPages) {
        assertTrue("Expected that the returned breadcrumb will contain " + expectedPages.length + " items",
                breadcrumb.getItems().size() == expectedPages.length);
        int index = 0;
        for (NavigationItem item : breadcrumb.getItems()) {
            assertEquals(expectedPages[index++], item.getTitle());
        }
    }

    private Breadcrumb getBreadcrumbUnderTest(String resourcePath) {
        return getBreadcrumbUnderTest(resourcePath, null);
    }

    private Breadcrumb getBreadcrumbUnderTest(String resourcePath, Style style) {
        Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Did you forget to define test resource " + resourcePath + "?");
        }
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        request.setResource(resource);
        request.setContextPath("");
        SlingBindings bindings = new SlingBindings();
        bindings.put(SlingBindings.RESOURCE, resource);
        bindings.put(WCMBindings.PROPERTIES, resource.getValueMap());
        bindings.put(WCMBindings.CURRENT_PAGE, CONTEXT.pageManager().getPage(CURRENT_PAGE));
        if (style == null) {
            style = mock(Style.class);
            when(style.get(any(), any(Object.class))).thenAnswer(
                    invocation -> invocation.getArguments()[1]
            );
        }
        bindings.put(WCMBindings.CURRENT_STYLE, style);
        request.setAttribute(SlingBindings.class.getName(), bindings);
        return request.adaptTo(Breadcrumb.class);
    }

}
