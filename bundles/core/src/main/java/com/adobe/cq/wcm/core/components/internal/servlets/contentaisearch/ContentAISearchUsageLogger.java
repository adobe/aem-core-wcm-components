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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.ContentAISupportedSearch;

/**
 * Emits structured usage logs for the ContentAI Supported Search component so platform
 * operators can aggregate request counts per customer (AEM program/environment) and operation.
 */
final class ContentAISearchUsageLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentAISearchUsageLogger.class);

    private static final String ENV_PROGRAM_ID = "AEM_PROGRAM_ID";
    private static final String ENV_ENV_ID = "AEM_ENV_ID";
    private static final String ENV_SERVICE = "AEM_SERVICE";
    private static final String UNKNOWN = "unknown";

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

    static class UsageContext {

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

        @NotNull
        String resolveProgramId() {
            String programId = getEnv(ENV_PROGRAM_ID);
            if (StringUtils.isNotBlank(programId)) {
                return programId.trim();
            }
            String service = getEnv(ENV_SERVICE);
            if (StringUtils.isNotBlank(service) && service.startsWith("cm-p")) {
                int envSeparator = service.indexOf("-e", 3);
                if (envSeparator > 4) {
                    return service.substring(4, envSeparator);
                }
            }
            return UNKNOWN;
        }

        @NotNull
        String resolveEnvironmentId() {
            String envId = getEnv(ENV_ENV_ID);
            if (StringUtils.isNotBlank(envId)) {
                return envId.trim();
            }
            String service = getEnv(ENV_SERVICE);
            if (StringUtils.isNotBlank(service) && service.startsWith("cm-p") && service.contains("-e")) {
                return service.substring(service.indexOf("-e") + 2);
            }
            return UNKNOWN;
        }

        @NotNull
        String resolveCustomerBucket(@NotNull String programId, @NotNull String environmentId) {
            if (!UNKNOWN.equals(programId) && !UNKNOWN.equals(environmentId)) {
                return "p" + programId + "-e" + environmentId;
            }
            return UNKNOWN;
        }

        /**
         * Package-visible seam for tests.
         *
         * @param name environment variable name
         * @return value or {@code null}
         */
        protected String getEnv(String name) {
            return System.getenv(name);
        }
    }
}
