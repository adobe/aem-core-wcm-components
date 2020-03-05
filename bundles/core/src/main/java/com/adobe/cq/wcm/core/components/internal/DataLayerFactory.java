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
package com.adobe.cq.wcm.core.components.internal;

import com.adobe.cq.wcm.core.components.models.DataLayerProvider;
import com.day.cq.dam.api.Asset;
import org.apache.sling.api.resource.Resource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataLayerFactory {

    static public Map<String, Object> build(DataLayerProvider provider) {
        MapWrapper data = new MapWrapper();
        data.put("id", invoke(provider, "getDataLayerId"));
        data.put("type", invoke(provider, "getDataLayerType"));
        data.put("name", invoke(provider, "getDataLayerName"));
        data.put("title", invoke(provider, "getDataLayerTitle"));
        data.put("template", invoke(provider, "getDataLayerTemplate"));
        data.put("src", invoke(provider, "getDataLayerSrc"));
        data.put("text", invoke(provider, "getDataLayerText"));
        data.put("tags", invoke(provider, "getDataLayerTags"));
        data.put("asset", getAssetMetadata(provider));
        data.put("linkUrl", invoke(provider, "getDataLayerLinkUrl"));
        data.put("language", invoke(provider, "getDataLayerLanguage"));
        data.put("itemsCount", invoke(provider, "getDataLayerItemsCount"));
        data.put("activeItem", invoke(provider, "getDataLayerActiveItem"));
        data.put("expandedItems", getExpandedItems(provider));
        return data.getMap();
    }

    static private Object invoke(DataLayerProvider provider, String method) {
        try {
            Method providerMethod = provider.getClass().getMethod(method);
            return providerMethod.invoke(provider);
        } catch (UnsupportedOperationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    static private Map<String, Object> getAssetMetadata(DataLayerProvider provider) {
        Map<String, Object> assetMetadataObject = null;
        Resource assetResource = null;
        try {
            assetResource = provider.getAssetResource();
        } catch (UnsupportedOperationException e) {
            // do nothing as we need to continue
        }
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset != null) {
                MapWrapper assetMetadata = new MapWrapper();
                assetMetadata.put("id", asset.getID());
                assetMetadata.put("name", asset.getName());
                assetMetadata.put("path", asset.getPath());
                assetMetadata.put("type", asset.getMimeType());
                assetMetadata.put("url", "https://"); // aboslute URL
                assetMetadata.put("tags", getAssetTags(asset));
                assetMetadataObject = assetMetadata.getMap();
            }
        }
        return assetMetadataObject;
    }

    static private Map<String, Object> getAssetTags(Asset asset) {
        MapWrapper assetTags = new MapWrapper();
        String tagsValue = asset.getMetadataValueFromJcr("cq:tags");
        if (tagsValue != null) {
            String[] tags = tagsValue.split(",");
            for (String tag : tags) {
                assetTags.put(tag, 1);
            }
        }
        return assetTags.getMap();
    }

    static private ArrayList<Object> getExpandedItems(DataLayerProvider provider) {
        ArrayList<Object> obj = null;
        String[] expandedItems = null;
        try {
            expandedItems = provider.getDataLayerExpandedItems();
        } catch (UnsupportedOperationException e) {
            // do nothing as we need to continue
        }

        if (expandedItems != null) {
            obj = new ArrayList<>();
            Collections.addAll(obj, expandedItems);
        }
        return obj;
    }

    static class MapWrapper {
        private Map<String, Object> map;

        public MapWrapper() {
            map = new HashMap<>();
        }

        public Map<String, Object> getMap() {
            return map;
        }

        void put(String propName, Object propValue) {
            if ((propValue instanceof Integer && (Integer) propValue >= 0) || propValue != null)  {
                map.put(propName, propValue);
            }
        }
    }
}
