/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.day.cq.wcm.api.Page;

/**
 * Defines the {@code List} Sling Model used for the {@code /apps/core/wcm/components/list} component. This component
 * currently only supports page lists.
 *
 * @since com.adobe.cq.wcm.core.components.models 11.0.0
 */
@ConsumerType
public interface List extends Component {

    /**
     * Name of the resource property indicating how the list will be built. Possible values are:
     *
     * <ul>
     *     <li><code>children</code> - the list will be built from the child pages of the page identified by {@link #PN_PARENT_PAGE}</li>
     *     <li><code>static</code> - the list will be built from a custom set of pages, stored by the {@link #PN_PAGES} property</li>
     *     <li><code>search</code> - the list will be built from the result of a search query</li>
     *     <li><code>tags</code> - the list will be built from the sub-pages of the page identified by {@link #PN_TAGS_PARENT_PAGE}
     *     which are tagged with the tags stored by the {@link #PN_TAGS} property</li>
     * </ul>
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_SOURCE = "listFrom";

    /**
     * Name of the resource property storing the list of pages to be rendered if the source of the list is <code>static</code>.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_PAGES = "pages";

    /**
     * Name of the resource property storing the root page from which to build the list if the source of the list is <code>children</code>.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_PARENT_PAGE = "parentPage";

    /**
     * Name of the resource property storing the root from where the tag search is performed.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_TAGS_PARENT_PAGE = "tagsSearchRoot";

    /**
     * Name of the resource property storing the tags that will be used for building the list if the source of the list is
     * <code>tags</code>.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_TAGS = "tags";

    /**
     * Name of the resource property indicating if the matching against tags can accept any tag from the tag list. The accepted value is
     * <code>any</code>.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_TAGS_MATCH = "tagsMatch";

    /**
     * Name of the boolean resource property indicating if the list items should render a description.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_SHOW_DESCRIPTION = "showDescription";

    /**
     * Name of the boolean resource property indicating if the list items should render the modification date of each item.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_SHOW_MODIFICATION_DATE = "showModificationDate";

    /**
     * Name of the boolean resource property indication if the items should render a link to the page they represent.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_LINK_ITEMS = "linkItems";

    /**
     * Name of the boolean resource property indication if the items should render a link to the page they represent.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_DISPLAY_ITEM_AS_TEASER = "displayItemAsTeaser";

    /**
     * Name of the resource property storing where a search should be performed if the source of the list is <code>search</code>.
     *
     * @see #PN_SOURCE
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_SEARCH_IN = "searchIn";

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
     * Name of the resource property indicating which date format should be used when the list items render their modification date.
     *
     * @since com.adobe.cq.wcm.core.components.models 11.0.0
     */
    String PN_DATE_FORMAT = "dateFormat";

    /**
     * Name of the component property indicating to which teaser component the list items should be delegated.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.21.0
     */
    String PN_TEASER_DELEGATE = "teaserDelegate";

    /**
     * Returns the list's items collection, as {@link Page} elements.
     *
     * @return {@link Collection} of {@link Page}s
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     * @deprecated since 12.1.0: use {@link List#getListItems} instead
     */
    @Deprecated
    default Collection<Page> getItems() {
        return null;
    }

    /**
     * Returns the list's items collection, as {@link ListItem}s elements.
     *
     * @return {@link Collection} of {@link ListItem}s
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    default Collection<ListItem> getListItems() {
        return Collections.emptyList();
    }

    /**
     * Returns {@code true} if the list's items should be displayed as teasers.
     *
     * @return {@code true} if the items should be displayed as teasers, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.21.0
     */
    default boolean displayItemAsTeaser() {
        return false;
    }

    /**
     * Returns {@code true} if the list's items should link to the corresponding {@link Page}s they represent.
     *
     * @return {@code true} if the pages should be linked, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean linkItems() {
        return false;
    }

    /**
     * Returns {@code true} if the list's items should render their description.
     *
     * @return {@code true} if page description should be shown, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean showDescription() {
        return false;
    }

    /**
     * Returns {@code true} if the list's items should render their last modification date.
     *
     * @return {@code true} if modification date should be shown, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default boolean showModificationDate() {
        return false;
    }

    /**
     * Returns the date format used to display the last modification date of the list's items.
     *
     * @return format to use for the display of the last modification date.
     * @see #showModificationDate()
     * @since com.adobe.cq.wcm.core.components.models 11.0.0; marked <code>default</code> in 12.1.0
     */
    default String getDateFormatString() {
        return null;
    }

}
