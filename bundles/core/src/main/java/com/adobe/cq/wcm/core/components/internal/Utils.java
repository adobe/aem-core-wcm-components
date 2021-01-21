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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.foundation.AllowedComponentList;
import com.google.common.collect.ImmutableSet;

public class Utils {

    private static final Set<String> INTERNAL_PARAMETER = ImmutableSet.of(
            ":formstart",
            "_charset_",
            ":redirect",
            ":cq_csrf_token"
    );

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
     * Returns a set of resource types for components used to render a given page, including those
     * from the page template and embedded experience templates.
     *
     * @param page the {@link Page}
     * @param request the current request
     * @param modelFactory the {@link ModelFactory}
     *
     * @return The set of resource types for components used to render a page.
     */
    @NotNull
    public static Set<String> getPageResourceTypes(@NotNull Page page, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.addAll(getResourceTypes(page.getContentResource(), request, modelFactory));
        resourceTypes.addAll(getTemplateResourceTypes(page, request, modelFactory));
        return resourceTypes;
    }

    /**
     * Returns a set of resource types for components used to render a given resource,
     * including it's direct children
     *
     * @param resource the resource
     * @param request the current request
     * @param modelFactory the {@link ModelFactory}
     *
     * @return a set of resource types for components used to render the resource
     */
    @NotNull
    public static Set<String> getResourceTypes(@NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Set<String> resourceTypes = new HashSet<>();
        resourceTypes.add(resource.getResourceType());
        resourceTypes.addAll(getXFResourceTypes(resource, request, modelFactory));
        for (Resource child : resource.getChildren()) {
            resourceTypes.addAll(getResourceTypes(child, request, modelFactory));
        }
        return resourceTypes;
    }

    /**
     * Returns a set of resource types for components included in the experience template
     *
     * @param resource the resource, will be tested to see if it's an experience template
     * @param request the current request
     * @param modelFactory the {@link ModelFactory}
     *
     * @return a set of resource types for components included in the experience template
     */
    @NotNull
    public static Set<String> getXFResourceTypes(@NotNull Resource resource, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        ExperienceFragment experienceFragment = modelFactory.getModelFromWrappedRequest(request, resource, ExperienceFragment.class);
        if (experienceFragment != null) {
            String fragmentPath = experienceFragment.getLocalizedFragmentVariationPath();
            if (StringUtils.isNotEmpty(fragmentPath)) {
                Resource fragmentResource = resource.getResourceResolver().getResource(fragmentPath);
                if (fragmentResource != null) {
                    return getResourceTypes(fragmentResource, request, modelFactory);
                }
            }
        }
        return Collections.emptySet();
    }

    /**
     * Returns a set of resource types for components included in the page template
     *
     * @param page the page
     * @param request the current request
     * @param modelFactory the {@link ModelFactory}
     *
     * @return a set of resource types for components included in the page template
     */
    @NotNull
    public static Set<String> getTemplateResourceTypes(@NotNull Page page, @NotNull SlingHttpServletRequest request, @NotNull ModelFactory modelFactory) {
        Template template = page.getTemplate();
        if (template != null) {
            String templatePath = template.getPath() + AllowedComponentList.STRUCTURE_JCR_CONTENT;
            Resource templateResource = request.getResourceResolver().getResource(templatePath);
            if (templateResource != null) {
                return getResourceTypes(templateResource, request, modelFactory);
            }
        }
        return Collections.emptySet();
    }

    /**
     * Returns all the super-types of a component defined by its resource type.
     *
     * @param resourceType the resource type of the component.
     * @param resourceResolver the resource resolver.
     *
     * @return a set of the inherited resource types.
     */
    @NotNull
    public static Set<String> getSuperTypes(@NotNull final String resourceType, @NotNull final ResourceResolver resourceResolver) {
        Set<String> superTypes = new HashSet<>();
        String superType = resourceType;
        while (superType != null) {
            superType = Optional.ofNullable(resourceResolver.getResource(superType))
                .map(Resource::getResourceSuperType)
                .filter(StringUtils::isNotEmpty)
                .orElse(null);
            if (superType != null) {
                superTypes.add(superType);
            }
        }
        return superTypes;
    }


    /**
     * Get the inherited value of a property from the page content resource. Walk the content tree upwards until an override of that
     * property is specified.
     *
     * @param startPage the page in the content tree to start looking for the requested property.
     * @param propertyName the name of the property which is inherited.
     * @return the inherited value of the property or empty string if the property is not specified in the content tree.
     */
    @NotNull
    public static String getInheritedValue(com.day.cq.wcm.api.Page startPage, String propertyName) {
    	if( startPage == null ) {
    		return StringUtils.EMPTY;
    	}
    	
    	com.day.cq.wcm.api.Page tmp = startPage;
    	
		while( tmp != null && tmp.hasContent() && tmp.getDepth() > 1 ) {
			ValueMap props = tmp.getProperties();
			if( props != null ) {
				boolean override = Boolean.parseBoolean(props.get(propertyName + "_override", String.class));
				if(override) {
					return props.get(propertyName, StringUtils.EMPTY);
				}
				tmp = tmp.getParent();
			}
		}
    	return StringUtils.EMPTY;
    }

    /**
     * Converts the input into a set of strings. The input can be either a {@link Collection}, an array or a CSV.
     *
     * @param input - the input
     *
     * @return Set of strings from input
     */
    @NotNull
    public static Set<String> getStrings(@Nullable final Object input) {
        Set<String> strings = new LinkedHashSet<>();
        if (input != null) {
            Class clazz = input.getClass();
            if (Collection.class.isAssignableFrom(clazz)) {
                // Try to convert from a collection
                for (Object obj : (Collection)input) {
                    if (obj != null) {
                        strings.add(obj.toString());
                    }
                }
            } else if (Object[].class.isAssignableFrom(clazz)) {
                // Try to convert from an array
                for (Object obj : (Object[]) input) {
                    if (obj != null) {
                        strings.add(obj.toString());
                    }
                }
            } else if (String.class.isAssignableFrom(clazz)) {
                // Try to convert from a CSV string
                for (String str : ((String)input).split(",")) {
                    if (StringUtils.isNotBlank(str)) {
                        strings.add(str.trim());
                    }
                }
            }
        }
        return strings;
    }

    /**
     * Converts request parameters to a JSON object and filter AEM specific parameters out.
     *
     * @param request - the current {@link SlingHttpServletRequest}
     * @return JSON object of the request parameters
     */
    public static JSONObject getJsonOfRequestParameters(SlingHttpServletRequest request) throws JSONException {
        org.json.JSONObject jsonObj = new org.json.JSONObject();
        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (!INTERNAL_PARAMETER.contains(entry.getKey())) {
                String[] v = entry.getValue();
                Object o = (v.length == 1) ? v[0] : v;
                jsonObj.put(entry.getKey(), o);
            }
        }
        return jsonObj;
    }
}
