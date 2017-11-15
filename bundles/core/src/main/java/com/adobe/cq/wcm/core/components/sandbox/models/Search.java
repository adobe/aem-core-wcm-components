/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.sandbox.models;

import javax.annotation.Nonnull;
import java.util.Collection;

import org.apache.sling.api.resource.Resource;

import com.adobe.cq.export.json.ComponentExporter;

/**
 * Defines the {@code Search} Sling Model used for the {@code /apps/core/wcm/components/search} component.
 *
 * @since com.adobe.cq.wcm.core.components.sandbox.models 2.2.0
 */
public interface Search extends ComponentExporter {

    /**
     * Name of the resource / configuration policy property that defines the search level from which to search
     * for results. The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.2.0
     */
    String PN_START_LEVEL = "startLevel";

    /**
     * Name of the configuration policy property that defines the minimum length of the search term to start the search.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.4.0
     */
    String PN_SEARCH_TERM_MINIMUM_LENGTH= "searchTermMinimumLength";

    /**
     * Name of the configuration policy property that defines the maximal number of results fetched by a search request.
     *
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.4.0
     */
    String PN_RESULTS_SIZE = "resultsSize";

    /**
     * A collection of {@link Resource} items as search result.
     * If the search term was not found, the collection will be empty.
     *
     * @return collection of search result
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.2.0
     */
    default Collection<Resource> getResults() {
        throw new UnsupportedOperationException();
    }

    /**
     * The maximal number of results fetched by a search request.
     *
     * @return number of results
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.4.0
     */
    default int getResultsSize() {
        throw new UnsupportedOperationException();
    }

    /**
     * The minimum length of the search term to start the search.
     *
     * @return minimum length of the search term
     * @since com.adobe.cq.wcm.core.components.sandbox.models 2.4.0
     */
    default int getSearchTermMinimumLength() {
        throw new UnsupportedOperationException();
    }

    /**
     * @since com.adobe.cq.wcm.core.components.sandbox.models 3.1.0
     */
    @Nonnull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
}
