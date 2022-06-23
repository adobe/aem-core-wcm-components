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
 * Interface for a single item of the {@link Options} form element.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface OptionItem {

    /**
     * Returns {@code true} if item should be initially selected, otherwise {@code false}.
     *
     * @return {@code true} if item should be initially selected, otherwise {@code false}
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean isSelected() {
        return false;
    }

    /**
     * Returns {@code true} if item should be disabled and therefore not clickable, otherwise {@code false}.
     *
     * @return {@code true} if item should be disabled and therefore not clickable, otherwise {@code false}
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default boolean isDisabled() {
        return false;
    }

    /**
     * Returns the value of this item.
     *
     * @return the value of this item
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getValue() {
        return null;
    }

    /**
     * Return the text for this item.
     *
     * @return the text for this item
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default String getText() {
        return null;
    }
}
