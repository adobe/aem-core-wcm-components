/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * Configuration factory for the adaptive image servlet mapping. Allows multiple mappings for the servlet, based on combinations
 * of resource types, selectors and extensions.
 */
@Designate(
        factory = true,
        ocd = AdaptiveImageServletMappingConfigurationFactory.Config.class
        )
@Component(
        service = AdaptiveImageServletMappingConfigurationFactory.class,
        configurationPolicy = org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE
)
public class AdaptiveImageServletMappingConfigurationFactory {

    @ObjectClassDefinition(
            name ="AEM Core WCM Components Adaptive Image Servlet Mapping Configuration",
            description="Configuration for the adaptive image servlet mapping."
    )
    @interface Config {

        @AttributeDefinition(
                name = "Resource types",
                description = "List of resource types for which the adaptive image servlet should be enabled."
        )
        String[] resource_types() default {};

        @AttributeDefinition(
                name = "Selectors",
                description = "List of selectors for which the adaptive image servlet should be enabled."
        )
        String[] selectors() default {};

        @AttributeDefinition(
                name = "File extensions",
                description = "List of file extensions for which the adaptive image servlet should be enabled."
        )
        String[] extensions() default {};

        @AttributeDefinition(
                name = "Default resize width",
                description = "In case the requested image contains no width information in the request and the image also doesn't have " +
                        "a content policy that defines the allowed rendition widths, then the image processed by this server will be" +
                        " resized to this configured width, for images whose width is larger than this value."
        )
        int defaultResizeWidth() default AdaptiveImageServlet.DEFAULT_RESIZE_WIDTH;

        @AttributeDefinition(
                name = "Maximum processed image size",
                description = "In case the source image is larger on either dimensions than this size, the servlet will refuse to process " +
                        "it and will throw an exception, to avoid running out of memory."
        )
        int maxSize() default AdaptiveImageServlet.DEFAULT_MAX_SIZE;

    }

    private List<String> resourceTypes;

    private List<String> selectors;

    private List<String> extensions;

    private int defaultResizeWidth;

    private int maxSize;

    /**
     * Invoked when a configuration is created or modified.
     *
     * @param config The created or updated configuration
     */
    @Activate
    @Modified
    void configure(Config config) {
        resourceTypes = getValues(config.resource_types());
        selectors = getValues(config.selectors());
        extensions = getValues(config.extensions());
        defaultResizeWidth = config.defaultResizeWidth();
        maxSize = config.maxSize();
    }

    /**
     * Getter for resource types.
     *
     * @return {@link List} of resource types
     */
    @NotNull
    public List<String> getResourceTypes() {
        return Collections.unmodifiableList(this.resourceTypes);
    }

    /**
     * Getter for selectors.
     *
     * @return {@link List} of selectors
     */
    @NotNull
    public List<String> getSelectors() {
        return Collections.unmodifiableList(this.selectors);
    }

    /**
     * Getter for extensions.
     *
     * @return {@link List} of extensions
     */
    @NotNull
    public List<String> getExtensions() {
        return Collections.unmodifiableList(this.extensions);
    }

    /**
     * Returns the default resize width that the {@link AdaptiveImageServlet} will use for resizing images that don't provide a width
     * information in their request, nor does a content policy exist for them.
     * @return
     */
    public int getDefaultResizeWidth() {
        return defaultResizeWidth;
    }

    /**
     * Returns the maximum image width that the {@link AdaptiveImageServlet} will process.
     * @return
     */
    public int getMaxSize() {
        return maxSize;
    }

    /**
     * Internal helper for filtering out null and empty values from the configuration options.
     *
     * @param config - Array of strings which might include null or empty values
     *
     * @return - {@link List} of strings with no empty values; might be empty.
     */
    @NotNull
    private List<String> getValues(@NotNull String[] config) {
        List<String> values = new ArrayList<>(config.length);
        for (String conf : config) {
            if (StringUtils.isNotEmpty(conf)) {
                values.add(StringUtils.trim(conf));
            }
        }
        return values;
    }

    @Override
    public String toString() {
        return "{resourceTypes: " + resourceTypes.toString() + ", selectors: " + selectors.toString() + ", extensions: " + extensions
                .toString() + ", defaultResizeWidth: " + defaultResizeWidth + "}";
    }
}
