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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.wcm.core.components.models.DataLayerProperties;
import com.day.cq.dam.api.Asset;

/**
 * Builder class for generating the final dataLayer representation of a model
 *
 */
public class DataLayerPropertiesFactory {

    /**
     * Builds a dataLayer Map representation of the model passed as argument
     *
     * @return the dataLayer Map
     */
    static public Map<String, Map<String, Object>> build(DataLayerProperties provider) {
        MapWrapper data = new MapWrapper();
        Map<String, Map<String, Object>> result = new HashMap<>();

        if (provider != null) {
            String id = (String)invoke(provider, "getDataLayerId");
            if (id != null) {
                data.put("type", invoke(provider, "getDataLayerType"));
                data.put("title", invoke(provider, "getDataLayerTitle"));
                data.put("templatePath", invoke(provider, "getDataLayerTemplatePath"));
                data.put("text", invoke(provider, "getDataLayerText"));
                data.put("tags", invoke(provider, "getDataLayerTags"));
                data.put("asset", getAssetMetadata(provider));
                data.put("linkUrl", invoke(provider, "getDataLayerLinkUrl"));
                data.put("language", invoke(provider, "getDataLayerLanguage"));
                data.put("shownItems", getShownItems(provider));

                result.put(id, data.getMap());
            }
        }

        return result;
    }

    /**
     * Helper method for invoking dataLayer specific methods of the model
     *
     * @return the dataLayer property value
     */
    static private Object invoke(DataLayerProperties provider, String method) {
        try {
            Method providerMethod = provider.getClass().getMethod(method);
            Object result = providerMethod.invoke(provider);
            return result;
        } catch (UnsupportedOperationException | NoSuchMethodException | IllegalAccessException
                | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Helper method for getting asset metadata where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Image}
     *
     * @return the metadata Map
     */
    static private Map<String, Object> getAssetMetadata(DataLayerProperties provider) {
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
                assetMetadata.put("url", asset.getPath());
                assetMetadata.put("format", asset.getMimeType());
                assetMetadata.put("tags", getAssetTags(asset));
                assetMetadata.put("lastModifiedDate", getAssetLastModifiedDate(asset, assetResource));
                assetMetadataObject = assetMetadata.getMap();
            }
        }
        return assetMetadataObject;
    }

    /**
     * Helper method for getting asset tags where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Image}
     *
     * @return the asset tags Map
     */
    static private Map<String, Object> getAssetTags(Asset asset) {
        MapWrapper assetTags = new MapWrapper();
        String tagsValue = asset.getMetadataValueFromJcr("cq:tags");
        if (StringUtils.isNotEmpty(tagsValue)) {
            String[] tags = tagsValue.split(",");
            for (String tag : tags) {
                assetTags.put(tag, 1);
            }
        }
        return assetTags.getMap();
    }

    /**
     * Helper method for getting asset modified date where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Image}
     *
     * @return the asset modified date
     */
    static private String getAssetLastModifiedDate(Asset asset, Resource assetResource) {
        long assetLastModification = asset.getLastModified();
        Calendar created = null;
        if (assetLastModification == 0) {
            ValueMap resourceMap = assetResource.adaptTo(ValueMap.class);
            if (resourceMap != null) {
                created = resourceMap.get("jcr:created", Calendar.class);
            }

            assetLastModification = (null != created) ? created.getTimeInMillis() : 0;
        }

        return new Date(assetLastModification).toInstant().toString();
    }

    /**
     * Helper method for getting a list of IDs where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Accordion}
     *
     * @return the array of expanded items IDs
     */
    static private ArrayList<Object> getShownItems(DataLayerProperties provider) {
        ArrayList<Object> obj = null;
        String[] expandedItems = null;
        try {
            expandedItems = provider.getDataLayerShownItems();
        } catch (UnsupportedOperationException e) {
            // do nothing as we need to continue
        }

        if (expandedItems != null) {
            obj = new ArrayList<>();
            Collections.addAll(obj, expandedItems);
        }
        return obj;
    }

    /**
     * Wrapper class for storing dataLayer properties
     *
     */
    static class MapWrapper {
        private Map<String, Object> map;

        public MapWrapper() {
            map = new HashMap<>();
        }

        public Map<String, Object> getMap() {
            return map;
        }

        void put(String propName, Object propValue) {
            if (propName != null && propValue != null) {
                map.put(propName, propValue);
            }
        }
    }
}
