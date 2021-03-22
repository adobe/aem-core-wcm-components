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

import org.jetbrains.annotations.NotNull;

/**
 * Defines the {@code Search} Sling Model used for the {@code /apps/core/wcm/components/search} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
public interface Search extends Component {

    /**
     * Name of the resource / configuration policy property that defines the site's search root from which to search.
     * The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_SEARCH_ROOT = "searchRoot";

    /**
     * Name of the configuration policy property that defines the minimum length of the search term to start the search.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_SEARCH_TERM_MINIMUM_LENGTH= "searchTermMinimumLength";

    /**
     * Name of the configuration policy property that defines the maximal number of results fetched by a search request.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_RESULTS_SIZE = "resultsSize";

    /**
     * The maximal number of results fetched by a search request.
     *
     * @return number of results
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getResultsSize() {
        return 0;
    }

    /**
     * The minimum length of the search term to start the search.
     *
     * @return minimum length of the search term
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getSearchTermMinimumLength() {
        return 0;
    }

    /**
     * Relative path of the search component in the current page.
     *
     * @return the relative path of search inside the current page
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    default String getRelativePath() {
        return "";
    }

    /**
     * Gets the path of the localized search root.
     *
     * @return The search root path.
     * @since com.adobe.cq.wcm.core.components.models 12.17.0
     */
    @NotNull
    default String getSearchRootPagePath() {
        return "";
    }

}
