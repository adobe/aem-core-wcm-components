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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.jcr.RangeIterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageImpl;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.LanguageManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.msm.api.LiveRelationship;
import com.day.cq.wcm.msm.api.LiveRelationshipManager;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {Navigation.class, ComponentExporter.class},
       resourceType = {NavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationImpl implements Navigation {

    public static final String RESOURCE_TYPE = "core/wcm/components/navigation/v1/navigation";

    @Self
    private SlingHttpServletRequest request;

    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @OSGiService
    private LanguageManager languageManager;

    @OSGiService
    private LiveRelationshipManager relationshipManager;

    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    private String accessibilityLabel;

    private int structureDepth;
    private String navigationRootPage;
    private List<NavigationItem> items;
    private boolean skipNavigationRoot;
    private int structureStart;

    @PostConstruct
    private void initModel() {
        structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, -1));
        boolean collectAllPages = properties.get(PN_COLLECT_ALL_PAGES, currentStyle.get(PN_COLLECT_ALL_PAGES, true));
        if (collectAllPages) {
            structureDepth = -1;
        }
        navigationRootPage = properties.get(PN_NAVIGATION_ROOT, currentStyle.get(PN_NAVIGATION_ROOT, String.class));
        if (currentStyle.containsKey(PN_STRUCTURE_START) || properties.containsKey(PN_STRUCTURE_START)) {
            //workaround to maintain the content of Navigation component of users in case they update to the current i.e. the `structureStart` version.
            structureStart = properties.get(PN_STRUCTURE_START, currentStyle.get(PN_STRUCTURE_START, 1));
        } else {
            skipNavigationRoot = properties.get(PN_SKIP_NAVIGATION_ROOT, currentStyle.get(PN_SKIP_NAVIGATION_ROOT, true));
            if (skipNavigationRoot) {
                structureStart = 1;
            } else {
                structureStart = 0;
            }
        }
    }

    @Override
    public List<NavigationItem> getItems() {
        if (items == null) {
            PageManager pageManager = currentPage.getPageManager();
            Page rootPage = pageManager.getPage(navigationRootPage);
            if (rootPage != null) {
                NavigationRoot navigationRoot = new NavigationRoot(rootPage, structureDepth);
                Page navigationRootLanguageRoot = navigationRoot.getPageResource().map(languageManager::getLanguageRoot).orElse(null);
                Page currentPageLanguageRoot = languageManager.getLanguageRoot(currentPage.getContentResource());
                RangeIterator liveCopiesIterator = null;
                try {
                    liveCopiesIterator = relationshipManager.getLiveRelationships(navigationRoot.page.adaptTo(Resource.class), null, null);
                } catch (WCMException e) {
                    // ignore it
                }
                if (navigationRootLanguageRoot != null && currentPageLanguageRoot != null && !navigationRootLanguageRoot.equals
                        (currentPageLanguageRoot)) {
                    // check if there's a language copy of the navigation root
                    Page languageCopyNavigationRoot = pageManager.getPage(ResourceUtil.normalize(currentPageLanguageRoot.getPath() + "/" +
                            getRelativePath(navigationRootLanguageRoot, navigationRoot.page)));
                    if (languageCopyNavigationRoot != null) {
                        navigationRoot = new NavigationRoot(languageCopyNavigationRoot, structureDepth);
                    }
                } else if (liveCopiesIterator != null) {
                    while (liveCopiesIterator.hasNext()) {
                        LiveRelationship relationship = (LiveRelationship) liveCopiesIterator.next();
                        String currentPagePath = currentPage.getPath() + "/";
                        if (currentPagePath.startsWith(relationship.getTargetPath() + "/")) {
                            Page liveCopyNavigationRoot = pageManager.getPage(relationship.getTargetPath());
                            if (liveCopyNavigationRoot != null) {
                                navigationRoot = new NavigationRoot(liveCopyNavigationRoot, structureDepth);
                                break;
                            }
                        }
                    }
                }
                items = getNavigationTree(navigationRoot);
            } else {
                items = Collections.emptyList();
            }
        }
        return Collections.unmodifiableList(items);
    }

    @Override
    public String getAccessibilityLabel() {
        return accessibilityLabel;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    /**
     * Builds the navigation tree for a {@code navigationRoot} page.
     *
     * @param navigationRoot the global navigation tree root (start page)
     * @param subtreeRoot the current sub-tree root (changes depending on the level of recursion)
     * @return the list of collected navigation trees
     */
    private List<NavigationItem> getItems(NavigationRoot navigationRoot, Page subtreeRoot) {
        List<NavigationItem> pages = new ArrayList<>();
        if (navigationRoot.structureDepth == -1 || getLevel(subtreeRoot) < navigationRoot.structureDepth) {
            Iterator<Page> it = subtreeRoot.listChildren(new PageFilter());
            while (it.hasNext()) {
                Page page = it.next();
                int pageLevel = getLevel(page);
                int level = pageLevel - navigationRoot.startLevel - 1;
                List<NavigationItem> children = getItems(navigationRoot, page);
                boolean isSelected = checkSelected(page);
                if (structureStart == 0) {
                    level = level + 1;
                }
                pages.add(new NavigationItemImpl(page, isSelected, request, level, children));
            }
        }
        return pages;
    }

    private List<NavigationItem> getNavigationTree(NavigationRoot navigationRoot) {
        List<NavigationItem> itemTree = new ArrayList<>();
        Iterator<NavigationRoot> it = getRootItems(navigationRoot, structureStart).iterator();
        while (it.hasNext()) {
            NavigationRoot item = it.next();
            itemTree.addAll(getItems(item, item.page));
        }
        if (structureStart == 0) {
            boolean isSelected = checkSelected(navigationRoot.page);
            NavigationItemImpl root = new NavigationItemImpl(navigationRoot.page, isSelected, request, 0, itemTree);
            itemTree = new ArrayList<>();
            itemTree.add(root);
        }
        return  itemTree;
    }

    private List<NavigationRoot> getRootItems(NavigationRoot navigationRoot, int structureStart) {
        LinkedList<NavigationRoot> pages = new LinkedList<>();
        pages.addLast(navigationRoot);
        if (structureStart != 0) {
            int level = 1;
            while (level != structureStart && !pages.isEmpty()) {
                int size = pages.size();
                while (size > 0) {
                    NavigationRoot item = pages.removeFirst();
                    Iterator<Page> it = item.page.listChildren(new PageFilter());
                    while (it.hasNext()) {
                        pages.addLast(new NavigationRoot(it.next(), structureDepth));
                    }
                    size = size - 1;
                }
                level = level + 1;
            }
        }
        return pages;
    }

    private boolean checkSelected(Page page) {
        return this.currentPage.equals(page) ||
                this.currentPage.getPath().startsWith(page.getPath() + "/") ||
                currentPageIsRedirectTarget(page);
    }

    private boolean currentPageIsRedirectTarget(Page page) {
        boolean currentPageIsRedirectTarget = false;
        Resource contentResource = page.getContentResource();
        if (contentResource != null) {
            ValueMap valueMap = contentResource.getValueMap();
            String redirectTarget = valueMap.get(PageImpl.PN_REDIRECT_TARGET, String.class);
            if(StringUtils.isNotBlank(redirectTarget)) {
                PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
                if (pageManager != null) {
                    Page redirectPage = pageManager.getPage(redirectTarget);
                    if (currentPage.equals(redirectPage)) {
                        currentPageIsRedirectTarget = true;
                    }
                }
            }
        }
        return currentPageIsRedirectTarget;
    }

    private int getLevel(Page page) {
        return StringUtils.countMatches(page.getPath(), "/") - 1;
    }

    @Nullable
    private String getRelativePath(@NotNull Page root, @NotNull Page child) {
        if (child.equals(root)) {
            return ".";
        } else if ((child.getPath() + "/").startsWith(root.getPath())) {
            return child.getPath().substring(root.getPath().length() + 1);
        }
        return null;
    }

    private class NavigationRoot {
        final Page page;
        int startLevel;
        int structureDepth = -1;

        private NavigationRoot(@NotNull Page navigationRoot, int configuredStructureDepth) {
            page = navigationRoot;
            this.startLevel = getLevel(navigationRoot);
            if (configuredStructureDepth > -1) {
                structureDepth = configuredStructureDepth + startLevel;
            }
        }

        /**
         * Gets the resource representation of the navigation root page.
         *
         * @return the resource for the navigation root, empty if the resource could not be resolved
         */
        @NotNull
        final Optional<Resource> getPageResource() {
            return Optional.ofNullable(
                Optional.of(this.page)
                    // get the parent of the content resource
                    .map(Page::getContentResource)
                    .map(Resource::getParent)
                    // if content resource is missing, resolve resource at page path
                    .orElseGet(() -> resourceResolver.getResource(this.page.getPath())));
        }
    }

}
