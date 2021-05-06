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
package com.adobe.cq.wcm.core.components.internal.models.v1;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.adobe.cq.wcm.core.components.util.LocalizationUtils;
import com.adobe.cq.xf.ExperienceFragmentsConstants;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.designer.ComponentStyle;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.day.text.Text;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.day.cq.wcm.api.NameConstants.NN_CONTENT;

/**
 * Experience Fragment model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ExperienceFragment.class, ComponentExporter.class, ContainerExporter.class },
    resourceType = {ExperienceFragmentImpl.RESOURCE_TYPE_V1 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl implements ExperienceFragment {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceFragmentImpl.class);

    /**
     * The experience fragment component resource type.
     */
    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/experiencefragment/v1/experiencefragment";

    /**
     * Sling path delimiter.
     */
    private static final char PATH_DELIMITER_CHAR = '/';

    /**
     * Content root.
     */
    private static final String CONTENT_ROOT = "/content";

    /**
     * Class name to be applied if the XF is empty or not configured.
     */
    private static final String CSS_EMPTY_CLASS = "empty";

    /**
     * Class name to be applied to all experience fragments.
     */
    private static final String CSS_BASE_CLASS = "aem-xf";

    /**
     * The current request.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * The current resource.
     */
    @SlingObject
    protected Resource resource;

    /**
     * The current page.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Page currentPage;

    /**
     * The language manager service.
     */
    @OSGiService
    private LanguageManager languageManager;

    /**
     * The live relationship manager service.
     */
    @OSGiService
    private LiveRelationshipManager relationshipManager;

    /**
     * The model factory service.
     */
    @OSGiService
    private ModelFactory modelFactory;

    /**
     * Path of the experience fragment variation.
     */
    private String localizedFragmentVariationPath;

    /**
     * Name of the experience fragment.
     */
    private String name;

    /**
     * Class names of the responsive grid.
     */
    private String classNames;

    /**
     * Child columns of the responsive grid.
     */
    private LinkedHashMap<String, ComponentExporter> children;

    /**
     * Initialize the model.
     */
    @PostConstruct
    private void initModel() {
        // currentPage is null when accessing the sling model exporter.
        this.currentPage = Optional.ofNullable(this.currentPage)
            .orElseGet(() -> Optional.ofNullable(this.request.getResourceResolver().adaptTo(PageManager.class))
                .map(pm -> pm.getContainingPage(this.request.getResource()))
                .orElse(null));

        if (this.currentPage == null) {
            LOGGER.error("Could not resolve currentPage!");
        }
    }

    @Override
    @Nullable
    public String getLocalizedFragmentVariationPath() {
        if (this.localizedFragmentVariationPath == null) {

            // get the configured fragment variation path
            String fragmentVariationPath = request.getResource().getValueMap()
                .get(ExperienceFragment.PN_FRAGMENT_VARIATION_PATH, String.class);

            final Page page = this.currentPage;
            if (page != null && inTemplate()) {
                final Resource pageResource = Optional.ofNullable(currentPage)
                    .map(p -> p.adaptTo(Resource.class))
                    .orElse(null);

                final String currentPageRootPath = pageResource != null ? LocalizationUtils.getLocalizationRoot(pageResource, request
                    .getResourceResolver(), languageManager, relationshipManager) : null;
                // we should use getLocalizationRoot instead of getXfLocalizationRoot once the XF UI supports creating Live and Language Copies
                String xfRootPath = getXfLocalizationRoot(fragmentVariationPath, currentPageRootPath);
                if (StringUtils.isNotEmpty(currentPageRootPath) && StringUtils.isNotEmpty(xfRootPath)) {
                    String xfRelativePath = StringUtils.substring(fragmentVariationPath, xfRootPath.length());
                    String localizedXfRootPath = StringUtils.replace(currentPageRootPath, CONTENT_ROOT, ExperienceFragmentsConstants.CONTENT_PATH, 1);
                    this.localizedFragmentVariationPath = StringUtils.join(localizedXfRootPath, xfRelativePath, PATH_DELIMITER_CHAR, NN_CONTENT);
                }
            }

            String xfContentPath = String.join(Character.toString(PATH_DELIMITER_CHAR), fragmentVariationPath, NN_CONTENT);
            if (!resourceExists(localizedFragmentVariationPath) && resourceExists(xfContentPath)) {
                this.localizedFragmentVariationPath = xfContentPath;
            }
            if (!isExperienceFragmentVariation(localizedFragmentVariationPath)) {
                this.localizedFragmentVariationPath = null;
            }
        }
        return this.localizedFragmentVariationPath;
    }

    @Override
    @JsonIgnore
    @Nullable
    public String getName() {
        if (this.name == null) {
            this.name = Optional.ofNullable(this.request.getResourceResolver().adaptTo(PageManager.class))
                .flatMap(pm -> Optional.ofNullable(this.getLocalizedFragmentVariationPath())
                    .map(pm::getContainingPage))
                .map(Page::getParent)
                .map(Page::getName)
                .orElse(null);
        }
        return this.name;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return this.request.getResource().getResourceType();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return this.getChildren();
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return this.getChildren().keySet().toArray(new String[0]);
    }

    @Override
    @JsonProperty("classNames")
    @NotNull
    public String getCssClassNames() {
        if (this.classNames == null) {
            this.classNames = Stream.of(
                CSS_BASE_CLASS,
                this.getExportedItems().isEmpty() ? CSS_EMPTY_CLASS : "",
                this.request.getResource().getValueMap().get(ComponentStyle.PN_CSS_CLASS, "")
            ).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
        }
        return classNames;
    }

    @Override
    @JsonInclude
    public boolean isConfigured() {
        return StringUtils.isNotEmpty(this.getLocalizedFragmentVariationPath()) && !this.getExportedItems().isEmpty();
    }

    /**
     * Gets an ordered map of all children resources of the experience fragment with the resource name as the key
     * and the corresponding {@link ComponentExporter} model as the value.
     *
     * @return Ordered map of resource names to {@link ComponentExporter} models.
     */
    private LinkedHashMap<String, ComponentExporter> getChildren() {
        if (this.children == null) {
            this.children = Optional.ofNullable(this.getLocalizedFragmentVariationPath())
                    .filter(StringUtils::isNotBlank)
                    .map(this.resource.getResourceResolver()::getResource)
                    .map(Resource::listChildren)
                    .map(it -> ContentFragmentUtils.getComponentExporters(it, this.modelFactory, this.request, this.resource))
                    .orElseGet(LinkedHashMap::new);
        }
        return this.children;
    }

    /**
     * Returns the localization root of the experience fragment path based on the localization root of the current page.
     *
     * As of today (08/aug/2019) the XF UI does not support creating Live and Language Copies, which prevents getRoot
     * to be used with XF.
     * This method works around this issue by deducting the XF root from the XF path and the root of the current page.
     *
     * @param xfPath the experience fragment path
     * @param currentPageRoot the localization root of the current page
     * @return the localization root of the experience fragment path if it exists, {@code null} otherwise
     */
    @Nullable
    private String getXfLocalizationRoot(@Nullable final String xfPath, @Nullable final String currentPageRoot) {
        if (StringUtils.isNotEmpty(xfPath) && StringUtils.isNotEmpty(currentPageRoot)
                && this.request.getResourceResolver().getResource(xfPath) != null
                && this.request.getResourceResolver().getResource(currentPageRoot) != null) {
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
        return (StringUtils.isNotEmpty(path) && this.request.getResourceResolver().getResource(path) != null);
    }

    /**
     * Checks if the resource is defined in the template.
     *
     * @return {@code true} if the resource is defined in the template, {@code false} otherwise
     */
    private boolean inTemplate() {
        return Optional.ofNullable(this.currentPage)
            .map(Page::getTemplate)
            .map(Template::getPath)
            .filter(request.getResource().getPath()::startsWith)
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
            .map(request.getResourceResolver()::getResource)
            .map(Resource::getValueMap)
            .map(vm -> vm.get(ExperienceFragmentsConstants.PN_XF_VARIANT_TYPE, String.class))
            .isPresent();
    }

}
