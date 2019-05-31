/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2017 Adobe Systems Incorporated
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

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.servlets.AdaptiveImageServlet;
import com.adobe.cq.wcm.core.components.models.Image;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.wcm.api.components.Component;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Image.class, ComponentExporter.class}, resourceType = ImageImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageImpl extends com.adobe.cq.wcm.core.components.internal.models.v2.ImageImpl implements Image {

    public static final String RESOURCE_TYPE = "core/wcm/components/image/v3/image";
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageImpl.class);
    public final static String SPACE = " ";
    public final static String OBJECT_ID = "s_objectID='";
        
    private String objectId = StringUtils.EMPTY;    
    private String id = StringUtils.EMPTY;
    private String analyticsObjectId = StringUtils.EMPTY;
    private boolean trackingEnabled = false;

    public ImageImpl() {
        selector = AdaptiveImageServlet.CORE_DEFAULT_SELECTOR;
    }

    protected String uuid;
    
    @ScriptVariable
    protected com.day.cq.wcm.api.Page currentPage;
    
    @ScriptVariable
    private Component component;
    
    @SlingObject
    private ResourceResolver resourceResolver;

    @PostConstruct
    protected void initModel() {
    	  objectId = properties.get(Teaser.PN_TRACKING_OBJECT_ID,objectId);
    	  trackingEnabled = properties.get(Teaser.PN_TRACKING_ENABLED, trackingEnabled);
    	  id = String.valueOf(Math.abs(resource.getPath().hashCode()-1));
          populateObjectId();
          super.initModel();
    }
    
    public void populateObjectId(){
    	ModifiableValueMap map = resource.adaptTo(ModifiableValueMap.class);
    	if (null != map && objectId.isEmpty() ) {
    		map.put(Teaser.PN_TRACKING_OBJECT_ID, currentPage.getName()+ SPACE + component.getCellName()+ SPACE + id);
    	}
    	try {
    		resourceResolver.commit();
    	} catch (PersistenceException e) {
    		LOGGER.error("Error occured while saving the modalId for {}", currentPage.getName(), e);
    	}    	
    }
    
    @Override
    public String getAnlalyticData() {
    	if(trackingEnabled && !objectId.isEmpty() && !linkURL.isEmpty()){
    		analyticsObjectId = OBJECT_ID + objectId + "';" ;
	        return analyticsObjectId;
    	}
    	return StringUtils.EMPTY;
    }


}
