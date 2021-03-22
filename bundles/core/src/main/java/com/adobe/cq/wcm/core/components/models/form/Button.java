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
 * Defines the {@code Button} Sling Model used for the {@code /apps/core/wcm/components/button} component.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface Button extends Field {

    /**
     * Defines button type.
     *
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
     */
    enum Type {
        /**
         * Button type used to submit forms.
         *
         * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
         */
        SUBMIT("submit"),

        /**
         * Normal button.
         *
         * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
         */
        BUTTON("button");

        private String value;

        Type(String value) {
            this.value = value;
        }

        /**
         * Given a {@link String} <code>value</code>, this method returns the enum's value that corresponds to the provided string
         * representation. If no representation is found, {@link #SUBMIT} will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link #SUBMIT}
         * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
         */
        public static Type fromString(String value) {
            for (Type type : values()) {
                if (type.value.equals(value)) {
                    return type;
                }
            }
            return SUBMIT;
        }
    }

    /**
     * Returns this button's type.
     *
     * @return the type of the button; possible values: 'button', 'submit'
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default Type getType() {
        return null;
    }

}
