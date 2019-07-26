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
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code ExperienceFragment} Sling Model used for the
 * {@code /apps/core/wcm/components/experiencefragment} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.9.0
 */
@ConsumerType
public interface ExperienceFragment extends ComponentExporter {

    /**
     * Name of the configuration policy property that specifies the experience fragment variation path. . The property
     * should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_FRAGMENT_PATH = "fragmentPath";

    /**
     * Name of the configuration policy property that defines the depth of the global localization structure in the
     * content tree relative to the localization root. The property should provide a Long value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_LOCALIZATION_ROOT = "localizationRoot";

    /**
     * Name of the configuration policy property that defines the localization root from which to build the global
     * localization structure. The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    String PN_LOCALIZATION_DEPTH = "localizationDepth";

    /**
     * Returns the configured path of the experience fragment variation.
     *
     * @return Configured experience fragment variation path
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    default String getFragmentPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the localized path of the experience fragment variation.
     *
     * If the localization properties (localization root and depth) are not defined, it returns the fragment path.
     * Otherwise it computes the localization string in the fragment path from the the localization properties (e.g. us/en)
     * and replaces it with the one from the page.
     *
     * @return Localized experience fragment variation path
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    default String getLocalizedFragmentPath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.9.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
