/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.image.v1.image;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.commons.mime.MimeTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.designer.Style;

public class Image extends WCMUsePojo {

    public static final Logger LOGGER = LoggerFactory.getLogger(Image.class);

    public static final String PROP_FILE_REFERENCE = "fileReference";
    public static final String PROP_ALT = "alt";
    public static final String PROP_IS_DECORATIVE = "isDecorative";
    public static final String PROP_WIDTH = "width";
    public static final String PROP_HEIGHT = "height";
    public static final String PROP_LINK_URL = "linkURL";
    public static final String PROP_IMAGE_MAP = "imageMap";

    public static final String DESIGN_PROP_ALLOWED_RENDITION_WIDTHS = "allowedRenditionWidths";
    public static final String DESIGN_PROP_ENFORCE_ASPECT_RATIO = "enforceAspectRatio";
    public static final String DESIGN_PROP_ALLOW_CROPPING = "allowCropping";
    public static final String DESIGN_PROP_ALLOW_ROTATING = "allowRotating";
    public static final String DESIGN_PROP_ALLOW_IMAGE_MAPS = "allowImageMaps";
    public static final String DESIGN_PROP_ALLOW_LINKING = "allowLinking";
    public static final String DESIGN_PROP_ALLOW_CAPTION_TEXT = "allowCaptionText";
    public static final String DESIGN_PROP_DISPLAY_CAPTION_POPUP = "displayCaptionPopup";
    public static final String DESIGN_PROP_ALLOWED_STYLES = "allowedStyles";

    public static final String CHILD_NODE_IMAGE_FILE = "file";

    private String link;
    private String fileReference;
    private String fileName;
    private String src;
    private String alt;
    private String title;
    private int width;
    private int height;
    private List<ImageMapItem> imageMap;
    private String[] smartImages = new String[0];
    private String extension;
    private String caption;

    private Resource resource;
    private SlingHttpServletRequest request;

    // design properties
    private Integer[] allowedRenditionWidths;
    private boolean enforceAspectRatio;
    private boolean allowCropping;
    private boolean allowRotating;
    private boolean allowImageMaps;
    private boolean allowLinking;
    private boolean allowCaptionText;
    private boolean displayCaptionPopup;
    private String[] allowedStyles;

    @Override
    public void activate() throws Exception {
        resource = getResource();
        request = getRequest();
        ValueMap properties = getProperties();
        Style style = getCurrentStyle();
        extension = "jpg";
        allowedRenditionWidths = style.get(DESIGN_PROP_ALLOWED_RENDITION_WIDTHS, new Integer[]{});
        enforceAspectRatio = style.get(DESIGN_PROP_ENFORCE_ASPECT_RATIO, true);
        allowCropping = style.get(DESIGN_PROP_ALLOW_CROPPING, true);
        allowRotating = style.get(DESIGN_PROP_ALLOW_ROTATING, true);
        allowImageMaps = style.get(DESIGN_PROP_ALLOW_IMAGE_MAPS, true);
        allowLinking = style.get(DESIGN_PROP_ALLOW_LINKING, true);
        allowCaptionText = style.get(DESIGN_PROP_ALLOW_CAPTION_TEXT, true);
        displayCaptionPopup = style.get(DESIGN_PROP_DISPLAY_CAPTION_POPUP, false);
        allowedStyles = style.get(DESIGN_PROP_ALLOWED_STYLES, new String[]{});

        fileReference = properties.get(PROP_FILE_REFERENCE, "");
        if (StringUtils.isNotEmpty(fileReference)) {
            fileName = fileReference.substring(fileReference.lastIndexOf("/") + 1);
            extension = fileReference.substring(fileReference.lastIndexOf(".") + 1);
        } else {
            Resource file = resource.getChild(CHILD_NODE_IMAGE_FILE);
            if (file != null) {
                fileName = resource.getName();
                SlingScriptHelper scriptHelper = getSlingScriptHelper();
                MimeTypeService mimeTypeService = scriptHelper.getService(MimeTypeService.class);
                if (mimeTypeService != null) {
                    extension = mimeTypeService.getExtension((String) file.getResourceMetadata().getOrDefault(ResourceMetadata
                            .CONTENT_TYPE, "image/jpeg"));
                }
            }
        }
        if (allowedRenditionWidths.length > 0) {
            Arrays.sort(allowedRenditionWidths);
            smartImages = new String[allowedRenditionWidths.length];
            int index = 0;
            for (int width : allowedRenditionWidths) {
                smartImages[index] = "\"" + request.getContextPath() + resource.getPath() + ".img." + width + ".high." + extension + "\"";
                index++;
            }
        }
        width = properties.get(PROP_WIDTH, 0);
        if (width < 0) {
            width = 0;
        }
        height = properties.get(PROP_HEIGHT, 0);
        if (height < 0) {
            height = 0;
        }
        src = request.getContextPath() + resource.getPath() + ".img.full.high." + extension;
        alt = properties.get(PROP_ALT, String.class);
        title = properties.get(NameConstants.PN_TITLE, String.class);
        if (allowLinking) {
            link = properties.get(PROP_LINK_URL, String.class);
        }
        if (allowCaptionText) {
            caption = properties.get(NameConstants.PN_DESCRIPTION, String.class);
        }

    }

    /**
     * Returns the file name of the image.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the referenced image file.
     *
     * @return the referenced image file
     */
    public String getFileReference() {
        return fileReference;
    }

    /**
     * Returns the value for the {@code src} attribute of the image.
     *
     * @return the image's URL
     */
    public String getSrc() {
        return src;
    }

    /**
     * Returns the array of allowed rendition widths, if one was defined for the component's design.
     *
     * @return the array of allowed rendition widths, or an empty array if the allowed renditions widths have not been configured for the
     * component's design
     */
    public Integer[] getSmartSizes() {
        return allowedRenditionWidths;
    }

    /**
     * Returns an array of URLs for smart images, if the component's design provides an array of allowed rendition widths.
     *
     * @return the array of URLs for smart images, or an empty array if the component's design doesn't provide an array of allowed
     * rendition widths
     */
    public String[] getSmartImages() {
        return smartImages;
    }

    /**
     * Returns the set width of the image.
     *
     * @return the set width of the image; will return 0 if the set width is negative or missing
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the set height of the image.
     *
     * @return the set height of the image; will return 0 if the set height is negative or missing
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the value for the image's {@code alt} attribute, if one was set.
     *
     * @return the value, if one was set, or {@code null}
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Returns the value for the image's {@code title} attribute, if one was set.
     *
     * @return the value, if one was set, or {@code null}
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the caption's value if one was set.
     *
     * @return the caption's value, if one was set, or {@code null}
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Returns the image's link URL, if one was set.
     *
     * @return the image's link URL, if one was set, or {@code null}
     */
    public String getLink() {
        return link;
    }

    public class ImageMapItem {
        private String type;
        private String coordonates;
        private String href;
        private String target;
        private String alt;
        private String title;
    }
}
