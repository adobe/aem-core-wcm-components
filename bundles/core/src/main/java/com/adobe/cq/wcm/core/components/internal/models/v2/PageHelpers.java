/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Style;

final class PageHelpers {

    /**
     * URL extension specific to the Sling Model exporter
     */
    private static final String URL_MODEL_EXTENSION = ".model.json";

    /**
     * Request attribute key of the request page entry point
     */
    private static final String HIERARCHY_ENTRY_POINT_PAGE_ATTR = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.entryPointPage";

    private PageHelpers() {
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param url page URL
     * @return {@link String} model URL
     */
    static String getModelUrl(@Nonnull String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        int dotIndex = url.indexOf(".");

        if (dotIndex < 0) {
            dotIndex = url.length();
        }

        return url.substring(0, dotIndex) + URL_MODEL_EXTENSION;
    }

    /**
     * Wrap the provided request to ensure the static references to the containing page of a component is accurate
     *
     * @param request request to be wrapped
     * @param page page to be referenced as statically containing the current page content
     * @return {@link SlingHttpServletRequest} a {@link SlingHttpServletRequestWrapper} containing given page and request
     */
    @Nonnull
    static SlingHttpServletRequest getHierarchyServletRequest(@Nonnull SlingHttpServletRequest request, @Nonnull Page page) {
        // Request attribute key of the component context
        final String COMPONENT_CONTEXT_ATTR = "com.day.cq.wcm.componentcontext";

        // Request attribute key of the current page
        final String CURRENT_PAGE_ATTR = "currentPage";

        SlingHttpServletRequest wrapper = new SlingHttpServletRequestWrapper(request);

        ComponentContext componentContext = (ComponentContext) request.getAttribute(COMPONENT_CONTEXT_ATTR);

        // When traversing child pages the currentPage must be updated
        wrapper.setAttribute(COMPONENT_CONTEXT_ATTR, new HierarchyComponentContextWrapper(componentContext, page));
        wrapper.setAttribute(CURRENT_PAGE_ATTR, page);

        return wrapper;
    }


    // style helpers zone

    /**
     * Returns the first numeric selector. The default value is 0
     *
     * @return {@link int} the defined traversal depth or 0 if none defined
     */
    static int getPageTreeTraversalDepth(Style style) {
        // Depth of the tree of pages
        final String STRUCTURE_DEPTH_PN = "structureDepth";

        Integer pageTreeTraversalDepth = null;

        if (style != null) {
            pageTreeTraversalDepth = style.get(STRUCTURE_DEPTH_PN, Integer.class);
        }

        if (pageTreeTraversalDepth == null) {
            return 0;
        }

        return pageTreeTraversalDepth;
    }

    /**
     * Get request's entry point attribute value
     */
    static Page requestGetHierarchyEntryPoint(@Nonnull SlingHttpServletRequest request) {
        return (Page) request.getAttribute(HIERARCHY_ENTRY_POINT_PAGE_ATTR);
    }

    /**
     * Set request's entry point attribute value
     */
    static void requestSetHierarchyEntryPoint(@Nonnull SlingHttpServletRequest request, @Nonnull Page page) {
        request.setAttribute(HIERARCHY_ENTRY_POINT_PAGE_ATTR, page);
    }

    /**
     * Returns the page structure patterns to filter the child pages to be exported.
     * The patterns can either be stored on the template policy of the page or provided as a request parameter
     *
     * @param request request
     * @return {@link List} list of page structure patterns
     */
    @Nonnull
    static List<Pattern> getStructurePatterns(@Nonnull SlingHttpServletRequest request, Style currentStyle) {
        // List of Regexp patterns to filter the exported tree of pages
        final String STRUCTURE_PATTERNS_PN = "structurePatterns";

        RequestParameter pageFilterParameter = request.getRequestParameter(STRUCTURE_PATTERNS_PN.toLowerCase());

        String rawPageFilters = null;

        if (pageFilterParameter != null) {
            rawPageFilters = pageFilterParameter.getString();
        }

        if (currentStyle != null && StringUtils.isBlank(rawPageFilters)) {
            rawPageFilters = currentStyle.get(STRUCTURE_PATTERNS_PN, String.class);
        }

        if (StringUtils.isBlank(rawPageFilters)) {
            return Collections.emptyList();
        }

        String[] pageFilters = rawPageFilters.split(",");

        List<Pattern> pageFilterPatterns = new ArrayList<>();
        for (String pageFilter : pageFilters) {
            pageFilterPatterns.add(Pattern.compile(pageFilter));
        }

        return pageFilterPatterns;
    }

}
