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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ContentAIClientImplTest {

    private static final String TEST_BASE_URL = "http://test.contentai.example.com";
    private static final String TEST_API_KEY = "test-key";

    private ContentAIClientImpl client;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        // Default client: uses the dev baseUrlOverride so the core request tests need no environment.
        client = new ContentAIClientImpl();
        client.activate(config(TEST_API_KEY, TEST_BASE_URL));
        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(CloseableHttpResponse.class);
        attachTransport(client);
    }

    private ContentAIConfig config(String apiKey, String baseUrlOverride) {
        return new ContentAIConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String apiKey() {
                return apiKey;
            }

            @Override
            public String baseUrlOverride() {
                return baseUrlOverride;
            }

            @Override
            public String defaultContentSource() {
                return "";
            }

            @Override
            public int connectionTimeout() {
                return 1000;
            }

            @Override
            public int socketTimeout() {
                return 1000;
            }
        };
    }

    private void attachTransport(ContentAIClientImpl target) {
        HttpClientBuilderFactory mockBuilderFactory = mock(HttpClientBuilderFactory.class);
        setField(ContentAIClientImpl.class, "httpClientBuilderFactory", target, mockBuilderFactory);

        HttpClientBuilder mockBuilder = mock(HttpClientBuilder.class);
        when(mockBuilderFactory.newBuilder()).thenReturn(mockBuilder);
        when(mockBuilder.setDefaultRequestConfig(any(RequestConfig.class))).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockHttpClient);
    }

    private void respondWith(int statusCode, String jsonBody) throws IOException {
        StatusLine statusLine = new BasicStatusLine(new ProtocolVersion("HTTP", 1, 1), statusCode, "");
        when(mockResponse.getStatusLine()).thenReturn(statusLine);
        HttpEntity mockEntity = mock(HttpEntity.class);
        when(mockEntity.getContent()).thenReturn(new ByteArrayInputStream(jsonBody.getBytes(StandardCharsets.UTF_8)));
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);
    }

    private HttpUriRequest captureExecutedRequest() throws IOException {
        ArgumentCaptor<HttpUriRequest> captor = ArgumentCaptor.forClass(HttpUriRequest.class);
        verify(mockHttpClient).execute(captor.capture());
        return captor.getValue();
    }

    /**
     * A client whose environment lookups come from a supplied map, so bucket derivation can be tested without a
     * real AEM Cloud Service environment.
     */
    private ContentAIClientImpl envClient(ContentAIConfig cfg, Map<String, String> env) {
        // Default: no run modes → treated as publish tier (the component's primary tier).
        return envClient(cfg, env, Collections.emptySet());
    }

    private ContentAIClientImpl envClient(ContentAIConfig cfg, Map<String, String> env, Set<String> runModes) {
        ContentAIClientImpl envClient = new ContentAIClientImpl() {
            @Override
            protected String getEnv(String name) {
                return env.get(name);
            }

            @Override
            protected Set<String> getRunModes() {
                return runModes;
            }
        };
        envClient.activate(cfg);
        attachTransport(envClient);
        return envClient;
    }

    @Test
    void listContentSourcesReturnsParsedItems() throws Exception {
        respondWith(200, "{\"items\":[{\"name\":\"aem-live\",\"type\":\"ACQUISITION\",\"config\":{\"access\":{\"public\":true}}}]}");

        com.adobe.cq.wcm.core.components.services.contentai.ContentSourceListResult result =
            client.listContentSources();

        assertEquals(1, result.getItems().size());
        assertEquals("aem-live", result.getItems().get(0).getName());
        assertTrue(result.getItems().get(0).isPublicAccess());
    }

    @Test
    void searchIncludesContentSourceType() throws Exception {
        respondWith(200, "{\"totalResults\":0,\"results\":[]}");

        client.search("my-content-source", "ACQUISITION", "electric cars", 10);

        HttpPost sent = (HttpPost) captureExecutedRequest();
        String body = new String(sent.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(body.contains("\"type\":\"ACQUISITION\""));
    }

    @Test
    void searchReturnsParsedResult() throws Exception {
        respondWith(200, "{\"totalResults\":1,\"results\":[{\"id\":\"doc_1\",\"score\":0.75,\"data\":{\"title\":\"Electric Cars\"}}],\"cursor\":\"abc\"}");

        ContentSourceSearchResult result = client.search("my-content-source", "electric cars", 10);

        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getResults().size());
        assertEquals("doc_1", result.getResults().get(0).getId());
        assertEquals("abc", result.getCursor());
    }

    @Test
    void searchThrowsOnNon200Response() throws IOException {
        respondWith(401, "{\"error\":\"Unauthorized\"}");

        ContentAIClientException exception = assertThrows(ContentAIClientException.class,
            () -> client.search("my-content-source", "electric cars", 10));
        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void genSearchReturnsParsedResult() throws Exception {
        respondWith(200, "{\"query\":\"electric cars\",\"result\":\"Electric cars are efficient.\",\"hits\":[{\"id\":\"doc_1\"},{\"id\":\"doc_2\"}]}");

        com.adobe.cq.wcm.core.components.services.contentai.ContentSourceQueryResult result =
            client.genSearch("my-content-source", "electric cars");

        assertEquals("electric cars", result.getQuery());
        assertEquals("Electric cars are efficient.", result.getResult());
        assertEquals(2, result.getHits().size());
        assertEquals("doc_1", result.getHits().get(0).getId());
    }

    @Test
    void genSearchThrowsOnServerError() throws IOException {
        respondWith(503, "{\"error\":\"Service Unavailable\"}");

        ContentAIClientException exception = assertThrows(ContentAIClientException.class,
            () -> client.genSearch("my-content-source", "electric cars"));
        assertEquals(503, exception.getStatusCode());
    }

    @Test
    void requestCarriesApiKeyHeaderAndNoBearer() throws Exception {
        respondWith(200, "{\"totalResults\":0,\"results\":[]}");

        client.search("my-content-source", "electric cars", 10);

        HttpUriRequest sent = captureExecutedRequest();
        Header apiKey = sent.getFirstHeader("X-Api-Key");
        assertEquals(TEST_API_KEY, apiKey == null ? null : apiKey.getValue());
        // Anonymous public access must NOT send an Authorization bearer header.
        assertNull(sent.getFirstHeader("Authorization"));
    }

    @Test
    void missingApiKeyFailsCleanly() {
        ContentAIClientImpl noKey = new ContentAIClientImpl();
        noKey.activate(config("", TEST_BASE_URL));
        attachTransport(noKey);

        ContentAIClientException exception = assertThrows(ContentAIClientException.class,
            () -> noKey.search("my-content-source", "electric cars", 10));
        assertEquals(0, exception.getStatusCode());
    }

    @Test
    void baseUrlDerivedFromProgramAndEnvIds() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_PROGRAM_ID", "12345");
        env.put("AEM_ENV_ID", "67890");
        ContentAIClientImpl envClient = envClient(config(TEST_API_KEY, ""), env);
        respondWith(200, "{\"totalResults\":0,\"results\":[]}");

        envClient.search("my-content-source", "electric cars", 10);

        HttpPost sent = (HttpPost) captureExecutedRequest();
        assertEquals("https://publish-p12345-e67890.adobeaemcloud.com"
            + "/adobe/experimental/aemcontentai-expires-20261231/contentAI/content-sources/search",
            sent.getURI().toString());
    }

    @Test
    void baseUrlDerivedFromAemServiceFallback() throws Exception {
        // No AEM_PROGRAM_ID/AEM_ENV_ID; bucket parsed from AEM_SERVICE=cm-p{PID}-e{EID}.
        Map<String, String> env = new HashMap<>();
        env.put("AEM_SERVICE", "cm-p12345-e67890");
        ContentAIClientImpl envClient = envClient(config(TEST_API_KEY, ""), env);
        respondWith(200, "{\"totalResults\":0,\"results\":[]}");

        envClient.search("my-content-source", "electric cars", 10);

        HttpPost sent = (HttpPost) captureExecutedRequest();
        assertTrue(sent.getURI().toString().startsWith(
            "https://publish-p12345-e67890.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI"),
            "Expected bucket parsed from AEM_SERVICE, got " + sent.getURI());
    }

    @Test
    void baseUrlUsesAuthorPrefixOnAuthorTier() throws Exception {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_PROGRAM_ID", "12345");
        env.put("AEM_ENV_ID", "67890");
        ContentAIClientImpl envClient = envClient(config(TEST_API_KEY, ""), env, Collections.singleton("author"));
        respondWith(200, "{\"totalResults\":0,\"results\":[]}");

        envClient.search("my-content-source", "electric cars", 10);

        HttpPost sent = (HttpPost) captureExecutedRequest();
        assertTrue(sent.getURI().toString().startsWith(
            "https://author-p12345-e67890.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI"),
            "Expected author- host on author tier, got " + sent.getURI());
    }

    @Test
    void baseUrlDerivationFailsCleanlyWhenNoEnvAndNoOverride() {
        ContentAIClientImpl envClient = envClient(config(TEST_API_KEY, ""), new HashMap<>());

        ContentAIClientException exception = assertThrows(ContentAIClientException.class,
            () -> envClient.search("my-content-source", "electric cars", 10));
        assertEquals(0, exception.getStatusCode());
    }

    public static void setField(@NotNull final Class<?> clazz,
                                 @NotNull final String fieldName,
                                 @Nullable final Object target,
                                 @Nullable final Object value) {
        final Field f = FieldUtils.getField(clazz, fieldName, true);
        FieldUtils.removeFinalModifier(f);
        try {
            f.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
