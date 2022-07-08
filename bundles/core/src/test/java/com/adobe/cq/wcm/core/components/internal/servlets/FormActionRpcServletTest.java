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
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.form.FormHandlerImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.form.FormsHelperStubber;
import com.day.cq.wcm.foundation.forms.ValidationInfo;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FormActionRpcServletTest {

    private FormActionRpcServlet underTest;

    private WireMockServer wireMockServer;
    private int wireMockPort;

    public final AemContext context = CoreComponentTestContext.newAemContext();
    private static final String TEST_BASE = "/form/form-rpc-servlet";
    private static final String CONTENT_ROOT = "/content";

    @Mock
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        wireMockServer = new WireMockServer(wireMockConfig().dynamicPort());
        wireMockServer.start();
        wireMockPort = wireMockServer.port();
        setupStub();
        context.registerService(HttpClientBuilderFactory.class, HttpClientBuilder::create);
        context.registerInjectActivateService(new FormHandlerImpl());
        context.request().setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String path, RequestDispatcherOptions options) {
                return requestDispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions options) {
                return requestDispatcher;
            }
        });
        underTest = context.registerInjectActivateService(new FormActionRpcServlet());
        FormsHelperStubber.createStub();
    }

    @Test
    void testDoPostWithSuccess() throws ServletException {
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("text", "hello"));
        request.setAttribute("cq.form.id", "new_form");
        Resource resource = context.currentResource("/content/container");
        ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        modifiableValueMap.put("externalServiceEndPointUrl", "http://localhost:" + wireMockPort + "/form/endpoint");
        underTest.doPost(request, context.response());
        assertEquals(302 , context.response().getStatus());
    }

    @Test
    void testDoPostWithMappedRedirect() throws ServletException {
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("text", "hello"));
        request.setAttribute("cq.form.id", "new_form");
        Resource resource = context.currentResource("/content/containerWithMappedRedirect");
        ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        modifiableValueMap.put("externalServiceEndPointUrl", "http://localhost:" + wireMockPort + "/form/endpoint");
        underTest.doPost(request, context.response());
        assertEquals(302 , context.response().getStatus());
    }

    @Test
    void testDoPostWithError() throws ServletException {
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("text", "hello"));
        request.setAttribute("cq.form.id", "new_form");
        Resource resource = context.currentResource("/content/container");
        ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        modifiableValueMap.put("formEndPointUrl", "http://localhost:" + wireMockPort + "/form/nonExistingEndpoint");
        underTest.doPost(context.request(), context.response());
        assertEquals(200 , context.response().getStatus());
        ValidationInfo validationInfo = ValidationInfo.getValidationInfo(request);
        assertNotNull(validationInfo);
    }

    @Test
    void testDoPostWithExtraParameters() throws ServletException {
        MockSlingHttpServletRequest request = context.request();
        request.setParameterMap(ImmutableMap.of("text", "hello", "extra", "foobar"));
        request.setAttribute("cq.form.id", "new_form");
        Resource resource = context.currentResource("/content/container");
        ModifiableValueMap modifiableValueMap = resource.adaptTo(ModifiableValueMap.class);
        modifiableValueMap.put("externalServiceEndPointUrl", "http://localhost:" + wireMockPort + "/form/endpoint");
        underTest.doPost(context.request(), context.response());
        assertEquals(302 , context.response().getStatus());
    }

    private void setupStub() {
        wireMockServer.stubFor(post(urlEqualTo("/form/endpoint"))
                .withRequestBody(equalTo("{}"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)));
        wireMockServer.stubFor(post(urlEqualTo("/form/endpoint"))
                .withRequestBody(equalTo("{\"text\":\"hello\"}"))
                .willReturn(aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200)));
    }
}
