/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.util.Locale;

import org.apache.sling.testing.mock.caconfig.MockContextAwareConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.wcm.core.components.config.PWACaConfig;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class IsSiteProjectRenderConditionServletTest {

    private static final String TEST_BASE = "/is-site-project-render-condition";
    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String CONTENT_ROOT = "/content/page";
    private static final String SLING_CONFIGS_ROOT = "/conf/page/sling:configs";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @Mock
    private ExpressionResolver expressionResolver;
    private IsSiteProjectRenderConditionServlet underTest;

    @BeforeEach
    void setUp() {
        context.registerService(ExpressionResolver.class, expressionResolver);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_APPS_JSON, APPS_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, CONTENT_ROOT);
        context.load().json(TEST_BASE + "/test-sling-configs.json", SLING_CONFIGS_ROOT);
        when(expressionResolver.resolve(anyString(), any(Locale.class), eq(String.class), eq(context.request()))).thenReturn(CONTENT_ROOT +
                "/templated-page");
        MockContextAwareConfig.registerAnnotationClasses(context, PWACaConfig.class);
        underTest = context.registerInjectActivateService(new IsSiteProjectRenderConditionServlet());
    }

    @Test
    void testDoGet() throws Exception {
        context.currentResource(APPS_ROOT + "/issiteproject");
        underTest.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertNotNull(renderCondition);
        assertTrue(renderCondition.check());
    }
}