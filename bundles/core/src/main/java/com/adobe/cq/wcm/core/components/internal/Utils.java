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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.foundation.AllowedComponentList;

public class Utils {

    /**
     * Name of the separator character used between prefix and hash when generating an ID, e.g. image-5c7e0ef90d
     */
    public static final String ID_SEPARATOR = "-";

    /**
     * Name of the subservice used to authenticate as in order to be able to read details about components and
     * client libraries.
     */
    public static final String COMPONENTS_SERVICE = "components-service";

    private Utils() {
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
        return StringUtils.isEmpty(vanityURL) ? (request.getContextPath() + page.getPath() + ".html"): (request.getContextPath() + vanityURL);
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

    /**
     * Returns an ID based on the prefix, the ID_SEPARATOR and a hash of the path, e.g. image-5c7e0ef90d
     *
     * @param prefix the prefix for the ID
     * @param path   the resource path
     * @return the generated ID
     */
    public static String generateId(String prefix, String path) {
        return StringUtils.join(prefix, ID_SEPARATOR, StringUtils.substring(DigestUtils.sha256Hex(path), 0, 10));
    }

    public static Set<String> getPageResourceTypes(@NotNull Page page, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.addAll(getResourceTypes(page.getContentResource(), request, modelFactory));
        resourceTypes.addAll(getTemplateResourceTypes(page, request, modelFactory));
        return resourceTypes;
    }

    @NotNull
    public static Set<String> getResourceTypes(@NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.add(resource.getResourceType());
        //resourceTypes.addAll(getSuperTypes(resource.getResourceType(), resolver));
        resourceTypes.addAll(getXFResourceTypes(resource, request, modelFactory));
        for (Resource child : resource.getChildren()) {
            //TODO: check it's a cq:Component, used to be allowed node (filtered out by regex)
            resourceTypes.addAll(getResourceTypes(child, request, modelFactory));
        }
        return resourceTypes;
    }

    public static Set<String> getXFResourceTypes(@NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        ExperienceFragment experienceFragment = modelFactory.getModelFromWrappedRequest(request, resource, ExperienceFragment.class);
        if (experienceFragment != null) {
            String fragmentPath = experienceFragment.getLocalizedFragmentVariationPath();
            if (StringUtils.isNotEmpty(fragmentPath)) {
                ResourceResolver resolver = resource.getResourceResolver();
                if (resolver != null) {
                    Resource fragmentResource = resolver.getResource(fragmentPath);
                    if (fragmentResource != null) {
                        return getResourceTypes(fragmentResource, request, modelFactory);
                    }
                }
            }
        }
        return Collections.emptySet();
    }

    public static Set<String> getTemplateResourceTypes(@NotNull Page page, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Template template = page.getTemplate();
        if (template != null) {
            String templatePath = template.getPath() + AllowedComponentList.STRUCTURE_JCR_CONTENT;
            ResourceResolver resolver = page.getContentResource().getResourceResolver();
            if (resolver != null) {
                Resource templateResource = resolver.getResource(templatePath);
                if (templateResource != null) {
                    return getResourceTypes(templateResource, request, modelFactory);
                }
            }
        }
        return Collections.emptySet();
    }

    @NotNull
    public static Set<String> getSuperTypes(@NotNull String resourceType, @NotNull ResourceResolver resolver) {
        Set<String> superTypes = new HashSet<>();
        Resource resource;
        while ((resource = resolver.getResource(resourceType)) != null) {
            resourceType = resource.getResourceSuperType();
            if (resourceType == null ||
                    !superTypes.add(resourceType)) { // avoid infinite loops
                break;
            }
        }
        return superTypes;
    }

    @Nullable
    public static ResourceResolver getComponentsResolver(ResourceResolverFactory resolverFactory) {
        try {
            return resolverFactory.getServiceResourceResolver(
                    Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, COMPONENTS_SERVICE));
        } catch (LoginException e) {
            return null;
        }
    }
}
