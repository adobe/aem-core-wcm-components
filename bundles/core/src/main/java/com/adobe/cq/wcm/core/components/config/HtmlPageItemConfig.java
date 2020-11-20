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

import org.apache.sling.caconfig.annotation.Property;

/**
 * Context-aware configuration holding information on an item to be included in pages:
 * - scripts
 * - links
 * - meta
 * This configuration is meant to be used as a context-aware resource.
 * See <a href="https://sling.apache.org/documentation/bundles/context-aware-configuration/context-aware-configuration.html#context-aware-resources">Context-Aware Resources</a>
 */
public @interface HtmlPageItemConfig {
    /**
     * Returns the type of element that should be rendered.
     *
     * @return The type of element to render
     * @since com.adobe.cq.wcm.core.components.config 2.0.0
     */
    @Property(label = "Element", description = "The type of element that should be rendered: Either 'link', 'script' or 'meta'.", property = {
        "widgetType=dropdown",
        "dropdownOptions=["
            + "{'value':'link','description':'<link>'},"
            + "{'value':'script','description':'<script>'},"
            + "{'value':'meta','description':'<meta>'}"
            + "]"
    })
    String element();

    /**
     * Returns the location of where the element should be rendered.
     *
     * @return The location where to render the element
     * @since com.adobe.cq.wcm.core.components.config 2.0.0
     */
    @Property(label = "Location", description = "The location where to render the element: Either in the header or in the footer.", property = {
        "widgetType=dropdown",
        "dropdownOptions=["
            + "{'value':'header','description':'Header'},"
            + "{'value':'footer','description':'Footer'}"
            + "]"
    })
    String location();

    /**
     * The attributes to render as part of the element.
     *
     * @return The attributes of the element to render
     * @since com.adobe.cq.wcm.core.components.config 2.0.0
     */
    @Property(label = "Attributes")
    AttributeConfig[] attributes() default {};
}
