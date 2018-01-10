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
package com.adobe.cq.wcm.core.components.internal.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.granite.ui.components.ExpressionCustomizer;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit.AemContext;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ImageDelegateRenderConditionTest {

    @ClassRule
    public static final AemContext CONTEXT = CoreComponentTestContext.createContext("/image-delegate-render-condition",
            "/apps/core/wcm/components");

    private static final String SUFFIX = "/conf/coretest/settings/wcm/policies/core/wcm/components/teaser/policy_1505736393478";

    @BeforeClass
    public static void setUp() {
        CONTEXT.load().json("/image-delegate-render-condition/test-conf.json", "/conf");
    }

    @Test
    public void testDoGet() throws Exception {
        ImageDelegateRenderCondition imageDelegateRenderCondition = new ImageDelegateRenderCondition();
        SlingHttpServletRequest request = prepareRequest();
        imageDelegateRenderCondition.doGet(request, CONTEXT.response());
        RenderCondition renderCondition = (RenderCondition) request.getAttribute(RenderCondition.class.getName());
        assertNotNull(renderCondition);
        assertTrue(renderCondition.check());
        ExpressionCustomizer expressionCustomizer = (ExpressionCustomizer) request.getAttribute(ExpressionCustomizer.class.getName());
        assertNotNull(expressionCustomizer);
        assertTrue(expressionCustomizer.hasVariable(AbstractImageDelegatingModel.IMAGE_DELEGATE));
    }

    public SlingHttpServletRequest prepareRequest() {
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(CONTEXT.resourceResolver(), CONTEXT.bundleContext());
        MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        requestPathInfo.setSuffix(SUFFIX);
        return request;
    }

}
