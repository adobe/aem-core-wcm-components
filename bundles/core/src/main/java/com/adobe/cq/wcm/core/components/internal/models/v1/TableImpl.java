/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Table;
import com.adobe.cq.wcm.core.components.services.table.ResourceProcessor;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.commons.util.DamUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Model(
    adaptables = SlingHttpServletRequest.class,
    adapters = {Table.class, ComponentExporter.class},
    resourceType = TableImpl.RESOURCE_TYPE
)

@Exporter(
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION
)
public class TableImpl extends AbstractComponentImpl implements Table {

    public static final String RESOURCE_TYPE = "core/wcm/components/table/v1/table";

    @ValueMapValue(name = "source", injectionStrategy = InjectionStrategy.REQUIRED)
    private String source;

    @ValueMapValue(name = "headerNames", injectionStrategy = InjectionStrategy.REQUIRED)
    private String[] headerNames;

    @ValueMapValue(name = "description", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String description;

    @ValueMapValue(name = "ariaLabel", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String ariaLabel;

    @Inject
    @Optional
    private List<ResourceProcessor> resourceProcessors;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<String> formattedTableHeaderNames;


    @PostConstruct
    public void initModel() {
        formatHeaderNames();
    }

    private String getSourceResourceMimeType(Resource sourceResource) {
        if (nonNull(sourceResource)) {
            if (DamUtil.isAsset(sourceResource)) {
                Asset asset = DamUtil.resolveToAsset(sourceResource);
                return nonNull(asset) ? asset.getMimeType() : StringUtils.EMPTY;
            } else {
                return StringUtils.EMPTY;
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * This method formats the property name to friendly names by removing jcr: prefix. Formatted
     * property names are used to display the headers in table.
     */
    private void formatHeaderNames() {
        formattedTableHeaderNames = new ArrayList<>();
        for (String propertyName : headerNames) {
            if (propertyName.contains("jcr:")) {
                formattedTableHeaderNames.add(propertyName.replace("jcr:", ""));
            } else {
                formattedTableHeaderNames.add(propertyName);
            }
        }
    }


    @Override
    public @NotNull List<String> getFormattedHeaderNames() {
        return formattedTableHeaderNames;
    }


    @Override
    public @NotNull List<List<String>> getItems() throws IOException {
        if (nonNull(resourceProcessors)) {
            Resource sourceResource = resourceResolver.getResource(source);
            if (nonNull(sourceResource)) {
                for (ResourceProcessor resourceProcessor : resourceProcessors) {
                    if (resourceProcessor.canProcess(getSourceResourceMimeType(sourceResource))) {
                        return resourceProcessor.processData(sourceResource, headerNames);
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    @NotNull
    @Override
    public String getDescription() {
        return StringUtils.defaultIfEmpty(description, StringUtils.EMPTY);
    }

    @NotNull
    @Override
    public String getAriaLabel() {
        return StringUtils.defaultIfEmpty(ariaLabel, StringUtils.EMPTY);
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    /*
     * DataLayerProvider implementation of field getters
     */

    @Nullable
    @Override
    public String getDataLayerText() {
        return StringUtils.defaultIfEmpty(description, StringUtils.EMPTY);
    }
}
