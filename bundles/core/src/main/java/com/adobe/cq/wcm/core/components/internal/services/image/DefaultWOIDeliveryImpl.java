package com.adobe.cq.wcm.core.components.internal.services.image;

import com.adobe.cq.wcm.core.components.services.image.WOIDelivery;
import org.osgi.service.component.annotations.Component;

@Component(
    service = WOIDelivery.class,
    property = {
        org.osgi.framework.Constants.SERVICE_RANKING + ":Integer=" +Integer.MIN_VALUE
    }
)
public class DefaultWOIDeliveryImpl implements WOIDelivery {


    @Override
    public boolean isWOIDAllowed() {
        // default behaviour will be false in final implementation, this will be calculated in aem not here.
        return true;
    }

    @Override
    public String getWOIDBaseUrl() {
        return "https://publish-p56138-e410068.adobeaemcloud.com/api/dynamicmedia.deliver";
    }
}
