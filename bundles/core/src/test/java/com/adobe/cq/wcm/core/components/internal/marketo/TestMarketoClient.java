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
package com.adobe.cq.wcm.core.components.internal.marketo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.marketo.MarketoClient;
import com.adobe.cq.wcm.core.components.marketo.MarketoField;
import com.adobe.cq.wcm.core.components.marketo.MarketoForm;
import com.adobe.cq.wcm.core.components.marketo.StaticResponseMarketoClient;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfiguration;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class TestMarketoClient {

  private MarketoClientConfiguration config = new MarketoClientConfiguration() {

    @Override
    public String getClientId() {
      return "CLIENT";
    }

    @Override
    public String getClientSecret() {
      return "SECRET";
    }

    @Override
    public String getEndpointHost() {
      return "SERVER";
    }

  };

  private final AemContext context = CoreComponentTestContext.newAemContext();

  @BeforeEach
  public void init() {
    context.addModelsForPackage("com.adobe.cq.wcm.core.components.models.marketo");
    context.addModelsForPackage("com.adobe.cq.wcm.core.components.internal.models.marketo");
    context.load().json("/marketo/pages.json", "/content/page");
    context.load().json("/marketo/cloudconfig.json", "/etc/cloudservices/marketo/test");
  }

  @Test
  public void testError() throws IOException {
    MarketoClient client = new StaticResponseMarketoClient("/marketo/response-error.json");
    try {
      client.getForms(config);
      fail();
    } catch (IOException e) {
      assertEquals("Retrieved errors in response: Access token invalid", e.getMessage());
    }
  }

  @Test
  public void testGetFields() throws IOException {
    MarketoClient client = new StaticResponseMarketoClient(new String[] {
        "/marketo/token-response.json", "/marketo/field-response.json",
        "/marketo/response-noassets.json" });
    List<MarketoField> fields = client.getFields(config);
    assertNotNull(fields);
    assertFalse(fields.isEmpty());
    assertEquals(1, fields.size());
    
    assertEquals("Address", fields.get(0).getId());
  }

  @Test
  public void testGetForms() throws IOException {
    MarketoClient client = new StaticResponseMarketoClient(new String[] {
        "/marketo/token-response.json", "/marketo/form-response.json",
        "/marketo/response-noassets.json" });
    List<MarketoForm> forms = client.getForms(config);
    assertNotNull(forms);
    assertFalse(forms.isEmpty());
    assertEquals(1, forms.size());
    
    assertEquals("MarketoForm [id=1, locale=en_US, name=Sample Form]", forms.get(0).toString());
  }

  @Test
  public void testGetToken() throws IOException {
    MarketoClient client = new StaticResponseMarketoClient("/marketo/token-response.json");
    String token = client.getApiToken(config);
    assertNotNull(token);
    assertEquals("TOKEN", token);
  }

  @Test
  public void testNotSuccess() throws IOException {
    MarketoClient client = new StaticResponseMarketoClient("/marketo/response-notsuccess.json");
    try {
      client.getForms(config);
      fail();
    } catch (IOException e) {
      // expected to occur
    }
  }
}
