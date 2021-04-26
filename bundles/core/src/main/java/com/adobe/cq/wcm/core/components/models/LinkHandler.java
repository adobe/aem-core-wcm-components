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
package com.adobe.cq.wcm.core.components.models;

import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;

/**
 * Model to  resolving and validating links from component model's resources.
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.21.0
 */
public interface LinkHandler {

    /**
     * Resolves a link from the properties of the given resource.
     * @param resource Resource
     *
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    default Optional<Link> getLink(@NotNull Resource resource) {
        return Optional.empty();
    }

    /**
     * Resolves a link from the properties of the given resource.
     * @param resource Resource
     * @param linkURLPropertyName Property name to read link URL from.
     *
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    default Optional<Link> getLink(@NotNull Resource resource, String linkURLPropertyName) {
        return Optional.empty();
    }

    /**
     * Builds a link pointing to the given target page.
     * @param page Target page
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    default Optional<Link<Page>> getLink(@Nullable Page page) {
        return Optional.empty();
    }

    /**
     * Builds a link with the given Link URL and target.
     * @param linkURL Link URL
     * @param target Target
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    default Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target) {
        return Optional.empty();
    }

    /**
     * Builds a link with the given Link URL, target, accessibility label, title.
     * @param linkURL Link URL
     * @param target Target
     * @param linkAccessibilityLabel Link Accessibility Label
     * @param linkTitleAttribute Link Title Attribute
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    default Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target, @Nullable String linkAccessibilityLabel,
                                   @Nullable String linkTitleAttribute) {
        return Optional.empty();
    }
}
