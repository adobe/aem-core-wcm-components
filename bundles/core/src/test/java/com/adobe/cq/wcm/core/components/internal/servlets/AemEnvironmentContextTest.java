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

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AemEnvironmentContextTest {

    @Test
    void resolvesIdentifiersFromProgramAndEnvironmentIds() {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_PROGRAM_ID", "12345");
        env.put("AEM_ENV_ID", "67890");
        AemEnvironmentContext context = envContext(env);

        assertEquals("12345", context.resolveProgramId());
        assertEquals("67890", context.resolveEnvironmentId());
        assertEquals("p12345-e67890", context.resolveCustomerBucket("12345", "67890"));
    }

    @Test
    void resolvesIdentifiersFromAemServiceFallback() {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_SERVICE", "cm-p99999-e11111");
        AemEnvironmentContext context = envContext(env);

        assertEquals("99999", context.resolveProgramId());
        assertEquals("11111", context.resolveEnvironmentId());
        assertEquals("p99999-e11111", context.resolveCustomerBucket("99999", "11111"));
    }

    @Test
    void resolvesUnknownIdentifiersWhenEnvMissing() {
        AemEnvironmentContext context = envContext(new HashMap<>());

        assertEquals("unknown", context.resolveProgramId());
        assertEquals("unknown", context.resolveEnvironmentId());
        assertEquals("unknown", context.resolveCustomerBucket("unknown", "unknown"));
    }

    @Test
    void trimsProgramAndEnvironmentIds() {
        Map<String, String> env = new HashMap<>();
        env.put("AEM_PROGRAM_ID", " 12345 ");
        env.put("AEM_ENV_ID", " 67890 ");
        AemEnvironmentContext context = envContext(env);

        assertEquals("12345", context.resolveProgramId());
        assertEquals("67890", context.resolveEnvironmentId());
    }

    private static AemEnvironmentContext envContext(Map<String, String> env) {
        return new AemEnvironmentContext() {
            @Override
            protected String getEnv(String name) {
                return env.get(name);
            }
        };
    }
}
