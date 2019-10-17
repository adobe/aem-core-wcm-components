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
package com.adobe.cq.wcm.core.components.internal.marketo;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.marketo.MarketoClient;
import com.adobe.cq.wcm.core.components.marketo.MarketoError;
import com.adobe.cq.wcm.core.components.marketo.MarketoField;
import com.adobe.cq.wcm.core.components.marketo.MarketoForm;
import com.adobe.cq.wcm.core.components.marketo.MarketoResponse;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Implementation of the MarketoClient using the REST API.
 * 
 * @param <I>
 */
@Component(service = MarketoClient.class)
public class MarketoClientImpl implements MarketoClient {

  private static final Logger log = LoggerFactory.getLogger(MarketoClientImpl.class);

  private static final int PAGE_SIZE = 200;

  private ObjectMapper mapper = new ObjectMapper();

  protected @Nonnull String getApiResponse(@Nonnull String url, String bearerToken) throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet httpGet = new HttpGet(url);
      if (StringUtils.isNotBlank(bearerToken)) {
        httpGet.setHeader(AUTH.WWW_AUTH_RESP, "Bearer " + bearerToken);
      }
      try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
        try (InputStream content = httpResponse.getEntity().getContent()) {
          return IOUtils.toString(content, StandardCharsets.UTF_8);
        }
      }
    }
  }

  public @Nonnull String getApiToken(@Nonnull MarketoClientConfiguration config) throws IOException {
    log.trace("getApiToken");
    String url = String.format(
        "https://%s/identity/oauth/token?grant_type=client_credentials&client_id=%s&client_secret=%s",
        config.getEndpointHost(), config.getClientId(), config.getClientSecret());
    String response = getApiResponse(url, null);
    Map<?, ?> responseData = mapper.readValue(response, Map.class);
    return (String) responseData.get("access_token");

  }

  @Override
  public List<MarketoField> getFields(MarketoClientConfiguration config) throws IOException {
    String apiToken = getApiToken(config);
    List<MarketoField> fields = new ArrayList<>();

    String base = String.format("https://%s/rest/asset/v1/form/fields.json?", config.getEndpointHost());

    for (int i = 0; true; i++) {
      MarketoField[] page = getApiPage(base, apiToken, i, MarketoFieldResponse.class);
      if (page == null || page.length == 0) {
        break;
      } else {
        Arrays.stream(page).forEach(fields::add);
      }
    }
    return fields;
  }

  private @Nullable <T, R extends MarketoResponse<T>> T[] getApiPage(@Nonnull String urlBase, @Nonnull String token,
      int page, Class<R> responseType) throws IOException {
    log.trace("getApiPage({})", page);
    int offset = PAGE_SIZE * page;

    String url = String.format("%smaxReturn=%s&offset=%s", urlBase, PAGE_SIZE, offset);

    String responseText = getApiResponse(url, token);
    MarketoResponse<T> response = mapper.readValue(responseText, responseType);
    if (response.getErrors() != null && response.getErrors().length > 0) {
      throw new IOException("Retrieved errors in response: "
          + Arrays.stream(response.getErrors()).map(MarketoError::getMessage).collect(Collectors.joining(", ")));
    }
    if (!response.isSuccess()) {
      throw new IOException("Retrieved non-success response");
    }
    return response.getResult();
  }

  @Override
  public List<MarketoForm> getForms(@Nonnull MarketoClientConfiguration config) throws IOException {
    String apiToken = getApiToken(config);
    List<MarketoForm> forms = new ArrayList<>();
    String base = String.format("https://%s/rest/asset/v1/forms.json?status=approved&", config.getEndpointHost());
    for (int i = 0; true; i++) {

      MarketoForm[] page = getApiPage(base, apiToken, i, MarketoFormResponse.class);
      if (page == null || page.length == 0) {
        break;
      } else {
        Arrays.stream(page).forEach(forms::add);
      }
    }
    return forms;
  }

}
