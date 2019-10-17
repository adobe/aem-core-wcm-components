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
package com.adobe.cq.wcm.core.components.models.marketo;

/**
 * Model for retrieving the configuration values for the Marketo form component
 */
public interface MarketoForm {

  default String getFormId() {
    throw new UnsupportedOperationException();
  }

  default String getHidden() {
    throw new UnsupportedOperationException();
  }

  default String getScript() {
    throw new UnsupportedOperationException();
  }

  default String getSuccessUrl() {
    throw new UnsupportedOperationException();
  }

  default String getValues() {
    throw new UnsupportedOperationException();
  }

  default boolean isEdit() {
    throw new UnsupportedOperationException();
  }

}
