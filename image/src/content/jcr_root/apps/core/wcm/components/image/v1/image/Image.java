/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package apps.core.wcm.components.image.v1.image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.commons.mime.MimeTypeService;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.SightlyWCMMode;
import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
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

    public static final String ADAPTIVE_IMAGE_SERVLET_PID = "com.day.cq.wcm.foundation.impl.AdaptiveImageComponentServlet";
    public static final String ADAPTIVE_IMAGE_SERVLET_WIDTH_CONF = "adapt.supported.widths";
    public static final Set<Integer> DEFAULT_SUPPORTED_WIDTHS = new HashSet<>(Arrays.asList(new Integer[] {
            320, // iPhone portrait
            476, // iPad portrait
            480, // iPhone landscape
            620  // iPad landscape
    }));

    public static final String PLACEHOLDER_TOUCH = "cq-placeholder file";
    public static final String PLACEHOLDER_CLASSIC = "cq-image-placeholder";

    private static final Pattern mapInfoRegex = Pattern.compile("\\[([^(]*)\\(([^)]*)\\)([^|]*)\\|([^|]*)\\|([^\\]]*)\\]");
    private static final Pattern mapItemRegex = Pattern.compile("\"([^\"]*)\"");

    private String link;
    private String fileReference;
    private String fileName;
    private String src;
    private String alt;
    private String title;
    private int width;
    private int height;
    private ImageMap imageMap;
    private String[] smartImages = new String[0];
    private String extension;
    private String caption;

    private Resource resource;
    private SlingHttpServletRequest request;

    private Set<Integer> supportedWidths;

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
    private String cssClass;


    @Override
    public void activate() throws Exception {
        resource = getResource();
        request = getRequest();
        ValueMap properties = getProperties();
        Style style = getCurrentStyle();
        extension = "jpg";
        allowedRenditionWidths = getSupportedWidths(style.get(DESIGN_PROP_ALLOWED_RENDITION_WIDTHS, new Integer[]{}));
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
            int dotIndex;
            if ((dotIndex = fileReference.lastIndexOf(".")) != -1) {
                extension = fileReference.substring(dotIndex + 1);
            }
        } else {
            Resource file = resource.getChild(CHILD_NODE_IMAGE_FILE);
            if (file != null) {
                fileName = resource.getName();
                SlingScriptHelper scriptHelper = getSlingScriptHelper();
                MimeTypeService mimeTypeService = scriptHelper.getService(MimeTypeService.class);
                if (mimeTypeService != null) {
                    extension = mimeTypeService.getExtension(
                            PropertiesUtil.toString(
                                    file.getResourceMetadata().get(ResourceMetadata.CONTENT_TYPE),
                                    "image/jpeg"));
                }
            }
        }

        if (StringUtils.isNotEmpty(fileName)) {
            src = request.getContextPath() + resource.getPath() + ".img.full.high." + extension;
            title = properties.get(NameConstants.PN_TITLE, String.class);
            alt = properties.get(PROP_ALT, String.class);
            if (allowLinking) {
                link = properties.get(PROP_LINK_URL, String.class);
            }
            if (allowCaptionText) {
                caption = properties.get(NameConstants.PN_DESCRIPTION, String.class);
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
            if (allowImageMaps) {
                String imageMapString = properties.get(PROP_IMAGE_MAP, String.class);
                if (StringUtils.isNotEmpty(imageMapString)) {
                    Matcher matcher = mapInfoRegex.matcher(imageMapString);
                    List<ImageMapArea> areas = new ArrayList<>();
                    while (matcher.find()) {
                        String type = matcher.group(1);
                        String coords = matcher.group(2);
                        String href = matcher.group(3);
                        Matcher hrefMatcher = mapItemRegex.matcher(href);
                        if (hrefMatcher.matches()) {
                            href = hrefMatcher.group(1);
                        }

                        String target = matcher.group(4);
                        Matcher targetMatcher = mapItemRegex.matcher(target);
                        if (targetMatcher.matches()) {
                            target = targetMatcher.group(1);
                        }

                        String text = matcher.group(5);
                        Matcher textMatcher = mapItemRegex.matcher(text);
                        if (textMatcher.matches()) {
                            text = textMatcher.group(1);
                        }
                        areas.add(new ImageMapArea(type, coords, href, target, text));
                    }
                    String imageMapName = "map-" + System.currentTimeMillis();
                    imageMap = new ImageMap("#" + imageMapName, imageMapName, areas);
                    // disable the link since we have maps
                    link = null;
                }
            }
        }
        SightlyWCMMode wcmMode = getWcmMode();
        if (wcmMode.isEdit()) {
            cssClass = "cq-dd-image " + (AuthoringUtils.isTouch(request) ? PLACEHOLDER_TOUCH : PLACEHOLDER_CLASSIC);
        }
    }

    private Integer[] getSupportedWidths(Integer[] widths) throws Exception {
        if (widths == null || widths.length == 0) {
            return widths;
        }
        // widths supported by the adaptive image servlet
        Set<Integer> adaptiveImageServletWidths = getSupportedWidths();
        Set<Integer> supported = new HashSet<>();
        for (Integer width : widths) {
            if (adaptiveImageServletWidths.contains(width)) {
                supported.add(width);
            }
        }
        return supported.toArray(new Integer[0]);
    }

    /**
     * Returns the set of supported widths for adaptive images.
     *
     * @return the set of supported widths for adaptive images
     *
     * @throws Exception
     */
    public Set<Integer> getSupportedWidths() throws Exception {
        if (supportedWidths != null) {
            return supportedWidths;
        }
        ConfigurationAdmin configurationAdmin = getSlingScriptHelper().getService(ConfigurationAdmin.class);
        String filter = String.format("(service.pid=%s)", ADAPTIVE_IMAGE_SERVLET_PID);
        Configuration[] configurations = configurationAdmin.listConfigurations(filter);
        if (configurations != null) {
            for (Configuration config : configurations) {
                Dictionary properties = config.getProperties();
                if (properties != null) {
                    String[] widths = PropertiesUtil.toStringArray(properties.get(ADAPTIVE_IMAGE_SERVLET_WIDTH_CONF), null);
                    if (widths != null) {
                        supportedWidths = new HashSet<>();
                        for (String strWidth : widths) {
                            try {
                                supportedWidths.add(Integer.valueOf(strWidth));
                            } catch (NumberFormatException e) {
                                LOGGER.error("Invalid number format for supported with: {}", strWidth);
                            }
                        }
                    }
                    return supportedWidths;
                }
            }
        }
        supportedWidths = DEFAULT_SUPPORTED_WIDTHS;
        return supportedWidths;
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
     * Returns the image's link URL, if one was set.
     *
     * @return the image's link URL, if one was set, or {@code null}
     */
    public String getLink() {
        return link;
    }

    public String getCssClass() {
        return cssClass;
    }

    public ImageMap getImageMap() {
        return imageMap;
    }

    public String getCaption() {
        return caption;
    }

    public boolean shouldDisplayCaptionPopup() {
        return displayCaptionPopup;
    }

    public class ImageMap {
        private String id;
        private String name;
        private List<ImageMapArea> areas;

        public ImageMap(String id, String name, List<ImageMapArea> areas) {
            this.id = id;
            this.name = name;
            this.areas = areas;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public List<ImageMapArea> getAreas() {
            return areas;
        }
    }

    public class ImageMapArea {
        private String type;
        private String coordinates;
        private String href;
        private String target;
        private String text;

        public ImageMapArea(String type, String coordinates, String href, String target, String text) {

            this.type = type;
            this.coordinates = coordinates;
            this.href = href;
            this.target = target;
            this.text = text;
        }

        public String getType() {
            return type;
        }

        public String getCoordinates() {
            return coordinates;
        }

        public String getHref() {
            return href;
        }

        public String getTarget() {
            return target;
        }

        public String getText() {
            return text;
        }


    }
}
