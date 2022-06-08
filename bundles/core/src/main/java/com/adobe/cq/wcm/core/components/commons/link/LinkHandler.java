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
package com.adobe.cq.wcm.core.components.commons.link;

import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.day.cq.wcm.api.Page;

/**
 * Computes a link based on input configuration.
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 */
public interface LinkHandler {

    /**
     * Returns a link builder based on the given resource.
     *
     * @param resource Resource to read the link properties from.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull Resource resource);

    /**
     * Returns a link builder pointing to the given target page.
     *
     * @param page Target page of the link.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull Page page);

    /**
     * Returns a link builder pointing to the URL.
     *
     * @param URL URL string of the link.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull String URL);

}
