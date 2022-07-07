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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.helper.image.AssetDeliveryHelper;
import com.adobe.cq.wcm.core.components.internal.models.v1.ImageAreaImpl;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.DamConstants;
import com.day.cq.dam.scene7.api.constants.Scene7AssetType;
import com.day.cq.dam.scene7.api.constants.Scene7Constants;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.net.UrlEscapers;

/**
 * V2 Image model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {Image.class, ComponentExporter.class},
    resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ImageImpl implements Image {

    @ValueMapValue(name = "imageModifiers", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String imageModifiers;

    @ValueMapValue(name = "imagePreset", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String imagePreset;

    @ValueMapValue(name = "smartCropRendition", injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected String smartCropRendition;

    /**
     * The resource type.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/image/v2/image";

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);

    /**
     * The width variable to use when building {@link #srcUriTemplate}.
     */
    static final String SRC_URI_TEMPLATE_WIDTH_VAR = "{.width}";

    /**
     * The width variable to use when building {@link #srcUriTemplate} for CDN route.
     */
    static final String SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY = "{width}";

    /**
     * The smartcrop "auto" constant.
     */
    private static final String SMART_CROP_AUTO = "SmartCrop:Auto";

    /**
     * The path of the delegated content policy.
     */
    private static final String CONTENT_POLICY_DELEGATE_PATH = "contentPolicyDelegatePath";

    /**
     * Server path for dynamic media
     */
    private static final String DM_IMAGE_SERVER_PATH = "/is/image/";

    /**
     * Content path for Scene7
     */
    private static final String DM_CONTENT_SERVER_PATH = "/is/content/";

    /**
     * Placeholder for the SRC URI template.
     */
    protected String srcUriTemplate;

    /**
     * Placeholder for the areas.
     */
    protected List<ImageArea> areas;

    /**
     * Placeholder for the number of pixels, in advance of becoming visible, at which point this image should load.
     */
    protected int lazyThreshold;

    /**
     * Placeholder for the SRC URI template.
     */
    protected boolean dmImage = false;

    /**
     * Placeholder for the referenced assed ID.
     */
    protected String uuid;

    /**
     * Construct the model.
     *
     * Note: Using this constructor does not imply constructor injection, it does not initialize the model.
     * The model must be initialized via the {@link org.apache.sling.models.factory.ModelFactory}.
     */
    public ImageImpl() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
    }

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        boolean altValueFromDAM = properties.get(PN_ALT_VALUE_FROM_DAM, currentStyle.get(PN_ALT_VALUE_FROM_DAM, true));
        boolean titleValueFromDAM = properties.get(PN_TITLE_VALUE_FROM_DAM, currentStyle.get(PN_TITLE_VALUE_FROM_DAM, true));
        boolean isDmFeaturesEnabled = currentStyle.get(PN_DESIGN_DYNAMIC_MEDIA_ENABLED, false);
        displayPopupTitle = properties.get(PN_DISPLAY_POPUP_TITLE, currentStyle.get(PN_DISPLAY_POPUP_TITLE, true));
        boolean uuidDisabled = currentStyle.get(PN_UUID_DISABLED, false);
        // if content policy delegate path is provided pass it to the image Uri
        String policyDelegatePath = request.getParameter(CONTENT_POLICY_DELEGATE_PATH);
        String dmImageUrl = null;
        if (StringUtils.isNotEmpty(fileReference)) {
            // the image is coming from DAM
            final Resource assetResource = request.getResourceResolver().getResource(fileReference);
            if (assetResource != null) {
                Asset asset = assetResource.adaptTo(Asset.class);
                if (asset != null) {
                    if (!uuidDisabled) {
                        uuid = asset.getID();
                    } else {
                        uuid = null;
                    }
                    if (!isDecorative && altValueFromDAM) {
                        String damDescription = asset.getMetadataValue(DamConstants.DC_DESCRIPTION);
                        if(StringUtils.isEmpty(damDescription)) {
                            damDescription = asset.getMetadataValue(DamConstants.DC_TITLE);
                        }
                        if (StringUtils.isNotEmpty(damDescription)) {
                            alt = damDescription;
                        }
                    }
                    if (titleValueFromDAM) {
                        title = StringUtils.trimToNull(asset.getMetadataValue(DamConstants.DC_TITLE));
                    }

                    //check "Enable DM features" checkbox
                    //check DM asset - check for "dam:scene7File" metadata value
                    String dmAssetName = asset.getMetadataValue(Scene7Constants.PN_S7_FILE);
                    if(isDmFeaturesEnabled && (!StringUtils.isEmpty(dmAssetName))){
                        dmAssetName = UrlEscapers.urlFragmentEscaper().escape(dmAssetName);
                        //image is DM
                        dmImage = true;
                        useAssetDelivery = false;
                        //check for publish side
                        boolean isWCMDisabled =  (com.day.cq.wcm.api.WCMMode.fromRequest(request) == com.day.cq.wcm.api.WCMMode.DISABLED);
                        //sets to '/is/image/ or '/is/content' based on dam:scene7Type property
                        String dmServerPath;
                        if (asset.getMetadataValue(Scene7Constants.PN_S7_TYPE).equals(Scene7AssetType.ANIMATED_GIF.getValue())) {
                            dmServerPath = DM_CONTENT_SERVER_PATH;
                        } else {
                            dmServerPath = DM_IMAGE_SERVER_PATH;
                        }
                        String dmServerUrl;
                        // for Author
                        if (!isWCMDisabled) {
                            dmServerUrl = dmServerPath;
                        } else {
                            // for Publish
                            dmServerUrl = asset.getMetadataValue(Scene7Constants.PN_S7_DOMAIN) + dmServerPath.substring(1);
                        }
                        dmImageUrl = dmServerUrl + dmAssetName;
                    }
                    useAssetDelivery = useAssetDelivery && StringUtils.isEmpty(policyDelegatePath);

                } else {
                    LOGGER.error("Unable to adapt resource '{}' used by image '{}' to an asset.", fileReference,
                            request.getResource().getPath());
                }
            } else {
                LOGGER.error("Unable to find resource '{}' used by image '{}'.", fileReference, request.getResource().getPath());
            }
        }
        if (hasContent) {
            disableLazyLoading = currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, true);

            if (dmImageUrl == null){
                if (useAssetDelivery) {
                    srcUriTemplate = AssetDeliveryHelper.getSrcUriTemplate(assetDelivery, resource, imageName, extension,
                            jpegQuality, SRC_URI_TEMPLATE_WIDTH_VAR_ASSET_DELIVERY);
                }

                if (StringUtils.isEmpty(srcUriTemplate)) {
                    String staticSelectors = selector;
                    if (smartSizes.length > 0) {
                        // only include the quality selector in the URL, if there are sizes configured
                        staticSelectors += DOT + jpegQuality;
                    }
                    srcUriTemplate = baseResourcePath + DOT + staticSelectors +
                        SRC_URI_TEMPLATE_WIDTH_VAR + DOT + extension +
                        (inTemplate ? templateRelativePath : "") +
                        (lastModifiedDate > 0 ? ("/" + lastModifiedDate + (StringUtils.isNotBlank(imageName) ? ("/" + imageName) : "")) : "") +
                        (inTemplate || lastModifiedDate > 0 ? DOT + extension : "");
                }

                if (StringUtils.isNotBlank(policyDelegatePath)) {
                    srcUriTemplate += "?" + CONTENT_POLICY_DELEGATE_PATH + "=" + policyDelegatePath;
                    src += "?" + CONTENT_POLICY_DELEGATE_PATH + "=" + policyDelegatePath;
                }
            } else {
                srcUriTemplate = dmImageUrl;
                src = dmImageUrl;
                if (StringUtils.isNotBlank(smartCropRendition)) {
                    if(smartCropRendition.equals(SMART_CROP_AUTO)) {
                        srcUriTemplate += SRC_URI_TEMPLATE_WIDTH_VAR;
                    } else {
                        srcUriTemplate += "%3A" + smartCropRendition;
                        src += "%3A" + smartCropRendition;
                    }
                }
                if (smartSizes.length > 0 && StringUtils.isBlank(smartCropRendition)) {
                    String qualityCommand = "?qlt=" + jpegQuality;
                    srcUriTemplate += qualityCommand;
                    src += qualityCommand;
                    String widCommand;
                    if (smartSizes.length == 1) {
                        widCommand = "&wid=" + smartSizes[0];
                        srcUriTemplate += widCommand;
                        src += widCommand;
                    } else {
                        widCommand = "&wid=%7B.width%7D";
                        srcUriTemplate += widCommand;
                    }
                }
                String suffix = "";
                if (lastModifiedDate > 0){
                    String timeStampCommand = (srcUriTemplate.contains("?") ? '&':'?') + "ts=" + lastModifiedDate;
                    srcUriTemplate += timeStampCommand;
                    src += timeStampCommand;
                }
                if (StringUtils.isNotBlank(imagePreset) && StringUtils.isBlank(smartCropRendition)){
                    String imagePresetCommand = (srcUriTemplate.contains("?") ? '&':'?') + "$" + imagePreset + "$";
                    srcUriTemplate += imagePresetCommand;
                    src += imagePresetCommand;
                }
                if (StringUtils.isNotBlank(imageModifiers)){
                    String imageModifiersCommand = (srcUriTemplate.contains("?") ? '&':'?') + imageModifiers;
                    srcUriTemplate += imageModifiersCommand;
                    src += imageModifiersCommand;
                }

                String dprParameter = "";
                // If DM is enabled, use smart imaging for smartcrop renditions
                if (getClass().equals(com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl.class) && isDmFeaturesEnabled && !StringUtils.isBlank(smartCropRendition)) {
                    dprParameter = (srcUriTemplate.contains("?") ? '&':'?') + "dpr=on,{dpr}";
                } else {
                    //add "dpr=off" parameter to image source url
                    dprParameter = (srcUriTemplate.contains("?") ? '&':'?') + "dpr=off";
                }

                srcUriTemplate += dprParameter;
                src += dprParameter;

                if (srcUriTemplate.equals(src)) {
                    srcUriTemplate = null;
                }
            }

            buildAreas();
            buildJson();
        }

        this.lazyThreshold = currentStyle.get(PN_DESIGN_LAZY_THRESHOLD, 0);
    }

    @Override
    public int @NotNull [] getWidths() {
        return Arrays.copyOf(smartSizes, smartSizes.length);
    }

    @Override
    public String getSrcUriTemplate() {
        return srcUriTemplate;
    }

    @Override
    public boolean isLazyEnabled() {
        return !disableLazyLoading;
    }

    public boolean isDmImage() {
        return dmImage;
    }

    public String getSmartCropRendition() {
        return smartCropRendition;
    }

    @Override
    public int getLazyThreshold() {
        return this.lazyThreshold;
    }

    @Override
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<ImageArea> getAreas() {
        if (areas == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(areas);
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    protected void buildAreas() {
        areas = new ArrayList<>();
        String mapProperty = properties.get(Image.PN_MAP, String.class);
        if (StringUtils.isNotEmpty(mapProperty)) {
            // Parse the image map areas as defined at {@code Image.PN_MAP}
            String[] mapAreas = StringUtils.split(mapProperty, "][");
            for (String area : mapAreas) {
                int coordinatesEndIndex = area.indexOf(')');
                if (coordinatesEndIndex < 0) {
                    break;
                }
                String shapeAndCoords = StringUtils.substring(area, 0, coordinatesEndIndex + 1);
                String shape = StringUtils.substringBefore(shapeAndCoords, "(");
                String coordinates = StringUtils.substringBetween(shapeAndCoords, "(", ")");
                String remaining = StringUtils.substring(area, coordinatesEndIndex + 1);
                String[] remainingTokens = StringUtils.split(remaining, "|");
                if (StringUtils.isBlank(shape) || StringUtils.isBlank(coordinates)) {
                    break;
                }
                if (remainingTokens.length > 0) {
                    String href = StringUtils.removeAll(remainingTokens[0], "\"");
                    String target = remainingTokens.length > 1 ? StringUtils.removeAll(remainingTokens[1], "\"") : "";

                    Link link = linkManager.get(href).withLinkTarget(target).build();
                    if (!link.isValid()) {
                        break;
                    }

                    String alt = remainingTokens.length > 2 ? StringUtils.removeAll(remainingTokens[2], "\"") : "";
                    String relativeCoordinates = remainingTokens.length > 3 ? remainingTokens[3] : "";
                    relativeCoordinates = StringUtils.substringBetween(relativeCoordinates, "(", ")");
                    areas.add(newImageArea(shape, coordinates, relativeCoordinates, link, alt));
                }
            }
        }
    }

    protected ImageArea newImageArea(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt ) {
        return new ImageAreaImpl(shape, coordinates, relativeCoordinates, link, alt);
    }


}
