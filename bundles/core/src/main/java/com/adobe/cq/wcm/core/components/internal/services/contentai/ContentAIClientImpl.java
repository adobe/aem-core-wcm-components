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
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListResult;
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

    /**
     * The experimental, expiry-dated Content AI API path appended to the environment's own host.
     */
    private static final String CONTENT_AI_PATH = "/adobe/experimental/aemcontentai-expires-20261231/contentAI";

    /**
     * AEM as a Cloud Service environment variables (sourced from pod annotations aemProgramId/aemEnvId/aemService)
     * used to derive the instance's own Content AI bucket. NOTE: {@code AEM_DOMAIN_PUBLISH} is deliberately NOT used —
     * on AEM CS it holds the CDN/customer FQDN, not the {@code {bucket}.adobeaemcloud.com} host Content AI is served on.
     */
    private static final String ENV_PROGRAM_ID = "AEM_PROGRAM_ID";
    private static final String ENV_ENV_ID = "AEM_ENV_ID";
    private static final String ENV_SERVICE = "AEM_SERVICE";

    @Reference
    private HttpClientBuilderFactory httpClientBuilderFactory;

    @Reference
    private SlingSettingsService slingSettings;

    private ContentAIConfig config;

    private final ObjectMapper mapper = new ObjectMapper();

    @Activate
    @Modified
    protected void activate(ContentAIConfig config) {
        this.config = config;
    }

    @Override
    public ContentSourceListResult listContentSources() throws ContentAIClientException {
        JsonNode response = executeGet("/content-sources");
        try {
            return mapper.treeToValue(response, ContentSourceListResult.class);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to parse Content AI content-sources response", e);
        }
    }

    @Override
    public ContentSourceSearchResult search(String contentSource, String contentSourceType, String query, int limit)
        throws ContentAIClientException {
        return search(contentSource, contentSourceType, query, limit, null);
    }

    @Override
    public ContentSourceSearchResult search(String contentSource, String contentSourceType, String query, int limit,
        String cursor) throws ContentAIClientException {
        ObjectNode body = mapper.createObjectNode();
        ObjectNode contentSourceNode = body.putObject("contentSource");
        contentSourceNode.put("name", contentSource);
        if (StringUtils.isNotBlank(contentSourceType)) {
            contentSourceNode.put("type", contentSourceType);
        }

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

        ObjectNode pagination = body.putObject("queryOptions").putObject("pagination");
        pagination.put("limit", limit);
        if (StringUtils.isNotBlank(cursor)) {
            pagination.put("cursor", cursor);
        }

        JsonNode response = executeRequest("/content-sources/search", body);
        try {
            return mapper.treeToValue(response, ContentSourceSearchResult.class);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to parse Content AI search response", e);
        }
    }

    @Override
    public ContentSourceQueryResult genSearch(String contentSource, String contentSourceType, String query)
        throws ContentAIClientException {
        ObjectNode body = mapper.createObjectNode();
        body.put("query", query);
        ObjectNode contentSourceNode = body.putObject("contentSource");
        contentSourceNode.put("name", contentSource);
        if (StringUtils.isNotBlank(contentSourceType)) {
            contentSourceNode.put("type", contentSourceType);
        }

        JsonNode response = executeRequest("/content-sources/gensearch", body);
        try {
            return mapper.treeToValue(response, ContentSourceQueryResult.class);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to parse Content AI gensearch response", e);
        }
    }

    private JsonNode executeGet(String path) throws ContentAIClientException {
        String apiKey = config.apiKey();
        if (StringUtils.isBlank(apiKey)) {
            throw new ContentAIClientException("Content AI API key (X-Api-Key) is not configured", 0);
        }
        String url = resolveBaseUrl() + path;
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(config.connectionTimeout())
            .setSocketTimeout(config.socketTimeout())
            .build();
        try (CloseableHttpClient httpClient = getHttpClient(requestConfig)) {
            HttpGet get = new HttpGet(url);
            get.setHeader("Accept", "application/json");
            get.setHeader("X-Api-Key", apiKey);
            return executeAndParse(httpClient, get, path);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to call Content AI at " + path, e);
        }
    }

    private JsonNode executeRequest(String path, ObjectNode body) throws ContentAIClientException {
        String apiKey = config.apiKey();
        if (StringUtils.isBlank(apiKey)) {
            throw new ContentAIClientException("Content AI API key (X-Api-Key) is not configured", 0);
        }
        String url = resolveBaseUrl() + path;
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(config.connectionTimeout())
            .setSocketTimeout(config.socketTimeout())
            .build();
        try (CloseableHttpClient httpClient = getHttpClient(requestConfig)) {
            HttpPost post = new HttpPost(url);
            post.setHeader("Content-Type", "application/json");
            // Anonymous, public-index access uses X-Api-Key (a Developer Console client ID), never a bearer token.
            post.setHeader("X-Api-Key", apiKey);
            post.setEntity(new StringEntity(mapper.writeValueAsString(body), StandardCharsets.UTF_8));

            return executeAndParse(httpClient, post, path);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to call Content AI at " + path, e);
        }
    }

    /**
     * Resolves the Content AI base URL from the running AEM as a Cloud Service environment, so the component always
     * targets its own environment's bucket rather than a hand-configured value. Falls back to the dev-only
     * {@code baseUrlOverride} config when the CS environment variables are absent (local/non-CS development).
     *
     * @return the base URL (without a trailing slash), up to and including the Content AI path
     * @throws ContentAIClientException if no override is set and the environment variables are not present
     */
    protected String resolveBaseUrl() throws ContentAIClientException {
        String override = config.baseUrlOverride();
        if (StringUtils.isNotBlank(override)) {
            return stripTrailingSlash(override.trim());
        }
        // The Content AI host is {tier}-p{PID}-e{EID}.adobeaemcloud.com (e.g. author-p12345-e67890...). Derive the
        // bucket (p{PID}-e{EID}) from the environment, and the tier from the instance's run modes — a public-site
        // component runs on publish at request time but may also render on author (e.g. preview).
        String bucket = deriveBucket();
        if (StringUtils.isBlank(bucket)) {
            throw new ContentAIClientException("Content AI base URL could not be derived: no baseUrlOverride is set "
                + "and the AEM_PROGRAM_ID+AEM_ENV_ID (or AEM_SERVICE) environment variables are absent", 0);
        }
        String tier = isAuthorTier() ? "author" : "publish";
        return "https://" + tier + "-" + bucket + ".adobeaemcloud.com" + CONTENT_AI_PATH;
    }

    /**
     * Derives the environment bucket ({@code p{PID}-e{EID}}) from AEM CS environment variables: preferring
     * {@code AEM_PROGRAM_ID}+{@code AEM_ENV_ID}, falling back to parsing {@code AEM_SERVICE} ({@code cm-p{PID}-e{EID}}).
     *
     * @return the bucket without a tier prefix, or {@code null} if it cannot be derived
     */
    private String deriveBucket() {
        String programId = getEnv(ENV_PROGRAM_ID);
        String envId = getEnv(ENV_ENV_ID);
        if (StringUtils.isNotBlank(programId) && StringUtils.isNotBlank(envId)) {
            return "p" + programId + "-e" + envId;
        }
        String service = getEnv(ENV_SERVICE);
        if (StringUtils.isNotBlank(service) && service.startsWith("cm-")) {
            return service.substring("cm-".length());
        }
        return null;
    }

    private static String stripTrailingSlash(String value) {
        if (value.endsWith("/")) {
            return value.substring(0, value.length() - 1);
        }
        return value;
    }

    /**
     * @return {@code true} if this instance runs on the author tier (author run mode present and publish absent);
     *         {@code false} otherwise — ambiguous/unknown defaults to publish, the primary tier for this
     *         public-site component.
     */
    private boolean isAuthorTier() {
        Set<String> runModes = getRunModes();
        return runModes.contains("author") && !runModes.contains("publish");
    }

    /**
     * Reads an environment variable. Package-visible seam so tests can supply values without a real CS environment.
     *
     * @param name the environment variable name
     * @return the value, or {@code null} if unset
     */
    protected String getEnv(String name) {
        return System.getenv(name);
    }

    /**
     * @return the instance's Sling run modes. Seam so tests can control the tier without an OSGi container.
     */
    protected Set<String> getRunModes() {
        return slingSettings != null ? slingSettings.getRunModes() : Collections.emptySet();
    }

    /**
     * Package-visible for unit tests.
     */
    JsonNode executeAndParse(CloseableHttpClient httpClient, HttpUriRequest request, String path)
        throws IOException, ContentAIClientException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            if (statusCode != HttpStatus.SC_OK) {
                LOGGER.error("Content AI request to {} failed with status {}: {}", path, statusCode, responseBody);
                throw new ContentAIClientException("Content AI request failed with status " + statusCode, statusCode);
            }
            return mapper.readTree(responseBody);
        }
    }

    protected CloseableHttpClient getHttpClient(RequestConfig requestConfig) {
        if (httpClientBuilderFactory != null) {
            return httpClientBuilderFactory.newBuilder().setDefaultRequestConfig(requestConfig).build();
        }
        return HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }
}
