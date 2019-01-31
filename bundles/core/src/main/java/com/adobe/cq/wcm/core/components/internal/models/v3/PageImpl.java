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
package com.adobe.cq.wcm.core.components.internal.models.v3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.request.RequestParameter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.hierarchy.type.HierarchyTypes;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;

/**
 * <p>
 * Page that allows the retrieval of the model in JSON format with hierarchical structures of more than one Page.
 *</p><p>
 * The content of the JSON export of the page's model is limited by two parameters:
 * <ul><li>
 *     filterPatterns - paths from which Pages are to be included
 * </li><li>
 *     traversalDepth - number of levels to be included.
 * </li></ul></p>
 *
 * <p>
 * However there is also possibility to use the Java API to get the model. If the {@link #getHierarchyRootModel()} is
 * used the {@link PageImpl#createHierarchyServletRequest(SlingHttpServletRequest, com.day.cq.wcm.api.Page,
 * com.day.cq.wcm.api.Page)} would wrap the request saving:
 * <ul><li>
 *     the original request
 * </li><li>
 *      the root page of the hierarchy
 * </li><li>
 *     the entry point page - so the page for which the actual request was made
 * </li></ul>
 * in order to provide the full hierarchy from the root, with all descendants' models of the root page (with respect
 * to filterPatterns or traversalDepth) plus the entry point page (even if was excluded based on rules enforced by
 * filterPatterns or traversalDepth).
 * </p>
 *
 * <p>
 * Among many others, the exported structure would contain:
 * <ul><li>
 *     a flat map of all descendants' models identifiable by their paths (getExportedChildren()) -> :children
 * </li><li>
 *      a map of the content of the page (v1.getExportedItems()) -> :items,
 *      together with the order (v1.getExportedItemsOrder()) -> :itemsOrder
 * </li></ul>
 * </p>
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v3/page";

    /**
     * Name of the request attribute which is used to flag the child pages. Optionally available as a request attribute.
     */
    private static final String ATTR_IS_CHILD_PAGE = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.isChildPage";

    /**
     * Is the current model to be considered as a model root
     */
    private static final String PN_IS_ROOT = "isRoot";

    /**
     * URL extension specific to the Sling Model exporter
     */
    private static final String JSON_EXPORT_SUFFIX = ".model.json";

    /**
     * Name of the request attribute that defines whether the page is an entry point of the request.
     */
    private static final String ATTR_HIERARCHY_ENTRY_POINT_PAGE = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.entryPointPage";

    @ScriptVariable
    private Resource resource;

    /**
     * {@link Map} containing the page models with their corresponding paths (as keys).
     */
    private Map<String, ? extends Page> descendedPageModels = null;

    private com.day.cq.wcm.api.Page rootPage = null;

    @Nullable
    @Override
    public String getExportedHierarchyType() {
        return HierarchyTypes.PAGE;
    }

    @Nonnull
    @Override
    public Map<String, ? extends Page> getExportedChildren() {
        if (descendedPageModels == null) {
            descendedPageModels = getDescendantsModels();
        }

        return descendedPageModels;
    }

    @Nonnull
    @Override
    public String getExportedPath() {
        return currentPage.getPath();
    }

    @Nullable
    @Override
    public String getHierarchyRootJsonExportUrl() {
        if (isRootPage()) {
            return getPageJsonExportUrl(request, currentPage);
        }

        com.day.cq.wcm.api.Page hierarchyRootPage = getRootPage();

        if (hierarchyRootPage != null) {
            return getPageJsonExportUrl(request, hierarchyRootPage);
        }

        return null;
    }

    /**
     * @return Returns the model of the root page which this page is part of.
     */
    @Nullable
    @Override
    public Page getHierarchyRootModel() {
        if (isRootPage()) {
            return this;
        }

        com.day.cq.wcm.api.Page hierarchyRootPage = getRootPage();

        if (hierarchyRootPage == null) {
            return null;
        }

        return modelFactory.getModelFromWrappedRequest(
            PageImpl.createHierarchyServletRequest(request, hierarchyRootPage, currentPage),
            hierarchyRootPage.getContentResource(),
            this.getClass());
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param url page URL
     * @return {@link String} model URL
     */
    protected static String getJsonExportURL(@Nonnull String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        int dotIndex = url.indexOf('.');

        if (dotIndex < 0) {
            dotIndex = url.length();
        }

        return url.substring(0, dotIndex) + JSON_EXPORT_SUFFIX;
    }

    /**
     * Creates a new request wrapping the {@code request} from the parameters.<br />
     *
     * The new request is created in order to set attributes: <ul>
     * <li>
     *     componentcontext - includes the {@link Page} from {@code page} parameter and the context from {@code request}
     * </li>
     * <li>
     *     currentPage - {@link Page} from {@code page} parameter and the
     * </li><li>
     *     entryPointPage - {@link Page} from {@code entryPage} parameter
     * </li></ul>
     *
     * to ensure that the references to the page are accurate for the hierarchical structure.
     *
     * @param request request to be wrapped
     * @param page page to be referenced as statically containing the current page content
     * @param entryPage page that is the entry point of the request
     * @return {@link SlingHttpServletRequest} a {@link SlingHttpServletRequestWrapper} containing given page and request
     */
    protected static SlingHttpServletRequest createHierarchyServletRequest(@Nonnull SlingHttpServletRequest request,
                                                                           @Nonnull com.day.cq.wcm.api.Page page,
                                                                           @Nullable com.day.cq.wcm.api.Page entryPage) {
        // Request attribute key of the component context
        final String ATTR_COMPONENT_CONTEXT = "com.day.cq.wcm.componentcontext";

        // Request attribute key of the current page
        final String ATTR_CURRENT_PAGE = "currentPage";

        SlingHttpServletRequest wrapper = new SlingHttpServletRequestWrapper(request);

        ComponentContext componentContext = (ComponentContext) request.getAttribute(ATTR_COMPONENT_CONTEXT);

        // When traversing child pages the currentPage must be updated
        wrapper.setAttribute(ATTR_COMPONENT_CONTEXT, new HierarchyComponentContextWrapper(componentContext, page));
        wrapper.setAttribute(ATTR_CURRENT_PAGE, page);
        wrapper.setAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE, entryPage);

        return wrapper;
    }


    // style helpers zone

    /**
     *  Returns the tree depth that can be configured in the policy. Defaults to 0.
     *
     * @param style policy to search in
     * @return {@link int} the defined traversal depth or 0 if none defined
     */
    protected static int getPageTreeDepth(Style style) {
        // Depth of the tree of pages

        Integer pageTreeTraversalDepth = getStructureDepth(style);


        if (pageTreeTraversalDepth == null) {
            return 0;
        }

        return pageTreeTraversalDepth;
    }

    /**
     * Get request's entry point attribute value
     *
     * @param request request to get the entry point attribute from
     */
    protected static com.day.cq.wcm.api.Page getEntryPoint(@Nonnull SlingHttpServletRequest request) {
        return (com.day.cq.wcm.api.Page) request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE);
    }

    /**
     * Returns the page structure patterns to filter the descendants to be exported.
     * The patterns can either be stored on the template policy of the page or provided as a request parameter
     *
     * @param request request
     * @param currentStyle current style
     * @return {@link List} list of page structure patterns
     */
    @Nonnull
    protected static List<Pattern> getStructurePatterns(@Nonnull SlingHttpServletRequest request, Style currentStyle) {
        // List of Regexp patterns to filter the exported tree of pages
        final String PN_STRUCTURE_PATTERNS = "structurePatterns";

        RequestParameter pageFilterParameter = request.getRequestParameter(PN_STRUCTURE_PATTERNS.toLowerCase());

        String rawPageFilters = null;

        if (pageFilterParameter != null) {
            rawPageFilters = pageFilterParameter.getString();
        }

        if (currentStyle != null && StringUtils.isBlank(rawPageFilters)) {
            rawPageFilters = currentStyle.get(PN_STRUCTURE_PATTERNS, String.class);
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


    private boolean isRootPage() {
        return currentStyle != null && currentStyle.containsKey(PN_IS_ROOT);
    }

    /**
     * Get style's structure depth attribute value
     *
     * @param style style to search in
     */
    private static Integer getStructureDepth(Style style) {
        if (style != null) {
            return style.get(PN_STRUCTURE_DEPTH, Integer.class);
        }

        return null;
    }

    /**
     * Traverses the tree of descendants of the page. Descendants that:
     * - are not deeper than defined depth
     * - has path that matches one of defined structurePattern
     * would be returned in a flat list.
     *
     * @param page page from which to extract descended pages
     * @param slingRequest request
     * @param structurePatterns patterns to filter descended pages
     * @param depth depth of the traversal
     * @return {@link List}
     */
    @Nonnull
    private List<com.day.cq.wcm.api.Page> getDescendants(com.day.cq.wcm.api.Page page, SlingHttpServletRequest slingRequest, List<Pattern> structurePatterns, int depth) {
        // By default the depth is 0 meaning we do not expose descendants
        // If the value is set as a positive number it is going to be exposed until the counter is brought down to 0
        // If the value is set to a negative value all descendants will be exposed (full traversal tree - aka infinity)
        // Descendants pages do not expose their child pages
        if (page == null || depth == 0 || Boolean.TRUE.equals(slingRequest.getAttribute(ATTR_IS_CHILD_PAGE))) {
            return Collections.emptyList();
        }

        List<com.day.cq.wcm.api.Page> pages = new ArrayList<>();
        Iterator<com.day.cq.wcm.api.Page> childPagesIterator = page.listChildren();

        if (childPagesIterator == null || !childPagesIterator.hasNext()) {
            return Collections.emptyList();
        }

        // we are about to explore one lower level down the tree
        depth--;

        boolean noPageFilters = structurePatterns.isEmpty();

        while (childPagesIterator.hasNext()) {
            com.day.cq.wcm.api.Page childPage = childPagesIterator.next();
            boolean found = noPageFilters;

            for (Pattern pageFilterPattern : structurePatterns) {
                if (pageFilterPattern.matcher(childPage.getPath()).find()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                continue;
            }

            pages.add(childPage);

            pages.addAll(getDescendants(childPage, slingRequest, structurePatterns, depth));
        }

        return pages;
    }

    /**
     * Optionally add a page that is the entry point of a site model request even when was not added because
     * of the root structure configuration
     *
     * @param descendedPages    List of descendants
     */
    private void addEntryPointPage(@Nonnull List<com.day.cq.wcm.api.Page> descendedPages) {
        // Child pages are only added to the root page
        if (Boolean.TRUE.equals(request.getAttribute(ATTR_IS_CHILD_PAGE))) {
            return;
        }

        com.day.cq.wcm.api.Page entryPointPage = PageImpl.getEntryPoint(request);

        if (entryPointPage == null) {
            return;
        }

        // Filter the root page
        if (entryPointPage.getPath().equals(currentPage.getPath())) {
            return;
        }

        // Filter duplicates
        if (descendedPages.contains(entryPointPage)) {
            return;
        }

        descendedPages.add(entryPointPage);
    }

    /**
     * Returns all descended page models of the currentPage plus the entryPoint page (even if was excluded based on
     * rules enforced by filterPatterns or traversalDepth).
     *
     * @return {@link Map} containing the page models with their corresponding paths (as keys).
     */
    @Nonnull
    private Map<String, Page> getDescendantsModels() {

        int pageTreeTraversalDepth = PageImpl.getPageTreeDepth(currentStyle);

        List<Pattern> pageFilterPatterns = PageImpl.getStructurePatterns(request, currentStyle);

        // Setting the child page to true to prevent child pages to expose their own child pages
        SlingHttpServletRequest slingRequestWrapper = new SlingHttpServletRequestWrapper(request);

        Map<String, Page> itemWrappers = new LinkedHashMap<>();

        List<com.day.cq.wcm.api.Page> descendants = getDescendants(currentPage, slingRequestWrapper,
            pageFilterPatterns, pageTreeTraversalDepth);

        addEntryPointPage(descendants);

        // Add a flag to inform the model of the descendant page that it is not the root of the returned hierarchy
        slingRequestWrapper.setAttribute(ATTR_IS_CHILD_PAGE, true);

        for (com.day.cq.wcm.api.Page childPage: descendants) {
            Resource childPageContentResource = childPage.getContentResource();

            if (childPageContentResource == null) {
                continue;
            }

            // Try to pass the templated content resource
            TemplatedResource templatedResource = childPageContentResource.adaptTo(TemplatedResource.class);

            if (templatedResource != null) {
                childPageContentResource = templatedResource;
            }

            itemWrappers.put(childPage.getPath(), modelFactory.getModelFromWrappedRequest(
                PageImpl.createHierarchyServletRequest(slingRequestWrapper, childPage, null),
                childPageContentResource,
                Page.class));
        }

        return itemWrappers;
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param slingRequest the current servlet request
     * @param page page for which to get the model URL
     * @return {@link String} model URL
     */
    private String getPageJsonExportUrl(@Nonnull SlingHttpServletRequest slingRequest, @Nonnull com.day.cq.wcm.api.Page page) {
        return PageImpl.getJsonExportURL(Utils.getURL(slingRequest, page));
    }

    /**
     * @return Returns the root page which the current page is part of.
     */
    private com.day.cq.wcm.api.Page getRootPage() {
        if (rootPage != null) {
            return rootPage;
        }

        com.day.cq.wcm.api.Page tempRootPage = currentPage;
        boolean isRootPage = false;

        ContentPolicyManager contentPolicyManager = resource.getResourceResolver().adaptTo(ContentPolicyManager.class);

        do {
            tempRootPage = tempRootPage.getParent();

            if (tempRootPage == null) {
                continue;
            }

            Template template = tempRootPage.getTemplate();

            if (template == null || !template.hasStructureSupport()) {
                continue;
            }

            Resource pageContentResource = tempRootPage.getContentResource();

            if (pageContentResource == null) {
                continue;
            }

            ContentPolicy pageContentPolicy = contentPolicyManager.getPolicy(pageContentResource);

            if (pageContentPolicy == null) {
                continue;
            }

            ValueMap properties = pageContentPolicy.getProperties();

            if (properties == null) {
                continue;
            }

            isRootPage = properties.containsKey(PN_IS_ROOT);

        } while(tempRootPage != null && !isRootPage);

        rootPage = tempRootPage;

        return rootPage;
    }
}
