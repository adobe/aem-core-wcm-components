/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import javax.annotation.Nullable;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.google.common.base.Function;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkflowModelDataSourceServletTest {

    @Rule
    public AemContext context = CoreComponentTestContext.createContext("/form/container/datasource/workflowmodeldatasource",
            "/apps/workflowdatasource");

    @Mock
    private WorkflowSession workflowSessionMock;

    @Mock
    private WorkflowModel workflowModelMock;

    private WorkflowModelDataSourceServlet dataSourceServlet;

    @Before
    public void setUp() throws Exception {
        when(workflowModelMock.getTitle()).thenReturn("Workflow Title");
        when(workflowModelMock.getId()).thenReturn("test/workflow");
        when(workflowSessionMock.getModels()).thenReturn(new WorkflowModel[]{workflowModelMock});
        registerWorkflowSessionAdapter();
    }

    private void registerWorkflowSessionAdapter() {
        context.registerAdapter(ResourceResolver.class, WorkflowSession.class, new Function<ResourceResolver, WorkflowSession>() {
            @Nullable
            @Override
            public WorkflowSession apply(@Nullable ResourceResolver input) {
                return workflowSessionMock;
            }
        });
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/workflowdatasource");
        dataSourceServlet = new WorkflowModelDataSourceServlet();
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        ValueMap valueMap = resource.adaptTo(ValueMap.class);
        assertEquals("Workflow Title", valueMap.get("text", String.class));
        assertEquals("test/workflow", valueMap.get("value", String.class));
    }
}
