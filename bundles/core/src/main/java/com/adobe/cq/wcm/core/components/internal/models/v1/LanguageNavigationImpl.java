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

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LanguageNavigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class,
       adapters = {LanguageNavigation.class, ComponentExporter.class},
       resourceType = {LanguageNavigationImpl.RESOURCE_TYPE})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME ,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class LanguageNavigationImpl extends AbstractComponentImpl implements LanguageNavigation {

    public static final String RESOURCE_TYPE = "core/wcm/components/languagenavigation/v1/languagenavigation";

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    @ScriptVariable
    private ValueMap properties;

    @ScriptVariable
    private Style currentStyle;

    private String navigationRoot;
    private int structureDepth;
    private Page rootPage;
    private List<NavigationItem> items;
    private int startLevel;

    @PostConstruct
    private void initModel() {
        navigationRoot = properties.get(PN_NAVIGATION_ROOT, currentStyle.get(PN_NAVIGATION_ROOT, String.class));
        structureDepth = properties.get(PN_STRUCTURE_DEPTH, currentStyle.get(PN_STRUCTURE_DEPTH, 1));
    }

    @Override
    public List<NavigationItem> getItems() {
        if (items == null) {
            PageManager pageManager = currentPage.getPageManager();
            rootPage = pageManager.getPage(navigationRoot);
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

    @NotNull
    @Override
    public String getExportedType() {
        return request.getResource().getResourceType();
    }

    private List<NavigationItem> getItems(Page root) {
        List<NavigationItem> pages = new ArrayList<>();
        if (root.getDepth() < structureDepth) {
            Iterator<Page> it = root.listChildren(new PageFilter());
            while (it.hasNext()) {
                Page page = it.next();
                boolean active = currentPage.getPath().equals(page.getPath()) || currentPage.getPath().startsWith(page.getPath() + "/");
                String title = page.getNavigationTitle();
                if (title == null) {
                    title = page.getTitle();
                }
                List<NavigationItem> children = getItems(page);
                int level = page.getDepth() - startLevel;
                Page localizedPage = getLocalizedPage(currentPage, page);
                if (localizedPage != null) {
                    page = localizedPage;
                }
                pages.add(new LanguageNavigationItemImpl(page, active, request, level, children, title, getId()));
            }
        }

        return pages;
    }

    private Page getLocalizedPage(Page page, Page languageRoot) {
        Page localizedPage;
        String path = languageRoot.getPath();
        String relativePath = page.getPath();
        if (relativePath.startsWith(path)) {
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
