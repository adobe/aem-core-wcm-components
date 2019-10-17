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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.cq.wcm.core.components.models.marketo.FormValue;

/**
 * A Model for representing setting a form value in the Marketo form.
 */
@Model(adaptables = { Resource.class, SlingHttpServletRequest.class }, adapters = { FormValue.class })
public class FormValueImpl implements FormValue {

  @ValueMapValue
  private String name;

  @ValueMapValue
  private String source;

  @ValueMapValue
  private String value;

  @Override
  public String getName() {
    return StringEscapeUtils.escapeEcmaScript(name);
  }

  @Override
  public String getSource() {
    return source;
  }

  @Override
  public String getValue() {
    return StringEscapeUtils.escapeEcmaScript(value);
  }

}
