/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.Nonnull;

/**
 * Defines the {@code Image} Sling Model used for the {@code /apps/core/wcm/sandbox/components/image} component.
 */
@ConsumerType
public interface Image extends com.adobe.cq.wcm.core.components.models.Image, ComponentExporter {

    @Override
    @JsonIgnore
    default String getJson() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns the available image sizes (expressed in width).
     *
     * @return the available image sizes (expressed in width)
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default int[] getSmartSizes() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the URLs for the available image renditions.
     *
     * @return the URLs for the available image renditions
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default String[] getSmartImages() {
        throw new UnsupportedOperationException();
    }

    /**
     * Indicates if the image should be rendered lazily or not.
     *
     * @return true if the image should be rendered lazily; false otherwise
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.3.0
     */
    default boolean isLazyEnabled() {
        throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    default String getFileReference() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
