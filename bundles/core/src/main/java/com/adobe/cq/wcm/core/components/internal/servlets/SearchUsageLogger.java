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
package com.adobe.cq.wcm.core.components.internal.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.Search;

/**
 * Emits structured usage and error logs for the Quick Search component (v1/v2/v3, which
 * all share {@link SearchResultServlet}) so platform operators can aggregate request counts
 * and failures per customer (AEM program/environment), matching the existing logging
 * behavior of {@code ContentAISearchUsageLogger} for Content AI Supported Search.
 */
final class SearchUsageLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchUsageLogger.class);

    private SearchUsageLogger() {
        // utility
    }

    /**
     * Logs one usage event. The search query string is intentionally omitted to avoid
     * persisting visitor input, matching {@code ContentAISearchUsageLogger}'s constraint.
     *
     * @param request the current request
     * @param model the resolved search component model
     * @param resultCount the number of results returned for this request
     */
    static void logUsage(@NotNull SlingHttpServletRequest request, @NotNull Search model, int resultCount) {
        new UsageContext().logUsage(request, model, resultCount);
    }

    /**
     * Logs one failure event.
     *
     * @param request the current request
     * @param reason a short machine-readable failure reason, e.g. {@code invalid_results_offset}
     * @param cause the exception that caused the failure
     */
    static void logError(@NotNull SlingHttpServletRequest request, @NotNull String reason, @NotNull Exception cause) {
        String resourcePath = request.getResource().getPath();
        LOGGER.error("Quick Search request failed: resourcePath={}, reason={}", resourcePath, reason, cause);
    }

    static class UsageContext extends AemEnvironmentContext {

        void logUsage(@NotNull SlingHttpServletRequest request, @NotNull Search model, int resultCount) {
            Resource resource = request.getResource();
            String resourcePath = resource.getPath();
            String programId = resolveProgramId();
            String environmentId = resolveEnvironmentId();
            String customerBucket = resolveCustomerBucket(programId, environmentId);

            LOGGER.info(
                "Quick Search used: programId={}, environmentId={}, customerBucket={}, "
                    + "resourcePath={}, configuredResultsSize={}, resultCount={}",
                programId,
                environmentId,
                customerBucket,
                resourcePath,
                model.getResultsSize(),
                resultCount);
        }
    }
}
