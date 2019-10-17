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
package com.adobe.cq.wcm.core.components.internal.models.v1.marketo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.marketo.FormValue;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
public class TestFormValueImpl {

  private final AemContext context = CoreComponentTestContext.newAemContext();

  @BeforeEach
  void init() {
    context.addModelsForPackage("com.adobe.acs.commons.marketo.models",
        "com.adobe.cq.wcm.core.components.internal.models.v1.marketo");
    context.load().json("/marketo/formvalue.json", "/content");
  }

  @Test
  void valid() throws IOException {
    context.currentResource("/content/formvalue/jcr:content/root/valid");
    FormValue formValue = Optional.ofNullable(context.currentResource()).map(r -> r.adaptTo(FormValue.class))
        .orElse(null);
    assertNotNull(formValue);

    assertEquals("Test", formValue.getName());
    assertEquals("static", formValue.getSource());
    assertEquals("Value", formValue.getValue());
  }

  @Test
  void invalid() {
    context.currentResource("/content/formvalue/jcr:content/root/invalid");
    FormValue invalid = Optional.ofNullable(context.currentResource()).map(r -> r.adaptTo(FormValue.class))
        .orElse(null);
    assertNull(invalid);
  }
}
