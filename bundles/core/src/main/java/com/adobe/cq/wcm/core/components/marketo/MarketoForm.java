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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a form from the Marketo API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketoForm {

  private int id;

  private String locale;

  private String name;

  public int getId() {
    return id;
  }
  public String getLocale() {
    return locale;
  }
  public String getName() {
    return name;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "MarketoForm [id=" + getId() + ", locale=" + getLocale() + ", name=" + getName() + "]";
  }

}
