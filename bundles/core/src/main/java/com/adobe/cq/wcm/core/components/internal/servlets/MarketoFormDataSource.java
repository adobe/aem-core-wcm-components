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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.marketo.MarketoClient;
import com.adobe.cq.wcm.core.components.marketo.MarketoForm;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfiguration;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoClientConfigurationManager;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Used to drive the list of Form and Field ID options for the Marketo Form
 * component dialog.
 */
@Component(service = Servlet.class, property = { ServletResolverConstants.SLING_SERVLET_METHODS + "=GET",
    ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES
        + "=acs-commons/components/content/marketo-form/form-data-source" })
public class MarketoFormDataSource extends SlingSafeMethodsServlet {

  private static final Logger log = LoggerFactory.getLogger(MarketoFormDataSource.class);

  private static final long serialVersionUID = -4047967365420628578L;

  private transient MarketoClient client;

  @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Field is a cache and expected to reload itself as required")
  private transient LoadingCache<MarketoClientConfiguration, List<MarketoForm>> formCache = CacheBuilder.newBuilder()
      .expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<MarketoClientConfiguration, List<MarketoForm>>() {
        public List<MarketoForm> load(MarketoClientConfiguration config) throws Exception {
          return client.getForms(config);
        }
      });

  @Reference
  public void bindMarketoClient(MarketoClient client) {
    this.client = client;
  }

  @Override
  public void doGet(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response) {
    log.trace("doGet");

    List<Resource> options = null;
    MarketoClientConfiguration config = null;
    try {
      MarketoClientConfigurationManager cfgMgr = request.adaptTo(MarketoClientConfigurationManager.class);
      if (cfgMgr != null) {
        config = cfgMgr.getConfiguration();
      }
      if (config == null) {
        log.warn("No Marketo configuration found for resource {}", request.getRequestPathInfo().getSuffix());
        throw new RepositoryException("No Marketo configuration found for resource");
      }

      int currentValue = getCurrentValue(request);
      options = formCache.get(config).stream()
          .sorted((MarketoForm f1, MarketoForm f2) -> f1.getName().compareTo(f2.getName())).map(f -> {
            Map<String, Object> data = new HashMap<>();
            data.put("selected", currentValue == f.getId());
            data.put("value", f.getId());
            data.put("text", String.format("%s [%s] (%s)", f.getName(), f.getLocale(), f.getId()));
            return new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), "nt:unstructured",
                new ValueMapDecorator(data));
          }).collect(Collectors.toList());
      log.debug("Loaded {} options", options.size());
    } catch (RepositoryException | ExecutionException e) {
      log.warn("Failed to load Marketo forms", e);
      options = new ArrayList<>();
      Map<String, Object> data = new HashMap<>();
      data.put("value", "");
      data.put("text", "Unable to load forms from Marketo");
      options.add(new ValueMapResource(request.getResourceResolver(), new ResourceMetadata(), "nt:unstructured",
          new ValueMapDecorator(data)));
    }
    request.setAttribute(DataSource.class.getName(), new SimpleDataSource(options.iterator()));

  }

  private int getCurrentValue(SlingHttpServletRequest request) {
    Resource suffix = request.getRequestPathInfo().getSuffixResource();
    if (suffix != null) {
      return suffix.getValueMap().get("formId", -1);
    }
    return -1;
  }

}
