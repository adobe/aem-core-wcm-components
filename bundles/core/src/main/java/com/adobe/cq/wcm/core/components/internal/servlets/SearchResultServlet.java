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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.scripting.core.ScriptHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.internal.LocalizationUtils;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v1.PageListItemImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.SearchImpl;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.FulltextPredicateEvaluator;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.eval.TypePredicateEvaluator;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.adobe.cq.wcm.core.components.models.ExperienceFragment.PN_FRAGMENT_VARIATION_PATH;

/**
 * Search servlet.
 */
@Component(
    service = Servlet.class,
    property = {
        "sling.servlet.selectors=" + SearchResultServlet.DEFAULT_SELECTOR,
        "sling.servlet.resourceTypes=cq/Page",
        "sling.servlet.extensions=json",
        "sling.servlet.methods=GET"
    }
)
public final class SearchResultServlet extends SlingSafeMethodsServlet {

    /**
     * Selector to trigger the search servlet.
     */
    protected static final String DEFAULT_SELECTOR = "searchresults";

    /**
     * Name of the query parameter containing the user query.
     */
    protected static final String PARAM_FULLTEXT = "fulltext";

    /**
     * Name of the query parameter indicating the search result offset.
     */
    protected static final String PARAM_RESULTS_OFFSET = "resultsOffset";

    /**
     * Name of the template structure node.
     */
    private static final String NN_STRUCTURE = "structure";

    /**
     * Query builder service.
     */
    @Reference
    private transient QueryBuilder queryBuilder;

    /**
     * Language manager service.
     */
    @Reference
    private transient LanguageManager languageManager;

    /**
     * Relationship manager service.
     */
    @Reference
    private transient LiveRelationshipManager relationshipManager;

    /**
     * Model factory service.
     */
    @Reference
    private transient ModelFactory modelFactory;

    /**
     * Bundle context.
     */
    private BundleContext bundleContext;

