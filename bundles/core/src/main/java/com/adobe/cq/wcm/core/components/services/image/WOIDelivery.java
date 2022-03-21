package com.adobe.cq.wcm.core.components.services.image;

/**
 * Web Optimized Image Delivery (WOID)
 */
public interface WOIDelivery {

    public boolean isWOIDAllowed();

    public String getWOIDBaseUrl();
}
