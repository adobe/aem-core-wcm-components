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
package com.adobe.cq.wcm.core.components.internal.servlets;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AllowedHeadingElementsDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(null, "/apps");

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private ContentPolicy contentPolicy;

    @Mock
    private ValueMap properties;

    private AllowedHeadingElementsDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        dataSourceServlet = new AllowedHeadingElementsDataSourceServlet();
        registerContentPolicyManager();
        when(contentPolicyManager.getPolicy(context.currentResource())).thenReturn(contentPolicy);
        when(contentPolicy.getProperties()).thenReturn(properties);
    }

    @Test
    public void testDataSource() throws Exception {
        when(properties.get(AllowedHeadingElementsDataSourceServlet.PN_ALLOWED_HEADING_ELEMENTS, String[].class))
                .thenReturn(new String[]{"h3", "h4"});
        when(properties.get(AllowedHeadingElementsDataSourceServlet.PN_DEFAULT_TYPE, String.class)).thenReturn("h3");
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue("Expected class", TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()));
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertTrue("Expected type in (h3, h4)", textValueDataResourceSource.getText().matches("h[3|4]"));
            assertTrue("Expected value in (h3, h4)", textValueDataResourceSource.getValue().matches("h[3|4]"));
            if (textValueDataResourceSource.getValue().equals("h3")) {
                assertTrue(textValueDataResourceSource.getSelected());
            } else {
                assertFalse(textValueDataResourceSource.getSelected());
            }
        });
    }

    @Test
    public void testDataSourceWithInvalidValues() throws Exception {
        when(properties.get(AllowedHeadingElementsDataSourceServlet.PN_ALLOWED_HEADING_ELEMENTS, String[].class))
                .thenReturn(new String[]{"foo", "h10"});
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            assertTrue("Expected class", TextValueDataResourceSource.class.isAssignableFrom(resource.getClass()));
            TextValueDataResourceSource textValueDataResourceSource = (TextValueDataResourceSource) resource;
            assertNull("Expected null type", textValueDataResourceSource.getText());
            assertTrue("Expected value in (foo, h10)", textValueDataResourceSource.getValue().matches("foo|h10"));
        });
    }

    private void registerContentPolicyManager() {
        context.registerAdapter(ResourceResolver.class, ContentPolicyManager.class, new Function<ResourceResolver, ContentPolicyManager>() {
            @Nullable
            @Override
            public ContentPolicyManager apply(@Nullable ResourceResolver input) {
                return contentPolicyManager;
            }
        });
    }
}
