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

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.servlets.post.JSONResponse;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.form.FormHandler;

@Component(
        service = {FormHandler.class}
)
@Designate(
        ocd = FormHandlerImpl.Config.class
)
public class FormHandlerImpl implements FormHandler {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 6000;
    private static final Logger LOG = LoggerFactory.getLogger(FormHandlerImpl.class);



    private static final String CHARSET = "UTF-8";

    private int connectionTimeout;
    private int socketTimeout;

    @Reference
    private HttpClientBuilderFactory clientBuilderFactory;

    @Override
    public boolean forwardFormData(JSONObject formData, String endPointUrl) {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        CloseableHttpClient client = clientBuilderFactory.newBuilder().setDefaultRequestConfig(config).build();
        HttpPost post = new HttpPost(endPointUrl);
        post.setEntity(new StringEntity(formData.toString(), ContentType.create(JSONResponse.RESPONSE_CONTENT_TYPE,
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
