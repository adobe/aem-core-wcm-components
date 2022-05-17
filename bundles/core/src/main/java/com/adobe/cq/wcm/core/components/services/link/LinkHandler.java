/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
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

import java.util.Map;
import java.util.Optional;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.day.cq.wcm.api.Page;

public interface LinkHandler {

    /**
     * Builds a link based on the given resource.
     *
     * @param resource Resource
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
    @SuppressWarnings("rawtypes")
    Optional<Link> getLink(@Nullable Page page);

    /**
     * Builds a link pointing to the given Link URL and based on link attributes (e.g. target, accessibility label, title).
     * @param linkURL Link URL
     * @param attributes Link attributes
     *
     * @return {@link Optional} of  {@link Link<Page>}
     */
    @NotNull
    @SuppressWarnings("rawtypes")
    Optional<Link> getLink(@Nullable String linkURL, Map<String, String> attributes);

}
