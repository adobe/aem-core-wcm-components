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

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Collections;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.marketo.MarketoClient;
import com.adobe.cq.wcm.core.components.marketo.StaticResponseMarketoClient;
import com.adobe.granite.ui.components.ds.DataSource;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class TestMarketoFormDataSource {

  private final AemContext context = CoreComponentTestContext.newAemContext();

  @BeforeEach
  public void init() {
    context.addModelsForPackage("com.adobe.acs.commons.marketo.models",
        "com.adobe.cq.wcm.core.components.internal.models.v1.marketo");
    context.load().json("/marketo/pages.json", "/content/page");
    context.load().json("/marketo/cloudconfig.json", "/conf/test");

    Resource resource = Mockito.mock(Resource.class);
    Mockito.when(resource.getPath()).thenReturn("/mnt/somewhere");
    Mockito.when(resource.getResourceResolver()).thenReturn(context.resourceResolver());
    context.request().setResource(resource);
    context.requestPathInfo().setSuffix("/content/page");

  }

  @Test
  public void testdoGet() throws IOException {

    ConfigurationResourceResolver configrr = Mockito.mock(ConfigurationResourceResolver.class);
    Mockito.when(configrr.getResourceCollection(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Collections.singletonList(context.resourceResolver().getResource("/conf/test")));
    context.registerService(ConfigurationResourceResolver.class, configrr);

    MarketoFormDataSource mktoDataSrc = new MarketoFormDataSource();

    MarketoClient client = new StaticResponseMarketoClient(new String[] { "/marketo/token-response.json",
        "/marketo/form-response.json", "/marketo/response-noassets.json" });
    mktoDataSrc.bindMarketoClient(client);

    mktoDataSrc.doGet(context.request(), context.response());

    assertNotNull(context.request().getAttribute(DataSource.class.getName()));
  }

  @Test
  public void testInvalidResource() {

    ConfigurationResourceResolver configrr = Mockito.mock(ConfigurationResourceResolver.class);
    Mockito.when(configrr.getResourceCollection(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Collections.emptyList());
    context.registerService(ConfigurationResourceResolver.class, configrr);

    MarketoFormDataSource mktoDataSrc = new MarketoFormDataSource();

    MarketoClient client = new StaticResponseMarketoClient(new String[] { "/marketo/token-response.json",
        "/marketo/form-response.json", "/marketo/response-noassets.json" });
    mktoDataSrc.bindMarketoClient(client);

    mktoDataSrc.doGet(context.request(), context.response());

    assertNotNull(context.request().getAttribute(DataSource.class.getName()));
  }
}
