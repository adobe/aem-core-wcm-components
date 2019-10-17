/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.jcr.query.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.CloudConfiguration;
import com.adobe.cq.wcm.core.components.models.CloudConfigurationList;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { CloudConfigurationList.class })
public class CloudConfigurationListImpl implements CloudConfigurationList {

  private static final Logger log = LoggerFactory.getLogger(CloudConfigurationListImpl.class);

  private List<CloudConfiguration> configs = new ArrayList<>();

  public CloudConfigurationListImpl(SlingHttpServletRequest slingRequest) {

    String template = Optional.ofNullable(slingRequest.getRequestPathInfo().getSuffix()).orElse("");

    if (StringUtils.isNotBlank(template)) {
      String query = "SELECT * FROM [cq:Page] WHERE ISDESCENDANTNODE([/conf]) AND [jcr:content/cq:template]='"
          + template.replace("'", "''") + "'";
      log.debug("Finding cloud configuerations with: {}", query);

      slingRequest.getResourceResolver().findResources(query, Query.JCR_SQL2).forEachRemaining(ccr -> {
        configs.add(ccr.adaptTo(CloudConfiguration.class));
      });
    } else {
      log.debug("Suffix not specified");

    }
  }

  @NotNull
  @Override
  public List<CloudConfiguration> getCloudConfigurations() {
    return configs;
  }
}
