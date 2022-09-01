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

import java.util.Map;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the Sling Model for the {@code /apps/core/wcm/components/sharing} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 *
 * @deprecated The Social Media Sharing component is deprecated since Core Components 2.18.0 and should not be used in new projects.
 */
@Deprecated
@ConsumerType
public interface SocialMediaHelper extends Component {

    /**
     * Name of the resource property that will indicate which social networks are supported for social sharing.
     *
     * @see #PV_FACEBOOK
     * @see #PV_PINTEREST
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    @Deprecated
    String PN_SOCIAL_MEDIA = "socialMedia";

    /**
     * Name of the resource property that will indicate which page variant has been used for social sharing.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    @Deprecated
    String PN_VARIANT_PATH = "variantPath";

    /**
     * Possible value of the {@link #PN_SOCIAL_MEDIA} resource property.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    @Deprecated
    String PV_FACEBOOK = "facebook";

    /**
     * Name of the property that will return the Facebook App ID.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    @Deprecated
    String PN_FACEBOOK_APP_ID = "facebookAppId";

    /**
     * Possible value of the {@link #PN_SOCIAL_MEDIA} resource property.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    @Deprecated
    String PV_PINTEREST = "pinterest";

    /**
     * Returns {@code true} if Facebook sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if Facebook sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default boolean isFacebookEnabled() {
        return false;
    }

    /**
     * Returns {@code true} if Pinterest sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if Pinterest sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default boolean isPinterestEnabled() {
        return false;
    }

    /**
     * Returns {@code true} if a supported social media sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if a supported social media sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default boolean isSocialMediaEnabled() {
        return false;
    }

    /**
     * Returns the value of the Facebook App ID if specified, null otherwise.
     *
     * @return the value of the Facebook App ID if specified, null otherwise.
     * @since com.adobe.cq.wcm.core.components.models 12.14.0; marked <code>default</code> in 12.14.0
     */
    @Deprecated
    default String getFacebookAppId() {
        return null;
    }

    /**
     * Returns {@code true} if Facebook sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise.
     *
     * @return {@code true} if Facebook sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default boolean hasFacebookSharing() {
        return false;
    }

    /**
     * Returns {@code true} if Pinterest sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     *
     * @return {@code true} if Pinterest sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default boolean hasPinterestSharing() {
        return false;
    }

    /**
     * Returns the social media metadata for the current page.
     *
     * @return the social media metadata for the current page; the {@link Map} can be empty if there's no social media configuration
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    @Deprecated
    default Map<String, String> getMetadata() {
        return null;
    }

}
