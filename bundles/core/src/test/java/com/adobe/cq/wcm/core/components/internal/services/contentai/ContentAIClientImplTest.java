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

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ContentAIClientImplTest {

    private ContentAIClientImpl client;
    private CloseableHttpClient mockHttpClient;
    private CloseableHttpResponse mockResponse;

    @BeforeEach
    void setUp() {
        client = new ContentAIClientImpl();
        client.activate(testConfig());
        mockHttpClient = mock(CloseableHttpClient.class);
        mockResponse = mock(CloseableHttpResponse.class);
        mockTransport();
    }

    private ContentAIConfig testConfig() {
        return new ContentAIConfig() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String baseUrl() {
                return "http://test.contentai.example.com";
            }

            @Override
            public String bearerToken() {
                return "test-token";
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

    private void mockTransport() {
        HttpClientBuilderFactory mockBuilderFactory = mock(HttpClientBuilderFactory.class);
        setField(ContentAIClientImpl.class, "httpClientBuilderFactory", client, mockBuilderFactory);

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
