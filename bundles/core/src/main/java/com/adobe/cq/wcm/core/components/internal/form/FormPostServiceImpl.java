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
package com.adobe.cq.wcm.core.components.internal.form;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlets.post.JSONResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.form.FormPostService;
import com.day.cq.wcm.foundation.forms.FormsHandlingRequest;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import com.day.cq.wcm.foundation.forms.ValidationInfo;
import com.google.common.collect.ImmutableSet;

@Component(
        service = {FormPostService.class}
)
@Designate(
        ocd = FormPostServiceImpl.Config.class
)
public class FormPostServiceImpl implements FormPostService {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 6000;
    private static final String ATTR_RESOURCE = FormsHandlingServletHelper.class.getName() + "/resource";
    private static final Logger LOG = LoggerFactory.getLogger(FormPostServiceImpl.class);
    private static final Set<String> INTERNAL_PARAMETER = ImmutableSet.of(
            ":formstart",
            "_charset_",
            ":redirect",
            ":cq_csrf_token"
    );

    private static final String PN_FORM_ENDPOINT_URL = "formEndPointUrl";
    private static final String CHARSET = "UTF-8";

    private int connectionTimeout;
    private int socketTimeout;

    @Reference
    private HttpClientBuilderFactory clientBuilderFactory;

    @Override
    public boolean sendFormData(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException {
        try {
            JSONObject jsonRequest = requestParamsToJSON(request);
            return processFormApi(request, response, jsonRequest);
        } catch (JSONException e) {
            LOG.error(e.getMessage());
        }
        return false;
    }

    private boolean processFormApi(SlingHttpServletRequest request, SlingHttpServletResponse response, JSONObject jsonRequest)
            throws ServletException {
        boolean processFormApiSuccess = false;
        Resource formContainerResource = request.getResource();
        ValueMap valueMap = formContainerResource.adaptTo(ValueMap.class);
        if (valueMap != null) {
            String formEndpoint = valueMap.get(PN_FORM_ENDPOINT_URL, String.class);
            if (StringUtils.isNotEmpty(formEndpoint)) {
                processFormApiSuccess = callFormApi(formEndpoint, jsonRequest);
            }
            sendRedirect(valueMap, request, response, processFormApiSuccess);
        }
        return processFormApiSuccess;
    }

    private void sendRedirect(ValueMap valueMap, SlingHttpServletRequest request, SlingHttpServletResponse response, boolean processFormApiSuccess)
            throws ServletException {
        String redirect = valueMap.get("redirect", String.class);
        FormsHandlingRequest formRequest = new FormsHandlingRequest(request);
        try {
            if (!StringUtils.isEmpty(redirect) && processFormApiSuccess) {
                if (!redirect.contains(".")) {
                    redirect = redirect + ".html";
                }
                response.sendRedirect(request.getResourceResolver().map(request, redirect));
            } else {
                if (!processFormApiSuccess) {
                    ValidationInfo validationInfo = ValidationInfo.createValidationInfo(request);
                    validationInfo.addErrorMessage(null, "Ups!");
                }
                final Resource formResource = (Resource) request.getAttribute(ATTR_RESOURCE);
                request.removeAttribute(ATTR_RESOURCE);
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

    private boolean callFormApi(String formEndpointUrl, JSONObject jsonRequest) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient client = clientBuilderFactory.newBuilder().setDefaultRequestConfig(config).build();
        HttpPost post = new HttpPost(formEndpointUrl);
        post.setEntity(new StringEntity(jsonRequest.toString(), ContentType.create(JSONResponse.RESPONSE_CONTENT_TYPE,
                CHARSET)));
        CloseableHttpResponse response = null;
        try {
            response = client.execute(post);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        StatusLine responseStatusLine = response != null ? response.getStatusLine() : null;
        return responseStatusLine != null && responseStatusLine.getStatusCode() == HttpStatus.SC_OK;
    }

    private JSONObject requestParamsToJSON(SlingHttpServletRequest req) throws JSONException {
        org.json.JSONObject jsonObj = new org.json.JSONObject();
        Map<String, String[]> params = req.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (!INTERNAL_PARAMETER.contains(entry.getKey())) {
                String[] v = entry.getValue();
                Object o = (v.length == 1) ? v[0] : v;
                jsonObj.put(entry.getKey(), o);
            }
        }
        return jsonObj;
    }

    @Activate
    protected void activate(Config config) {
        connectionTimeout = config.connectionTimeout();
        if (connectionTimeout < 0) {
            throw new IllegalArgumentException("Connection timeout value cannot be less than 0");
        }
        socketTimeout = config.socketTimeout();
        if (socketTimeout < 0) {
            throw new IllegalArgumentException("Socket timeout value cannot be less than 0");
        }
    }

    @ObjectClassDefinition(
            name = "Core Components Form API Client",
            description = "A HTTP Client wrapper for Form API requests"
    )
    public @interface Config {

        @AttributeDefinition(
                name = "Connection timeout",
                description = "Timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an " +
                        "infinite timeout. Default is 6000ms",
                defaultValue = {"" + DEFAULT_CONNECTION_TIMEOUT}
        )
        int connectionTimeout() default DEFAULT_CONNECTION_TIMEOUT;

        @AttributeDefinition(
                name = "Socket timeout",
                description = "Timeout in milliseconds for waiting for data or a maximum period of inactivity between two consecutive " +
                        "data packets. Default is 6000ms",
                defaultValue = {"" + DEFAULT_SOCKET_TIMEOUT}
        )
        int socketTimeout() default DEFAULT_SOCKET_TIMEOUT;
    }
}
