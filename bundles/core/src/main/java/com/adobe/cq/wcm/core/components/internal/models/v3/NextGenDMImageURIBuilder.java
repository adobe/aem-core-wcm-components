/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;

import static com.adobe.cq.wcm.core.components.internal.models.v3.ImageImpl.DEFAULT_NGDM_ASSET_WIDTH;

public class NextGenDMImageURIBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(NextGenDMImageURIBuilder.class);
    private static final String PATH_PLACEHOLDER_ASSET_ID = "{asset-id}";
    private static final String PATH_PLACEHOLDER_SEO_NAME = "{seo-name}";
    private static final String PATH_PLACEHOLDER_FORMAT = "{format}";
    private static final String DEFAULT_NGDM_ASSET_EXTENSION = "jpg";

    private NextGenDynamicMediaConfig config;
    private String fileReference;
    private String smartCropName;
    private String previewToken;
    private String previewTokenExpiry;
    private int width = DEFAULT_NGDM_ASSET_WIDTH;

    private int height;
    private boolean preferWebp = true;

    private String modifiers;

    public NextGenDMImageURIBuilder(NextGenDynamicMediaConfig config, String fileReference) {
        this.config = config;
        this.fileReference = fileReference;
    }

    /**
     * Smart Crop name.
     * @param smartCropName - a string denoting the name of the smartcrop;
     */
    public NextGenDMImageURIBuilder withSmartCrop(String smartCropName) {
        this.smartCropName = smartCropName;
        return this;
    }

    /**
     *  Image width
     * @param width - an integer.
     */
    public NextGenDMImageURIBuilder withWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     *  Image height
     * @param height - an integer.
     */
    public NextGenDMImageURIBuilder withHeight(int height) {
        this.height = height;
        return this;
    }


    /**
     * Set to use webp image format.
     * @param preferWebp - should set preferwebp param.
     */
    public NextGenDMImageURIBuilder withPreferWebp(boolean preferWebp) {
        this.preferWebp = preferWebp;
        return this;
    }

    /**
     * Set extra image modifiers.
     * @param modifiersStr
     */
    public NextGenDMImageURIBuilder withImageModifiers(String modifiersStr) {
        this.modifiers = modifiersStr;
        return this;
    }

    /**
     * Set preview token.
     * @param previewToken
     */
    public NextGenDMImageURIBuilder withPreviewToken(String previewToken) {
        this.previewToken = previewToken;
        return this;
    }

    /**
     * Set preview expiry.
     * @param previewTokenExpiry
     */
    public NextGenDMImageURIBuilder withPreviewTokenExpiry(String previewTokenExpiry) {
        this.previewTokenExpiry = previewTokenExpiry;
        return this;
    }

    /**
     * Use this to create a NextGen Dynamic Media Image URI.
     * @return a uri.
     */
    public String build() {
        if(StringUtils.isNotEmpty(this.fileReference) && this.config != null) {
            Scanner scanner = new Scanner(this.fileReference);
            scanner.useDelimiter("/");
            String assetId = scanner.next();
            scanner = new Scanner(scanner.next());
            scanner.useDelimiter("\\.");
            String assetName = scanner.hasNext() ? scanner.next() : assetId;
            String assetExtension = scanner.hasNext() ? scanner.next() : DEFAULT_NGDM_ASSET_EXTENSION;
            String imageDeliveryBasePath = this.config.getImageDeliveryBasePath();
            String imageDeliveryPath = imageDeliveryBasePath.replace(PATH_PLACEHOLDER_ASSET_ID, assetId);
            imageDeliveryPath = imageDeliveryPath.replace(PATH_PLACEHOLDER_SEO_NAME, assetName);
            imageDeliveryPath = imageDeliveryPath.replace(PATH_PLACEHOLDER_FORMAT, assetExtension);
            String repositoryId = this.config.getRepositoryId();
            StringBuilder uriBuilder = new StringBuilder("https://" + repositoryId + imageDeliveryPath);
            Map<String, String> params = new LinkedHashMap<>();
            if(this.width > 0) {
                params.put("width", Integer.toString(this.width));
            }
            if(this.height > 0) {
                params.put("height", Integer.toString(this.height));
            }
            if(this.preferWebp) {
                params.put("preferwebp", "true");
            }
            if (StringUtils.isNotEmpty(this.smartCropName)) {
                params.put("smartcrop", this.smartCropName);
            }
            if (StringUtils.isNotEmpty(this.previewToken)) {
                params.put("previewtoken", this.previewToken);
            }
            if (StringUtils.isNotEmpty(this.previewTokenExpiry)) {
                params.put("previewtokenexpiry", this.previewTokenExpiry);
            }
            if(params.size() > 0) {
                uriBuilder.append("?");
                for(Map.Entry<String, String> entry: params.entrySet()) {
                    uriBuilder.append(entry.getKey() + "=" + entry.getValue());
                    uriBuilder.append("&");
                }
                uriBuilder.deleteCharAt(uriBuilder.length() - 1);
            }
            if(StringUtils.isNotEmpty(this.modifiers)) {
                if (this.modifiers.startsWith("&")) {
                    uriBuilder.append(this.modifiers);
                } else {
                    uriBuilder.append("&" + this.modifiers);
                }
            }
            return uriBuilder.toString();
        }
        LOGGER.info("Invalid fileReference or NGDMConfig. fileReference = {}", this.fileReference);
        return null;
    }
}
