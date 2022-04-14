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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import javax.annotation.PostConstruct;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.LocalizationUtils;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.designer.Style;

import java.util.Optional;

/**
 * Search model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {Search.class, ComponentExporter.class},
    resourceType = SearchImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SearchImpl extends AbstractComponentImpl implements Search {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE = "core/wcm/components/search/v1/search";

    /**
     * Default number of results to show.
     */
    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;

    /**
     * Default minimum search term length.
     */
    public static final int PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT = 3;

    /**
     * The current request.
     */
    @Self
    private SlingHttpServletRequest request;

    /**
     * The current page.
     */
    @ScriptVariable
    private Page currentPage;

    /**
     * The current style.
     */
    @ScriptVariable
    private Style currentStyle;

    /**
     * The language manager service.
     */
    @OSGiService
    private LanguageManager languageManager;

    /**
     * The live relationship manager service.
     */
    @OSGiService
    private LiveRelationshipManager relationshipManager;

    /**
     * The relative path between this component and the containing page.
     */
    private String relativePath;

    /**
     * The number of results to return.
     */
    private int resultsSize;

    /**
     * The minimum search term length.
     */
    private int searchTermMinimumLength;

    /**
     * The path of the search root page.
     */
    private String searchRootPagePath;

    /**
     * Initialize the model.
     */
    @PostConstruct
    private void initModel() {
        resultsSize = currentStyle.get(PN_RESULTS_SIZE, PROP_RESULTS_SIZE_DEFAULT);
        searchTermMinimumLength = currentStyle.get(PN_SEARCH_TERM_MINIMUM_LENGTH, PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT);
        Resource currentResource = request.getResource();
        this.relativePath = Optional.ofNullable(currentPage.getPageManager().getContainingPage(currentResource))
            .map(Page::getPath)
            .map(path -> StringUtils.substringAfter(currentResource.getPath(), path))
            .orElse(null);
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @Override
    public int getSearchTermMinimumLength() {
        return searchTermMinimumLength;
    }

    @NotNull
    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @NotNull
    @Override
    public String getSearchRootPagePath() {
        if (this.searchRootPagePath == null) {
            this.searchRootPagePath = Optional.ofNullable(this.request.getResource().getValueMap().get(Search.PN_SEARCH_ROOT, String.class))
                .flatMap(searchRoot -> LocalizationUtils.getLocalPage(searchRoot, currentPage, this.request.getResourceResolver(), languageManager, relationshipManager))
                .map(Page::getPath)
                .orElseGet(currentPage::getPath);
        }
        return this.searchRootPagePath;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
