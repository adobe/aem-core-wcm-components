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
import java.util.LinkedList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.models.v1.NavigationItemImpl;
import com.adobe.cq.wcm.core.components.models.Navigation;
import com.adobe.cq.wcm.core.components.models.NavigationItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Navigation.class, ComponentExporter.class}, resourceType = NavigationImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class NavigationImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.NavigationImpl implements Navigation {

    public static final String RESOURCE_TYPE = "core/wcm/components/navigation/v2/navigation";

    private int rootLevel;

    @PostConstruct
    protected void initModel() {
        super.initModel();
        rootLevel = properties.get(PN_ROOT_LEVEL, currentStyle.get(PN_ROOT_LEVEL, 1));
    }

    @Override
    protected List<NavigationItem> getNavigationTree(NavigationRoot navigationRoot) {
        List<NavigationItem> itemTree = new ArrayList<>();
        Iterator<NavigationRoot> it = getRootItems(navigationRoot, rootLevel).iterator();
        while (it.hasNext()) {
            NavigationRoot item = it.next();
            itemTree.addAll(getItems(item, item.page));
        }
        if (rootLevel == 0) {
            boolean isSelected = checkSelected(navigationRoot.page);
            NavigationItemImpl root = new NavigationItemImpl(navigationRoot.page, isSelected, request, 0, itemTree);
            itemTree = new ArrayList<>();
            itemTree.add(root);
        }
        return  itemTree;
    }

    private List<NavigationRoot> getRootItems(NavigationRoot navigationRoot, int rootLevel) {
        LinkedList<NavigationRoot> pages = new LinkedList<>();
        pages.addLast(navigationRoot);
        if (rootLevel != 0) {
            int level = 1;
            while (level != rootLevel && !pages.isEmpty()) {
                int size = pages.size();
                while (size > 0) {
                    NavigationRoot item = pages.removeFirst();
                    Iterator<Page> it = item.page.listChildren(new PageFilter());
                    while (it.hasNext()) {
                        pages.addLast(new NavigationRoot(it.next(), structureDepth));
                    }
                    size = size -1;
                }
                level = level + 1;
            }
        }
        return pages;
    }

    @Override
    protected int getTreeLevel(Page page, NavigationRoot navigationRoot) {
        int pageLevel = getLevel(page);
        int level = pageLevel - navigationRoot.startLevel - 1;
        if (rootLevel == 0) {
            level = level + 1;
        }
        return level;
    }
}
