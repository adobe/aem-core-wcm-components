/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.ui.wcm.commons.config.NextGenDynamicMediaConfig;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.helper.image.AssetDeliveryHelper;
import com.adobe.cq.wcm.core.components.internal.models.v2.ImageAreaImpl;
import com.adobe.cq.wcm.core.components.internal.servlets.EnhancedRendition;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.commons.DownloadResource;
import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.adobe.cq.wcm.core.components.internal.Utils.getWrappedImageResourceWithInheritance;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_IMAGE_LINK_HIDDEN;


@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    private static final String URI_WIDTH_PLACEHOLDER_ENCODED = "%7B.width%7D";
    private static final String URI_WIDTH_PLACEHOLDER = "{.width}";
    private static final String EMPTY_PIXEL = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";
    static final int DEFAULT_NGDM_ASSET_WIDTH = 640;

    @OSGiService
    @Optional
    private NextGenDynamicMediaConfig nextGenDynamicMediaConfig;

    private boolean imageLinkHidden = false;

    private String srcSet = StringUtils.EMPTY;
    private Map<String, String> srcSetWithMimeType = Collections.EMPTY_MAP;
    private String sizes;

    private Dimension dimension;

    private boolean ngdmImage = false;

    @PostConstruct
    protected void initModel() {
        if (isNgdmSupportAvailable()) {
            initNextGenerationDynamicMedia();
        }
        super.initModel();
        if (hasContent) {
            disableLazyLoading = currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, false);
            imageLinkHidden = properties.get(PN_IMAGE_LINK_HIDDEN, imageLinkHidden);
            sizes = String.join((", "), currentStyle.get(PN_DESIGN_SIZES, new String[0]));
            disableLazyLoading = properties.get(PN_DESIGN_LAZY_LOADING_ENABLED, currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, false));
        }
    }

    @Override
    @Nullable
    public Link getImageLink() {
        return (imageLinkHidden || (link != null && !link.isValid())) ? null : link;
    }

    @Override
    @JsonIgnore
    @Deprecated
    public String getLink() {
        return super.getLink();
    }

    @Override
    protected ImageArea newImageArea(String shape, String coordinates, String relativeCoordinates, @NotNull Link link, String alt) {
        return new ImageAreaImpl(shape, coordinates, relativeCoordinates, link, alt);
    }


    @Override
    public String getSrcset() {

        if (!StringUtils.isEmpty(srcSet)) {
            return srcSet;
        }

        if (useAssetDelivery) {
            srcSet = AssetDeliveryHelper.getSrcSet(assetDelivery, resource, imageName, extension, smartSizes,
                jpegQuality);
            if (!StringUtils.isEmpty(srcSet)) {
                return srcSet;
            }
        }

        int[] widthsArray = getWidths();
        String srcUritemplate = getSrcUriTemplate();
        String[] srcsetArray = new String[widthsArray.length];
        if (widthsArray.length > 0 && srcUritemplate != null) {
            srcUritemplate = StringUtils.replace(srcUriTemplate, URI_WIDTH_PLACEHOLDER_ENCODED, URI_WIDTH_PLACEHOLDER);
            if (srcUritemplate.contains(URI_WIDTH_PLACEHOLDER)) {
                // in case of dm image and auto smartcrop the srcset needs to generated client side
                if (dmImage && StringUtils.equals(smartCropRendition, SMART_CROP_AUTO)) {
                    srcSet = EMPTY_PIXEL;
                } else {
                    for (int i = 0; i < widthsArray.length; i++) {
                        if (srcUritemplate.contains("=" + URI_WIDTH_PLACEHOLDER)) {
                            srcsetArray[i] =
                                srcUritemplate.replace("{.width}", String.format("%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                        } else {
                            srcsetArray[i] =
                                srcUritemplate.replace("{.width}", String.format(".%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                        }
                    }
                    srcSet = StringUtils.join(srcsetArray, ',');
                }
                return srcSet;
            }
        }
        return null;
    }

    @Override
    @Nullable
    public String getSizes() {
        return sizes;
    }

    @Nullable
    @Override
    @JsonIgnore
    public String getHeight() {
        int height = getOriginalDimension().height;
        if (height > 0) {
            return String.valueOf(height);
        }
        return null;
    }

    @Nullable
    @Override
    @JsonIgnore
    public String getWidth() {
        int width = getOriginalDimension().width;
        if (width > 0) {
            return String.valueOf(width);
        }
        return null;
    }

    @Override
    @JsonIgnore
    public String getSrcUriTemplate() {
        if (ngdmImage) {
            return prepareNgdmSrcUriTemplate();
        }
        return super.getSrcUriTemplate();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public int getLazyThreshold() {
        return 0;
    }

    @Override
    @JsonIgnore
    public int @NotNull [] getWidths() {
        return super.getWidths();
    }

    @Override
    @JsonIgnore
    public boolean isDmImage() {
        return super.isDmImage();
    }

    @Override
    @JsonIgnore
    @Deprecated
    public List<ImageArea> getAreas() {
        return super.getAreas();
    }

    @Override
    protected void initResource() {
        resource = getWrappedImageResourceWithInheritance(resource, linkManager, currentStyle, currentPage);
    }

    @Override
    public boolean isLazyEnabled() {
        return !disableLazyLoading;
    }


    private Dimension getOriginalDimension() {
        if (this.dimension == null) {
            this.dimension = getOriginalDimensionInternal();
        }
        return this.dimension;
    }

    private Dimension getOriginalDimensionInternal() {
        ValueMap inheritedResourceProperties = resource.getValueMap();
        String inheritedFileReference = inheritedResourceProperties.get(DownloadResource.PN_REFERENCE, String.class);
        Asset asset;
        String resizeWidth = currentStyle.get(PN_DESIGN_RESIZE_WIDTH, String.class);
        if (StringUtils.isNotEmpty(inheritedFileReference)) {
            final Resource assetResource = request.getResourceResolver().getResource(inheritedFileReference);
            if (assetResource != null) {
                asset = assetResource.adaptTo(Asset.class);
                EnhancedRendition original = null;
                if (asset != null) {
                    original = new EnhancedRendition(asset.getOriginal());
                }
                if (original != null) {
                    Dimension dimension = original.getDimension();
                    if (dimension != null) {
                        if (resizeWidth != null && Integer.parseInt(resizeWidth) > 0 && Integer.parseInt(resizeWidth) < dimension.getWidth()) {
                            int calculatedHeight = (int) Math.round(Integer.parseInt(resizeWidth) * (dimension.getHeight() / (float) dimension.getWidth()));
                            return new Dimension(Integer.parseInt(resizeWidth), calculatedHeight);
                        }
                        return dimension;
                    }
                }
            }
        }
        return new Dimension(0, 0);
    }

    private boolean isNgdmSupportAvailable() {
        return nextGenDynamicMediaConfig != null && nextGenDynamicMediaConfig.enabled() &&
            StringUtils.isNotBlank(nextGenDynamicMediaConfig.getRepositoryId());
    }

    private void initNextGenerationDynamicMedia() {
        initResource();
        properties = resource.getValueMap();
        String fileReference = properties.get("fileReference", String.class);
        String smartCrop = properties.get("smartCrop", String.class);
        if (isNgdmImageReference(fileReference)) {
            int width = currentStyle.get(PN_DESIGN_RESIZE_WIDTH, DEFAULT_NGDM_ASSET_WIDTH);
            NextGenDMImageURIBuilder builder = new NextGenDMImageURIBuilder(nextGenDynamicMediaConfig, fileReference)
                .withPreferWebp(true)
                .withWidth(width);
            if(StringUtils.isNotEmpty(smartCrop)) {
                builder.withSmartCrop(smartCrop);
            }
            src = builder.build();
            ngdmImage = true;
            hasContent = true;
        }
    }

    @NotNull
    private String prepareNgdmSrcUriTemplate() {
        // replace the value of the width URL parameter with the placeholder
        srcUriTemplate = src.replaceFirst("width=\\d+", "width=" + URI_WIDTH_PLACEHOLDER_ENCODED);
        String ret = src.replaceFirst("width=\\d+", "width=" + URI_WIDTH_PLACEHOLDER);
        return ret;
    }

    public static boolean isNgdmImageReference(String fileReference) {
        return StringUtils.isNotBlank(fileReference) && fileReference.startsWith("/urn:");
    }
}
