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
package com.adobe.cq.wcm.core.components.testing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.commerce.api.CommerceException;
import com.adobe.cq.commerce.api.CommerceService;
import com.adobe.cq.commerce.api.CommerceSession;
import com.adobe.cq.commerce.api.PriceInfo;
import com.adobe.cq.commerce.api.Product;
import com.adobe.cq.commerce.common.PriceFilter;
import com.day.cq.commons.ImageResource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockCommerceFactory {

    public static final BigDecimal UNIVERSAL_PRICE = new BigDecimal(10.00);

    public static Product getProduct(Resource resource) {
        Product product = mock(Product.class);
        when(product.getPath()).thenReturn(resource.getPath());
        Resource image = resource.getChild("image");
        if (image != null) {
            ImageResource imageResource = new ImageResource(image);
            when(product.getImage()).thenReturn(imageResource);
        }
        return product;
    }

    @SuppressWarnings("squid:S00112")
    public static CommerceService getCommerceService(Resource resource) {
        CommerceService commerceService = mock(CommerceService.class);
        try {
            when(commerceService.login(any(SlingHttpServletRequest.class), any(SlingHttpServletResponse.class))).then(invocationOnMock -> {
                CommerceSession commerceSession = mock(CommerceSession.class);
                when(commerceSession.getProductPriceInfo(any(Product.class), any(PriceFilter.class))).then(invocation -> {
                    Product product = invocation.getArgument(0);
                    if (product.getPath().equals(resource.getPath())) {
                        return new ArrayList<PriceInfo>() {{
                            add(new PriceInfo(UNIVERSAL_PRICE, new Locale("en", "US")));
                        }};
                    }
                    return null;
                });
                return commerceSession;
            });
        } catch (CommerceException e) {
            throw new RuntimeException("Unable to mock CommerceService.");
        }
        return commerceService;
    }


}
