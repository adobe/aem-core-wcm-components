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
import com.adobe.cq.wcm.core.components.models.DocumentCloudViewer;

import com.adobe.cq.wcm.core.components.internal.services.documentcloudviewer.DocumentCloudViewerConfigService;

@Model(adaptables = SlingHttpServletRequest.class, adapters = { DocumentCloudViewer.class,
        ComponentExporter.class }, resourceType = { DocumentCloudViewerImpl.RESOURCE_TYPE })
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class DocumentCloudViewerImpl implements DocumentCloudViewer {

    protected static final String RESOURCE_TYPE = "core/wcm/components/documentcloudviewer/v1/documentcloudviewer";
    protected static final String FULL_WINDOW = "FULL_WINDOW";
    protected static final String SIZED_CONTAINER = "SIZED_CONTAINER";
    protected static final String IN_LINE = "IN_LINE";
    protected static final String EMBED_MODE = "embedMode";
    protected static final String DEFAULT_VIEW_MODE= "defaultViewMode";
    protected static final String SHOW_ANNOTATION_TOOLS = "showAnnotationTools";
    protected static final String SHOW_LEFT_HAND_PANEL = "showLeftHandPanel";
    protected static final String SHOW_FULL_SCREEN = "showFullScreen";
    protected static final String SHOW_PAGE_CONTROLS = "showPageControls";
    protected static final String DOCK_PAGE_CONTROLS = "dockPageControls";
    protected static final String SHOW_DOWNLOAD_PDF = "showDownloadPDF";
    protected static final String SHOW_PRINT_PDF = "showPrintPDF";
    protected static final String CSS_FULL_WINDOW = "cmp-documentcloudviewer__full-window";
    protected static final String CSS_BORDERLESS = "cmp-documentcloudviewer__full-window-borderless";
    protected static final String CSS_SIZED_CONTAINER = "cmp-documentcloudviewer__sized-container";
    protected static final String CSS_IN_LINE = "cmp-documentcloudviewer__in-line";

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
    private DocumentCloudViewerConfigService config;

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
        jsonObjectBuilder.add(EMBED_MODE, type);

        if(!StringUtils.isEmpty(type)) {
            if (type.equals(FULL_WINDOW)) {
                jsonObjectBuilder.add(DEFAULT_VIEW_MODE, defaultViewMode);
                jsonObjectBuilder.add(SHOW_ANNOTATION_TOOLS, showAnnotationTools);
                jsonObjectBuilder.add(SHOW_LEFT_HAND_PANEL, showLeftHandPanel);
            }

            if (type.equals(SIZED_CONTAINER)) {
                jsonObjectBuilder.add(SHOW_FULL_SCREEN, showFullScreen);
            }

            if (type.equals(FULL_WINDOW) || type.equals(SIZED_CONTAINER)) {
                jsonObjectBuilder.add(SHOW_PAGE_CONTROLS, showPageControls);
                jsonObjectBuilder.add(DOCK_PAGE_CONTROLS, dockPageControls);
            }
        }

        jsonObjectBuilder.add(SHOW_DOWNLOAD_PDF, showDownloadPdf);
        jsonObjectBuilder.add(SHOW_PRINT_PDF, showPrintPdf);

        return jsonObjectBuilder.build().toString();
    }

    @Override
    public String getContainerClass() {
        if(!StringUtils.isEmpty(type)) {
            if (type.equals(FULL_WINDOW) && borderless) {
                return CSS_BORDERLESS;
            } else if (type.equals(SIZED_CONTAINER)) {
                return CSS_SIZED_CONTAINER;
            } else if (type.equals(IN_LINE)) {
                return CSS_IN_LINE;
            }
        }
        return CSS_FULL_WINDOW;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }
}
