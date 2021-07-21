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
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlethelpers.MockSlingHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.wcm.seo.localization.SiteRootSelectionStrategy;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.TemplatedResource;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

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

    private static final Logger LOG = LoggerFactory.getLogger(LanguageNavigationSiteRootSelectionStrategy.class);

    @Reference(
        cardinality = ReferenceCardinality.OPTIONAL,
        policyOption = ReferencePolicyOption.GREEDY,
        target = "(objectClass=*DefaultSiteRootSelectionStrategy)")
    private SiteRootSelectionStrategy defaultSelectionStrategy;

    private final Cache<Page, Optional<Resource>> languageNavigationCache = CacheBuilder.newBuilder().weakKeys().build();

    @Override
    @Nullable
    public Page getSiteRoot(@NotNull Page page) {
        return findLanguageNavigation(page)
            .map(languageNavigation -> Utils.getPropertyOrStyle(languageNavigation, LanguageNavigation.PN_NAVIGATION_ROOT, String.class))
            .map(siteRoot -> page.getPageManager().getPage(siteRoot))
            .orElseGet(() -> defaultSelectionStrategy != null ? defaultSelectionStrategy.getSiteRoot(page) : null);
    }

    @Override
    public int getStructuralDepth(@NotNull Page page) {
        return findLanguageNavigation(page)
            .map(languageNavigation -> Utils.getPropertyOrStyle(languageNavigation, LanguageNavigation.PN_STRUCTURE_DEPTH, Integer.class))
            .orElseGet(() -> defaultSelectionStrategy != null ? defaultSelectionStrategy.getStructuralDepth(page) : 1);
    }

    private Optional<Resource> findLanguageNavigation(Page page) {
        try {
            return languageNavigationCache.get(page, () -> findLanguageNavigation(page.getContentResource()));
        } catch (ExecutionException ex) {
            LOG.warn("Failed to find language navigation", ex);
            return Optional.empty();
        }
    }

    private Optional<Resource> findLanguageNavigation(Resource contentResource) {
        Resource templatedResource = contentResource != null ? contentResource.adaptTo(TemplatedResource.class) : null;
        if (templatedResource != null) {
            contentResource = templatedResource;
        }
        return findFirst(contentResource,
            resource -> resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImpl.RESOURCE_TYPE)
                || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v2.LanguageNavigationImpl.RESOURCE_TYPE));
    }

    private Optional<Resource> findFirst(Resource resource, Predicate<Resource> condition) {
        if (resource == null) {
            return Optional.empty();
        }
        // check if the condition is met
        if (condition.test(resource)) {
            return Optional.of(resource);
        }
        // if not, check for experience fragments and resolve them
        if (resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V1)
            || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V2)) {
            return Optional.ofNullable(new ExperienceFragmentRequest(resource).adaptTo(ExperienceFragment.class))
                .map(ExperienceFragment::getLocalizedFragmentVariationPath)
                .map(xfPath -> resource.getResourceResolver().getResource(xfPath))
                .flatMap(xfResource -> findFirst(xfResource, condition));
        }
        // if not iterate over the resource's children
        for (Resource child : resource.getChildren()) {
            Optional<Resource> result = findFirst(child, condition);
            if (result.isPresent()) {
                return result;
            }
        }

        return Optional.empty();
    }

    private static class ExperienceFragmentRequest extends MockSlingHttpServletRequest {
        ExperienceFragmentRequest(Resource xfResource) {
            super(xfResource.getResourceResolver());
            setResource(xfResource);
        }
    }
}
