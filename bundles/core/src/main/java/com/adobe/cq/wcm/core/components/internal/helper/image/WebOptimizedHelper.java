package com.adobe.cq.wcm.core.components.internal.helper.image;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class WebOptimizedHelper {

    private String DM_DELIVERY_BASE_URL = "/adobe/api/dynamicmedia.deliver";

    private String QUESTION_SYMBOL = "?";

    private String EQUAL_SYMBOL = "=";

    private String AND_SYMBOL = "&";

    private int DEFAULT_WIDTH = 800;

    private int DEFAULT_QUALITY = 75;

    private String WIDTH_PARAMETER = "w";

    private String QUALITY_PARAMETER = "q";

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

    public String getUrl() {
        StringBuilder stringBuilder = new StringBuilder(DM_DELIVERY_BASE_URL + QUESTION_SYMBOL +  WIDTH_PARAMETER + EQUAL_SYMBOL + width +
                                                            AND_SYMBOL + QUALITY_PARAMETER + EQUAL_SYMBOL + quality);

        if (!StringUtils.isEmpty(cropParameter)) {
            stringBuilder.append(AND_SYMBOL + CROP_PARAMETER + EQUAL_SYMBOL + cropParameter);
        }

        if (rotate != 0) {
            stringBuilder.append(AND_SYMBOL + ROTATE_PARAMETER + EQUAL_SYMBOL + rotate);
        }

    }

}
