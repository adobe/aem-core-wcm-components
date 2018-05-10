/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.models;

import java.util.List;

import javax.annotation.Nonnull;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;

/**
 * Defines the {@code Teaser} Sling Model for the {@code /apps/core/wcm/components/teaser} component.
 *
 * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
 */
public interface Teaser extends ComponentExporter {

    /**
     * Name of the property that stores if the teaser has Call-to-Action elements
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_WITH_CTA = "withCTA";

    /**
     * Name of the parent node where the Call-to-Action elements are stored
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String NN_CTAS = "ctas";

    /**
     * Name of the property that stores the Call-to-Action link
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_CTA_LINK = "link";

    /**
     * Name of the property that stores the Call-to-Action text
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_CTA_TEXT = "text";

    /**
     * Name of the property that stores if the Call-to-Action is disabled
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_DISABLE_CTA = "disableCTA";

    /**
     * Name of the property that will store if the image link should be hidden.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_HIDE_IMAGE_LINK = "hideImageLink";

    /**
     * Name of the property that will store if the title should be hidden.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_HIDE_TITLE = "hideTitle";

    /**
     * Name of the property that will store if the title link should be hidden.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_HIDE_TITLE_LINK = "hideTitleLink";

    /**
     * Name of the property that will store if the title value should be taken from the linked page.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_TITLE_VALUE_FROM_PAGE = "titleValueFromPage";

    /**
     * Name of the property that will store if the description should be hidden.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_HIDE_DESCRIPTION = "hideDescription";

    /**
     * Name of the property that will store if the description value should be taken from the linked page.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_DESCRIPTION_VALUE_FROM_PAGE = "descriptionValueFromPage";

    /**
     * Name of the configuration policy property that will store the value for this title's HTML element type.
     *
     * @see #getTitleType()
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    String PN_TITLE_TYPE = "titleType";

    /**
     * Checks if teaser has Call-to-Action elements
     *
     * @return {@code true} if teaser has CTAs, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default boolean isWithCTA() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the list of Call-to-Action elements
     *
     * @return the list of CTAs
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default List<ListItem> getCTAs() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the URL to which this teaser links, if one was defined.
     *
     * @return the URL to which teaser links or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default String getLinkURL() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the image resource for this teaser.
     *
     * @return the image resource for this teaser or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default Resource getImageResource() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if link should be hidden on the image.
     *
     * @return {@code true} if link should be hidden on the image, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default boolean isHideImageLink() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this teaser's title, if one was defined.
     *
     * @return the teaser's title or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if link should be hidden on the title.
     *
     * @return {@code true} if link should be hidden on the title, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default boolean isHideTitleLink() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns this teaser's description, if one was defined.
     *
     * @return the teaser's description or {@code null}
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the HTML element type (h1-h6) used for the markup.
     *
     * @return the element type
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    default String getTitleType() {
        throw new UnsupportedOperationException();
    }

    /**
     * @since com.adobe.cq.wcm.core.components.sandbox.models 1.0.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
