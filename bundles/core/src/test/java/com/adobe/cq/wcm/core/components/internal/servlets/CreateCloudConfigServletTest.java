/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class CreateCloudConfigServletTest {

  private final AemContext context = CoreComponentTestContext.newAemContext();

  @BeforeEach
  public void init() {
    context.load().json("/cloudconfig/cloudconfig.json", "/conf/test");

    ConfigurationResourceResolver configrr = Mockito.mock(ConfigurationResourceResolver.class);
    Mockito.when(configrr.getResourceCollection(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Collections.singletonList(context.resourceResolver().getResource("/conf/test")));
    context.registerService(ConfigurationResourceResolver.class, configrr);

  }

  @Test
  public void testDoPost() throws IOException {

    CreateCloudConfigServlet cccSrvlt = new CreateCloudConfigServlet();

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("configPath", "/conf/test2");
    parameterMap.put("title", "Test");
    parameterMap.put("name", "test");
    parameterMap.put("template", "/apps/core/wcm/templates/marketocloudconfig");

    context.request().setParameterMap(parameterMap);

    cccSrvlt.doPost(context.request(), context.response());

    assertNotNull(context.response().getOutputAsString());
    assertTrue(context.response().getOutputAsString().contains("Created Cloud Configuration"));
    assertNotNull(context.resourceResolver().getResource("/conf/test2/settings/cloudconfigs/test"));
    ValueMap properties = Optional
        .ofNullable(context.resourceResolver().getResource("/conf/test2/settings/cloudconfigs/test/jcr:content"))
        .map(r -> r.getValueMap()).orElse(null);
    assertEquals("Test", properties.get("jcr:title", String.class));
    assertEquals("/apps/core/wcm/templates/marketocloudconfig", properties.get("cq:template", String.class));
  }

  @Test
  public void testInvalid() throws IOException {

    CreateCloudConfigServlet cccSrvlt = new CreateCloudConfigServlet();

    Map<String, Object> parameterMap = new HashMap<>();
    parameterMap.put("configPath", "/conf/test2");
    parameterMap.put("name", "test2");
    parameterMap.put("template", "/apps/core/wcm/templates/marketocloudconfig");

    context.request().setParameterMap(parameterMap);

    try {
      cccSrvlt.doPost(context.request(), context.response());
      fail();
    } catch (IOException e) {
      assertNull(context.resourceResolver().getResource("/conf/test2/settings/cloudconfigs/test2"));
    }

  }

}
