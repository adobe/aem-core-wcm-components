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
 * Container object for a Marketo REST API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketoResponse<T> {

  private MarketoError[] errors;
  private T[] result;
  private boolean success;

  public MarketoError[] getErrors() {
    return errors != null ? errors.clone() : null;
  }

  public T[] getResult() {
    return result != null ? result.clone() : null;
  }

  public boolean isSuccess() {
    return success;
  }

  public void setErrors(MarketoError[] errors) {
    this.errors = errors.clone();
  }

  public void setResult(T[] result) {
    this.result = result.clone();
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

}
