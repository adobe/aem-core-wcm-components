# ContentAI Supported Search Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a new, self-contained AEM Core WCM core component, "ContentAI Supported Search", that shows a generative-AI summary (from Content AI's `gensearch` API, toggleable, default on) above a results list (from Content AI's `search` API, always shown) — with no dependency on Quick Search or Elasticsearch-direct calls.

**Architecture:** Two Content AI API calls, both server-side proxied through new thin Sling servlets bound to the component's own resource type, both delegating to a single new OSGi service (`ContentAIClient`) that does the actual outbound HTTP via `HttpClientBuilderFactory` — mirroring the existing Embed component's `OEmbedClient`/`OEmbedClientImpl` pattern already in this codebase. A new Sling Model exposes the component's dialog-configured properties. Client-side JS always fetches results; fetches the generative summary in parallel only when the toggle is on.

**Tech Stack:** Java 11, Sling Models, OSGi Declarative Services (`@Component`/`@Designate`), Apache HttpComponents 4.x (`org.apache.http.*`, via `HttpClientBuilderFactory`), Jackson (`ObjectMapper`/`ObjectNode`), JUnit 5 + Mockito, HTL, vanilla JS (matching the existing Search component's clientlib style).

## Global Constraints

- Resource type root: `core/wcm/components/contentaisearch/v1/contentaisearch`
- `componentGroup=".core-wcm"`
- No new Maven dependencies — `HttpClientBuilderFactory`, Apache HttpComponents, and Jackson are all already available (used by `OEmbedClientImpl`, `SearchResultServlet`).
- Do not modify anything under `search/v2`, `search/v3`, or `SearchResultServlet` — this component is fully additive.
- Servlets are bound via `sling.servlet.resourceTypes` to this component's own resource type, never a fixed `/bin/...` path.
- Java package conventions: public API in `com.adobe.cq.wcm.core.components.{models,services.contentai}`, internals in `com.adobe.cq.wcm.core.components.internal.{models.v1,services.contentai,servlets.contentaisearch}` — matching the existing `services.embed` / `internal.services.embed` split.
- Content AI request shapes (do not deviate — these are the officially published contract):
  - `POST {baseUrl}/content-sources/search` — body `{contentSource:{name}, query:{type:"composite",operator:"OR",queries:[...]}, queryOptions:{pagination:{limit}}}` — response `{totalResults, results:[{id,score,data,chunks}], cursor}`
  - `POST {baseUrl}/content-sources/gensearch` — body `{query, contentSource:{name}}` — response `{query, result, hits:[{id, metadata?}]}`
- Sources rendering: render `hit.metadata.url`/`hit.metadata.title` if present, else fall back to showing the raw `id` as unlinked text (per design decision — no ingestion convention required).
- All Java classes need the standard Apache 2.0 license header (copy the header verbatim from `SearchImpl.java`, changing the year to 2026).

---

### Task 1: Public API — `ContentAIClient` interface, result types, exception

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/package-info.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentAIClient.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentAIClientException.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentSourceSearchResult.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/ContentSourceQueryResult.java`

**Interfaces:**
- Produces: `ContentAIClient.search(String contentSource, String query, int limit)` returns `ContentSourceSearchResult`, throws `ContentAIClientException`. `ContentAIClient.genSearch(String contentSource, String query)` returns `ContentSourceQueryResult`, throws `ContentAIClientException`. Both result types have public no-arg constructors and public getters/setters (Jackson needs them) matching the JSON field names exactly (`totalResults`, `results`, `cursor`, `id`, `score`, `data`, `chunks`, `query`, `result`, `hits`, `metadata`).

This is a pure data/interface task — no test needed (nothing to assert on yet; behavior is tested in Task 2/3 against `ContentAIClientImpl`).

- [ ] **Step 1: Create the package-info with API version annotation**

```java
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
@Version("1.0.0")
package com.adobe.cq.wcm.core.components.services.contentai;

import org.osgi.annotation.versioning.Version;
```

- [ ] **Step 2: Create `ContentAIClientException`**

```java
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
 * Thrown when a call to the Content AI APIs fails, either due to a transport-level
 * error or a non-200 response.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
public class ContentAIClientException extends Exception {

    private static final long serialVersionUID = 1L;

    private final int statusCode;

    public ContentAIClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
    }

    public ContentAIClientException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     * @return the HTTP status code returned by Content AI, or {@code 0} if the failure was a transport-level error
     *         (timeout, connection refused, etc.) rather than an HTTP response.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
```

- [ ] **Step 3: Create `ContentSourceSearchResult`**

```java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Deserialized response of a {@code POST /content-sources/search} call to the Content AI AI Search API.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSourceSearchResult {

    private long totalResults;
    private List<Item> results = new ArrayList<>();
    private String cursor;

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public List<Item> getResults() {
        return results;
    }

    public void setResults(List<Item> results) {
        this.results = results;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String id;
        private double score;
        private Map<String, Object> data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
```

- [ ] **Step 4: Create `ContentSourceQueryResult`**

```java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Deserialized response of a {@code POST /content-sources/gensearch} call to the Content AI Generative Search API.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentai 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContentSourceQueryResult {

    private String query;
    private String result;
    private List<Hit> hits = new ArrayList<>();

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Hit {
        private String id;
        private Map<String, Object> metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }
    }
}
```

- [ ] **Step 5: Create the `ContentAIClient` interface**

```java
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
```

- [ ] **Step 6: Compile to confirm no errors**

Run: `mvn compile -pl bundles/core -am`
Expected: `BUILD SUCCESS`

- [ ] **Step 7: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/services/contentai/
git commit -m "feat: add ContentAIClient public API interfaces and result types"
```

---

### Task 2: `ContentAIConfig` + `ContentAIClientImpl.search()`

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIConfig.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImpl.java`
- Test: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImplTest.java`

**Interfaces:**
- Consumes: `ContentAIClient`, `ContentAIClientException`, `ContentSourceSearchResult` from Task 1.
- Produces: `ContentAIClientImpl implements ContentAIClient` — this task implements `search(...)` fully; `genSearch(...)` is added in Task 3 (throw `UnsupportedOperationException` as a placeholder body so the class compiles against the interface — Task 3 replaces it immediately).

- [ ] **Step 1: Create `ContentAIConfig` (OSGi metatype)**

```java
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

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Core Components Content AI Client",
    description = "Configuration for connecting to the AEM Content AI APIs."
)
public @interface ContentAIConfig {

    int DEFAULT_CONNECTION_TIMEOUT = 2000;
    int DEFAULT_SOCKET_TIMEOUT = 10000;

    @AttributeDefinition(
        name = "Content AI Base URL",
        description = "Base URL for the Content AI APIs, e.g. " +
            "https://author-p12345-e123456.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI"
    )
    String baseUrl();

    @AttributeDefinition(
        name = "Bearer Token",
        description = "IMS bearer token used to authenticate with the Content AI APIs.",
        type = AttributeType.PASSWORD
    )
    String bearerToken();

    @AttributeDefinition(
        name = "Default Content Source",
        description = "Default content source name to search against when a component instance does not specify one."
    )
    String defaultContentSource() default "";

    @AttributeDefinition(
        name = "Connection Timeout",
        description = "Time (ms) to establish the connection with Content AI."
    )
    int connectionTimeout() default DEFAULT_CONNECTION_TIMEOUT;

    @AttributeDefinition(
        name = "Socket Timeout",
        description = "Time (ms) waiting for data after establishing the connection."
    )
    int socketTimeout() default DEFAULT_SOCKET_TIMEOUT;
}
```

- [ ] **Step 2: Write the failing test for `search()`**

```java
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
```

- [ ] **Step 3: Run the test to verify it fails (class doesn't exist yet)**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest`
Expected: `COMPILATION ERROR` — `ContentAIClientImpl` does not exist.

- [ ] **Step 4: Create `ContentAIClientImpl` implementing `search()`**

```java
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
```

- [ ] **Step 5: Run the tests to verify they pass**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest`
Expected: `Tests run: 2, Failures: 0, Errors: 0` (the `genSearch` method is untested here — its test comes in Task 3)

- [ ] **Step 6: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/
git commit -m "feat: implement ContentAIClient.search() with OSGi config"
```

---

### Task 3: `ContentAIClientImpl.genSearch()`

**Files:**
- Modify: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImpl.java`
- Modify: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImplTest.java`

**Interfaces:**
- Consumes: `ContentSourceQueryResult` from Task 1, `executeRequest(...)` private helper from Task 2 (reused as-is).
- Produces: working `ContentAIClientImpl.genSearch(String contentSource, String query)`.

- [ ] **Step 1: Add the failing test for `genSearch()`**

Add to `ContentAIClientImplTest.java` (inside the class, alongside the existing tests):

```java
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
```

- [ ] **Step 2: Run the tests to verify the new ones fail**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest`
Expected: `genSearchReturnsParsedResult` and `genSearchThrowsOnServerError` FAIL with `UnsupportedOperationException`.

- [ ] **Step 3: Implement `genSearch()`**

Replace the `genSearch` method body in `ContentAIClientImpl.java`:

```java
    @Override
    public ContentSourceQueryResult genSearch(String contentSource, String query) throws ContentAIClientException {
        ObjectNode body = mapper.createObjectNode();
        body.put("query", query);
        body.putObject("contentSource").put("name", contentSource);

        JsonNode response = executeRequest("/content-sources/gensearch", body);
        try {
            return mapper.treeToValue(response, ContentSourceQueryResult.class);
        } catch (IOException e) {
            throw new ContentAIClientException("Failed to parse Content AI gensearch response", e);
        }
    }
```

- [ ] **Step 4: Run the tests to verify they pass**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest`
Expected: `Tests run: 4, Failures: 0, Errors: 0`

- [ ] **Step 5: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImpl.java bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/services/contentai/ContentAIClientImplTest.java
git commit -m "feat: implement ContentAIClient.genSearch()"
```

---

### Task 4: `ContentAISupportedSearch` Sling Model

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ContentAISupportedSearch.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/ContentAISupportedSearchImpl.java`
- Test: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/models/v1/ContentAISupportedSearchImplTest.java`
- Test resources: `bundles/core/src/test/resources/contentaisupportedsearch/test-content-dam.json` (page/component content fixture, AEM-mock style)

**Interfaces:**
- Produces: `ContentAISupportedSearch` interface with `getContentSource()`, `getResultsSize()`, `isGenSearchEnabledByDefault()`, `getPlaceholder()`, `getDisclaimerText()`, `getI18nMessages()`. `ContentAISupportedSearchImpl.RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/contentaisearch"` (referenced by servlets in Tasks 5–6).

- [ ] **Step 1: Write the failing test**

```java
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.Utils;
import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class ContentAISupportedSearchImplTest {

    private static final String TEST_BASE = "/contentaisupportedsearch";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content-dam.json", CONTENT_ROOT);
    }

    @Test
    void testProperties() {
        ContentAISupportedSearch search = context.currentResource(COMPONENT_PATH).adaptTo(ContentAISupportedSearch.class);
        assertEquals("my-content-source", search.getContentSource());
        assertEquals(5, search.getResultsSize());
        assertTrue(search.isGenSearchEnabledByDefault());
    }
}
```

- [ ] **Step 2: Create the test content fixture**

```json
{
  "jcr:primaryType": "cq:Page",
  "jcr:content": {
    "jcr:primaryType": "cq:PageContent",
    "sling:resourceType": "core/wcm/components/page/v3/page",
    "contentaisearch": {
      "jcr:primaryType": "nt:unstructured",
      "sling:resourceType": "core/wcm/components/contentaisearch/v1/contentaisearch",
      "id": "contentaisearch-1",
      "contentSource": "my-content-source",
      "resultsSize": 5,
      "genSearchEnabledByDefault": true
    }
  }
}
```

- [ ] **Step 3: Run the test to verify it fails**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAISupportedSearchImplTest`
Expected: `COMPILATION ERROR` — `ContentAISupportedSearch` model interface doesn't exist.

- [ ] **Step 4: Create the `ContentAISupportedSearch` interface**

```java
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
package com.adobe.cq.wcm.core.components.models;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the {@code ContentAISupportedSearch} Sling Model used for the
 * {@code /apps/core/wcm/components/contentaisearch} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 1.0.0
 */
public interface ContentAISupportedSearch extends Component {

    String PN_CONTENT_SOURCE = "contentSource";
    String PN_RESULTS_SIZE = "resultsSize";
    String PN_GENSEARCH_ENABLED_BY_DEFAULT = "genSearchEnabledByDefault";
    String PN_PLACEHOLDER = "placeholder";
    String PN_DISCLAIMER_TEXT = "disclaimerText";

    /**
     * @return the name of the Content AI content source this component queries.
     */
    @NotNull
    default String getContentSource() {
        return "";
    }

    /**
     * @return the maximum number of results to fetch from the results list.
     */
    default int getResultsSize() {
        return 0;
    }

    /**
     * @return whether the generative-summary toggle should default to on.
     */
    default boolean isGenSearchEnabledByDefault() {
        return true;
    }

    /**
     * @return the placeholder text for the search input, or {@code null} if not configured.
     */
    @Nullable
    default String getPlaceholder() {
        return null;
    }

    /**
     * @return the disclaimer text shown below the generative summary, or {@code null} to use the default i18n string.
     */
    @Nullable
    default String getDisclaimerText() {
        return null;
    }

    /**
     * @return a JSON string of localized messages for client-side use.
     */
    @NotNull
    default String getI18nMessages() {
        return "{}";
    }
}
```

- [ ] **Step 5: Create the `ContentAISupportedSearchImpl` implementation**

```java
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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.i18n.I18n;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ContentAISupportedSearch.class, ComponentExporter.class},
    resourceType = ContentAISupportedSearchImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentAISupportedSearchImpl extends AbstractComponentImpl implements ContentAISupportedSearch {

    protected static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/contentaisearch";

    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;
    public static final boolean PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT = true;

    @ScriptVariable
    private Page currentPage;

    @ValueMapValue
    @Default(values = "")
    private String contentSource;

    @ValueMapValue
    @Default(intValues = PROP_RESULTS_SIZE_DEFAULT)
    private int resultsSize;

    @ValueMapValue
    @Default(booleanValues = PROP_GENSEARCH_ENABLED_BY_DEFAULT_DEFAULT)
    private boolean genSearchEnabledByDefault;

    @ValueMapValue
    @Default(values = "")
    private String placeholder;

    @ValueMapValue
    @Default(values = "")
    private String disclaimerText;

    private final Map<String, String> i18nMessagesMap = new HashMap<>();

    @PostConstruct
    private void initModel() {
        // no-op for now; kept for parity with SearchImpl's initModel pattern and future extension
    }

    @NotNull
    @Override
    public String getContentSource() {
        return contentSource;
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @Override
    public boolean isGenSearchEnabledByDefault() {
        return genSearchEnabledByDefault;
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public String getDisclaimerText() {
        return disclaimerText;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return RESOURCE_TYPE;
    }

    @JsonIgnore
    @NotNull
    @Override
    public String getI18nMessages() {
        Locale pageLocale = currentPage.getLanguage(false);
        ResourceBundle resourceBundle = request.getResourceBundle(pageLocale);
        I18n i18n = new I18n(resourceBundle);
        i18nMessagesMap.put("Search", i18n.get("Search"));
        i18nMessagesMap.put("Clear", i18n.get("Clear"));
        i18nMessagesMap.put("AI-generated responses may be inaccurate. Verify important information.",
            i18n.get("AI-generated responses may be inaccurate. Verify important information."));
        try {
            return new ObjectMapper().writeValueAsString(i18nMessagesMap);
        } catch (Exception e) {
            return "{}";
        }
    }
}
```

- [ ] **Step 6: Run the test to verify it passes**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAISupportedSearchImplTest`
Expected: `Tests run: 1, Failures: 0, Errors: 0`

- [ ] **Step 7: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/models/ContentAISupportedSearch.java bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/models/v1/ContentAISupportedSearchImpl.java bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/models/v1/ContentAISupportedSearchImplTest.java bundles/core/src/test/resources/contentaisupportedsearch/
git commit -m "feat: add ContentAISupportedSearch Sling Model"
```

---

### Task 5: `AbstractContentAISearchServlet` + `ContentAISearchResultsServlet`

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/AbstractContentAISearchServlet.java`
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAISearchResultsServlet.java`
- Test: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAISearchResultsServletTest.java`
- Test resource: `bundles/core/src/test/resources/contentaisearchservlet/test-content.json`

**Interfaces:**
- Consumes: `ContentAIClient.search(...)` (Task 2/3), `ContentAISupportedSearch` model (Task 4).
- Produces:
  - `AbstractContentAISearchServlet` — a package-private abstract `SlingSafeMethodsServlet` holding the shared request-handling logic. Declares `@Reference protected transient ContentAIClient contentAIClient;`, reads/validates the `q` param, adapts `ContentAISupportedSearch`, calls the abstract `Object executeQuery(ContentAISupportedSearch model, String query) throws ContentAIClientException`, and marshals the result to JSON. Task 6's servlet extends this too.
  - `ContentAISearchResultsServlet extends AbstractContentAISearchServlet` — registered on `sling.servlet.resourceTypes=core/wcm/components/contentaisearch/v1/contentaisearch`, `sling.servlet.selectors=search`, `sling.servlet.extensions=json`, `sling.servlet.methods=GET`. Its `executeQuery` calls `contentAIClient.search(model.getContentSource(), query, model.getResultsSize())`.

Design note (adjustment from the original plan text): the two servlets are ~90% identical, so the shared `doGet`/validation/`writeJson` logic lives in an abstract base with one abstract `executeQuery` hook per concrete servlet — the same "abstract base + concrete subclasses" shape this codebase already uses for `AbstractComponentImpl`. `@Reference` fields declared on the abstract superclass are picked up by both the real OSGi DS runtime (bnd scans the full class hierarchy) and `osgi-mock`'s `registerInjectActivateService`, so the test approach is unchanged. This follows `SearchResultServletTest`'s real pattern: `AemContext` + `context.registerService(...)` + `context.registerInjectActivateService(...)` auto-injects the `@Reference ContentAIClient` — no fake test seams.

- [ ] **Step 1: Create the test content fixture**

```json
{
  "jcr:primaryType": "cq:Page",
  "jcr:content": {
    "jcr:primaryType": "cq:PageContent",
    "sling:resourceType": "core/wcm/components/page/v3/page",
    "contentaisearch": {
      "jcr:primaryType": "nt:unstructured",
      "sling:resourceType": "core/wcm/components/contentaisearch/v1/contentaisearch",
      "id": "contentaisearch-1",
      "contentSource": "my-source",
      "resultsSize": 10
    }
  }
}
```

- [ ] **Step 2: Write the failing test**

```java
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceSearchResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentAISearchResultsServletTest {

    private static final String TEST_BASE = "/contentaisearchservlet";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ContentAISearchResultsServlet underTest;
    private ContentAIClient mockClient;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
        mockClient = mock(ContentAIClient.class);
        context.registerService(ContentAIClient.class, mockClient);
        underTest = context.registerInjectActivateService(new ContentAISearchResultsServlet());
    }

    @Test
    void doGetWritesSearchResultsAsJson() throws Exception {
        ContentSourceSearchResult expected = new ContentSourceSearchResult();
        expected.setTotalResults(1);
        when(mockClient.search(eq("my-source"), eq("electric cars"), anyInt())).thenReturn(expected);

        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=electric+cars");

        underTest.doGet(context.request(), context.response());

        assertEquals(200, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("\"totalResults\":1"));
    }

    @Test
    void doGetReturns400WhenQueryMissing() throws Exception {
        context.currentResource(COMPONENT_PATH);

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }
}
```

- [ ] **Step 3: Run the test to verify it fails**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAISearchResultsServletTest`
Expected: `COMPILATION ERROR` — `ContentAISearchResultsServlet` doesn't exist yet.

- [ ] **Step 4: Create `AbstractContentAISearchServlet`**

```java
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base servlet for the ContentAI Supported Search component's two query endpoints.
 * Handles the shared request lifecycle — {@code q} parameter validation, model adaptation,
 * Content AI error handling, and JSON marshaling — delegating the actual Content AI call to
 * {@link #executeQuery(ContentAISupportedSearch, String)}.
 */
abstract class AbstractContentAISearchServlet extends SlingSafeMethodsServlet {

    protected static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/contentaisearch";
    protected static final String EXTENSION = "json";
    private static final String PARAM_QUERY = "q";
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContentAISearchServlet.class);

    @Reference
    protected transient ContentAIClient contentAIClient;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
        String queryText = request.getParameter(PARAM_QUERY);
        if (StringUtils.isBlank(queryText)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter: " + PARAM_QUERY);
            return;
        }

        ContentAISupportedSearch model = request.adaptTo(ContentAISupportedSearch.class);
        if (model == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        try {
            Object result = executeQuery(model, queryText);
            writeJson(result, response);
        } catch (ContentAIClientException e) {
            LOGGER.error("Content AI request failed for content source {}", model.getContentSource(), e);
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Content AI request failed");
        }
    }

    /**
     * Executes the specific Content AI call for this servlet.
     *
     * @param model the resolved component model providing the content source and result size
     * @param query the validated user query
     * @return the result object to serialize as JSON
     * @throws ContentAIClientException if the Content AI call fails
     */
    protected abstract Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query) throws ContentAIClientException;

    private void writeJson(Object result, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        new ObjectMapper().writeValue(response.getWriter(), result);
    }
}
```

- [ ] **Step 5: Create `ContentAISearchResultsServlet`**

```java
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import javax.servlet.Servlet;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;

/**
 * Servlet exposing the ContentAI Supported Search component's results-list endpoint,
 * backed by the Content AI AI Search API.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=" + AbstractContentAISearchServlet.RESOURCE_TYPE,
        "sling.servlet.selectors=" + ContentAISearchResultsServlet.SELECTOR,
        "sling.servlet.extensions=" + AbstractContentAISearchServlet.EXTENSION
    }
)
public class ContentAISearchResultsServlet extends AbstractContentAISearchServlet {

    protected static final String SELECTOR = "search";
    private static final long serialVersionUID = 1L;

    @Override
    protected Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query) throws ContentAIClientException {
        return contentAIClient.search(model.getContentSource(), query, model.getResultsSize());
    }
}
```

*(Note: `context.registerInjectActivateService(...)` resolves the inherited `@Reference ContentAIClient` against the OSGi-mock service registry, so registering the mock via `context.registerService(ContentAIClient.class, mockClient)` before activating is enough. `ContentAISupportedSearch` is not separately mocked — `context.currentResource(COMPONENT_PATH)` with the real `ContentAISupportedSearchImpl` model from Task 4 makes `request.adaptTo(ContentAISupportedSearch.class)` resolve end-to-end.)*

- [ ] **Step 6: Run the test to verify it passes**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAISearchResultsServletTest`
Expected: `Tests run: 2, Failures: 0, Errors: 0`

- [ ] **Step 7: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/AbstractContentAISearchServlet.java bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAISearchResultsServlet.java bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAISearchResultsServletTest.java bundles/core/src/test/resources/contentaisearchservlet/
git commit -m "feat: add AbstractContentAISearchServlet and ContentAISearchResultsServlet"
```

---

### Task 6: `ContentAIGenSearchServlet`

**Files:**
- Create: `bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAIGenSearchServlet.java`
- Test: `bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAIGenSearchServletTest.java`

**Interfaces:**
- Consumes: `AbstractContentAISearchServlet` (Task 5), `ContentAIClient.genSearch(...)` (Task 3), `ContentAISupportedSearch` model (Task 4). Reuses the test fixture from Task 5 (`bundles/core/src/test/resources/contentaisearchservlet/test-content.json`).
- Produces: `ContentAIGenSearchServlet extends AbstractContentAISearchServlet`, registered on the same resource type with `sling.servlet.selectors=gensearch`. Its `executeQuery` calls `contentAIClient.genSearch(model.getContentSource(), query)`.

- [ ] **Step 1: Write the failing test**

```java
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.adobe.cq.wcm.core.components.context.CoreComponentTestContext;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentSourceQueryResult;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class ContentAIGenSearchServletTest {

    private static final String TEST_BASE = "/contentaisearchservlet";
    private static final String CONTENT_ROOT = "/content";
    private static final String COMPONENT_PATH = CONTENT_ROOT + "/jcr:content/contentaisearch";

    private final AemContext context = CoreComponentTestContext.newAemContext();

    private ContentAIGenSearchServlet underTest;
    private ContentAIClient mockClient;

    @BeforeEach
    void setUp() {
        context.load().json(TEST_BASE + "/test-content.json", CONTENT_ROOT);
        mockClient = mock(ContentAIClient.class);
        context.registerService(ContentAIClient.class, mockClient);
        underTest = context.registerInjectActivateService(new ContentAIGenSearchServlet());
    }

    @Test
    void doGetWritesGenSearchResultAsJson() throws Exception {
        ContentSourceQueryResult expected = new ContentSourceQueryResult();
        expected.setResult("Electric cars are efficient.");
        when(mockClient.genSearch(eq("my-source"), eq("electric cars"))).thenReturn(expected);

        context.currentResource(COMPONENT_PATH);
        context.request().setQueryString("q=electric+cars");

        underTest.doGet(context.request(), context.response());

        assertEquals(200, context.response().getStatus());
        assertTrue(context.response().getOutputAsString().contains("Electric cars are efficient."));
    }

    @Test
    void doGetReturns400WhenQueryMissing() throws Exception {
        context.currentResource(COMPONENT_PATH);

        underTest.doGet(context.request(), context.response());

        assertEquals(400, context.response().getStatus());
    }
}
```

- [ ] **Step 2: Run the test to verify it fails**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIGenSearchServletTest`
Expected: `COMPILATION ERROR` — `ContentAIGenSearchServlet` doesn't exist yet.

- [ ] **Step 3: Create `ContentAIGenSearchServlet`**

```java
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import javax.servlet.Servlet;

import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;

/**
 * Servlet exposing the ContentAI Supported Search component's generative-summary endpoint,
 * backed by the Content AI Generative Search API.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.methods=GET",
        "sling.servlet.resourceTypes=" + AbstractContentAISearchServlet.RESOURCE_TYPE,
        "sling.servlet.selectors=" + ContentAIGenSearchServlet.SELECTOR,
        "sling.servlet.extensions=" + AbstractContentAISearchServlet.EXTENSION
    }
)
public class ContentAIGenSearchServlet extends AbstractContentAISearchServlet {

    protected static final String SELECTOR = "gensearch";
    private static final long serialVersionUID = 1L;

    @Override
    protected Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query) throws ContentAIClientException {
        return contentAIClient.genSearch(model.getContentSource(), query);
    }
}
```

- [ ] **Step 4: Run the tests to verify they pass**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIGenSearchServletTest,ContentAISearchResultsServletTest`
Expected: `Tests run: 4, Failures: 0, Errors: 0`

- [ ] **Step 5: Run the full backend test suite for this feature**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest,ContentAISupportedSearchImplTest,ContentAISearchResultsServletTest,ContentAIGenSearchServletTest`
Expected: `Tests run: 10, Failures: 0, Errors: 0`

- [ ] **Step 6: Commit**

```bash
git add bundles/core/src/main/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAIGenSearchServlet.java bundles/core/src/test/java/com/adobe/cq/wcm/core/components/internal/servlets/contentaisearch/ContentAIGenSearchServletTest.java
git commit -m "feat: add ContentAIGenSearchServlet"
```

---

### Task 7: Component definition and dialog (content package)

**Files:**
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/.content.xml`
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/_cq_dialog/.content.xml`

**Interfaces:**
- Produces: JCR component node at `apps/core/wcm/components/contentaisearch/v1/contentaisearch` with `sling:resourceType` matching `ContentAISupportedSearchImpl.RESOURCE_TYPE` and the servlets' `RESOURCE_TYPE` constant exactly — a typo here silently breaks every servlet binding, so copy the string `core/wcm/components/contentaisearch/v1/contentaisearch` verbatim from those classes.

There is no meaningful unit test for JCR content XML in this repo's Java test suite — verification is via the content-package build succeeding and (once installed) exercising the component in AEM, covered by Task 10's manual verification step. No test step here; this is a config/scaffolding task.

- [ ] **Step 1: Create the component definition**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    xmlns:sling="http://sling.apache.org/jcr/sling/1.0"
    jcr:primaryType="cq:Component"
    jcr:title="ContentAI Supported Search"
    jcr:description="Search component with a Content AI generative summary and results list"
    cq:icon="search"
    componentGroup=".core-wcm"/>
```

- [ ] **Step 2: Create the dialog**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!--~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->
<jcr:root xmlns:sling="http://sling.apache.org/jcr/sling/1.0" xmlns:granite="http://www.adobe.com/jcr/granite/1.0"
    xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="nt:unstructured"
    jcr:title="ContentAI Supported Search"
    sling:resourceType="cq/gui/components/authoring/dialog">
    <content
        jcr:primaryType="nt:unstructured"
        sling:resourceType="granite/ui/components/coral/foundation/container">
        <items jcr:primaryType="nt:unstructured">
            <tabs
                jcr:primaryType="nt:unstructured"
                sling:resourceType="granite/ui/components/coral/foundation/tabs"
                maximized="{Boolean}true">
                <items jcr:primaryType="nt:unstructured">
                    <properties
                        jcr:primaryType="nt:unstructured"
                        jcr:title="Properties"
                        sling:resourceType="granite/ui/components/coral/foundation/fixedcolumns"
                        margin="{Boolean}true">
                        <items jcr:primaryType="nt:unstructured">
                            <column
                                jcr:primaryType="nt:unstructured"
                                sling:resourceType="granite/ui/components/coral/foundation/container">
                                <items jcr:primaryType="nt:unstructured">
                                    <id
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/textfield"
                                        fieldLabel="ID"
                                        fieldDescription="Optional HTML ID for this component."
                                        name="./id"/>
                                    <contentSource
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/textfield"
                                        fieldLabel="Content Source"
                                        fieldDescription="Name of the Content AI content source to search. Requires Content AI to be provisioned for this content source on this environment."
                                        name="./contentSource"
                                        required="{Boolean}true"/>
                                    <resultsSize
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/numberfield"
                                        fieldLabel="Number of Results"
                                        fieldDescription="Maximum number of results to fetch."
                                        name="./resultsSize"
                                        value="10"
                                        min="1"/>
                                    <placeholder
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/textfield"
                                        fieldLabel="Placeholder Text"
                                        fieldDescription="Placeholder text for the search input."
                                        name="./placeholder"/>
                                    <genSearchEnabledByDefault
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/checkbox"
                                        text="Show generative summary by default"
                                        fieldDescription="Controls the default state of the visitor-facing toggle for the AI-generated summary."
                                        name="./genSearchEnabledByDefault"
                                        checked="{Boolean}true"
                                        value="{Boolean}true"
                                        uncheckedValue="{Boolean}false"/>
                                    <disclaimerText
                                        jcr:primaryType="nt:unstructured"
                                        sling:resourceType="granite/ui/components/coral/foundation/form/textarea"
                                        fieldLabel="Disclaimer Text"
                                        fieldDescription="Shown below the generative summary. Leave empty to use the default disclaimer."
                                        name="./disclaimerText"/>
                                </items>
                            </column>
                        </items>
                    </properties>
                </items>
            </tabs>
        </items>
    </content>
</jcr:root>
```

- [ ] **Step 3: Validate the content package builds**

Run: `mvn package -pl content -am`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/
git commit -m "feat: add ContentAI Supported Search component definition and dialog"
```

---

### Task 8: HTL markup and i18n

**Files:**
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/contentaisearch.html`
- Modify: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/.content.xml` (add `jcr:description` already present — no change needed here beyond Task 7)
- Create/Modify i18n dictionary entries (see Step 3)

**Interfaces:**
- Consumes: `ContentAISupportedSearch` model getters from Task 4 (`getId()`, `getContentSource()`, `getPlaceholder()`, `isGenSearchEnabledByDefault()`, `getDisclaimerText()`, `getI18nMessages()`).
- Produces: DOM structure with `data-cmp-is="contentaisearch"`, a `data-cmp-resource-path="${resource.path}"` attribute (the component's own JCR resource path, used by Task 9's JS to build servlet URLs), and `data-cmp-hook-contentaisearch="..."` hooks that Task 9's JS binds to: `input`, `toggle`, `summary`, `summaryText`, `sources`, `disclaimer`, `results`, `loadingIndicator`, `error`.

- [ ] **Step 1: Create the HTL template**

```html
<!--/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/-->
<section data-sly-use.search="com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch"
         id="${search.id}"
         class="cmp-contentaisearch"
         role="search"
         data-cmp-is="contentaisearch"
         data-cmp-content-source="${search.contentSource}"
         data-cmp-gensearch-enabled-default="${search.genSearchEnabledByDefault}"
         data-cmp-resource-path="${resource.path}"
         data-i18n-messages='${search.i18nMessages}'>
    <form class="cmp-contentaisearch__form" data-cmp-hook-contentaisearch="form" autocomplete="off">
        <div class="cmp-contentaisearch__field">
            <label class="cmp-contentaisearch__input-label" for="${search.id}-input">${'Search' @ i18n}</label>
            <input id="${search.id}-input" class="cmp-contentaisearch__input" data-cmp-hook-contentaisearch="input"
                   type="text" name="q" placeholder="${search.placeholder || 'Search' @ i18n}" autocomplete="off">
            <span class="cmp-contentaisearch__loading-indicator" data-cmp-hook-contentaisearch="loadingIndicator" hidden="hidden"></span>
        </div>
        <label class="cmp-contentaisearch__toggle">
            <input type="checkbox" data-cmp-hook-contentaisearch="toggle"
                   checked="${search.genSearchEnabledByDefault ? true : false}"
                   aria-label="${'Show AI-generated summary' @ i18n}">
            ${'Show AI-generated summary' @ i18n}
        </label>
    </form>
    <div class="cmp-contentaisearch__summary" data-cmp-hook-contentaisearch="summary" hidden="hidden">
        <p class="cmp-contentaisearch__summary-text" data-cmp-hook-contentaisearch="summaryText"></p>
        <ul class="cmp-contentaisearch__sources" data-cmp-hook-contentaisearch="sources"></ul>
        <p class="cmp-contentaisearch__disclaimer" data-cmp-hook-contentaisearch="disclaimer">
            ${search.disclaimerText || 'AI-generated responses may be inaccurate. Verify important information.' @ i18n}
        </p>
    </div>
    <div class="cmp-contentaisearch__error" data-cmp-hook-contentaisearch="error" hidden="hidden">
        <p>${'Something went wrong generating the summary.' @ i18n}</p>
        <button type="button" data-cmp-hook-contentaisearch="retry">${'Try again' @ i18n}</button>
    </div>
    <ul class="cmp-contentaisearch__results" data-cmp-hook-contentaisearch="results" aria-label="${'Search results' @ i18n}"></ul>
</section>
```

- [ ] **Step 2: Add the i18n keys used above**

Add the new translatable strings (`Show AI-generated summary`, `Something went wrong generating the summary.`, `Try again`, `AI-generated responses may be inaccurate. Verify important information.`) to this repo's English i18n dictionary. Find the existing dictionary file used by `search/v3` for `'AI Search' @ i18n` (check `content/src/content/jcr_root/apps/core/wcm/components/search/v3/search/...` or a shared `i18n/en.xml`/`.content.xml` dictionary node this repo already uses for Quick Search's toggle strings — GRANITE-69682 added `'AI Search' @ i18n` and `'Clear' @ i18n` the same way; add the new keys as sibling entries in that same dictionary resource, following its exact node structure).

- [ ] **Step 3: Validate the content package builds**

Run: `mvn package -pl content -am`
Expected: `BUILD SUCCESS`

- [ ] **Step 4: Commit**

```bash
git add content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/contentaisearch.html
git commit -m "feat: add ContentAI Supported Search HTL markup"
```

---

### Task 9: Client-side JS — toggle, parallel fetch, rendering

**Files:**
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/js/contentaisearch.js`

**Interfaces:**
- Consumes: the `data-cmp-hook-contentaisearch="..."` hooks and the `data-cmp-resource-path` attribute from Task 8 (`form`, `input`, `toggle`, `loadingIndicator`, `summary`, `summaryText`, `sources`, `disclaimer`, `error`, `retry`, `results`), and the two GET endpoints from Tasks 5–6, built as `{data-cmp-resource-path}.search.json?q=...` and `{data-cmp-resource-path}.gensearch.json?q=...` — resolved against the component's own resource path (not the page path), since these servlets are bound to the component's resource type, not `cq/Page`.

- [ ] **Step 1: Write the JS**

```javascript
/*******************************************************************************
 * Copyright 2026 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
(function() {
    "use strict";

    var NS = "cmp";
    var IS = "contentaisearch";
    var DELAY = 300;

    var selectors = {
        self: "[data-" + NS + '-is="' + IS + '"]'
    };

    function toggleShow(element, show) {
        if (element) {
            if (show) {
                element.removeAttribute("hidden");
            } else {
                element.setAttribute("hidden", "hidden");
            }
        }
    }

    function localizeMessage(root, message) {
        try {
            var i18nMessages = JSON.parse(root.getAttribute("data-i18n-messages"));
            return i18nMessages[message] || message;
        } catch (e) {
            return message;
        }
    }

    function ContentAISearch(element) {
        this._element = element;
        this._cacheElements();
        this._resourcePath = this._resolveResourcePath();
        this._genSearchEnabled = this._elements.toggle ? this._elements.toggle.checked : false;
        this._timeout = null;

        this._elements.input.addEventListener("input", this._onInput.bind(this));
        if (this._elements.toggle) {
            this._elements.toggle.addEventListener("change", this._onToggleChange.bind(this));
        }
        if (this._elements.retry) {
            this._elements.retry.addEventListener("click", this._onRetry.bind(this));
        }
        if (this._elements.form) {
            this._elements.form.addEventListener("submit", function(event) {
                event.preventDefault();
            });
        }
    }

    ContentAISearch.prototype._cacheElements = function() {
        this._elements = {};
        var hooks = this._element.querySelectorAll("[data-" + NS + "-hook-" + IS + "]");
        for (var i = 0; i < hooks.length; i++) {
            var hook = hooks[i];
            var key = hook.dataset[NS + "Hook" + IS.charAt(0).toUpperCase() + IS.slice(1)];
            this._elements[key] = hook;
        }
    };

    ContentAISearch.prototype._resolveResourcePath = function() {
        // The component's own JCR resource path, rendered by the HTL as data-cmp-resource-path.
        // Used to build the .search.json / .gensearch.json selector URLs against the servlets
        // bound to this component's resource type — correct even with multiple instances of
        // this component on the same page, since each instance's root element carries its own path.
        return this._element.getAttribute("data-cmp-resource-path");
    };

    ContentAISearch.prototype._onInput = function() {
        var self = this;
        clearTimeout(this._timeout);
        this._timeout = setTimeout(function() {
            self._runQuery();
        }, DELAY);
    };

    ContentAISearch.prototype._onToggleChange = function() {
        this._genSearchEnabled = this._elements.toggle.checked;
        if (!this._genSearchEnabled) {
            toggleShow(this._elements.summary, false);
            toggleShow(this._elements.error, false);
        }
        this._runQuery();
    };

    ContentAISearch.prototype._onRetry = function() {
        this._runGenSearch(this._elements.input.value);
    };

    ContentAISearch.prototype._runQuery = function() {
        var query = this._elements.input.value;
        if (!query) {
            this._clearResults();
            return;
        }
        this._runResultsSearch(query);
        if (this._genSearchEnabled) {
            this._runGenSearch(query);
        }
    };

    ContentAISearch.prototype._clearResults = function() {
        this._elements.results.innerHTML = "";
        toggleShow(this._elements.summary, false);
        toggleShow(this._elements.error, false);
    };

    ContentAISearch.prototype._runResultsSearch = function(query) {
        var self = this;
        toggleShow(this._elements.loadingIndicator, true);
        this._fetchJson(this._resourcePath + ".search.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                self._renderResults(data);
            })
            .catch(function() {
                self._elements.results.innerHTML = "";
            })
            .then(function() {
                toggleShow(self._elements.loadingIndicator, false);
            });
    };

    ContentAISearch.prototype._runGenSearch = function(query) {
        var self = this;
        toggleShow(this._elements.error, false);
        toggleShow(this._elements.summary, false);
        this._fetchJson(this._resourcePath + ".gensearch.json?q=" + encodeURIComponent(query))
            .then(function(data) {
                self._renderSummary(data);
            })
            .catch(function() {
                toggleShow(self._elements.error, true);
            });
    };

    ContentAISearch.prototype._fetchJson = function(url) {
        return fetch(url).then(function(response) {
            if (!response.ok) {
                throw new Error("Request to " + url + " failed with status " + response.status);
            }
            return response.json();
        });
    };

    ContentAISearch.prototype._renderResults = function(data) {
        var results = (data && data.results) || [];
        var html = "";
        for (var i = 0; i < results.length; i++) {
            var item = results[i];
            var title = (item.data && (item.data.title || item.data.name)) || item.id;
            html += "<li class=\"cmp-contentaisearch__item\">" + this._escapeHtml(title) + "</li>";
        }
        this._elements.results.innerHTML = html;
    };

    ContentAISearch.prototype._renderSummary = function(data) {
        this._elements.summaryText.textContent = data.result || "";
        var hits = data.hits || [];
        var sourcesHtml = "";
        for (var i = 0; i < hits.length; i++) {
            var hit = hits[i];
            var url = hit.metadata && hit.metadata.url;
            var label = (hit.metadata && hit.metadata.title) || hit.id;
            if (url) {
                sourcesHtml += "<li><a href=\"" + this._escapeAttribute(url) + "\">" + this._escapeHtml(label) + "</a></li>";
            } else {
                sourcesHtml += "<li>" + this._escapeHtml(label) + "</li>";
            }
        }
        this._elements.sources.innerHTML = sourcesHtml;
        toggleShow(this._elements.summary, true);
    };

    ContentAISearch.prototype._escapeHtml = function(text) {
        var div = document.createElement("div");
        div.textContent = String(text == null ? "" : text);
        return div.innerHTML;
    };

    ContentAISearch.prototype._escapeAttribute = function(text) {
        return this._escapeHtml(text).replace(/"/g, "&quot;");
    };

    function onDocumentReady() {
        var elements = document.querySelectorAll(selectors.self);
        for (var i = 0; i < elements.length; i++) {
            new ContentAISearch(elements[i]);
        }
    }

    if (document.readyState !== "loading") {
        onDocumentReady();
    } else {
        document.addEventListener("DOMContentLoaded", onDocumentReady);
    }
})();
```

- [ ] **Step 2: Manual verification (no automated JS test harness exists for clientlibs in this repo)**

This repo does not have a JS unit test runner wired up for component clientlibs (confirmed: `search/v3`'s `search.js` also has no corresponding JS test file). Verification here is via Task 10's manual browser check.

- [ ] **Step 3: Commit**

```bash
git add content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/js/contentaisearch.js
git commit -m "feat: add ContentAI Supported Search client-side toggle and fetch logic"
```

---

### Task 10: Clientlib registration, CSS, and manual verification

**Files:**
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/.content.xml`
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/.content.xml`
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/js.txt`
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/css.txt`
- Create: `content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/site/css/contentaisearch.less`

**Interfaces:** none — this task wires up what Tasks 7–9 already created.

- [ ] **Step 1: Create the clientlib folder node**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:jcr="http://www.jcp.org/jcr/1.0" xmlns:nt="http://www.jcp.org/jcr/nt/1.0"
    jcr:primaryType="nt:folder"/>
```

- [ ] **Step 2: Create the clientlib definition**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jcr:root xmlns:cq="http://www.day.com/jcr/cq/1.0" xmlns:jcr="http://www.jcp.org/jcr/1.0"
    jcr:primaryType="cq:ClientLibraryFolder"
    categories="[core.wcm.components.contentaisearch.v1]"
    dependencies="[core.wcm.components.commons.site]"
    embed="[]"/>
```

- [ ] **Step 3: Create `js.txt` and `css.txt`**

`js.txt`:
```
js/contentaisearch.js
```

`css.txt`:
```
css/contentaisearch.less
```

- [ ] **Step 4: Create the CSS**

```less
/*******************************************************************************
 * Copyright 2026 Adobe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
.cmp-contentaisearch {
    &__field {
        position: relative;
        display: flex;
        align-items: center;
    }

    &__toggle {
        display: flex;
        align-items: center;
        gap: 0.5em;
        margin-top: 0.5em;
        font-size: 0.875em;
    }

    &__summary {
        margin: 1em 0;
        padding: 1em;
        border: 1px solid #ccc;
        border-radius: 4px;
        background: #f8f8f8;
    }

    &__sources {
        list-style: none;
        margin: 0.5em 0 0;
        padding: 0;
        font-size: 0.875em;
    }

    &__disclaimer {
        margin: 0.5em 0 0;
        font-size: 0.75em;
        color: #666;
    }

    &__error {
        margin: 1em 0;
        color: #b00;
    }

    &__results {
        list-style: none;
        margin: 1em 0 0;
        padding: 0;
    }
}
```

- [ ] **Step 5: Validate the content package builds**

Run: `mvn package -pl content -am`
Expected: `BUILD SUCCESS`

- [ ] **Step 6: Run the full backend test suite one more time**

Run: `mvn test -pl bundles/core -am -Dtest=ContentAIClientImplTest,ContentAISupportedSearchImplTest,ContentAISearchResultsServletTest,ContentAIGenSearchServletTest`
Expected: `Tests run: 10, Failures: 0, Errors: 0`

- [ ] **Step 7: Manual verification in a running AEM instance**

Per this repo's README (`mvn clean install -PautoInstallPackage -pl content -am`), deploy to a local AEM instance that has Content AI provisioned (per the design doc's rollout section — a real `ContentAIConfig` OSGi config with a valid `baseUrl`/`bearerToken`/content source must be set via Felix Console for this to return real data; without it, the servlets will surface a 502 from `ContentAIClientException`, which is the correct behavior to verify first). Steps:
1. Deploy: `mvn clean install -PautoInstallPackage -pl content -am`
2. Configure `Core Components Content AI Client` in Felix Console (`/system/console/configMgr`) with a valid Content AI `baseUrl` and `bearerToken`.
3. Create a page, drag "ContentAI Supported Search" onto it, configure a valid `contentSource` in the dialog.
4. On the published/preview page: type a query, confirm the results list appears below and the AI summary appears above with sources.
5. Uncheck the toggle, confirm the summary section disappears and the results list still updates on new queries.
6. Stop the OSGi config (or point `baseUrl` at an invalid host) and confirm the summary section shows the error/retry state without breaking the results list.

- [ ] **Step 8: Commit**

```bash
git add content/src/content/jcr_root/apps/core/wcm/components/contentaisearch/v1/contentaisearch/clientlibs/
git commit -m "feat: wire up ContentAI Supported Search clientlib and styling"
```

---

## Explicitly out of scope for this plan (per the design doc)

- Selenium/`it.tests` end-to-end coverage — not attempted here; the design doc's own testing section only specifies unit-level coverage.
- The release feature toggle (`FT_GRANITE-70028`) itself — that's created via the ops-level release-toggles/LaunchDarkly workflow, not code in this repository (confirmed: no `FeatureFlag`/`FeatureToggle` usage exists anywhere in `bundles/core`).
- Resolving the two blocking open questions from the design doc before merging to `main`: (1) Content AI API access/licensing confirmation, (2) whether `content-sources/gensearch` is stable enough — both are organizational, not implementation, blockers, and don't stop this plan's tasks from being built and tested against mocks.
