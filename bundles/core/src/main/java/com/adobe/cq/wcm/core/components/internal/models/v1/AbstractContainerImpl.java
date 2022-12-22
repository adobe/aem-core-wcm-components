/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe
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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.util.AbstractComponentImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.SlingModelFilter;
import com.adobe.cq.wcm.core.components.commons.link.LinkManager;
import com.adobe.cq.wcm.core.components.models.Container;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ContainerData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class which can be used as base class for {@link Container} implementations.
 */
public abstract class AbstractContainerImpl extends AbstractComponentImpl implements Container {

    @Self
    protected LinkManager linkManager;

    /**
     * The current style for this component.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    @Nullable
    protected Style currentStyle;

    /**
     * The sling model factory service.
     */
    @OSGiService
    protected SlingModelFilter slingModelFilter;

    /**
     * The model factory.
     */
    @OSGiService
    protected ModelFactory modelFactory;

    /**
     * The list of child items.
     */
    private List<ListItem> items;

    /**
     * The name of the child resources in the order they are to be exported.
     */
    private String[] exportedItemsOrder;

    /**
     * The background style string for this container component.
     */
    private String backgroundStyle;

    /**
     * Get the list of items in the container.
     *
     * @return The list of items in the container.
     */
    @NotNull
    @Deprecated
    protected abstract List<? extends ListItem> readItems();

    /**
     * Get the background colour.
     *
     * @return The background colour if set, empty if background colour is not enabled or not defined.
     */
    private Optional<String> getBackgroundColor() {
        return Optional.ofNullable(this.currentStyle)
            .filter(style -> style.get(PN_BACKGROUND_COLOR_ENABLED, Boolean.FALSE))
            .flatMap(style -> Optional.ofNullable(this.resource.getValueMap().get(PN_BACKGROUND_COLOR, String.class)))
            .filter(StringUtils::isNotEmpty);
    }

    /**
     * Get the background image.
     *
     * @return The background image if set, empty if background image is not enabled or not defined.
     */
    private Optional<String> getBackgroundImage() {
        return Optional.ofNullable(this.currentStyle)
            .filter(style -> style.get(PN_BACKGROUND_IMAGE_ENABLED, Boolean.FALSE))
            .flatMap(style -> Utils.getOptionalLink(linkManager.get(resource).withLinkUrlPropertyName(PN_BACKGROUND_IMAGE_REFERENCE).build()).map(Link::getURL))
            .filter(StringUtils::isNotEmpty);
    }

    @Override
    @JsonIgnore
    @NotNull
    @Deprecated
    public List<ListItem> getItems() {
        if (items == null) {
            items = readItems().stream().map(i -> (ListItem) i).collect(Collectors.toList());
        }
        return items;
    }

    @Nullable
    @Override
    public final String getBackgroundStyle() {
        if (this.backgroundStyle == null) {
            StringBuilder styleBuilder = new StringBuilder();
            getBackgroundImage().ifPresent(image -> styleBuilder.append("background-image:url(").append(image).append(");background-size:cover;background-repeat:no-repeat;"));
            getBackgroundColor().ifPresent(color -> styleBuilder.append("background-color:").append(color).append(";"));
            this.backgroundStyle = styleBuilder.toString();
        }

        if (StringUtils.isEmpty(this.backgroundStyle)) {
            return null;
        }
        return this.backgroundStyle;
    }

    @NotNull
    @Override
    public final String getExportedType() {
        return resource.getResourceType();
    }

    @NotNull
    @Override
    public abstract LinkedHashMap<String, ? extends ComponentExporter> getExportedItems();

    @NotNull
    @Override
    public final String[] getExportedItemsOrder() {
        if (exportedItemsOrder == null) {
            this.exportedItemsOrder = this.getExportedItems().keySet().toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        }
        return Arrays.copyOf(exportedItemsOrder, exportedItemsOrder.length);
    }

    @Override
    @NotNull
    protected ContainerData getComponentData() {
        return DataLayerBuilder.extending(super.getComponentData()).asContainer()
            .withShownItems(this::getDataLayerShownItems)
            .build();
    }

    @JsonIgnore
    public abstract String[] getDataLayerShownItems();

}
