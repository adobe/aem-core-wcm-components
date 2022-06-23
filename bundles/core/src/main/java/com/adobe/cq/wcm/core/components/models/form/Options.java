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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the form {@code Options} Sling Model used for the {@code /apps/core/wcm/components/form/options} component.
 *
 * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
 */
@ConsumerType
public interface Options extends Field {

    /**
     * Defines the Options type. Possible values: {@code checkbox}, {@code radio}, {@code drop-down}, {@code multi-drop-down}.
     *
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
     */
    enum Type {
        CHECKBOX("checkbox"),
        RADIO("radio"),
        DROP_DOWN("drop-down"),
        MULTI_DROP_DOWN("multi-drop-down");

        private String value;

        Type(String value) {
            this.value = value;
        }

        /**
         * Given a {@link String} <code>value</code>, this method returns the enum's value that corresponds to the provided string
         * representation. If no representation is found, {@link #CHECKBOX} will be returned.
         *
         * @param value the string representation for which an enum value should be returned
         * @return the corresponding enum value, if one was found, or {@link #CHECKBOX}
         * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
         */
        public static Type fromString(String value) {
            for (Type type : Type.values()) {
                if (StringUtils.equals(value, type.value)) {
                    return type;
                }
            }
            return CHECKBOX;
        }

        /**
         * Returns the string value of this enum constant.
         *
         * @return the string value of this enum constant
         * @since com.adobe.cq.wcm.core.components.models.form 13.0.0
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Returns the list of all the options.
     *
     * @return the list of all the options
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default List<OptionItem> getItems() {
        return null;
    }

    /**
     * Returns the type of the options element.
     *
     * @return the type of the options element
     * <br>
     * Possible values: {@code checkbox}, {@code radio}, {@code drop-down}, {@code multi-drop-down}
     * @since com.adobe.cq.wcm.core.components.models.form 13.0.0; marked <code>default</code> in 14.1.0
     */
    default Type getType() {
        return null;
    }

}
