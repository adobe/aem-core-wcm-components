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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.internal.servlets.AemEnvironmentContext;
import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;

/**
 * Emits structured usage logs for the ContentAI Supported Search component so platform
 * operators can aggregate request counts per customer (AEM program/environment) and operation.
 */
final class ContentAISearchUsageLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAISearchUsageLogger.class);

    private ContentAISearchUsageLogger() {
        // utility
    }

    /**
     * Logs one usage event. Query text is intentionally omitted to avoid persisting visitor input.
     *
     * @param operation the servlet operation ({@code search} or {@code gensearch})
     * @param request the current request
     * @param model the resolved component model
     */
    static void logUsage(@NotNull String operation, @NotNull SlingHttpServletRequest request,
                         @NotNull ContentAISupportedSearch model) {
        new UsageContext().logUsage(operation, request, model);
    }

    static class UsageContext extends AemEnvironmentContext {

        void logUsage(@NotNull String operation, @NotNull SlingHttpServletRequest request,
                      @NotNull ContentAISupportedSearch model) {
            Resource resource = request.getResource();
            String resourcePath = resource.getPath();
            String programId = resolveProgramId();
            String environmentId = resolveEnvironmentId();
            String customerBucket = resolveCustomerBucket(programId, environmentId);

            LOGGER.info(
                "ContentAI Supported Search used: operation={}, programId={}, environmentId={}, customerBucket={}, "
                    + "resourcePath={}, contentSource={}, contentSourceType={}, contentSources={}",
                operation,
                programId,
                environmentId,
                customerBucket,
                resourcePath,
                model.getContentSource(),
                model.getContentSourceType(),
                model.getContentSources());
        }
    }
}
