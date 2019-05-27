/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.adobe.cq.export.json.ComponentExporter;
import com.day.cq.wcm.api.Page;

import com.adobe.cq.wcm.core.components.models.ImageListItem;
/**
 * Defines the {@code List} Sling Model used for the {@code /apps/core/wcm/components/list} component. This component
 * currently only supports page lists.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface ImageList extends ComponentExporter {

    /**
     * Name of the resource property storing the list of image link to be rendered.
     *
     * @see #PN_IMAGE_LIST
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_IMAGE_LIST = "imageList";

    /**
     * Name of the boolean resource property indication if the items should render a link to the page they represent.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_LINK_ITEMS = "linkItems";

    /**
     * Name of the resource property indicating how the list items should be sorted. Possible values: <code>asc</code>, <code>desc</code>.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_SORT_ORDER = "sortOrder";

    /**
     * Name of the resource property indicating by which criterion the sort is performed. Possible value: <code>title</code>,
     * <code>modified</code>.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_ORDER_BY = "orderBy";

    /**
     * Returns the list's items collection, as {@link ListItem}s elements.
     *
     * @return {@link Collection} of {@link ListItem}s
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    default Collection<ImageListItem> getListItems() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }

}
