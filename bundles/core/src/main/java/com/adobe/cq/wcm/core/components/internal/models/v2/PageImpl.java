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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.SlingHttpServletRequestWrapper;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.factory.ModelFactory;
import org.osgi.framework.Version;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.export.json.hierarchy.type.HierarchyTypes;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.internal.models.v1.RedirectItemImpl;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.adobe.cq.wcm.core.components.models.Page;
import com.adobe.granite.license.ProductInfoProvider;
import com.adobe.granite.ui.clientlibs.ClientLibrary;
import com.adobe.granite.ui.clientlibs.HtmlLibraryManager;
import com.adobe.granite.ui.clientlibs.LibraryType;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Page.class, ContainerExporter.class}, resourceType = PageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class PageImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.PageImpl implements Page {

    protected static final String RESOURCE_TYPE = "core/wcm/components/page/v2/page";
    protected static final String PN_CLIENTLIBS_JS_HEAD = "clientlibsJsHead";
    public static final String PN_REDIRECT_TARGET = "cq:redirectTarget";

    private Boolean hasCloudconfigSupport;

    /**
     * Flags the child pages. Optionally available as a request attribute
     */
    private static final String ATTR_IS_CHILD_PAGE = "com.adobe.cq.wcm.core.components.internal.models.HierarchyPage.isChildPage";

    /**
     * Is the current model to be considered as a model root
     */
    private static final String PR_IS_ROOT = "isRoot";

    @Inject
    private ModelFactory modelFactory;

    @OSGiService
    private HtmlLibraryManager htmlLibraryManager;

    @OSGiService
    private ProductInfoProvider productInfoProvider;

    @Self
    protected SlingHttpServletRequest request;

    @ScriptVariable
    private ComponentContext componentContext;

    @ScriptVariable
    private Resource resource;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL,
                   name = PN_REDIRECT_TARGET)
    private String redirectTargetValue;

    private String appResourcesPath;
    private NavigationItem redirectTarget;

    private Map<String, ? extends Page> childPages = null;

    protected String[] clientLibCategoriesJsBody = new String[0];
    protected String[] clientLibCategoriesJsHead = new String[0];

    @PostConstruct
    protected void initModel() {
        super.initModel();
        String resourcesClientLibrary = currentStyle.get(PN_APP_RESOURCES_CLIENTLIB, String.class);
        if (resourcesClientLibrary != null) {
            Collection<ClientLibrary> clientLibraries =
                    htmlLibraryManager.getLibraries(new String[]{resourcesClientLibrary}, LibraryType.CSS, true, true);
            ArrayList<ClientLibrary> clientLibraryList = Lists.newArrayList(clientLibraries.iterator());
            if (!clientLibraryList.isEmpty()) {
                appResourcesPath = getProxyPath(clientLibraryList.get(0));
            }
        }
        populateClientLibCategoriesJs();
        setRedirect();
    }

    private void setRedirect() {
        if (StringUtils.isNotEmpty(redirectTargetValue)) {
            redirectTarget = new RedirectItemImpl(redirectTargetValue, request);
        }
    }

    private String getProxyPath(ClientLibrary lib) {
        String path = lib.getPath();
        if (lib.allowProxy()) {
            for (String searchPath : request.getResourceResolver().getSearchPath()) {
                if (path.startsWith(searchPath)) {
                    path = request.getContextPath() + "/etc.clientlibs/" + path.replaceFirst(searchPath, "");
                }
            }
        } else {
            if (request.getResourceResolver().getResource(lib.getPath()) == null) {
                path = null;
            }
        }
        if (path != null) {
            path = path + "/resources";
        }
        return path;
    }

    protected void populateClientLibCategoriesJs() {
        if (currentStyle != null) {
            clientLibCategoriesJsHead = currentStyle.get(PN_CLIENTLIBS_JS_HEAD, ArrayUtils.EMPTY_STRING_ARRAY);
            LinkedHashSet<String> categories = new LinkedHashSet<>(Arrays.asList(clientLibCategories));
            categories.removeAll(Arrays.asList(clientLibCategoriesJsHead));
            clientLibCategoriesJsBody = categories.toArray(new String[0]);
        }
    }

    @Override
    protected void loadFavicons(String designPath) {
    }

    @Override
    public Map<String, String> getFavicons() {
        throw new UnsupportedOperationException();
    }

    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsBody() {
        return Arrays.copyOf(clientLibCategoriesJsBody, clientLibCategoriesJsBody.length);
    }

    @Override
    @JsonIgnore
    public String[] getClientLibCategoriesJsHead() {
        return Arrays.copyOf(clientLibCategoriesJsHead, clientLibCategoriesJsHead.length);
    }

    @Override
    public String getAppResourcesPath() {
        return appResourcesPath;
    }

    @Override
    public String getCssClassNames() {
        Set<String> cssClassesSet = componentContext.getCssClassNames();
        return StringUtils.join(cssClassesSet, " ");
    }

    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        return super.getExportedItemsOrder();
    }

    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return super.getExportedItems();
    }

    @Nullable
    @Override
    public NavigationItem getRedirectTarget() {
        return redirectTarget;
    }

    @Override
    public boolean hasCloudconfigSupport() {
        if (hasCloudconfigSupport == null) {
            if (productInfoProvider == null || productInfoProvider.getProductInfo() == null ||
                    productInfoProvider.getProductInfo().getVersion() == null) {
                hasCloudconfigSupport = false;
            } else {
                hasCloudconfigSupport = productInfoProvider.getProductInfo().getVersion().compareTo(new Version("6.4.0")) >= 0;
            }
        }
        return hasCloudconfigSupport;
    }

    @Nullable
    @Override
    public String getExportedHierarchyType() {
        return HierarchyTypes.PAGE;
    }

    @Nonnull
    @Override
    public Map<String, ? extends Page> getExportedChildren() {
        if (childPages == null) {
            childPages = getChildPageModels(request, Page.class);
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

        PageHelpers.requestSetHierarchyEntryPoint(request, currentPage);

        return modelFactory.getModelFromWrappedRequest(PageHelpers.getHierarchyServletRequest(request, rootPage), rootPage.getContentResource(), this.getClass());
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
        com.day.cq.wcm.api.Page entryPointPage = PageHelpers.requestGetHierarchyEntryPoint(slingRequest);

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
    private <T> Map<String, T> getChildPageModels(@Nonnull SlingHttpServletRequest slingRequest,
                                                  @Nonnull Class<T> modelClass) {

        int pageTreeTraversalDepth = PageHelpers.getPageTreeTraversalDepth(currentStyle);

        List<Pattern> pageFilterPatterns = PageHelpers.getStructurePatterns(slingRequest, currentStyle);

        // Setting the child page to true to prevent child pages to expose their own child pages
        SlingHttpServletRequest slingRequestWrapper = new SlingHttpServletRequestWrapper(slingRequest);

        Map<String, T> itemWrappers = new LinkedHashMap<>();

        List<com.day.cq.wcm.api.Page> childPages = getChildPageRecursive(currentPage, slingRequestWrapper, pageFilterPatterns, pageTreeTraversalDepth);

        addAsynchronousChildPage(slingRequest, childPages);

        // Add a flag to inform the model of the child pages that they are not the root of the tree
        slingRequestWrapper.setAttribute(ATTR_IS_CHILD_PAGE, true);

        for (com.day.cq.wcm.api.Page childPage: childPages) {
            Resource childPageContentResource = childPage.getContentResource();

            if (childPageContentResource == null) {
                continue;
            }

            // Try to pass the templated content resource
            TemplatedResource templatedResource = childPageContentResource.adaptTo(TemplatedResource.class);

            if (templatedResource != null) {
                childPageContentResource = templatedResource;
            }

            itemWrappers.put(childPage.getPath(), modelFactory.getModelFromWrappedRequest(PageHelpers.getHierarchyServletRequest(slingRequestWrapper, childPage), childPageContentResource, modelClass));
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
        return PageHelpers.getModelUrl(Utils.getURL(slingRequest, page));
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
