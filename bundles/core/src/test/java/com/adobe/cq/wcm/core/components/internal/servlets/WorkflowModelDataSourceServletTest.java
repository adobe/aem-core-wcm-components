/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.model.WorkflowModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class WorkflowModelDataSourceServletTest {

    private static final String TEST_BASE = "/form/container/datasource/workflowmodeldatasource";
    private static final String APPS_ROOT = "/apps/workflowdatasource";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() throws Exception {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);

        WorkflowSession workflowSessionMock = mock(WorkflowSession.class);
        WorkflowModel workflowModelMock = mock(WorkflowModel.class);
        when(workflowModelMock.getTitle()).thenReturn("Workflow Title");
        when(workflowModelMock.getId()).thenReturn("test/workflow");
        when(workflowSessionMock.getModels()).thenReturn(new WorkflowModel[]{workflowModelMock});
        context.registerAdapter(ResourceResolver.class, WorkflowSession.class,
            (Function<ResourceResolver, WorkflowSession>) input -> workflowSessionMock);
    }

    @Test
    public void testDataSource() throws Exception {
        context.currentResource("/apps/workflowdatasource");
        WorkflowModelDataSourceServlet dataSourceServlet = new WorkflowModelDataSourceServlet();
        dataSourceServlet.doGet(context.request(), context.response());
        DataSource dataSource = (DataSource) context.request().getAttribute(DataSource.class.getName());
        assertNotNull(dataSource);
        Resource resource = dataSource.iterator().next();
        assertEquals("Workflow Title", resource.getValueMap().get("text", String.class));
        assertEquals("test/workflow", resource.getValueMap().get("value", String.class));
    }
}
