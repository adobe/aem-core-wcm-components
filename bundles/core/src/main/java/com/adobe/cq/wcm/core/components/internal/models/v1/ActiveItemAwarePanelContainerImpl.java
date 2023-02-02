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
package com.adobe.cq.wcm.core.components.internal.models.v1;

import com.day.cq.wcm.api.components.ComponentManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Abstract base class for {@code PanelContainerImpl} based component model implementations which support the
 * configuration of the default active item with one of the child panels.
 */
public abstract class ActiveItemAwarePanelContainerImpl extends PanelContainerImpl {
    /**
     * The current request.
     */
    @Self
    SlingHttpServletRequest request;
    /**
     * The active item property.
     */
    @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private String activeItem;
    /**
     * The current resource.
     */
    @SlingObject
    private Resource resource;
    /**
     * The name of the default active item.
     */
    private String activeItemName;

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
}
