/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.List;
import com.day.cq.commons.RangeIterator;
import com.day.cq.search.Predicate;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonProperty;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl implements List {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v1/list";

    private static final Logger LOGGER = LoggerFactory.getLogger(ListImpl.class);

    private static final int LIMIT_DEFAULT = 100;
    private static final boolean SHOW_DESCRIPTION_DEFAULT = false;
    private static final boolean SHOW_MODIFICATION_DATE_DEFAULT = false;
    private static final boolean LINK_ITEMS_DEFAULT = false;
    private static final int PN_DEPTH_DEFAULT = 1;
    private static final String PN_DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
    private static final String TAGS_MATCH_ANY_VALUE = "any";

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @ScriptVariable
    private Page currentPage;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = LIMIT_DEFAULT)
    private int limit;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = PN_DEPTH_DEFAULT)
    private int childDepth;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(values = StringUtils.EMPTY)
    private String query;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Default(intValues = 0)
    private int maxItems;

    private String startIn;
    private SortOrder sortOrder;
    private OrderBy orderBy;
    private String dateFormatString;

    private boolean showDescription;
    private boolean showModificationDate;
    private boolean linkItems;

    private PageManager pageManager;
    protected java.util.List<Page> listItems;

    @PostConstruct
    private void initModel() {
        pageManager = resourceResolver.adaptTo(PageManager.class);
        readProperties();
    }

    private void readProperties() {
        // read edit config properties
        startIn = properties.get(PN_SEARCH_IN, currentPage.getPath());
        sortOrder = SortOrder.fromString(properties.get(PN_SORT_ORDER, SortOrder.ASC.value));
        orderBy = OrderBy.fromString(properties.get(PN_ORDER_BY, StringUtils.EMPTY));

        // read design config properties
        showDescription = properties.get(PN_SHOW_DESCRIPTION, currentStyle.get(PN_SHOW_DESCRIPTION, SHOW_DESCRIPTION_DEFAULT));
        showModificationDate = properties.get(
                PN_SHOW_MODIFICATION_DATE, currentStyle.get(PN_SHOW_MODIFICATION_DATE, SHOW_MODIFICATION_DATE_DEFAULT));
        linkItems = properties.get(PN_LINK_ITEMS, currentStyle.get(PN_LINK_ITEMS, LINK_ITEMS_DEFAULT));
        dateFormatString = properties.get(PN_DATE_FORMAT, currentStyle.get(PN_DATE_FORMAT, PN_DATE_FORMAT_DEFAULT));

    }

    @Override
    public Collection<Page> getItems() {
        if (listItems == null) {
            Source listType = getListType();
            populateListItems(listType);
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

    @Nonnull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    protected Source getListType() {
        String listFromValue = properties.get(PN_SOURCE, currentStyle.get(PN_SOURCE, StringUtils.EMPTY));
        return Source.fromString(listFromValue);
    }

    protected void populateListItems(Source listType) {
        switch (listType) {
            case STATIC:
                populateStaticListItems();
                break;
            case CHILDREN:
                populateChildListItems();
                break;
            case TAGS:
                populateTagListItems();
                break;
            case SEARCH:
                populateSearchListItems();
                break;
            default:
                listItems = new ArrayList<>();
                break;
        }
        sortListItems();
        setMaxItems();
    }


    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        String[] pagesPaths = properties.get(PN_PAGES, new String[0]);
        for (String path : pagesPaths) {
            Page page = pageManager.getContainingPage(path);
            if (page != null) {
                listItems.add(page);
            }
        }
    }

    private void populateChildListItems() {
        listItems = new ArrayList<>();
        Page rootPage = getRootPage(PN_PARENT_PAGE);
        if (rootPage != null) {
            collectChildren(rootPage.getDepth(), rootPage);
        }
    }

    private void collectChildren(int startLevel, Page parent) {
        Iterator<Page> childIterator = parent.listChildren();
        while (childIterator.hasNext()) {
            Page child = childIterator.next();
            listItems.add(child);
            if (child.getDepth() - startLevel < childDepth) {
                collectChildren(startLevel, child);
            }
        }
    }

    private void populateTagListItems() {
        listItems = new ArrayList<>();
        String[] tags = properties.get(PN_TAGS, new String[0]);
        boolean matchAny = properties.get(PN_TAGS_MATCH, TAGS_MATCH_ANY_VALUE).equals(TAGS_MATCH_ANY_VALUE);
        if (ArrayUtils.isNotEmpty(tags)) {
            Page rootPage = getRootPage(PN_TAGS_PARENT_PAGE);
            if (rootPage != null) {
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                if (tagManager != null) {
                    RangeIterator<Resource> resourceRangeIterator = tagManager.find(rootPage.getPath(), tags, matchAny);
                    if (resourceRangeIterator != null) {
                        while (resourceRangeIterator.hasNext()) {
                            Page containingPage = pageManager.getContainingPage(resourceRangeIterator.next());
                            if (containingPage != null) {
                                listItems.add(containingPage);
                            }
                        }
                    }
                }
            }
        }
    }

    private void populateSearchListItems() {
        listItems = new ArrayList<>();
        if (!StringUtils.isBlank(query)) {
            SimpleSearch search = resource.adaptTo(SimpleSearch.class);
            if (search != null) {
                search.setQuery(query);
                search.setSearchIn(startIn);
                search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                search.setHitsPerPage(limit);
                try {
                    collectSearchResults(search.getResult());
                } catch (RepositoryException e) {
                    LOGGER.error("Unable to retrieve search results for query.", e);
                }
            }
        }
    }

    private void collectSearchResults(SearchResult result) throws RepositoryException {
        for (Hit hit : result.getHits()) {
            Page containingPage = pageManager.getContainingPage(hit.getResource());
            if (containingPage != null) {
                listItems.add(containingPage);
            }
        }
    }

    private void sortListItems() {
        if (orderBy != null) {
            listItems.sort(new ListSort(orderBy, sortOrder));
        }
    }

    private void setMaxItems() {
        if (maxItems != 0) {
            java.util.List<Page> tmpListItems = new ArrayList<>();
            for (Page item : listItems) {
                if (tmpListItems.size() < maxItems) {
                    tmpListItems.add(item);
                } else {
                    break;
                }
            }
            listItems = tmpListItems;
        }
    }

    private Page getRootPage(String fieldName) {
        String parentPath = properties.get(fieldName, currentPage.getPath());
        return pageManager.getContainingPage(resourceResolver.getResource(parentPath));
    }


    protected enum Source {
        CHILDREN("children"),
        STATIC("static"),
        SEARCH("search"),
        TAGS("tags"),
        EMPTY(StringUtils.EMPTY);

        private String value;

        Source(String value) {
            this.value = value;
        }

        public static Source fromString(String value) {
            for (Source s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

    private enum SortOrder {
        ASC("asc"),
        DESC("desc");

        private String value;

        SortOrder(String value) {
            this.value = value;
        }

        public static SortOrder fromString(String value) {
            for (SortOrder s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return ASC;
        }
    }

    private enum OrderBy {
        TITLE("title"),
        MODIFIED("modified");

        private String value;

        OrderBy(String value) {
            this.value = value;
        }

        public static OrderBy fromString(String value) {
            for (OrderBy s : values()) {
                if (StringUtils.equals(value, s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

    private static class ListSort implements Comparator<Page>, Serializable {


        private static final long serialVersionUID = 204096578105548876L;
        private SortOrder sortOrder;
        private OrderBy orderBy;

        ListSort(OrderBy orderBy, SortOrder sortOrder) {
            this.orderBy = orderBy;
            this.sortOrder = sortOrder;
        }

        @Override
        public int compare(Page item1, Page item2) {
            int i = 0;
            if (orderBy == OrderBy.MODIFIED) {
                // getLastModified may return null, define null to be after nonnull values
                i = ObjectUtils.compare(item1.getLastModified(), item2.getLastModified(), true);
            } else if (orderBy == OrderBy.TITLE) {
                // getTitle may return null, define null to be greater than nonnull values
                i = ObjectUtils.compare(item1.getTitle(), item2.getTitle(), true);
            }

            if (sortOrder == SortOrder.DESC) {
                i = i * -1;
            }
            return i;
        }
    }
}
