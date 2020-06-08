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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {Table.class, ComponentExporter.class},
    resourceType = TableImpl.RESOURCE_TYPE)

@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TableImpl extends AbstractComponentImpl implements Table {

    public static final String RESOURCE_TYPE = "core/wcm/components/table/v1/table";
    @ValueMapValue(name = "source", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String source;

    @ValueMapValue(name = "headerNames", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String[] headerNames;

    @ValueMapValue(name = "title", injectionStrategy = InjectionStrategy.OPTIONAL)
    private String title;

    @Inject
    @Optional
    private List<ResourceProcessor> resourceProcessors;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<String> formattedTableHeaderNames;
    private List<List<String>> items;


    @PostConstruct
    public void initModel() throws IOException {
        Resource resource = resourceResolver.getResource(source);
        if (!isNull(resource)) {
            if (nonNull(resourceProcessors)) {
                for (ResourceProcessor resourceProcessor : resourceProcessors) {
                    if (resourceProcessor.canProcess(getResourceMimeType())) {
                        items = resourceProcessor.processData(resource, headerNames);
                        break;
                    }
                }
            }
            formatPropertyNames();
        }
    }

    private String getResourceMimeType() {
        if (nonNull(resource) && DamUtil.isAsset(resource)) {
            Asset asset = DamUtil.resolveToAsset(resource);
            return asset.getMimeType();
        }
        return StringUtils.EMPTY;
    }

    /**
     * This method formats the property name to friendly names by removing jcr: prefix. Formatted
     * property names are used to display the headers in table.
     */
    private void formatPropertyNames() {
        formattedTableHeaderNames = new ArrayList<>();
        for (String propertyName : headerNames) {
            if (propertyName.contains("jcr:")) {
                formattedTableHeaderNames.add(propertyName.substring(propertyName.indexOf("jcr:")));
            } else {
                formattedTableHeaderNames.add(propertyName);
            }
        }
    }

    @Override
    public List<String> getFormattedHeaderNames() {
        return formattedTableHeaderNames;
    }

    @Override
    public List<List<String>> getItems() {
        return items;
    }

    @Override
    public String getDescription() {
        return title;
    }
}
