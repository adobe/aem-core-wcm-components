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
package com.adobe.cq.wcm.core.components.internal.services.contentai;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceQueryResult;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component(service = ContentAIClient.class)
@Designate(ocd = ContentAIConfig.class)
public class ContentAIClientImpl implements ContentAIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAIClientImpl.class);

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    private ContentAIConfig config;

    private final ObjectMapper mapper = new ObjectMapper();

    @Activate
    @Modified
    protected void activate(ContentAIConfig config) {
        this.config = config;
    }

    @Override
    public ContentSourceSearchResult search(String contentSource, String query, int limit) throws ContentAIClientException {
        ObjectNode body = mapper.createObjectNode();
        body.putObject("contentSource").put("name", contentSource);

        ObjectNode queryNode = body.putObject("query");
        queryNode.put("type", "composite");
        queryNode.put("operator", "OR");
        ArrayNode queries = queryNode.putArray("queries");

        ObjectNode vectorQuery = queries.addObject();
        vectorQuery.put("type", "vector");
        vectorQuery.put("text", query);
        ObjectNode vectorOptions = vectorQuery.putObject("options");
        vectorOptions.put("numCandidates", 10);
        vectorOptions.put("boost", 1);

        ObjectNode fulltextQuery = queries.addObject();
        fulltextQuery.put("type", "fulltext");
        fulltextQuery.put("text", query);
        fulltextQuery.putObject("options").put("boost", 1.5);

        body.putObject("queryOptions").putObject("pagination").put("limit", limit);

        JsonNode response = executeRequest("/content-sources/search", body);
        try {
            return mapper.treeToValue(response, ContentSourceSearchResult.class);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to parse Content AI search response", e);
        }
    }

    @Override
    public ContentSourceQueryResult genSearch(String contentSource, String query) throws ContentAIClientException {
        throw new UnsupportedOperationException("Implemented in Task 3");
    }

    private JsonNode executeRequest(String path, ObjectNode body) throws ContentAIClientException {
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(config.connectionTimeout())
            .setSocketTimeout(config.socketTimeout())
            .build();
        try (CloseableHttpClient httpClient = getHttpClient(requestConfig)) {
            HttpPost post = new HttpPost(config.baseUrl() + path);
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", "Bearer " + config.bearerToken());
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                if (statusCode != HttpStatus.SC_OK) {
                    LOGGER.error("Content AI request to {} failed with status {}: {}", path, statusCode, responseBody);
                    throw new ContentAIClientException("Content AI request failed with status " + statusCode, statusCode);
                }
                return mapper.readTree(responseBody);
            }
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to call Content AI at " + path, e);
        }
    }

    protected CloseableHttpClient getHttpClient(RequestConfig requestConfig) {
        if (httpClientBuilderFactory != null) {
            return httpClientBuilderFactory.newBuilder().setDefaultRequestConfig(requestConfig).build();
        }
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }
}
