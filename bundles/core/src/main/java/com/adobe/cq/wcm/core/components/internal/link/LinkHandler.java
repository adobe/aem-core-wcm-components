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
package com.adobe.cq.wcm.core.components.internal.link;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ConsumerType;

import java.util.Optional;

/**
 * interface for resolving and validating links from model's resources.
 * This is implemented as Sling model that can be injected into other models using the <code>@Self</code> annotation.
 */
@ConsumerType
public interface LinkHandler {

    String HTML_EXTENSION = ".html";


    /**
     * Name of the resource property that for redirecting pages will indicate if original page or redirect target page should be returned.
     * Default is `false`. If `true` - original page is returned. If `false` or not configured - redirect target page.
     */
    String PN_DISABLE_SHADOWING = "disableShadowing";

    /**
     * Flag indicating if shadowing is disabled.
     */
    boolean PROP_DISABLE_SHADOWING_DEFAULT = false;

    /**
     * Resolves a link from the properties of the given resource.
     *
     * @param resource Resource
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    Optional<Link> getLink(@NotNull Resource resource);

    /**
     * Resolves a link from the properties of the given resource.
     *
     * @param resource            Resource
     * @param linkURLPropertyName Property name to read link URL from.
     * @return {@link Optional} of  {@link Link}
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    Optional<Link> getLink(@NotNull Resource resource, String linkURLPropertyName);

    /**
     * Builds a link pointing to the given target page.
     * @param page Target page
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    Optional<Link<Page>> getLink(@Nullable Page page);

    /**
     * Builds a link with the given Link URL and target.
     * @param linkURL Link URL
     * @param target Target
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target);

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
    Optional<Link<Page>> getLink(@Nullable String linkURL, @Nullable String target, @Nullable String linkAccessibilityLabel, @Nullable String linkTitleAttribute);

    /**
     * Attempts to resolve the redirect chain starting from the given page, avoiding loops.
     *
     * @param page The starting {@link Page}
     * @return A pair of {@link Page} and {@link String} the redirect chain resolves to. The page can be the original page, if no redirect
     * target is defined or even {@code null} if the redirect chain does not resolve to a valid page, in this case one should use the right
     * part of the pair (the {@link String} redirect target).
     */
    @NotNull
    Pair<Page, String> resolveRedirects(@Nullable final Page page);
}
