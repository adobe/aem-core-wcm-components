/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.services.link;

import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.commons.link.LinkManager;

/**
 * A service that can process a given path. This service is used by the
 * {@link LinkManager} to build the final
 * {@link com.adobe.cq.wcm.core.components.commons.link.Link}. The path processor chain of the link manager can be extended by a custom
 * path processor which has to get a higher service ranking than the
 * {@link com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor}.
 *
 * @since com.adobe.cq.wcm.core.components.services.link 1.0.0
 */
@ConsumerType
public interface PathProcessor {

    /**
     * Checks if the given path should be handled by this processor.
     * @param path the path which should be processed
     * @param request the current request
     * @return {@code true} if the processor should handle the given path, otherwise {@code false} and the next path processor is applied by
     * the {@link LinkManager} if present
     */
    boolean accepts(@NotNull String path, @NotNull SlingHttpServletRequest request);

    /**
     * Sanitizes the given path by doing proper escaping and prepends the context path if needed.
     * @param path the path which needs to be sanitized
     * @param request the current request
     * @return the escaped absolute path with optional context path information
     */
    @NotNull String sanitize(@NotNull String path, @NotNull SlingHttpServletRequest request);

    /**
     * Applies mappings to the given path. Usually this is done with the {@link ResourceResolver#map(String)} method.
     * @param path the path which should be mapped
     * @param request the current request
     * @return the mapped path
     */
    @NotNull String map(@NotNull String path, @NotNull SlingHttpServletRequest request);

    /**
     * Externalizes the given path.
     * @param path the resource path
     * @param request the current request
     * @return the external link of the given path
     */
    @NotNull String externalize(@NotNull String path, @NotNull SlingHttpServletRequest request);


    /**
     * Processes the HTML attributes for the {@link LinkManager}
     * @param path the path of the linked resource
     * @param htmlAttributes the origin HTML attributes of the link
     * @return a map of the processed HTML attributes for the link
     */
    default @Nullable Map<String, String> processHtmlAttributes(@NotNull String path, @Nullable Map<String, String> htmlAttributes) {
        return htmlAttributes;
    };
}
