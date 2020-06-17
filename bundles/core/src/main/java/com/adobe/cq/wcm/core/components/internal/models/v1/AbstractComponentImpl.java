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

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.internal.models.v1.datalayer.ComponentDataImpl;
import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
public abstract class AbstractComponentImpl implements Component {

    @SlingObject
    protected Resource resource;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected com.day.cq.wcm.api.components.Component component;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected ComponentContext componentContext;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Page currentPage;

    private String id;
    private Boolean dataLayerEnabled;
    private ComponentData componentData;

    @Nullable
    @Override
    public String getId() {
        if (id == null) {
            this.id = ComponentUtils.getPropertyOrGeneratedId(this.resource, this.currentPage, this.componentContext);
        }
        return id;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    /**
     * See {@link Component#getData()}
     *
     * @return The component data
     */
    @Override
    @Nullable
    public ComponentData getData() {
        if (componentData == null) {
            if (this.dataLayerEnabled == null) {
                this.dataLayerEnabled = ComponentUtils.isDataLayerEnabled(this.resource);
            }
            if (this.dataLayerEnabled) {
                componentData = getComponentData();
            }
        }
        return componentData;
    }

    /*
     * Data layer specific methods. Each component can choose to implement some of these, to override or feed the data model.
     */

    /**
     * Override this method to provide a different data model for your component. This will be called by
     * {@link AbstractComponentImpl#getData()} in case the datalayer is activated
     *
     * @return The component data
     */
    @NotNull
    protected ComponentData getComponentData() {
        return new ComponentDataImpl(this, resource);
    }

    @JsonIgnore
    public String getDataLayerTitle() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerDescription() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerText() {
        return null;
    }

    @JsonIgnore
    public String[] getDataLayerTags() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerUrl() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerLinkUrl() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerTemplatePath() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerLanguage() {
        return null;
    }

    @JsonIgnore
    public String[] getDataLayerShownItems() {
        return null;
    }

    @JsonIgnore
    public String getDataLayerType() {
        return resource.getResourceType();
    }
}
