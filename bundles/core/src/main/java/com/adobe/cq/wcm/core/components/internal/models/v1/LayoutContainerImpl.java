/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.day.cq.wcm.api.WCMMode;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.wcm.core.components.models.ContainerItem;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;

/**
 * Layout container model implementation.
 */
@Model(adaptables = SlingHttpServletRequest.class, adapters = LayoutContainer.class, resourceType = LayoutContainerImpl.RESOURCE_TYPE_V1)
public class LayoutContainerImpl extends AbstractContainerImpl implements LayoutContainer {

    /**
     * The resource type.
     */
    protected static final String RESOURCE_TYPE_V1 = "core/wcm/components/container/v1/container";
    protected static final String GHOST_COMPONENT_RESOURCE_TYPE = "wcm/msm/components/ghost";

    /**
     * The layout type.
     */
    private LayoutType layout;

    /**
     * The accessibility label.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String accessibilityLabel;

    /**
     * The role attribute.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String roleAttribute;

    /**
     * Map of the child items to be exported wherein the key is the child name, and the value is the child model.
     */
    private LinkedHashMap<String, ComponentExporter> itemModels;

    /**
     * List of items in this container.
     */
    private List<ContainerItem> items;

    /**
     * Initialize the model.
     */
    @PostConstruct
    protected void initModel() {
        // Note: this can be simplified using Optional.or() in JDK 11
        this.layout = Optional.ofNullable(
            Optional.ofNullable(resource.getValueMap().get(LayoutContainer.PN_LAYOUT, String.class))
                .orElseGet(() -> Optional.ofNullable(currentStyle)
                    .map(style -> currentStyle.get(LayoutContainer.PN_LAYOUT, String.class))
                    .orElse(null)
                ))
            .map(LayoutType::getLayoutType)
            .orElse(LayoutType.SIMPLE);
    }

    @Override
    @NotNull
    @Deprecated
    protected final List<ResourceListItemImpl> readItems() {
        return getChildren().stream()
            .map(res -> new ResourceListItemImpl(linkManager, res.getResource(), getId(), component))
            .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public final List<ContainerItem> getChildren() {
        if (this.items == null) {
            WCMMode wcmMode = WCMMode.fromRequest(this.request);
            boolean showGhostComponent = wcmMode == WCMMode.EDIT;
            this.items = ComponentUtils.getChildComponents(this.resource, this.request).stream()
                .filter(item -> showGhostComponent || !GHOST_COMPONENT_RESOURCE_TYPE.equals(item.getResourceType()))
                .map(ContainerItemImpl::new)
                .collect(Collectors.toList());
        }
        return this.items;
    }

    @Override
    @NotNull
    public final LinkedHashMap<String, ? extends ComponentExporter> getExportedItems() {
        if (this.itemModels == null) {
            this.itemModels = ComponentUtils.getComponentModels(this.slingModelFilter,
                this.modelFactory,
                this.getChildren().stream().map(ContainerItem::getResource).collect(Collectors.toList()),
                this.request,
                ComponentExporter.class);
        }
        return this.itemModels;
    }

    @Override
    public String[] getDataLayerShownItems() {
        return null;
    }

    @Override
    public @NotNull LayoutType getLayout() {
        return layout;
    }

    @Override
    @Nullable
    public String getAccessibilityLabel() {
        return accessibilityLabel;
    }

    @Override
    @Nullable
    public String getRoleAttribute() {
        return roleAttribute;
    }
}
