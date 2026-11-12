/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2020 Adobe
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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.adobe.cq.wcm.core.components.models.PanelContainer;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;

import com.day.cq.wcm.api.WCMMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.day.cq.wcm.api.components.ComponentManager;


/**
 * Abstract panel container model.
 */
public abstract class AbstractPanelContainerImpl extends AbstractContainerImpl implements PanelContainer {

    /**
     * The resource type.
     */
    public static final String RESOURCE_TYPE = "core/wcm/components/panelcontainer/v1/panelcontainer";
    public static final String GHOST_COMPONENT_RESOURCE_TYPE = "wcm/msm/components/ghost";

    /**
     * The active item property.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String activeItem;

    /**
     * The name of the default active item.
     */
    private String activeItemName;

    /**
     * Map of the child items to be exported wherein the key is the child name, and the value is the child model.
     */
    private LinkedHashMap<String, ? extends ComponentExporter> itemModels;

    /**
     * List of child panels in this panel container.
     */
    private List<PanelContainerItemImpl> panelItems;

    @Override
    @NotNull
    @Deprecated
    protected final List<ListItem> readItems() {
        return getChildren().stream()
            .map(res -> new PanelContainerListItemImpl(this.linkManager, res.getResource(), getId(), this.component, this.getCurrentPage()))
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public final List<PanelContainerItemImpl> getChildren() {
        if (this.panelItems == null) {
            WCMMode wcmMode = WCMMode.fromRequest(this.request);
            boolean showGhostComponent = wcmMode == WCMMode.EDIT;
            this.panelItems = ComponentUtils.getChildComponents(this.resource, this.request).stream()
                .filter(item -> showGhostComponent || !GHOST_COMPONENT_RESOURCE_TYPE.equals(item.getResourceType()))
                .map(item -> new PanelContainerItemImpl(item, this))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        }
        return this.panelItems;
    }



    public String getActiveItem() {
        if (activeItemName == null) {
            this.activeItemName = Optional.ofNullable(this.activeItem)
                    .map(resource::getChild)
                    .map(Resource::getName)
                    .orElseGet(() ->
                            Optional.ofNullable(request.getResourceResolver().adaptTo(ComponentManager.class))
                                    .flatMap(componentManager -> StreamSupport.stream(resource.getChildren().spliterator(), false)
                                            .filter(Objects::nonNull)
                                            .filter(res -> Objects.nonNull(componentManager.getComponentOfResource(res)))
                                            .findFirst()
                                            .map(Resource::getName))
                                    .orElse(null));
        }
        return activeItemName;
    }

    @NotNull
    @Override
    public final LinkedHashMap<String, ? extends ComponentExporter> getExportedItems() {
        if (this.itemModels == null) {
            this.itemModels = this.getChildren().stream()
                .map(i -> i.getComponentExporter(this.slingModelFilter, this.modelFactory, this.request))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
        }
        return this.itemModels;
    }

    @Override
    public String[] getDataLayerShownItems() {
        String activeItemName = getActiveItem();
        return this.getChildren().stream()
                .filter(e -> StringUtils.equals(e.getName(), activeItemName))
                .findFirst()
                .map(PanelContainerItemImpl::getData)
                .map(ComponentData::getId)
                .map(id -> new String[]{id})
                .orElseGet(() -> new String[0]);
    }
}
