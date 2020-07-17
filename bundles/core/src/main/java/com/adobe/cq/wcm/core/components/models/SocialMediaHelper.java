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

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the Sling Model for the {@code /apps/core/wcm/components/sharing} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface SocialMediaHelper extends Component {

    /**
     * Name of the resource property that will indicate which social networks are supported for social sharing.
     *
     * @see #PV_FACEBOOK
     * @see #PV_PINTEREST
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_SOCIAL_MEDIA = "socialMedia";

    /**
     * Name of the resource property that will indicate which page variant has been used for social sharing.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_VARIANT_PATH = "variantPath";

    /**
     * Possible value of the {@link #PN_SOCIAL_MEDIA} resource property.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PV_FACEBOOK = "facebook";

    /**
     * Name of the property that will return the Facebook App ID.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PN_FACEBOOK_APP_ID = "facebookAppId";

    /**
     * Possible value of the {@link #PN_SOCIAL_MEDIA} resource property.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.1.0
     */
    String PV_PINTEREST = "pinterest";

    /**
     * Returns {@code true} if Facebook sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if Facebook sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean isFacebookEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if Pinterest sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if Pinterest sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean isPinterestEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if a supported social media sharing is enabled in page configuration, {@code false} otherwise.
     *
     * @return {@code true} if a supported social media sharing is enabled in page configuration, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean isSocialMediaEnabled() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of the Facebook App ID if specified, null otherwise.
     *
     * @return the value of the Facebook App ID if specified, null otherwise.
     * @since com.adobe.cq.wcm.core.components.models 12.14.0; marked <code>default</code> in 12.14.0
     */
    default String getFacebookAppId() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if Facebook sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise.
     *
     * @return {@code true} if Facebook sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean hasFacebookSharing() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns {@code true} if Pinterest sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     *
     * @return {@code true} if Pinterest sharing is enabled in page configuration and the page contains the sharing component, {@code
     * false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean hasPinterestSharing() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the social media metadata for the current page.
     *
     * @return the social media metadata for the current page; the {@link Map} can be empty if there's no social media configuration
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default Map<String, String> getMetadata() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
