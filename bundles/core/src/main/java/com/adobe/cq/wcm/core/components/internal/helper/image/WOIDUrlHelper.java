package com.adobe.cq.wcm.core.components.internal.helper.image;

import com.adobe.cq.wcm.core.components.models.Image;
import com.day.cq.commons.DownloadResource;
import com.day.cq.commons.ImageResource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.ValueMap;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WOIDUrlHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WOIDUrlHelper.class);

    private static String QUESTION = "?";

    private static String EQUAL = "=";

    private static String AND = "&";

    private static final String PATH_SEPARATOR = "/";

    private static String DOT = ".";

    private static String COMMA = ",";

    private static String WIDTH_PARAMETER = "width";

    private static String QUALITY_PARAMETER = "quality";


    private static String CROP_PARAMETER = "c";

    private static String ROTATE_PARAMETER = "r";

    private static String FLIP_PARAMETER = "flip";

    private static String SIZE_PARAMETER = "sz";


    public static String getSrcSet(@NotNull String WOIDBaseUrl, @NotNull String imageName,
                                   @NotNull String mimeType, @NotNull ValueMap componentProperties,
                                   int[] smartSizes, Dimension originalDimension, int jpegQuality) {

        if (smartSizes.length == 0) {
            return null;
        }
        List<String> srcsetList = new ArrayList<String>();
        for (int i = 0; i < smartSizes.length; i++) {
            String src =  getSrc(WOIDBaseUrl, imageName, mimeType, componentProperties, new int[]{smartSizes[i]}, originalDimension, jpegQuality);
            if (!StringUtils.isEmpty(src)) {
                srcsetList.add(src);
            }
        }

        if (srcsetList.size() > 0) {
            return StringUtils.join(srcsetList, COMMA);
        }

        return null;
    }

    public static  String getSrc(@NotNull String WOIDBaseUrl, @NotNull String imageName,
                                 @NotNull String mimeType, @NotNull ValueMap componentProperties,
                                 int[] smartSizes, Dimension originalDimension, int jpegQuality) {

        String assetPath = componentProperties.get(DownloadResource.PN_REFERENCE, String.class);

        if (StringUtils.isEmpty(WOIDBaseUrl) || StringUtils.isEmpty(imageName) || StringUtils.isEmpty(assetPath)
                || StringUtils.isEmpty(mimeType) || "svg".equalsIgnoreCase(mimeType)) {
            return null;
        }

        String srcUrl = WOIDBaseUrl + assetPath +
            PATH_SEPARATOR + imageName + DOT + mimeType;

        StringBuilder stringBuilder = new StringBuilder();
        if (smartSizes.length == 1) {
            stringBuilder.append(WIDTH_PARAMETER + EQUAL + smartSizes[0] +
                AND + QUALITY_PARAMETER + EQUAL + jpegQuality);
        } else if (originalDimension.width != 0 && originalDimension.height != 0) {
            // in image component v3, img tag has width and height set, so if smart size is not width paramater,
            // better to get image from dm with weight and hight
            // this needs to be confirm though
            stringBuilder.append(SIZE_PARAMETER + EQUAL + originalDimension.width + "," + originalDimension.height);
        }

        String cropParameter = getCropRect(componentProperties);
        if (!StringUtils.isEmpty(cropParameter)) {
            stringBuilder.append(AND + CROP_PARAMETER  + EQUAL + cropParameter);
        }
        int rotate = getRotation(componentProperties);
        if (Integer.valueOf(rotate) != null && rotate != 0) {
            stringBuilder.append(AND + ROTATE_PARAMETER  + EQUAL + rotate);
        }

        String flipParameter = getFlip(componentProperties);
        if (!StringUtils.isEmpty(flipParameter)) {
            stringBuilder.append(AND + FLIP_PARAMETER  + EQUAL + flipParameter);
        }

        if (stringBuilder.length() > 0) {
            stringBuilder.insert(0, srcUrl + QUESTION);
        } else {
            // no parameter added so far
            stringBuilder.append(srcUrl);
        }

        String srcUriTemplateDecoded = "";
        try {
            srcUriTemplateDecoded = URLDecoder.decode(stringBuilder.toString(), StandardCharsets.UTF_8.name());
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

        StringBuilder flip = new StringBuilder();

        if (flipHorizontally) {
            flip.append("h");
        }

        if (flipVertically) {
            flip.append("v");
        }
        return flip.toString();
    }

}
