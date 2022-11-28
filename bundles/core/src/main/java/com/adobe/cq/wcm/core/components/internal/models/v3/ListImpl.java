/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
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

import javax.annotation.PostConstruct;

import com.adobe.cq.wcm.core.components.internal.models.v1.AbstractListItemImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.internal.models.v2.PageListItemImpl;
import com.adobe.cq.wcm.core.components.models.List;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.adobe.cq.wcm.core.components.commons.link.Link.PN_LINK_URL;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {List.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ListImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ListImpl implements List {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v3/list";

    /**
     * Default flag indicating if list items should be displayed as teasers.
     */
    private static final boolean DISPLAY_ITEM_AS_TEASER_DEFAULT = false;

    /**
     * Flag indicating if items should be displayed as teasers.
     */
    private boolean displayItemAsTeaser;

    /**
     * Flag indicating if the list has external links configured.
     */
    private Boolean hasExternalLink;

    protected ListItem newPageListItem(@NotNull LinkManager linkManager, @NotNull Page page, String parentId, Component component) {
        return new PageListItemImpl(linkManager, page, parentId, component, showDescription, linkItems || displayItemAsTeaser, resource);
    }

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        super.initModel();
        displayItemAsTeaser = properties.get(PN_DISPLAY_ITEM_AS_TEASER, currentStyle.get(PN_DISPLAY_ITEM_AS_TEASER, DISPLAY_ITEM_AS_TEASER_DEFAULT));
        if (Source.MIXED.equals(getListType())) {
            if (this.listItems == null) {
                if (hasExternalLink()) {
                    // When external links are configured, we display only the linked title for all list items,
                    // other display modes are ignored.
                    showDescription = false;
                    showModificationDate = false;
                    displayItemAsTeaser = false;
                    linkItems = true;
                }
                this.listItems = getMixedListItems();
            }
        }
    }

    @Override
    @JsonProperty("displayItemAsTeaser")
    public boolean displayItemAsTeaser() {
        return displayItemAsTeaser;
    }

    private Collection<ListItem> getMixedListItems() {
        Stream<AbstractListItemImpl> itemStream = getMixedLinkResourceStream().map(linkResource -> {
            String link = linkResource.getValueMap().get(PN_LINK_URL, "").trim();
            if (StringUtils.isNotBlank(link)) {
                if (isExternalLink(link)) {
                    return new MixedLinkListItemImpl(linkManager, linkResource, getId(), component);
                } else {
                    Page page = this.currentPage.getPageManager().getPage(link);
                    if (page != null) {
                        return new MixedPageListItemImpl(linkManager, page, linkResource, getId(), component, showDescription, linkItems || displayItemAsTeaser, resource);
                    }
                }
            }

            return null;
        }).filter(Objects::nonNull);

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
        } else if (!hasExternalLink() && OrderBy.MODIFIED.equals(orderBy)) {
            // getLastModified may return null, define null to be after nonnull values
            itemStream = itemStream.sorted((item1, item2) -> direction * ObjectUtils.compare(item1.getLastModified(), item2.getLastModified(), true));
        }

        return itemStream.collect(Collectors.toList());
    }

    private Stream<Resource> getMixedLinkResourceStream() {
        Resource mixed = this.resource.getChild(NN_MIXED);
        if (mixed == null) {
            return Stream.empty();
        } else {
            return StreamSupport.stream(mixed.getChildren().spliterator(), false);
        }
    }

    private boolean hasExternalLink() {
        if (hasExternalLink == null) {
            hasExternalLink = getMixedLinkResourceStream().map(resource ->
                resource.getValueMap().get(PN_LINK_URL, "").trim()).anyMatch(ListImpl::isExternalLink);
        }
        return hasExternalLink;
    }

    private static boolean isExternalLink(String s) {
        return !s.startsWith("/");
    }
}
