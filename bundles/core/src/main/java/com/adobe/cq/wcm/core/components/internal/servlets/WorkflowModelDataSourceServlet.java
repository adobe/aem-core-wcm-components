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

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.model.WorkflowModel;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ WorkflowModelDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class WorkflowModelDataSourceServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 9114656669504668093L;

    public final static String RESOURCE_TYPE = FormConstants.RT_CORE_FORM_CONTAINER_DATASOURCE_V1 + "/workflowmodel";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            WorkflowSession workflowSession = request.getResourceResolver().adaptTo(WorkflowSession.class);
            ArrayList<Resource> resources = new ArrayList<>();
            if (workflowSession != null) {
                WorkflowModel[] models = workflowSession.getModels();
                for (WorkflowModel model : models) {
                    resources.add(new WorkflowModelResource(model, request.getResourceResolver()));
                }
            }
            SimpleDataSource dataSource = new SimpleDataSource(resources.iterator());
            request.setAttribute(DataSource.class.getName(), dataSource);
        } catch (WorkflowException e) {
            throw new ServletException(e);
        }
    }

    private static class WorkflowModelResource extends TextValueDataResourceSource {

        private final WorkflowModel model;

        WorkflowModelResource(WorkflowModel model, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, RESOURCE_TYPE_NON_EXISTING);
            this.model = model;
        }

        @Override
        public String getText() {
            return model.getTitle();
        }

        @Override
        public String getValue() {
            return model.getId();
        }
    }
}
