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
import javax.servlet.ServletException;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.form.FormPostServiceImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.form.FormsHelperStubber;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FormRpcServletTest {

    private FormRpcServlet underTest;

    private WireMockServer wireMockServer;

    public final AemContext context = CoreComponentTestContext.newAemContext();
    private static final String TEST_BASE = "/form/form-post-service";
    private static final String CONTENT_ROOT = "/content";

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        wireMockServer = new WireMockServer(8090);
        wireMockServer.start();
        setupStub();
        context.registerService(HttpClientBuilderFactory.class, HttpClientBuilder::create);
        context.registerInjectActivateService(new FormPostServiceImpl());
        underTest = context.registerInjectActivateService(new FormRpcServlet());
        FormsHelperStubber.createStub();
    }

    @Test
    void testDoPost() throws ServletException, IOException {
        context.currentResource("/content/container");
        underTest.doPost(context.request(), context.response());
        assertEquals(302 , context.response().getStatus());
    }

    private void setupStub() {
        wireMockServer.stubFor(post(urlEqualTo("/form/endpoint"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)));
    }
}