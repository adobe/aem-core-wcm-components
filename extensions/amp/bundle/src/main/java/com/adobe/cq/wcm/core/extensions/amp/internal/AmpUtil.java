/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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
package com.adobe.cq.wcm.core.extensions.amp.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * Provides common value constants and methods used across AMP services.
 */
public class AmpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(AmpUtil.class);

    private static final String AMP_MODE_PROP = "ampMode";

    public static final String AMP_ONLY = "ampOnly";

    static final String NO_AMP = "noAmp";

    public static final String PAIRED_AMP = "pairedAmp";

    public static final String AMP_SELECTOR = "amp";

    public static final String DOT = ".";

    private static final String TEMPLATE_STRUCTURE_CONTENT_PATH = "/structure/jcr:content";

    public static final String CLIENTLIB_SUBSERVICE = "component-clientlib-service";

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
            String templatePath = template.getPath() + TEMPLATE_STRUCTURE_CONTENT_PATH;
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

    /**
     * Retrieves the AMP mode value of the requested resource.
     * @param slingRequest Request used to resolve the resource and AMP mode value from.
     * @return The AMP mode value.
     */
    public static String getAmpMode(@NotNull SlingHttpServletRequest slingRequest) {

        PageManager pageManager = slingRequest.getResourceResolver().adaptTo(PageManager.class);

        if (pageManager == null) {
            LOG.debug("Can't resolve page manager. Falling back to content policy AMP mode.");
            return getPolicyProperty(AMP_MODE_PROP, "", slingRequest);
        }

        Page page = pageManager.getContainingPage(slingRequest.getResource());

        if (page != null) {

            String ampMode = page.getProperties().get(AMP_MODE_PROP, "");

            if (!ampMode.isEmpty()) {
                return ampMode;
            }
        }

        return getPolicyProperty(AMP_MODE_PROP, "", slingRequest);
    }

    /**
     * Retrieves the value of the given property from the request resource's content policy.
     * @param property The name of the property to read.
     * @param defaultValue The type hint and default value returned.
     * @param slingRequest The request used to get the resource and its content policy.
     * @param <T> The type of the property value expected.
     * @return The value of the property of the resource's content policy. Returns null if fails to read the content
     * policy.
     */
    private static <T> T getPolicyProperty(String property, T defaultValue,
                                           @NotNull SlingHttpServletRequest slingRequest) {

        ContentPolicyManager policyManager = slingRequest.getResourceResolver().adaptTo(ContentPolicyManager.class);
        if (policyManager == null) {
            LOG.trace("Policy manager is null. Unable to read policy property.");
            return defaultValue;
        }

        ContentPolicy contentPolicy = policyManager.getPolicy(slingRequest.getResource());
        if (contentPolicy == null) {
            LOG.trace("Content policy is null. Unable to read policy property.");
            return defaultValue;
        }

        return contentPolicy.getProperties().get(property, defaultValue);
    }
}
