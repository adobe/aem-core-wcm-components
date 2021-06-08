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
package com.adobe.cq.wcm.core.components.services.sitemap;

import java.util.Locale;
import java.util.Map;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.sitemap.generator.SitemapGenerator;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

import com.day.cq.wcm.api.Page;

/**
 * A service that exposes the filters and utility methods the default {@link SitemapGenerator} of the WCM Core Components uses.
 * <p>
 * It acts as extension point for a delegation pattern implementation, where another {@link SitemapGenerator} replaces the default one but
 * needs to use some of its functionality anyway.
 */
@ProviderType
public interface PageTreeSitemapGenerator extends SitemapGenerator {

    /**
     * Returns a mapping from {@link Locale} to {@link Page} for the language alternatives of the given {@link Page}.
     * <p>
     * Each path is checked with {@link SitemapGenerator#shouldInclude(Resource)} before being returned.
     *
     * @return
     */
    Map<Locale, String> getLanguageAlternatives(Page page);

    /**
     * Returns true when the {@link Page} is published.
     * <p>
     * When called on publishers, this is always true.
     *
     * @param page
     * @return
     */
    boolean isPublished(@NotNull Page page);

    /**
     * Returns true when the {@link Page} is set to be not indexed by search engines.
     *
     * @param page
     * @return
     */
    boolean isNoIndex(@NotNull Page page);

    /**
     * Returns true when the {@link Page} has a redirect target.
     *
     * @param page
     * @return
     */
    boolean isRedirect(@NotNull Page page);

    /**
     * Returns true when the {@link Page} requires authentication.
     *
     * @param page
     * @return
     */
    boolean isProtected(@NotNull Page page);

    /**
     * Returns true when the {@link Resource} requires authentication.
     *
     * @param resource
     * @return
     */
    boolean isProtected(@NotNull Resource resource);

}
