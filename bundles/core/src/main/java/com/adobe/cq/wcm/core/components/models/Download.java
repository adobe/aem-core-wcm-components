/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import javax.annotation.Nonnull;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code Download} Sling Model used for the {@code /apps/core/wcm/components/download} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.8.0
 */
public interface Download extends Component {

    /**
     * Name of the resource property that defines whether or not the title value is taken from the configured asset.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_TITLE_FROM_ASSET = "titleFromAsset";

    /**
     * Name of the resource property that defines whether or not the description value is taken from the configured asset.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_DESCRIPTION_FROM_ASSET = "descriptionFromAsset";

    /**
     * Name of the resource property that defines whether or not the download item should be displayed inline vs. attachment.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_INLINE = "inline";

    /**
     * Name of the policy property that defines the text to be displayed on the action.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_ACTION_TEXT = "actionText";

    /**
     * Name of the policy property that stores the value for this title's HTML element type.
     *
     * @see #getTitleType()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_TITLE_TYPE = "titleType";

    /**
     * Name of the policy property that defines whether the file's size will be displayed.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_DISPLAY_SIZE = "displaySize";

    /**
     * Name of the policy property that defines whether the file's format will be displayed.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_DISPLAY_FORMAT = "displayFormat";

    /**
     * Name of the policy property that defines whether the filename will be displayed.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    String PN_DISPLAY_FILENAME = "displayFilename";

    /**
     * Returns either the title configured in the dialog or the title of the DAM asset,
     * depending on the state of the titleFromAsset checkbox.
     *
     * @return the download title
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getTitle() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns either the description configured in the dialog or the description of the DAM asset,
     * depending on the state of the descriptionFromAsset checkbox.
     *
     * @return the download description
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getDescription() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the url to the asset.
     *
     * @return the asset url
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getUrl() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the action text from the dialog if it is configured there. Otherwise, it returns the value set in the
     * component policy.
     *
     * @return the action text
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getActionText() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the HTML element to be used for the title as defined in the component policy.
     *
     * @return the title header element type
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getTitleType() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the size of the file to be downloaded.
     *
     * @return the size of download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getSize() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the extension of file to be downloaded. Extension is mapped with the {@link org.apache.sling.commons.mime.MimeTypeService}
     * . If no mapping can be found the extension is extracted from the filename.
     *
     * @return the extension of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getExtension() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the file size should be displayed.
     *
     * @return {@code true} if the size should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displaySize() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the mime type of the file to be downloaded.
     *
     * @return the mime type of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getFormat() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the file format should be displayed.
     *
     * @return {@code true} if the format should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displayFormat() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the filename of the file to be downloaded.
     *
     * @return the filename of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getFilename() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the filename should be displayed.
     *
     * @return {@code true} if the filename should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displayFilename() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
