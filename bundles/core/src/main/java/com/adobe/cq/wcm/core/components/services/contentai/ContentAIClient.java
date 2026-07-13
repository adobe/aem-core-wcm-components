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

    String DEFAULT_CONTENT_SOURCE_TYPE = "ACQUISITION";

    /**
     * Lists available Content AI content sources for the configured environment.
     *
     * @return parsed list response from {@code GET /content-sources}
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceListResult listContentSources() throws ContentAIClientException;

    /**
     * Executes a hybrid (vector + fulltext) search against the given content source.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param contentSourceType the Content AI content source type
     * @param query the user's search text
     * @param limit the maximum number of results to return
     * @return the search result
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceSearchResult search(String contentSource, String contentSourceType, String query, int limit)
        throws ContentAIClientException;

    /**
     * Executes a hybrid search with optional cursor-based pagination.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param contentSourceType the Content AI content source type
     * @param query the user's search text
     * @param limit the maximum number of results to return per page
     * @param cursor optional pagination cursor from a previous response
     * @return the search result
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceSearchResult search(String contentSource, String contentSourceType, String query, int limit,
        String cursor) throws ContentAIClientException;

    /**
     * Executes a hybrid search using {@link #DEFAULT_CONTENT_SOURCE_TYPE}.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param query the user's search text
     * @param limit the maximum number of results to return
     * @return the search result
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    default ContentSourceSearchResult search(String contentSource, String query, int limit) throws ContentAIClientException {
        return search(contentSource, DEFAULT_CONTENT_SOURCE_TYPE, query, limit);
    }

    /**
     * Executes a hybrid search using {@link #DEFAULT_CONTENT_SOURCE_TYPE}.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param query the user's search text
     * @param limit the maximum number of results to return per page
     * @param cursor optional pagination cursor from a previous response
     * @return the search result
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    default ContentSourceSearchResult search(String contentSource, String query, int limit, String cursor)
        throws ContentAIClientException {
        return search(contentSource, DEFAULT_CONTENT_SOURCE_TYPE, query, limit, cursor);
    }

    /**
     * Executes a blocking generative (RAG) search against the given content source.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param contentSourceType the Content AI content source type
     * @param query the user's natural-language query
     * @return the generative search result, including the generated answer and cited hits
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    ContentSourceQueryResult genSearch(String contentSource, String contentSourceType, String query)
        throws ContentAIClientException;

    /**
     * Executes generative search using {@link #DEFAULT_CONTENT_SOURCE_TYPE}.
     *
     * @param contentSource the name of the Content AI content source to search
     * @param query the user's natural-language query
     * @return the generative search result
     * @throws ContentAIClientException if the call to Content AI fails
     * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
     */
    default ContentSourceQueryResult genSearch(String contentSource, String query) throws ContentAIClientException {
        return genSearch(contentSource, DEFAULT_CONTENT_SOURCE_TYPE, query);
    }
}
