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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;

import java.io.IOException;
import java.util.Locale;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.testing.Utils;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment.AbstractContentFragmentTest.CONTENT_FRAGMENT_ADAPTER;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ElementNamesRenderConditionTest {

    private static final String TEST_BASE = "/contentfragment";
    private static final String RENDERCONDITIONS_PATH = "/content/renderconditions";
    private static final String CONTENT_FRAGMENTS_PATH = "/content/dam/contentfragments";

    /* names of the datasource instances to test */

    private static final String RC_SINGLE_TEXT = "display-mode-single-text";
    private static final String RC_MULTI = "display-mode-multi";

    private final AemContext context = CoreComponentTestContext.newAemContext();
    private ElementNamesRenderCondition servlet;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, "/content");
        // load the content fragments
        context.load().json(TEST_BASE + "/test-content-dam-contentfragments.json", CONTENT_FRAGMENTS_PATH);
        // load the render conditions
        context.load().json(TEST_BASE + "/test-content-renderconditions.json", RENDERCONDITIONS_PATH);
        // load the content fragment models
        context.load().json(TEST_BASE + "/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        // register an adapter that adapts resources to (mocks of) content fragments
        context.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, CONTENT_FRAGMENT_ADAPTER);
        // mock services
        context.registerService(FragmentRenderService.class, mock(FragmentRenderService.class));
        context.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));

        ExpressionResolver expressionResolver = mock(ExpressionResolver.class);
        when(expressionResolver.resolve(any(String.class), any(Locale.class), any(Class.class),
                any(SlingHttpServletRequest.class))).then(returnsFirstArg());

        // create the servlet to test
        servlet = new ElementNamesRenderCondition();
        Utils.setInternalState(servlet, "expressionResolver", expressionResolver);
    }

    @Test
    void testSingleTextDisplayMode() throws ServletException, IOException {
        RenderCondition renderCondition = getRenderCondition(RC_SINGLE_TEXT);
        assertTrue(renderCondition.check(), "Invalid value of render condition");
    }

    @Test
    void testMultipleElementsDisplayMode() throws ServletException, IOException {
        RenderCondition renderCondition = getRenderCondition(RC_MULTI);
        assertFalse(renderCondition.check(), "Invalid value of render condition");
    }

    /**
     * Calls the servlet with the specified render conditino resource and returns the resulting render condition.
     */
    private RenderCondition getRenderCondition(String name)
            throws ServletException, IOException {
        // get render condition resource
        ResourceResolver resolver = context.resourceResolver();
        Resource renderCondition = resolver.getResource(RENDERCONDITIONS_PATH + "/" + name);

        // mock the request
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, context.bundleContext());
        request.setResource(renderCondition);

        // call the servlet
        servlet.doGet(request, new MockSlingHttpServletResponse());

        // return the resulting render condition
        return (RenderCondition) request.getAttribute(RenderCondition.class.getName());
    }

}
