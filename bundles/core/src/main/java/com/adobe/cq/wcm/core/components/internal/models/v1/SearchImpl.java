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
import javax.jcr.RangeIterator;

import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.Search;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Search.class, ComponentExporter.class},
       resourceType = {SearchImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME ,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class SearchImpl extends AbstractComponentImpl implements Search {

    protected static final String RESOURCE_TYPE = "core/wcm/components/search/v1/search";

    public static final int PROP_RESULTS_SIZE_DEFAULT = 10;
    public static final int PROP_SEARCH_TERM_MINIMUM_LENGTH_DEFAULT = 3;
    public static final String PROP_SEARCH_ROOT_DEFAULT = "/content";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private Style currentStyle;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

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

    @NotNull
    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @NotNull
    @Override
    public String getSearchRootPagePath() {
        String searchRoot = Optional.ofNullable(this.request.getResource().getValueMap().get(Search.PN_SEARCH_ROOT, String.class))
            .orElseGet(() -> this.currentStyle.get(Search.PN_SEARCH_ROOT, SearchImpl.PROP_SEARCH_ROOT_DEFAULT));
        return getSearchRootPagePath(languageManager, relationshipManager, searchRoot, currentPage);
    }

    @Nullable
    public static String getSearchRootPagePath(@NotNull final LanguageManager languageManager,
                                        @NotNull final LiveRelationshipManager relationshipManager,
                                        @Nullable final String searchRoot,
                                        @NotNull final Page currentPage) {
        String searchRootPagePath = null;
        if (StringUtils.isNotEmpty(searchRoot)) {
            PageManager pageManager = currentPage.getPageManager();
            Page rootPage = pageManager.getPage(searchRoot);
            if (rootPage != null) {
                Page searchRootLanguageRoot = languageManager.getLanguageRoot(rootPage.getContentResource());
                Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
                RangeIterator liveCopiesIterator = null;
                try {
                    liveCopiesIterator = relationshipManager.getLiveRelationships(currentPage.adaptTo(Resource.class), null, null);
                } catch (WCMException e) {
                    // ignore it
                }
                if (searchRootLanguageRoot != null && currentPageLanguageRoot != null && !searchRootLanguageRoot.equals
                    (currentPageLanguageRoot)) {
                    // check if there's a language copy of the search root
                    Page languageCopySearchRoot = pageManager.getPage(ResourceUtil.normalize(currentPageLanguageRoot.getPath() + "/" +
                        getRelativePath(searchRootLanguageRoot, rootPage)));
                    if (languageCopySearchRoot != null) {
                        rootPage = languageCopySearchRoot;
                    }
                } else if (liveCopiesIterator != null) {
                    while (liveCopiesIterator.hasNext()) {
                        LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                        if (currentPage.getPath().startsWith(relationship.getTargetPath() + "/")) {
                            Page liveCopySearchRoot = pageManager.getPage(relationship.getTargetPath());
                            if (liveCopySearchRoot != null) {
                                rootPage = liveCopySearchRoot;
                                break;
                            }
                        }
                    }
                }
                searchRootPagePath = rootPage.getPath();
            }
        }
        return searchRootPagePath;
    }

    /**
     * Get the relative path between the two pages.
     *
     * @param root The root page.
     * @param child The child page.
     * @return The relative path between root and child page, null if child is not a child of root.
     */
    @Nullable
    private static String getRelativePath(@NotNull final Page root, @NotNull final Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

}
