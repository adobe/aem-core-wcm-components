/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.testing;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.SlingModelFilter;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ContainerExporter.class, ComponentExporter.class}, resourceType = MockResponsiveGrid.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class MockResponsiveGrid implements ContainerExporter {

    public static final String RESOURCE_TYPE = "wcm/foundation/components/responsivegrid";

    @Self
    private SlingHttpServletRequest request;

    @Inject
    SlingModelFilter slingModelFilter;

    @Inject
    ModelFactory modelFactory;

    /**
     * Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link ComponentExporter}.
     *
     * @param slingRequest The current request.
     * @return Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link ComponentExporter}.
     */
    @Nonnull
    private Map<String, ComponentExporter> getChildModels(@Nonnull SlingHttpServletRequest slingRequest) {
        Map<String, ComponentExporter> itemWrappers = new LinkedHashMap<>();
        ComponentManager componentManager = request.getResourceResolver().adaptTo(ComponentManager.class);

        for (final Resource child : slingModelFilter.filterChildResources(request.getResource().getChildren())) {
            Component component = componentManager.getComponentOfResource(child);
            Class<? extends ComponentExporter>  modelClass = ComponentExporter.class;
            if (component != null && component.getProperties().get(NameConstants.PN_IS_CONTAINER, false)) {
                modelClass = ContainerExporter.class;
            }
            ComponentExporter model = modelFactory.getModelFromWrappedRequest(slingRequest, child, modelClass);
            itemWrappers.put(child.getName(), model);
        }

        return  itemWrappers;
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return getChildModels(request);
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        Map<String, ? extends ComponentExporter> models = getExportedItems();

        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}
