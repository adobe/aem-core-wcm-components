package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.adobe.cq.wcm.core.components.internal.DataLayerFactory;
import com.adobe.cq.wcm.core.components.models.DataLayerProvider;

public abstract class AbstractDataLayerProvider implements DataLayerProvider {

    @Override
    public boolean isDataLayerEnabled() {
        return true;
    }

    @Override
    public String getDataLayerJson() {
        if (isDataLayerEnabled()) {
            return DataLayerFactory.build(this);
        }
        throw new UnsupportedOperationException();
    }
}
