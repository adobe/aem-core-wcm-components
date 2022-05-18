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

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.spi.AssetDelivery;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class AssetDeliveryEnableRenderConditionTest {

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private AssetDeliveryEnableRenderCondition assetDeliveryServiceEnableRenderCondition;

    @Test
    void testRenderConditionDisabled() throws Exception {
        assetDeliveryServiceEnableRenderCondition = new AssetDeliveryEnableRenderCondition();
        context.registerInjectActivateService(assetDeliveryServiceEnableRenderCondition);
        assetDeliveryServiceEnableRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertFalse(renderCondition.check());
    }

    @Test
    void testRenderConditionEnabled() throws Exception {
        context.registerService(AssetDelivery.class, mock(AssetDelivery.class));
        assetDeliveryServiceEnableRenderCondition = new AssetDeliveryEnableRenderCondition();
        context.registerInjectActivateService(assetDeliveryServiceEnableRenderCondition);
        assetDeliveryServiceEnableRenderCondition.doGet(context.request(), context.response());
        RenderCondition renderCondition = (RenderCondition) context.request().getAttribute(RenderCondition.class.getName());
        assertTrue(renderCondition.check());
    }

}
