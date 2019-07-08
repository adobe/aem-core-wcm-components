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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMBindings;
import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageTest;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.testing.MockContentPolicyStyle;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyMapping;

public class ImageImplTest extends AbstractImageTest {

  protected static String TEST_BASE = "/image/v3";

  @BeforeClass
  public static void setUp() {
    internalSetUp(CONTEXT, TEST_BASE);
  }

  @Before
  public void setDefaultRunMode() throws Exception {
    CONTEXT.runMode("publish");
  }

  @Test
  public void testLinkTrackingCodeInAuthor() {
    CONTEXT.runMode("author");
    Image image = getImageUnderTest(AbstractImageTest.IMAGE0_PATH, Image.class, null);
    assertEquals("", image.getLinkTrackingCode());
    Utils.testJSONExport(image,
        Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE0_PATH));
  }

  @Test
  public void testLinkTrackingCode() {
    Image image = getImageUnderTest(AbstractImageTest.IMAGE1_PATH, Image.class, null);
    assertEquals("s_objectID='test image 1111311570';", image.getLinkTrackingCode());
    Utils.testJSONExport(image,
        Utils.getTestExporterJSONPath(TEST_BASE, AbstractImageTest.IMAGE1_PATH));
  }

  protected <T> T getImageUnderTest(String resourcePath, Class<T> imageClass,
      String policyDelegatePath) {
    Resource resource = CONTEXT.resourceResolver().getResource(resourcePath);
    if (resource == null) {
      throw new IllegalStateException("Does the test resource " + resourcePath + " exist?");
    }
    ContentPolicyMapping mapping = resource.adaptTo(ContentPolicyMapping.class);
    ContentPolicy contentPolicy = null;
    if (mapping != null) {
      contentPolicy = mapping.getPolicy();
    }
    SlingBindings slingBindings = new SlingBindings();
    Style style = null;
    if (contentPolicy != null) {
      when(contentPolicyManager.getPolicy(resource)).thenReturn(contentPolicy);
      style = new MockContentPolicyStyle(contentPolicy);
    }
    if (style == null) {
      style = mock(Style.class);
      when(style.get(anyString(), (Object) Matchers.anyObject()))
          .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[1]);
    }
    slingBindings.put(SlingBindings.RESOURCE, resource);
    final MockSlingHttpServletRequest request =
        new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
    request.setContextPath(CONTEXT_PATH);
    request.setResource(resource);
    Page page = CONTEXT.pageManager().getPage(PAGE);
    slingBindings.put(WCMBindings.CURRENT_PAGE, page);
    slingBindings.put(WCMBindings.WCM_MODE, new SightlyWCMMode(request));
    slingBindings.put(WCMBindings.PAGE_MANAGER, CONTEXT.pageManager());
    slingBindings.put(WCMBindings.CURRENT_STYLE, style);
    slingBindings.put(WCMBindings.PROPERTIES, resource.adaptTo(ValueMap.class));
    Component component = mock(Component.class);
    when(component.getCellName()).thenReturn("image");
    slingBindings.put(WCMBindings.COMPONENT, component);
    request.setAttribute(SlingBindings.class.getName(), slingBindings);
    if (StringUtils.isNotBlank(policyDelegatePath)) {
      request.setParameterMap(new HashMap<String, Object>() {
        {
          put("contentPolicyDelegatePath", policyDelegatePath);
        }
      });
    }
    return request.adaptTo(imageClass);
  }
}
