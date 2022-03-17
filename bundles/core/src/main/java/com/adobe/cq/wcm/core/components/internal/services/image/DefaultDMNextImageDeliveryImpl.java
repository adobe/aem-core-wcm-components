package com.adobe.cq.wcm.core.components.internal.services.image;

import com.adobe.cq.wcm.core.components.services.image.DMNextImageDelivery;
import org.osgi.service.component.annotations.Component;

@Component(
    service = DMNextImageDelivery.class,
    property = {
        org.osgi.framework.Constants.SERVICE_RANKING + ":Integer=" +Integer.MIN_VALUE
    }
)
public class DefaultDMNextImageDeliveryImpl implements DMNextImageDelivery {


    @Override
    public boolean isWebOptimizedImageDeliveryAllowed() {
        // default behaviour will be false in final implementation, this will be calculated in aem not here.
        return true;
    }

    @Override
    public String getDMNextServiceUrl() {
        return "https://publish-p56138-e410068.adobeaemcloud.com/api/dynamicmedia.deliver";
    }
}
