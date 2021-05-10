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
     * Name of the policy property that defines whether the title links should be hidden.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    String PN_HIDE_TITLE_LINK = "hideTitleLink";

    /**
     * Returns either the title configured in the dialog or the title of the DAM asset,
     * depending on the state of the titleFromAsset checkbox.
     *
     * @return the download title
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getTitle() {
        return null;
    }

    /**
     * Returns either the description configured in the dialog or the description of the DAM asset,
     * depending on the state of the descriptionFromAsset checkbox.
     *
     * @return the download description
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getDescription() {
        return null;
    }

    /**
     * Returns the url to the asset.
     *
     * @return the asset url
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getUrl() {
        return null;
    }

    /**
     * Returns the action text from the dialog if it is configured there. Otherwise, it returns the value set in the
     * component policy.
     *
     * @return the action text
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getActionText() {
        return null;
    }

    /**
     * Returns the HTML element to be used for the title as defined in the component policy.
     *
     * @return the title header element type
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getTitleType() {
        return null;
    }

    /**
     * Returns the size of the file to be downloaded.
     *
     * @return the size of download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getSize() {
        return null;
    }

    /**
     * Returns the extension of file to be downloaded. Extension is mapped with the {@link org.apache.sling.commons.mime.MimeTypeService}
     * . If no mapping can be found the extension is extracted from the filename.
     *
     * @return the extension of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getExtension() {
        return null;
    }

    /**
     * Checks if the file size should be displayed.
     *
     * @return {@code true} if the size should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displaySize() {
        return false;
    }

    /**
     * Returns the mime type of the file to be downloaded.
     *
     * @return the mime type of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getFormat() {
        return null;
    }

    /**
     * Checks if the file format should be displayed.
     *
     * @return {@code true} if the format should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displayFormat() {
        return false;
    }

    /**
     * Returns the filename of the file to be downloaded.
     *
     * @return the filename of the download file
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default String getFilename() {
        return null;
    }

    /**
     * Checks if the filename should be displayed.
     *
     * @return {@code true} if the filename should be displayed, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
    default boolean displayFilename() {
        return false;
    }

    /**
     * Checks if the title link should be hidden.
     *
     * @return {@code true} if the title link should be hidden, {@code false} if it should be rendered
     * @since com.adobe.cq.wcm.core.components.models 12.20.0
     */
    default boolean hideTitleLink() {
        return false;
    }

}
