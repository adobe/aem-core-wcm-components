/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.models;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code ExperienceFragment} Sling Model used for the
 * {@code /apps/core/wcm/components/experiencefragment} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
public interface ExperienceFragment extends ComponentExporter {

    /**
     * Name of the resource / configuration policy property that specifies the
     * experience fragment path variation. The property should provide a String
     * value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_FRAGMENT_PATH = "fragmentPath";

    /**
     * Returns the evaluated localized experience fragment path variation.
     *
     * @return Localized experience fragment variation path
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getExperienceFragmentVariationPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the configured experience fragment path variation.
     *
     * @return Configured experience fragment variation path
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getConfiguredExperienceFragmentPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
