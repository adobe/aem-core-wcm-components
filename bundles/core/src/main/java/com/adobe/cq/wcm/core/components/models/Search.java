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
package com.adobe.cq.wcm.core.components.models;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.form.OptionItem;

/**
 * Defines the {@code Search} Sling Model used for the {@code /apps/core/wcm/components/search} component.
 *
 * @since com.adobe.cq.wcm.core.components.models 12.2.0
 */
public interface Search extends ComponentExporter {

    /**
     * Name of the resource / configuration policy property that defines the site's search root from which to search.
     * The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_SEARCH_ROOT = "searchRoot";
    
    /**
     * Name of the resource / configuration policy property that defines whether to enable sort or not.
     * The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_ENABLE_SORT = "enableSort";
    
    /**
     * Name of the resource / configuration policy property that defines whether to enable facet or not.
     * The property should provide a String value.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_ENABLE_FACET = "enableFacet";

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
     * Name of the configuration policy property that defines the maximal number of results fetched by a search request.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_DEFAULT_SORT = "defaultSort";
    
    /**
     * Name of the configuration policy property that defines the maximal number of results fetched by a search request.
     *
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    String PN_DEFAULT_SORT_DIRECTION = "defaultSortDirection";

    /**
     * The maximal number of results fetched by a search request.
     *
     * @return number of results
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getResultsSize() {
        throw new UnsupportedOperationException();
    }

    /**
     * The minimum length of the search term to start the search.
     *
     * @return minimum length of the search term
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    default int getSearchTermMinimumLength() {
        throw new UnsupportedOperationException();
    }

    /**
     * Relative path of the search component in the current page.
     *
     * @return the relative path of search inside the current page
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    default String getRelativePath() {
        throw new UnsupportedOperationException();
    }

    /**
     * @see ComponentExporter#getExportedType()
     * @since com.adobe.cq.wcm.core.components.models 12.2.0
     */
    @NotNull
    @Override
    default String getExportedType() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the facet is enabled
     *
     * @return {@code true} if facet is enabled, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
     
    default boolean isFacetEnabled() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the sorting is enabled
     *
     * @return {@code true} if sort is enabled, {@code false} otherwise
     * @since com.adobe.cq.wcm.core.components.models 12.8.0
     */
     
    default boolean isSortEnabled() {
        throw new UnsupportedOperationException();
    }
    
    /**
    * Returns the list's items collection, as {@link ListItem}s elements.
    *
    * @return {@link Collection} of {@link ListItem}s
    * @since com.adobe.cq.wcm.core.components.models 12.8.0
    */
    
   default Collection<ListItem> getTags() {
       throw new UnsupportedOperationException();
   }
   /**
    * @see ComponentExporter#getFacetTitle()
    * @since com.adobe.cq.wcm.core.components.models 12.8.0
    */
   
   default String getFacetTitle() {
       throw new UnsupportedOperationException();
   }
   
   /**
    * @see ComponentExporter#getProperty()
    * @since com.adobe.cq.wcm.core.components.models 12.8.0
    */
   default String getTagProperty() {
       throw new UnsupportedOperationException();
   }
   
   /**
    * 
    * @return
    */
   default Collection<OptionItem> getSortOptions() {
       throw new UnsupportedOperationException();
   }
   
   /**
    * @see ComponentExporter#getAscLabel()
    * @since com.adobe.cq.wcm.core.components.models 12.8.0
    */
   default String getAscLabel() {
       throw new UnsupportedOperationException();
   }
   
   /**
    * @see ComponentExporter#getDescLabel()
    * @since com.adobe.cq.wcm.core.components.models 12.8.0
    */
   
   default String getDescLabel() {
       throw new UnsupportedOperationException();
   }
}