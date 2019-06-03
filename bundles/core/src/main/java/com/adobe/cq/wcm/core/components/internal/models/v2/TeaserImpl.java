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
package com.adobe.cq.wcm.core.components.internal.models.v2;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.internal.Utils;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.components.Component;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {Teaser.class, ComponentExporter.class}, resourceType = TeaserImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME , extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class TeaserImpl extends com.adobe.cq.wcm.core.components.internal.models.v1.TeaserImpl implements Teaser {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeaserImpl.class);

    public final static String RESOURCE_TYPE = "core/wcm/components/teaser/v2/teaser";
    public final static String OBJECT_ID = "s_objectID='";
    public final static String SPACE = " ";
   
    private String objectId = StringUtils.EMPTY;
    private boolean trackingEnabled = false;
    private String analyticData;
    private int counter = 0;
    String id = StringUtils.EMPTY;
    
    private List<ListItem> actions = new ArrayList<>();

    @ScriptVariable
    private Component component;

    @ScriptVariable
    private ValueMap properties;

    @Inject
    private Resource resource;
    
    @ScriptVariable
    private PageManager pageManager;
   
    @Self
    private SlingHttpServletRequest request;
    
    @SlingObject
    private ResourceResolver resourceResolver;	
    
    @ScriptVariable
    protected com.day.cq.wcm.api.Page currentPage;    

    @PostConstruct
    protected void initModel() {    	
    	objectId = properties.get(Teaser.PN_TRACKING_OBJECT_ID,objectId);
    	trackingEnabled = properties.get(Teaser.PN_TRACKING_ENABLED, trackingEnabled);
    	super.initModel();
      	id = String.valueOf(Math.abs(resource.getPath().hashCode()-1));   
      	populateObjectId();       
    }
   
    @Override
	protected void populateActions() {
        Resource actionsNode = resource.getChild(Teaser.NN_ACTIONS);
        if (actionsNode != null) {
            for(Resource action : actionsNode.getChildren()) {
                actions.add(new ListItem() {

                    private ValueMap properties = action.getValueMap();
                    private String title = properties.get(PN_ACTION_TEXT, String.class);
                    private String url = properties.get(PN_ACTION_LINK, String.class);
                    private Page page = null;
                    {
                        if (url != null && url.startsWith("/")) {
                            page = pageManager.getPage(url);
                        }
                    }

                    @Nullable
                    @Override
                    public String getTitle() {
                        return title;
                    }

                    @Nullable
                    @Override
                    @JsonIgnore
                    public String getPath() {
                        return url;
                    }

                    @Nullable
                    @Override
                    public String getURL() {
                        if (page != null) {
                            return Utils.getURL(request, page);
                        } else {
                            return url;
                        }
                    }
                    @Nullable
                    @Override
                    public String getAnalyticsDataList() {
                    	if(trackingEnabled && !objectId.isEmpty()){
                    		analyticData = OBJECT_ID + objectId + SPACE + ++counter + "';" ;
                	        return analyticData;
                    	}
                    	return StringUtils.EMPTY;
                    }
                });
            }
        }
    }

    @Override
    public List<ListItem> getActions() {
        return actions;
    }
        
    @Override
    public String getAnalyticData() {
    	if(trackingEnabled && !objectId.isEmpty()){
    		analyticData = OBJECT_ID + objectId + "';" ;
	        return analyticData;
    	}
    	return StringUtils.EMPTY;
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
}