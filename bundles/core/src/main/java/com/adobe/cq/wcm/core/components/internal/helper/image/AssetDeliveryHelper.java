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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.spi.AssetDelivery;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;

public class AssetDeliveryHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssetDeliveryHelper.class);

    private static String COMMA = ",";
    private static String WIDTH_PARAMETER = "width";
    private static String QUALITY_PARAMETER = "quality";
    private static String CROP_PARAMETER = "c";
    private static String ROTATE_PARAMETER = "r";
    private static String FLIP_PARAMETER = "flip";
    private static String FORMAT_PARAMETER = "format";
    private static String PATH_PARAMETER = "path";
    private static String SEO_PARAMETER = "seoname";
    private static String PREFER_WEBP_PARAMETER = "preferwebp";
    private static String HORIZONTAL_FLIP = "HORIZONTAL";
    private static String VERTICAL_FLIP = "VERTICAL";
    private static String HORIZONTAL_AND_VERTICAL_FLIP = "HORIZONTAL_AND_VERTICAL";
    private static int PSEUDO_WIDTH_PARAM = Integer.MAX_VALUE;


    public static String getSrcSet(@NotNull AssetDelivery assetDelivery, @NotNull Resource imageComponentResource, @NotNull String imageName,
                                   @NotNull String extension, int[] smartSizes, int jpegQuality) {

        if (smartSizes.length == 0) {
            return null;
        }
        List<String> srcsetList = new ArrayList<String>();
        for (int i = 0; i < smartSizes.length; i++) {
            String src =  getSrc(assetDelivery, imageComponentResource,  imageName, extension, smartSizes[i], jpegQuality);
            if (!StringUtils.isEmpty(src)) {
                srcsetList.add(src + " " + smartSizes[i] + "w");
            }
        }

        if (srcsetList.size() > 0) {
            return StringUtils.join(srcsetList, COMMA);
        }

        return null;
    }

    public static String getSrcUriTemplate(@NotNull AssetDelivery assetDelivery, @NotNull Resource imageComponentResource,
                                           @NotNull String imageName, @NotNull String extension,
                                           @Nullable Integer jpegQuality, @NotNull String widthPlaceholder) {

        String templateUrl = getSrc(assetDelivery, imageComponentResource, imageName, extension, PSEUDO_WIDTH_PARAM, jpegQuality);
        if(!StringUtils.isEmpty(templateUrl)) {
            templateUrl = templateUrl.replaceAll(String.valueOf(PSEUDO_WIDTH_PARAM), widthPlaceholder);
        }
        return templateUrl;
    }

    public static  String getSrc(@NotNull AssetDelivery assetDelivery, @NotNull Resource imageComponentResource,
                                  @NotNull String imageName, @NotNull String extension,
                                  @Nullable Integer width, @Nullable Integer jpegQuality) {

        Map<String, Object> params = new HashMap<>();

        ValueMap componentProperties = imageComponentResource.getValueMap();
        String assetPath = componentProperties.get(DownloadResource.PN_REFERENCE, String.class);

        if (StringUtils.isEmpty(imageName) || StringUtils.isEmpty(assetPath)
                || StringUtils.isEmpty(extension) || "svg".equalsIgnoreCase(extension)) {
            return null;
        }

        Resource assetResource = imageComponentResource.getResourceResolver().getResource(assetPath);
        if (assetResource == null) {
            return null;
        }

        params.put(PATH_PARAMETER, assetPath);
        params.put(SEO_PARAMETER, imageName);
        params.put(FORMAT_PARAMETER, extension);
        params.put(PREFER_WEBP_PARAMETER, "true");

        if (jpegQuality != null) {
            addQualityParameter(params, jpegQuality);
        }
        if (width != null) {
            addWidthParameter(params, width);
        }

        addCropParameter(params, componentProperties);
        addRotationParameter(params, componentProperties);
        addFlipParameter(params, componentProperties);

        String assetDeliveryURL = assetDelivery.getDeliveryURL(assetResource, params);
        if (!StringUtils.isEmpty(assetDeliveryURL)) {
            return  assetDeliveryURL;
        }
        return StringUtils.EMPTY;
    }

    private static void addQualityParameter(@NotNull Map<String, Object> params, @NotNull int quality) {
        params.put(QUALITY_PARAMETER, "" + quality);
    }

    private static void addWidthParameter(@NotNull Map<String, Object> params, @NotNull int width) {
        params.put(WIDTH_PARAMETER, "" + width);
    }

    private static void addCropParameter(@NotNull Map<String, Object> params, @NotNull ValueMap componentProperties) {
        String cropParameter = getCropRect(componentProperties);
        if (!StringUtils.isEmpty(cropParameter)) {
            params.put(CROP_PARAMETER, cropParameter);
        }
    }

    private static void addRotationParameter(@NotNull Map<String, Object> params, @NotNull ValueMap componentProperties) {
        int rotate = getRotation(componentProperties);
        if (Integer.valueOf(rotate) != null && rotate != 0) {
            params.put(ROTATE_PARAMETER, "" + rotate);
        }
    }

    private static void addFlipParameter(@NotNull Map<String, Object> params, @NotNull ValueMap componentProperties) {
        String flipParameter = getFlip(componentProperties);
        if (!StringUtils.isEmpty(flipParameter)) {
            params.put(FLIP_PARAMETER, flipParameter);
        }
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
     * Retrieves the flip parameter for the image, if one is present.
     * @param properties the image component's properties
     * @return the flip parameter
     */
    private static String getFlip(@NotNull ValueMap properties) {
        boolean flipHorizontally = properties.get(Image.PN_FLIP_HORIZONTAL, Boolean.FALSE);
        boolean flipVertically = properties.get(Image.PN_FLIP_VERTICAL, Boolean.FALSE);
        if (flipHorizontally && flipVertically) {
            return HORIZONTAL_AND_VERTICAL_FLIP;
        } else if (flipHorizontally) {
            return HORIZONTAL_FLIP;
        } else if (flipVertically){
            return VERTICAL_FLIP;
        }
        return StringUtils.EMPTY;
    }

}
