/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import com.adobe.cq.export.json.ComponentExporter;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code TableOfContent} Sling Model used for the {@code /apps/core/wcm/components/tableOfContent} component.
 *
 * @since com.adobe.cq.wcm.core.components.models
 */
@ConsumerType
public interface TableOfContent extends ComponentExporter {

    /**
     * Checks if the this text field is mandatory.
     *
     * @return {@code true} if the field must have a input, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean isRequired() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the message to be displayed if the field is mandatory.
     *
     * @return the message to be displayed if the field is mandatory but has not been filled by the user
     * @see #isRequired()
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getRequiredMessage() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of the {@code placeholder} HTML attribute.
     *
     * @return the value of placeholder attribute
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getPlaceholder() {
        throw new UnsupportedOperationException();
    }


    /**
     * Returns the Table of Content title.
     *
     * @return the button text
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getTitle() {
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
