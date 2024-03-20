/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2024 Adobe
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
import com.adobe.cq.wcm.core.components.testing.MockNextGenDynamicMediaConfig;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class NGDMEnableRenderConditionTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private NGDMEnableRenderCondition ngdmEnableRenderCondition;
    private static final String REPOSITORY_ID = "delivery-pxxxx-exxxx-cmstg.adobeaemcloud.com";

    @Test
    void testRenderConditionDisabledWithNoConfig() throws Exception {
        ngdmEnableRenderCondition = new NGDMEnableRenderCondition();
        context.registerInjectActivateService(ngdmEnableRenderCondition);
        ngdmEnableRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertFalse(renderCondition.check());
    }

    @Test
    void testRenderConditionDisabledWithNotEnabledConfig() throws Exception {
        ngdmEnableRenderCondition = new NGDMEnableRenderCondition();
        context.registerInjectActivateService(ngdmEnableRenderCondition);
        MockNextGenDynamicMediaConfig nextGenDynamicMediaConfig = new MockNextGenDynamicMediaConfig();
        nextGenDynamicMediaConfig.setEnabled(false);
        nextGenDynamicMediaConfig.setRepositoryId(REPOSITORY_ID);
        context.registerInjectActivateService(nextGenDynamicMediaConfig);
        ngdmEnableRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertFalse(renderCondition.check());
    }

    @Test
    void testRenderConditionEnabled() throws Exception {
        ngdmEnableRenderCondition = new NGDMEnableRenderCondition();
        context.registerInjectActivateService(ngdmEnableRenderCondition);
        MockNextGenDynamicMediaConfig nextGenDynamicMediaConfig = new MockNextGenDynamicMediaConfig();
        nextGenDynamicMediaConfig.setEnabled(true);
        nextGenDynamicMediaConfig.setRepositoryId(REPOSITORY_ID);
        context.registerInjectActivateService(nextGenDynamicMediaConfig);
        ngdmEnableRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertTrue(renderCondition.check());
    }
}
