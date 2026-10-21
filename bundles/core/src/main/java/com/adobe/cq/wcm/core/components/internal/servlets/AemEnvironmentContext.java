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

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Resolves the AEM Cloud program/environment identifiers used to bucket usage logs per
 * customer, shared by every component-specific usage logger (e.g. {@code SearchUsageLogger},
 * {@code ContentAISearchUsageLogger}) so the resolution logic exists exactly once.
 */
public class AemEnvironmentContext {

    private static final String ENV_PROGRAM_ID = "AEM_PROGRAM_ID";
    private static final String ENV_ENV_ID = "AEM_ENV_ID";
    private static final String ENV_SERVICE = "AEM_SERVICE";
    private static final String UNKNOWN = "unknown";

    @NotNull
    public String resolveProgramId() {
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
    public String resolveEnvironmentId() {
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
    public String resolveCustomerBucket(@NotNull String programId, @NotNull String environmentId) {
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
