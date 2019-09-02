/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.services.oembed;

/**
 * A service that allows finding out providers and embedding information for URLs.
 */
public interface OEmbedClient {

    /**
     * Gets a suitable oEmbed provider for the given URL.
     *
     * @param url The URL.
     * @return The name of the oEmbed provider, as defined in configuration. <code>null</code> if no provider is found.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    String getProvider(String url);

    /**
     * Gets the embedding information from the oEmbed provider.
     *
     * @param url The URL to retrieve embedding information for.
     * @return The oEmbed response, <code>null</code> otherwise.
     *
     * @since com.adobe.cq.wcm.core.components.models.embed.oembed 1.0.0
     */
    OEmbedResponse getResponse(String url);
}
