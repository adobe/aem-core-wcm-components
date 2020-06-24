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
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.foundation.AllowedComponentList;

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

    public static Set<String> getTemplateResourceTypes(Page page, String resourceTypeRegex, SlingHttpServletRequest request,
                                                       ModelFactory modelFactory, Set<String> resourceTypes) {
        if (page.getTemplate() == null) {
            return resourceTypes;
        }

        String templatePath = page.getTemplate().getPath() + AllowedComponentList.STRUCTURE_JCR_CONTENT;

        Resource templateResource = request.getResourceResolver().getResource(templatePath);

        if (templateResource != null) {
            getResourceTypes(templateResource,resourceTypeRegex, resourceTypes, request, modelFactory);
        }

        return resourceTypes;
    }


    public static Set<String> getTemplateResourceTypes(Page page, String resourceTypeRegex, ResourceResolver resolver,
                                                       Set<String> resourceTypes) {
        if (page.getTemplate() == null) {
            return resourceTypes;
        }

        String templatePath = page.getTemplate().getPath() + AllowedComponentList.STRUCTURE_JCR_CONTENT;

        Resource templateResource = resolver.getResource(templatePath);

        if (templateResource != null) {
            getResourceTypes(templateResource,resourceTypeRegex, resourceTypes);
        }

        return resourceTypes;
    }


    /**
     * Retrieves the resource types of the given resource and all of its child resources.
     * @param resource The resource to start retrieving resources types from.
     * @param resourceTypeRegex Regex used to filter the resource types collected. Gets all resource types if empty.
     * @param resourceTypes String set to append resource type values to.
     * @param request The current request
     * @param modelFactory The ModelFactory to create the SlingModel
     * @return String set of resource type values found.
     */
    public static Set<String> getResourceTypes(Resource resource, String resourceTypeRegex, Set<String> resourceTypes,
                                               SlingHttpServletRequest request, ModelFactory modelFactory) {

        if (resource == null) {
            return resourceTypes;
        }

        ExperienceFragment experienceFragment = modelFactory.getModelFromWrappedRequest(request, resource, ExperienceFragment.class);
        if (experienceFragment != null && StringUtils.isNotEmpty(experienceFragment.getLocalizedFragmentVariationPath())) {
            Resource experienceResource = resource.getResourceResolver().getResource(experienceFragment.getLocalizedFragmentVariationPath());
            if (experienceResource != null) {
                getResourceTypes(experienceResource, resourceTypeRegex, resourceTypes, request, modelFactory);
            }
        }

        // Add resource type to return set if allowed by the resource type regex.
        String resourceType = resource.getResourceType();
        if (StringUtils.isBlank(resourceTypeRegex)
                || resourceType.matches(resourceTypeRegex)) {
            resourceTypes.add(resourceType);
        }

        // Iterate through the resource's children and recurse through them for resource types.
        for (Resource child : resource.getChildren()) {
            getResourceTypes(child, resourceTypeRegex, resourceTypes, request, modelFactory);
        }

        return resourceTypes;
    }


    /**
     * Retrieves the resource types of the given resource and all of its child resources.
     * @param resource The resource to start retrieving resources types from.
     * @param resourceTypeRegex Regex used to filter the resource types collected. Gets all resource types if empty.
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
