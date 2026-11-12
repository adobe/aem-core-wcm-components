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

import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;
import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

@Component(
    service = { Servlet.class },
    property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=core/wcm/components/rendercondition/isNGDMEnabled"
    }
)
public class NGDMEnableRenderCondition extends SlingSafeMethodsServlet {

    @Reference(cardinality= ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
    private NextGenDynamicMediaConfig nextGenDynamicMediaConfig;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
        throws ServletException, IOException {

        boolean isNGDMEnabled = false;
        if (nextGenDynamicMediaConfig != null && nextGenDynamicMediaConfig.enabled() &&
            StringUtils.isNotBlank(nextGenDynamicMediaConfig.getRepositoryId())) {
            isNGDMEnabled = true;
        }

        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(isNGDMEnabled));
    }
}
