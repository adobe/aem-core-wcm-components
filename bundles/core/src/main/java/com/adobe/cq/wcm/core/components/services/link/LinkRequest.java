/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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
package com.adobe.cq.wcm.core.components.services.link;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

/**
 * Simple PoJo which contains required information to create a {@link com.adobe.cq.wcm.core.components.commons.link.Link} by the
 * {@link LinkProcessor}
 *
 * @since com.adobe.cq.wcm.core.components.services.link 1.0.0
 */
@ProviderType
public final class LinkRequest<T> {

    final private T reference;
    final private String path;
    final private Map<String, Optional<String>> htmlAttributes;

    public LinkRequest(@Nullable T reference, @Nullable String path, @Nullable Map<String, Optional<String>> htmlAttributes) {
        this.reference = reference;
        this.path = path;
        this.htmlAttributes = Optional.ofNullable(htmlAttributes).orElse(Collections.emptyMap());
    }

    @Nullable
    public T getReference() {
        return reference;
    }

    @Nullable
    public String getPath() {
        return path;
    }

    @NotNull
    public Map<String, Optional<String>> getHtmlAttributes() {
        return htmlAttributes;
    }
}
