/*******************************************************************************
 * Copyright 2016 Adobe Systems Incorporated
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.adobe.cq.wcm.core.components.models;

import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the Sling Model for the {@code /apps/core/wcm/components/sharing} component.
 */
@ConsumerType
public interface SocialMediaHelper {

    /**
     * Name of the resource property that will indicate which social networks are supported for social sharing.
     *
     * @see #PV_FACEBOOK
     * @see #PN_SOCIAL_MEDIA
     */
    String PN_SOCIAL_MEDIA = "socialMedia";

    /**
     * Name of the resource property that will indicate which page variant has been used for social sharing.
     */
    String PN_VARIANT_PATH = "variantPath";

    String PV_FACEBOOK = "facebook";
    String PV_PINTEREST = "pinterest";

    /**
     * @return {@code true} if Facebook sharing is enabled in page configuration, {@code false} otherwise.
     */
    boolean isFacebookEnabled();

    /**
     * @return {@code true} if Pinterest sharing is enabled in page configuration, {@code false} otherwise.
     */
    boolean isPinterestEnabled();

    /**
     * @return {@code true} if a supported social media sharing is enabled in page configuration, {@code false} otherwise.
     */
    boolean isSocialMediaEnabled();

    /**
     * @return {@code true} if Facebook sharing is enabled in page configuration
     * and the page contains the sharing component, {@code false} otherwise.
     */
    boolean hasFacebookSharing();

    /**
     * @return {@code true} if Pinterest sharing is enabled in page configuration
     * and the page contains the sharing component, {@code false} otherwise.
     */
    boolean hasPinterestSharing();

    /**
     * @return the social media metadata for the current page.
     */
    Map<String, String> getMetadata();
}
