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
package com.adobe.cq.wcm.core.components.models;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.datalayer.ImageData;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Defines the {@code Image} Sling Model used for the {@code /apps/core/wcm/components/image} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface Image extends Component {

    /**
     * Name of the resource property that will indicate if the image is inherited from the featured image of the page.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.23.0
     */
    String PN_IMAGE_FROM_PAGE_IMAGE = "imageFromPageImage";

    /**
     * Name of the resource property that will indicate if the value of the {@code alt} attribute should be inherited
     * from the featured image of the page.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.23.0
     */
    String PN_ALT_VALUE_FROM_PAGE_IMAGE = "altValueFromPageImage";

    /**
     * Name of the configuration policy property that will store the allowed rendition widths for an image.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_DESIGN_ALLOWED_RENDITION_WIDTHS = "allowedRenditionWidths";

    /**
     * Name of the configuration policy property that will store the image quality for an image.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    String PN_DESIGN_JPEG_QUALITY = "jpegQuality";

    /**
     * Name of the configuration policy property that will indicate if lazy loading should be disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_DESIGN_LAZY_LOADING_ENABLED = "disableLazyLoading";

    /**
     * Name of the configuration policy property that indicate the number of pixels, in advance of becoming visible,
     * that a lazy loading image should load.
     */
    String PN_DESIGN_LAZY_THRESHOLD = "lazyThreshold";

    /**
     * Name of the resource property that will indicate if the image is decorative.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_IS_DECORATIVE = "isDecorative";

    /**
     * Name of the policy property that defines whether or not the UUID is disabled.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    String PN_UUID_DISABLED = "uuidDisabled";

    /**
     * Name of the resource property that will indicate if the image's caption will be rendered as a popup.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_DISPLAY_POPUP_TITLE = "displayPopupTitle";

    /**
     * Name of the JSON property that will store the smart sizes for smart loading.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     * @deprecated since 12.1.0
     */
    @Deprecated
    String JSON_SMART_SIZES = "smartSizes";

    /**
     * Name of the JSON property that will store the smart images for smart loading.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     * @deprecated since 12.1.0
     */
    @Deprecated
    String JSON_SMART_IMAGES = "smartImages";

    /**
     * Name of the JSON property that will indicate if the image should be loaded lazily.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     * @deprecated since 12.1.0
     */
    @Deprecated
    String JSON_LAZY_ENABLED = "lazyEnabled";

    /**
     * Name of the configuration policy property that will indicate if the value of the {@code alt} attribute should be populated from
     * DAM if the component is configured with a file reference.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_ALT_VALUE_FROM_DAM = "altValueFromDAM";

    /**
     * Name of the configuration policy property that will indicate if the value of the {@code title} attribute should be populated from
     * DAM if the component is configured with a file reference.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_TITLE_VALUE_FROM_DAM = "titleValueFromDAM";

    /**
     * Name of the resource property that will indicate if the current image should be flipped horizontally.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.3.0
     */
    String PN_FLIP_HORIZONTAL = "imageFlipHorizontal";

    /**
     * Name of the resource property that will indicate if the current image should be flipped vertically.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.3.0
     */
    String PN_FLIP_VERTICAL = "imageFlipVertical";

    /**
     * Name of the resource property that will indicate if the current image should has Image Modifiers settings.
     */
    String PN_IMAGE_MODIFIERS = "imageModifers";

    /**
     * Name of the resource property that will indicate imageServerUrl.
     */
    String PN_IMAGE_SERVER_URL = "imageServerUrl";

    /**
     * Name of the resource property that defines areas of an image map.
     * <p>
     * The property stores map areas as follows:
     * [area1][area2][...]
     * <p>
     * Area format:
     * [SHAPE(COORDINATES)"HREF"|"TARGET"|"ALT"|(RELATIVE_COORDINATES)]
     * <p>
     * Example:
     * [rect(0,0,10,10)"http://www.adobe.com"|"_self"|"alt"|(0,0,0.8,0.8)][circle(10,10,10)"http://www.adobe.com"|"_self"|"alt"|(0.8,0.8,0.8)]
     *
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    String PN_MAP = "imageMap";

    /**
     * Name of the configuration policy property that controls whether Dynamic Media features are used by Core component.
     */
    String PN_DESIGN_DYNAMIC_MEDIA_ENABLED = "enableDmFeatures";

    /**
     * Name of the configuration policy property that controls whether assets will be delivered through Dynamic Media.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.25.0
     */
    String PN_DESIGN_ASSET_DELIVERY_ENABLED = "enableAssetDelivery";

    /**
     * Name of the configuration policy property that will be used for resizing the base images, the ones from {@code src} attribute.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.23.0
     */
    String PN_DESIGN_RESIZE_WIDTH = "resizeWidth";

    /**
     * Returns the value for the {@code src} attribute of the image.
     *
     * @return the image's URL
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getSrc() {
        return null;
    }

    /**
     * Returns the value for the {@code alt} attribute of the image.
     *
     * @return the value for the image's {@code alt} attribute, if one was set, or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getAlt() {
        return null;
    }

    /**
     * Returns the value for the image's {@code title} attribute, if one was set.
     *
     * @return the value for the image's {@code title} attribute, if one was set, or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getTitle() {
        return null;
    }

    /**
     * Returns the value for the image's uuid, if one was set.
     *
     * @return the value for the image's uuid, if one was set, or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 12.4.0;
     */
    default String getUuid() {
        return null;
    }

    /**
     * Returns the image's link.
     *
     * @return the image's link.
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    default Link getImageLink() {
        return null;
    }

    /**
     * Returns the image's link URL, if one was set.
     *
     * @return the image's link URL, if one was set, or {@code null}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     * @deprecated Please use {@link #getImageLink()}
     */
    @Deprecated
    default String getLink() {
        return null;
    }

    /**
     * Checks if the image should display its caption as a popup (through the <code>&lt;img&gt;</code> {@code title}
     * attribute).
     *
     * @return {@code true} if the caption should be displayed as a popup, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean displayPopupTitle() {
        return false;
    }

    /**
     * Returns the file reference of the current image, if one exists.
     *
     * @return the file reference of the current image, if one exists, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @JsonIgnore
    default String getFileReference() {
        return null;
    }

    /**
     * Returns a JSON object used for the smart image functionality. The object provides the following properties:
     *
     * <ul>
     *     <li>{@link #JSON_SMART_SIZES} - array of integers, representing the available image widths</li>
     *     <li>{@link #JSON_SMART_IMAGES} - array of strings, providing the URLs for the available image renditions</li>
     *     <li>{@link #JSON_LAZY_ENABLED} - boolean, specifying if the image should be rendered lazily or not</li>
     * </ul>
     *
     * @return the JSON for the smart image functionality
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     * @deprecated since 12.1.0
     */
    @Deprecated
    @JsonIgnore
    default String getJson() {
        return null;
    }

    /**
     * Returns the alternative image widths (in pixels), configured through the {@link #PN_DESIGN_ALLOWED_RENDITION_WIDTHS}
     * content policy. If no configuration is present, this method will return an empty array.
     *
     * @return the alternative image widths (in pixels)
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    default int[] getWidths() {
        return new int[]{};
    }

    /**
     * Returns a URI template representation of the image src attribute that can be variable expanded
     * to a URI reference. Useful for building an alternative image configuration from the original src.
     *
     * @return the image src URI template
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default String getSrcUriTemplate() {
        return null;
    }

    /**
     * Indicates if the image should be rendered lazily or not.
     *
     * @return true if the image should be rendered lazily; false otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default boolean isLazyEnabled() {
        return false;
    }

    /**
     * Returns the value for the {@code srcset} html attribute of the image.
     *
     * @return the value of the {@code srcset} attribute, if one was set, or {@code null}.
     * @since com.adobe.cq.wcm.core.components.models 12.21.0
     */
    default String getSrcset() {
        return null;
    }

    /**
     * Returns the width of the base DAM asset image, the one from the {@code src} attribute.
     * It will be used as value for the {@code width} attribute of the image, only if the image is a DAM asset and is not SVG.
     *
     * @return the width of the base DAM asset image, the one from the {@code src} attribute.
     * @since com.adobe.cq.wcm.core.components.models 12.21.0;
     */
    default String getWidth() {
        return null;
    }

    /**
     * Returns the height of the base DAM asset image, the one from the {@code src} attribute.
     * It will be used as value for the {@code height} attribute of the image, only if the image is a DAM asset and is not SVG.
     *
     * @return the height of the base DAM asset image, the one from the {@code src} attribute.
     * @since com.adobe.cq.wcm.core.components.models 12.21.0;
     */
    default String getHeight() {
        return null;
    }

    /**
     * Returns the number of pixels in advance of an image becoming visible that a lazy
     * loading image should load.
     *
     * @return The number of pixels.
     */
    default int getLazyThreshold() {
        return 0;
    }

    /**
     * Returns a list of image map areas.
     *
     * @return the image map areas
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    default List<ImageArea> getAreas() {
        return null;
    }

    /**
     * Indicates whether the image is decorative.
     *
     * @return true if the image is decorative; false otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.11.0
     */
    default boolean isDecorative() {
        return false;
    }

    default String getSmartCropRendition() {
        return null;
    }

    default boolean isDmImage() {
        return false;
    }

    default ImageData getComponentData() {
        return null;
    }

}
