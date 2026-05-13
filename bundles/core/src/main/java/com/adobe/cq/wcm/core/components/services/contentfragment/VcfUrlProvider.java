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
 * Optional OSGi service: VCF preview URL patterns and templates API base. Implemented by DAM Content Fragment;
 * consumed by Core when the Content Fragment component uses {@code displayMode} {@code vcf}.
 *
 * @since com.adobe.cq.wcm.core.components.services.contentfragment 1.0.0
 */
@ConsumerType
public interface VcfUrlProvider {

    /** @return API root (optional for Core), or {@code null} */
    @Nullable
    String getVcfApiBase();

    /** @return author preview format, one {@code %s} for fragment id, or {@code null} */
    @Nullable
    String getVcfAuthorUrlFormat();

    /** @return publish HTML format, three {@code %s}: template id, fragment id, variation, or {@code null} */
    @Nullable
    String getVcfPublishUrlFormat();

    /** @return templates API base (dialog appends {@code /{modelId}/templates?...}), or {@code null} */
    @Nullable
    String getVcfTemplatesApiBase();
}
