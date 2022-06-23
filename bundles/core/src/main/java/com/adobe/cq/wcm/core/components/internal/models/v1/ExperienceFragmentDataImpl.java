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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.util.LocalizationUtils;
import com.adobe.cq.xf.ExperienceFragmentsConstants;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.text.Text;

import static com.day.cq.wcm.api.NameConstants.NN_CONTENT;

/**
 * An internal model that implements the logic used by the {@link ExperienceFragmentImpl} in a way it can be used with a
 * {@link SlingHttpServletRequest} or a {@link Resource}.
 */
@Model(adaptables = { Resource.class, SlingHttpServletRequest.class })
public class ExperienceFragmentDataImpl {

    /**
     * Sling path delimiter.
     */
    private static final char PATH_DELIMITER_CHAR = '/';

    /**
     * Content root.
     */
    private static final String CONTENT_ROOT = "/content";

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    private Page currentPage;

    private final Resource resource;

    public ExperienceFragmentDataImpl(SlingHttpServletRequest request) {
        this(request.getResource());
    }

    public ExperienceFragmentDataImpl(Resource resource) {
        this.resource = resource;
    }

    /**
     * Path of the experience fragment variation.
     */
    private String localizedFragmentVariationPath;

    @PostConstruct
    protected void postConstruct() {
        if (currentPage == null) {
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                currentPage = pageManager.getContainingPage(resource);
            }
        }
    }

    /**
     * Returns the localization root of the experience fragment path based on the localization root of the current page.
     * <p>
     * As of today (08/aug/2019) the XF UI does not support creating Live and Language Copies, which prevents getRoot
     * to be used with XF.
     * This method works around this issue by deducting the XF root from the XF path and the root of the current page.
     *
     * @param xfPath          the experience fragment path
     * @param currentPageRoot the localization root of the current page
     * @return the localization root of the experience fragment path if it exists, {@code null} otherwise
     */
    @Nullable
    private String getXfLocalizationRoot(@Nullable final String xfPath, @Nullable final String currentPageRoot) {
        if (StringUtils.isNotEmpty(xfPath) && StringUtils.isNotEmpty(currentPageRoot)
            && resourceResolver.getResource(xfPath) != null
            && resourceResolver.getResource(currentPageRoot) != null) {
            String[] xfPathTokens = Text.explode(xfPath, PATH_DELIMITER_CHAR);
            int xfRootDepth = Text.explode(currentPageRoot, PATH_DELIMITER_CHAR).length + 1;
            if (xfPathTokens.length >= xfRootDepth) {
                String[] xfRootTokens = new String[xfRootDepth];
                System.arraycopy(xfPathTokens, 0, xfRootTokens, 0, xfRootDepth);
                return StringUtils.join(PATH_DELIMITER_CHAR,
                    Text.implode(xfRootTokens, Character.toString(PATH_DELIMITER_CHAR)));
            }
        }
        return null;
    }

    /**
     * Checks if the resource exists at the given path.
     *
     * @param path the resource path
     * @return {@code true} if the resource exists, {@code false} otherwise
     */
    private boolean resourceExists(@Nullable final String path) {
        return StringUtils.isNotEmpty(path) && resourceResolver.getResource(path) != null;
    }

    /**
     * Checks if the resource is defined in the template.
     *
     * @return {@code true} if the resource is defined in the template, {@code false} otherwise
     */
    private boolean inTemplate() {
        return Optional.ofNullable(currentPage)
            .map(Page::getTemplate)
            .map(Template::getPath)
            .filter(resource.getPath()::startsWith)
            .isPresent();
    }

    /**
     * Checks if the resource at the given path is an Experience Fragment variation.
     *
     * @return {@code true} if the resource is an XF variation, {@code false} otherwise
     */
    private boolean isExperienceFragmentVariation(@Nullable final String path) {
        return Optional.ofNullable(path)
            .filter(StringUtils::isNotEmpty)
            .map(resourceResolver::getResource)
            .map(Resource::getValueMap)
            .map(vm -> vm.get(ExperienceFragmentsConstants.PN_XF_VARIANT_TYPE, String.class))
            .isPresent();
    }

    /**
     * Returns the localized path of the experience fragment variation if the experience fragment resource is defined
     * in a template.
     *
     * @return Localized experience fragment variation path
     * @see ExperienceFragment#getLocalizedFragmentVariationPath()
     */
    @Nullable
    public String getLocalizedFragmentVariationPath() {
        if (localizedFragmentVariationPath != null) {
            return localizedFragmentVariationPath;
        }

        // get the configured fragment variation path
        String fragmentVariationPath = resource.getValueMap().get(ExperienceFragment.PN_FRAGMENT_VARIATION_PATH, String.class);

        if (currentPage != null && inTemplate()) {
            final Resource pageResource = Optional.ofNullable(currentPage)
                .map(p -> p.adaptTo(Resource.class))
                .orElse(null);

            final String currentPageRootPath = pageResource != null ? LocalizationUtils.getLocalizationRoot(pageResource,
                resourceResolver, languageManager, relationshipManager) : null;
            // we should use getLocalizationRoot instead of getXfLocalizationRoot once the XF UI supports creating Live and Language Copies
            String xfRootPath = getXfLocalizationRoot(fragmentVariationPath, currentPageRootPath);
            if (StringUtils.isNotEmpty(currentPageRootPath) && StringUtils.isNotEmpty(xfRootPath)) {
                String xfRelativePath = StringUtils.substring(fragmentVariationPath, xfRootPath.length());
                String localizedXfRootPath = StringUtils
                    .replace(currentPageRootPath, CONTENT_ROOT, ExperienceFragmentsConstants.CONTENT_PATH, 1);
                localizedFragmentVariationPath = StringUtils.join(localizedXfRootPath, xfRelativePath, PATH_DELIMITER_CHAR, NN_CONTENT);
            }
        }

        String xfContentPath = String.join(Character.toString(PATH_DELIMITER_CHAR), fragmentVariationPath, NN_CONTENT);
        if (!resourceExists(localizedFragmentVariationPath) && resourceExists(xfContentPath)) {
            localizedFragmentVariationPath = xfContentPath;
        }
        if (!isExperienceFragmentVariation(localizedFragmentVariationPath)) {
            localizedFragmentVariationPath = null;
        }

        return localizedFragmentVariationPath;
    }
}
