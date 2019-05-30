/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2019 Adobe Systems Incorporated
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.ImageList;
import com.adobe.cq.wcm.core.components.models.ImageListItem;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.designer.Style;

@Model(adaptables = SlingHttpServletRequest.class, adapters = {ImageList.class, ComponentExporter.class}, resourceType = ListImpl.RESOURCE_TYPE)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class ImageListImpl implements ImageList {

    protected static final String RESOURCE_TYPE = "core/wcm/components/list/v1/imagelist";

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageListImpl.class);
    
    private static final String NN_IMAGE_LIST = "imageList";
    private final List<String> hiddenImageResourceProperties = new ArrayList<String>() {{
        add(JcrConstants.JCR_DESCRIPTION);
    }};

    
    @ScriptVariable
    private ValueMap properties;
    
    @ScriptVariable
    private Component component;

    @SlingObject
    private ResourceResolver resourceResolver;

    @SlingObject
    private Resource resource;

    @Self
    private SlingHttpServletRequest request;
    
    protected java.util.List<ImageListItem> listItems;

    @Override
    public Collection<ImageListItem> getListItems() {
        if (listItems == null) {
        	populateStaticListItems();
        }
        return listItems;
    }

    @NotNull
    @Override
    public String getExportedType() {
        return resource.getResourceType();
    }

    private void populateStaticListItems() {
        listItems = new ArrayList<>();
        Resource imageList = resource.getChild(NN_IMAGE_LIST);
        if(imageList != null) {
            Iterator<Resource> imagesList = imageList.listChildren();
            while(imagesList.hasNext()) {
            	Resource imageItem = imagesList.next();
            	ImageDelegatingModelImpl imageDelegatingModel = new ImageDelegatingModelImpl();
            	imageDelegatingModel.setImageResource(component, imageItem, hiddenImageResourceProperties);
            	ImageListItem imageListItem = imageItem.adaptTo(ImageListItem.class);            	
                if(imageListItem != null) {
                	imageListItem.setImageResource(imageDelegatingModel.getImageResource());
                	listItems.add(imageListItem);
                }
            }
        }
    } 
}
