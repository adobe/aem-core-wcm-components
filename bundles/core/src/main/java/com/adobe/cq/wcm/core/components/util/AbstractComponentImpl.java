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
package com.adobe.cq.wcm.core.components.util;

import java.util.Calendar;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.Component;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;
import com.adobe.cq.wcm.core.components.models.datalayer.builder.DataLayerBuilder;
import com.adobe.cq.wcm.style.ComponentStyleInfo;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.ComponentContext;
import org.osgi.annotation.versioning.ConsumerType;

/**
 * Abstract class that can be used as a base class for {@link Component} implementations.
 */
@ConsumerType
public abstract class AbstractComponentImpl implements Component {

    /**
     * The current request.
     */
    @Self
    protected SlingHttpServletRequest request;

    /**
     * The current resource.
     */
    @SlingObject
    protected Resource resource;

    /**
     * The component.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected com.day.cq.wcm.api.components.Component component;

    /**
     * The component context.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    protected ComponentContext componentContext;

    /**
     * The current page.
     */
    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @Nullable
    private Page currentPage;

    /**
     * The ID for this component.
     */
    private String id;

    /**
     * Flag indicating if the data layer is enabled.
     */
    private Boolean dataLayerEnabled;

    /**
     * The data layer component data.
     */
    private ComponentData componentData;

    /**
     * Getter for current page.
     *
     * @return The current {@link Page}
     */
    protected Page getCurrentPage() {
        return currentPage;
    }

    /**
     * Setter for current page.
     * @param currentPage The {@link Page} to set
     */
    protected void setCurrentPage(Page currentPage) {
        this.currentPage = currentPage;
    }

    @NotNull
    @Override
    public String getId() {
        if (id == null) {
            String resourceCallerPath = (String)request.getAttribute(ContentFragmentUtils.ATTR_RESOURCE_CALLER_PATH);
            this.id = ComponentUtils.getId(this.resource, this.currentPage, resourceCallerPath, this.componentContext);
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
                if (this.currentPage != null ) {
                    // Check at page level to allow components embedded via containers in editable templates to inherit the setting
                    this.dataLayerEnabled = ComponentUtils.isDataLayerEnabled(this.currentPage.getContentResource());
                } else {
                    this.dataLayerEnabled = ComponentUtils.isDataLayerEnabled(this.resource);
                }
            }
            if (this.dataLayerEnabled) {
                componentData = getComponentData();
            }
        }
        return componentData;
    }

    /**
     * See {@link Component#getAppliedCssClasses()}
     *
     * @return The component styles/css class names
     */
    @Override
    @Nullable
	public String getAppliedCssClasses() {

    	return Optional.ofNullable(this.resource.adaptTo(ComponentStyleInfo.class))
    			.map(ComponentStyleInfo::getAppliedCssClasses)
    			.filter(StringUtils::isNotBlank)
    			.orElse(null);		// Returning null so sling model exporters don't return anything for this property if not configured
	}

    /**
     * Override this method to provide a different data model for your component. This will be called by
     * {@link AbstractComponentImpl#getData()} in case the datalayer is activated.
     *
     * @return The component data.
     */
    @NotNull
    protected ComponentData getComponentData() {
        return DataLayerBuilder.forComponent()
            .withId(this::getId)
            .withLastModifiedDate(() ->
                // Note: this can be simplified in JDK 11
                Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_LASTMODIFIED, Calendar.class))
                    .map(Calendar::getTime)
                    .orElseGet(() ->
                        Optional.ofNullable(resource.getValueMap().get(JcrConstants.JCR_CREATED, Calendar.class))
                            .map(Calendar::getTime)
                            .orElse(null)))
            .withType(() -> this.resource.getResourceType())
            .build();
    }

}
