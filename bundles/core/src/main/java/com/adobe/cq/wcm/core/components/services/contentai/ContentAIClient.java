/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.services.contentai;

/**
 * A service that allows querying Adobe Content AI's AI Search and Generative Search APIs
 * for a given content source.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
public interface ContentAIClient {

    /**
     * Executes a hybrid (vector + fulltext) search against the given content source.
     *
     * @param contentSource The name of the Content AI content source to search.
     * @param query The user's search text.
     * @param limit The maximum number of results to return.
     * @return The search result.
     * @throws ContentAIClientException if the call to Content AI fails.
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceSearchResult search(String contentSource, String query, int limit) throws ContentAIClientException;

    /**
     * Executes a blocking generative (RAG) search against the given content source.
     *
     * @param contentSource The name of the Content AI content source to search.
     * @param query The user's natural-language query.
     * @return The generative search result, including the generated answer and cited hits.
     * @throws ContentAIClientException if the call to Content AI fails.
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceQueryResult genSearch(String contentSource, String query) throws ContentAIClientException;
}
