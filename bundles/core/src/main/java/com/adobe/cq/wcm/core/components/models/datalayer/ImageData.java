/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

/**
 * Interface defining data for image components.
 *
 * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
 */
public interface ImageData extends ComponentData {

    /**
     * Returns the asset associated with the image.
     *
     * @return Asset data model
     *
     * @since com.adobe.cq.wcm.core.components.models.datalayer 1.0.0
     */
    @JsonProperty("image")
    default AssetData getAssetData() {
        return null;
    }
}
