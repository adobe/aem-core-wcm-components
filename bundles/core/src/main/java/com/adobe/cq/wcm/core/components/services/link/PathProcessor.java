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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * A service that can process a given path. This service is used by the
 * {@link com.adobe.cq.wcm.core.components.internal.link.LinkHandler} to build the final
 * {@link com.adobe.cq.wcm.core.components.commons.link.Link}. The path processor chain of the Link Handler can be extended by a custom
 * path processor which has to get a higher service ranking than the
 * {@link com.adobe.cq.wcm.core.components.internal.link.DefaultPathProcessor}.
 *
 * @since com.adobe.cq.wcm.core.components.services.link 1.0.0
 */
@ConsumerType
public interface PathProcessor {

    /**
     * Checks if the current processor can handle the requested path
     * @param path the path which should be processed
     * @param request the current request
     * @return {@code true} if the processor can handle the request, otherwise {@code false} and the next path processor is used by the
     * {@link com.adobe.cq.wcm.core.components.internal.link.LinkHandler}
     */
    boolean canHandle(String path, SlingHttpServletRequest request);

    /**
     * Path is prefixed with the context path and escaped
     * @param path the path which needs to be checked /fixed.
     * @param request the current request
     * @return the escaped absolute URL path with context path
     */
    @NotNull String fixPath(String path, SlingHttpServletRequest request);

    /**
     * Map the fixed path to the internal resource. Usually this is done be the {@link ResourceResolver#map(String)} method.
     * @param path the resource path
     * @param request the current request
     * @return the mapped path
     */
    @NotNull String mapPath(String path, SlingHttpServletRequest request);

    /**
     * Externalize the given mapped path.
     * @param path the resource path
     * @param request the current request
     * @return the external link of the given path
     */
    @NotNull String externalizeLink(@NotNull String path, @NotNull SlingHttpServletRequest request);
}