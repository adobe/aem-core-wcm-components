/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code Carousel} Sling Model used for the {@code /apps/core/wcm/components/carousel} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.5.0
 */
@ConsumerType
public interface Carousel extends Container {

    /**
     * Name of the resource property that indicates whether to automatically transition between slides, or not.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    String PN_AUTOPLAY = "autoplay";

    /**
     * Name of the resource property that indicates the delay (in milliseconds) when automatically transitioning between slides.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    String PN_DELAY = "delay";

    /**
     * Name of the resource property that indicates whether automatic pause on hovering the carousel is disabled, or not.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.6.0
     */
    String PN_AUTOPAUSE_DISABLED = "autopauseDisabled";

    /**
     * Indicates whether the carousel should automatically transition between slides or not.
     *
     * @return {@code true} if the carousel should automatically transition slides; {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    default boolean getAutoplay() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the delay (in milliseconds) when automatically transitioning between slides.
     *
     * @return the delay (in milliseconds) when automatically transitioning between slides
     * @since com.adobe.cq.wcm.core.components.models 12.5.0
     */
    default Long getDelay() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indicates whether automatic pause on hovering the carousel is disabled, or not.
     *
     * @return {@code true} if automatic pause on hovering the carousel should be disabled; {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.6.0
     */
    default boolean getAutopauseDisabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns an accessibility label that describes the carousel.
     *
     * @return an accessibility label for the carousel
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    default String getAccessibilityLabel() {
        throw new UnsupportedOperationException();
    }
}
