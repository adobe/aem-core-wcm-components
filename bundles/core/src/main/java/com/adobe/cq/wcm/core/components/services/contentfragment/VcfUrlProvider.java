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
package com.adobe.cq.wcm.core.components.services.contentfragment;

import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Provides URL patterns and API bases for Visual Content Fragment (VCF) preview and template listing.
 * <p>
 * Register an implementation of this interface as an OSGi service (typically from the DAM Content Fragment
 * implementation bundle). The Core Content Fragment component consumes it optionally when {@code displayMode} is VCF.
 * </p>
 *
 * @since com.adobe.cq.wcm.core.components.services.contentfragment 1.0.0
 */
@ConsumerType
public interface VcfUrlProvider {

    /**
     * @return base path for VCF-related APIs, or {@code null} if not configured
     */
    @Nullable
    String getVcfApiBase();

    /**
     * @return author preview URL format string with a single {@code %s} placeholder for the fragment id, or {@code null}
     */
    @Nullable
    String getVcfAuthorUrlFormat();

    /**
     * @return publish URL format with placeholders {@code %s} for template id, fragment id, and variation key, or {@code null}
     */
    @Nullable
    String getVcfPublishUrlFormat();

    /**
     * @return base URL for the VCF templates API, or {@code null} if not configured
     */
    @Nullable
    String getVcfTemplatesApiBase();
}
