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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.models.v2.ImageAreaImpl;
import com.adobe.cq.wcm.core.components.internal.servlets.EnhancedRendition;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.ImageArea;
import com.day.cq.commons.DownloadResource;
import com.day.cq.dam.api.Asset;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.adobe.cq.wcm.core.components.internal.helper.image.AssetDeliveryHelper;

import static com.adobe.cq.wcm.core.components.internal.Utils.getWrappedImageResourceWithInheritance;
import static com.adobe.cq.wcm.core.components.models.Teaser.PN_IMAGE_LINK_HIDDEN;


@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);

    private boolean imageLinkHidden = false;

    private String srcSet = StringUtils.EMPTY;
    private Map<String, String> srcSetWithMimeType = Collections.EMPTY_MAP;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        if (hasContent) {
            disableLazyLoading = currentStyle.get(PN_DESIGN_LAZY_LOADING_ENABLED, false);
            imageLinkHidden = properties.get(PN_IMAGE_LINK_HIDDEN, imageLinkHidden);
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
            String srcUriTemplateDecoded = "";
            try {
                srcUriTemplateDecoded = URLDecoder.decode(srcUritemplate, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("Character Decoding failed for " + resource.getPath());
            }
            if (srcUriTemplateDecoded.contains("{.width}")) {
                for (int i = 0; i < widthsArray.length; i++) {
                    if (srcUriTemplateDecoded.contains("={.width}")) {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format("%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    } else {
                        srcsetArray[i] = srcUriTemplateDecoded.replace("{.width}", String.format(".%s", widthsArray[i])) + " " + widthsArray[i] + "w";
                    }
                }
                srcSet = StringUtils.join(srcsetArray, ',');
                return srcSet;
            }
        }
        return null;
    }

    @Nullable
    @Override
    @JsonIgnore
    public String getHeight () {
        int height = getOriginalDimension().height;
        if (height > 0) {
            return String.valueOf(height);
        }
        return null;
    }

    @Nullable
    @Override
    @JsonIgnore
    public String getWidth () {
        int width = getOriginalDimension().width;
        if (width > 0) {
            return String.valueOf(width);
        }
        return null;
    }

    @Override
    @JsonIgnore
    public String getSrcUriTemplate() {
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

    private Dimension getOriginalDimension() {
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
                            int calculatedHeight = (int) Math.round(Integer.parseInt(resizeWidth) * (dimension.getHeight() / (float)dimension.getWidth()));
                            return new Dimension(Integer.parseInt(resizeWidth), calculatedHeight);
                        }
                        return dimension;
                    }
                }
            }
        }
        return new Dimension(0, 0);
    }

}
