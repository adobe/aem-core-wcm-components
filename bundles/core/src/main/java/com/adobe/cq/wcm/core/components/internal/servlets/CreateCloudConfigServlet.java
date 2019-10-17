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

import java.io.IOException;
import java.util.Collections;

import javax.annotation.Nonnull;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.post.HtmlResponse;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_POST,
    "sling.servlet.paths=/bin/core/createcloudconfig" })
public class CreateCloudConfigServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = -397622433323474345L;
  private static final Logger log = LoggerFactory.getLogger(CreateCloudConfigServlet.class);

  @Override
  protected void doPost(@Nonnull SlingHttpServletRequest request, @Nonnull SlingHttpServletResponse response)
      throws IOException {
    ResourceResolver resolver = request.getResourceResolver();
    PageManager pageManager = resolver.adaptTo(PageManager.class);

    HtmlResponse resp = new HtmlResponse();

    if (pageManager == null) {
      resp.setError(new IOException("Unable to get page manager"));
    } else {

      String configPath = getParam(request, "configPath");
      Resource cloudConfigFolder = ResourceUtil.getOrCreateResource(resolver, configPath + "/settings/cloudconfigs",
          Collections.singletonMap(JcrConstants.JCR_PRIMARYTYPE, JcrResourceConstants.NT_SLING_FOLDER),
          JcrResourceConstants.NT_SLING_FOLDER, false);
      log.debug("Creating Cloud Config in: {}", cloudConfigFolder);
      
      resp.setParentLocation(cloudConfigFolder.getPath());
      
      // create a new page
      Page page;
      try {
        page = pageManager.create(cloudConfigFolder.getPath(), getParam(request, "name"), getParam(request, "template"),
            getParam(request, "title"));
        resp.setPath(page.getPath());
        resp.setLocation(page.getPath());
        resp.setStatus(200, "Created Cloud Configuration");
        log.debug("Created configuration: {}", page.getPath());
        resolver.commit();
      } catch (WCMException e) {
        resp.setError(e);
      }

    }
    response.setContentType("text/plain");
    resp.send(response, true);

  }

  private String getParam(SlingHttpServletRequest request, String param) throws IOException {
    String value = request.getParameter(param);
    if (StringUtils.isBlank(value)) {
      throw new IOException("Parameter " + param + " must not be blank");
    } else {
      log.debug("Loaded {} for parameter {}", value, param);
    }
    return value;
  }

}
