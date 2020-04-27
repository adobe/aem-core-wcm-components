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
package com.adobe.cq.wcm.core.components.internal.jackson;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.wcm.core.components.models.DataLayer;
import com.day.cq.dam.api.Asset;
import com.day.cq.tagging.TagConstants;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DataLayerSerializer extends StdSerializer<DataLayer> {

    static final String JSON_KEY_TYPE = "type";
    static final String JSON_KEY_TITLE = "title";
    static final String JSON_KEY_DESCRIPTION = "description";
    static final String JSON_KEY_LAST_MODIFIED_DATE = "lastModifiedDate";
    static final String JSON_KEY_TEMPLATE_PATH = "templatePath";
    static final String JSON_KEY_TEXT = "text";
    static final String JSON_KEY_TAGS = "tags";
    static final String JSON_KEY_ASSET = "asset";
    static final String JSON_KEY_LINK_URL = "linkUrl";
    static final String JSON_KEY_URL = "url";
    static final String JSON_KEY_LANGUAGE = "language";
    static final String JSON_KEY_SHOWN_ITEMS = "shownItems";

    static final String JSON_KEY_ASSET_ID = "id";
    static final String JSON_KEY_ASSET_URL = "url";
    static final String JSON_KEY_ASSET_FORMAT = "format";
    static final String JSON_KEY_ASSET_TAGS = "tags";
    static final String JSON_KEY_ASSET_LAST_MODIFIED_DATE = "lastModifiedDate";

    public DataLayerSerializer() {
        this(null);
    }

    public DataLayerSerializer(Class<DataLayer> t) {
        super(t);
    }

    @Override
    public void serialize(DataLayer dataLayer, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeStartObject();
        if (dataLayer.isEnabled()) {
            jsonGenerator.setCodec(new ObjectMapper());
            jsonGenerator.writeObjectField(dataLayer.getId(), getDataLayerProperties(dataLayer));
        }
        jsonGenerator.writeEndObject();
    }

    private Map<String, Object> getDataLayerProperties(DataLayer dataLayer) {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put(JSON_KEY_TYPE, dataLayer.getType());
        properties.put(JSON_KEY_TITLE, dataLayer.getTitle());
        properties.put(JSON_KEY_DESCRIPTION, dataLayer.getDescription());
        properties.put(JSON_KEY_LAST_MODIFIED_DATE, dataLayer.getLastModifiedDate());
        properties.put(JSON_KEY_TEMPLATE_PATH, dataLayer.getTemplatePath());
        properties.put(JSON_KEY_TEXT, dataLayer.getText());
        properties.put(JSON_KEY_TAGS, dataLayer.getTags());
        properties.put(JSON_KEY_ASSET, getAssetMetadata(dataLayer.getAssetResource()));
        properties.put(JSON_KEY_LINK_URL, dataLayer.getLinkUrl());
        properties.put(JSON_KEY_URL, dataLayer.getUrl());
        properties.put(JSON_KEY_LANGUAGE, dataLayer.getLanguage());
        properties.put(JSON_KEY_SHOWN_ITEMS, dataLayer.getShownItems());

        // Remove nulls
        while(properties.values().remove(null));

        return properties;
    }

    /**
     * Helper method for getting asset metadata where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Image}
     *
     * @return the metadata Map
     */
    static private Map<String, Object> getAssetMetadata(Resource assetResource) {
        Map<String, Object> assetMetadata = null;
        if (assetResource != null) {
            Asset asset = assetResource.adaptTo(Asset.class);
            if (asset != null) {
                assetMetadata = new LinkedHashMap<>();
                assetMetadata.put(JSON_KEY_ASSET_ID, asset.getID());
                assetMetadata.put(JSON_KEY_ASSET_URL, asset.getPath());
                assetMetadata.put(JSON_KEY_ASSET_FORMAT, asset.getMimeType());
                assetMetadata.put(JSON_KEY_ASSET_TAGS, getAssetTags(asset));
                assetMetadata.put(JSON_KEY_ASSET_LAST_MODIFIED_DATE, getAssetLastModifiedDate(asset, assetResource));

                // Remove nulls
                while(assetMetadata.values().remove(null));
            }
        }
        return assetMetadata;
    }

    /**
     * Helper method for getting asset tags where it applies (ex
     * {@link com.adobe.cq.wcm.core.components.models.Image}
     *
     * @return the asset tags Map
     */
    static private Map<String, Object> getAssetTags(Asset asset) {
        Map<String, Object> assetTags = new LinkedHashMap<>();
        String tagsValue = asset.getMetadataValueFromJcr(TagConstants.PN_TAGS);
        if (StringUtils.isNotEmpty(tagsValue)) {
            String[] tags = tagsValue.split(",");
            for (String tag : tags) {
                assetTags.put(tag, 1);
            }
        }

        // Remove nulls
        while(assetTags.values().remove(null));

        return assetTags;
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
                created = resourceMap.get(JcrConstants.JCR_CREATED, Calendar.class);
            }

            assetLastModification = (null != created) ? created.getTimeInMillis() : 0;
        }

        return new Date(assetLastModification).toInstant().toString();
    }
}
