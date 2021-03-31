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
package com.adobe.cq.wcm.core.components.models.contentfragment;


import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.wcm.core.components.models.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Defines a Sling model used for the {@code /apps/core/wcm/components/contentfragmentlist} component.
 *
 * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
 */
@ConsumerType
public interface ContentFragmentList  extends Component {

    /**
     * Name of the property (in JSON export) that provides the content fragment items.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String JSON_PN_ITEMS = "items";

    /**
     * Name of the optional resource property that stores the names of the elements to be used.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_ELEMENT_NAMES = "elementNames";

    /**
     * Path of the model resource. Property is required.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_MODEL_PATH = "modelPath";

    /**
     * Name of the optional resource property that stores the tag names for filtering the result.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_TAG_NAMES = "tagNames";

    /**
     * Name of the optional resource property that stores the parent path of the content fragments.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    String PN_PARENT_PATH = "parentPath";

    /**
     * Name of the optional resource property that sets the query limit for the number of results to return.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.1.0
     */
    String PN_MAX_ITEMS= "maxItems";

    /**
     * Name of the optional resource property that stores the model element the list is sorted by.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.2.0
     */
    String PN_ORDER_BY = "orderBy";

    /**
     * Name of the optional resource property that sets the sort order of the list (ascending / descending).
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.2.0
     */
    String PN_SORT_ORDER = "sortOrder";

    /**
     * Returns a list of {@link DAMContentFragment content fragments}.
     *
     * @return the list of content fragments
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @JsonProperty(JSON_PN_ITEMS)
    default Collection<DAMContentFragment> getListItems() {
        return Collections.emptyList();
    }

    /**
     * Returns the type of the resource for which the export is performed.
     *
     * @since com.adobe.cq.wcm.core.components.models.contentfragment 1.0.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        return "";
    }
}
