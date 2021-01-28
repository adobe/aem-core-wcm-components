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

import java.lang.annotation.Annotation;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.form.FormHandler;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith({AemContextExtension.class})
public class FormHandlerImplTest {

    public final AemContext context = CoreComponentTestContext.newAemContext();

    private WireMockServer wireMockServer;
    private int wireMockPort;
    private FormHandler underTest;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockPort = wireMockServer.port();
        setupStub();
        context.registerService(HttpClientBuilderFactory.class, HttpClientBuilder::create);
        underTest = context.registerInjectActivateService(new FormHandlerImpl());
        ((FormHandlerImpl) underTest).activate(new FormHandlerImpl.Config() {
            @Override
            public int connectionTimeout() {
                return 6000;
            }

            @Override
            public int socketTimeout() {
                return 6000;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        });
    }

    private void setupStub() {
        wireMockServer.stubFor(post(urlEqualTo("/form/endpoint"))
        .willReturn(aResponse().withHeader("Content-Type", "application/json")
        .withStatus(200)));
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testSendFormDataWithSuccess() throws JSONException {
        String endPointUrl = "http://localhost:" + wireMockPort + "/form/endpoint";
        JSONObject formData = new JSONObject();
        formData.append("text", "Hello World!");
        assertTrue(underTest.forwardFormData(formData, endPointUrl));
    }

    @Test
    void testSendFormDataWithError() throws JSONException {
        String endPointUrl = "http://localhost:" + wireMockPort + "/form/nonExistingEndpoint";
        JSONObject formData = new JSONObject();
        formData.append("text", "Hello World!");
        assertFalse(underTest.forwardFormData(formData, endPointUrl));
    }
}
