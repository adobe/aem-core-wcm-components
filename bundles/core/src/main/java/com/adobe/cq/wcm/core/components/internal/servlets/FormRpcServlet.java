/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
import javax.annotation.Nonnull;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.services.form.FormPostService;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes=" + FormRpcServlet.RESOURCE_TYPE,
                "sling.servlet.selectors=" + FormRpcServlet.SELECTOR
        }
)
public class FormRpcServlet extends SlingAllMethodsServlet {

    static final String RESOURCE_TYPE = "core/wcm/components/form/actions/rpc";
    static final String SELECTOR = "post";

    @Reference
    private FormPostService formPostService;

    @Override
    protected void doPost(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws ServletException, IOException {
        formPostService.sendFormData(request, response);
    }
}
