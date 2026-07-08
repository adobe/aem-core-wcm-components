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
package com.adobe.cq.wcm.core.components.internal.services.contentai;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "Core Components Content AI Client",
    description = "Configuration for connecting to the AEM Content AI APIs."
)
public @interface ContentAIConfig {

    int DEFAULT_CONNECTION_TIMEOUT = 2000;
    int DEFAULT_SOCKET_TIMEOUT = 10000;

    @AttributeDefinition(
        name = "API Key (X-Api-Key)",
        description = "Adobe Developer Console client ID used as the X-Api-Key for anonymous, public-index Content AI " +
            "access. This is not a user credential; it identifies the calling source. Provide via a Cloud Manager " +
            "environment variable / secret.",
        type = AttributeType.PASSWORD
    )
    String apiKey();

    @AttributeDefinition(
        name = "Base URL Override (dev only)",
        description = "Optional full Content AI base URL, used ONLY for local/non-Cloud-Service development where the " +
            "AEM_DOMAIN_PUBLISH / AEM_PROGRAM_ID+AEM_ENV_ID environment variables are absent. Leave empty on AEM as a " +
            "Cloud Service: the base URL is derived from the running environment. Example: " +
            "https://publish-p12345-e123456.adobeaemcloud.com/adobe/experimental/aemcontentai-expires-20261231/contentAI"
    )
    String baseUrlOverride() default "";

    @AttributeDefinition(
        name = "Default Content Source",
        description = "Default (public) content source name to search against when a component instance does not " +
            "specify one. Must be a public Content AI index of published content."
    )
    String defaultContentSource() default "";

    @AttributeDefinition(
        name = "Connection Timeout",
        description = "Time (ms) to establish the connection with Content AI."
    )
    int connectionTimeout() default DEFAULT_CONNECTION_TIMEOUT;

    @AttributeDefinition(
        name = "Socket Timeout",
        description = "Time (ms) waiting for data after establishing the connection."
    )
    int socketTimeout() default DEFAULT_SOCKET_TIMEOUT;
}
