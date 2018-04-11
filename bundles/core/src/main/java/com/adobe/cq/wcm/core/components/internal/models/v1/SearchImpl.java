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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Search.class, ComponentExporter.class},
       resourceType = {SearchImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME ,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SearchImpl implements Search {

    protected static final String RESOURCE_TYPE = "core/wcm/components/search/v1/search";

    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;
    public static final int PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT = 3;
    public static final String PROP_SEARCH_ROOT_DEFAULT = "/content";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    private String relativePath;
    private int resultsSize;
    private int searchTermMinimumLength;

    @PostConstruct
    private void initModel() {
        resultsSize = currentStyle.get(PN_RESULTS_SIZE, PROP_RESULTS_SIZE_DEFAULT);
        searchTermMinimumLength = currentStyle.get(PN_SEARCH_TERM_MINIMUM_LENGTH, PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT);
        PageManager pageManager = currentPage.getPageManager();
        Resource currentResource = request.getResource();
        if (pageManager != null) {
            Page containingPage = pageManager.getContainingPage(currentResource);
            if(containingPage != null) {
                relativePath = StringUtils.substringAfter(currentResource.getPath(), containingPage.getPath());
            }
        }
    }

    @Override
    public int getResultsSize() {
        return resultsSize;
    }

    @Override
    public int getSearchTermMinimumLength() {
        return searchTermMinimumLength;
    }

    @Nonnull
    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Nonnull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
