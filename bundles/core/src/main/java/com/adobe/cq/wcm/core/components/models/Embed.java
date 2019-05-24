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
 * Defines the {@code Embed} Sling Model used for the
 * {@code /apps/core/wcm/components/embed} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8
 */
@ConsumerType
public interface Embed extends ComponentExporter {

    /**
     * Retrieves the Embed mode value.
     *
     * @return the Embed mode value to be displayed, or {@code null} if no value
     *         can be returned
     * @since com.adobe.cq.wcm.core.components.models 12.8
     */
    default String getEmbedMode() {
	throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the HTML markup if HTML mode enabled.
     *
     * @return HTML markup string if HTML mode is enabled otherwise returns null
     * @since com.adobe.cq.wcm.core.components.models 12.8
     */
    default String getMarkup() {
	throw new UnsupportedOperationException();
    }

    /**
     * Retrieves the embed resource type if Embed mode enabled.
     *
     * @return The embed resource type if Embed mode is enabled. otherwise
     *         returns null
     * @since com.adobe.cq.wcm.core.components.models 12.8
     */
    default String getEmbedType() {
	throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.8
     */
    @NotNull
    @Override
    default String getExportedType() {
	throw new UnsupportedOperationException();
    }
}
