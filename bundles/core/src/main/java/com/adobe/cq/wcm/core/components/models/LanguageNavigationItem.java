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

import java.util.Locale;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Interface for a single language navigation item, used by the {@link LanguageNavigation} model.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
@ConsumerType
public interface LanguageNavigationItem extends NavigationItem {

    /**
     * Returns the locale of this {@code LanguageNavigationItem}.
     *
     * @return the locale of the language navigation item
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default Locale getLocale() {
        return null;
    }

    /**
     * Returns the country of this {@code LanguageNavigationItem}.
     *
     * @return the country of the language navigation item
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default String getCountry() {
        return null;
    }

    /**
     * Returns the language of this {@code LanguageNavigationItem}.
     *
     * @return the language of the language navigation item
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default String getLanguage() {
        return null;
    }

}
