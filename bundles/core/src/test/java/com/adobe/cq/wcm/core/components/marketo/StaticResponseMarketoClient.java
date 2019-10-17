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
package com.adobe.cq.wcm.core.components.marketo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import com.adobe.cq.wcm.core.components.internal.marketo.MarketoClientImpl;
import com.drew.lang.annotations.NotNull;

public class StaticResponseMarketoClient extends MarketoClientImpl {

  private String resourcePath;
  private Iterator<String> resourcePaths;

  public StaticResponseMarketoClient(String resourcePath) {
    this.resourcePath = resourcePath;
  }

  public StaticResponseMarketoClient(String[] resourcePaths) {
    this.resourcePaths = Arrays.asList(resourcePaths).iterator();
    if (this.resourcePaths.hasNext()) {
      resourcePath = this.resourcePaths.next();
    }
  }

  protected @NotNull String getApiResponse(@NotNull String url, String bearerToken) throws IOException {
    String resp = IOUtils.toString(StaticResponseMarketoClient.class.getResourceAsStream(resourcePath), StandardCharsets.UTF_8);
    if (resourcePaths != null && resourcePaths.hasNext()) {
      resourcePath = resourcePaths.next();
    }
    return resp;
  }
}
