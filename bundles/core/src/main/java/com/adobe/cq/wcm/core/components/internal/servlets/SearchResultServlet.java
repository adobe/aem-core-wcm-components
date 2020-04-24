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
package com.adobe.cq.wcm.core.components.internal.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RangeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.request.RequestPathInfo;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.SearchImpl;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.*;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.selectors=" + SearchResultServlet.DEFAULT_SELECTOR,
                "sling.servlet.resourceTypes=cq/Page",
                "sling.servlet.extensions=json",
                "sling.servlet.methods=GET"
        }
)
public class SearchResultServlet extends SlingSafeMethodsServlet {

    protected static final String DEFAULT_SELECTOR = "searchresults";
    protected static final String PARAM_FULLTEXT = "fulltext";

    private static final String PARAM_RESULTS_OFFSET = "resultsOffset";
    private static final String PREDICATE_FULLTEXT = "fulltext";
    private static final String PREDICATE_TYPE = "type";
    private static final String PREDICATE_PATH = "path";
    private static final String NN_STRUCTURE = "structure";

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResultServlet.class);

    @Reference
    private transient QueryBuilder queryBuilder;

    @Reference
    private transient LanguageManager languageManager;

    @Reference
    private transient LiveRelationshipManager relationshipManager;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws IOException {
        Page currentPage = getCurrentPage(request);
        if (currentPage != null) {
            Resource searchResource = getSearchContentResource(request, currentPage);
            List<ListItem> results = getResults(request, searchResource, currentPage);
            writeJson(results, response);
        }
    }

    private Page getCurrentPage(SlingHttpServletRequest request) {
        Page currentPage = null;
        Resource currentResource = request.getResource();
        ResourceResolver resourceResolver = currentResource.getResourceResolver();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (pageManager != null) {
            currentPage = pageManager.getContainingPage(currentResource.getPath());
        }
        return currentPage;
    }

    private void writeJson(List<ListItem> results, SlingHttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(response.getWriter(), results);
        } catch (IOException e) {
            LOGGER.error("cannot serialize to JSON",e);
        }
    }

    private Resource getSearchContentResource(SlingHttpServletRequest request, Page currentPage) {
        Resource searchContentResource = null;
        RequestPathInfo requestPathInfo = request.getRequestPathInfo();
        Resource resource = request.getResource();
        String relativeContentResource = requestPathInfo.getSuffix();
        if (StringUtils.startsWith(relativeContentResource, "/")) {
            relativeContentResource = StringUtils.substring(relativeContentResource, 1);
        }
        if (StringUtils.isNotEmpty(relativeContentResource)) {
            searchContentResource = resource.getChild(relativeContentResource);
            if (searchContentResource == null) {
                PageManager pageManager = resource.getResourceResolver().adaptTo(PageManager.class);
                if (pageManager != null) {
                    Template template = currentPage.getTemplate();
                    if (template != null) {
                        Resource templateResource = request.getResourceResolver().getResource(template.getPath());
                        if (templateResource != null) {
                            searchContentResource = templateResource.getChild(NN_STRUCTURE + "/" + relativeContentResource);
                        }
                    }
                }
            }
        }
        return searchContentResource;
    }


    private List<ListItem> getResults(SlingHttpServletRequest request, Resource searchResource, Page currentPage) {
        int searchTermMinimumLength = SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT;
        int resultsSize = SearchImpl.PROP_RESULTS_SIZE_DEFAULT;
        String searchRootPagePath;
        if (searchResource != null) {
            ValueMap valueMap = searchResource.getValueMap();
            ValueMap contentPolicyMap = getContentPolicyProperties(searchResource, request.getResource());
            searchTermMinimumLength = valueMap.get(Search.PN_SEARCH_TERM_MINIMUM_LENGTH, contentPolicyMap.get(Search
                        .PN_SEARCH_TERM_MINIMUM_LENGTH, SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT));
            resultsSize = valueMap.get(Search.PN_RESULTS_SIZE, contentPolicyMap.get(Search.PN_RESULTS_SIZE,
                        SearchImpl.PROP_RESULTS_SIZE_DEFAULT));
            String searchRoot = valueMap.get(Search.PN_SEARCH_ROOT, contentPolicyMap.get(Search.PN_SEARCH_ROOT, SearchImpl.PROP_SEARCH_ROOT_DEFAULT));
            searchRootPagePath = getSearchRootPagePath(searchRoot, currentPage);
        } else {
            String languageRoot = languageManager.getLanguageRoot(currentPage.getContentResource()).getPath();
            searchRootPagePath = getSearchRootPagePath(languageRoot, currentPage);
        }
        if (StringUtils.isEmpty(searchRootPagePath)) {
            searchRootPagePath = currentPage.getPath();
        }
        List<ListItem> results = new ArrayList<>();
        String fulltext = request.getParameter(PARAM_FULLTEXT);
        if (fulltext == null || fulltext.length() < searchTermMinimumLength) {
            return results;
        }
        long resultsOffset = 0;
        if (request.getParameter(PARAM_RESULTS_OFFSET) != null) {
            resultsOffset = Long.parseLong(request.getParameter(PARAM_RESULTS_OFFSET));
        }
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put(PREDICATE_FULLTEXT, fulltext);
        predicatesMap.put(PREDICATE_PATH, searchRootPagePath);
        predicatesMap.put(PREDICATE_TYPE, NameConstants.NT_PAGE);
        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
        if (resultsSize != 0) {
            query.setHitsPerPage(resultsSize);
        }
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();

        List<Hit> hits = searchResult.getHits();
        if (hits != null) {
            for (Hit hit : hits) {
                try {
                    Resource hitRes = hit.getResource();
                    Page page = getPage(hitRes);
                    if (page != null) {
                        results.add(new PageListItemImpl(request, page, getId(searchResource), PageListItemImpl.PROP_DISABLE_SHADOWING_DEFAULT));
                    }
                } catch (RepositoryException e) {
                    LOGGER.error("Unable to retrieve search results for query.", e);
                }
            }
        }
        return results;
    }

    private String getId(Resource resource) {
        if (resource == null) {
            return null;
        }
        return Utils.generateId("search", resource.getPath());
    }

    private String getSearchRootPagePath(String searchRoot, Page currentPage) {
        String searchRootPagePath = null;
        PageManager pageManager = currentPage.getPageManager();
        if (StringUtils.isNotEmpty(searchRoot) && pageManager != null) {
            Page rootPage = pageManager.getPage(searchRoot);
            if (rootPage != null) {
                Page searchRootLanguageRoot = languageManager.getLanguageRoot(rootPage.getContentResource());
                Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
                RangeIterator liveCopiesIterator = null;
                try {
                    liveCopiesIterator = relationshipManager.getLiveRelationships(currentPage.adaptTo(Resource.class), null, null);
                } catch (WCMException e) {
                    // ignore it
                }
                if (searchRootLanguageRoot != null && currentPageLanguageRoot != null && !searchRootLanguageRoot.equals
                        (currentPageLanguageRoot)) {
                    // check if there's a language copy of the search root
                    Page languageCopySearchRoot = pageManager.getPage(ResourceUtil.normalize(currentPageLanguageRoot.getPath() + "/" +
                            getRelativePath(searchRootLanguageRoot, rootPage)));
                    if (languageCopySearchRoot != null) {
                        rootPage = languageCopySearchRoot;
                    }
                } else if (liveCopiesIterator != null) {
                    while (liveCopiesIterator.hasNext()) {
                        LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                        if (currentPage.getPath().startsWith(relationship.getTargetPath() + "/")) {
                            Page liveCopySearchRoot = pageManager.getPage(relationship.getTargetPath());
                            if (liveCopySearchRoot != null) {
                                rootPage = liveCopySearchRoot;
                                break;
                            }
                        }
                    }
                }
                searchRootPagePath = rootPage.getPath();
            }
        }
        return searchRootPagePath;
    }

    private ValueMap getContentPolicyProperties(Resource searchResource, Resource requestedResource) {
        ValueMap contentPolicyProperties = new ValueMapDecorator(new HashMap<>());
        ResourceResolver resourceResolver = searchResource.getResourceResolver();
        ContentPolicyManager contentPolicyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
        if (contentPolicyManager != null) {
            ContentPolicy policy = contentPolicyManager.getPolicy(searchResource);
            if (policy != null) {
                contentPolicyProperties = policy.getProperties();
            }
        }
        return contentPolicyProperties;
    }

    @Nullable
    private String getRelativePath(@NotNull Page root, @NotNull Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

    private Page getPage(Resource resource) {
        if (resource != null) {
            ResourceResolver resourceResolver = resource.getResourceResolver();
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            if (pageManager != null) {
                return pageManager.getContainingPage(resource);
            }
        }
        return null;
    }
}
