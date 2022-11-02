/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import org.apache.http.HttpStatus;
import org.apache.sling.api.request.RequestDispatcherOptions;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.testing.mock.sling.servlet.MockRequestDispatcherFactory;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ExpressionCustomizer;
import com.day.cq.wcm.api.designer.Style;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ImageDelegatePolicyServletTest {

    private static final String TEST_BASE = "/image-delegate-policy-servlet";
    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String CONTENT_ROOT = "/content";
    private static final String SUFFIX = "/content/test/jcr:content/root/responsivegrid/teaser";

    public final AemContext context = CoreComponentTestContext.newAemContext();
    private ImageDelegatePolicyServlet underTest;

    @Mock
    private RequestDispatcher requestDispatcher;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
        underTest = new ImageDelegatePolicyServlet();
    }

    @Test
    void testCqDesignAttribute() throws ServletException, IOException {
        context.requestPathInfo().setSuffix(SUFFIX);
        context.currentResource("/apps/core/wcm/components/teaser/cq:design/content/items/tabs/items/image");
        MockSlingHttpServletRequest request = context.request();
        request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return requestDispatcher;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions requestDispatcherOptions) {
                return requestDispatcher;
            }
        });
        underTest.doGet(request, context.response());
        ExpressionCustomizer expressionCustomizer = (ExpressionCustomizer) request.getAttribute(ExpressionCustomizer.class.getName());
        assertNotNull(expressionCustomizer);
        Style cqDesign = (Style) expressionCustomizer.getVariable("cqDesign");
        assertNotNull(cqDesign);
        verify(requestDispatcher).include(context.request(), context.response());
    }

    @Test
    void testComponentNotFound() throws ServletException, IOException {
        context.requestPathInfo().setSuffix(SUFFIX + "non-existing");
        context.currentResource("/apps/core/wcm/components/teaser/cq:design/content/items/tabs/items/image");
        MockSlingHttpServletRequest request = context.request();
        underTest.doGet(request, context.response());
        assertEquals(HttpStatus.SC_NOT_FOUND, context.response().getStatus());
    }

    @Test
    void testNoRequestDispatcher() throws ServletException, IOException {
        MockSlingHttpServletRequest request = context.request();
        context.currentResource("/apps/core/wcm/components/teaser/cq:design/content/items/tabs/items/image");
        request.setRequestDispatcherFactory(new MockRequestDispatcherFactory() {
            @Override
            public RequestDispatcher getRequestDispatcher(String s, RequestDispatcherOptions requestDispatcherOptions) {
                return null;
            }

            @Override
            public RequestDispatcher getRequestDispatcher(Resource resource, RequestDispatcherOptions requestDispatcherOptions) {
                return null;
            }
        });
        context.requestPathInfo().setSuffix(SUFFIX);
        underTest.doGet(request, context.response());
        assertEquals(HttpStatus.SC_NOT_FOUND, context.response().getStatus());
    }
}