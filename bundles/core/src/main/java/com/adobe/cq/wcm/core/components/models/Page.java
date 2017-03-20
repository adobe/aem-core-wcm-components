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

import java.util.Calendar;
import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Page} Sling Model used for the {@code /apps/core/wcm/components/page} component.
 */
@ConsumerType
public interface Page {

    /**
     * Key used for the regular favicon file.
     *
     * @see #getFavicons()
     */
    String PN_FAVICON_ICO = "faviconIco";

    /**
     * Key for the PNG-format favicon file.
     *
     * @see #getFavicons()
     */
    String PN_FAVICON_PNG = "faviconPng";

    /**
     * Key for the touch-enabled 60px square favicon file.
     *
     * @see #getFavicons()
     */
    String PN_TOUCH_ICON_60 = "touchIcon60";

    /**
     * Key for the touch-enabled 76px square favicon file.
     *
     * @see #getFavicons()
     */
    String PN_TOUCH_ICON_76 = "touchIcon76";

    /**
     * Key for the touch-enabled 120px square favicon file.
     *
     * @see #getFavicons()
     */
    String PN_TOUCH_ICON_120 = "touchIcon120";

    /**
     * Key for the touch-enabled 152px square favicon file.
     *
     * @see #getFavicons()
     */
    String PN_TOUCH_ICON_152 = "touchIcon152";

    /**
     * Expected file name for the regular favicon file.
     *
     * @see #getFavicons()
     */
    String FN_FAVICON_ICO = "favicon.ico";

    /**
     * Expected file name for the PNG-format favicon file.
     *
     * @see #getFavicons()
     */
    String FN_FAVICON_PNG = "favicon_32.png";

    /**
     * Expected file name for the touch 60px square favicon file.
     *
     * @see #getFavicons()
     */
    String FN_TOUCH_ICON_60 = "touch-icon_60.png";

    /**
     * Expected file name for the touch 76px square favicon file.
     *
     * @see #getFavicons()
     */
    String FN_TOUCH_ICON_76 = "touch-icon_76.png";

    /**
     * Expected file name for the touch 120px square favicon file.
     *
     * @see #getFavicons()
     */
    String FN_TOUCH_ICON_120 = "touch-icon_120.png";

    /**
     * Expected file name for the touch 152px square favicon file.
     *
     * @see #getFavicons()
     */
    String FN_TOUCH_ICON_152 = "touch-icon_152.png";

    /**
     * Returns the language of this page, if one has been defined. Otherwise the default {@link java.util.Locale} will be used.
     *
     * @return the language code (IETF BCP 47) for this page
     */
    String getLanguage();

    /**
     * @return {@link Calendar} representing the last modified date of this page
     */
    Calendar getLastModifiedDate();

    /**
     * Returns an array with the page's keywords.
     *
     * @return an array of keywords represented as {@link String}s; the array can be empty if no keywords have been defined for the page
     */
    String[] getKeywords();

    /**
     * Retrieves the page's design path.
     *
     * @return the design path as a {@link String}
     */
    String getDesignPath();

    /**
     * Retrieves the static design path if {@code static.css} exists in the design path.
     *
     * @return the static design path if it exists, {@code null} otherwise
     */
    String getStaticDesignPath();

    /**
     * <p>
     * Retrieves the paths to the various favicons for the website as <code>&lt;favicon_name&gt;:&lt;path&gt;</code> pairs.
     * </p>
     * <p>
     * If a file corresponding to a particular type of favicon is found under the page's design path, then the
     * &lt;favicon_name&gt;:&lt;path&gt; pair is added to the map, otherwise that type of favicon is ignored. The following list
     * defines the currently supported favicons along with their brief descriptions:</p>
     *  <ul>
     *      <li>{@link #PN_FAVICON_ICO}: The favicon.ico favicon</li>
     *      <li>{@link #PN_FAVICON_PNG}: The png version of the favicon</li>
     *      <li>{@link #PN_TOUCH_ICON_60}: The touch icon with size 60px</li>
     *      <li>{@link #PN_TOUCH_ICON_76}: The touch icon with size 76px</li>
     *      <li>{@link #PN_TOUCH_ICON_120}: The touch icon with size 120px</li>
     *      <li>{@link #PN_TOUCH_ICON_152}: The touch icon with size 152px</li>
     *  </ul>
     *
     * @return {@link Map} containing the names of the favicons and their corresponding paths
     */
    Map<String, String> getFavicons();

    /**
     * @return the page's title
     */
    String getTitle();

    /**
     * If this page is associated with a Template, then this method will return the Template's client libraries categories.
     *
     * @return an array of client libraries categories; the array can be empty if the page doesn't have an associated template or if the
     * template has no client libraries
     */
    String[] getClientLibCategories();

    /**
     * Returns the template name of the currently used template.
     *
     * @return the template name of the current template
     */
    String getTemplateName();

}
