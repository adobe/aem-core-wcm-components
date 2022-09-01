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

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Defines the {@code Page} Sling Model used for the {@code /apps/core/wcm/components/page} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface Page extends ContainerExporter, Component {

    /**
     * Key used for the regular favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_FAVICON_ICO = "faviconIco";

    /**
     * Key for the PNG-format favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_FAVICON_PNG = "faviconPng";

    /**
     * Key for the touch-enabled 60px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_TOUCH_ICON_60 = "touchIcon60";

    /**
     * Key for the touch-enabled 76px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_TOUCH_ICON_76 = "touchIcon76";

    /**
     * Key for the touch-enabled 120px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_TOUCH_ICON_120 = "touchIcon120";

    /**
     * Key for the touch-enabled 152px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_TOUCH_ICON_152 = "touchIcon152";

    /**
     * Name of the configuration policy property that will store the category of the client library from which web application resources
     * will be served.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_APP_RESOURCES_CLIENTLIB = "appResourcesClientlib";

    /**
     * Expected file name for the regular favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_FAVICON_ICO = "favicon.ico";

    /**
     * Expected file name for the PNG-format favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_FAVICON_PNG = "favicon_32.png";

    /**
     * Expected file name for the touch 60px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_TOUCH_ICON_60 = "touch-icon_60.png";

    /**
     * Expected file name for the touch 76px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_TOUCH_ICON_76 = "touch-icon_76.png";

    /**
     * Expected file name for the touch 120px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_TOUCH_ICON_120 = "touch-icon_120.png";

    /**
     * Expected file name for the touch 152px square favicon file.
     *
     * @see #getFavicons()
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String FN_TOUCH_ICON_152 = "touch-icon_152.png";

    /**
     * Name of the node holding the properties of the featured image of the page.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.21.0
     */
    String NN_PAGE_FEATURED_IMAGE = "cq:featuredimage";

    /**
     * Returns the language of this page, if one has been defined. Otherwise the default {@link java.util.Locale} will be used.
     *
     * @return the language code (IETF BCP 47) for this page
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getLanguage() {
        return null;
    }

    /**
     * Returns the last modified date of this page.
     *
     * @return {@link Calendar} representing the last modified date of this page
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default Calendar getLastModifiedDate() {
        return null;
    }

    /**
     * Returns an array with the page's keywords.
     *
     * @return an array of keywords represented as {@link String}s; the array can be empty if no keywords have been defined for the page
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @JsonIgnore
    default String[] getKeywords() {
        return null;
    }

    /**
     * Retrieves the page's design path.
     *
     * @return the design path as a {@link String}
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getDesignPath() {
        return null;
    }

    /**
     * Retrieves the static design path if {@code static.css} exists in the design path.
     *
     * @return the static design path if it exists, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getStaticDesignPath() {
        return null;
    }

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
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     * @deprecated since 12.2.0
     */
    @Deprecated
    @JsonIgnore
    default Map<String, String> getFavicons() {
        return null;
    }

    /**
     * Returns the title of this page.
     *
     * @return the page's title
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getTitle() {
        return null;
    }

    /**
     * Returns the description of this page.
     *
     * @return the page's description
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.23.0
     */
    default String getDescription() {
        return null;
    }

    /**
     * Returns the brand slug of this page.
     *
     * @return the page's brandSlug
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.18.0
     */
    default String getBrandSlug() {
    	return null;
    }

    /**
     * If this page is associated with a Template, then this method will return the Template's client library categories
     * to be included in the page as defined by the user in the policy.
     *
     * @return an array of client library categories to be included; the array can be empty if the page doesn't have an
     * associated template or if the template has no client libraries defined.
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @JsonIgnore
    default String[] getClientLibCategories() {
        return null;
    }

    /**
     * If this page is associated with a Template, then this method will return the JavaScript client library categories
     * which are intended specifically to be loaded at the page body end (JavaScript only), as defined by the user in the policy.
     *
     * @return an array of JavaScript client library categories which are expected to be loaded at the page body end;
     * the array can be empty if the page doesn't have an associated template or if the template has no body client libraries defined.
     * @since com.adobe.cq.wcm.core.components.models 12.5.0;
     */
    @JsonIgnore
    default String[] getClientLibCategoriesJsBody() {
        return null;
    }

    /**
     * If this page is associated with a Template, then this method will return the JavaScript client library categories
     * which are intended specifically to be loaded up front in the page head (JavaScript only), as defined by the user in the policy.
     *
     * @return an array of JavaScript client library categories which are expected to be loaded in the page head; the array can be empty
     * if the page doesn't have an associated template or if the template has no head client libraries defined.
     * @since com.adobe.cq.wcm.core.components.models 12.5.0;
     */
    @JsonIgnore
    default String[] getClientLibCategoriesJsHead() {
        return null;
    }

    /**
     * Returns the template name of the currently used template.
     *
     * @return the template name of the current template
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getTemplateName() {
        return null;
    }

    /**
     * Returns the root path of the application's web resources (e.g. favicons, application manifest, etc.).
     *
     * @return resources path; can return {@code null} if no such resources were defined
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default String getAppResourcesPath() {
        return null;
    }

    /**
     * Returns the CSS classes defined for the page
     *
     * @return {@link String} containing the CSS classes defined for the page, if one class exists, {@code null} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default String getCssClassNames() {
        return null;
    }

    /**
     * Returns the target page if this page is redirecting to another page.
     *
     * @return {@link NavigationItem} of redirect target
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nullable
    default NavigationItem getRedirectTarget() {
        return null;
    }

    /**
     * Checks if there's support for cloudconfig script tags so they can be added to page header/footer
     *
     * @return {@code true} if there's support for cloudconfig script tags
     * @since com.adobe.cq.wcm.core.components.models 12.3.0
     */
    default boolean hasCloudconfigSupport() {
        return false;
    }

    /**
     * Returns a set of resource types for components included in the page.
     *
     * @return Resource types for components included in the page
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    @NotNull
    default Set<String> getComponentsResourceTypes() {
        return Collections.emptySet();
    }

    /**
     * @see ContainerExporter#getExportedItemsOrder()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default String[] getExportedItemsOrder() {
        return new String[]{};
    }

    /**
     * @see ContainerExporter#getExportedItems()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default Map<String, ? extends ComponentExporter> getExportedItems() {
        return Collections.emptyMap();
    }

    /**
     * @see ContainerExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }

    /**
     * Returns the selector for the main content element of the page (used by the "skip to main content" accessibility feature)
     *
     * @return selector for the main content element
     * @since com.adobe.cq.wcm.core.components.models 12.4.0
     */
    @Nullable
    default String getMainContentSelector() {
        return null;
    }

    /**
     * Returns a list of HTML items that should be included in the page
     *
     * @return list of page items
     * @since com.adobe.cq.wcm.core.components.models 12.16.0
     */
    @Nullable
    default List<HtmlPageItem> getHtmlPageItems() {
        return null;
    }

    /**
     * Returns the canonical link of the page.
     * <p>
     * This should be an absolute url.
     *
     * @return the canonical url of the page
     * @since om.adobe.cq.wcm.core.components.models 12.22.0
     */
    @Nullable
    @JsonIgnore
    default String getCanonicalLink() { return null; }

    /**
     * Returns a {@link Map} of alternate languages of the page.
     * <p>
     * The {@link Map} maps each alternate language to an absolute url to the page in this language, if it exists.
     *
     * @return a {@link Map} of alternate languages and links to the page in this language
     * @since om.adobe.cq.wcm.core.components.models 12.22.0
     */
    @NotNull
    @JsonIgnore
    default Map<Locale, String> getAlternateLanguageLinks() {
        return Collections.emptyMap();
    }

    /**
     * Returns a list of robots meta tags configured for the page.
     * <p>
     * The list is cleared from duplicates and conflicting combinations.
     *
     * @return a list of robots meta tags
     * @since om.adobe.cq.wcm.core.components.models 12.22.0
     */
    @NotNull
    @JsonIgnore
    default List<String> getRobotsTags() {
        return Collections.emptyList();
    }

    /**
     * Checks if the Javascript libraries should be loaded asynchronously.
     *
     * @return {@code true} if the Javascript libraries should be loaded asynchronously
     * @since om.adobe.cq.wcm.core.components.models 12.24.0
     */
    default boolean isClientlibsAsync() {return false;}

    /**
     * Checks if the data layer client library should be included.
     *
     * {@code true} if the data layer client library should be included.
     * @since om.adobe.cq.wcm.core.components.models 12.24.0
     */
    default boolean isDataLayerClientlibIncluded() {return true;}

}
