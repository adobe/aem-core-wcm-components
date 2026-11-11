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
import org.osgi.annotation.versioning.ProviderType;

import com.day.cq.dam.api.Asset;
import com.day.cq.wcm.api.Page;

/**
 * Interface to get resolved {@link Link} instances directly.
 *
 * <p>This Sling Model can be injected into custom models using the {@code @Self} annotation
 * to quickly obtain built links without manually invoking a builder.</p>
 */
@ProviderType
public interface LinkHandler {

    /**
     * Returns a resolved link pointing to a page.
     *
     * @param page Target page of the link.
     * @return {@link Link} pointing to the specified page.
     */
    @NotNull
    Link<Page> getLink(@NotNull Page page);

    /**
     * Returns a resolved link defined by the resource properties.
     *
     * @param resource Resource to read the link properties from.
     * @return {@link Link} built from the specified resource.
     */
    @NotNull
    Link<Page> getLink(@NotNull Resource resource);

    /**
     * Returns a resolved link pointing to an asset.
     *
     * @param asset Target asset of the link.
     * @return {@link Link} pointing to the specified asset.
     */
    @NotNull
    Link<Asset> getLink(@NotNull Asset asset);

    /**
     * Returns a resolved link pointing to an URL or path.
     *
     * @param url URL string of the link.
     * @return {@link Link} pointing to the specified URL.
     */
    @NotNull
    Link<String> getLink(@NotNull String url);
}
