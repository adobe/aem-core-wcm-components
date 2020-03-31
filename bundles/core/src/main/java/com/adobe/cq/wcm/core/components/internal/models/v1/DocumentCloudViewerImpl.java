/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.ConfigurationBuilder;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.DocumentCloudViewer;
import com.adobe.cq.wcm.core.config.DocumentCloud;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {DocumentCloudViewer.class, ComponentExporter.class}, resourceType = DocumentCloudViewerImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DocumentCloudViewerImpl implements DocumentCloudViewer {

    protected static final String RESOURCE_TYPE = "core/wcm/components/pdf/v1/pdf";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Resource resource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fileUrl;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String fileName;


    private String clientId;

    @Override
    public String getClientId() {
        if (StringUtils.isEmpty(clientId)) {
            setClientId();
        }
        return clientId;
    }

    @Override
    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    private void setClientId() {
        ConfigurationBuilder configurationBuilder = resource.adaptTo(ConfigurationBuilder.class);
        if (configurationBuilder != null) {
            DocumentCloud documentCloud = configurationBuilder.as(DocumentCloud.class);
            clientId = documentCloud.clientId();
        }
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }
}
