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

import java.util.List;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * This utility model allows looking up the paths of specific files
 * from a collection of components, specified as Sling resource types.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.14.0
 */
@ConsumerType
public interface ComponentFiles {

    /**
     * Name of the option used to specify the {@link java.util.Collection}
     * of resource types.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_RESOURCE_TYPES = "resourceTypes";

    /**
     * Name of the option used to specify the regular expression that
     * needs to be matched by files to be collected.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    String OPTION_FILTER_REGEX = "filter";

    /**
     * Name of the option used to specify if files should be searched up
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
     * Returns a list of paths for files inside the components (specified
     * by the resource types) that match the given filter regex.
     *
     * @return List of file paths
     * @since com.adobe.cq.wcm.core.components.models 12.14.0
     */
    default List<String> getPaths() {
        return null;
    }
}
