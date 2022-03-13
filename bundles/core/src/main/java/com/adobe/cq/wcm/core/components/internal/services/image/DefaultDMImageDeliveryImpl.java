package com.adobe.cq.wcm.core.components.internal.services.image;

import com.adobe.cq.wcm.core.components.services.image.DMImageDelivery;
import org.osgi.service.component.annotations.Component;

@Component(
    service = DMImageDelivery.class,
    property = {
        org.osgi.framework.Constants.SERVICE_RANKING + ":Integer=" +Integer.MIN_VALUE
    }
)
public class DefaultDMImageDeliveryImpl implements DMImageDelivery {

    @Override
    public boolean isWebOptimizedImageDeliveryAllowed() {
        // default behaviour will be false in final implementation, this will be calculated in aem not here.
        return true;
    }
}
