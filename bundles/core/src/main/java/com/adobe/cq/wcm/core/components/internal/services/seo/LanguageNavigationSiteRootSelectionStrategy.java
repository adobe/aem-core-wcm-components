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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.aem.wcm.seo.localization.SiteRootSelectionStrategy;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

/**
 * An implementation of {@link SiteRootSelectionStrategy} that looks for a language navigation component on the given page and uses it's
 * configured navigation root as site root.
 */
@Component(
    property = {
        Constants.SERVICE_RANKING + ":Integer=100"
    },
    service = { SiteRootSelectionStrategy.class }
)
public class LanguageNavigationSiteRootSelectionStrategy implements SiteRootSelectionStrategy {

    private static final String RT_RESPONSIVE_GRID = "wcm/foundation/components/responsivegrid";
    private static final String RT_BASICPAGE = "wcm/foundation/components/basicpage/v1/basicpage";

    @Reference
    private Designer designer;

    @Override
    @Nullable
    public Page getSiteRoot(@NotNull Page page) {
        return findLanguageNavigation(page.getContentResource()).
            map(languageNavigation -> {
                ValueMap properties = languageNavigation.getValueMap();
                String navigationRoot = properties.get(LanguageNavigation.PN_NAVIGATION_ROOT, String.class);
                if (navigationRoot == null) {
                    Style style = designer.getStyle(languageNavigation);
                    if (style != null) {
                        navigationRoot = style.get(LanguageNavigation.PN_NAVIGATION_ROOT, String.class);
                    }
                }

                return page.getPageManager().getPage(navigationRoot);
            })
            .orElse(null);
    }

    private Optional<Resource> findLanguageNavigation(Resource content) {
        return traverse(content)
            .filter(resource ->
                resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.LanguageNavigationImpl.RESOURCE_TYPE)
                    || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v2.LanguageNavigationImpl.RESOURCE_TYPE))
            .findFirst();
    }

    private Stream<Resource> traverse(Resource resource) {
        if (resource == null) {
            return Stream.empty();
        }
        // traverse only on types that are supposed to have children
        if (resource.isResourceType(RT_RESPONSIVE_GRID) || resource.isResourceType(RT_BASICPAGE)) {
            return StreamSupport.stream(resource.getChildren().spliterator(), false)
                .flatMap(this::traverse);
        }
        // resolve experience fragments
        if (resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V1)
            || resource.isResourceType(com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl.RESOURCE_TYPE_V2)) {
            return Optional.ofNullable(resource.adaptTo(ExperienceFragment.class))
                .map(ExperienceFragment::getLocalizedFragmentVariationPath)
                .map(xfPath -> resource.getResourceResolver().getResource(xfPath))
                .map(this::traverse)
                .orElseGet(Stream::empty);
        }

        return Stream.of(resource);
    }
}
