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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ContainerExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.ContentFragmentUtils;
import com.adobe.cq.wcm.core.components.models.ExperienceFragment;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.TemplatedResource;
import com.day.cq.wcm.api.designer.ComponentStyle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.factory.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.adobe.cq.wcm.core.components.internal.models.v2.ExperienceFragmentImpl.RESOURCE_TYPE_V2;

@Model(adaptables = SlingHttpServletRequest.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
        adapters = {ExperienceFragment.class, ContainerExporter.class, ComponentExporter.class},
        resourceType = {RESOURCE_TYPE_V2})
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
        extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ExperienceFragmentImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.ExperienceFragmentImpl implements ExperienceFragment,ContainerExporter {
    
    public static final String RESOURCE_TYPE_V2 = "core/wcm/components/experiencefragment/v2/experiencefragment";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExperienceFragmentImpl.class);
    
    @Self
    private SlingHttpServletRequest slingRequest;
    
    @Inject
    private Resource resource;
    
    private ValueMap properties;
    
    @Inject
    private ModelFactory modelFactory;
    
    /**
     * Class names of the responsive grid
     */
    private String classNames;
    
    /**
     * Child columns of the responsive grid
     */
    private Map<String, ComponentExporter> children = new LinkedHashMap<>();
    
    /**
     * Effective resource
     */
    private @Nullable Resource efvResource;
    
    /**
     * Number of columns set on the design or the default number of columns
     */
    
    
    @PostConstruct
    protected void initModel() {
        
        super.initModel();
        
        properties = resource.getValueMap();
        
        // grid columns css
        classNames = "aem-xf";
        
        retrieveExperienceFragmentContentResource();
        
        boolean isEmpty = true;
        
        if (efvResource != null) {
            // Columns provided by the design
    
            children = ContentFragmentUtils.getComponentExporters(efvResource.listChildren(), modelFactory, slingRequest);
        }
        
        if(isEmpty){
            classNames += " empty";
        }
        
        
        // add custom styles of the resource
        classNames += " " + resource.getValueMap().get(ComponentStyle.PN_CSS_CLASS, "");
        
        classNames = classNames.trim();
    }
    
    
    /**
     * @return The CSS class names to be applied to the current grid.
     * @deprecated Use {@link #getCssClassNames()}
     */
    @Override
    @JsonProperty("classNames")
    public String getCssClassNames() {
        return classNames;
    }
    
    @Override
    @JsonInclude
    public boolean isPathConfigured() {
        return StringUtils.isNotBlank(properties.get(PN_FRAGMENT_VARIATION_PATH, StringUtils.EMPTY));
    }
    
    /**
     * @param <T> The type of the resource
     * @return Returns the resource optionally wrapped into a {@link TemplatedResource}
     * <p>
     * AdobePatentID="P6273-US"
     */
    @Nonnull
    @JsonIgnore
    public <T extends Resource> T getEffectiveResource() {
        if (resource instanceof TemplatedResource) {
            return (T) resource;
        }
        
        Resource templatedResource = slingRequest.adaptTo(TemplatedResource.class);
        
        if (templatedResource == null) {
            return (T) resource;
        } else {
            return (T) templatedResource;
        }
    }
    
    @Nonnull
    @Override
    public Map<String, ? extends ComponentExporter> getExportedItems() {
        return children;
    }
    
    @Nonnull
    @Override
    public String[] getExportedItemsOrder() {
        return children.isEmpty() ?
                new String[0] : children.keySet().toArray(new String[children.size()]);
    }
    
    private void retrieveExperienceFragmentContentResource() {
        Resource effectiveResource = getEffectiveResource();
        
        
        String pathToExperienceFragmentVariation = effectiveResource.getValueMap().get(PN_FRAGMENT_VARIATION_PATH, String.class);
        
        if (StringUtils.isNotBlank(pathToExperienceFragmentVariation)) {
            final ResourceResolver resourceResolver = effectiveResource.getResourceResolver();
            
            
            PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
            
            if (pageManager != null) {
                Page page = pageManager.getPage(pathToExperienceFragmentVariation);
                
                efvResource = page.getContentResource();
            } else {
                LOGGER.error("PageManager  is null! This should never happen");
            }
            
            
        } else {
            LOGGER.debug("Fragment path is null");
        }
        
        
    }
    
    
}
