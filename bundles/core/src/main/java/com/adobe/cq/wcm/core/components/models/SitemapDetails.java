/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe
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

import java.util.List;

/**
 * Describes the details that can be returned for a sitemap root.
 */
public interface SitemapDetails {

    /**
     * Returns a list of details about the sitemaps of the current resource if it is a sitemap root.
     *
     * @return
     */
    List<Detail> getDetails();

    /**
     * Returns true when the current resource is a sitemap root.
     *
     * @return
     */
    boolean isSitemapRoot();

    /**
     * Returns true when the at least one of the current resource's sitemaps is currently being generated.
     *
     * @return
     */
    boolean isGenerationPending();

    /**
     * Returns true when at least one of the current resource's sitemaps exceeds the configured limits.
     *
     * @return
     */
    boolean hasWarning();

    interface Detail {

        String getName();

        String getUrl();

        String getSize();

        String getEntries();

    }
}
