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

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ConsumerType;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

/**
 * This interface offers a flexible way, based on the builder pattern, to compute links.
 *
 * This is a Sling model that can be injected into other models using the <code>@Self</code> annotation.
 * It can be adapted from a {@link SlingHttpServletRequest}.
 *
 * It can be used as follows:
 * <pre>
 *     Link link = linkManager.get(page).build();
 *     Link link = linkManager.get(page)
 *          .withLinkTarget(...)
 *          .withLinkAttribute(...)
 *          .build();
 * </pre>
 */
@ConsumerType
public interface LinkManager {

    /**
     * Returns a link builder where the link is defined by the resource properties.
     *
     * @param resource Resource to read the link properties from.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull Resource resource);

    /**
     * Returns a link builder where the link points to a page.
     *
     * @param page Target page of the link.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull Page page);

    /**
     * Returns a link builder where the link points to an asset.
     *
     * @param asset Target asset of the link.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull Asset asset);

    /**
     * Returns a link builder where the link points to an URL.
     *
     * @param url URL string of the link.
     * @return {@link LinkBuilder}
     */
    @NotNull
    LinkBuilder get(@NotNull String url);

}
