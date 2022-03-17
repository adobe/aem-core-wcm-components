package com.adobe.cq.wcm.core.components.internal.helper.image;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class WebOptimizedHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebOptimizedHelper.class);

    private String DM_DELIVERY_BASE_URL = "https://publish-p56138-e410068.adobeaemcloud.com/api/dynamicmedia.deliver";

    private static String QUESTION_SYMBOL = "?";

    private static String EQUAL_SYMBOL = "=";

    private static String AND_SYMBOL = "&";

    private static final String PATH_SEPARATOR = "/";

    private static String DOT = ".";

    private int DEFAULT_WIDTH = 800;

    private int DEFAULT_QUALITY = 75;

    private static String WIDTH_PARAMETER = "width";

    private static String QUALITY_PARAMETER = "quality";

    private String PATH_PARAMETER = "path";

    private String CROP_PARAMETER = "c";

    private String ROTATE_PARAMETER = "r";

    private String FLIP_PARAMETER = "flip";

    private String SIZE_PARAMETER = "sz";



    private String assetPath;
    private int width = DEFAULT_WIDTH;
    private int quality = DEFAULT_QUALITY;
    private String cropParameter;
    private int rotate =0;
    private String[] imageFormat;
    private String seoName;

    public WebOptimizedHelper(@NotNull String assetPath) {
        this.assetPath = assetPath;
    }
    public WebOptimizedHelper(@NotNull String assetPath, @NotNull int width, @NotNull int quality, String cropParameter, int rotate, String[] imageFormat, String seoName) {
        this.assetPath = assetPath;
        this.width = width;
        this.quality = quality;
        this.cropParameter = cropParameter;
        this.rotate = rotate;
        this.imageFormat = imageFormat;
        if (this.imageFormat.length == 0) {
            this.imageFormat = new String[]{"jpeg"};
        }
        this.seoName = seoName;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setCropParameter(String cropParameter) {
        this.cropParameter = cropParameter;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public void setImageFormat(String[] imageFormat) {
        this.imageFormat = imageFormat;
    }

    public void setSeoName(String seoName) {
        this.seoName = seoName;
    }

    public static String getSrcSet(String dmNextBaseUrl, String assetPath, String imageName, String mimeType, int[] widths, int jpegQuality) {
        if (widths.length == 0) {
            return null;
        }
        String widthPlaceHolder = "{.width}";
        String[] srcsetArray = new String[widths.length];
        String srcUritemplate = dmNextBaseUrl + assetPath +
                                PATH_SEPARATOR + imageName + DOT + mimeType +
                                QUESTION_SYMBOL + WIDTH_PARAMETER + EQUAL_SYMBOL + widthPlaceHolder +
                                AND_SYMBOL + QUALITY_PARAMETER + EQUAL_SYMBOL + jpegQuality;

        String srcUriTemplateDecoded = "";
        try {
            srcUriTemplateDecoded = URLDecoder.decode(srcUritemplate, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Character Decoding failed for " + assetPath);
        }

        if (srcUriTemplateDecoded.contains(widthPlaceHolder)) {
            for (int i = 0; i < widths.length; i++) {
                    srcsetArray[i] = srcUriTemplateDecoded.replace(widthPlaceHolder, String.format("%s", widths[i])) + " " + widths[i] + "w";
            }
            return StringUtils.join(srcsetArray, ',');
        }
        return null;
    }

    public String getSrc(String dmNextBaseUrl, String assetPath, String imageName, String mimeType, int[] widths, int originalWidth, int originalHeight, int jpegQuality) {

        return StringUtils.EMPTY;
    }

}
