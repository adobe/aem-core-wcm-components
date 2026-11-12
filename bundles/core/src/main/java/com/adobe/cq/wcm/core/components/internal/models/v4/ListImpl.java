/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2023 Adobe
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
package com.adobe.cq.wcm.core.components.internal.models.v4;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.link.LinkManagerImpl;
import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v3.ListImpl implements List {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v4/list";

    @Override
    protected ListItem newPageListItem(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component) {
        Resource listResource = getListResource();
        return new PageListItemImpl(linkManager.get(page).build(), page, parentId, component, showDescription, linkItems || displayItemAsTeaser, listResource);
    }

    @Override
    @NotNull
    @JsonProperty("items")
    public Collection<ListItem> getListItems() {
        if (this.listItems == null) {
            if (Source.STATIC.equals(getListType())) {
                Resource staticNode = this.resource.getChild(NN_STATIC);
                if (staticNode != null) {
                    this.listItems = getStaticListItems();
                }
            }
        }

        return super.getListItems();
    }

    private Collection<ListItem> getStaticListItems() {
        Stream<AbstractListItemImpl> itemStream = getStaticItemResourceStream().map(linkResource -> {
            Link link = linkManager.get(linkResource).build();
            if (LinkManagerImpl.isExternalLink(link.getURL()) && (link.getReference() == null)) {
                return new ExternalLinkListItemImpl(link, linkResource, getId(), component);
            } else {
                Object reference = link.getReference();
                if (reference instanceof Page) {
                    return new PageListItemImpl(link, (Page) reference, linkResource, getId(), component, showDescription, linkItems || displayItemAsTeaser, resource);
                } else {
                    return null;
                }
            }
        }).filter(Objects::nonNull).filter(item -> item.getLink() != null && item.getLink().isValid());

        // apply sorting
        OrderBy orderBy = OrderBy.fromString(properties.get(PN_ORDER_BY, StringUtils.EMPTY));
        SortOrder sortOrder = SortOrder.fromString(properties.get(PN_SORT_ORDER, SortOrder.ASC.value));
        int direction = sortOrder.equals(SortOrder.ASC) ? 1 : -1;
        if (OrderBy.TITLE.equals(orderBy)) {
            Collator collator = Collator.getInstance(currentPage.getLanguage());
            collator.setStrength(Collator.PRIMARY);
            // getTitle may return null, define null to be greater than nonnull values
            Comparator<String> titleComparator = Comparator.nullsLast(collator);
            itemStream = itemStream.sorted((item1, item2) -> direction * titleComparator.compare(item1.getTitle(), item2.getTitle()));
        } else if (OrderBy.MODIFIED.equals(orderBy)) {
            // getLastModified may return null, define null to be after nonnull values
            itemStream = itemStream.sorted((item1, item2) -> direction * ObjectUtils.compare(item1.getLastModified(), item2.getLastModified(), true));
        }

        return itemStream.collect(Collectors.toList());
    }

    private Stream<Resource> getStaticItemResourceStream() {
        Resource staticNode = this.resource.getChild(NN_STATIC);
        if (staticNode == null) {
            return Stream.empty();
        } else {
            return StreamSupport.stream(staticNode.getChildren().spliterator(), false);
        }
    }
}
