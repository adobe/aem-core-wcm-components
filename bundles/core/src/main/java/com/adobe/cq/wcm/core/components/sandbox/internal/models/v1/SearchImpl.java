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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.SyntheticResource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.sandbox.models.Search;
import com.day.cq.search.PredicateConverter;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = Search.class,
       resourceType = {SearchImpl.RESOURCE_TYPE})
@Exporter(name = Constants.EXPORTER_NAME,
          extensions = Constants.EXPORTER_EXTENSION)
public class SearchImpl implements Search {

    protected static final String RESOURCE_TYPE = "core/wcm/sandbox/components/search/v1/search";

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchImpl.class);

    protected static final int PROP_START_LEVEL_DEFAULT = 2;

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @OSGiService
    private QueryBuilder queryBuilder;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    private PageManager pageManager;
    private String path;

    @PostConstruct
    private void initModel() {
        pageManager = resourceResolver.adaptTo(PageManager.class);
        int startLevel = properties.get(PN_START_LEVEL, currentStyle.get(PN_START_LEVEL, PROP_START_LEVEL_DEFAULT));
        path = calculatePath(startLevel);
    }

    private String calculatePath(int startLevel) {
        Page rootPage = currentPage.getAbsoluteParent(startLevel);
        if(rootPage != null) {
            return rootPage.getPath();
        } else {
            return currentPage.getPath();
        }
    }

    @Override
    public String getRootPath() {
        return path;
    }

    @Override
    public List<Resource> getResults() {
        SearchResult searchResult = getSearchResult(request.getParameterMap(), resourceResolver);
        return searchResult.getHits().stream().map(this::populateItem).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private Resource populateItem(final Hit hit) {
        try {
            final String title = getTitle(hit);
            final String path = getPath(hit);

            if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(path)) {
                return new SyntheticResource(null, (String) null, null) {
                    public <T> T adaptTo(Class<T> type) {
                        if (type == ValueMap.class) {
                            ValueMap m = new ValueMapDecorator(new HashMap<>());
                            m.put("title", title);
                            m.put("path", path);
                            return (T) m;
                        }
                        return super.adaptTo(type);
                    }
                };
            }
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    private String getTitle(Hit hit) throws RepositoryException {
        ValueMap vm = hit.getResource().getValueMap();
        return vm.get("jcr:content/jcr:title", vm.get("jcr:title", hit.getResource().getName()));
    }

    private String getPath(Hit hit) throws RepositoryException {
        Page page = pageManager.getContainingPage(hit.getResource());
        if (page != null) {
            return page.getPath();
        }
        return null;
    }

    private SearchResult getSearchResult(Map predicateParameters, ResourceResolver resolver) {
        PredicateGroup predicates = PredicateConverter.createPredicates(predicateParameters);
        Query query = queryBuilder.createQuery(predicates, resolver.adaptTo(Session.class));
        return query.getResult();
    }


}
