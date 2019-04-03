/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2018 Adobe Systems Incorporated
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

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.wcm.core.components.models.GenericContainer;
import com.day.cq.wcm.api.designer.Style;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = GenericContainer.class, resourceType = GenericContainerImpl.RESOURCE_TYPE)
public class GenericContainerImpl extends AbstractContainerImpl implements GenericContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericContainerImpl.class);
    protected static final String RESOURCE_TYPE = "core/wcm/components/container/v1/container";
    
    private boolean propertyDisabled = false;
    private boolean colorsDisabled = false;
    private boolean imageDisabled = false;
    private String backgroundImageSrc;
    private String backgroundColor;  
    private StringBuilder backgroundStyle;
    
    List<Resource> options;
    
    @ScriptVariable
    private ValueMap properties;
    
    @SlingObject
    private ResourceResolver resourceResolver;

    @ScriptVariable(injectionStrategy = InjectionStrategy.OPTIONAL)
    @JsonIgnore
    protected Style currentStyle;

    @PostConstruct
    private void initModel() {    	
    	populateStyleProperties();    	
    	backgroundColor = properties.get(GenericContainer.PN_BACKGROUND_COLOR, String.class);
    	backgroundImageSrc = properties.get(GenericContainer.PN_BACKGROUND_IMAGE, String.class);
    	setBackgroundStyleString();
    }
    
    private void populateStyleProperties() {
        if (currentStyle != null) {
        	propertyDisabled = currentStyle.get(GenericContainer.PN_PROPERTIES_DISABLED, propertyDisabled);
        	colorsDisabled = currentStyle.get(GenericContainer.PN_COLORS_DISABLED, colorsDisabled);
        	imageDisabled = currentStyle.get(GenericContainer.PN_IMAGE_DISABLED, imageDisabled);
        }
    }
    
    public void setBackgroundStyleString()
    {
    	backgroundStyle = new StringBuilder();        
        if(!imageDisabled){
        	backgroundStyle.append("background-image:url(" + backgroundImageSrc + ");background-size:cover;background-repeat:no-repeat;");
        }
        if(!colorsDisabled){
        	backgroundStyle.append("background-color:" + backgroundColor);
        }
    }
    
    @Override
    public String getBackgroundStyleString()
    {
        return backgroundStyle.toString();
    }

    @Override
    public String getBackgroundImageSrc() {
        return backgroundImageSrc;
    }
    
    @Override
    public String getBackgroundColor() {
        return backgroundColor;
    }
    
    @Override
    public boolean isPropertyDisabled() {
        return propertyDisabled;
    }
    
    @Override
    public boolean isColorsDisabled() {
        return colorsDisabled;
    }
    
    @Override
    public boolean isImageDisabled() {
        return imageDisabled;
    }
}
