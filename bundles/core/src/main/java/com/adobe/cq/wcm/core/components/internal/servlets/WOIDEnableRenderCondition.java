package com.adobe.cq.wcm.core.components.internal.servlets;

import com.adobe.cq.wcm.core.components.internal.services.image.DefaultWOIDeliveryImpl;
import com.adobe.cq.wcm.core.components.services.image.WOIDelivery;
import com.adobe.granite.ui.components.rendercondition.RenderCondition;
import com.adobe.granite.ui.components.rendercondition.SimpleRenderCondition;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(
    service = { Servlet.class },
    property = {
        ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + "=core/wcm/components/rendercondition/isWOIDEnabled"
    }
)
public class WOIDEnableRenderCondition extends SlingSafeMethodsServlet {

    @Reference
    protected WOIDelivery WOIDeliveryService;

    protected void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
        throws ServletException, IOException {

        if (WOIDeliveryService == null) {
            WOIDeliveryService = new DefaultWOIDeliveryImpl();
        }

        request.setAttribute(RenderCondition.class.getName(), new SimpleRenderCondition(WOIDeliveryService.isWOIDAllowed()));
    }
}
