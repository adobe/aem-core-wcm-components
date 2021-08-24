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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import java.io.Serializable;
import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.search.result.SearchResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.search.Predicate;
import com.day.cq.search.SimpleSearch;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * List model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends AbstractComponentImpl implements List {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v1/list";

    /**
     * Standard logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ListImpl.class);

    /**
     * Property name for the maximum search results if the source type is search.
     * Note: this is not the maximum number of list items returned.
     */
    private static final String PN_SEARCH_LIMIT = "limit";

    /**
     * Max items property name.
     */
    private static final String PN_MAX_ITEMS = "maxItems";

    /**
     * Children depth property name (only used if the source type is children).
     */
    private static final String PN_CHILD_DEPTH = "childDepth";

    /**
     * Search query property name.
     */
    private static final String PN_SEARCH_QUERY = "query";

    /**
     * Default flag indicating if the description should be shown.
     */
    private static final boolean SHOW_DESCRIPTION_DEFAULT = false;

    /**
     * Default flag indicating if the date should be shown.
     */
    private static final boolean SHOW_MODIFICATION_DATE_DEFAULT = false;

    /**
     * Default flag indicating if list items should be linked.
     */
    private static final boolean LINK_ITEMS_DEFAULT = false;

    /**
     * The default maximum search results if the source type is search.
     * Note: this is not the maximum number of list items returned.
     */
    private static final int SEARCH_LIMIT_DEFAULT = 100;

    /**
     * Default children depth if the source type is children.
     */
    private static final int CHILD_DEPTH_DEFAULT = 1;

    /**
     * Default date format.
     */
    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";

    /**
     * Default tag match requirement.
     */
    private static final String TAGS_MATCH_ANY_VALUE = "any";

    /**
     * Max items default.
     */
    private static final int MAX_ITEMS_DEFAULT = 0;

    /**
     * Component properties.
     */
    @ScriptVariable
    protected ValueMap properties;

    /**
     * The current style.
     */
    @ScriptVariable
    protected Style currentStyle;

    /**
     * The current page.
     */
    @ScriptVariable
    private Page currentPage;

    /**
     * Date format string.
     */
    private String dateFormatString;

    /**
     * Flag indicating if description should be shown.
     */
    protected boolean showDescription;

    /**
     * Flag indicating if modification date should be shown.
     */
    private boolean showModificationDate;

    /**
     * Flag indicating if items should be linked.
     */
    protected boolean linkItems;

    /**
     * The list items.
     */
    private java.util.List<Page> listItems;

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        // read design config properties
        showDescription = properties.get(PN_SHOW_DESCRIPTION, currentStyle.get(PN_SHOW_DESCRIPTION, SHOW_DESCRIPTION_DEFAULT));
        showModificationDate = properties.get(
            PN_SHOW_MODIFICATION_DATE, currentStyle.get(PN_SHOW_MODIFICATION_DATE, SHOW_MODIFICATION_DATE_DEFAULT));
        linkItems = properties.get(PN_LINK_ITEMS, currentStyle.get(PN_LINK_ITEMS, LINK_ITEMS_DEFAULT));
        dateFormatString = properties.get(PN_DATE_FORMAT, currentStyle.get(PN_DATE_FORMAT, DATE_FORMAT_DEFAULT));
    }

    @Override
    @Deprecated
    public Collection<Page> getItems() {
        if (listItems == null) {
            this.listItems = getPages();
        }
        return listItems;
    }

    @Override
    @JsonProperty("linkItems")
    public boolean linkItems() {
        return linkItems;
    }

    @Override
    @JsonProperty("showDescription")
    public boolean showDescription() {
        return showDescription;
    }

    @Override
    @JsonProperty("showModificationDate")
    public boolean showModificationDate() {
        return showModificationDate;
    }

    @Override
    public String getDateFormatString() {
        return dateFormatString;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    /**
     * Get the list source.
     * The list source will be determined from the component properties, or the style policy if not present in the
     * component properties.
     *
     * @return The list type.
     */
    @NotNull
    private Source getListType() {
        // Note: this can be done a lot cleaner in JDK 11.
        return Optional.ofNullable(
            // get the source from the properties
            Optional.ofNullable(properties.get(PN_SOURCE, String.class))
                // or get it from the style
                .orElseGet(() -> Optional.ofNullable(currentStyle.get(PN_SOURCE, String.class))
                    .orElse(null)))
            // convert the value to a source enum
            .map(Source::fromString)
            // default to empty if no value is found
            .orElse(Source.EMPTY);
    }

    /**
     * Get the list of pages.
     *
     * @return The list of pages.
     */
    protected java.util.List<Page> getPages() {
        // get the list item stream
        Stream<Page> itemStream;
        switch (getListType()) {
            case STATIC:
                itemStream = getStaticListItems();
                break;
            case CHILDREN:
                itemStream = getChildListItems();
                break;
            case TAGS:
                itemStream = getTagListItems();
                break;
            case SEARCH:
                itemStream = getSearchListItems();
                break;
            default:
                itemStream = Stream.empty();
                break;
        }

        // order the results
        OrderBy orderBy = OrderBy.fromString(properties.get(PN_ORDER_BY, StringUtils.EMPTY));
        if (orderBy != null) {
            SortOrder sortOrder = SortOrder.fromString(properties.get(PN_SORT_ORDER, SortOrder.ASC.value));
            itemStream = itemStream.sorted(new ListSort(orderBy, sortOrder, this.currentPage.getLanguage()));
        }

        int maxItems = properties.get(PN_MAX_ITEMS, MAX_ITEMS_DEFAULT);
        // limit the results
        if (maxItems != 0) {
            itemStream = itemStream.limit(maxItems);
        }

        // collect the results
        return itemStream.collect(Collectors.toList());
    }


    /**
     * Get the list items if using a static source.
     *
     * @return The static list items.
     */
    private Stream<Page> getStaticListItems() {
        return Arrays.stream(this.properties.get(PN_PAGES, new String[0]))
            .map(this.currentPage.getPageManager()::getContainingPage)
            .filter(Objects::nonNull);
    }

    /**
     * Get the list items if using children source.
     *
     * @return The child list items.
     */
    private Stream<Page> getChildListItems() {
        int childDepth = properties.get(PN_CHILD_DEPTH, CHILD_DEPTH_DEFAULT);
        return getRootPage(PN_PARENT_PAGE)
            .map(rootPage -> collectChildren(childDepth, rootPage))
            .orElseGet(Stream::empty);
    }

    /**
     * Get a stream of all children of the specified parent page not deeper than the specified max depth.
     * This call is recursive and expects that the caller uses startLevel = parent.getDepth().
     *
     * @param depth  The number of levels under the root page to be included.
     * @param parent The root page.
     * @return Stream of all children of the specified parent that are not deeper than the end level.
     */
    private static Stream<Page> collectChildren(int depth, @NotNull final Page parent) {
        if (depth <= 0) {
            return Stream.empty();
        }
        Iterator<Page> childIterator = parent.listChildren();
        return StreamSupport.stream(((Iterable<Page>) () -> childIterator).spliterator(), false)
            .flatMap(child -> Stream.concat(Stream.of(child), collectChildren(depth - 1, child)));
    }

    /**
     * Get the list items if using tag source.
     *
     * @return The tag list items.
     */
    private Stream<Page> getTagListItems() {
        boolean matchAny = properties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);
        return Optional.ofNullable(properties.get(PN_TAGS, String[].class))
            // only continue if there are tags
            .filter(ArrayUtils::isNotEmpty)
            // get the root page
            .flatMap(tags -> getRootPage(PN_TAGS_PARENT_PAGE)
                .flatMap(rootPage ->
                    // get the tag manager
                    Optional.ofNullable(resource.getResourceResolver().adaptTo(TagManager.class))
                        // find content tagged with the tags
                        .map(tagManager -> tagManager.find(rootPage.getPath(), tags, matchAny))
                        // convert the hits into pages
                        .map(iterator -> StreamSupport.stream(((Iterable<Resource>) () -> iterator).spliterator(), false)
                            .map(currentPage.getPageManager()::getContainingPage)
                            .filter(Objects::nonNull))))
            .orElseGet(Stream::empty);
    }

    /**
     * Get the list items if using the search source type.
     *
     * @return The search list items.
     */
    private Stream<Page> getSearchListItems() {
        Optional<Page> searchRoot = getRootPage(PN_SEARCH_IN);
        String query = properties.get(PN_SEARCH_QUERY, String.class);

        if (!StringUtils.isBlank(query) && searchRoot.isPresent()) {
            SimpleSearch search = resource.adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(searchRoot.get().getPath());
                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                int limit = properties.get(PN_SEARCH_LIMIT, SEARCH_LIMIT_DEFAULT);
                search.setHitsPerPage(limit);
                return safeGetSearchResult(search)
                    .map(SearchResult::getResources)
                    .map(it -> (Iterable<Resource>) () -> it)
                    .map(it -> StreamSupport.stream(it.spliterator(), false))
                    .orElseGet(Stream::empty)
                    .filter(Objects::nonNull)
                    .map(currentPage.getPageManager()::getContainingPage)
                    .filter(Objects::nonNull);
            }
        }
        return Stream.empty();
    }

    /**
     * Gets the search result, or empty if an exception occurs.
     *
     * @param search The search for which to get the results.
     * @return The search result, or empty if {@link RepositoryException} occurs.
     */
    @NotNull
    private Optional<SearchResult> safeGetSearchResult(@NotNull final SimpleSearch search) {
        try {
            return Optional.of(search.getResult());
        } catch (RepositoryException e) {
            LOGGER.error("Unable to retrieve search results for query.", e);
        }
        return Optional.empty();
    }

    /**
     * Get the root page.
     *
     * The root page is the page referenced by the fieldName property, or the current page if the fieldName property
     * is not set or is blank. This function will return empty only if the fieldName property is set, but the referenced
     * page does not exist.
     *
     * @param fieldName The name of the property containing the path of the root page.
     * @return The root page, or empty if the page does not exist.
     */
    @NotNull
    private Optional<Page> getRootPage(@NotNull final String fieldName) {
        Optional<String> parentPath = Optional.ofNullable(this.properties.get(fieldName, String.class))
            .filter(StringUtils::isNotBlank);

        // no path is specified, use current page
        if (!parentPath.isPresent()) {
            return Optional.of(this.currentPage);
        }

        // a path is specified, get that page or return null
        return parentPath
            .map(resource.getResourceResolver()::getResource)
            .map(currentPage.getPageManager()::getContainingPage);
    }

    /**
     * Sources.
     */
    protected enum Source {
        CHILDREN("children"),
        STATIC("static"),
        SEARCH("search"),
        TAGS("tags"),
        EMPTY(StringUtils.EMPTY);

        private final String value;

        Source(String value) {
            this.value = value;
        }

        /**
         * Get the source from the string value.
         *
         * @param value The value.
         * @return The source if it exists, or null if no source has a matching value.
         */
        @Nullable
        public static Source fromString(String value) {
            for (Source s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * Sort orders.
     */
    private enum SortOrder {
        /**
         * Ascending.
         */
        ASC("asc"),

        /**
         * Descending.
         */
        DESC("desc");

        private final String value;

        SortOrder(String value) {
            this.value = value;
        }

        /**
         * Get the sort order from string value.
         *
         * @param value The string value.
         * @return The sort order, or ascending if not found.
         */
        @NotNull
        public static SortOrder fromString(String value) {
            for (SortOrder s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ASC;
        }
    }

    /**
     * Order by options.
     */
    private enum OrderBy {
        /**
         * Order by page title.
         */
        TITLE("title"),

        /**
         * Order by last modified date.
         */
        MODIFIED("modified");

        private final String value;

        OrderBy(String value) {
            this.value = value;
        }

        /**
         * Get the order by from string value.
         *
         * @param value The string value.
         * @return The order by field, or null if not found.
         */
        @Nullable
        public static OrderBy fromString(String value) {
            for (OrderBy s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

    /**
     * Comparator for sorting pages.
     */
    private static class ListSort implements Comparator<Page>, Serializable {

        /**
         * Serial version UID.
         */
        private static final long serialVersionUID = -707429230313589969L;

        /**
         * The sort order
         */
        @Nullable
        private final SortOrder sortOrder;

        /**
         * Comparator for comparing pages.
         */
        @NotNull
        private final Comparator<Page> pageComparator;

        /**
         * Construct a page sorting comparator.
         *
         * @param orderBy The field to order by.
         * @param sortOrder The sort order.
         * @param locale Current locale.
         */
        ListSort(@Nullable final OrderBy orderBy, @Nullable final SortOrder sortOrder, @NotNull Locale locale) {
            this.sortOrder = sortOrder;

            if (orderBy == OrderBy.MODIFIED) {
                // getLastModified may return null, define null to be after nonnull values
                this.pageComparator = (a, b) -> ObjectUtils.compare(a.getLastModified(), b.getLastModified(), true);
            } else if (orderBy == OrderBy.TITLE) {
                Collator collator = Collator.getInstance(locale);
                collator.setStrength(Collator.PRIMARY);
                // getTitle may return null, define null to be greater than nonnull values
                Comparator<String> titleComparator = Comparator.nullsLast(collator);
                this.pageComparator = (a, b) -> titleComparator.compare(PageListItemImpl.getTitle(a), PageListItemImpl.getTitle(b));
            } else {
                this.pageComparator = (a, b) -> 0;
            }
        }

        @Override
        public int compare(@NotNull final Page item1, @NotNull final Page item2) {
            int i = this.pageComparator.compare(item1, item2);
            if (sortOrder == SortOrder.DESC) {
                i = i * -1;
            }
            return i;
        }
    }
}
