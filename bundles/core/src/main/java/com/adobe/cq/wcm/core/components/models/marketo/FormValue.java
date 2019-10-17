/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2019 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.cq.wcm.core.components.models.marketo;

/**
 * A Model representing setting a form value in the Marketo form.
 */
public interface FormValue {


  default String getName() {
    throw new UnsupportedOperationException();
  }

  default String getSource() {
    throw new UnsupportedOperationException();
  }

  default String getValue() {
    throw new UnsupportedOperationException();
  }

}
