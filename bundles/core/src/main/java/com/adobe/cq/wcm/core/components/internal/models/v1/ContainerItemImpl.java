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


import org.apache.sling.api.resource.Resource;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.models.ContainerItem;

/**
 * An individual item found within a {@link com.adobe.cq.wcm.core.components.models.Container}.
 */
public final class ContainerItemImpl implements ContainerItem {

    /**
     * The resource for this container item.
     */
    @NotNull
    private final Resource resource;

    /**
     * Construct a container Item.
     *
     * @param resource The resource for this container item.
     */
    public ContainerItemImpl(@NotNull final Resource resource) {
        this.resource = resource;
    }

    @Override
    @NotNull
    public Resource getResource() {
        return this.resource;
    }

    @Override
    @NotNull
    public String getName() {
        return this.resource.getName();
    }

    @Override
    @NotNull
    public String getPath() {
        return this.resource.getPath();
    }



}
