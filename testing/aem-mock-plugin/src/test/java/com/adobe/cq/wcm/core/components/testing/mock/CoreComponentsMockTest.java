/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.testing.mock;

import static com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins.CORE_COMPONENTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.models.Title;
import com.adobe.cq.wcm.core.components.testing.MockExternalizerFactory;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

@ExtendWith(AemContextExtension.class)
class CoreComponentsMockTest {

  private AemContext context = new AemContextBuilder()
          .beforeSetUp(context -> {
              context.registerService(Externalizer.class, MockExternalizerFactory.getExternalizerService());
          })
          .plugin(CORE_COMPONENTS)
          .build();

  /**
   * Tests a component with link handling that depends on the presence of a path processor implementation.
   */
  @Test
  void testLinkHandling() {
      Page page = context.create().page("/content/test");
      Resource resource = context.create().resource(page, "title",
              "sling:resourceType", "core/wcm/components/title/v3/title",
              "linkURL", "https://example.com");
      context.currentResource(resource);

      Title title = context.request().adaptTo(Title.class);
      assertNotNull(title, "Unable instantiate title component");
      assertNotNull(title.getLink(), "Missing link");
      assertEquals("https://example.com", title.getLink().getURL());
  }

}
