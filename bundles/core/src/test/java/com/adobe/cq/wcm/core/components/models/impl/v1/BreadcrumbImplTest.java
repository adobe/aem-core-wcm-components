/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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

package com.adobe.cq.wcm.core.components.models.impl.v1;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.context.MockStyle;
import com.adobe.cq.wcm.core.components.models.Breadcrumb;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BreadcrumbImplTest {

    private static final String CURRENT_PAGE = "/content/breadcrumb/women/shirts/devi-sleeveless-shirt";

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/breadcrumb", "/content/breadcrumb/women");

    private Breadcrumb underTest;
    private SlingBindings slingBindings;

    @Before
    public void setUp() throws Exception {
        Page page = context.currentPage(CURRENT_PAGE);
        slingBindings = (SlingBindings) context.request().getAttribute(SlingBindings.class.getName());
        slingBindings.put(WCMBindings.CURRENT_PAGE, page);
    }

    @Test
    public void testBreadcrumbItems() throws Exception {
        Resource resource = context.currentResource(CURRENT_PAGE
                + "/jcr:content/header/breadcrumb");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Breadcrumb.class);
        checkBreadcrumbConsistency(new String[]{"Women", "Devi Sleeveless Shirt"});
    }

    @Test
    public void testGetShowHidden() throws Exception {
        Resource resource = context.currentResource(CURRENT_PAGE
                + "/jcr:content/header/breadcrumb-show-hidden");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Breadcrumb.class);
        checkBreadcrumbConsistency(new String[]{"Women", "Shirts", "Devi Sleeveless Shirt"});
    }

    @Test
    public void testGetHideCurrent() throws Exception {
        Resource resource = context.currentResource(CURRENT_PAGE
                + "/jcr:content/header/breadcrumb-hide-current");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Breadcrumb.class);
        checkBreadcrumbConsistency(new String[]{"Women"});
    }

    @Test
    public void testStartLevel() throws Exception {
        Resource resource = context.currentResource(CURRENT_PAGE
                + "/jcr:content/header/breadcrumb-start-level");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.CURRENT_STYLE, new MockStyle(resource));
        underTest = context.request().adaptTo(Breadcrumb.class);
        checkBreadcrumbConsistency(new String[]{"Shirts", "Devi Sleeveless Shirt"});
    }

    @Test
    public void testStyleBasedBreadcrumb() throws Exception {
        Resource resource = context.currentResource(CURRENT_PAGE
                + "/jcr:content/header/breadcrumb-style-based");
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        Style style = mock(Style.class);
        when(style.get(BreadcrumbImpl.PN_START_LEVEL, BreadcrumbImpl.PROP_START_LEVEL_DEFAULT)).thenReturn(3);
        when(style.get(BreadcrumbImpl.PN_HIDE_CURRENT, BreadcrumbImpl.PROP_SHOW_HIDDEN_DEFAULT)).thenReturn(false);
        when(style.get(BreadcrumbImpl.PN_SHOW_HIDDEN, BreadcrumbImpl.PROP_SHOW_HIDDEN_DEFAULT)).thenReturn(false);
        slingBindings.put(WCMBindings.CURRENT_STYLE, style);
        underTest = context.request().adaptTo(Breadcrumb.class);
        checkBreadcrumbConsistency(new String[]{"Devi Sleeveless Shirt"});
    }

    private void checkBreadcrumbConsistency(String[] expectedPages) {
        assertTrue("Expected that the returned breadcrumb will contain " + expectedPages.length + " items",
                underTest.getItems().size() == expectedPages.length);
        int index = 0;
        for (NavigationItem item : underTest.getItems()) {
            assertEquals(expectedPages[index++], item.getPage().getTitle());
        }
    }

}