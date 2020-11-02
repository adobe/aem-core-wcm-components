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

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.designer.Designer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class AllowedEmbeddablesDataSourceServletTest {

    private static final String TEST_BASE = "/embed/v1/datasources/allowedembeddables";
    private static final String APPS_ROOT = "/apps";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private final AllowedEmbeddablesDataSourceServlet dataSourceServlet = new AllowedEmbeddablesDataSourceServlet();

    private static final String CURRENT_PATH = "/apps/content/embed";

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);
        context.currentResource(CURRENT_PATH);
    }

    @Test
    public void testAllowedEmbeddablesDataSourceServlet() {
        context.contentPolicyMapping("my-app/components/embed",
            Objects.requireNonNull(context.resourceResolver().getResource("/apps/conf/policy_1558011912823"))
                .getValueMap());

        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE, CURRENT_PATH);
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        validateAllowedEmbeddables(dataSource, getExpectedAllowedEmbeddables(new String[][]{
                {"Select", ""},
                {"Chatbot", "/apps/my-app/chatbot"},
                {"Social", "/apps/my-app/social"}
        }));
    }

    @Test
    public void testAllowedEmbeddablesDesignDataSourceServlet() {
        Designer designer = mock(Designer.class);
        context.registerAdapter(ResourceResolver.class, Designer.class,
            (Function<ResourceResolver, Designer>) input -> designer);
        Resource styleResource = context.resourceResolver().getResource("/apps/etc/designs/embed");
        MockStyle mockStyle = new MockStyle(styleResource, styleResource.getValueMap());
        when(designer.getStyle(any(Resource.class))).thenReturn(mockStyle);
        context.request().setAttribute(Value.CONTENTPATH_ATTRIBUTE, CURRENT_PATH);
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        validateAllowedEmbeddables(dataSource, getExpectedAllowedEmbeddables(new String[][]{
                {"Select", ""},
                {"Chatbot", "/apps/my-app/chatbot"},
                {"Social", "/apps/my-app/social"}
        }));
    }

    private TextValueDataResourceSource[] getExpectedAllowedEmbeddables(String[][] expectedAllowedEmbeddables) {
        TextValueDataResourceSource[] textValueDataResourceSources = new TextValueDataResourceSource[expectedAllowedEmbeddables.length];
        for (int i = 0; i < expectedAllowedEmbeddables.length; i++) {
            final int index = i;
            textValueDataResourceSources[i] = new TextValueDataResourceSource(context.resourceResolver(), "", "") {
                @Override
                public String getText() {
                    return expectedAllowedEmbeddables[index][0];
                }

                @Override
                public String getValue() {
                    return expectedAllowedEmbeddables[index][1];
                }
            };
        }
        return textValueDataResourceSources;
    }

    private void validateAllowedEmbeddables(DataSource dataSource, TextValueDataResourceSource ... textValueDataResourceSources) {
        Iterator<Resource> iterator = dataSource.iterator();
        int items = 0;
        while (iterator.hasNext()) {
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource)iterator.next();
            assertEquals(textValueDataResourceSources[items].getValue(), textValueDataResourceSource.getValue());
            assertEquals(textValueDataResourceSources[items].getText(), textValueDataResourceSource.getText());
            items++;
        }
        assertEquals(textValueDataResourceSources.length, items);
    }
}
