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
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.internal.form.FormConstants;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.wcm.foundation.forms.FormsManager;

@Component(
        service = { Servlet.class },
        property = {
                "sling.servlet.resourceTypes="+ FormActionTypeDataSourceServlet.RESOURCE_TYPE,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class FormActionTypeDataSourceServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 9114656669504668093L;

    public final static String RESOURCE_TYPE = FormConstants.RT_CORE_FORM_CONTAINER_DATASOURCE_V1 + "/actiontype";

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {
        SimpleDataSource actionTypeDataSource = new SimpleDataSource(getActionTypeResources(
                request.getResourceResolver()).iterator());
        request.setAttribute(DataSource.class.getName(), actionTypeDataSource);
    }

    private List<Resource> getActionTypeResources(ResourceResolver resourceResolver) {
        List<Resource> actionTypeResources = new ArrayList<>();
        FormsManager formsManager = resourceResolver.adaptTo(FormsManager.class);
        if (formsManager != null) {
            Iterator<FormsManager.ComponentDescription> actions = formsManager.getActions();
            while (actions.hasNext()) {
                FormsManager.ComponentDescription description = actions.next();
                Resource dialogResource = resourceResolver.getResource(description.getResourceType() + "/" + FormConstants.NN_DIALOG);
                if (dialogResource != null) {
                    actionTypeResources.add(new ActionTypeResource(description, resourceResolver));
                }
            }
        }
        return actionTypeResources;
    }

    private static class ActionTypeResource extends TextValueDataResourceSource {

        private final FormsManager.ComponentDescription description;

        ActionTypeResource(FormsManager.ComponentDescription description, ResourceResolver resourceResolver) {
            super(resourceResolver, StringUtils.EMPTY, NonExistingResource.RESOURCE_TYPE_NON_EXISTING);
            this.description = description;
        }

        @Override
        public String getText() {
            return description.getTitle();
        }

        @Override
        public String getValue() {
            return description.getResourceType();
        }
    }
}
