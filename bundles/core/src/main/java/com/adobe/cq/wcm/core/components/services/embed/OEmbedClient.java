/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.components.services.embed;

/**
 * A service that allows finding providers and embedding information for URLs.
 *
 * @since com.adobe.cq.wcm.core.components.services.embed 1.0.0
 */
public interface OEmbedClient {

    /**
     * Gets a suitable oEmbed provider for the given URL.
     *
     * @param url The URL
     * @return The name of the oEmbed provider, as defined in configuration. {@code null} if no provider is found
     *
     * @since com.adobe.cq.wcm.core.components.services.embed 1.0.0
     */
    String getProvider(String url);

    /**
     * Gets the embedding information from the oEmbed provider.
     *
     * @param url The URL to retrieve embedding information for
     * @return The oEmbed response, {@code null} otherwise
     *
     * @since com.adobe.cq.wcm.core.components.services.embed 1.0.0
     */
    OEmbedResponse getResponse(String url);

    /**
     * Determines whether the provider response HTML is allowed to be displayed in an unsafe context.
     *
     * @param url The URL to retrieve the unsafe context flag for
     * @return {@code true} if the provider response HTML is allowed to be displayed in an unsafe context,
     * {@code false} otherwise
     *
     * @since com.adobe.cq.wcm.core.components.services.embed 1.0.0
     */
    boolean isUnsafeContext(String url);
}
