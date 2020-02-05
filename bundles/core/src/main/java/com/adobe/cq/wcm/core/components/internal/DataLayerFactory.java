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
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.sling.api.resource.Resource;

import javax.json.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DataLayerFactory {

    static public String build(DataLayerProvider provider) {

        JsonObjectBuilderWrapper data = new JsonObjectBuilderWrapper();

        Method[] providerMethods = DataLayerProvider.class.getMethods();

        for (Method providerMethod : providerMethods) {
            //System.out.println("methodName: " + providerMethod);
            if (providerMethod.isAnnotationPresent(JsonIgnore.class) && providerMethod.getName().contains("getDataLayer")) {
                String propertyName = providerMethod.getName().replace("getDataLayer", "");
                propertyName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);

                //System.out.println("Property: " + propertyName);

                if ("asset, expandedItems".contains(propertyName)) {

                    try {
                        if ("assetResource".equals(propertyName)) {
                            data.add( "asset", getAssetMetadata(provider));
                        }

                        if ("expandedItems".equals(propertyName)) {
                            data.add( "expandedItems", getExpandedItems(provider));
                        }

                        //System.out.println("Added x property: " + propertyName);

                    } catch (UnsupportedOperationException e) {
                        //System.out.println("Unsupported op: " + propertyName);
                        //e.printStackTrace();
                    }

                    continue;
                }

                try {
                    Object result = providerMethod.invoke(provider);

                    if (result instanceof Integer) {
                        data.add(propertyName, (Integer) result);
                    } else if (result instanceof String) {
                        data.add(propertyName, (String) result);
                    } else if (result instanceof JsonArray) {
                        data.add(propertyName, (JsonArray) result);
                    } else if (result instanceof JsonObject) {
                        data.add(propertyName, (JsonObject) result);
                    }

                    //System.out.println("Added property :" + propertyName);


                } catch (IllegalAccessException e) {
                    //e.printStackTrace();
                } catch (InvocationTargetException e) {
                    //e.printStackTrace();
                    //System.out.println("InvocationTargetException : " + propertyName);
                }
            }
        }

        return  data.build().toString();
    }

    static private JsonObject getAssetMetadata(DataLayerProvider provider) throws UnsupportedOperationException{
        JsonObject assetMetadataObject = null;
        Resource assetResource = provider.getAssetResource(); //getResource().getResourceResolver().getResource(fileReference);
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset != null) {
                JsonObjectBuilderWrapper assetMetadata = new JsonObjectBuilderWrapper();
                assetMetadata.add("id", asset.getID());
                assetMetadata.add("name", asset.getName());
                assetMetadata.add("path", asset.getPath());
                assetMetadata.add("type", asset.getMimeType());
                assetMetadata.add("url", "https://"); // aboslute URL
                assetMetadata.add("tags", getAssetTags(asset));
                assetMetadataObject = assetMetadata.build();
            }
        }
        return assetMetadataObject;
    }

    static private JsonObject getAssetTags(Asset asset) {
        JsonObjectBuilder assetTags = Json.createObjectBuilder();
        String tagsValue = asset.getMetadataValueFromJcr("cq:tags");
        if (tagsValue != null) {
            String[] tags = tagsValue.split(",");
            for (String tag : tags) {
                assetTags.add(tag, 1);
            }
        }
        return assetTags.build();
    }

    static private JsonArray getExpandedItems(DataLayerProvider provider)  throws UnsupportedOperationException{
        JsonArray obj = null;
        String[] expandedItems = provider.getDataLayerExpandedItems();

        if (expandedItems != null) {
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for (String expandedItem : expandedItems) {
                arrayBuilder.add(expandedItem);
            }
            obj = arrayBuilder.build();
        }
        return obj;
    }

    static class JsonObjectBuilderWrapper {
        private JsonObjectBuilder objectBuilder;

        public JsonObjectBuilderWrapper() {
            objectBuilder = Json.createObjectBuilder();
        }

        void add(String propName, String propValue) {
            if (propValue != null) {
                objectBuilder.add(propName, propValue);
            }
        }

        void add(String propName, JsonObject propValue) {
            if (propValue != null) {
                objectBuilder.add(propName, propValue);
            }
        }

        void add(String propName, JsonArray propValue) {
            if (propValue != null) {
                objectBuilder.add(propName, propValue);
            }
        }

        void add(String propName, int propValue) {
            if (propValue >= 0) {
                objectBuilder.add(propName, propValue);
            }
        }

        void add(String propName, Integer propValue) {
            if (propValue >= 0) {
                objectBuilder.add(propName, propValue);
            }
        }

        JsonObject build() {
            return objectBuilder.build();
        }
    }
}
