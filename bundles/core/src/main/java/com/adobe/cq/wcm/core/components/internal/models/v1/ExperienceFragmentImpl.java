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

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.ComponentStyle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Experience Fragment model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {ExperienceFragment.class, ComponentExporter.class, ContainerExporter.class },
    resourceType = {ExperienceFragmentImpl.RESOURCE_TYPE_V1, ExperienceFragmentImpl.RESOURCE_TYPE_V2 })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl extends AbstractComponentImpl implements ExperienceFragment {

    /**
     * The experience fragment component resource type.
     */
    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/experiencefragment/v1/experiencefragment";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/experiencefragment/v2/experiencefragment";

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

    @Self
    private ExperienceFragmentDataImpl data;

    /**
     * The model factory service.
     */
    @OSGiService
    private ModelFactory modelFactory;

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


    @Override
    @Nullable
    public String getLocalizedFragmentVariationPath() {
        return data.getLocalizedFragmentVariationPath();
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
}
