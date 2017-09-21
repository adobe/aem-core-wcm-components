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
package com.adobe.cq.wcm.core.components.sandbox.internal.models.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import com.adobe.cq.wcm.core.components.internal.Constants;
import com.adobe.cq.wcm.core.components.sandbox.models.Navigation;
import com.adobe.cq.wcm.core.components.sandbox.models.NavigationItem;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = Navigation.class,
       resourceType = {NavigationImpl.RESOURCE_TYPE})
@Exporter(name = Constants.EXPORTER_NAME,
          extensions = Constants.EXPORTER_EXTENSION)
public class NavigationImpl implements Navigation {

    public static final String RESOURCE_TYPE = "core/wcm/sandbox/components/navigation/v1/navigation";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    private int startLevel;
    private int maxDepth;
    private String siteRoot;
    private boolean currentPageTreeOnly;
    private List<NavigationItem> items;
    private boolean skipRoot;
    private boolean wouldIncludeNavRoot = false;

    @PostConstruct
    private void initModel() {
        startLevel = properties.get(PN_CONTENT_START_LEVEL, currentStyle.get(PN_CONTENT_START_LEVEL, 0));
        maxDepth = properties.get(PN_MAX_DEPTH, currentStyle.get(PN_MAX_DEPTH, -1));
        if (startLevel > 0 && maxDepth > -1 && maxDepth < startLevel) {
            throw new IllegalStateException(
                    "The value of the " + PN_MAX_DEPTH + " property is smaller than " + PN_CONTENT_START_LEVEL + ".");

        }
        siteRoot = properties.get(PN_SITE_ROOT, currentStyle.get(PN_SITE_ROOT, String.class));
        currentPageTreeOnly = properties.get(PN_CURRENT_PAGE_TREE_ONLY, currentStyle.get(PN_CURRENT_PAGE_TREE_ONLY, true));
        skipRoot = properties.get(PN_SKIP_ROOT, currentStyle.get(PN_SKIP_ROOT, true));
    }

    @Override
    public List<NavigationItem> getItems() {
        if (items == null) {
            PageManager pageManager = currentPage.getPageManager();
            Page rootPage = pageManager.getPage(siteRoot);
            if (rootPage != null) {
                int rootPageLevel = getLevel(rootPage);
                startLevel += rootPageLevel;
                if (maxDepth > -1) {
                    maxDepth += rootPageLevel;
                }
                Page navigationRoot = rootPage;
                if (currentPageTreeOnly && currentPage.getPath().startsWith(rootPage.getPath() + "/")) {
                    navigationRoot = currentPage.getAbsoluteParent(startLevel);
                    if (navigationRoot == null) {
                        navigationRoot = rootPage;
                    }
                }
                // check if we're on a template
                String template = currentPage.getProperties().get(NameConstants.PN_TEMPLATE, String.class);
                boolean inTemplate = StringUtils.isNotEmpty(template) && currentPage.getPath().startsWith(template);
                wouldIncludeNavRoot = startLevel == getLevel(navigationRoot);
                items = getItems(navigationRoot, navigationRoot, inTemplate);
                if (wouldIncludeNavRoot && !skipRoot) {
                    boolean isSelected =
                            currentPage.equals(navigationRoot) || currentPage.getPath().startsWith(navigationRoot.getPath() + "/");
                    NavigationItemImpl root = new NavigationItemImpl(navigationRoot, isSelected, request, 0, items);
                    items = new ArrayList<>();
                    items.add(root);
                }
            } else {
                items = Collections.emptyList();
            }
        }
        return items;
    }

    /**
     * Builds the navigation tree for a {@code navigationRoot} page. The {@code navigationRoot} will be included in the returned tree
     * only if {@link #skipRoot} is false and {@link #wouldIncludeNavRoot} is true (see {@link #getItems()}).
     *
     * @param navigationRoot the global navigation tree root (start page)
     * @param subtreeRoot the current sub-tree root (changes depending on the level of recursion)
     * @param inTemplate flag that indicates that rendering happens on a template page; when in a template, siblings of the {@code
     * navigationRoot} will not be collected
     * @return the list of collected navigation trees
     */
    private List<NavigationItem> getItems(Page navigationRoot, Page subtreeRoot, boolean inTemplate) {
        List<NavigationItem> pages = new ArrayList<>();
        if (maxDepth == -1 || getLevel(subtreeRoot) < maxDepth) {
            Iterator<Page> it = subtreeRoot.listChildren(new PageFilter());
            while (it.hasNext()) {
                Page page = it.next();
                int pageLevel = getLevel(page);
                int level = pageLevel - startLevel;
                if (level <= 0 && inTemplate && pages.size() > 0) {
                    break;
                }
                List<NavigationItem> children = getItems(navigationRoot, page, inTemplate);
                if (pageLevel >= startLevel && !page.equals(navigationRoot)) {
                    boolean isSelected = this.currentPage.equals(page) || this.currentPage.getPath().startsWith(page.getPath() + "/");
                    if (skipRoot && wouldIncludeNavRoot) {
                        level = level -1;
                    }
                    pages.add(new NavigationItemImpl(page, isSelected, request, level, children));
                } else {
                    // keep the children found below
                    if (children.size() > 0) {
                        pages.addAll(children);
                    }
                }
            }
        }
        return pages;
    }

    private int getLevel(Page page) {
        return StringUtils.countMatches(page.getPath(), "/") - 1;
    }
}
