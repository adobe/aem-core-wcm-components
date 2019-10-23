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
package com.adobe.cq.wcm.core.components.internal;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

import java.util.Set;

public class Utils {

    private Utils() {
    }

    /**
     * Resolves the resource at the given path. Supports relative and absolute paths.
     * @param resolver Provides search paths used to turn relative paths to full paths and resolves the resource.
     * @param path The path of the resource to resolve.
     * @return The resource of the given path.
     */
    public static Resource resolveResource(ResourceResolver resolver, String path) {

        // Resolve absolute resource path.
        if (path.startsWith("/")) {
            return resolver.getResource(path);
        }

        // Resolve relative resource path.
        for (String searchPath : resolver.getSearchPath()) {

            Resource resource = resolver.getResource(searchPath + path);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    /**
     * Retrieves the resource types of the given resource and all of its child resources.
     * @param resource The resource to start retrieving resources types from.
     * @param resourceTypes String set to append resource type values to.
     * @return String set of resource type values found.
     */
    public static Set<String> getResourceTypes(Resource resource, String resourceTypeRegex, Set<String> resourceTypes) {

        if (resource == null) {
            return resourceTypes;
        }

        // Add resource type to return set if allowed by the resource type regex.
        String resourceType = resource.getResourceType();
        if (StringUtils.isBlank(resourceTypeRegex)
            || resourceType.matches(resourceTypeRegex)) {
            resourceTypes.add(resourceType);
        }

        // Iterate through the resource's children and recurse through them for resource types.
        for (Resource child : resource.getChildren()) {
            getResourceTypes(child, resourceTypeRegex, resourceTypes);
        }

        return resourceTypes;
    }

    /**
     * If the provided {@code path} identifies a {@link Page}, this method will generate the correct URL for the page. Otherwise the
     * original {@code String} is returned.
     *
     * @param request     the current request, used to determine the server's context path
     * @param pageManager the page manager
     * @param path        the page path
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a
     * {@link Page}
     */
    @NotNull
    public static String getURL(@NotNull SlingHttpServletRequest request, @NotNull PageManager pageManager, @NotNull String path) {
        Page page = pageManager.getPage(path);
        if (page != null) {
            return getURL(request, page);
        }
        return path;
    }

    /**
     * Given a {@link Page}, this method returns the correct URL, taking into account that the provided {@code page} might provide a
     * vanity URL.
     *
     * @param request the current request, used to determine the server's context path
     * @param page    the page
     * @return the URL of the page identified by the provided {@code path}, or the original {@code path} if this doesn't identify a
     * {@link Page}
     */
    @NotNull
    public static String getURL(@NotNull SlingHttpServletRequest request, @NotNull Page page) {
        String vanityURL = page.getVanityUrl();
        return StringUtils.isEmpty(vanityURL) ? request.getContextPath() + page.getPath() + ".html" : request.getContextPath() + vanityURL;
    }

    public enum Heading {

        H1("h1"),
        H2("h2"),
        H3("h3"),
        H4("h4"),
        H5("h5"),
        H6("h6");

        private String element;

        Heading(String element) {
            this.element = element;
        }

        public static Heading getHeading(String value) {
            for (Heading heading : values()) {
                if (StringUtils.equalsIgnoreCase(heading.element, value)) {
                    return heading;
                }
            }
            return null;
        }

        public String getElement() {
            return element;
        }
    }


}
