/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code ClientLibraries} Sling Model used to collect and include client libraries.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.14.0
 */
@ConsumerType
public interface ClientLibraries {

    /**
     * Name of the option used to specify the {@link java.util.Collection}
     * of resource types.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_RESOURCE_TYPES = "resourceTypes";

    /**
     * Name of the option used to specify the regular expression that
     * needs to be matched by client library categories to be collected.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_FILTER_REGEX = "filter";

    /**
     * Name of the option used to specify if client libraries should be searched up
     * the inheritance chain (using Sling resource supertype).
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_INHERITED = "inherited";

    /**
     * Default value for {@code OPTION_INHERITED}.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    boolean OPTION_INHERITED_DEFAULT = true;

    /**
     * Name of the HTL option used to inject the clientlib categories.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     *
     */
    String OPTION_CATEGORIES = "categories";

    /**
     * Name of the HTL option to inject the async attribute into the javascript script tag.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_ASYNC = "async";

    /**
     * Name of the HTL option to inject the defer attribute into the javascript script tag.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_DEFER= "defer";

    /**
     * Name of the HTL option to inject the crossorigin attribute into the javascript script tag.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_CROSSORIGIN= "crossorigin";

    /**
     * Name of the HTL option to inject the onload attribute into the javascript script tag.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_ONLOAD = "onload";

    /**
     * Name of the HTL option to inject the media attribute into the stylesheet link tag.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_MEDIA = "media";

    /**
     * Returns a concatenation of all the JS libraries defined for the requested resource.
     *
     * @return the inlined JS libraries
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getJsInline() {
        return null;
    }

    /**
     * Returns a concatenation of all the CSS libraries defined for the requested resource.
     *
     * @return the inlined CSS libraries
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getCssInline() {
        return null;
    }

    /**
     * Returns a concatenation of all the HTML JS script tags defined for the requested resource.
     *
     * @return the HTML JS script tags
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getJsIncludes() {
        return null;
    }

    /**
     * Returns a concatenation of all the HTML CSS link tags defined for the requested resource.
     *
     * @return the HTML CSS link tags
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getCssIncludes() {
        return null;
    }

    /**
     * Returns a concatenation of all the HTML JS script and CSS link tags defined for the requested resource.
     *
     * @return the HTML JS script and CSS link tags
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default String getJsAndCssIncludes() {
        return null;
    }

}
