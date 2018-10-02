/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.extension.contentfragment.internal.models.v2;

import com.adobe.cq.dam.cfm.converter.ContentTypeConverter;
import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.extension.contentfragment.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2.ContentFragment;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2.ContentFragmentComponentModel;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {
        ContentFragmentComponentModel.class,
        ContentFragment.class,
        ContainerExporter.class,
        ComponentExporter.class
    },
    resourceType = ContentFragmentComponentModelImpl.RESOURCE_TYPE
)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ContentFragmentComponentModelImpl implements ContentFragmentComponentModel {

    private static final Logger LOG = LoggerFactory.getLogger(ContentFragmentComponentModelImpl.class);

    public static final String RESOURCE_TYPE = "core/wcm/extension/components/contentfragment/v2/contentfragment";

    @Self(injectionStrategy = InjectionStrategy.REQUIRED)
    private SlingHttpServletRequest slingHttpServletRequest;

    @Inject
    private ContentTypeConverter contentTypeConverter;

    @Inject
    private ModelFactory modelFactory;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Resource resource;

    @ValueMapValue(name = ContentFragmentComponentModel.PN_PATH, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fragmentPath;

    @ValueMapValue(name = ContentFragmentComponentModel.PN_ELEMENT_NAMES, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] elementNames;

    @ValueMapValue(name = ContentFragmentComponentModel.PN_VARIATION_NAME, injectionStrategy = InjectionStrategy.OPTIONAL)
    private String variationName;

    private ContentFragment contentFragment;

    @PostConstruct
    private void initModel() {
        if (StringUtils.isNotEmpty(fragmentPath)) {
            // get fragment resource
            Resource fragmentResource = resourceResolver.getResource(fragmentPath);
            if (fragmentResource == null) {
                LOG.error("Content Fragment can not be initialized because the '{}' does not exist.", fragmentPath);
            } else {
                this.contentFragment = new ContentFragmentImpl(fragmentResource, contentTypeConverter, variationName, elementNames);
            }
        } else {
            LOG.warn("Please provide a path for the content fragment component.");
        }
    }

    @Nonnull
    @Override
    public String getGridResourceType() {
        return ContentFragmentUtils.getGridResourceType(resourceResolver, resource);
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return ContentFragmentUtils.getComponentExporters(resource.listChildren(), modelFactory, slingHttpServletRequest);
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        return ContentFragmentUtils.getItemsOrder(getExportedItems());
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return slingHttpServletRequest.getResource().getResourceType();
    }

    /**
     * Returns the delegate, i.e. the {@link ContentFragment content fragment}.
     */
    private ContentFragment getContentFragment() {
        if (contentFragment == null) {
            throw new IllegalStateException("There is no content fragment available to delegate to");
        }
        return contentFragment;
    }

    @Nullable
    @Override
    public String getTitle() {
        return getContentFragment().getTitle();
    }

    @Nullable
    @Override
    public String getDescription() {
        return getContentFragment().getDescription();
    }

    @Nullable
    @Override
    public String getType() {
        return getContentFragment().getType();
    }

    @Nullable
    @Override
    public List<ContentElement> getElements() {
        return getContentFragment().getElements();
    }

    @Nonnull
    @Override
    public Map<String, ContentElement> getExportedElements() {
        return getContentFragment().getExportedElements();
    }

    @Nonnull
    @Override
    public String[] getExportedElementsOrder() {
        return getContentFragment().getExportedElementsOrder();
    }

    @Nullable
    @Override
    public List<Resource> getAssociatedContent() {
        return getContentFragment().getAssociatedContent();
    }

    @Nonnull
    @Override
    public String getEditorJSON() {
        return getContentFragment().getEditorJSON();
    }

    @Nonnull
    @Override
    public String getPath() {
        return getContentFragment().getPath();
    }
}
