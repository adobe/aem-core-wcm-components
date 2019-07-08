/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.wcm.api.components.Component;

import io.wcm.testing.mock.aem.junit.AemContext;

public class TeaserImplTest {

    private static final String TEST_BASE = "/teaser/v2";
    private static final String CONTENT_ROOT = "/content";
    private static final String CURRENT_PAGE = "/content/teasers";
    private static final String PNG_IMAGE_BINARY_NAME = "Adobe_Systems_logo_and_wordmark.png";
    private static final String PNG_ASSET_PATH = "/content/dam/core/images/" + PNG_IMAGE_BINARY_NAME;
    private static final String CONTEXT_PATH = "/core";
    private static final String TEST_ROOT_PAGE = "/content/teasers";
    private static final String TEST_ROOT_PAGE_GRID = "/jcr:content/root/responsivegrid";
    private static final String TEASER_15 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-15";
    private static final String TEASER_28 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-28";
    private static final String TEASER_26 = TEST_ROOT_PAGE + TEST_ROOT_PAGE_GRID + "/teaser-26";
    private static final String TITLE = "Teasers Test";
    private static final String DESCRIPTION = "Description";
    private static final String TEST_APPS_ROOT = "/apps/core/wcm/components";

    @ClassRule
    public static final AemContext AEM_CONTEXT = CoreComponentTestContext.createContext(TEST_BASE, CONTENT_ROOT);

    @BeforeClass
    public static void setUp() throws Exception {
        AEM_CONTEXT.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, TEST_APPS_ROOT);
        AEM_CONTEXT.load().binaryFile("/image/" + PNG_IMAGE_BINARY_NAME,
                PNG_ASSET_PATH + "/jcr:content/renditions/original");
    }
    
    @Before
    public void setDefaultRunMode() throws Exception {
        AEM_CONTEXT.runMode("publish");
    }

    @Test
    public void testFullyConfiguredTeaserVanityPath() {
        Teaser teaser = getTeaserUnderTest(TEASER_15);
        assertEquals(TEASER_15, teaser.getImageResource().getPath());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals("https://www.adobe.com", teaser.getLinkURL());
        assertEquals("s_objectID='teasers teaser 1291652432';", teaser.getLinkTrackingCode());
        Utils.testJSONExport(teaser, Utils.getTestExporterJSONPath(TEST_BASE, "teaser15"));
    }
    
    @Test
    public void testTeaserInAuthorMode() {
        Teaser teaser = getTeaserUnderTest(TEASER_15, "author");
        assertEquals(TEASER_15, teaser.getImageResource().getPath());
        assertEquals(TITLE, teaser.getTitle());
        assertEquals(DESCRIPTION, teaser.getDescription());
        assertEquals("https://www.adobe.com", teaser.getLinkURL());
        assertEquals("", teaser.getLinkTrackingCode());
    }

    @Test
    public void testPageInheritedProperties() {
        Teaser teaser = getTeaserUnderTest(TEASER_26);
        assertEquals("Teaser Test", teaser.getTitle());
        assertEquals("/core/content/teasers.html", teaser.getLinkURL());
        assertEquals(false, teaser.isActionsEnabled());
    }

    @Test
    public void testTeaserWithTitleAndDescriptionFromActions() {
        Teaser teaser1 = getTeaserUnderTest(TEASER_28);
        assertTrue("Expected teaser with actions", teaser1.isActionsEnabled());
        assertEquals("Expected to find two Actions", 2, teaser1.getActions().size());
        assertEquals(2, teaser1.getActions().size());
        ListItem action = teaser1.getActions().get(0);
        assertEquals("Action link does not match", "/content/teasers", action.getPath());
        assertEquals("Action text does not match", "Teasers", action.getTitle());
        assertEquals("Action text does not match", "s_objectID='teasers teaser 1291652432 1';", action.getLinkTrackingCode());
        action = teaser1.getActions().get(1);
        assertEquals("Action link does not match", "http://www.adobe.com", action.getPath());
        assertEquals("Action text does not match", "Adobe", action.getTitle());
        assertEquals("Action text does not match", "s_objectID='teasers teaser 1291652432 2';", action.getLinkTrackingCode());
    }

    private Teaser getTeaserUnderTest(String resourcePath) {
        Resource resource = AEM_CONTEXT.resourceResolver().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
        }
        final MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(AEM_CONTEXT.resourceResolver(),
                AEM_CONTEXT.bundleContext());
        request.setContextPath(CONTEXT_PATH);
        request.setResource(resource);
        SlingBindings slingBindings = new SlingBindings();
        slingBindings.put(SlingBindings.RESOURCE, resource);
        slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
        slingBindings.put(WCMBindings.PAGE_MANAGER, AEM_CONTEXT.pageManager());
        slingBindings.put(WCMBindings.CURRENT_PAGE, AEM_CONTEXT.pageManager().getPage(CURRENT_PAGE));
        Component component = mock(Component.class);
        when(component.getProperties()).thenReturn(new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(AbstractImageDelegatingModel.IMAGE_DELEGATE, "core/wcm/components/image/v2/image");
            }
        }));
        when(component.getCellName()).thenReturn("teaser");
        slingBindings.put(WCMBindings.COMPONENT, component);
        request.setAttribute(SlingBindings.class.getName(), slingBindings);
        return request.adaptTo(Teaser.class);
    }
    
    private Teaser getTeaserUnderTest(String resourcePath, String runMode) {
      AEM_CONTEXT.runMode(runMode);
      return getTeaserUnderTest(resourcePath);
    }

}
