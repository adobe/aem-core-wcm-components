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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.content.FragmentRenderService;
import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.contentfragment.ContentFragment;
import com.adobe.cq.wcm.core.components.models.contentfragment.DAMContentFragment;

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
public class ContentFragmentImpl implements ContentFragment {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentImpl.class);

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

    @ValueMapValue(name = ContentFragment.PN_PATH,
                   injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    @ValueMapValue(name = ContentFragment.PN_ELEMENT_NAMES,
                   injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] elementNames;

    @ValueMapValue(name = ContentFragment.PN_VARIATION_NAME,
                   injectionStrategy = InjectionStrategy.OPTIONAL)
    private String variationName;

    @ValueMapValue(name = ContentFragment.PN_DISPLAY_MODE,
                   injectionStrategy = InjectionStrategy.OPTIONAL)
    private String displayMode;

    private DAMContentFragment damContentFragment;

    @PostConstruct
    private void initModel() {
        if (StringUtils.isNotEmpty(fragmentPath)) {
            // get fragment resource
            Resource fragmentResource = resourceResolver.getResource(fragmentPath);
            if (fragmentResource == null) {
                LOG.error("Content Fragment can not be initialized because the '{}' does not exist.", fragmentPath);
            } else {
                this.damContentFragment = new DAMContentFragmentImpl(fragmentResource, contentTypeConverter, variationName, elementNames);
            }
        } else {
            LOG.warn("Please provide a path for the content fragment component.");
        }
    }

    @Nullable
    @Override
    public String getTitle() {
        return getDAMContentFragment().getTitle();
    }

    @Nullable
    @Override
    public String getDescription() {
        return getDAMContentFragment().getDescription();
    }

    @Nullable
    @Override
    public String getType() {
        return getDAMContentFragment().getType();
    }

    @NotNull
    @Override
    public String getGridResourceType() {
        return ContentFragmentUtils.getGridResourceType(resource);
    }

    @NotNull
    @Override
    public String getEditorJSON() {
        return getDAMContentFragment().getEditorJSON();
    }

    @NotNull
    @Override
    public Map<String, DAMContentElement> getExportedElements() {
        return getDAMContentFragment().getExportedElements();
    }

    @NotNull
    @Override
    public String[] getExportedElementsOrder() {
        return getDAMContentFragment().getExportedElementsOrder();
    }

    @Nullable
    @Override
    public List<DAMContentElement> getElements() {
        return getDAMContentFragment().getElements();
    }

    @Nullable
    @Override
    public List<Resource> getAssociatedContent() {
        return getDAMContentFragment().getAssociatedContent();
    }

    @NotNull
    @Override
    public String getExportedType() {
        return slingHttpServletRequest.getResource().getResourceType();
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return ContentFragmentUtils.getComponentExporters(resource.listChildren(), modelFactory, slingHttpServletRequest);
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        return ContentFragmentUtils.getItemsOrder(getExportedItems());
    }

    /**
     * Returns the delegate, i.e. the {@link DAMContentFragment content fragment}.
     */
    private DAMContentFragment getDAMContentFragment() {
        if (damContentFragment != null) {
            return damContentFragment;
        } else {
            return new DAMContentFragment() {
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
            };
        }
    }

    @Nullable
    @Override
    public String[] getParagraphs() {
        if (!"singleText".equals(displayMode)) {
            return null;
        }

        if (getDAMContentFragment().getElements() == null || getDAMContentFragment().getElements().isEmpty()) {
            return null;
        }

        DAMContentElement damContentElement = getDAMContentFragment().getElements().get(0);

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
}
