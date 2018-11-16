/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import com.adobe.cq.export.json.ComponentExporter;

import javax.annotation.Nonnull;

/**
 * Sling model for the Download component.
 */
public interface Download extends ComponentExporter {

    String PN_TITLE_FROM_ASSET = "titleFromAsset";
    String PN_DESCRIPTION_FROM_ASSET = "descriptionFromAsset";


    /**
     * Returns either the title configured in the dialog or the title of the DAM asset,
     * depending on the state of the titleFromAsset checkbox.
     *
     * @return the download title
     */
    default String getTitle(){
        throw new UnsupportedOperationException();
    }

    /**
     * Returns either the description configured in the dialog or the title of the DAM asset,
     * depending on the state of the descriptionFromAsset checkbox.
     *
     * @return the download title
     */
    default String getDescription(){
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the url to the asset.
     *
     * @return the asset url
     */
    default String getDownloadUrl(){
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the button text from the dialog if it is configured there. Otherwise, it returns the value set in the
     * component policy.
     *
     * @return the button text
     */
    default String getCtaText()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the path to display the image representation of the asset, which is constructed using the core adaptive
     * image servlet.
     *
     * @return the path to the image representation of the asset.
     */
    default String getImagePath()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the element to be used for the title as defined in the component policy.
     *
     * @return the title header element type
     */
    default String getTitleType()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
