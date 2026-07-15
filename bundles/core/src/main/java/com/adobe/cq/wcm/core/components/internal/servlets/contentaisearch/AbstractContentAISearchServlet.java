/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2026 Adobe
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
package com.adobe.cq.wcm.core.components.internal.servlets.contentaisearch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClient;
import com.adobe.cq.wcm.core.components.services.contentai.ContentAIClientException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base servlet for the ContentAI Supported Search component's two query endpoints.
 * Handles the shared request lifecycle — {@code q} parameter validation, model adaptation,
 * Content AI error handling, and JSON marshaling — delegating the actual Content AI call to
 * {@link #executeQuery(SlingHttpServletRequest, ContentAISupportedSearch, String)}.
 */
abstract class AbstractContentAISearchServlet extends SlingSafeMethodsServlet {

    protected static final String RESOURCE_TYPE = "core/wcm/components/contentaisearch/v1/contentaisearch";
    protected static final String EXTENSION = "json";
    private static final String PARAM_QUERY = "q";
    private static final int MAX_QUERY_LENGTH = 512;
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractContentAISearchServlet.class);

    @Reference
    protected transient ContentAIClient contentAIClient;

    @Reference
    protected transient ModelFactory modelFactory;

    @Override
    protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response) throws IOException {
        String queryText = request.getParameter(PARAM_QUERY);
        if (StringUtils.isBlank(queryText)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required parameter: " + PARAM_QUERY);
            return;
        }
        if (queryText.length() > MAX_QUERY_LENGTH) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parameter " + PARAM_QUERY + " exceeds maximum length of " + MAX_QUERY_LENGTH);
            return;
        }

        ContentAISupportedSearch model = getModel(request);
        if (model == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ContentAISearchUsageLogger.logUsage(getOperationName(), request, model);

        try {
            Object result = executeQuery(request, model, queryText);
            writeJson(result, response);
        } catch (ContentAIClientException e) {
            LOGGER.error("Content AI request failed for content source {}", model.getContentSource(), e);
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Content AI request failed");
        }
    }

    /**
     * Executes the specific Content AI call for this servlet.
     *
     * @param request the Sling request; required for subclasses that read request parameters (e.g. cursor pagination)
     * @param model   the resolved component model providing the content source and result size
     * @param query   the validated user query
     * @return the result object to serialize as JSON
     * @throws ContentAIClientException if the Content AI call fails
     */
    protected Object executeQuery(@NotNull SlingHttpServletRequest request, @NotNull ContentAISupportedSearch model,
        @NotNull String query) throws ContentAIClientException {
        Objects.requireNonNull(request);
        return executeQuery(model, query);
    }

    /**
     * Executes the Content AI call when no request-scoped input is required.
     *
     * @param model the resolved component model
     * @param query the validated user query
     * @return the result object to serialize as JSON
     * @throws ContentAIClientException if the Content AI call fails
     */
    protected Object executeQuery(@NotNull ContentAISupportedSearch model, @NotNull String query)
        throws ContentAIClientException {
        throw new UnsupportedOperationException("executeQuery(request, model, query) must be overridden");
    }

    /**
     * @return a short operation label included in usage logs ({@code search} or {@code gensearch})
     */
    @NotNull
    protected abstract String getOperationName();

    /**
     * Adapts the request to {@link ContentAISupportedSearch} via {@link ModelFactory}, so proxy components
     * ({@code sling:resourceSuperType} pointing at the core resource type) resolve correctly.
     */
    private ContentAISupportedSearch getModel(@NotNull SlingHttpServletRequest request) {
        Resource resource = request.getResource();
        ContentAISupportedSearch model = null;
        if (resource != null && modelFactory != null) {
            model = modelFactory.getModelFromWrappedRequest(request, resource, ContentAISupportedSearch.class);
        }
        if (model == null) {
            model = request.adaptTo(ContentAISupportedSearch.class);
        }
        return model;
    }

    private void writeJson(Object result, SlingHttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        new ObjectMapper().writeValue(response.getWriter(), result);
    }
}
