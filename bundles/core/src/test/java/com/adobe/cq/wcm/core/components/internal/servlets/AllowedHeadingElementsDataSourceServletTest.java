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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class AllowedHeadingElementsDataSourceServletTest {

    private static final String TEST_BASE = "/title/datasource/allowedheadingelements";
    private static final String TEST_PAGE = "/content/title";
    private static final String TITLE_RESOURCE_JCR_TITLE_TYPE = TEST_PAGE + "/jcr:content/par/title-jcr-title-type";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private AllowedHeadingElementsDataSourceServlet dataSourceServlet;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, TEST_PAGE);
        dataSourceServlet = new AllowedHeadingElementsDataSourceServlet();
        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE,TITLE_RESOURCE_JCR_TITLE_TYPE);
    }

    @Test
    void testDataSource() throws Exception {
        context.contentPolicyMapping("core/wcm/components/title/v1/title",
                "type", "h3",
                AllowedHeadingElementsDataSourceServlet.PN_ALLOWED_TYPES, new String[]{"h3", "h4"});
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue(TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()), "Expected class");
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue(textValueDataResourceSource.getText().matches("h[3|4]"), "Expected type in (h3, h4)");
            assertTrue(textValueDataResourceSource.getValue().matches("h[3|4]"), "Expected value in (h3, h4)");
            if (textValueDataResourceSource.getValue().equals("h3")) {
                assertTrue(textValueDataResourceSource.getSelected());
            } else {
                assertFalse(textValueDataResourceSource.getSelected());
            }
        });
    }

    @Test
    void testDataSourceWithInvalidValues() throws Exception {
        context.contentPolicyMapping("core/wcm/components/title/v1/title",
                AllowedHeadingElementsDataSourceServlet.PN_ALLOWED_TYPES, new String[]{"foo", "h10"});
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue(TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()), "Expected class");
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertNull(textValueDataResourceSource.getText(), "Expected null type");
            assertTrue(textValueDataResourceSource.getValue().matches("foo|h10"), "Expected value in (foo, h10)");
        });
    }
}
