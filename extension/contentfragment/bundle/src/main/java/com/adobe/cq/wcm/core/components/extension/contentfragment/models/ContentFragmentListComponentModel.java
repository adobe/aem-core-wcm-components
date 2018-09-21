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
package com.adobe.cq.wcm.core.components.extension.contentfragment.models;


import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.extension.contentfragment.models.v2.ContentFragment;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.osgi.annotation.versioning.ConsumerType;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Defines a Sling model used for the {@code /apps/core/wcm/extension/components/contentfragmentlist} component.
 *
 * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
 */
@ConsumerType
public interface ContentFragmentListComponentModel extends ComponentExporter {

    /**
     * Name of the property (in JSON export) that provides the all content fragment items.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    String JSON_PN_FRAGMENTS = "items";

    /**
     * Name of the optional resource property that stores the names of the elements to be used.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    String PN_ELEMENT_NAMES = "elementNames";

    /**
     * Path of the model resource. Property is required.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    String PN_MODEL_PATH = "modelPath";

    /**
     * Name of the optional resource property that stores the name of the variation to be used.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    String PN_TAG_NAME = "tagNames";

    /**
     * Name of the optional resource property that stores the name of the variation to be used.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    String PN_FOLDER_NAME = "parentPath";

    /**
     * Returns a list of {@link ContentFragment content fragments}.
     *
     * @return the list of content fragments
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    @Nonnull
    @JsonProperty(JSON_PN_FRAGMENTS)
    default Collection<ContentFragment> getListItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the type of the resource for which the export is performed.
     *
     * @since com.adobe.cq.wcm.core.components.extension.contentfragment.models 1.1.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
