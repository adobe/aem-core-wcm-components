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
 * - scripts
 * - links
 * - meta
 * This configuration is meant to be used as a context-aware resource.
 * See <a href="https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html#context-aware-resources">Context-Aware Resources</a>
 * <p>
 * The JCR node structure depends on the provided Persistence Strategy.
 * See https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration-spi.html#configuration-persistence-strategy-1
 *
 * Assumed structure (default AEM Setup):
 *
 * <pre>
 *      com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
 *          + jcr:content
 *              - prefixPath="/some/path"
 *              + items
 *                  + item01
 *                      - element=["link"|"script"|"meta"]
 *                      - location=["header"|"footer"]
 *                      + attributes
 *                          + attribute01
 *                              - name="attribute01Name"
 *                              - value="attribute01Value"
 *                          + attribute02
 *                              - name="attribute02Name"
 *                              - value="attribute02Value"
 *                              ...
 *                  + item02
 *                      ...
 *                  ...
 *  </pre>
 *
 *  The structure of the initial implementation is deprecated but still supported:
 *
 *  <pre>
 *      com.adobe.cq.wcm.core.components.config.HtmlPageItemsConfig
 *          - prefixPath="/some/path"
 *          + item01
 *              - element=["link"|"script"|"meta"]
 *              - location=["header"|"footer"]
 *              + attributes
 *                  - attributeName01="attributeValue01"
 *                  - attributeName02="attributeValue02"
 *              ...
 *          + item02
 *              ...
 *          ...
 *  </pre>
 */
@Configuration(label = "Page Items", description = "Context-Aware Configuration for items that will be included in the page")
public @interface HtmlPageItemsConfig {

    /**
     * Name of the property that stores the path that will be prefixed to all href's and src's
     *
     * @since com.adobe.cq.wcm.core.components.config 1.0.0
     */
    String PN_PREFIX_PATH = "prefixPath";

    /**
     * Returns the path that will be prefixed to all href's and src's
     *
     * @return The prefix path
     * @since com.adobe.cq.wcm.core.components.config 1.0.0
     */
    @Property(label = "Prefix path")
    String prefixPath() default "";

    /**
     * Returns the items to render.
     *
     * @return The array of items to render
     * @since com.adobe.cq.wcm.core.components.config 2.0.0
     */
    @Property(label = "Items")
    HtmlPageItemConfig[] items() default {};
}
