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
package com.adobe.cq.wcm.core.components.config;

import org.apache.sling.caconfig.annotation.Configuration;
import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-aware configuration holding information on items to be included in pages:
 *      - scripts
 *      - links
 *      - meta
 *  This configuration is meant to be used as a context-aware resource.
 *  See <a href="https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html#context-aware-resources">Context-Aware Resources</a>
 *
 *  Assumed structure:
 *
 *  <pre>
 *      com.adobe.cq.wcm.core.components.config.PageItemsConfig
 *          - prefixPath="/some/path"
 *          + item01
 *              - element=["link"|"script"|"meta"]
 *              - location=["header"|"footer"]
 *              - attributeName01="attributeValue01"
 *              - attributeName02="attributeValue02"
 *              ...
 *          + item02
 *              ...
 *          ...
 *  </pre>
 */
@Configuration(label = "Page Items", description = "Context-Aware Configuration for items that will be included in the page")
public @interface PageItemsConfig {

    /**
     * Name of the property that store the prefix path
     *
     * @since com.adobe.cq.wcm.core.components.config 1.0.0
     */
    String PROP_PREFIX_PATH = "prefixPath";

    /**
     * Returns the path that will be prefixed to all href's and src's
     *
     * @return The prefix path
     *
     * @since com.adobe.cq.wcm.core.components.config 1.0.0
     */
    @Property(label = "Prefix path")
    String prefixPath() default "";
}
