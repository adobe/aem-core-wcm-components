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
package com.adobe.cq.wcm.core.components.internal.servlets;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractImageDelegatingModel;
import com.adobe.granite.ui.components.ExpressionCustomizer;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class ImageDelegateRenderConditionTest {

    private static final String TEST_BASE = "/image-delegate-render-condition";
    private static final String APPS_ROOT = "/apps/core/wcm/components";
    private static final String CONF_ROOT = "/conf";
    private static final String SUFFIX = "/conf/coretest/settings/wcm/policies/core/wcm/components/teaser/policy_1505736393478";

    public final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    public void setUp() {
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONTENT_JSON, APPS_ROOT);
        context.load().json(TEST_BASE + CoreComponentTestContext.TEST_CONF_JSON, CONF_ROOT);
    }

    @Test
    public void testDoGet() throws Exception {
        ImageDelegateRenderCondition imageDelegateRenderCondition = new ImageDelegateRenderCondition();
        context.requestPathInfo().setSuffix(SUFFIX);
        imageDelegateRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertNotNull(renderCondition);
        assertTrue(renderCondition.check());
        ExpressionCustomizer expressionCustomizer = (ExpressionCustomizer) context.request().getAttribute(ExpressionCustomizer.class.getName());
        assertNotNull(expressionCustomizer);
        assertTrue(expressionCustomizer.hasVariable(AbstractImageDelegatingModel.IMAGE_DELEGATE));
    }

}
