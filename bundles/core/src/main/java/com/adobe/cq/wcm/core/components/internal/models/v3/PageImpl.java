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

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v3/page";

    /**
     * Flags the child pages. Optionally available as a request attribute
     */
    private static final String ATTR_IS_CHILD_PAGE = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.isChildPage";

    /**
     * Is the current model to be considered as a model root
     */
    private static final String PR_IS_ROOT = "isRoot";

    /**
     * URL extension specific to the Sling Model exporter
     */
    private static final String URL_MODEL_EXTENSION = ".model.json";

    /**
     * Request attribute key of the request page entry point
     */
    private static final String ATTR_HIERARCHY_ENTRY_POINT_PAGE = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.entryPointPage";

    @ScriptVariable
    private Resource resource;

    private Map<String, ? extends Page> childPages = null;

    @Nullable
    @Override
    public String getExportedHierarchyType() {
        return HierarchyTypes.PAGE;
    }

    @Nonnull
    @Override
    public Map<String, ? extends Page> getExportedChildren() {
        if (childPages == null) {
            childPages = getChildPageModels(request);
        }

        return childPages;
    }

    @Nonnull
    @Override
    public String getExportedPath() {
        return currentPage.getPath();
    }

    @Nullable
    @Override
    public String getRootUrl() {
        if (currentStyle != null && currentStyle.containsKey(PR_IS_ROOT)) {
            return getModelUrl(request, currentPage);
        }

        com.day.cq.wcm.api.Page page = getRootPage();

        if (page != null) {
            return getModelUrl(request, page);
        }

        return null;
    }

    /**
     * @return Returns the root model of the given page
     */
    @Nullable
    @Override
    public Page getRootModel() {
        if (currentStyle != null && currentStyle.containsKey(PR_IS_ROOT)) {
            return this;
        }

        com.day.cq.wcm.api.Page rootPage = getRootPage();

        if (rootPage == null) {
            return null;
        }

        PageImpl.requestSetHierarchyEntryPoint(request, currentPage);

        return modelFactory.getModelFromWrappedRequest(PageImpl.getHierarchyServletRequest(request, rootPage), rootPage.getContentResource(), this.getClass());
    }

    /**
     * Returns a model URL for the given page URL
     *
     * @param url page URL
     * @return {@link String} model URL
     */
    protected static String getModelUrl(@Nonnull String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }

        int dotIndex = url.indexOf('.');

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
    protected static SlingHttpServletRequest getHierarchyServletRequest(@Nonnull SlingHttpServletRequest request, @Nonnull com.day.cq.wcm.api.Page page) {
        // Request attribute key of the component context
        final String ATTR_COMPONENT_CONTEXT = "com.day.cq.wcm.componentcontext";

        // Request attribute key of the current page
        final String ATTR_CURRENT_PAGE = "currentPage";

        SlingHttpServletRequest wrapper = new SlingHttpServletRequestWrapper(request);

        ComponentContext componentContext = (ComponentContext) request.getAttribute(ATTR_COMPONENT_CONTEXT);

        // When traversing child pages the currentPage must be updated
        wrapper.setAttribute(ATTR_COMPONENT_CONTEXT, new HierarchyComponentContextWrapper(componentContext, page));
        wrapper.setAttribute(ATTR_CURRENT_PAGE, page);

        return wrapper;
    }


    // style helpers zone

    /**
     * Returns the first numeric selector. The default value is 0
     *
     * @return {@link int} the defined traversal depth or 0 if none defined
     */
    protected static int getPageTreeTraversalDepth(Style style) {
        // Depth of the tree of pages
        final String PN_STRUCTURE_DEPTH = "structureDepth";

        Integer pageTreeTraversalDepth = null;

        if (style != null) {
            pageTreeTraversalDepth = style.get(PN_STRUCTURE_DEPTH, Integer.class);
        }

        if (pageTreeTraversalDepth == null) {
            return 0;
        }

        return pageTreeTraversalDepth;
    }

    /**
     * Get request's entry point attribute value
     */
    protected static com.day.cq.wcm.api.Page requestGetHierarchyEntryPoint(@Nonnull SlingHttpServletRequest request) {
        return (com.day.cq.wcm.api.Page) request.getAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE);
    }

    /**
     * Set request's entry point attribute value
     */
    protected static void requestSetHierarchyEntryPoint(@Nonnull SlingHttpServletRequest request, @Nonnull com.day.cq.wcm.api.Page page) {
        request.setAttribute(ATTR_HIERARCHY_ENTRY_POINT_PAGE, page);
    }

    /**
     * Returns the page structure patterns to filter the child pages to be exported.
     * The patterns can either be stored on the template policy of the page or provided as a request parameter
     *
     * @param request request
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

    /**
     * Returns a flat list of all the child pages of a given page
     *
     * @param page page from which to extract child pages
     * @param slingRequest request
     * @param structurePatterns patterns to filter child pages
     * @param depth depth of the traversal
     * @return {@link List}
     */
    @Nonnull
    private List<com.day.cq.wcm.api.Page> getChildPageRecursive(com.day.cq.wcm.api.Page page, SlingHttpServletRequest slingRequest, List<Pattern> structurePatterns, int depth) {
        // By default the value is 0 meaning we do not expose child pages
        // If the value is set as a positive number it is going to be exposed until the counter is brought down to 0
        // If the value is set to a negative value all the child pages will be exposed (full traversal tree - aka infinity)
        // Child pages do not expose their respective child pages
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

            pages.addAll(getChildPageRecursive(childPage, slingRequest, structurePatterns, depth));
        }

        return pages;
    }

    /**
     * Optionally add a child page that is the entry point of a site model request when this child is not added by the root structure configuration
     *
     * @param slingRequest  The current servlet request
     * @param childPages    List of child pages
     */
    private void addAsynchronousChildPage(@Nonnull SlingHttpServletRequest slingRequest, @Nonnull List<com.day.cq.wcm.api.Page> childPages) {
        // Child pages are only added to the root page
        if (Boolean.TRUE.equals(slingRequest.getAttribute(ATTR_IS_CHILD_PAGE))) {
            return;
        }

        // Eventually add a child page that is not part page root children of the but is the entry point of the request
        com.day.cq.wcm.api.Page entryPointPage = PageImpl.requestGetHierarchyEntryPoint(slingRequest);

        if (entryPointPage == null) {
            return;
        }

        // Filter the root page
        if (entryPointPage.getPath().equals(currentPage.getPath())) {
            return;
        }

        // Filter duplicates
        if (childPages.contains(entryPointPage)) {
            return;
        }

        childPages.add(entryPointPage);
    }

    @Nonnull
    private Map<String, Page> getChildPageModels(@Nonnull SlingHttpServletRequest slingRequest) {

        int pageTreeTraversalDepth = getPageTreeTraversalDepth(currentStyle);

        List<Pattern> pageFilterPatterns = PageImpl.getStructurePatterns(slingRequest, currentStyle);

        // Setting the child page to true to prevent child pages to expose their own child pages
        SlingHttpServletRequest slingRequestWrapper = new SlingHttpServletRequestWrapper(slingRequest);

        Map<String, Page> itemWrappers = new LinkedHashMap<>();

        List<com.day.cq.wcm.api.Page> children = getChildPageRecursive(currentPage, slingRequestWrapper, pageFilterPatterns, pageTreeTraversalDepth);

        addAsynchronousChildPage(slingRequest, children);

        // Add a flag to inform the model of the child pages that they are not the root of the tree
        slingRequestWrapper.setAttribute(ATTR_IS_CHILD_PAGE, true);

        for (com.day.cq.wcm.api.Page childPage: children) {
            Resource childPageContentResource = childPage.getContentResource();

            if (childPageContentResource == null) {
                continue;
            }

            // Try to pass the templated content resource
            TemplatedResource templatedResource = childPageContentResource.adaptTo(TemplatedResource.class);

            if (templatedResource != null) {
                childPageContentResource = templatedResource;
            }

            itemWrappers.put(childPage.getPath(), modelFactory.getModelFromWrappedRequest(PageImpl.getHierarchyServletRequest(slingRequestWrapper, childPage), childPageContentResource, Page.class));
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
    private String getModelUrl(@Nonnull SlingHttpServletRequest slingRequest, @Nonnull com.day.cq.wcm.api.Page page) {
        return PageImpl.getModelUrl(Utils.getURL(slingRequest, page));
    }

    /**
     * @return Returns the root (app) page the current page is part of
     */
    private com.day.cq.wcm.api.Page getRootPage() {
        com.day.cq.wcm.api.Page page = currentPage;
        boolean isRootModel = false;

        ContentPolicyManager contentPolicyManager = resource.getResourceResolver().adaptTo(ContentPolicyManager.class);

        do {
            page = page.getParent();

            if (page == null) {
                continue;
            }

            Template template = page.getTemplate();

            if (template == null || !template.hasStructureSupport()) {
                continue;
            }

            Resource pageContentResource = page.getContentResource();

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

            isRootModel = properties.containsKey(PR_IS_ROOT);

        } while(page != null && !isRootModel);

        return page;
    }
}
