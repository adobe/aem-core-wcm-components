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
package com.adobe.cq.wcm.core.components.internal.servlets.embed;

import com.adobe.granite.ui.components.Value;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import java.util.Objects;

@ExtendWith(AemContextExtension.class)
public class EmbeddableOptionsDataSourceServletTest {

    private static final String TEST_BASE = "/embed/v1/datasources/allowedembeddables";
    private static final String APPS_ROOT = "/apps";
    private static final String CURRENT_PATH = "/apps/content/embed";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private final EmbeddableOptionsDataSourceServlet dataSourceServlet = new EmbeddableOptionsDataSourceServlet();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);
        context.contentPolicyMapping("my-app/components/embed",
            Objects.requireNonNull(context.resourceResolver().getResource("/apps/conf/policy_1558011912823"))
                .getValueMap());
        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE, CURRENT_PATH);
    }

    @Test
    public void testEmbeddableOptionsDataSourceServlet() {
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        Assertions.assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(Assertions::assertNotNull);
    }
}
