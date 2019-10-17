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
package com.adobe.cq.wcm.core.components.internal.models.v1.marketo;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfiguration;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfigurationManager;
import com.day.cq.commons.jcr.JcrConstants;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = { MarketoClientConfigurationManager.class })
public class MarketoClientConfigurationManagerImpl implements MarketoClientConfigurationManager {

  private static final Logger log = LoggerFactory.getLogger(MarketoClientConfigurationManagerImpl.class);

  @OSGiService
  private ConfigurationResourceResolver configRsrcRslvr;

  private Resource resource;

  public MarketoClientConfigurationManagerImpl(SlingHttpServletRequest slingRequest) {
    if (slingRequest.getResource().getPath().startsWith("/content")) {
      resource = slingRequest.getResource();
    } else {
      resource = slingRequest.getRequestPathInfo().getSuffixResource();
    }
  }

  @Override
  public MarketoClientConfiguration getConfiguration() {
    log.trace("getConfiguration");
    log.debug("Using context path: {}", configRsrcRslvr.getContextPath(resource));
    return configRsrcRslvr.getResourceCollection(resource, "settings", "cloudconfigs").stream().filter(c -> {
      boolean matches = "/apps/core/wcm/templates/marketocloudconfig"
          .equals(c.getValueMap().get("jcr:content/cq:template", ""));
      log.debug("Resource: {} matches: {}", c, matches);
      return matches;
    }).findFirst().map(c -> c.getChild(JcrConstants.JCR_CONTENT)).map(c -> c.adaptTo(MarketoClientConfiguration.class))
        .orElse(null);
  }
}
