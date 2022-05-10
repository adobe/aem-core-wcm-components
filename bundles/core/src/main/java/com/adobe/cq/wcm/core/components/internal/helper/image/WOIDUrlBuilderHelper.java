/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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
package com.adobe.cq.wcm.core.components.internal.helper.image;

import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.spi.AssetDelivery;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.ValueMap;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WOIDUrlBuilderHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WOIDUrlBuilderHelper.class);

    private static String COMMA = ",";
    private static String WIDTH_PARAMETER = "width";
    private static String QUALITY_PARAMETER = "quality";
    private static String CROP_PARAMETER = "c";
    private static String ROTATE_PARAMETER = "r";
    private static String FLIP_PARAMETER = "flip";
    private static String SIZE_PARAMETER = "sz";
    private static String FORMAT_PARAMETER = "format";
    private static String PATH_PARAMETER = "path";
    private static String SEO_PARAMETER = "seoname";


    public static String getSrcSet(@NotNull AssetDelivery assetDelivery, @NotNull Resource assetResource, @NotNull String imageName,
                                   @NotNull String extension, @NotNull ValueMap componentProperties,
                                   int[] smartSizes, Dimension originalDimension, int jpegQuality) {

        if (smartSizes.length == 0) {
            return null;
        }
        List<String> srcsetList = new ArrayList<String>();
        for (int i = 0; i < smartSizes.length; i++) {
            String src =  getSrc(assetDelivery, assetResource,  imageName, extension, componentProperties, new int[]{smartSizes[i]}, jpegQuality, originalDimension);
            if (!StringUtils.isEmpty(src)) {
                srcsetList.add(src + " " + smartSizes[i] + "w");
            }
        }

        if (srcsetList.size() > 0) {
            return StringUtils.join(srcsetList, COMMA);
        }

        return null;
    }

    public static  String getSrc(@NotNull AssetDelivery assetDelivery, @NotNull Resource assetResource, @NotNull String imageName,
                                 @NotNull String extension, @NotNull ValueMap componentProperties,
                                 @NotNull int[] smartSizes, @NotNull int jpegQuality, Dimension originalDimension) {

        Map<String, Object> params = new HashMap<>();

        String assetPath = componentProperties.get(DownloadResource.PN_REFERENCE, String.class);

        if (StringUtils.isEmpty(imageName) || StringUtils.isEmpty(assetPath)
                || StringUtils.isEmpty(extension) || "svg".equalsIgnoreCase(extension)) {
            return null;
        }

        params.put(PATH_PARAMETER, assetPath);
        params.put(SEO_PARAMETER, imageName);
        params.put(FORMAT_PARAMETER, extension);

        if (smartSizes.length == 1) {
            params.put(QUALITY_PARAMETER, "" + jpegQuality);
            params.put(WIDTH_PARAMETER, "" + smartSizes[0]);

        } else if (originalDimension != null && originalDimension.width != 0 && originalDimension.height != 0) {
            params.put(SIZE_PARAMETER, originalDimension.width + "," + originalDimension.height);
        }

        String cropParameter = getCropRect(componentProperties);
        if (!StringUtils.isEmpty(cropParameter)) {
            params.put(CROP_PARAMETER, cropParameter);
        }
        int rotate = getRotation(componentProperties);
        if (Integer.valueOf(rotate) != null && rotate != 0) {
            params.put(ROTATE_PARAMETER, "" + rotate);
        }

        String flipParameter = getFlip(componentProperties);
        if (!StringUtils.isEmpty(flipParameter)) {
            params.put(FLIP_PARAMETER, flipParameter);
        }

        String srcUriTemplateDecoded = "";
        try {
            String woidUrl = assetDelivery.getDeliveryURL(assetResource, params);
            if (!StringUtils.isEmpty(woidUrl)) {
                srcUriTemplateDecoded = URLDecoder.decode(assetDelivery.getDeliveryURL(assetResource, params), StandardCharsets.UTF_8.name());
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Character Decoding failed for " + assetPath);
        }

        return srcUriTemplateDecoded;
    }

    /**
     * Retrieves the cropping rectangle, if one is defined for the image.
     *
     * @param properties the image component's properties
     * @return the cropping parameters, if one is found, {@code null} otherwise
     */
    private static String getCropRect(@NotNull ValueMap properties) {
        String csv = properties.get(ImageResource.PN_IMAGE_CROP, String.class);
        if (StringUtils.isNotEmpty(csv)) {
            try {
                int ratio = csv.indexOf('/');
                if (ratio >= 0) {
                    // skip ratio
                    csv = csv.substring(0, ratio);
                }

                String[] coords = csv.split(",");
                int x1 = Integer.parseInt(coords[0]);
                int y1 = Integer.parseInt(coords[1]);
                int x2 = Integer.parseInt(coords[2]);
                int y2 = Integer.parseInt(coords[3]);
                int width = x2-x1;
                int height = y2-y1;
                return x1 + COMMA + y1 + COMMA + width + COMMA + height;
            } catch (RuntimeException e) {
                LOGGER.warn(String.format("Invalid cropping rectangle %s.", csv), e);
            }
        }
        return null;
    }

    /**
     * Retrieves the rotation angle for the image, if one is present. Typically this should be a value between 0 and 360.
     *
     * @param properties the image component's properties
     * @return the rotation angle
     */
    private static int getRotation(@NotNull ValueMap properties) {
        String rotationString = properties.get(ImageResource.PN_IMAGE_ROTATE, String.class);
        if (rotationString != null) {
            try {
                return Integer.parseInt(rotationString);
            } catch (NumberFormatException e) {
                LOGGER.warn(String.format("Invalid rotation value %s.", rotationString), e);
            }
        }
        return 0;
    }

    /**
     * Retrieves the rotation angle for the image, if one is present. Typically this should be a value between 0 and 360.
     *
     * @param properties the image component's properties
     * @return the rotation angle
     */
    private static String getFlip(@NotNull ValueMap properties) {
        boolean flipHorizontally = properties.get(com.adobe.cq.wcm.core.components.models.Image.PN_FLIP_HORIZONTAL, Boolean.FALSE);
        boolean flipVertically = properties.get(Image.PN_FLIP_VERTICAL, Boolean.FALSE);
        if (flipHorizontally && flipVertically) {
            return "HORIZONTAL_AND_VERTICAL";
        } else if (flipHorizontally) {
            return "HORIZONTAL";
        } else if (flipVertically){
            return "VERTICAL";
        }
        return StringUtils.EMPTY;
    }

}
