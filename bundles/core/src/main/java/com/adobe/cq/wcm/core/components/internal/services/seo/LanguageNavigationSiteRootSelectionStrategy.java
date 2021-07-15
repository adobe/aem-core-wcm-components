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
package com.adobe.cq.wcm.core.components.internal.services.seo;

import java.util.Optional;
import java.util.function.Predicate;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.adobe.aem.wcm.seo.localization.SiteRootSelectionStrategy;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

/**
 * An implementation of {@link SiteRootSelectionStrategy} that looks for a language navigation component on the given page and uses it's
 * configured navigation root as site root.
 * <p>
 * This Component must be explicitly enabled by providing an (empty) configuration for it. This should be the case for sites the use the
 * language navigation core component. For any other case this Component should be kept disabled.
 */
@Component(
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    property = {
        Constants.SERVICE_RANKING + ":Integer=100"
    },
    service = { SiteRootSelectionStrategy.class }
)
public class LanguageNavigationSiteRootSelectionStrategy implements SiteRootSelectionStrategy {

    @Reference(
        cardinality = ReferenceCardinality.OPTIONAL,
        policyOption = ReferencePolicyOption.GREEDY,
        target = "(objectClass=*DefaultSiteRootSelectionStrategy)")
    private SiteRootSelectionStrategy defaultSelectionStrategy;

    @Override
    @Nullable
    public Page getSiteRoot(@NotNull Page page) {
        Resource languageNavigation = findLanguageNavigation(page.getContentResource());
        if (languageNavigation != null) {
            String siteRoot = getPropertyLazy(languageNavigation, LanguageNavigation.PN_NAVIGATION_ROOT, String.class);
            if (siteRoot != null) {
                return page.getPageManager().getPage(siteRoot);
            }
        }
        return defaultSelectionStrategy != null ? defaultSelectionStrategy.getSiteRoot(page) : null;
    }

    @Override
    public int getStructuralDepth(@NotNull Page page) {
        Resource languageNavigation = findLanguageNavigation(page.getContentResource());
        if (languageNavigation != null) {
            Integer structureDepth = getPropertyLazy(languageNavigation, LanguageNavigation.PN_STRUCTURE_DEPTH, Integer.class);
            if (structureDepth != null) {
                return structureDepth;
            }
        }
        return defaultSelectionStrategy != null ? defaultSelectionStrategy.getStructuralDepth(page) : 1;
    }

    private Resource findLanguageNavigation(Resource contentResource) {
        Resource templatedResource = contentResource.adaptTo(TemplatedResource.class);
        if (templatedResource != null) {
            contentResource = templatedResource;
        }
        return findFirst(contentResource,
            resource -> resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImpl.RESOURCE_TYPE)
                || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v2.LanguageNavigationImpl.RESOURCE_TYPE));
    }

    private Resource findFirst(Resource resource, Predicate<Resource> condition) {
        if (resource == null) {
            return null;
        }
        // check if the condition is met
        if (condition.test(resource)) {
            return resource;
        }
        // if not, check for experience fragments and resolve them
        if (resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V1)
            || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V2)) {
            return Optional.ofNullable(new ExperienceFragmentRequest(resource).adaptTo(ExperienceFragment.class))
                .map(ExperienceFragment::getLocalizedFragmentVariationPath)
                .map(xfPath -> resource.getResourceResolver().getResource(xfPath))
                .map(xfResource -> findFirst(xfResource, condition))
                .orElse(null);
        }
        // if not iterate over the resource's children
        for (Resource child : resource.getChildren()) {
            Resource result = findFirst(child, condition);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    private static <T> T getPropertyLazy(Resource resource, String property, Class<T> type) {
        ValueMap properties = resource.getValueMap();
        T value = properties.get(property, type);
        if (value == null) {
            Designer designer = resource.getResourceResolver().adaptTo(Designer.class);
            Style style = designer != null ? designer.getStyle(resource) : null;
            if (style != null) {
                value = style.get(property, type);
            }
        }
        return value;
    }

    private static class ExperienceFragmentRequest extends MockSlingHttpServletRequest {

        ExperienceFragmentRequest(Resource xfResource) {
            super(xfResource.getResourceResolver());
            setResource(xfResource);
        }
    }
}
