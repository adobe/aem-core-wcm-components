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
package com.adobe.cq.wcm.core.components.models.datalayer;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface ContentFragmentData extends ComponentData {

    /**
     * Returns the elements data associated with the content fragment.
     *
     * @return array of element data
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("elements")
    default ElementData[] getElementsData() {
        return null;
    }

    interface ElementData {

        /**
         * Returns the title of the content fragment element
         *
         * @return Element title
         *
         * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
         */
        @JsonProperty("xdm:title")
        default String getTitle() {
            return null;
        }

        /**
         * Returns the value of content fragment element as string
         *
         * @return Element value as string
         *
         * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
         */
        @JsonProperty("xdm:text")
        default String getText() {
            return null;
        }
    }
}
