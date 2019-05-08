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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.apache.sling.api.SlingHttpServletRequest;
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

@RunWith(MockitoJUnitRunner.class)
public class ContainerBackgroundColorDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext(null, "/apps");

    @Mock
    private ContentPolicyManager contentPolicyManager;

    @Mock
    private ContentPolicy contentPolicy;

    @Mock
    private ValueMap properties;
    
    @Mock
    private SlingHttpServletRequest request;

    private ContainerBackgroundColorDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        dataSourceServlet = new ContainerBackgroundColorDataSourceServlet();
        registerContentPolicyManager();
    }

    @Test
    public void testDataSource() throws Exception {        
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            ContainerBackgroundColorDataSourceServlet containerBackgroundColorDataSource = (ContainerBackgroundColorDataSourceServlet) resource;
            assertNotNull("Test to check not null list",containerBackgroundColorDataSource.getColors(request));
        });
    }

    @Test
    public void testDataSourceWithInvalidValues() throws Exception {
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        dataSource.iterator().forEachRemaining(resource -> {
            ContainerBackgroundColorDataSourceServlet containerBackgroundColorDataSource = (ContainerBackgroundColorDataSourceServlet)resource;
            assertNull("Expected null type", containerBackgroundColorDataSource.getColors(request));
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
