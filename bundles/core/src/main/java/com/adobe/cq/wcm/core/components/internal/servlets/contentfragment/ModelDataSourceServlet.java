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
package com.adobe.cq.wcm.core.components.internal.servlets.contentfragment;


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;

import static com.day.cq.wcm.api.NameConstants.NT_TEMPLATE;

/**
 * Datasource that returns all content fragment models, where the title is the title of the content fragment and the
 * value is the path to the fragment.
 */
@Component(
        service = {Servlet.class},
        property = {
                "sling.servlet.resourceTypes=" + ModelDataSourceServlet.RESOURCE_TYPE_V1,
                "sling.servlet.resourceTypes=" + ModelDataSourceServlet.RESOURCE_TYPE_V2,
                "sling.servlet.methods=GET",
                "sling.servlet.extensions=html"
        }
)
public class ModelDataSourceServlet extends AbstractDataSourceServlet {

    public static final String RESOURCE_TYPE_V1 = "core/wcm/components/contentfragmentlist/v1/datasource/models";
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/contentfragmentlist/v2/datasource/models";

    @Reference
    private transient ExpressionResolver expressionResolver;

    @NotNull
    @Override
    protected ExpressionResolver getExpressionResolver() {
        return expressionResolver;
    }

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {

        DataSource dataSource = EmptyDataSource.instance();
        ResourceResolver resourceResolver = request.getResourceResolver();

        QueryBuilder queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        if (queryBuilder != null) {
            Map<String, String> parameterMap = new HashMap<>();

            parameterMap.put("path", "/conf");
            parameterMap.put("type", NT_TEMPLATE);
            parameterMap.put("p.limit", "-1");
            parameterMap.put("1_property", JcrConstants.JCR_CONTENT + "/model/" + ResourceResolver.PROPERTY_RESOURCE_TYPE);
            parameterMap.put("1_property.value", "wcm/scaffolding/components/scaffolding");

            PredicateGroup predicateGroup = PredicateGroup.create(parameterMap);
            Session session = resourceResolver.adaptTo(Session.class);
            Query query = queryBuilder.createQuery(predicateGroup, session);

            SearchResult searchResult = query.getResult();

            // Query builder has a leaking resource resolver, so the following work around is required.
            ResourceResolver leakingResourceResolver = null;

            try {
                // Iterate over the hits if you need special information
                List<Resource> resources = new LinkedList<>();
                for (Iterator<Resource> resourceIterator = searchResult.getResources(); resourceIterator.hasNext(); ) {
                    Resource resource = resourceIterator.next();
                    if (leakingResourceResolver == null) {
                        // Get a reference to query builder's leaking resource resolver
                        leakingResourceResolver = resource.getResourceResolver();
                    }
                    ValueMap modelValueMap = resource.getValueMap();
                    String modelTitle = modelValueMap.get(JcrConstants.JCR_CONTENT + "/" + JcrConstants.JCR_TITLE,
                            resource.getName());
                    String modelPath = resource.getPath();
                    Resource syntheticResource = createResource(resourceResolver, modelTitle, modelPath);
                    resources.add(syntheticResource);
                }
                dataSource = new SimpleDataSource(resources.iterator());
            } finally {
                if (leakingResourceResolver != null) {
                    // Always close the leaking query builder resource resolver.
                    leakingResourceResolver.close();
                }
            }
        }

        request.setAttribute(DataSource.class.getName(), dataSource);
    }
}
