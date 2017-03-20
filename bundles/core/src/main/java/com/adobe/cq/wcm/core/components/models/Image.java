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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Image} Sling Model used for the {@code /apps/core/wcm/components/image} component.
 */
@ConsumerType
public interface Image {

    /**
     * Name of the configuration policy property that will store the allowed rendition widths for an image.
     */
    String PN_DESIGN_ALLOWED_RENDITION_WIDTHS = "allowedRenditionWidths";

    /**
     * Name of the configuration policy property that will indicate if lazy loading should be disabled.
     */
    String PN_DESIGN_LAZY_LOADING_ENABLED = "disableLazyLoading";

    /**
     * Name of the resource property that will indicate if the image is decorative.
     */
    String PN_IS_DECORATIVE = "isDecorative";

    /**
     * Name of the resource property that will indicate if the image's caption will be rendered as a popup.
     */
    String PN_DISPLAY_POPUP_TITLE = "displayPopupTitle";

    /**
     * Name of the JSON property that will store the smart sizes for smart loading.
     */
    String JSON_SMART_SIZES = "smartSizes";

    /**
     * Name of the JSON property that will store the smart images for smart loading.
     */
    String JSON_SMART_IMAGES = "smartImages";

    /**
     * Name of the JSON property that will indicate if the image should be loaded lazily.
     */
    String JSON_LAZY_ENABLED = "lazyEnabled";

    /**
     * Gets the value for the {@code src} attribute of the image.
     *
     * @return the image's URL
     */
    String getSrc();

    /**
     * @return the value for the image's {@code alt} attribute, if one was set, or {@code null}
     */
    String getAlt();

    /**
     * @return the value for the image's {@code title} attribute, if one was set, or {@code null}
     */
    String getTitle();

    /**
     * @return the image's link URL, if one was set, or {@code null}
     */
    String getLink();

    /**
     * Checks if the image should display its caption as a popup (through the <code>&lt;img&gt;</code> {@code title}
     * attribute).
     *
     * @return {@code true} if the caption should be displayed as a popup,
     * <br>{@code false} otherwise
     */
    boolean displayPopupTitle();

    /**
     * @return the file reference of the current image, if one exists, {@code null} otherwise
     */
    String getFileReference();

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
     */
    String getJson();

}
