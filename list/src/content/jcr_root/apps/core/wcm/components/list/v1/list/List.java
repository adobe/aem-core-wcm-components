/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2016 Adobe Systems Incorporated
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
package apps.core.wcm.components.list.v1.list;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.request.RequestParameterMap;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUsePojo;
import com.adobe.cq.wcm.core.components.commons.AuthoringUtils;
import com.day.cq.commons.RangeIterator;
import com.day.cq.search.Predicate;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.SimpleSearch;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

public class List extends WCMUsePojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(List.class);

    public static final String PROP_ALLOW_DUPLICATES = "allowDuplicates";
    public static final String PROP_SOURCE = "listFrom";
    public static final String PROP_QUERY = "query";
    public static final String PROP_SEARCH_IN = "searchIn";
    public static final String PROP_TYPE = "displayAs";
    public static final String PROP_ORDER_BY = "orderBy";
    public static final String PROP_LIMIT = "limit";
    public static final String PROP_PAGE_MAX = "pageMax";
    public static final String PROP_ORDERED = "ordered";
    public static final String PROP_PARENT_PAGE = "parentPage";
    public static final String PROP_FEED_ENABLED = "feedEnabled";
    public static final String PROP_SAVED_QUERY = "savedquery";
    public static final String PROP_TAG_SEARCH_ROOT = "tagsSearchRoot";
    public static final String PROP_TAGS = "tags";
    public static final String PROP_TAGS_MATCH = "tagsMatch";
    public static final String PROP_PAGES = "pages";
    public static final String PROP_ITEM_TYPE = "itemType";
    public static final String PROP_STYLE = "style";

    public static final String PARAM_PAGE_START = "start";
    public static final String PARAM_PAGE_MAX = "max";
    public static final String TYPE_DEFAULT = "default";
    public static final int PAGE_MAX_DEFAULT = -1;
    public static final int LIMIT_DEFAULT = 100;

    public static final String SOURCE_CHILDREN = "children";
    public static final String SOURCE_STATIC = "static";
    public static final String SOURCE_SEARCH = "search";
    public static final String SOURCE_QUERYBUILDER = "querybuilder";
    public static final String SOURCE_TAGS = "tags";
    public static final String PN_FILE_REFERENCE = "fileReference";


    private SlingHttpServletRequest request;
    private PageManager pageManager;

    private Resource resource;
    private ValueMap properties;
    private boolean allowDuplicates;
    private String listSource;
    private String query;
    private String startIn;
    private String type;
    private String orderBy;
    private int limit;
    private Integer pageMax;
    private String ordered;
    private boolean feedEnabled;
    private String savedquery;
    private String tagsSearchRoot;
    private String tags;
    private String tagsMatch;
    private String pages;
    private String itemType;
    private String style;

    private String listHTMLElement;
    private String listId;
    private Integer pageStart = 0;
    private java.util.List<Page> resultPages;
    private java.util.List<ListItem> listItems = new ArrayList<ListItem>();

    public void activate() {
        request = getRequest();
        pageManager = getPageManager();
        properties = getProperties();
        resource = getResource();
        ResourceResolver resourceResolver = resource.getResourceResolver();
        readListConfiguration();
        listHTMLElement = Boolean.valueOf(ordered) ? "ol" : "ul";
        Source source = Source.getSource(listSource);
        if (source == null) {
            throw new IllegalArgumentException("Unknown list source: " + listSource);
        }
        Iterator<Page> pageIterator = Collections.emptyIterator();
        if (source == Source.CHILDREN) {
            String parentPath = properties.get(PROP_PARENT_PAGE, resource.getPath());
            Resource parentResource = resource.getPath().equals(parentPath) ? resource : resourceResolver.getResource(parentPath);
            Page startPage = pageManager.getContainingPage(parentResource);
            if (startPage != null) {
                pageIterator = startPage.listChildren();
            } else {
                pageIterator = Collections.emptyIterator();
            }
        } else if (source == Source.QUERYBUILDER) {
            QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
            Session session = resourceResolver.adaptTo(Session.class);
            if (queryBuilder != null && session != null) {
                try {
                    Query query = queryBuilder.loadQuery(resource.getPath() + "/" + PROP_SAVED_QUERY, session);
                    if (query != null) {
                        query.setHitsPerPage(limit);
                        SearchResult result = query.getResult();
                        pageIterator = new HitPageIterator(pageManager, result.getHits().iterator(), !allowDuplicates);
                    }
                } catch (Exception e) {
                    LOGGER.error("Unable to load stored query for " + resource.getPath(), e);
                }
            } else {
                LOGGER.error("Error loading query builder.");
            }
        } else if (source == Source.SEARCH) {
            if (!StringUtils.isEmpty(query)) {
                SimpleSearch search = resource.adaptTo(SimpleSearch.class);
                if (search != null) {
                    search.setQuery(query);
                    search.setSearchIn(startIn);
                    search.addPredicate(new Predicate("type", "type").set("type", NameConstants.NT_PAGE));
                    search.setHitsPerPage(limit);
                    try {
                        SearchResult result = search.getResult();
                        pageIterator = new HitPageIterator(pageManager, result.getHits().iterator(), !allowDuplicates);
                    } catch (RepositoryException e) {
                        LOGGER.error("Unable to retrieve search results for query.", e);
                    }
                }
            }
        } else if (source == Source.TAGS) {
            String parentPath = properties.get(PROP_TAG_SEARCH_ROOT, resource.getPath());
            String[] tags = properties.get(PROP_TAGS, new String[0]);
            boolean matchAny = properties.get(PROP_TAGS_MATCH, "any").equals("any");
            Page startPage = pageManager.getPage(parentPath);
            if (startPage != null && tags.length > 0) {
                TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
                RangeIterator<Resource> rangeIterator = tagManager.find(startPage.getPath(), tags, matchAny);
                java.util.List<Page> taggedPages = new ArrayList<Page>();
                while (rangeIterator.hasNext()) {
                    Resource r = rangeIterator.next();
                    taggedPages.add(pageManager.getContainingPage(r));
                }
                pageIterator = taggedPages.iterator();
            }
        } else if (source == Source.STATIC) {
            String[] pagesPaths = properties.get(PROP_PAGES, new String[0]);
            java.util.List<Page> pages = new ArrayList<Page>(pagesPaths.length);
            for (String path : pagesPaths) {
                Page page = pageManager.getContainingPage(path);
                if (page != null) {
                    pages.add(page);
                }
            }
            pageIterator = pages.iterator();
        }
        if (!pageIterator.hasNext() && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cannot find any elements for this list.");
        } else {
            resultPages = new ArrayList<Page>();
            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                resultPages.add(page);
            }
            if (StringUtils.isNotEmpty(orderBy)) {
                Collections.sort(resultPages, new PageComparator(orderBy));
            }
            if (resultPages.size() > limit) {
                resultPages = resultPages.subList(0, limit);
            }
            int index = 0, count = 0;
            for (Page p : resultPages) {
                if (pageMax > 0 && count >= pageMax) {
                    break;
                }
                if (pageStart >= 0 && index < pageStart) {
                    index++;
                    continue;
                }
                ListItem item = new ListItem(p.getTitle(), p.getPath(), p.getLastModified(), p.getDescription(), hasImage(p.adaptTo
                        (Resource.class)));
                listItems.add(item);
                count++;
            }
        }

    }


    public java.util.List<ListItem> getItems() {
        return listItems;
    }

    public String getListHTMLElement() {
        return listHTMLElement;
    }

    public String getType() {
        return type;
    }

    public boolean isEmpty() {
        return listItems.isEmpty();
    }

    public boolean isPaginating() {
        return pageStart > 0 || (pageMax > 0 && resultPages.size() > pageMax);
    }

    public boolean isFeedEnabled() {
        return feedEnabled;
    }

    public String nextLink() {
        if (isPaginating() && pageMax > 0) {
            if (pageStart + pageMax < resultPages.size()) {
                int start = pageStart + pageMax;
                PageLink link = new PageLink(request);
                link.addOrReplaceParameter(listId + "_" + PARAM_PAGE_START, Integer.toString(start));
                return link.getValue();
            }
        }
        return null;
    }

    public String previousLink() {
        if (isPaginating()) {
            if (pageStart > 0) {
                int start = pageMax > 0 && pageStart > pageMax ? pageStart - pageMax : 0;
                PageLink link = new PageLink(request);
                link.addOrReplaceParameter(listId + "_" + PARAM_PAGE_START, Integer.toString(start));
                return link.getValue();
            }
        }
        return null;
    }

    public String getAccessibleNextDescriptionId() {
        return properties.get("accessibleNext") != null ? "cq_" + listId + "_next" : "";
    }

    public String getAccessiblePreviousDescriptionId() {
        return properties.get("accessiblePrevious") != null ? "cq_" + listId + "_previous" : "";
    }

    public String getListId() {
        return listId;
    }

    public int getPageStart() {
        return pageStart;
    }

    public String getItemType() {
        return itemType;
    }

    public String getStyle() {
        return style;
    }

    public boolean isTouch() {
        return AuthoringUtils.isTouch(request);
    }

    /**
     * A {@link List} is composed of one or more {@code ListItems}, usually backed by a {@link Page} or a {@link Resource}.
     */
    public class ListItem {
        private String name;
        private String path;
        private Calendar modifiedDate;
        private String description;
        private boolean hasImage;

        /**
         * Create a {@code ListItem}.
         *
         * @param name         the item's name
         * @param path         the item's path
         * @param modifiedDate the item's last modified date
         * @param description  the item's description
         * @param hasImage     a flag that indicates if the item also has an image available
         */
        public ListItem(String name, String path, Calendar modifiedDate, String description, boolean hasImage) {
            this.name = name;
            this.path = path;
            this.modifiedDate = modifiedDate;
            this.description = description;
            this.hasImage = hasImage;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public Calendar getModifiedDate() {
            return modifiedDate;
        }

        public String getDescription() {
            return description;
        }

        public boolean hasImage() {
            return hasImage;
        }
    }

    public enum Source {

        CHILDREN(SOURCE_CHILDREN),
        STATIC(SOURCE_STATIC),
        SEARCH(SOURCE_SEARCH),
        QUERYBUILDER(SOURCE_QUERYBUILDER),
        TAGS(SOURCE_TAGS);

        private String value;

        Source(String value) {
            this.value = value;
        }

        public static Source getSource(String value) {
            for (Source s : values()) {
                if (value.equals(s.value)) {
                    return s;
                }
            }
            return null;
        }
    }

    class PageComparator implements Comparator<Page> {

        private String property;

        PageComparator(String property) {
            this.property = property;
        }

        @Override
        public int compare(Page p1, Page p2) {
            if (isDateProperty(property)) {
                Calendar d1 = p1.getProperties().get(property, Calendar.class);
                Calendar d2 = p2.getProperties().get(property, Calendar.class);
                return d1.compareTo(d2);
            }
            return p1.getProperties().get(property, "").compareTo(p2.getProperties().get(property, ""));
        }

        private boolean isDateProperty(String property) {
            return JcrConstants.JCR_CREATED.equals(property) || NameConstants.PN_PAGE_LAST_MOD.equals(property) || NameConstants
                    .PN_PAGE_LAST_PUBLISHED.equals(property);
        }


    }

    class PageLink {

        public static final String HTML_EXTENSION = ".html";

        private String path;
        private Map<String, String> parametersMap;

        public PageLink(SlingHttpServletRequest request) {
            path = resource.getPath() + HTML_EXTENSION;
            RequestParameterMap rpm = request.getRequestParameterMap();
            parametersMap = new HashMap<String, String>(rpm.size());
            for (Map.Entry<String, RequestParameter[]> entry : request.getRequestParameterMap().entrySet()) {
                RequestParameter requestParameter = entry.getValue()[0];
                parametersMap.put(entry.getKey(), requestParameter.getString());
            }
        }

        public void addOrReplaceParameter(String parameterName, String value) {
            parametersMap.put(parameterName, value);
        }

        public String getValue() {
            StringBuilder url = new StringBuilder(path);
            int i = 0;
            int paramNo = parametersMap.size();
            if (paramNo > 0) {
                url.append("?");
            }
            for (Map.Entry<String, String> entry : parametersMap.entrySet()) {
                url.append(entry.getKey()).append("=").append(entry.getValue());
                if (i != paramNo - 1) {
                    url.append("&");
                }
            }
            return url.toString();
        }
    }

    private class HitPageIterator extends AbstractPageIterator {

        private Iterator<Hit> hitIterator;

        public HitPageIterator(PageManager pageManager, Iterator<Hit> hitIterator, boolean avoidDuplicates) {
            this.pageManager = pageManager;
            this.hitIterator = hitIterator;
            if (avoidDuplicates) {
                seen = new HashSet<String>();
            }
            nextPage = seek();
        }

        @Override
        protected Page seek() {
            while (hitIterator != null && hitIterator.hasNext()) {
                try {
                    Page page = pageManager.getContainingPage(hitIterator.next().getResource());
                    if (page != null && (seen != null || seen.add(page.getPath()))) {
                        return page;
                    }
                } catch (RepositoryException e) {
                    LOGGER.error("Cannot get page for search result hit.", e);
                }

            }
            return null;
        }
    }

    private abstract class AbstractPageIterator implements Iterator<Page> {
        protected PageManager pageManager;
        protected Page nextPage;
        protected Set<String> seen;

        protected abstract Page seek();

        @Override
        public boolean hasNext() {
            return nextPage != null;
        }

        @Override
        public Page next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Page page = nextPage;
            nextPage = seek();
            return page;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private void readListConfiguration() {
        allowDuplicates = properties.get(PROP_ALLOW_DUPLICATES, false);
        listSource = properties.get(PROP_SOURCE, String.class);
        if (StringUtils.isEmpty(listSource)) {
            listSource = SOURCE_CHILDREN;
        }
        query = properties.get(PROP_QUERY, String.class);
        startIn = properties.get(PROP_SEARCH_IN, String.class);
        if (StringUtils.isEmpty(startIn)) {
            startIn = ResourceUtil.getParent(resource.getPath(), 1);
        }
        type = properties.get(PROP_TYPE, String.class);
        if (StringUtils.isEmpty(type)) {
            type = TYPE_DEFAULT;
        }
        orderBy = properties.get(PROP_ORDER_BY, String.class);
        limit = properties.get(PROP_LIMIT, 0);
        if (limit == 0) {
            limit = LIMIT_DEFAULT;
        }
        ordered = properties.get(PROP_ORDERED, String.class);
        feedEnabled = properties.get(PROP_FEED_ENABLED, Boolean.FALSE);
        pageMax = properties.get(PROP_PAGE_MAX, 0);
        if (pageMax == 0) {
            pageMax = PAGE_MAX_DEFAULT;
        }
        itemType = properties.get(PROP_ITEM_TYPE, String.class);
        style = properties.get(PROP_STYLE, String.class);

        listId = getGeneratedId();

        Integer pageStartFromRequest = getIntegerRequestParameter(getListSpecificParameterName(PARAM_PAGE_START, listId));
        if (pageStartFromRequest != null) {
            pageStart = pageStartFromRequest;
            if (pageStart < 0) {
                pageStart = 0;
            }
        }

        Integer pageMaxFromRequest = getIntegerRequestParameter(getListSpecificParameterName(PARAM_PAGE_MAX, listId));
        if (pageMaxFromRequest != null) {
            pageMax = pageMaxFromRequest;
        }
        if (pageMax < 0) {
            pageMax = PAGE_MAX_DEFAULT;
        }
    }

    private String getGeneratedId() {
        String path = resource.getPath();
        String inJcrContent = JcrConstants.JCR_CONTENT + "/";
        int root = path.indexOf(inJcrContent);
        if (root >= 0) {
            path = path.substring(root + inJcrContent.length());
        }
        return path.replace("/", "_");
    }

    private String getListSpecificParameterName(String parameterName, String listId) {
        return listId + "_" + parameterName;
    }

    private Integer getIntegerRequestParameter(String parameterName) {
        String parameterValue = request.getParameter(parameterName);
        if (parameterValue == null) {
            return null;
        }
        try {
            return Integer.parseInt(parameterValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasImage(Resource resource) {
        Resource jcrContent = resource.getChild(JcrConstants.JCR_CONTENT);
        if (jcrContent != null) {
            ValueMap jcrContentProperties = jcrContent.getValueMap();
            if (jcrContentProperties.containsKey(PN_FILE_REFERENCE)) {
                return true;
            } else {
                // search for an image name under jcr:content
                Resource imageResource = jcrContent.getChild("image");
                if (imageResource != null) {
                    ValueMap imageResourceProperties = imageResource.getValueMap();
                    if (imageResourceProperties.containsKey(PN_FILE_REFERENCE)) {
                        return true;
                    } else {
                        Resource imageResourceFile = imageResource.getChild("file");
                        if (imageResourceFile != null) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
