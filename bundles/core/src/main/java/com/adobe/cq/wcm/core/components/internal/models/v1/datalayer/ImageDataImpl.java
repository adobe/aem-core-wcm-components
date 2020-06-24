/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v1.datalayer;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.models.datalayer.AssetData;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.day.cq.dam.api.Asset;

public class ImageDataImpl extends ComponentDataImpl implements ImageData {

    public ImageDataImpl(@NotNull AbstractComponentImpl component, @NotNull Resource resource) {
        super(component, resource);
    }

    @Override
    public AssetData getAssetData() {
        Resource assetResource = component.getDataLayerAssetResource();
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset != null) {
                return new AssetDataImpl(asset);
            }
        }
        return null;
    }
}
