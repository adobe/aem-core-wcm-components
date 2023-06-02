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
package com.adobe.cq.wcm.core.components.testing;

import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;

@org.osgi.service.component.annotations.Component(
    service = NextGenDynamicMediaConfig.class)
public class MockNextGenDynamicMediaConfig implements NextGenDynamicMediaConfig {
    static final String DEFAULT_IMAGE_DELIVERY_BASE_PATH = "/adobe/dynamicmedia/deliver/{asset-id}/{seo-name}.{format}";
    private boolean enabled;
    private String repositoryId;
    private String apiKey;
    private String env;
    private String imsOrg;
    private String imsEnv;
    private String imsClient;
    private String assetSelectorsJsUrl;
    private String imageDeliveryBasePath = DEFAULT_IMAGE_DELIVERY_BASE_PATH;
    private String videoDeliveryPath;
    private String assetOriginalBinaryDeliveryPath;
    private String assetMetadataPath;

    @Override
    public boolean enabled() {
        return enabled;
    }

    @Override
    public String getRepositoryId() {
        return repositoryId;
    }

    @Override
    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String getEnv() {
        return env;
    }

    @Override
    public String getImsOrg() {
        return imsOrg;
    }

    @Override
    public String getImsEnv() {
        return imsEnv;
    }

    @Override
    public String getImsClient() {
        return imsClient;
    }

    @Override
    public String getAssetSelectorsJsUrl() {
        return assetSelectorsJsUrl;
    }

    @Override
    public String getImageDeliveryBasePath() {
        return imageDeliveryBasePath;
    }

    @Override
    public String getVideoDeliveryPath() {
        return videoDeliveryPath;
    }

    @Override
    public String getAssetOriginalBinaryDeliveryPath() {
        return assetOriginalBinaryDeliveryPath;
    }

    @Override
    public String getAssetMetadataPath() {
        return assetMetadataPath;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public void setImsOrg(String imsOrg) {
        this.imsOrg = imsOrg;
    }

    public void setImsEnv(String imsEnv) {
        this.imsEnv = imsEnv;
    }

    public void setImsClient(String imsClient) {
        this.imsClient = imsClient;
    }

    public void setAssetSelectorsJsUrl(String assetSelectorsJsUrl) {
        this.assetSelectorsJsUrl = assetSelectorsJsUrl;
    }

    public void setImageDeliveryBasePath(String imageDeliveryBasePath) {
        this.imageDeliveryBasePath = imageDeliveryBasePath;
    }

    public void setVideoDeliveryPath(String videoDeliveryPath) {
        this.videoDeliveryPath = videoDeliveryPath;
    }

    public void setAssetOriginalBinaryDeliveryPath(String assetOriginalBinaryDeliveryPath) {
        this.assetOriginalBinaryDeliveryPath = assetOriginalBinaryDeliveryPath;
    }

    public void setAssetMetadataPath(String assetMetadataPath) {
        this.assetMetadataPath = assetMetadataPath;
    }
}
