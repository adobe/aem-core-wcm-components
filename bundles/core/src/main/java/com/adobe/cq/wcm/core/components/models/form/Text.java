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
package com.adobe.cq.wcm.core.components.models.form;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the form {@code Text} Sling Model used for the {@code /apps/core/wcm/components/form/text} component.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface Text extends Field {

    /**
     * Checks if the this text field is mandatory.
     *
     * @return {@code true} if the field must have a input, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean isRequired() {
        return false;
    }

    /**
     * Returns the message to be displayed if the field is mandatory.
     *
     * @return the message to be displayed if the field is mandatory but has not been filled by the user
     * @see #isRequired()
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getRequiredMessage() {
        return null;
    }

    /**
     * Returns the value of the {@code placeholder} HTML attribute.
     *
     * @return the value of placeholder attribute
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getPlaceholder() {
        return null;
    }

    /**
     * Checks if the field should be rendered read only.
     *
     * @return {@code true} if the field should be read-only, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean isReadOnly() {
        return false;
    }

    /**
     * Returns the message to be displayed when the constraint specified by {@link #getType()} is not fulfilled.
     *
     * @return the message to be displayed when the constraint specified by {@link #getType()} is not fulfilled
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getConstraintMessage() {
        return null;
    }

    /**
     * Returns the type of the input field such as {@code text}, {@code textarea}, {@code date}, {@code email} etc.
     * The types other than {@code textarea} are defined by the HTML5 standard.
     *
     * @return the type of the field
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getType() {
        return null;
    }

    /**
     * Returns the number of rows to display in case this item's type is a {@code textarea}.
     *
     * @return the number of rows the {@code textarea} should display
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default int getRows() {
        return 0;
    }

    /**
     * Checks if the title (label of the field) should be visually hidden; this is required if the title is mandatory only for
     * accessibility purposes.
     *
     * @return {@code true} if the title should remain hidden, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean hideTitle() {
        return false;
    }
}
