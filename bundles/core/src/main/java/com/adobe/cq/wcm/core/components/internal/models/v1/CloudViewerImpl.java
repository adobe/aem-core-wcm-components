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

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.CloudViewer;

import com.adobe.cq.wcm.core.components.internal.services.cloudviewer.CloudViewerConfigService;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { CloudViewer.class,
        ComponentExporter.class }, resourceType = { CloudViewerImpl.RESOURCE_TYPE })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CloudViewerImpl implements CloudViewer {

    protected static final String RESOURCE_TYPE = "core/wcm/components/cloudviewer/v1/cloudviewer";
    protected static final String FULL_WINDOW = "FULL_WINDOW";
    protected static final String SIZED_CONTAINER = "SIZED_CONTAINER";
    protected static final String IN_LINE = "IN_LINE";

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String documentPath;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String type;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String defaultViewMode;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String viewerHeight;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean borderless;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showAnnotationTools;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showFullScreen;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showLeftHandPanel;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showDownloadPdf;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showPrintPdf;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean showPageControls;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private boolean dockPageControls;

    @Inject
    private Resource resource;

    @Inject
    @Optional
    private CloudViewerConfigService config;

    @Override
    public String getClientId() {
        return config.getClientId();
    }

    @Override
    public String getReportSuiteId() {
        return config.getReportSuiteId();
    }

    @Override
    public String getDocumentPath() {
        return documentPath;
    }

    @Override 
    public String getDocumentFileName() {
        int index = documentPath.lastIndexOf("/");
        return documentPath.substring(index + 1);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getDefaultViewMode() {
        return defaultViewMode;
    }

    @Override
    public String getViewerHeight() {
        return viewerHeight;
    }

    @Override
    public boolean getBorderless() {
        return borderless;
    }

    @Override
    public boolean getShowAnnotationTools() {
        return showAnnotationTools;
    }

    @Override
    public boolean getShowFullScreen() {
        return showFullScreen;
    }

    @Override
    public boolean getShowLeftHandPanel() {
        return showLeftHandPanel;
    }

    @Override
    public boolean getShowDownloadPdf() {
        return showDownloadPdf;
    }

    @Override
    public boolean getShowPrintPdf() {
        return showPrintPdf;
    }

    @Override
    public boolean getShowPageControls() {
        return showPageControls;
    }

    @Override
    public boolean getDockPageControls() {
        return dockPageControls;
    }

    @Override
    public String getViewerConfigJson() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
        jsonObjectBuilder.add("embedMode", type);

        if(!StringUtils.isEmpty(type)) {
            if (type.equals(FULL_WINDOW)) {
                jsonObjectBuilder.add("defaultViewMode", defaultViewMode);
                jsonObjectBuilder.add("showAnnotationTools", showAnnotationTools);
                jsonObjectBuilder.add("showLeftHandPanel", showLeftHandPanel);
            }

            if (type.equals(SIZED_CONTAINER)) {
                jsonObjectBuilder.add("showFullScreen", showFullScreen);
            }

            if (type.equals(FULL_WINDOW) || type.equals("SIZED_CONTAINER")) {
                jsonObjectBuilder.add("showPageControls", showPageControls);
                jsonObjectBuilder.add("dockPageControls", dockPageControls);
            }
        }

        jsonObjectBuilder.add("showDownloadPDF", showDownloadPdf);
        jsonObjectBuilder.add("showPrintPDF", showPrintPdf);

        return jsonObjectBuilder.build().toString();
    }

    @Override
    public String getContainerClass() {
        String str = "adobe-dc-view-full-window";
        
        if(!StringUtils.isEmpty(type)) {
            if (type.equals(FULL_WINDOW) && borderless) {
                str = "adobe-dc-view-full-window-borderless";
            } else if (type.equals(SIZED_CONTAINER)) {
            str = "adobe-dc-view-sized-container";
            } else if (type.equals(IN_LINE)) {
            str = "";
            }
        }

        return str;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
