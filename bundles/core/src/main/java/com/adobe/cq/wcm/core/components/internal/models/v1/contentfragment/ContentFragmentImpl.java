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
package com.adobe.cq.wcm.core.components.internal.models.v1.contentfragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ContentFragmentDataImpl;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.ContentFragmentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;

@Model(
        adaptables = SlingHttpServletRequest.class,
        adapters = {
                ContentFragment.class,
                DAMContentFragment.class,
                ContainerExporter.class,
                ComponentExporter.class
        },
        resourceType = ContentFragmentImpl.RESOURCE_TYPE
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentFragmentImpl extends AbstractComponentImpl implements ContentFragment {

    /**
     * The resource type of the component associated with this Sling model.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/contentfragment/v1/contentfragment";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private FragmentRenderService renderService;

    @Inject
    private ContentTypeConverter contentTypeConverter;

    @Inject
    private ModelFactory modelFactory;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Resource resource;

    @ValueMapValue(name = ContentFragment.PN_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String fragmentPath;

    @ValueMapValue(name = ContentFragment.PN_ELEMENT_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String[] elementNames;

    @ValueMapValue(name = ContentFragment.PN_VARIATION_NAME, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String variationName;

    @ValueMapValue(name = ContentFragment.PN_DISPLAY_MODE, injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String displayMode;

    private DAMContentFragment damContentFragment = new EmptyContentFragment();

    @PostConstruct
    private void initModel() {
        if (StringUtils.isNotEmpty(fragmentPath)) {
            Resource fragmentResource = resourceResolver.getResource(fragmentPath);
            if (fragmentResource != null) {
                damContentFragment = new DAMContentFragmentImpl(fragmentResource, contentTypeConverter, variationName, elementNames);
            }
        }
    }

    @Nullable
    @Override
    public String getTitle() {
        return damContentFragment.getTitle();
    }

    @Nullable
    @Override
    public String getDescription() {
        return damContentFragment.getDescription();
    }

    @Nullable
    @Override
    public String getType() {
        return damContentFragment.getType();
    }

    @NotNull
    @Override
    public String getName() {
        return damContentFragment.getName();
    }

    @NotNull
    @Override
    public String getGridResourceType() {
        return ContentFragmentUtils.getGridResourceType(resource);
    }

    @NotNull
    @Override
    public String getEditorJSON() {
        return damContentFragment.getEditorJSON();
    }

    @NotNull
    @Override
    public Map<String, DAMContentElement> getExportedElements() {
        return damContentFragment.getExportedElements();
    }

    @NotNull
    @Override
    public String[] getExportedElementsOrder() {
        return damContentFragment.getExportedElementsOrder();
    }

    @Nullable
    @Override
    public List<DAMContentElement> getElements() {
        return damContentFragment.getElements();
    }

    @Nullable
    @Override
    public List<Resource> getAssociatedContent() {
        return damContentFragment.getAssociatedContent();
    }

    @NotNull
    @Override
    public String getExportedType() {
        return slingHttpServletRequest.getResource().getResourceType();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return ContentFragmentUtils.getComponentExporters(resource.listChildren(), modelFactory, slingHttpServletRequest, resource);
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return ContentFragmentUtils.getItemsOrder(getExportedItems());
    }

    @Nullable
    @Override
    public String[] getParagraphs() {
        if (!"singleText".equals(displayMode)) {
            return null;
        }

        if (damContentFragment.getElements() == null || damContentFragment.getElements().isEmpty()) {
            return null;
        }

        DAMContentElement damContentElement = damContentFragment.getElements().get(0);

        // restrict this method to text elements
        if (!damContentElement.isMultiLine()) {
            return null;
        }

        // render the fragment
        String content = renderService.render(resource);
        if (content == null) {
            return null;
        }

        // split into paragraphs
        return content.split("(?=(<p>|<h1>|<h2>|<h3>|<h4>|<h5>|<h6>))");
    }

    @Override
    @NotNull
    protected ComponentData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asContentFragment()
            .withTitle(this::getTitle)
            .withElementsData(() -> {
                List<ContentFragmentData.ElementData> elementsData = new ArrayList<>();
                Optional.ofNullable(this.getElements())
                    .map(elements -> {
                        for (DAMContentFragment.DAMContentElement contentElement : elements) {
                            elementsData.add(new ContentFragmentDataImpl.ElementDataImpl(contentElement));
                        }
                        return Optional.empty();
                    });
                return elementsData.toArray(new ContentFragmentData.ElementData[0]);
            })
            .build();
    }

    /**
     * Empty placeholder content fragment.
     */
    private static class EmptyContentFragment implements DAMContentFragment {
        @Override
        public @Nullable String getTitle() {
            return null;
        }

        @Override
        public @Nullable String getDescription() {
            return null;
        }

        @Override
        public @Nullable String getType() {
            return null;
        }

        @Override
        public @NotNull String getName() {
            return "";
        }

        @Override
        public @Nullable List<DAMContentElement> getElements() {
            return null;
        }

        @Override
        public @NotNull Map<String, DAMContentElement> getExportedElements() {
            return new HashMap<>();
        }

        @Override
        public @NotNull String[] getExportedElementsOrder() {
            return new String[0];
        }

        @Override
        public @Nullable List<Resource> getAssociatedContent() {
            return null;
        }
    }
}
