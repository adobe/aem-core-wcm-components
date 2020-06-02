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

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface DocumentCloudViewer extends Component {

    default String getType() {
        throw new UnsupportedOperationException();
    }

    default String getDocumentPath() {
        throw new UnsupportedOperationException();
    }

    default String getDocumentFileName() {
        throw new UnsupportedOperationException();
    }

    default String getClientId() {
        throw new UnsupportedOperationException();
    }

    default String getReportSuiteId() {
        throw new UnsupportedOperationException();
    }

    default String getDefaultViewMode() {
        throw new UnsupportedOperationException();
    }

    default String getViewerHeight() {
        throw new UnsupportedOperationException();
    }

    default boolean getBorderless() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowAnnotationTools() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowLeftHandPanel() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowFullScreen() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowDownloadPdf() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowPrintPdf() {
        throw new UnsupportedOperationException();
    }

    default boolean getShowPageControls() {
        throw new UnsupportedOperationException();
    }

    default boolean getDockPageControls() {
        throw new UnsupportedOperationException();
    }

    default String getViewerConfigJson() {
        throw new UnsupportedOperationException();
    }

    default String getContainerClass() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
