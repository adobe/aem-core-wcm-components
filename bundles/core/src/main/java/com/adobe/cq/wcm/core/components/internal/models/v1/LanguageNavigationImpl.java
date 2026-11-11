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
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.LanguageNavigationItem;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;

/**
 * V1 Language Navigation model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class,
    adapters = {LanguageNavigation.class, ComponentExporter.class},
    resourceType = {LanguageNavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class LanguageNavigationImpl extends AbstractComponentImpl implements LanguageNavigation {

    /**
     * V2 Language Navigation resource type.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/languagenavigation/v1/languagenavigation";

    /**
     * Property name for the accessibility lavel.
     */
    private static final String PN_ACCESSIBILITY_LABEL = "accessibilityLabel";

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    @Self
    private LinkManager linkManager;

    @Nullable
    private String accessibilityLabel;

    @Nullable
    private String navigationRoot;
    private int structureDepth;
    private List<NavigationItem> items;
    private int startLevel;

    @PostConstruct
    private void initModel() {
        navigationRoot = Optional.ofNullable(properties.get(PN_NAVIGATION_ROOT, String.class))
            .orElseGet(() -> currentStyle.get(PN_NAVIGATION_ROOT, String.class));
        structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, 1));
    }

    @Override
    public List<NavigationItem> getItems() {
        if (items == null) {
            Page rootPage = Optional.ofNullable(this.navigationRoot).map(currentPage.getPageManager()::getPage).orElse(null);
            if (rootPage != null) {
                int rootPageLevel = rootPage.getDepth();
                startLevel = rootPageLevel + 1;
                structureDepth += rootPageLevel;
                items = getItems(rootPage);
            } else {
                items = Collections.emptyList();
            }
        }
        return Collections.unmodifiableList(items);
    }

    @Override
    @Nullable
    public String getAccessibilityLabel() {
        if (this.accessibilityLabel == null) {
            this.accessibilityLabel = this.resource.getValueMap().get(PN_ACCESSIBILITY_LABEL, String.class);
        }
        return this.accessibilityLabel;
    }

    /**
     * Get localized navigation items under a given page.
     *
     * @param root The page.
     * @return Localized navigation items based on the children of `Page`.
     */
    private List<NavigationItem> getItems(@NotNull final Page root) {
        List<NavigationItem> pages = new ArrayList<>();
        if (root.getDepth() < structureDepth) {
            Iterator<Page> it = root.listChildren(new PageFilter());
            while (it.hasNext()) {
                final Page page = it.next();
                String title = PageListItemImpl.getTitle(page);
                int level = page.getDepth() - startLevel;
                Page targetPage = Optional.ofNullable(getLocalizedPage(currentPage, page)).orElse(page);
                boolean current = currentPage.getPath().equals(targetPage.getPath());
                boolean active = currentPage.getPath().equals(page.getPath()) || currentPage.getPath().startsWith(page.getPath() + "/");
                pages.add(newLanguageNavigationItem(targetPage, active, current, linkManager, level, () -> getItems(page), title, getId(), component));
            }
        }

        return pages;
    }

    /**
     * Construct a Navigation Item.
     *
     * @param page             The page for which to create a navigation item.
     * @param active           Flag indicating if the navigation item is active.
     * @param current          Flag indicating if the navigation item is current page.
     * @param linkManager      Link manager service.
     * @param level            Depth level of the navigation item.
     * @param childrenSupplier The child navigation items supplier.
     * @param title            The item title.
     * @param parentId         ID of the parent navigation component.
     * @param component        The parent navigation {@link Component}.
     */
    protected LanguageNavigationItem newLanguageNavigationItem(@NotNull final Page page,
                                                               final boolean active,
                                                               final boolean current,
                                                               @NotNull final LinkManager linkManager,
                                                               final int level,
                                                               @NotNull final Supplier<List<NavigationItem>> childrenSupplier,
                                                               final String title,
                                                               final String parentId,
                                                               final Component component) {
        return new LanguageNavigationItemImpl(page, active, current, linkManager, level, childrenSupplier, title, parentId, component);
    }

    /**
     * Get the localized version of page found under the `languageRoot`.
     *
     * @param page The page for which to get the localized page.
     * @param languageRoot The language root page for the locale under which to find the localized page.
     * @return The localized page under languageRoot, or null if no such page exists.
     */
    @Nullable
    private Page getLocalizedPage(@NotNull final Page page, @NotNull final Page languageRoot) {
        Page localizedPage;
        String path = languageRoot.getPath();
        String relativePath = page.getPath();
        int indexOfStart = 0;
        if (path.length() < relativePath.length()) {
            indexOfStart = path.length();
        }
        if (relativePath.startsWith(path) &&
            relativePath.charAt(indexOfStart) == '/') {
            localizedPage = page;
        } else {
            String separator = "/";
            int i = relativePath.indexOf(separator);
            int occurrence = StringUtils.countMatches(path, separator) + 1;
            while (--occurrence > 0 && i != -1) {
                i = relativePath.indexOf(separator, i + 1);
            }
            relativePath = (i > 0) ? relativePath.substring(i) : "";
            path = path.concat(relativePath);
            PageManager pageManager = page.getPageManager();
            localizedPage = pageManager.getPage(path);
        }
        return localizedPage;
    }
}
