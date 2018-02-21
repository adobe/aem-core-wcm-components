/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.servlets;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit.AemContext;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlethelpers.MockSlingHttpServletResponse;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

import static com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v1.ContentFragmentImplTest.ADAPTER;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ElementNamesRenderConditionTest {

    private static final String RENDERCONDITIONS_PATH = "/content/renderconditions";
    private static final String CONTENT_FRAGMENTS_PATH = "/content/dam/contentfragments";

    /* names of the datasource instances to test */

    private static final String RC_SINGLE_TEXT = "display-mode-single-text";
    private static final String RC_MULTI = "display-mode-multi";

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext("/contentfragment", "/content");

    private ElementNamesRenderCondition servlet;

    @BeforeClass
    public static void setUp() {
        // load the content fragments
        CONTEXT.load().json("/contentfragment/test-content-dam-contentfragments.json", CONTENT_FRAGMENTS_PATH);
        // load the render conditions
        CONTEXT.load().json("/contentfragment/test-content-renderconditions.json", RENDERCONDITIONS_PATH);
        // load the content fragment models
        CONTEXT.load().json("/contentfragment/test-content-conf.json", "/conf/global/settings/dam/cfm/models");
        // register an adapter that adapts resources to (mocks of) content fragments
        CONTEXT.registerAdapter(Resource.class, com.adobe.cq.dam.cfm.ContentFragment.class, ADAPTER);
        // mock services
        CONTEXT.registerService(FragmentRenderService.class, mock(FragmentRenderService.class));
        CONTEXT.registerService(ContentTypeConverter.class, mock(ContentTypeConverter.class));
    }

    @Before
    public void before() throws Exception {
        // mock the expression resolver
        ExpressionResolver expressionResolver = mock(ExpressionResolver.class);
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<PageContext>anyObject())).then(returnsFirstArg());
        when(expressionResolver.resolve(anyString(), anyObject(), anyObject(),
                org.mockito.Matchers.<SlingHttpServletRequest>anyObject())).then(returnsFirstArg());

        // create the servlet to test
        servlet = new ElementNamesRenderCondition();
        Whitebox.setInternalState(servlet, "expressionResolver", expressionResolver);
    }

    @Test
    public void testSingleTextDisplayMode()
            throws ServletException, IOException {
        RenderCondition renderCondition = getRenderCondition(RC_SINGLE_TEXT);
        assertTrue("Invalid value of render condition", renderCondition.check());
    }

    @Test
    public void testMultipleElementsDisplayMode()
            throws ServletException, IOException {
        RenderCondition renderCondition = getRenderCondition(RC_MULTI);
        assertFalse("Invalid value of render condition", renderCondition.check());
    }

    /**
     * Calls the servlet with the specified render conditino resource and returns the resulting render condition.
     */
    private RenderCondition getRenderCondition(String name)
            throws ServletException, IOException {
        // get render condition resource
        ResourceResolver resolver = CONTEXT.resourceResolver();
        Resource renderCondition = resolver.getResource(RENDERCONDITIONS_PATH + "/" + name);

        // mock the request
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, CONTEXT.bundleContext());
        request.setResource(renderCondition);

        // call the servlet
        servlet.doGet(request, new MockSlingHttpServletResponse());

        // return the resulting render condition
        return (RenderCondition) request.getAttribute(RenderCondition.class.getName());
    }

}
