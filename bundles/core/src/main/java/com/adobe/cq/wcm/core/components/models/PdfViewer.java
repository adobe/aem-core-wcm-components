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
package com.adobe.cq.wcm.core.components.models;

import org.osgi.annotation.versioning.ConsumerType;

/**
 * Defines the {@code PdfViewer} component model.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.13.0
 */
@ConsumerType
public interface PdfViewer extends Component {

    /**
     * Defines the path of the PDF to display
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_DOCUMENT_PATH = "documentPath";

    /**
     * Defines the display type
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_DISPLAY_TYPE = "type";

    /**
     * Show PDF in full window
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String FULL_WINDOW = "FULL_WINDOW";

    /**
     * Show PDF in a sized container
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String SIZED_CONTAINER = "SIZED_CONTAINER";

    /**
     * Show PDF inline
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String IN_LINE = "IN_LINE";

    /**
     * Set to {@code true} to enable full screen borderless
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_BORDERLESS = "borderless";

    /**
     * Defines the display view
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_DEFAULT_VIEW_MODE = "defaultViewMode";

    /**
     * Set to {@code true} to enable annotation tools
     * 
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_ANNOTATION_TOOLS = "showAnnotationTools";

    /**
     * Set to {@code true} to display left side panel
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_LEFT_HAND_PANEL = "showLeftHandPanel";

    /**
     * Set to {@code true} to show full screen button
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_FULL_SCREEN = "showFullScreen";

    /**
     * Set to {@code true} to show page controls
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_PAGE_CONTROLS = "showPageControls";

    /**
     * Set to {@code true} to dock controls to bottom
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_DOCK_PAGE_CONTROLS = "dockPageControls";

    /**
     * Set to {@code true} to show download button
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_DOWNLOAD_PDF = "showDownloadPDF";

    /**
     * Set to {@code true} to show print button
     *
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String PN_SHOW_PRINT_PDF = "showPrintPDF";

    /**
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String CSS_FULL_WINDOW = "cmp-pdfviewer__full-window";

    /**
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String CSS_BORDERLESS = "cmp-pdfviewer__full-window-borderless";

    /**
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String CSS_SIZED_CONTAINER = "cmp-pdfviewer__sized-container";

    /**
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    String CSS_IN_LINE = "cmp-pdfviewer__in-line";
    
    /**
     * Returns Document Cloud Viewer embed type.
     *
     * @return Document Cloud Viewer embed type
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getType() {
        return null;
    }

    /**
     * Returns the path of the document
     *
     * @return Path of the document
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getDocumentPath() {
        return null;
    }

    /**
     * Returns the document file name
     *
     * @return Document file name
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getDocumentFileName() {
        return null;
    }

    /**
     * Returns the Document Cloud client id
     *
     * @return Document Cloud client id
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getClientId() {
        return null;
    }

    /**
     * Returns the Adobe Analytics report suite id
     *
     * @return Adobe Analytics report suite id
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getReportSuiteId() {
        return null;
    }

    /**
     * Returns the default view mode
     *
     * @return Default view mode
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getDefaultViewMode() {
        return null;
    }

    /**
     * Returns {@code true} if the document is to be diplayed borderless
     *
     * @return {@code true} if the document is to be diplayed borderless
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isBorderless() {
        return false;
    }

    /**
     * Returns {@code true} if the annotation tools should be displayed
     *
     * @return {@code true} if the annotation tools should be displayed
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowAnnotationTools() {
        return false;
    }

    /**
     * Returns {@code true} if the left hand panel should be showmn
     *
     * @return {@code true} if the left hand panel should be showmn
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowLeftHandPanel() {
        return false;
    }

    /**
     * Returns {@code true} if the fullscreen button should be shown
     *
     * @return {@code true} if the fullscreen button should be shown
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowFullScreen() {
        return false;
    }

    /**
     * Returns {@code true} if the download button should be shown
     *
     * @return {@code true} if the download button should be shown
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowDownloadPdf() {
        return false;
    }

    /**
     * Returns {@code true} if the print button should be shown
     *
     * @return {@code true} if the print button should be shown
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowPrintPdf() {
        return false;
    }

    /**
     * Returns {@code true} if the page controls should be shown
     *
     * @return {@code true} if the page controls should be shown
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isShowPageControls() {
        return false;
    }

    /**
     * Returns {@code true} if the page controls should be docked
     *
     * @return {@code true} if the page controls should be docked
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default boolean isDockPageControls() {
        return false;
    }

    /**
     * Returns the Document Cloud Viewer configuration JSON
     *
     * @return Document Cloud Viewer configuration JSON
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getViewerConfigJson() {
        return null;
    }

    /**
     * Returns the CSS class to be applied to the Document Cloud Viewer container
     *
     * @return one of defined {@code CSS_*} classes
     * @since com.adobe.cq.wcm.core.components.models 12.13.0
     */
    default String getContainerClass() {
        return null;
    }

}
