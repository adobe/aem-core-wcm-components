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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.servlets.TextValueDataResourceSource;
import com.adobe.cq.wcm.core.components.testing.MockStyle;
import com.adobe.granite.ui.components.Value;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllowedEmbeddablesDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/embed/v1/datasources/allowedembeddables",
        "/apps");

    private AllowedEmbeddablesDataSourceServlet dataSourceServlet;

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private Designer designer;

    @Mock
    private ContentPolicy contentPolicy;

    private static final String CURRENT_PATH = "/apps/content/embed";

    private ValueMap properties;

    @Before
    public void setUp() {
        dataSourceServlet = new AllowedEmbeddablesDataSourceServlet();
        registerAdapter();
        context.currentResource(CURRENT_PATH);
    }

    @Test
    public void testAllowedEmbeddablesDataSourceServlet() {
        Resource policyResource = context.resourceResolver().getResource("/apps/conf/policy_1558011912823");
        properties = ResourceUtil.getValueMap(policyResource);
        when(contentPolicyManager.getPolicy(any(Resource.class))).thenReturn(contentPolicy);
        when(contentPolicy.getProperties()).thenReturn(properties);
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

    private void registerAdapter() {
        context.registerAdapter(ResourceResolver.class, ContentPolicyManager.class,
            new Function<ResourceResolver, ContentPolicyManager>() {
                @Nullable
                @Override
                public ContentPolicyManager apply(@Nullable ResourceResolver input) {
                    return contentPolicyManager;
                }
            });
        context.registerAdapter(ResourceResolver.class, Designer.class,
            new Function<ResourceResolver, Designer>() {
                @Nullable
                @Override
                public Designer apply(@Nullable ResourceResolver input) { return designer; }
            });
    }
}
