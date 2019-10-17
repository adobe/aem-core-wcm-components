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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.marketo.FormValue;
import com.adobe.cq.wcm.core.components.models.marketo.MarketoForm;
import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.WCMMode;

/**
 * Model for retrieving the configuration values for the Marketo form component
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, adapters = {
    MarketoForm.class })
public class MarketoFormImpl implements MarketoForm {

  private static final Logger log = LoggerFactory.getLogger(MarketoFormImpl.class);

  @ValueMapValue
  @Via("resource")
  private String formId;

  @ChildResource
  @Via("resource")
  private List<FormValue> hidden;

  private SlingHttpServletRequest request;

  @ValueMapValue
  @Via("resource")
  private String script;

  @ValueMapValue
  @Via("resource")
  private String successUrl;

  @ChildResource
  @Via("resource")
  private List<FormValue> values;

  public MarketoFormImpl(SlingHttpServletRequest request) {
    this.request = request;
  }

  @Override
  public String getFormId() {
    return formId;
  }

  @Override
  public String getHidden() {
    if (hidden == null || hidden.isEmpty()) {
      return null;
    }
    String assignments = hidden.stream().map(v -> toJavaScript(v, "hidden")).collect(Collectors.joining());
    return "var hidden = {};\n" + assignments + "form.addHiddenFields(hidden);\n";
  }

  private String toJavaScript(FormValue value, String variable) {
    String key = StringEscapeUtils.escapeJavaScript(value.getName());
    String val = StringEscapeUtils.escapeJavaScript(value.getValue());
    if ("static".equals(value.getSource())) {
      return String.format("%s[\"%s\"]=\"%s\";%n", variable, key, val);
    } else if ("contextHub".equals(value.getSource())) {
      return String.format("%s[\"%s\"]=ContextHub.getItem(\"%s\");%n", variable, key, val);
    } else if ("jsVariable".equals(value.getSource())) {
      return String.format("%s[\"%s\"]=%s;%n", variable, key, value.getValue());
    } else {
      return String.format("%s[\"%s\"]=\"%s\";%n", variable, key,
          StringEscapeUtils.escapeJavaScript(request.getParameter(value.getValue())));
    }

  }

  @Override
  public String getScript() {
    return script;
  }

  @Override
  public String getSuccessUrl() {
    if (StringUtils.isBlank(successUrl)) {
      return null;
    }
    String fullUrl = successUrl;
    Externalizer externalizer = request.getResourceResolver().adaptTo(Externalizer.class);
    if (successUrl.startsWith("/") && externalizer != null) {
      fullUrl = externalizer.relativeLink(request, successUrl);
      log.debug("Externalized {} to {}", successUrl, fullUrl);
    }
    if (!successUrl.contains(".")) {
      fullUrl += ".html";
    }
    log.debug("Final URL: {}", fullUrl);

    return "form.onSuccess(function(values, followUpUrl) {\n" + "location.href = \""
        + StringEscapeUtils.escapeJavaScript(fullUrl) + "\";\n" + "return false;\n" + "});";

  }

  @Override
  public String getValues() {
    if (values == null || values.isEmpty()) {
      return null;
    }
    String assignments = values.stream().map(v -> toJavaScript(v, "values")).collect(Collectors.joining());
    return "var values = {};\n" + assignments + "form.vals(values);\n";
  }

  @Override
  public boolean isEdit() {
    return WCMMode.fromRequest(request) == WCMMode.EDIT;
  }

}
