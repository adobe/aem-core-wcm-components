/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import org.jetbrains.annotations.NotNull;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

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
     * Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link T}.
     *
     * @param slingRequest The current request.
     * @param modelClass  The Sling Model class to be adapted to.
     * @return Returns a map (resource name => Sling Model class) of the given resource children's Sling Models that can be adapted to {@link T}.
     */
    @NotNull
    private <T> Map<String, T> getChildModels(@NotNull SlingHttpServletRequest slingRequest,
                                              @NotNull Class<T> modelClass) {
        Map<String, T> itemWrappers = new LinkedHashMap<>();

        for (final Resource child : slingModelFilter.filterChildResources(request.getResource().getChildren())) {
            itemWrappers.put(child.getName(), modelFactory.getModelFromWrappedRequest(slingRequest, child, modelClass));
        }

        return  itemWrappers;
    }

    @NotNull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return getChildModels(request, ComponentExporter.class);
    }

    @NotNull
    @Override
    public String[] getExportedItemsOrder() {
        Map<String, ? extends ComponentExporter> models = getExportedItems();

        if (models.isEmpty()) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        return models.keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}