    /**
     * Activate the service.
     *
     * @param bundleContext The bundle context.
     */
    @Activate
    protected void activate(final BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected void doGet(@NotNull final SlingHttpServletRequest request, @NotNull final SlingHttpServletResponse response)
        throws IOException {
        Page currentPage = Optional.ofNullable(request.getResourceResolver().adaptTo(PageManager.class))
            .map(pm -> pm.getContainingPage(request.getResource()))
            .orElse(null);
        if (currentPage != null) {
            SlingBindings bindings = new SlingBindings();
            bindings.setSling(new ScriptHelper(bundleContext, null, request, response));
            request.setAttribute(SlingBindings.class.getName(), bindings);

            Search searchComponent = getSearchComponent(request, currentPage);
            try {
                List<ListItem> results = getResults(request, searchComponent, currentPage.getPageManager());
                response.setContentType("application/json");
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                new ObjectMapper().writeValue(response.getWriter(), results);
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Gets the search component for the given request.
     *
     * @param request The search request.
     * @param currentPage The current page.
     * @return The search component.
     */
    @NotNull
    private Search getSearchComponent(@NotNull final SlingHttpServletRequest request, @NotNull final Page currentPage) {
        String suffix = request.getRequestPathInfo().getSuffix();
        String relativeContentResourcePath = Optional.ofNullable(suffix)
            .filter(path -> StringUtils.startsWith(path, "/"))
            .map(path -> StringUtils.substring(path, 1))
            .orElse(suffix);

        return Optional.ofNullable(relativeContentResourcePath)
            .filter(StringUtils::isNotEmpty)
            .map(rcrp -> getSearchComponentResourceFromPage(request.getResource(), rcrp)
                .orElse(getSearchComponentResourceFromTemplate(currentPage, rcrp)
                    .orElse(null)))
            .map(resource -> modelFactory.getModelFromWrappedRequest(request, resource, Search.class))
            .orElseGet(() -> new DefaultSearch(currentPage, request.getResourceResolver()));
    }

    /**
     * Gets the search component resource from the page. Looks inside experience fragments in the page too.
     *
     * @param pageResource The page resource.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromPage(@NotNull final Resource pageResource, final String relativeContentResourcePath) {
        return Optional.ofNullable(Optional.ofNullable(pageResource.getChild(relativeContentResourcePath))
            .orElse(getSearchComponentResourceFromFragments(pageResource.getChild(NameConstants.NN_CONTENT), relativeContentResourcePath)
                .orElse(null)));
    }

    /**
     * Gets the search component resource from the page's template. Looks inside experience fragments in the template too.
     *
     * @param currentPage The current page, whose template will be used.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromTemplate(@NotNull final Page currentPage, final String relativeContentResourcePath) {
        return Optional.ofNullable(currentPage.getTemplate())
            .map(Template::getPath)
            .map(currentPage.getContentResource().getResourceResolver()::getResource)
            .map(templateResource -> Optional.ofNullable(templateResource.getChild(NN_STRUCTURE + "/" + relativeContentResourcePath))
                .orElse(getSearchComponentResourceFromFragments(templateResource, relativeContentResourcePath)
                    .orElse(null)));
    }

    /**
     * Gets the search component resource from experience fragments under the resource. Walks down the descendants tree.
     *
     * @param resource The resource where experience fragments with search component would be looked up.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromFragments(Resource resource, String relativeContentResourcePath) {
        return Optional.ofNullable(resource)
            .map(res -> getSearchComponentResourceFromFragment(res, relativeContentResourcePath)
                .orElse(StreamSupport.stream(res.getChildren().spliterator(), false)
                    .map(child -> getSearchComponentResourceFromFragments(child, relativeContentResourcePath).orElse(null))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null)));
    }

    /**
     * Gets the search component resource from a candidate experience fragment component resource.
     *
     * @param candidate The candidate experience fragment component resource.
     * @param relativeContentResourcePath The relative path of the search component resource.
     * @return The search component resource.
     */
    private Optional<Resource> getSearchComponentResourceFromFragment(Resource candidate, String relativeContentResourcePath) {
        return Optional.ofNullable(candidate)
            .map(Resource::getValueMap)
            .map(properties -> properties.get(PN_FRAGMENT_VARIATION_PATH, String.class))
            .map(path -> candidate.getResourceResolver().getResource(path + "/" + relativeContentResourcePath));
    }

    /**
     * Gets the search results.
     *
     * @param request The search request.
     * @param searchComponent The search component.
     * @param pageManager A PageManager.
     * @return List of search results.
     */
    @NotNull
    private List<ListItem> getResults(@NotNull final SlingHttpServletRequest request,
                                      @NotNull final Search searchComponent,
                                      @NotNull final PageManager pageManager) {

        List<ListItem> results = new ArrayList<>();
        String fulltext = request.getParameter(PARAM_FULLTEXT);
        if (fulltext == null || fulltext.length() < searchComponent.getSearchTermMinimumLength()) {
            return results;
        }
        long resultsOffset = Optional.ofNullable(request.getParameter(PARAM_RESULTS_OFFSET)).map(Long::parseLong).orElse(0L);
        Map<String, String> predicatesMap = new HashMap<>();
        predicatesMap.put(FulltextPredicateEvaluator.FULLTEXT, fulltext);
        predicatesMap.put(PathPredicateEvaluator.PATH, searchComponent.getSearchRootPagePath());
        predicatesMap.put(TypePredicateEvaluator.TYPE, NameConstants.NT_PAGE);
        PredicateGroup predicates = PredicateConverter.createPredicates(predicatesMap);
        ResourceResolver resourceResolver = request.getResource().getResourceResolver();
        Query query = queryBuilder.createQuery(predicates, resourceResolver.adaptTo(Session.class));
        if (searchComponent.getResultsSize() != 0) {
            query.setHitsPerPage(searchComponent.getResultsSize());
        }
        if (resultsOffset != 0) {
            query.setStart(resultsOffset);
        }
        SearchResult searchResult = query.getResult();

        LinkManager linkManager = request.adaptTo(LinkManager.class);
        // Query builder has a leaking resource resolver, so the following work around is required.
        ResourceResolver leakingResourceResolver = null;
        try {
            Iterator<Resource> resourceIterator = searchResult.getResources();
            while (resourceIterator.hasNext()) {
                Resource resource = resourceIterator.next();

                // Get a reference to QB's leaking resource resolver
                if (leakingResourceResolver == null) {
                    leakingResourceResolver = resource.getResourceResolver();
                }

                Optional.of(resource)
                    .map(res -> resourceResolver.getResource(res.getPath()))
                    .map(pageManager::getContainingPage)
                    .map(page -> new PageListItemImpl(linkManager, page, searchComponent.getId(), null))
                    .ifPresent(results::add);
            }
        } finally {
            if (leakingResourceResolver != null) {
                leakingResourceResolver.close();
            }
        }
        return results;
    }

    /**
     * A fall-back implementation of the Search model.
     */
    private final class DefaultSearch implements Search {

        /**
         * The search root page path.
         */
        @NotNull
        private final String searchRootPagePath;

        /**
         * Construct the default search.
         *
         * @param currentPage The current page.
         * @param resourceResolver The resource resolver.
         */
        public DefaultSearch(@NotNull final Page currentPage, @NotNull final ResourceResolver resourceResolver) {
            this.searchRootPagePath = Optional.ofNullable(currentPage.getContentResource())
                .map(languageManager::getLanguageRoot)
                .map(Page::getPath)
                .map(languageRoot -> LocalizationUtils.getLocalPage(languageRoot, currentPage, resourceResolver, languageManager, relationshipManager)
                    .map(Page::getPath)
                    .orElse(languageRoot)
                )
                .orElseGet(currentPage::getPath);
        }

        @Override
        @Nullable
        public String getId() {
            return null;
        }

        @Override
        public int getResultsSize() {
            return SearchImpl.PROP_RESULTS_SIZE_DEFAULT;
        }

        @Override
        public int getSearchTermMinimumLength() {
            return SearchImpl.PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT;
        }

        @NotNull
        @Override
        public String getSearchRootPagePath() {
            return this.searchRootPagePath;
        }

    }
}
