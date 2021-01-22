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
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.NonExistingResource;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.services.form.FormHandler;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.foundation.forms.FormsHandlingRequest;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import com.day.cq.wcm.foundation.forms.ValidationInfo;

/**
 * This servlet is used by the core form container as a form action to send the form data to a remote endpoint.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes=" + FormActionRpcServlet.RESOURCE_TYPE,
                "sling.servlet.selectors=" + FormActionRpcServlet.SELECTOR
        }
)
public class FormActionRpcServlet extends SlingAllMethodsServlet {

    static final String SELECTOR = "post";
    static final String RESOURCE_TYPE = "core/wcm/components/form/actions/rpc";
    private static final String ATTR_RESOURCE = FormsHandlingServletHelper.class.getName() + "/resource";
    private static final String PN_FORM_ENDPOINT_URL = "externalServiceEndPointUrl";
    private static final Logger LOG = LoggerFactory.getLogger(FormActionRpcServlet.class);
    public static final String HTML_SUFFIX = ".html";

    @Reference
    private FormHandler formHandler;

    @Override
    protected void doPost(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
            throws ServletException {
        boolean processFormApiSuccess = false;
        Resource formContainerResource = request.getResource();
        ValueMap valueMap = formContainerResource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            String endPointUrl = valueMap.get(PN_FORM_ENDPOINT_URL, String.class);
            if (StringUtils.isNotEmpty(endPointUrl)) {
                JSONObject formData = new JSONObject();
                try {
                    formData = Utils.getJsonOfRequestParameters(request);
                } catch (JSONException e) {
                    LOG.error("Unable to get JSON form request parameter", e);
                }
                processFormApiSuccess = formHandler.forwardFormData(formData, endPointUrl);
            }
            sendRedirect(valueMap, request, response, processFormApiSuccess);
        }
    }

    private void sendRedirect(ValueMap valueMap, SlingHttpServletRequest request, SlingHttpServletResponse response,
                              boolean processFormApiSuccess)
            throws ServletException {
        String redirect = getMappedRedirect(valueMap.get("redirect", String.class), request.getResourceResolver());

        String errorMessage = valueMap.get("errorMessage", String.class);
        FormsHandlingRequest formRequest = new FormsHandlingRequest(request);
        try {
            if (StringUtils.isNotEmpty(redirect) && processFormApiSuccess) {
                response.sendRedirect(redirect);
            } else {
                if (!processFormApiSuccess && StringUtils.isNotEmpty(errorMessage)) {
                    ValidationInfo validationInfo = ValidationInfo.createValidationInfo(request);
                    validationInfo.addErrorMessage(null, errorMessage);
                }
                final Resource formResource = (Resource) request.getAttribute(ATTR_RESOURCE);
                request.removeAttribute(ATTR_RESOURCE);
                request.removeAttribute(ComponentContext.BYPASS_COMPONENT_HANDLING_ON_INCLUDE_ATTRIBUTE);
                RequestDispatcher requestDispatcher = request.getRequestDispatcher(formResource);
                if (requestDispatcher != null) {
                    requestDispatcher.forward(formRequest, response);
                } else {
                    throw new IOException("can't get request dispatcher to forward the response");
                }
            }
        } catch (IOException var13) {
            LOG.error("Error redirecting to {}", redirect);
        }
    }

    private String getMappedRedirect(String redirect, ResourceResolver resourceResolver) {
        String mappedRedirect = null;
        if (StringUtils.isNotEmpty(redirect)) {
            if (StringUtils.endsWith(redirect, HTML_SUFFIX)) {
                Resource resource = resourceResolver.resolve(redirect);
                if (!(resource instanceof NonExistingResource)) {
                    mappedRedirect = redirect;
                }
            } else {
                Resource resource = resourceResolver.getResource(redirect);
                if (resource != null) {
                    redirect += HTML_SUFFIX;
                    mappedRedirect = resourceResolver.map(redirect);
                }
            }
        }
        return mappedRedirect;
    }
}
